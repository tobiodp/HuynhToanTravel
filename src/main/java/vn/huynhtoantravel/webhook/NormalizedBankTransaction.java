package vn.huynhtoantravel.webhook;
import vn.huynhtoantravel.domain.enums.PaymentProvider;
public record NormalizedBankTransaction(PaymentProvider provider, String transactionId, String referenceCode, String bankCode, String accountNumber, long amount, String content, String rawJson) {}
