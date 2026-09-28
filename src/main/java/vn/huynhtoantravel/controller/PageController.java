package vn.huynhtoantravel.controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import vn.huynhtoantravel.repository.BookingMasterRepository;

@Controller
public class PageController {
    private final BookingMasterRepository bookingMasterRepository;

    public PageController(BookingMasterRepository bookingMasterRepository) {
        this.bookingMasterRepository = bookingMasterRepository;
    }

    @GetMapping("/") String home(org.springframework.ui.Model model, java.security.Principal principal){
        model.addAttribute("isAdmin", principal != null && 
            org.springframework.security.core.context.SecurityContextHolder.getContext()
                .getAuthentication().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
        return "index";
    }
    @GetMapping("/login") String login(@org.springframework.web.bind.annotation.RequestParam(required = false) String tab, org.springframework.ui.Model model){
        model.addAttribute("initialTab", tab != null ? tab : "login");
        return "login";
    }
    @GetMapping("/register") String register(org.springframework.ui.Model model){
        model.addAttribute("initialTab", "register");
        return "login";
    }
    @GetMapping("/cart") String cart(){return "cart";}
    @GetMapping("/checkout") String checkout(){return "checkout";}
    
    @GetMapping("/checkout/{bookingCode}") 
    String checkoutWithCode(@PathVariable String bookingCode, org.springframework.ui.Model model) {
        String cleanCode = bookingCode != null ? bookingCode.trim() : "";
        var opt = bookingMasterRepository.findByBookingCodeIgnoreCase(cleanCode);
        if (opt.isEmpty()) {
            model.addAttribute("error", "Không tìm thấy thông tin cho mã đơn hàng: " + cleanCode);
            model.addAttribute("bookingCode", cleanCode);
            model.addAttribute("booking", null);
            return "checkout";
        }
        vn.huynhtoantravel.domain.BookingMaster booking = opt.get();
        model.addAttribute("bookingCode", booking.getBookingCode());
        model.addAttribute("booking", booking);
        return "checkout";
    }
    @GetMapping("/booking/vehicle") String vehicle(){return "vehicle-booking";}
    @GetMapping("/tickets") String tickets(){return "tickets";}

    @GetMapping("/payment/success")
    String paymentSuccess(@org.springframework.web.bind.annotation.RequestParam(required = false) String bookingCode,
                          @org.springframework.web.bind.annotation.RequestParam(required = false) Long orderCode,
                          org.springframework.ui.Model model) {
        String code = bookingCode != null ? bookingCode.trim() : "";
        var opt = bookingMasterRepository.findByBookingCodeIgnoreCase(code);
        if (opt.isPresent()) {
            model.addAttribute("booking", opt.get());
            model.addAttribute("bookingCode", opt.get().getBookingCode());
        } else {
            model.addAttribute("booking", null);
            model.addAttribute("bookingCode", code);
        }
        return "payment-success";
    }
}
