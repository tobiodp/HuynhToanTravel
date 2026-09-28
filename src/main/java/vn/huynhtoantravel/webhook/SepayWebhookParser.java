package vn.huynhtoantravel.webhook;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;
import vn.huynhtoantravel.domain.enums.PaymentProvider;

@Component
public class SepayWebhookParser {
    public NormalizedBankTransaction parse(JsonNode n) {
        String transferType = n.path("transferType").asText("");
        if (!transferType.isBlank() && !"in".equalsIgnoreCase(transferType)) {
            throw new IllegalArgumentException("Bỏ qua giao dịch tiền ra (" + transferType + ")");
        }

        long amount = n.path("transferAmount").asLong(0);
        if (amount <= 0 && n.hasNonNull("amountIn")) {
            amount = n.path("amountIn").asLong(0);
        }

        String content = n.path("content").asText("");
        if (content.isBlank() && n.hasNonNull("transactionContent")) {
            content = n.path("transactionContent").asText("");
        }
        if (content.isBlank() && n.hasNonNull("description")) {
            content = n.path("description").asText("");
        }
        if (content.isBlank() && n.hasNonNull("code")) {
            content = n.path("code").asText("");
        }

        String refCode = n.hasNonNull("referenceCode") ? n.path("referenceCode").asText() : null;
        if (refCode == null && n.hasNonNull("referenceNumber")) {
            refCode = n.path("referenceNumber").asText();
        }

        String accNo = n.hasNonNull("accountNumber") ? n.path("accountNumber").asText() : null;
        if (accNo == null && n.hasNonNull("subAccount")) {
            accNo = n.path("subAccount").asText();
        }

        String gateway = n.hasNonNull("gateway") ? n.path("gateway").asText() : null;
        String id = n.hasNonNull("id") ? n.path("id").asText() : String.valueOf(System.currentTimeMillis());

        return new NormalizedBankTransaction(
                PaymentProvider.SEPAY,
                id,
                refCode,
                gateway,
                accNo,
                amount,
                content,
                n.toString()
        );
    }
}
