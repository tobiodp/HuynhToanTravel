package vn.huynhtoantravel.dto;

import vn.huynhtoantravel.domain.enums.TripType;
import vn.huynhtoantravel.domain.enums.VehicleType;

import java.time.LocalDateTime;

public class VehicleBookingRequest {
    private String routeCode;
    private VehicleType vehicleType;
    private TripType tripType;
    private String pickupAddress;
    private String dropoffAddress;
    private LocalDateTime pickupAt;
    private String customerNote;

    public String getRouteCode() { return routeCode; }
    public void setRouteCode(String routeCode) { this.routeCode = routeCode; }
    public VehicleType getVehicleType() { return vehicleType; }
    public void setVehicleType(VehicleType vehicleType) { this.vehicleType = vehicleType; }
    public TripType getTripType() { return tripType; }
    public void setTripType(TripType tripType) { this.tripType = tripType; }
    public String getPickupAddress() { return pickupAddress; }
    public void setPickupAddress(String pickupAddress) { this.pickupAddress = pickupAddress; }
    public String getDropoffAddress() { return dropoffAddress; }
    public void setDropoffAddress(String dropoffAddress) { this.dropoffAddress = dropoffAddress; }
    public LocalDateTime getPickupAt() { return pickupAt; }
    public void setPickupAt(LocalDateTime pickupAt) { this.pickupAt = pickupAt; }
    public String getCustomerNote() { return customerNote; }
    public void setCustomerNote(String customerNote) { this.customerNote = customerNote; }
}
