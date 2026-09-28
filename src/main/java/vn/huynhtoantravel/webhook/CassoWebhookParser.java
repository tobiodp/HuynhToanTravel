package vn.huynhtoantravel.webhook;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;
import vn.huynhtoantravel.domain.enums.PaymentProvider;
@Component
public class CassoWebhookParser {
 public NormalizedBankTransaction parse(JsonNode root){
   JsonNode n = root.has("data") && root.get("data").isArray() && !root.get("data").isEmpty() ? root.get("data").get(0) : root;
   JsonNode desc = n.path("description");
   String content = desc.isObject() ? desc.path("original").asText("") : desc.asText(n.path("content").asText(""));
   long amount = n.has("amount") ? n.path("amount").asLong() : n.path("amoun").asLong();
   String account = n.path("accountNumber").asText(n.path("acountNumber").asText(null));
   return new NormalizedBankTransaction(PaymentProvider.CASSO,
       n.path("id").asText(), n.path("refid").asText(null), n.path("bankName").asText(null),
       account, amount, content, root.toString());
 }
}
