package vn.huynhtoantravel.controller;

import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.huynhtoantravel.domain.enums.PaymentProvider;
import vn.huynhtoantravel.service.PaymentWebhookService;
import vn.huynhtoantravel.webhook.NormalizedBankTransaction;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/dev/payments")
public class DevPaymentController {
    private final PaymentWebhookService paymentService;

    public DevPaymentController(PaymentWebhookService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/mock")
    public ResponseEntity<?> mockPayment(@RequestParam String bookingCode, @RequestParam long amount) {
        NormalizedBankTransaction tx = new NormalizedBankTransaction(
                PaymentProvider.MANUAL,
                UUID.randomUUID().toString(),
                "DEV-MOCK",
                "DEV-GATEWAY",
                "DEV-ACCOUNT",
                amount,
                bookingCode,
                "{\"mock\":true,\"source\":\"DEV-GATEWAY\",\"booking\":\"" + bookingCode + "\"}"
        );
        
        try {
            paymentService.process(tx);
            return ResponseEntity.ok(Map.of("success", true, "message", "Đã giả lập nhận " + amount + " cho đơn " + bookingCode));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        }
    }
}
