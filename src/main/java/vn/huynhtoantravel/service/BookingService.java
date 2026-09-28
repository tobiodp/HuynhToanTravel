package vn.huynhtoantravel.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.huynhtoantravel.domain.BookingMaster;
import vn.huynhtoantravel.domain.User;
import vn.huynhtoantravel.domain.VehicleBooking;
import vn.huynhtoantravel.domain.VehicleRate;
import vn.huynhtoantravel.domain.enums.BookingStatus;
import vn.huynhtoantravel.dto.VehicleBookingRequest;
import vn.huynhtoantravel.repository.BookingMasterRepository;
import vn.huynhtoantravel.repository.UserRepository;
import vn.huynhtoantravel.repository.VehicleRateRepository;

import java.util.UUID;

@Service
@Transactional
public class BookingService {

    private final BookingMasterRepository bookingMasterRepository;
    private final UserRepository userRepository;
    private final VehicleRateRepository vehicleRateRepository;
    private final vn.huynhtoantravel.repository.TicketRateRepository ticketRateRepository;
    private final vn.huynhtoantravel.repository.RoomRepository roomRepository;
    private final RoomAvailabilityService roomAvailabilityService;

    public BookingService(BookingMasterRepository bookingMasterRepository, UserRepository userRepository, 
                          VehicleRateRepository vehicleRateRepository, vn.huynhtoantravel.repository.TicketRateRepository ticketRateRepository,
                          vn.huynhtoantravel.repository.RoomRepository roomRepository, RoomAvailabilityService roomAvailabilityService) {
        this.bookingMasterRepository = bookingMasterRepository;
        this.userRepository = userRepository;
        this.vehicleRateRepository = vehicleRateRepository;
        this.ticketRateRepository = ticketRateRepository;
        this.roomRepository = roomRepository;
        this.roomAvailabilityService = roomAvailabilityService;
    }

    public BookingMaster createVehicleBooking(String email, VehicleBookingRequest request) {
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        VehicleRate rate = vehicleRateRepository.findByRouteCodeAndVehicleTypeAndTripTypeAndActiveTrue(
                request.getRouteCode(), request.getVehicleType(), request.getTripType())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tuyến xe hoặc giá không hợp lệ"));

        BookingMaster booking = new BookingMaster();
        booking.setBookingCode("HT" + UUID.randomUUID().toString().substring(0, 6).toUpperCase());
        booking.setUser(user);
        booking.setStatus(BookingStatus.PENDING_PAYMENT);
        booking.setSubtotal(rate.getPrice());
        booking.setDiscountAmount(0);
        booking.setGrandTotal(rate.getPrice());
        booking.setPaidAmount(0);
        booking.setCustomerNote(request.getCustomerNote());

        VehicleBooking vb = new VehicleBooking();
        vb.setBooking(booking);
        vb.setRouteCode(rate.getRouteCode());
        vb.setRouteName(rate.getRouteName());
        vb.setPickupAddress(request.getPickupAddress());
        vb.setDropoffAddress(request.getDropoffAddress());
        vb.setPickupAt(request.getPickupAt());
        vb.setVehicleType(request.getVehicleType());
        vb.setTripType(request.getTripType());
        vb.setQuantity(1);
        vb.setUnitPrice(rate.getPrice());
        vb.setLineTotal(rate.getPrice());

        booking.getVehicleBookings().add(vb);

        return bookingMasterRepository.save(booking);
    }

    public BookingMaster createTicketBooking(String email, vn.huynhtoantravel.dto.TicketBookingRequest request) {
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("Vui lòng chọn ít nhất một vé");
        }

        BookingMaster booking = new BookingMaster();
        booking.setBookingCode("HT" + UUID.randomUUID().toString().substring(0, 6).toUpperCase());
        booking.setUser(user);
        booking.setStatus(BookingStatus.PENDING_PAYMENT);
        booking.setCustomerNote(request.getCustomerNote());

        long grandTotal = 0;
        java.util.List<vn.huynhtoantravel.domain.TicketRate> rates = ticketRateRepository.findByActiveTrue();

        for (vn.huynhtoantravel.dto.TicketBookingRequest.TicketItemRequest item : request.getItems()) {
            if (item.getQuantity() <= 0) continue;

            vn.huynhtoantravel.domain.TicketRate rate = rates.stream()
                .filter(r -> r.getTicketType().name().equals(item.getTicketType()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Loại vé không hợp lệ: " + item.getTicketType()));

            long unitPrice = (item.isLocalResident() && rate.getLocalPrice() != null) ? rate.getLocalPrice() : rate.getPrice();
            long lineTotal = unitPrice * item.getQuantity();
            grandTotal += lineTotal;

            vn.huynhtoantravel.domain.TicketBooking tb = new vn.huynhtoantravel.domain.TicketBooking();
            tb.setBooking(booking);
            tb.setTicketType(rate.getTicketType());
            tb.setQuantity(item.getQuantity());
            tb.setLocalResident(item.isLocalResident());
            tb.setServiceDate(item.getServiceDate());
            tb.setUnitPrice(unitPrice);
            tb.setLineTotal(lineTotal);
            
            booking.getTicketBookings().add(tb);
        }

        if (booking.getTicketBookings().isEmpty()) {
            throw new IllegalArgumentException("Số lượng vé phải lớn hơn 0");
        }

        booking.setSubtotal(grandTotal);
        booking.setDiscountAmount(0);
        booking.setGrandTotal(grandTotal);
        booking.setPaidAmount(0);

        return bookingMasterRepository.save(booking);
    }

    public java.util.List<vn.huynhtoantravel.domain.TicketRate> getTicketRates() {
        return ticketRateRepository.findByActiveTrue();
    }

    public BookingMaster createRoomBooking(String email, vn.huynhtoantravel.dto.RoomBookingRequest request) {
        User user = userRepository.findByEmailIgnoreCase(email).orElseThrow(() -> new IllegalArgumentException("User not found"));
        vn.huynhtoantravel.domain.Room room = roomRepository.findById(request.getRoomId()).orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phòng"));
        
        roomAvailabilityService.assertAvailable(room.getId(), request.getCheckIn(), request.getCheckOut(), request.getRoomsCount());

        int nights = (int) java.time.temporal.ChronoUnit.DAYS.between(request.getCheckIn(), request.getCheckOut());
        if (nights <= 0) throw new IllegalArgumentException("Ngày nhận và trả phòng không hợp lệ");

        long lineTotal = room.getPricePerNight() * nights * request.getRoomsCount();

        BookingMaster booking = new BookingMaster();
        booking.setBookingCode("HT" + java.util.UUID.randomUUID().toString().substring(0, 6).toUpperCase());
        booking.setUser(user);
        booking.setStatus(BookingStatus.PENDING_PAYMENT);
        booking.setCustomerNote(request.getCustomerNote());
        booking.setSubtotal(lineTotal);
        booking.setDiscountAmount(0);
        booking.setGrandTotal(lineTotal);
        booking.setPaidAmount(0);

        vn.huynhtoantravel.domain.RoomBooking rb = new vn.huynhtoantravel.domain.RoomBooking();
        rb.setBooking(booking);
        rb.setRoom(room);
        rb.setCheckIn(request.getCheckIn());
        rb.setCheckOut(request.getCheckOut());
        rb.setRoomsCount(request.getRoomsCount());
        rb.setGuestsCount(request.getGuestsCount());
        rb.setNights(nights);
        rb.setUnitPrice(room.getPricePerNight());
        rb.setLineTotal(lineTotal);
        
        booking.getRoomBookings().add(rb);

        return bookingMasterRepository.save(booking);
    }
}
