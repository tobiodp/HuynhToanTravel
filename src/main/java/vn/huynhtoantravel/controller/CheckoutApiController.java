package vn.huynhtoantravel.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.huynhtoantravel.service.PaymentIntentService;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/checkout")
public class CheckoutApiController {
    private final PaymentIntentService service;

    public CheckoutApiController(PaymentIntentService service) {
        this.service = service;
    }

    @PostMapping("/{bookingCode}/payment-intent")
    public ResponseEntity<?> create(@PathVariable String bookingCode, @RequestParam int percent, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Vui lòng đăng nhập"));
        }
        try {
            var intent = service.create(bookingCode.trim(), principal.getName(), percent);
            return ResponseEntity.ok(intent);
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Lỗi tạo yêu cầu thanh toán: " + e.getMessage()));
        }
    }
}
