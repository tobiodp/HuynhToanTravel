package vn.huynhtoantravel.domain;
import jakarta.persistence.*;
import vn.huynhtoantravel.domain.enums.*;
import java.time.LocalDateTime;
@Entity @Table(name="vehicle_bookings")
public class VehicleBooking {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="booking_master_id") private BookingMaster booking;
 @Column(name="route_code", nullable=false) private String routeCode;
 @Column(name="route_name", nullable=false) private String routeName;
 @Column(name="pickup_address", nullable=false) private String pickupAddress;
 @Column(name="dropoff_address", nullable=false) private String dropoffAddress;
 @Column(name="pickup_at", nullable=false) private LocalDateTime pickupAt;
 @Enumerated(EnumType.STRING) @Column(name="vehicle_type", nullable=false) private VehicleType vehicleType;
 @Enumerated(EnumType.STRING) @Column(name="trip_type", nullable=false) private TripType tripType;
 @Column(nullable=false) private int quantity=1;
 @Column(name="unit_price", nullable=false) private long unitPrice;
 @Column(name="line_total", nullable=false) private long lineTotal;
 @Column(name="driver_name") private String driverName; @Column(name="driver_phone") private String driverPhone;
 @Column(name="vehicle_plate") private String vehiclePlate; @Column(name="partner_name") private String partnerName;
 @Column(name="dispatch_note", length=1000) private String dispatchNote;
 public Long getId(){return id;} public BookingMaster getBooking(){return booking;} public void setBooking(BookingMaster v){booking=v;}
 public String getRouteCode(){return routeCode;} public void setRouteCode(String v){routeCode=v;} public String getRouteName(){return routeName;} public void setRouteName(String v){routeName=v;}
 public String getPickupAddress(){return pickupAddress;} public void setPickupAddress(String v){pickupAddress=v;} public String getDropoffAddress(){return dropoffAddress;} public void setDropoffAddress(String v){dropoffAddress=v;}
 public LocalDateTime getPickupAt(){return pickupAt;} public void setPickupAt(LocalDateTime v){pickupAt=v;} public VehicleType getVehicleType(){return vehicleType;} public void setVehicleType(VehicleType v){vehicleType=v;}
 public TripType getTripType(){return tripType;} public void setTripType(TripType v){tripType=v;} public int getQuantity(){return quantity;} public void setQuantity(int v){quantity=v;}
 public long getUnitPrice(){return unitPrice;} public void setUnitPrice(long v){unitPrice=v;} public long getLineTotal(){return lineTotal;} public void setLineTotal(long v){lineTotal=v;}
 public String getDriverName(){return driverName;} public void setDriverName(String v){driverName=v;} public String getDriverPhone(){return driverPhone;} public void setDriverPhone(String v){driverPhone=v;}
 public String getVehiclePlate(){return vehiclePlate;} public void setVehiclePlate(String v){vehiclePlate=v;} public String getPartnerName(){return partnerName;} public void setPartnerName(String v){partnerName=v;}
}
