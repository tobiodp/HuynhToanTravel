package vn.huynhtoantravel.controller;
import org.springframework.web.bind.annotation.*; import vn.huynhtoantravel.repository.BookingMasterRepository; import java.util.Map;
@RestController @RequestMapping("/api/bookings")
public class BookingStatusController {
 private final BookingMasterRepository bookings; public BookingStatusController(BookingMasterRepository b){bookings=b;}
 @GetMapping("/{code}/status") public Map<String,Object> status(@PathVariable String code){ var b=bookings.findByBookingCode(code.toUpperCase()).orElseThrow(); return Map.of("code",b.getBookingCode(),"status",b.getStatus().name(),"paidAmount",b.getPaidAmount(),"grandTotal",b.getGrandTotal()); }
}
