package vn.huynhtoantravel.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.huynhtoantravel.domain.*;
import vn.huynhtoantravel.domain.enums.*;
import vn.huynhtoantravel.repository.*;
import vn.huynhtoantravel.webhook.NormalizedBankTransaction;

import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.*;

@Service
public class PaymentWebhookService {
    private static final Logger log = LoggerFactory.getLogger(PaymentWebhookService.class);
    private static final Pattern CODE = Pattern.compile("\\bHT-?[A-Z0-9]{4,12}\\b", Pattern.CASE_INSENSITIVE);

    private final BookingMasterRepository bookings;
    private final PaymentRepository payments;
    private final EmailService emailService;

    public PaymentWebhookService(BookingMasterRepository bookings, PaymentRepository payments, EmailService emailService) {
        this.bookings = bookings;
        this.payments = payments;
        this.emailService = emailService;
    }

    @Transactional
    public BookingMaster process(NormalizedBankTransaction tx) {
        if (tx.transactionId() == null || tx.transactionId().isBlank()) {
            throw new IllegalArgumentException("Thiếu transaction id");
        }

        var existing = payments.findByProviderAndProviderTransactionId(tx.provider(), tx.transactionId());
        if (existing.isPresent()) {
            log.info("Transaction {} already processed for booking {}", tx.transactionId(), existing.get().getBooking().getBookingCode());
            return existing.get().getBooking();
        }

        Matcher m = CODE.matcher(tx.content() == null ? "" : tx.content().toUpperCase());
        if (!m.find()) {
            throw new IllegalArgumentException("Không tìm thấy mã HT-XXXXX hoặc HTXXXXX trong nội dung chuyển khoản: " + tx.content());
        }

        String rawCode = m.group().toUpperCase();
        // Remove hyphen if bookingCode in DB is HT... without hyphen or vice-versa
        BookingMaster b = bookings.findByBookingCodeIgnoreCase(rawCode)
                .or(() -> bookings.findByBookingCodeIgnoreCase(rawCode.replace("-", "")))
                .orElseThrow(() -> new IllegalArgumentException("Không tồn tại đơn " + rawCode));

        List<Payment> pendingList = payments.findAllByBooking_BookingCodeAndStatusOrderByCreatedAtDesc(b.getBookingCode(), PaymentStatus.PENDING);
        Payment p;
        if (pendingList.isEmpty()) {
            p = new Payment();
            p.setBooking(b);
            p.setProvider(tx.provider());
            p.setPaymentPurpose(PaymentPurpose.FULL);
            p.setStatus(PaymentStatus.PENDING);
            p.setExpectedAmount(tx.amount());
            p.setReceivedAmount(0);
            p.setTransferContent(b.getBookingCode());
            p = payments.save(p);
        } else {
            // Pick the pending payment that matches exact amount, or fallback to the latest
            p = pendingList.stream()
                    .filter(item -> item.getExpectedAmount() == tx.amount())
                    .findFirst()
                    .orElse(pendingList.get(0));
        }

        p.setProvider(tx.provider());
        p.setProviderTransactionId(tx.transactionId());
        p.setProviderReferenceCode(tx.referenceCode());
        p.setBankCode(tx.bankCode());
        p.setBankAccount(tx.accountNumber());
        p.setReceivedAmount(tx.amount());

        String raw = tx.rawJson();
        if (raw == null || raw.isBlank() || (!raw.trim().startsWith("{") && !raw.trim().startsWith("["))) {
            raw = "{}";
        }
        p.setRawPayload(raw);
        p.setStatus(PaymentStatus.CONFIRMED);
        p.setConfirmedAt(LocalDateTime.now());
        payments.save(p);

        long confirmed = payments.sumConfirmed(b.getId());
        b.setPaidAmount(confirmed);
        b.setStatus(confirmed >= b.getGrandTotal() ? BookingStatus.PAID_FULL : BookingStatus.DEPOSITED);
        issueTicketTokens(b);
        BookingMaster saved = bookings.save(b);

        try {
            emailService.sendPaymentConfirmed(saved);
        } catch (Exception e) {
            log.error("Failed to send payment confirmation email for booking {}: {}", saved.getBookingCode(), e.getMessage());
        }

        log.info("Booking {} successfully updated via webhook: status={}, paidAmount={}", saved.getBookingCode(), saved.getStatus(), confirmed);
        return saved;
    }

    private void issueTicketTokens(BookingMaster b) {
        if (b.getTicketBookings() == null) return;
        for (TicketBooking t : b.getTicketBookings()) {
            if (t.getQrToken() == null) {
                t.setQrToken("HTT:" + b.getBookingCode() + ":TICKET:" + t.getId() + ":" + java.util.UUID.randomUUID());
                t.setQrIssuedAt(LocalDateTime.now());
            }
        }
    }
}
