package vn.huynhtoantravel.webhook;

import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.HexFormat;

@Component
public class SepayHmacVerifier {
    public boolean verify(String secret, String timestamp, String signature, String rawBody) {
        try {
            if (secret == null || timestamp == null || signature == null || rawBody == null) {
                return false;
            }
            long ts = Long.parseLong(timestamp);
            if (Math.abs(Instant.now().getEpochSecond() - ts) > 300) {
                return false;
            }
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            String hex = HexFormat.of().formatHex(mac.doFinal((timestamp + "." + rawBody).getBytes(StandardCharsets.UTF_8)));
            String expected = "sha256=" + hex;
            byte[] sigBytes = signature.getBytes(StandardCharsets.UTF_8);
            return MessageDigest.isEqual(expected.getBytes(StandardCharsets.UTF_8), sigBytes)
                    || MessageDigest.isEqual(hex.getBytes(StandardCharsets.UTF_8), sigBytes);
        } catch (Exception e) {
            return false;
        }
    }
}
