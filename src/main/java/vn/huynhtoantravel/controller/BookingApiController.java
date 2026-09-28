package vn.huynhtoantravel.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.huynhtoantravel.domain.BookingMaster;
import vn.huynhtoantravel.domain.VehicleRate;
import vn.huynhtoantravel.dto.VehicleBookingRequest;
import vn.huynhtoantravel.repository.VehicleRateRepository;
import vn.huynhtoantravel.service.BookingService;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class BookingApiController {

    private final BookingService bookingService;
    private final VehicleRateRepository vehicleRateRepository;

    public BookingApiController(BookingService bookingService, VehicleRateRepository vehicleRateRepository) {
        this.bookingService = bookingService;
        this.vehicleRateRepository = vehicleRateRepository;
    }

    @GetMapping("/vehicle-rates")
    public List<VehicleRate> getRates() {
        return vehicleRateRepository.findByActiveTrue();
    }

    @PostMapping("/bookings/vehicle")
    public ResponseEntity<?> createVehicleBooking(@RequestBody VehicleBookingRequest request, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Vui lòng đăng nhập để đặt xe"));
        }
        
        try {
            BookingMaster booking = bookingService.createVehicleBooking(principal.getName(), request);
            return ResponseEntity.ok(Map.of("bookingCode", booking.getBookingCode()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/ticket-rates")
    public List<vn.huynhtoantravel.domain.TicketRate> getTicketRates() {
        return bookingService.getTicketRates();
    }

    @PostMapping("/bookings/ticket")
    public ResponseEntity<?> createTicketBooking(@RequestBody vn.huynhtoantravel.dto.TicketBookingRequest request, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Vui lòng đăng nhập để đặt vé"));
        }
        
        try {
            BookingMaster booking = bookingService.createTicketBooking(principal.getName(), request);
            return ResponseEntity.ok(Map.of("bookingCode", booking.getBookingCode()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/bookings/hotel")
    public ResponseEntity<?> createRoomBooking(@RequestBody vn.huynhtoantravel.dto.RoomBookingRequest request, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Vui lòng đăng nhập để đặt phòng"));
        }
        
        try {
            BookingMaster booking = bookingService.createRoomBooking(principal.getName(), request);
            return ResponseEntity.ok(Map.of("bookingCode", booking.getBookingCode()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
