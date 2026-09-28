package vn.huynhtoantravel.config;

import jakarta.annotation.PostConstruct;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.jdbc.core.JdbcTemplate;
import vn.huynhtoantravel.domain.Role;
import vn.huynhtoantravel.domain.User;
import vn.huynhtoantravel.domain.VehicleRate;
import vn.huynhtoantravel.domain.enums.TripType;
import vn.huynhtoantravel.domain.enums.VehicleType;
import vn.huynhtoantravel.repository.RoleRepository;
import vn.huynhtoantravel.repository.TicketRateRepository;
import vn.huynhtoantravel.repository.UserRepository;
import vn.huynhtoantravel.repository.VehicleRateRepository;

import java.util.List;

@Component
public class DataSeeder {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final VehicleRateRepository vehicleRateRepository;
    private final TicketRateRepository ticketRateRepository;
    private final vn.huynhtoantravel.repository.HotelRepository hotelRepository;
    private final vn.huynhtoantravel.repository.RoomRepository roomRepository;
    private final PasswordEncoder passwordEncoder;
    private final JdbcTemplate jdbcTemplate;

    public DataSeeder(UserRepository userRepository, RoleRepository roleRepository,
                      VehicleRateRepository vehicleRateRepository, TicketRateRepository ticketRateRepository,
                      vn.huynhtoantravel.repository.HotelRepository hotelRepository, vn.huynhtoantravel.repository.RoomRepository roomRepository,
                      PasswordEncoder passwordEncoder, JdbcTemplate jdbcTemplate) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.vehicleRateRepository = vehicleRateRepository;
        this.ticketRateRepository = ticketRateRepository;
        this.hotelRepository = hotelRepository;
        this.roomRepository = roomRepository;
        this.passwordEncoder = passwordEncoder;
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct
    @Transactional
    public void seedData() {
        seedRolesAndUsers();
        seedVehicleRates();
        seedTicketRates();
        seedHotels();
    }

    private void seedRolesAndUsers() {
        Role userRole = roleRepository.findByName("ROLE_USER").orElseGet(() -> {
            Role r = new Role(); r.setName("ROLE_USER"); r.setDescription("Customer");
            return roleRepository.save(r);
        });
        
        Role adminRole = roleRepository.findByName("ROLE_ADMIN").orElseGet(() -> {
            Role r = new Role(); r.setName("ROLE_ADMIN"); r.setDescription("Administrator");
            return roleRepository.save(r);
        });

        if (userRepository.findByEmailIgnoreCase("test@gmail.com").isEmpty()) {
            User testUser = new User();
            testUser.setEmail("test@gmail.com");
            testUser.setFullName("Test Customer");
            testUser.setPhone("0901234567");
            testUser.setPasswordHash(passwordEncoder.encode("123456"));
            testUser.setEnabled(true);
            testUser.getRoles().add(userRole);
            userRepository.save(testUser);
        }
        
        if (userRepository.findByEmailIgnoreCase("admin@gmail.com").isEmpty()) {
            User adminUser = new User();
            adminUser.setEmail("admin@gmail.com");
            adminUser.setFullName("System Admin");
            adminUser.setPhone("0999999999");
            adminUser.setPasswordHash(passwordEncoder.encode("admin123"));
            adminUser.setEnabled(true);
            adminUser.getRoles().add(adminRole);
            userRepository.save(adminUser);
        }
    }

    private void seedVehicleRates() {
        List<VehicleRate> defaultRates = List.of(
            // 1. Sân bay Đà Nẵng ↔ Hội An
            new VehicleRate("DAD_HOIAN", "Sân bay Đà Nẵng ↔ Hội An", VehicleType.SEDAN_4, TripType.ONE_WAY, 250000),
            new VehicleRate("DAD_HOIAN", "Sân bay Đà Nẵng ↔ Hội An", VehicleType.SUV_7, TripType.ONE_WAY, 350000),
            new VehicleRate("DAD_HOIAN", "Sân bay Đà Nẵng ↔ Hội An", VehicleType.VAN_16, TripType.ONE_WAY, 500000),
            new VehicleRate("DAD_HOIAN", "Sân bay Đà Nẵng ↔ Hội An", VehicleType.SEDAN_4, TripType.ROUND_TRIP, 480000),
            new VehicleRate("DAD_HOIAN", "Sân bay Đà Nẵng ↔ Hội An", VehicleType.SUV_7, TripType.ROUND_TRIP, 650000),
            new VehicleRate("DAD_HOIAN", "Sân bay Đà Nẵng ↔ Hội An", VehicleType.VAN_16, TripType.ROUND_TRIP, 950000),

            // 2. Đà Nẵng ↔ Bà Nà Hills
            new VehicleRate("DAD_BANA", "Đà Nẵng ↔ Bà Nà Hills", VehicleType.SEDAN_4, TripType.ONE_WAY, 350000),
            new VehicleRate("DAD_BANA", "Đà Nẵng ↔ Bà Nà Hills", VehicleType.SUV_7, TripType.ONE_WAY, 450000),
            new VehicleRate("DAD_BANA", "Đà Nẵng ↔ Bà Nà Hills", VehicleType.VAN_16, TripType.ONE_WAY, 600000),
            new VehicleRate("DAD_BANA", "Đà Nẵng ↔ Bà Nà Hills", VehicleType.SEDAN_4, TripType.ROUND_TRIP, 600000),
            new VehicleRate("DAD_BANA", "Đà Nẵng ↔ Bà Nà Hills", VehicleType.SUV_7, TripType.ROUND_TRIP, 750000),
            new VehicleRate("DAD_BANA", "Đà Nẵng ↔ Bà Nà Hills", VehicleType.VAN_16, TripType.ROUND_TRIP, 950000),

            // 3. Đà Nẵng ↔ VinWonders Nam Hội An
            new VehicleRate("DAD_VINWONDERS", "Đà Nẵng ↔ VinWonders Nam Hội An", VehicleType.SEDAN_4, TripType.ONE_WAY, 380000),
            new VehicleRate("DAD_VINWONDERS", "Đà Nẵng ↔ VinWonders Nam Hội An", VehicleType.SUV_7, TripType.ONE_WAY, 480000),
            new VehicleRate("DAD_VINWONDERS", "Đà Nẵng ↔ VinWonders Nam Hội An", VehicleType.VAN_16, TripType.ONE_WAY, 700000),
            new VehicleRate("DAD_VINWONDERS", "Đà Nẵng ↔ VinWonders Nam Hội An", VehicleType.SEDAN_4, TripType.ROUND_TRIP, 700000),
            new VehicleRate("DAD_VINWONDERS", "Đà Nẵng ↔ VinWonders Nam Hội An", VehicleType.SUV_7, TripType.ROUND_TRIP, 880000),
            new VehicleRate("DAD_VINWONDERS", "Đà Nẵng ↔ VinWonders Nam Hội An", VehicleType.VAN_16, TripType.ROUND_TRIP, 1300000),

            // 4. Đà Nẵng ↔ Cố Đô Huế (Qua Đèo Hải Vân / Lăng Cô)
            new VehicleRate("DAD_HUE", "Đà Nẵng ↔ Cố Đô Huế", VehicleType.SEDAN_4, TripType.ONE_WAY, 900000),
            new VehicleRate("DAD_HUE", "Đà Nẵng ↔ Cố Đô Huế", VehicleType.SUV_7, TripType.ONE_WAY, 1100000),
            new VehicleRate("DAD_HUE", "Đà Nẵng ↔ Cố Đô Huế", VehicleType.VAN_16, TripType.ONE_WAY, 1500000),
            new VehicleRate("DAD_HUE", "Đà Nẵng ↔ Cố Đô Huế", VehicleType.SEDAN_4, TripType.ROUND_TRIP, 1500000),
            new VehicleRate("DAD_HUE", "Đà Nẵng ↔ Cố Đô Huế", VehicleType.SUV_7, TripType.ROUND_TRIP, 1800000),
            new VehicleRate("DAD_HUE", "Đà Nẵng ↔ Cố Đô Huế", VehicleType.VAN_16, TripType.ROUND_TRIP, 2400000),

            // 5. Đà Nẵng / Hội An ↔ Thánh Địa Mỹ Sơn
            new VehicleRate("DAD_MYSON", "Đà Nẵng / Hội An ↔ Thánh Địa Mỹ Sơn", VehicleType.SEDAN_4, TripType.ONE_WAY, 450000),
            new VehicleRate("DAD_MYSON", "Đà Nẵng / Hội An ↔ Thánh Địa Mỹ Sơn", VehicleType.SUV_7, TripType.ONE_WAY, 550000),
            new VehicleRate("DAD_MYSON", "Đà Nẵng / Hội An ↔ Thánh Địa Mỹ Sơn", VehicleType.VAN_16, TripType.ONE_WAY, 800000),
            new VehicleRate("DAD_MYSON", "Đà Nẵng / Hội An ↔ Thánh Địa Mỹ Sơn", VehicleType.SEDAN_4, TripType.ROUND_TRIP, 750000),
            new VehicleRate("DAD_MYSON", "Đà Nẵng / Hội An ↔ Thánh Địa Mỹ Sơn", VehicleType.SUV_7, TripType.ROUND_TRIP, 950000),
            new VehicleRate("DAD_MYSON", "Đà Nẵng / Hội An ↔ Thánh Địa Mỹ Sơn", VehicleType.VAN_16, TripType.ROUND_TRIP, 1400000),

            // 6. Đà Nẵng ↔ Tour Bán Đảo Sơn Trà - Linh Ứng - Ngũ Hành Sơn
            new VehicleRate("DAD_SONTRA", "Đà Nẵng ↔ Sơn Trà - Linh Ứng - Ngũ Hành Sơn", VehicleType.SEDAN_4, TripType.ONE_WAY, 300000),
            new VehicleRate("DAD_SONTRA", "Đà Nẵng ↔ Sơn Trà - Linh Ứng - Ngũ Hành Sơn", VehicleType.SUV_7, TripType.ONE_WAY, 400000),
            new VehicleRate("DAD_SONTRA", "Đà Nẵng ↔ Sơn Trà - Linh Ứng - Ngũ Hành Sơn", VehicleType.VAN_16, TripType.ONE_WAY, 600000),
            new VehicleRate("DAD_SONTRA", "Đà Nẵng ↔ Sơn Trà - Linh Ứng - Ngũ Hành Sơn", VehicleType.SEDAN_4, TripType.ROUND_TRIP, 550000),
            new VehicleRate("DAD_SONTRA", "Đà Nẵng ↔ Sơn Trà - Linh Ứng - Ngũ Hành Sơn", VehicleType.SUV_7, TripType.ROUND_TRIP, 700000),
            new VehicleRate("DAD_SONTRA", "Đà Nẵng ↔ Sơn Trà - Linh Ứng - Ngũ Hành Sơn", VehicleType.VAN_16, TripType.ROUND_TRIP, 1000000),

            // 7. Sân bay Đà Nẵng ↔ Khách sạn Nội thành / Biển Mỹ Khê
            new VehicleRate("DAD_CITY", "Sân bay Đà Nẵng ↔ Nội thành / Biển Mỹ Khê", VehicleType.SEDAN_4, TripType.ONE_WAY, 150000),
            new VehicleRate("DAD_CITY", "Sân bay Đà Nẵng ↔ Nội thành / Biển Mỹ Khê", VehicleType.SUV_7, TripType.ONE_WAY, 200000),
            new VehicleRate("DAD_CITY", "Sân bay Đà Nẵng ↔ Nội thành / Biển Mỹ Khê", VehicleType.VAN_16, TripType.ONE_WAY, 350000),
            new VehicleRate("DAD_CITY", "Sân bay Đà Nẵng ↔ Nội thành / Biển Mỹ Khê", VehicleType.SEDAN_4, TripType.ROUND_TRIP, 280000),
            new VehicleRate("DAD_CITY", "Sân bay Đà Nẵng ↔ Nội thành / Biển Mỹ Khê", VehicleType.SUV_7, TripType.ROUND_TRIP, 380000),
            new VehicleRate("DAD_CITY", "Sân bay Đà Nẵng ↔ Nội thành / Biển Mỹ Khê", VehicleType.VAN_16, TripType.ROUND_TRIP, 650000)
        );

        for (VehicleRate rate : defaultRates) {
            if (vehicleRateRepository.findByRouteCodeAndVehicleTypeAndTripType(
                    rate.getRouteCode(), rate.getVehicleType(), rate.getTripType()).isEmpty()) {
                vehicleRateRepository.save(rate);
            }
        }
    }

    private void seedTicketRates() {
        if (ticketRateRepository.count() == 0) {
            ticketRateRepository.save(createTicketRate(vn.huynhtoantravel.domain.enums.TicketType.BANA_ADULT, "Cáp Treo Bà Nà - Người lớn", "Vé cáp treo khứ hồi cho người lớn (>1.4m)", 900000, 600000L));
            ticketRateRepository.save(createTicketRate(vn.huynhtoantravel.domain.enums.TicketType.BANA_CHILD, "Cáp Treo Bà Nà - Trẻ em", "Vé cáp treo khứ hồi cho trẻ em (1.0m - 1.4m)", 750000, 500000L));
            ticketRateRepository.save(createTicketRate(vn.huynhtoantravel.domain.enums.TicketType.BANA_BUFFET_ADULT, "Combo Cáp Treo + Buffet Bà Nà - Người lớn", "Vé khứ hồi + Buffet trưa", 1250000, 950000L));
            ticketRateRepository.save(createTicketRate(vn.huynhtoantravel.domain.enums.TicketType.HOIAN_ECO, "Ký Ức Hội An - Hạng Eco", "Vé xem show Ký Ức Hội An hạng Eco (hàng ghế thường)", 600000, null));
            ticketRateRepository.save(createTicketRate(vn.huynhtoantravel.domain.enums.TicketType.HOIAN_VIP, "Ký Ức Hội An - Hạng VIP", "Vé xem show Ký Ức Hội An hạng VIP (nước uống + chỗ ngồi tốt)", 1200000, null));
            System.out.println("Seeded ticket rates.");
        }
    }

    private vn.huynhtoantravel.domain.TicketRate createTicketRate(vn.huynhtoantravel.domain.enums.TicketType type, String name, String desc, long price, Long localPrice) {
        vn.huynhtoantravel.domain.TicketRate r = new vn.huynhtoantravel.domain.TicketRate();
        r.setTicketType(type);
        r.setName(name);
        r.setDescription(desc);
        r.setPrice(price);
        r.setLocalPrice(localPrice);
        return r;
    }

    private void seedHotels() {
        if (hotelRepository.count() < 20 || roomRepository.count() < 60) {
            try {
                jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0;");
                jdbcTemplate.execute("TRUNCATE TABLE room_bookings;");
                jdbcTemplate.execute("TRUNCATE TABLE rooms;");
                jdbcTemplate.execute("TRUNCATE TABLE hotels;");
                jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1;");
            } catch (Exception e) {
                System.out.println("Could not truncate hotel tables: " + e.getMessage());
            }

            // 1. Furama Resort Danang (5★ Biển Mỹ Khê)
            vn.huynhtoantravel.domain.Hotel h1 = new vn.huynhtoantravel.domain.Hotel();
            h1.setName("Furama Resort Danang"); h1.setSlug("furama-resort-danang");
            h1.setArea("Biển Mỹ Khê"); h1.setAddress("105 Võ Nguyên Giáp, Ngũ Hành Sơn, Đà Nẵng");
            h1.setLatitude(new java.math.BigDecimal("16.0353")); h1.setLongitude(new java.math.BigDecimal("108.2514"));
            h1.setStarRating(5); h1.setDescription("Khu nghỉ dưỡng 5 sao danh tiếng ven biển Mỹ Khê với ẩm thực đỉnh cao và bãi biển riêng tư tuyệt đẹp.");
            h1.setImageUrl("https://images.unsplash.com/photo-1582719508461-905c673771fd?auto=format&fit=crop&w=900&q=80");
            h1 = hotelRepository.save(h1);
            roomRepository.save(createRoom(h1, "SUP_GARDEN", "Phòng Superior Hướng Vườn (Superior Garden View)", 2, 2800000, 15));
            roomRepository.save(createRoom(h1, "DLX_OCEAN", "Phòng Deluxe Hướng Biển (Deluxe Ocean View)", 2, 4500000, 10));
            roomRepository.save(createRoom(h1, "STUDIO_SUI", "Ocean Studio Suite Ban Công Rộng", 2, 6800000, 6));
            roomRepository.save(createRoom(h1, "POOL_VILLA2", "Biệt Thự 2 Phòng Ngủ Hồ Bơi Riêng (2-BR Pool Villa)", 4, 12500000, 4));

            // 2. Mường Thanh Luxury Đà Nẵng (5★ Biển Mỹ Khê)
            vn.huynhtoantravel.domain.Hotel h2 = new vn.huynhtoantravel.domain.Hotel();
            h2.setName("Mường Thanh Luxury Đà Nẵng"); h2.setSlug("muong-thanh-luxury-danang");
            h2.setArea("Biển Mỹ Khê"); h2.setAddress("270 Võ Nguyên Giáp, Phường Mỹ An, Ngũ Hành Sơn, Đà Nẵng");
            h2.setLatitude(new java.math.BigDecimal("16.0520")); h2.setLongitude(new java.math.BigDecimal("108.2465"));
            h2.setStarRating(5); h2.setDescription("Khách sạn 5 sao cao cấp trực diện biển Mỹ Khê, dịch vụ hoàn hảo mang phong cách Á Đông hiện đại.");
            h2.setImageUrl("https://images.unsplash.com/photo-1564501049412-61c2a3083791?auto=format&fit=crop&w=900&q=80");
            h2 = hotelRepository.save(h2);
            roomRepository.save(createRoom(h2, "DLX_CITY", "Deluxe King City View Ngắm Thành Phố", 2, 1350000, 25));
            roomRepository.save(createRoom(h2, "DLX_OCEAN", "Deluxe Twin Ocean View Hướng Biển", 2, 1750000, 20));
            roomRepository.save(createRoom(h2, "EXEC_SUITE", "Executive Suite Hướng Biển Trực Diện", 2, 2900000, 8));
            roomRepository.save(createRoom(h2, "PRESIDENT", "Grand Presidential Suite Hoàng Gia", 4, 7500000, 2));

            // 3. Naman Retreat Danang (5★ Biển Non Nước)
            vn.huynhtoantravel.domain.Hotel h3 = new vn.huynhtoantravel.domain.Hotel();
            h3.setName("Naman Retreat Danang"); h3.setSlug("naman-retreat-danang");
            h3.setArea("Biển Non Nước"); h3.setAddress("Đường Trường Sa, Quận Ngũ Hành Sơn, Đà Nẵng");
            h3.setLatitude(new java.math.BigDecimal("15.9754")); h3.setLongitude(new java.math.BigDecimal("108.2868"));
            h3.setStarRating(5); h3.setDescription("Khu nghỉ dưỡng phong cách kiến trúc tre nứa đương đại đạt nhiều giải thưởng quốc tế, bao gồm dịch vụ Spa thư giãn trọn gói.");
            h3.setImageUrl("https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?auto=format&fit=crop&w=900&q=80");
            h3 = hotelRepository.save(h3);
            roomRepository.save(createRoom(h3, "BABYLON", "Babylon Room View Vườn Tre Xanh Mát", 2, 3800000, 12));
            roomRepository.save(createRoom(h3, "POOL_VIL1", "1-Bedroom Pool Villa Biệt Thự Hồ Bơi Riêng", 2, 6500000, 8));
            roomRepository.save(createRoom(h3, "GARDEN_VIL", "Garden Pool Villa Sân Vườn Biệt Lập", 2, 8200000, 5));
            roomRepository.save(createRoom(h3, "BEACH_VIL2", "2-Bedroom Beachfront Villa Trực Diện Biển", 4, 14500000, 3));

            // 4. InterContinental Danang Sun Peninsula Resort (5★ Bán đảo Sơn Trà)
            vn.huynhtoantravel.domain.Hotel h4 = new vn.huynhtoantravel.domain.Hotel();
            h4.setName("InterContinental Danang Sun Peninsula Resort"); h4.setSlug("intercontinental-danang");
            h4.setArea("Bán đảo Sơn Trà"); h4.setAddress("Bãi Bắc, Bán đảo Sơn Trà, Đà Nẵng");
            h4.setLatitude(new java.math.BigDecimal("16.1245")); h4.setLongitude(new java.math.BigDecimal("108.3075"));
            h4.setStarRating(5); h4.setDescription("Kiệt tác kiến trúc của Bill Bensley ẩn mình giữa thiên nhiên hoang sơ của Bán đảo Sơn Trà, đẳng cấp bậc nhất châu Á.");
            h4.setImageUrl("https://images.unsplash.com/photo-1571896349842-33c89424de2d?auto=format&fit=crop&w=900&q=80");
            h4 = hotelRepository.save(h4);
            roomRepository.save(createRoom(h4, "RESORT_CLASSIC", "Classic Room Ocean View Ban Công Rộng", 2, 11500000, 10));
            roomRepository.save(createRoom(h4, "TERRACE_SUITE", "Terrace Suite Bán Đảo Sơn Trà", 2, 16800000, 6));
            roomRepository.save(createRoom(h4, "CLUB_LOUNGE", "Club InterContinental Lounge & Ocean View", 2, 22000000, 4));
            roomRepository.save(createRoom(h4, "SPA_LAGOON", "1-Bedroom Spa Lagoon Villa Siêu Sang", 2, 28000000, 2));

            // 5. Sala Danang Beach Hotel (4★ Biển Mỹ Khê)
            vn.huynhtoantravel.domain.Hotel h5 = new vn.huynhtoantravel.domain.Hotel();
            h5.setName("Sala Danang Beach Hotel"); h5.setSlug("sala-danang-beach");
            h5.setArea("Biển Mỹ Khê"); h5.setAddress("36 Lâm Hoành, Phước Mỹ, Sơn Trà, Đà Nẵng");
            h5.setLatitude(new java.math.BigDecimal("16.0642")); h5.setLongitude(new java.math.BigDecimal("108.2452"));
            h5.setStarRating(4); h5.setDescription("Khách sạn 4 sao thời thượng với hồ bơi vô cực trên tầng thượng view 360 độ toàn cảnh vịnh biển Đà Nẵng.");
            h5.setImageUrl("https://images.unsplash.com/photo-1566073771259-6a8506099945?auto=format&fit=crop&w=900&q=80");
            h5 = hotelRepository.save(h5);
            roomRepository.save(createRoom(h5, "SUP_DBL", "Superior Double City View", 2, 950000, 20));
            roomRepository.save(createRoom(h5, "DLX_TWIN", "Deluxe Twin Partial Ocean View", 2, 1350000, 15));
            roomRepository.save(createRoom(h5, "SALA_SUITE", "Sala Suite Ban Công Hướng Biển Mỹ Khê", 2, 2200000, 8));
            roomRepository.save(createRoom(h5, "FAM_SUITE", "Family 2-Bedroom Suite Cho Cả Gia Đình", 4, 3100000, 5));

            // 6. Silk Sense Hoi An River Resort (4★ Ven sông Thu Bồn)
            vn.huynhtoantravel.domain.Hotel h6 = new vn.huynhtoantravel.domain.Hotel();
            h6.setName("Silk Sense Hoi An River Resort"); h6.setSlug("silk-sense-hoi-an");
            h6.setArea("Ven sông Thu Bồn"); h6.setAddress("Tân Thịnh - Tân Mỹ, Cẩm An, Hội An");
            h6.setLatitude(new java.math.BigDecimal("15.8942")); h6.setLongitude(new java.math.BigDecimal("108.3491"));
            h6.setStarRating(4); h6.setDescription("Resort nghỉ dưỡng xanh bền vững bên dòng sông Cổ Cò / Thu Bồn thanh bình, không gian yên tĩnh chữa lành.");
            h6.setImageUrl("https://images.unsplash.com/photo-1540555700478-4be289fbecef?auto=format&fit=crop&w=900&q=80");
            h6 = hotelRepository.save(h6);
            roomRepository.save(createRoom(h6, "SUP_GARDEN", "Superior Garden View Vườn Nhiệt Đới", 2, 1250000, 15));
            roomRepository.save(createRoom(h6, "DLX_RIVER", "Deluxe River View Ngắm Hoàng Hôn Sông", 2, 1800000, 10));
            roomRepository.save(createRoom(h6, "FAM_SUITE", "Family Suite Rộng Rãi Cho 4 Khách", 4, 3200000, 5));
            roomRepository.save(createRoom(h6, "RIVER_VILLA", "Riverfront Pool Villa Biệt Thự Ven Sông", 2, 5500000, 3));

            // 7. Hoi An Memories Resort & Spa (4★ Phố Cổ Hội An)
            vn.huynhtoantravel.domain.Hotel h7 = new vn.huynhtoantravel.domain.Hotel();
            h7.setName("Hoi An Memories Resort & Spa"); h7.setSlug("hoian-memories-resort");
            h7.setArea("Phố Cổ Hội An"); h7.setAddress("Cồn Hến, 200 Nguyễn Tri Phương, Cẩm Nam, Hội An");
            h7.setLatitude(new java.math.BigDecimal("15.8732")); h7.setLongitude(new java.math.BigDecimal("108.3375"));
            h7.setStarRating(4); h7.setDescription("Ốc đảo nghỉ dưỡng độc đáo trên Đảo Ký Ức Hội An, kết nối trực tiếp với công viên văn hóa và show diễn thực cảnh trứ danh.");
            h7.setImageUrl("https://images.unsplash.com/photo-1596394516093-501ba68a0ba6?auto=format&fit=crop&w=900&q=80");
            h7 = hotelRepository.save(h7);
            roomRepository.save(createRoom(h7, "ECO_STD", "Eco Standard Room Phong Cách Cổ Điển", 2, 1400000, 18));
            roomRepository.save(createRoom(h7, "DLX_HOAI", "Deluxe King Hướng Sông Hoài Lãng Mạn", 2, 2100000, 12));
            roomRepository.save(createRoom(h7, "FAM_VILLA", "Family Villa Biệt Thự Sân Vườn Gia Đình", 4, 3500000, 6));
            roomRepository.save(createRoom(h7, "MOON_SUITE", "Moon River Suite Nghỉ Dưỡng Thượng Hạng", 2, 4800000, 4));

            // 8. Mercure Danang French Village Bana Hills (4★ Bà Nà Hills)
            vn.huynhtoantravel.domain.Hotel h8 = new vn.huynhtoantravel.domain.Hotel();
            h8.setName("Mercure Danang French Village Bana Hills"); h8.setSlug("mercure-bana-hills");
            h8.setArea("Bà Nà Hills"); h8.setAddress("Khu du lịch Sun World Bà Nà Hills, Hòa Vang, Đà Nẵng");
            h8.setLatitude(new java.math.BigDecimal("15.9988")); h8.setLongitude(new java.math.BigDecimal("107.9875"));
            h8.setStarRating(4); h8.setDescription("Tọa lạc trên đỉnh núi Bà Nà với kiến trúc lâu đài kiểu Pháp thế kỷ 19, trải nghiệm bốn mùa trong một ngày giữa mây ngàn.");
            h8.setImageUrl("https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?auto=format&fit=crop&w=900&q=80");
            h8 = hotelRepository.save(h8);
            roomRepository.save(createRoom(h8, "STD_FRENCH", "Standard King View Làng Pháp Cổ Kính", 2, 1950000, 20));
            roomRepository.save(createRoom(h8, "DLX_CLOUDS", "Deluxe Queen Ngắm Biển Mây Đỉnh Núi", 2, 2650000, 15));
            roomRepository.save(createRoom(h8, "EXEC_BANA", "Executive Suite Đỉnh Bà Nà Hills", 2, 3900000, 8));
            roomRepository.save(createRoom(h8, "FAM_BUNK", "Family Bunk 4 Giường Cho Gia Đình", 4, 3400000, 8));

            // 9. Four Seasons Resort The Nam Hai (5★ Biển An Bàng / Điện Bàn)
            vn.huynhtoantravel.domain.Hotel h9 = new vn.huynhtoantravel.domain.Hotel();
            h9.setName("Four Seasons Resort The Nam Hai"); h9.setSlug("four-seasons-the-nam-hai");
            h9.setArea("An Bàng"); h9.setAddress("Khối Hà My Đông B, Điện Bàn, Quảng Nam (Giáp Hội An)");
            h9.setLatitude(new java.math.BigDecimal("15.9285")); h9.setLongitude(new java.math.BigDecimal("108.3315"));
            h9.setStarRating(5); h9.setDescription("Khu nghỉ dưỡng biển siêu sang bậc nhất Việt Nam, sở hữu hệ thống hồ bơi vô cực ba tầng nối liền bãi cát trắng mịn.");
            h9.setImageUrl("https://images.unsplash.com/photo-1618773928121-c32242e63f39?auto=format&fit=crop&w=900&q=80");
            h9 = hotelRepository.save(h9);
            roomRepository.save(createRoom(h9, "VILLA_OCEAN", "1-Bedroom Villa Hướng Biển Tuyệt Mỹ", 2, 16500000, 8));
            roomRepository.save(createRoom(h9, "POOL_VIL1", "1-Bedroom Beachfront Pool Villa Hồ Bơi Riêng", 2, 24000000, 5));
            roomRepository.save(createRoom(h9, "HILLTOP_VIL3", "3-Bedroom Hilltop Pool Villa Đỉnh Đồi Biệt Lập", 6, 42000000, 2));

            // 10. Tuấn Phong Hotel (3★ Biển Mỹ Khê - Bình Dân)
            vn.huynhtoantravel.domain.Hotel h10 = new vn.huynhtoantravel.domain.Hotel();
            h10.setName("Tuấn Phong Hotel"); h10.setSlug("tuan-phong-hotel");
            h10.setArea("Biển Mỹ Khê"); h10.setAddress("190 Hồ Nghinh, Phước Mỹ, Sơn Trà, Đà Nẵng");
            h10.setLatitude(new java.math.BigDecimal("16.0655")); h10.setLongitude(new java.math.BigDecimal("108.2430"));
            h10.setStarRating(3); h10.setDescription("Khách sạn 3 sao giá rẻ tiện nghi, phòng sạch sẽ, đi bộ 3 phút ra bãi tắm Mỹ Khê, dịch vụ thân thiện.");
            h10.setImageUrl("https://images.unsplash.com/photo-1590490360182-c33d57733427?auto=format&fit=crop&w=900&q=80");
            h10 = hotelRepository.save(h10);
            roomRepository.save(createRoom(h10, "STD_NO_WIN", "Standard Không Cửa Sổ Tiết Kiệm", 2, 250000, 20));
            roomRepository.save(createRoom(h10, "SUP_WIN", "Superior Cửa Sổ Thoáng Mát", 2, 350000, 15));
            roomRepository.save(createRoom(h10, "DLX_BALCONY", "Deluxe Ban Công Ngắm Phố", 2, 450000, 10));
            roomRepository.save(createRoom(h10, "FAM_4P", "Family Room 2 Giường Đôi Cho 4 Khách", 4, 650000, 8));

            // 11. Seahorse Hostel & Bar by HAVI (2★ Trung tâm Đà Nẵng - Trẻ Trung)
            vn.huynhtoantravel.domain.Hotel h11 = new vn.huynhtoantravel.domain.Hotel();
            h11.setName("Seahorse Hostel & Bar by HAVI"); h11.setSlug("seahorse-hostel-danang");
            h11.setArea("Trung tâm Đà Nẵng"); h11.setAddress("7 Nguyễn Thái Học, Hải Châu, Đà Nẵng");
            h11.setLatitude(new java.math.BigDecimal("16.0680")); h11.setLongitude(new java.math.BigDecimal("108.2235"));
            h11.setStarRating(2); h11.setDescription("Hostel phong cách boutique ngay trung tâm sông Hàn & chợ Hàn, điểm dừng chân lý tưởng cho giới trẻ thích khám phá.");
            h11.setImageUrl("https://images.unsplash.com/photo-1555854877-bab0e564b8d5?auto=format&fit=crop&w=900&q=80");
            h11 = hotelRepository.save(h11);
            roomRepository.save(createRoom(h11, "DORM_BED", "Giường Tầng Ký Túc Xá (Dormitory)", 1, 130000, 30));
            roomRepository.save(createRoom(h11, "PVT_SINGLE", "Phòng Đơn Riêng Tư (Private Single)", 1, 280000, 10));
            roomRepository.save(createRoom(h11, "PVT_DBL", "Phòng Đôi Riêng Tư (Private Double)", 2, 380000, 12));
            roomRepository.save(createRoom(h11, "STUDIO_FAM", "Studio 4 Giường Nhóm Bạn Trẻ", 4, 600000, 6));

            // 12. Lò Gạch Cũ Homestay (2★ Ngoại ô Hội An - Sinh Thái)
            vn.huynhtoantravel.domain.Hotel h12 = new vn.huynhtoantravel.domain.Hotel();
            h12.setName("Lò Gạch Cũ Homestay"); h12.setSlug("lo-gach-cu-homestay");
            h12.setArea("Ngoại ô Hội An"); h12.setAddress("Thôn Vĩnh Nam, Duy Vinh, Duy Xuyên, Quảng Nam");
            h12.setLatitude(new java.math.BigDecimal("15.8421")); h12.setLongitude(new java.math.BigDecimal("108.3512"));
            h12.setStarRating(2); h12.setDescription("Trải nghiệm ngủ giữa cánh đồng lúa bát ngát tuyệt đẹp, đón bình minh và hoàng hôn đồng quê bình yên khó quên.");
            h12.setImageUrl("https://images.unsplash.com/photo-1587061949409-02df41d5e562?auto=format&fit=crop&w=900&q=80");
            h12 = hotelRepository.save(h12);
            roomRepository.save(createRoom(h12, "GLAMPING", "Lều Glamping Giữa Đồng Lúa", 2, 350000, 10));
            roomRepository.save(createRoom(h12, "BUNGALOW", "Bungalow Gỗ Mộc View Đồng Thơ Mộng", 2, 550000, 8));
            roomRepository.save(createRoom(h12, "COTTAGE_FAM", "Cottage Mái Lá Gia Đình 4 Khách", 4, 850000, 4));

            // 13. Vinpearl Resort & Spa Đà Nẵng (5★ Biển Non Nước)
            vn.huynhtoantravel.domain.Hotel h13 = new vn.huynhtoantravel.domain.Hotel();
            h13.setName("Vinpearl Resort & Spa Đà Nẵng"); h13.setSlug("vinpearl-resort-spa-danang");
            h13.setArea("Biển Non Nước"); h13.setAddress("23 Trường Sa, Hòa Hải, Ngũ Hành Sơn, Đà Nẵng");
            h13.setLatitude(new java.math.BigDecimal("16.0021")); h13.setLongitude(new java.math.BigDecimal("108.2721"));
            h13.setStarRating(5); h13.setDescription("Khu nghỉ dưỡng 5 sao sang trọng ôm trọn bãi biển Non Nước, hồ bơi vô cực 5.000m² và spa đẳng cấp quốc tế.");
            h13.setImageUrl("https://images.unsplash.com/photo-1540555700478-4be289fbecef?auto=format&fit=crop&w=900&q=80");
            h13 = hotelRepository.save(h13);
            roomRepository.save(createRoom(h13, "VIN_GARDEN", "Deluxe King Hướng Vườn Nhiệt Đới", 2, 3200000, 15));
            roomRepository.save(createRoom(h13, "VIN_OCEAN", "Deluxe Twin Hướng Biển Non Nước", 2, 3900000, 12));
            roomRepository.save(createRoom(h13, "VIN_VILLA3", "Biệt Thự 3 Phòng Ngủ Hồ Bơi Riêng", 6, 14000000, 4));

            // 14. Hyatt Regency Danang Resort and Spa (5★ Biển Non Nước)
            vn.huynhtoantravel.domain.Hotel h14 = new vn.huynhtoantravel.domain.Hotel();
            h14.setName("Hyatt Regency Danang Resort and Spa"); h14.setSlug("hyatt-regency-danang");
            h14.setArea("Biển Non Nước"); h14.setAddress("05 Trường Sa, Hòa Hải, Ngũ Hành Sơn, Đà Nẵng");
            h14.setLatitude(new java.math.BigDecimal("16.0125")); h14.setLongitude(new java.math.BigDecimal("108.2638"));
            h14.setStarRating(5); h14.setDescription("Khu nghỉ dưỡng biển biểu tượng dưới chân núi Ngũ Hành Sơn, sở hữu bãi cát trắng mịn riêng tư và 5 bể bơi tuyệt mỹ.");
            h14.setImageUrl("https://images.unsplash.com/photo-1571896349842-33c89424de2d?auto=format&fit=crop&w=900&q=80");
            h14 = hotelRepository.save(h14);
            roomRepository.save(createRoom(h14, "HYATT_STD", "Standard King Ocean View Ban Công", 2, 3500000, 15));
            roomRepository.save(createRoom(h14, "HYATT_CLUB", "1 King Bed with Club Lounge Access", 2, 5200000, 8));
            roomRepository.save(createRoom(h14, "HYATT_SUI", "Regency Suite Ocean Front Hướng Biển", 2, 8500000, 4));

            // 15. Novotel Danang Premier Han River (5★ Trung tâm Đà Nẵng)
            vn.huynhtoantravel.domain.Hotel h15 = new vn.huynhtoantravel.domain.Hotel();
            h15.setName("Novotel Danang Premier Han River"); h15.setSlug("novotel-danang-han-river");
            h15.setArea("Trung tâm Đà Nẵng"); h15.setAddress("36 Bạch Đằng, Hải Châu, Đà Nẵng");
            h15.setLatitude(new java.math.BigDecimal("16.0772")); h15.setLongitude(new java.math.BigDecimal("108.2248"));
            h15.setStarRating(5); h15.setDescription("Khách sạn 5 sao cao cấp bên bờ sông Hàn thơ mộng với sky bar tầng thượng ngắm toàn cảnh pháo hoa và cầu Rồng rực rỡ.");
            h15.setImageUrl("https://images.unsplash.com/photo-1566665797739-1674de7a421a?auto=format&fit=crop&w=900&q=80");
            h15 = hotelRepository.save(h15);
            roomRepository.save(createRoom(h15, "NOVO_RIVER", "Superior King River View Trực Diện Sông", 2, 2100000, 20));
            roomRepository.save(createRoom(h15, "NOVO_DLX", "Deluxe Twin Panorama Cầu Sông Hàn", 2, 2700000, 15));
            roomRepository.save(createRoom(h15, "NOVO_EXEC", "Executive Suite Sông Hàn Tầng Cao", 2, 4800000, 6));

            // 16. Sheraton Grand Danang Resort (5★ Biển Non Nước)
            vn.huynhtoantravel.domain.Hotel h16 = new vn.huynhtoantravel.domain.Hotel();
            h16.setName("Sheraton Grand Danang Resort"); h16.setSlug("sheraton-grand-danang");
            h16.setArea("Biển Non Nước"); h16.setAddress("35 Trường Sa, Hòa Hải, Ngũ Hành Sơn, Đà Nẵng");
            h16.setLatitude(new java.math.BigDecimal("15.9832")); h16.setLongitude(new java.math.BigDecimal("108.2831"));
            h16.setStarRating(5); h16.setDescription("Resort 5 sao đẳng cấp thế giới với bể bơi vô cực dài 250m vươn ra biển, dịch vụ chăm sóc chuẩn quốc tế.");
            h16.setImageUrl("https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?auto=format&fit=crop&w=900&q=80");
            h16 = hotelRepository.save(h16);
            roomRepository.save(createRoom(h16, "SHER_DLX", "Deluxe Guest Room Giường King", 2, 3600000, 15));
            roomRepository.save(createRoom(h16, "SHER_POOL", "Deluxe Plunge Pool Hồ Bơi Riêng", 2, 5800000, 8));
            roomRepository.save(createRoom(h16, "SHER_FAM", "Family Suite Ocean View Rộng Rãi", 4, 7500000, 5));

            // 17. La Siesta Hoi An Resort & Spa (4★ Phố Cổ Hội An)
            vn.huynhtoantravel.domain.Hotel h17 = new vn.huynhtoantravel.domain.Hotel();
            h17.setName("La Siesta Hoi An Resort & Spa"); h17.setSlug("la-siesta-hoi-an-resort");
            h17.setArea("Phố Cổ Hội An"); h17.setAddress("132 Hùng Vương, Thanh Hà, Hội An");
            h17.setLatitude(new java.math.BigDecimal("15.8795")); h17.setLongitude(new java.math.BigDecimal("108.3182"));
            h17.setStarRating(4); h17.setDescription("Khu nghỉ dưỡng phong cách boutique phố cổ thanh lịch, bao quanh bởi khu vườn nhiệt đới xanh mát và hồ bơi nước mặn.");
            h17.setImageUrl("https://images.unsplash.com/photo-1544984243-ec57ea16fe25?auto=format&fit=crop&w=900&q=80");
            h17 = hotelRepository.save(h17);
            roomRepository.save(createRoom(h17, "LASIESTA_CLS", "Classic Deluxe Cánh Đồng Thơ Mộng", 2, 1700000, 15));
            roomRepository.save(createRoom(h17, "LASIESTA_SUI", "Grand Suite Ban Công Hướng Hồ Bơi", 2, 2900000, 8));
            roomRepository.save(createRoom(h17, "LASIESTA_DUP", "Duplex Family Villa 4 Khách", 4, 4200000, 5));

            // 18. Anantara Hoi An Resort (5★ Ven sông Thu Bồn)
            vn.huynhtoantravel.domain.Hotel h18 = new vn.huynhtoantravel.domain.Hotel();
            h18.setName("Anantara Hoi An Resort"); h18.setSlug("anantara-hoi-an-resort");
            h18.setArea("Ven sông Thu Bồn"); h18.setAddress("01 Phạm Hồng Thái, Cẩm Châu, Hội An");
            h18.setLatitude(new java.math.BigDecimal("15.8778")); h18.setLongitude(new java.math.BigDecimal("108.3341"));
            h18.setStarRating(5); h18.setDescription("Khu nghỉ dưỡng 5 sao bờ sông Thu Bồn kết hợp phong cách Pháp cổ và nét duyên dáng Việt Nam, du thuyền hoàng hôn độc quyền.");
            h18.setImageUrl("https://images.unsplash.com/photo-1578683010236-d716f9a3f461?auto=format&fit=crop&w=900&q=80");
            h18 = hotelRepository.save(h18);
            roomRepository.save(createRoom(h18, "ANAN_GARDEN", "Deluxe Garden View Ban Công Vườn", 2, 4500000, 10));
            roomRepository.save(createRoom(h18, "ANAN_RIVER", "Deluxe River View Ngắm Sông Thu Bồn", 2, 5900000, 8));
            roomRepository.save(createRoom(h18, "ANAN_SUI", "Anantara Riverfront Suite Thượng Tuyệt", 2, 9800000, 4));

            // 19. Vanda Hotel Da Nang (4★ Trung tâm Đà Nẵng)
            vn.huynhtoantravel.domain.Hotel h19 = new vn.huynhtoantravel.domain.Hotel();
            h19.setName("Vanda Hotel Da Nang"); h19.setSlug("vanda-hotel-danang");
            h19.setArea("Trung tâm Đà Nẵng"); h19.setAddress("03 Nguyễn Văn Linh, Bình Hiên, Hải Châu, Đà Nẵng");
            h19.setLatitude(new java.math.BigDecimal("16.0607")); h19.setLongitude(new java.math.BigDecimal("108.2212"));
            h19.setStarRating(4); h19.setDescription("Khách sạn 4 sao nằm ngay đầu Cầu Rồng, vị trí đắc địa ngắm Rồng phun lửa nước cuối tuần và khám phá ẩm thực đêm.");
            h19.setImageUrl("https://images.unsplash.com/photo-1568495248636-6432b97bd949?auto=format&fit=crop&w=900&q=80");
            h19 = hotelRepository.save(h19);
            roomRepository.save(createRoom(h19, "VANDA_SUP", "Superior Double City View Phố Xá", 2, 950000, 20));
            roomRepository.save(createRoom(h19, "VANDA_DLX", "Deluxe King Cầu Rồng View Tuyệt Đẹp", 2, 1350000, 15));
            roomRepository.save(createRoom(h19, "VANDA_SUI", "Family Junior Suite 4 Khách", 4, 2100000, 6));

            // 20. Stella Maris Beach Danang (4★ Biển Mỹ Khê)
            vn.huynhtoantravel.domain.Hotel h20 = new vn.huynhtoantravel.domain.Hotel();
            h20.setName("Stella Maris Beach Danang"); h20.setSlug("stella-maris-beach-danang");
            h20.setArea("Biển Mỹ Khê"); h20.setAddress("03 Võ Văn Kiệt, Phước Mỹ, Sơn Trà, Đà Nẵng");
            h20.setLatitude(new java.math.BigDecimal("16.0628")); h20.setLongitude(new java.math.BigDecimal("108.2449"));
            h20.setStarRating(4); h20.setDescription("Khách sạn 4 sao hiện đại cách bãi tắm Mỹ Khê chỉ 50 bước chân, hồ bơi vô cực tầng 16 ngắm trọn bình minh biển Đông.");
            h20.setImageUrl("https://images.unsplash.com/photo-1551882547-ff40c63fe5fa?auto=format&fit=crop&w=900&q=80");
            h20 = hotelRepository.save(h20);
            roomRepository.save(createRoom(h20, "STELLA_SUP", "Superior King Cửa Sổ Phố Thoáng", 2, 1100000, 20));
            roomRepository.save(createRoom(h20, "STELLA_DLX", "Deluxe Twin Hướng Biển Mỹ Khê", 2, 1550000, 15));
            roomRepository.save(createRoom(h20, "STELLA_SUI", "Suite Stella Maris Ban Công Trực Diện Biển", 2, 2600000, 8));

            System.out.println("Seeded 20 hotels and 70+ luxury & boutique rooms.");
        }
    }

    private vn.huynhtoantravel.domain.Room createRoom(vn.huynhtoantravel.domain.Hotel hotel, String code, String type, int maxGuests, long price, int count) {
        vn.huynhtoantravel.domain.Room r = new vn.huynhtoantravel.domain.Room();
        r.setHotel(hotel); r.setCode(code); r.setRoomType(type);
        r.setMaxGuests(maxGuests); r.setPricePerNight(price); r.setInventoryCount(count);
        return r;
    }
}
