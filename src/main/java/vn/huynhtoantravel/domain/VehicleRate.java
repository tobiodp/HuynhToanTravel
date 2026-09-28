package vn.huynhtoantravel.domain;

import jakarta.persistence.*;
import vn.huynhtoantravel.domain.enums.TripType;
import vn.huynhtoantravel.domain.enums.VehicleType;

@Entity
@Table(name = "vehicle_rates")
public class VehicleRate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "route_code", nullable = false)
    private String routeCode;

    @Column(name = "route_name", nullable = false)
    private String routeName;

    @Enumerated(EnumType.STRING)
    @Column(name = "vehicle_type", nullable = false)
    private VehicleType vehicleType;

    @Enumerated(EnumType.STRING)
    @Column(name = "trip_type", nullable = false)
    private TripType tripType = TripType.ONE_WAY;

    @Column(nullable = false)
    private long price;

    @Column(nullable = false)
    private boolean active = true;

    public VehicleRate() {}
    public VehicleRate(String routeCode, String routeName, VehicleType vehicleType, TripType tripType, long price) {
        this.routeCode = routeCode;
        this.routeName = routeName;
        this.vehicleType = vehicleType;
        this.tripType = tripType;
        this.price = price;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getRouteCode() { return routeCode; }
    public void setRouteCode(String routeCode) { this.routeCode = routeCode; }
    public String getRouteName() { return routeName; }
    public void setRouteName(String routeName) { this.routeName = routeName; }
    public VehicleType getVehicleType() { return vehicleType; }
    public void setVehicleType(VehicleType vehicleType) { this.vehicleType = vehicleType; }
    public TripType getTripType() { return tripType; }
    public void setTripType(TripType tripType) { this.tripType = tripType; }
    public long getPrice() { return price; }
    public void setPrice(long price) { this.price = price; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
