package vn.huynhtoantravel.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.huynhtoantravel.domain.*;
import vn.huynhtoantravel.domain.enums.*;
import vn.huynhtoantravel.repository.*;

@Service
public class PaymentIntentService {
    private final BookingMasterRepository bookings;
    private final PaymentRepository payments;
    private final VietQrService vietQr;

    public PaymentIntentService(BookingMasterRepository b, PaymentRepository p, VietQrService v) {
        this.bookings = b;
        this.payments = p;
        this.vietQr = v;
    }

    @Transactional
    public PaymentIntent create(String bookingCode, String email, int percent) {
        BookingMaster b = bookings.findByBookingCode(bookingCode.toUpperCase())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn"));
        if (!b.getUser().getEmail().equalsIgnoreCase(email)) {
            throw new SecurityException("Bạn không sở hữu đơn này");
        }
        if (percent != 30 && percent != 50 && percent != 100) {
            throw new IllegalArgumentException("Chỉ hỗ trợ 30%, 50% hoặc 100%");
        }
        long remaining = Math.max(0, b.getGrandTotal() - b.getPaidAmount());
        if (remaining == 0) {
            throw new IllegalStateException("Đơn đã thanh toán đủ");
        }
        long amount = b.getPaidAmount() > 0 ? remaining : Math.max(1, Math.round(b.getGrandTotal() * (percent / 100.0)));
        PaymentPurpose purpose = b.getPaidAmount() > 0 ? PaymentPurpose.BALANCE : (percent == 30 ? PaymentPurpose.DEPOSIT_30 : percent == 50 ? PaymentPurpose.DEPOSIT_50 : PaymentPurpose.FULL);

        Payment p = new Payment();
        p.setBooking(b);
        p.setProvider(PaymentProvider.SEPAY);
        p.setPaymentPurpose(purpose);
        p.setStatus(PaymentStatus.PENDING);
        p.setExpectedAmount(amount);
        p.setReceivedAmount(0);
        p.setTransferContent(b.getBookingCode());
        payments.save(p);

        String qrUrl = vietQr.quickLink(amount, b.getBookingCode());
        return new PaymentIntent(
                b.getBookingCode(),
                amount,
                purpose.name(),
                qrUrl,
                vietQr.getBankId(),
                vietQr.getBankName(),
                vietQr.getAccountNo(),
                vietQr.getAccountName(),
                b.getBookingCode()
        );
    }

    public record PaymentIntent(
            String bookingCode,
            long amount,
            String purpose,
            String qrUrl,
            String bankId,
            String bankName,
            String accountNo,
            String accountName,
            String transferContent
    ) {}
}
