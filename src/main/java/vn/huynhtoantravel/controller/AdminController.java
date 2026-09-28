package vn.huynhtoantravel.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.huynhtoantravel.domain.BookingMaster;
import vn.huynhtoantravel.domain.VehicleRate;
import vn.huynhtoantravel.domain.enums.BookingStatus;
import vn.huynhtoantravel.domain.enums.TripType;
import vn.huynhtoantravel.domain.enums.VehicleType;
import vn.huynhtoantravel.repository.BookingMasterRepository;
import vn.huynhtoantravel.repository.VehicleRateRepository;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final BookingMasterRepository bookingMasterRepository;
    private final VehicleRateRepository vehicleRateRepository;

    public AdminController(BookingMasterRepository bookingMasterRepository, VehicleRateRepository vehicleRateRepository) {
        this.bookingMasterRepository = bookingMasterRepository;
        this.vehicleRateRepository = vehicleRateRepository;
    }

    @GetMapping
    public String dashboard(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login"; // Cần đăng nhập
        }

        List<BookingMaster> bookings = bookingMasterRepository.findAllByOrderByCreatedAtDesc();
        long totalRevenue = bookingMasterRepository.sumAllPaidAmount();
        
        long pendingCount = bookings.stream().filter(b -> b.getStatus() == BookingStatus.PENDING_PAYMENT).count();
        long depositedCount = bookings.stream().filter(b -> b.getStatus() == BookingStatus.DEPOSITED).count();
        long paidCount = bookings.stream().filter(b -> b.getStatus() == BookingStatus.PAID_FULL).count();

        model.addAttribute("bookings", bookings);
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("depositedCount", depositedCount);
        model.addAttribute("paidCount", paidCount);
        model.addAttribute("totalBookings", bookings.size());
        model.addAttribute("vehicleRates", vehicleRateRepository.findAllByOrderByRouteNameAsc());

        return "admin-dashboard";
    }

    @PostMapping("/bookings/{id}/status")
    public String updateBookingStatus(@PathVariable Long id, @RequestParam BookingStatus status, RedirectAttributes ra, Principal principal) {
        if (principal == null) return "redirect:/login";
        
        BookingMaster booking = bookingMasterRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn"));
        booking.setStatus(status);
        bookingMasterRepository.save(booking);
        
        ra.addFlashAttribute("message", "Đã cập nhật trạng thái đơn hàng " + booking.getBookingCode() + " thành " + status);
        return "redirect:/admin";
    }

    @PostMapping("/vehicle-rates")
    public String saveVehicleRate(@RequestParam String routeCode,
                                  @RequestParam String routeName,
                                  @RequestParam VehicleType vehicleType,
                                  @RequestParam TripType tripType,
                                  @RequestParam long price,
                                  RedirectAttributes ra,
                                  Principal principal) {
        if (principal == null) return "redirect:/login";

        String code = routeCode.trim().toUpperCase();
        String name = routeName.trim();

        VehicleRate rate = vehicleRateRepository.findByRouteCodeAndVehicleTypeAndTripType(code, vehicleType, tripType)
                .orElse(new VehicleRate(code, name, vehicleType, tripType, price));

        rate.setRouteName(name);
        rate.setPrice(price);
        rate.setActive(true);
        vehicleRateRepository.save(rate);

        ra.addFlashAttribute("message", "Đã lưu thông tin tuyến xe: " + name + " (" + vehicleType + " - " + tripType + ")");
        return "redirect:/admin#routes";
    }

    @PostMapping("/vehicle-rates/{id}/toggle")
    public String toggleVehicleRate(@PathVariable Long id, RedirectAttributes ra, Principal principal) {
        if (principal == null) return "redirect:/login";

        VehicleRate rate = vehicleRateRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tuyến xe"));

        rate.setActive(!rate.isActive());
        vehicleRateRepository.save(rate);

        ra.addFlashAttribute("message", "Đã " + (rate.isActive() ? "kích hoạt" : "vô hiệu hóa") + " tuyến xe " + rate.getRouteName());
        return "redirect:/admin#routes";
    }

    @PostMapping("/vehicle-rates/{id}/delete")
    public String deleteVehicleRate(@PathVariable Long id, RedirectAttributes ra, Principal principal) {
        if (principal == null) return "redirect:/login";

        vehicleRateRepository.deleteById(id);
        ra.addFlashAttribute("message", "Đã xóa tuyến xe thành công");
        return "redirect:/admin#routes";
    }
}
