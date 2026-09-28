CREATE DATABASE IF NOT EXISTS huynh_toan_travel
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE huynh_toan_travel;

CREATE TABLE roles (
  id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(50) NOT NULL UNIQUE,
  description VARCHAR(255),
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE users (
  id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  email VARCHAR(190) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  full_name VARCHAR(150) NOT NULL,
  phone VARCHAR(20),
  enabled BOOLEAN NOT NULL DEFAULT TRUE,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_users_phone (phone),
  INDEX idx_users_enabled (enabled)
) ENGINE=InnoDB;

CREATE TABLE user_roles (
  user_id BIGINT UNSIGNED NOT NULL,
  role_id BIGINT UNSIGNED NOT NULL,
  PRIMARY KEY (user_id, role_id),
  CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE hotels (
  id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(190) NOT NULL,
  slug VARCHAR(190) NOT NULL UNIQUE,
  area VARCHAR(100) NOT NULL,
  address VARCHAR(255) NOT NULL,
  latitude DECIMAL(10,7) NOT NULL,
  longitude DECIMAL(10,7) NOT NULL,
  star_rating TINYINT UNSIGNED NOT NULL DEFAULT 3,
  description TEXT,
  image_url VARCHAR(500),
  active BOOLEAN NOT NULL DEFAULT TRUE,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_hotels_area_star (area, star_rating),
  INDEX idx_hotels_geo (latitude, longitude),
  INDEX idx_hotels_active (active)
) ENGINE=InnoDB;

CREATE TABLE rooms (
  id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  hotel_id BIGINT UNSIGNED NOT NULL,
  code VARCHAR(80) NOT NULL,
  room_type VARCHAR(120) NOT NULL,
  max_guests SMALLINT UNSIGNED NOT NULL DEFAULT 2,
  price_per_night BIGINT UNSIGNED NOT NULL,
  inventory_count SMALLINT UNSIGNED NOT NULL DEFAULT 1,
  active BOOLEAN NOT NULL DEFAULT TRUE,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT uq_room_code UNIQUE (hotel_id, code),
  CONSTRAINT fk_rooms_hotel FOREIGN KEY (hotel_id) REFERENCES hotels(id),
  INDEX idx_rooms_hotel_active (hotel_id, active),
  INDEX idx_rooms_price (price_per_night)
) ENGINE=InnoDB;

CREATE TABLE bookings_master (
  id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  booking_code VARCHAR(20) NOT NULL UNIQUE,
  user_id BIGINT UNSIGNED NOT NULL,
  status ENUM('PENDING_PAYMENT','DEPOSITED','PAID_FULL','DISPATCHED','COMPLETED','CANCELLED') NOT NULL DEFAULT 'PENDING_PAYMENT',
  subtotal BIGINT UNSIGNED NOT NULL DEFAULT 0,
  discount_amount BIGINT UNSIGNED NOT NULL DEFAULT 0,
  grand_total BIGINT UNSIGNED NOT NULL DEFAULT 0,
  paid_amount BIGINT UNSIGNED NOT NULL DEFAULT 0,
  customer_note VARCHAR(1000),
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_bookings_user FOREIGN KEY (user_id) REFERENCES users(id),
  INDEX idx_booking_status_created (status, created_at),
  INDEX idx_booking_user_created (user_id, created_at)
) ENGINE=InnoDB;

CREATE TABLE vehicle_bookings (
  id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  booking_master_id BIGINT UNSIGNED NOT NULL,
  route_code VARCHAR(80) NOT NULL,
  route_name VARCHAR(190) NOT NULL,
  pickup_address VARCHAR(255) NOT NULL,
  dropoff_address VARCHAR(255) NOT NULL,
  pickup_at DATETIME NOT NULL,
  vehicle_type ENUM('SEDAN_4','SUV_7','VAN_16','LIMOUSINE') NOT NULL,
  trip_type ENUM('ONE_WAY','ROUND_TRIP','HOURLY','DAILY') NOT NULL DEFAULT 'ONE_WAY',
  quantity SMALLINT UNSIGNED NOT NULL DEFAULT 1,
  unit_price BIGINT UNSIGNED NOT NULL,
  line_total BIGINT UNSIGNED NOT NULL,
  driver_name VARCHAR(150),
  driver_phone VARCHAR(20),
  vehicle_plate VARCHAR(30),
  partner_name VARCHAR(190),
  dispatch_note VARCHAR(1000),
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_vehicle_booking_master FOREIGN KEY (booking_master_id) REFERENCES bookings_master(id) ON DELETE CASCADE,
  INDEX idx_vehicle_pickup (pickup_at),
  INDEX idx_vehicle_type (vehicle_type),
  INDEX idx_vehicle_master (booking_master_id)
) ENGINE=InnoDB;

CREATE TABLE ticket_bookings (
  id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  booking_master_id BIGINT UNSIGNED NOT NULL,
  ticket_type ENUM('BANA_ADULT','BANA_CHILD','BANA_BUFFET_ADULT','BANA_BUFFET_CHILD','HOIAN_ECO','HOIAN_UP','HOIAN_VIP','HOIAN_COMBO') NOT NULL,
  service_date DATE NOT NULL,
  local_resident BOOLEAN NOT NULL DEFAULT FALSE,
  quantity SMALLINT UNSIGNED NOT NULL,
  unit_price BIGINT UNSIGNED NOT NULL,
  line_total BIGINT UNSIGNED NOT NULL,
  qr_token VARCHAR(120),
  qr_issued_at DATETIME,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_ticket_booking_master FOREIGN KEY (booking_master_id) REFERENCES bookings_master(id) ON DELETE CASCADE,
  INDEX idx_ticket_master (booking_master_id),
  INDEX idx_ticket_service_date_type (service_date, ticket_type),
  INDEX idx_ticket_qr_token (qr_token)
) ENGINE=InnoDB;

CREATE TABLE room_bookings (
  id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  booking_master_id BIGINT UNSIGNED NOT NULL,
  room_id BIGINT UNSIGNED NOT NULL,
  check_in DATE NOT NULL,
  check_out DATE NOT NULL,
  rooms_count SMALLINT UNSIGNED NOT NULL DEFAULT 1,
  guests_count SMALLINT UNSIGNED NOT NULL DEFAULT 1,
  nights SMALLINT UNSIGNED NOT NULL,
  unit_price BIGINT UNSIGNED NOT NULL,
  line_total BIGINT UNSIGNED NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT chk_room_dates CHECK (check_out > check_in),
  CONSTRAINT fk_room_booking_master FOREIGN KEY (booking_master_id) REFERENCES bookings_master(id) ON DELETE CASCADE,
  CONSTRAINT fk_room_booking_room FOREIGN KEY (room_id) REFERENCES rooms(id),
  INDEX idx_room_booking_room_dates (room_id, check_in, check_out),
  INDEX idx_room_booking_master (booking_master_id)
) ENGINE=InnoDB;

CREATE TABLE payments (
  id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  booking_master_id BIGINT UNSIGNED NOT NULL,
  provider ENUM('VIETQR','CASSO','SEPAY','MANUAL') NOT NULL DEFAULT 'VIETQR',
  payment_purpose ENUM('DEPOSIT_30','DEPOSIT_50','FULL','BALANCE') NOT NULL,
  status ENUM('PENDING','CONFIRMED','REJECTED') NOT NULL DEFAULT 'PENDING',
  expected_amount BIGINT UNSIGNED NOT NULL,
  received_amount BIGINT UNSIGNED DEFAULT 0,
  bank_code VARCHAR(50),
  bank_account VARCHAR(50),
  transfer_content VARCHAR(255) NOT NULL,
  provider_transaction_id VARCHAR(190),
  provider_reference_code VARCHAR(190),
  raw_payload JSON,
  confirmed_at DATETIME,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_payments_booking FOREIGN KEY (booking_master_id) REFERENCES bookings_master(id) ON DELETE CASCADE,
  CONSTRAINT uq_payment_provider_tx UNIQUE (provider, provider_transaction_id),
  INDEX idx_payment_booking_status (booking_master_id, status),
  INDEX idx_payment_content (transfer_content),
  INDEX idx_payment_created (created_at)
) ENGINE=InnoDB;

-- Optional support table for dynamic route pricing.
CREATE TABLE vehicle_rates (
  id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  route_code VARCHAR(80) NOT NULL,
  route_name VARCHAR(190) NOT NULL,
  vehicle_type ENUM('SEDAN_4','SUV_7','VAN_16','LIMOUSINE') NOT NULL,
  trip_type ENUM('ONE_WAY','ROUND_TRIP','HOURLY','DAILY') NOT NULL DEFAULT 'ONE_WAY',
  price BIGINT UNSIGNED NOT NULL,
  active BOOLEAN NOT NULL DEFAULT TRUE,
  UNIQUE KEY uq_vehicle_rate (route_code, vehicle_type, trip_type),
  INDEX idx_vehicle_rate_active (active)
) ENGINE=InnoDB;

INSERT IGNORE INTO roles(name, description) VALUES
('ROLE_USER','Customer'),('ROLE_ADMIN','Administrator');
