package vn.huynhtoantravel.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.huynhtoantravel.service.PaymentWebhookService;
import vn.huynhtoantravel.webhook.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Map;

@RestController
@RequestMapping("/api/webhooks")
public class WebhookController {
    private static final Logger log = LoggerFactory.getLogger(WebhookController.class);

    private final SepayWebhookParser sepay;
    private final CassoWebhookParser casso;
    private final SepayHmacVerifier sepayHmac;
    private final PaymentWebhookService service;
    private final ObjectMapper mapper;

    @Value("${app.webhook.sepay-secret}")
    private String sepaySecret;

    @Value("${app.webhook.casso-secret}")
    private String cassoSecret;

    public WebhookController(SepayWebhookParser sepay, CassoWebhookParser casso, SepayHmacVerifier h,
                             PaymentWebhookService service, ObjectMapper mapper) {
        this.sepay = sepay;
        this.casso = casso;
        this.sepayHmac = h;
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping(value = "/sepay", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> sepay(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestHeader(value = "X-Secret-Key", required = false) String secretKeyHeader,
            @RequestHeader(value = "X-SePay-Signature", required = false) String sig,
            @RequestHeader(value = "X-SePay-Timestamp", required = false) String ts,
            @RequestBody String raw) {
        try {
            if (!isSepayAuthorized(authHeader, secretKeyHeader, sig, ts, raw)) {
                log.warn("SePay webhook rejected: Unauthorized");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("success", false, "error", "Unauthorized"));
            }

            JsonNode root = mapper.readTree(raw);
            var normalizedTx = sepay.parse(root);
            service.process(normalizedTx);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (IllegalArgumentException | IllegalStateException e) {
            log.warn("SePay webhook parsing or processing warning: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        } catch (Exception e) {
            log.error("SePay webhook error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    @PostMapping("/casso")
    public ResponseEntity<Map<String, Object>> casso(
            @RequestHeader(value = "X-Webhook-Secret", required = false) String secret,
            @RequestBody JsonNode body) {
        if (!safe(cassoSecret, secret)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("success", false));
        }
        service.process(casso.parse(body));
        return ResponseEntity.ok(Map.of("success", true));
    }

    private boolean isSepayAuthorized(String authHeader, String secretKeyHeader, String sig, String ts, String raw) {
        if (sepaySecret == null || sepaySecret.isBlank() || "change-me-sepay".equalsIgnoreCase(sepaySecret.trim())) {
            // Development or default placeholder
            return true;
        }
        String cleanSecret = sepaySecret.trim();

        // 1. Authorization header: "Apikey <KEY>" or "Bearer <KEY>" or "<KEY>"
        if (authHeader != null && !authHeader.isBlank()) {
            String token = authHeader.replaceFirst("(?i)^(Apikey|Bearer)\\s+", "").trim();
            if (safe(cleanSecret, token)) {
                return true;
            }
        }

        // 2. X-Secret-Key header
        if (secretKeyHeader != null && !secretKeyHeader.isBlank()) {
            if (safe(cleanSecret, secretKeyHeader.trim())) {
                return true;
            }
        }

        // 3. HMAC-SHA256 signature
        if (sig != null && !sig.isBlank() && ts != null && !ts.isBlank()) {
            if (sepayHmac.verify(cleanSecret, ts, sig, raw)) {
                return true;
            }
        }

        return false;
    }

    private boolean safe(String expected, String actual) {
        return expected != null && actual != null
                && MessageDigest.isEqual(expected.getBytes(StandardCharsets.UTF_8), actual.getBytes(StandardCharsets.UTF_8));
    }
}
