package vn.huynhtoantravel.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import vn.huynhtoantravel.domain.Payment;
import vn.huynhtoantravel.domain.enums.*;
import java.util.*;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findFirstByBooking_BookingCodeAndStatusOrderByCreatedAtDesc(String bookingCode, PaymentStatus status);
    List<Payment> findAllByBooking_BookingCodeAndStatusOrderByCreatedAtDesc(String bookingCode, PaymentStatus status);
    Optional<Payment> findByProviderAndProviderTransactionId(PaymentProvider provider, String providerTransactionId);
    Optional<Payment> findByProviderAndProviderReferenceCode(PaymentProvider provider, String providerReferenceCode);

    @Query("select coalesce(sum(p.receivedAmount),0) from Payment p where p.booking.id=:bookingId and p.status='CONFIRMED'")
    long sumConfirmed(@Param("bookingId") Long bookingId);
}
