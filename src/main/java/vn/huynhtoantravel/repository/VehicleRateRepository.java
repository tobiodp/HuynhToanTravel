package vn.huynhtoantravel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.huynhtoantravel.domain.VehicleRate;
import vn.huynhtoantravel.domain.enums.TripType;
import vn.huynhtoantravel.domain.enums.VehicleType;

import java.util.List;
import java.util.Optional;

public interface VehicleRateRepository extends JpaRepository<VehicleRate, Long> {
    List<VehicleRate> findByActiveTrue();
    Optional<VehicleRate> findByRouteCodeAndVehicleTypeAndTripTypeAndActiveTrue(String routeCode, VehicleType vehicleType, TripType tripType);
    Optional<VehicleRate> findByRouteCodeAndVehicleTypeAndTripType(String routeCode, VehicleType vehicleType, TripType tripType);
    List<VehicleRate> findAllByOrderByRouteNameAsc();
}
