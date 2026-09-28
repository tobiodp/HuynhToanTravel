package vn.huynhtoantravel.domain;
import jakarta.persistence.*;
import vn.huynhtoantravel.domain.enums.*;
import java.time.LocalDateTime;
@Entity @Table(name="payments")
public class Payment {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="booking_master_id") private BookingMaster booking;
 @Enumerated(EnumType.STRING) @Column(nullable=false) private PaymentProvider provider;
 @Enumerated(EnumType.STRING) @Column(name="payment_purpose", nullable=false) private PaymentPurpose paymentPurpose;
 @Enumerated(EnumType.STRING) @Column(nullable=false) private PaymentStatus status=PaymentStatus.PENDING;
 @Column(name="expected_amount", nullable=false) private long expectedAmount; @Column(name="received_amount", nullable=false) private long receivedAmount;
 @Column(name="bank_code") private String bankCode; @Column(name="bank_account") private String bankAccount;
 @Column(name="transfer_content", nullable=false) private String transferContent;
 @Column(name="provider_transaction_id") private String providerTransactionId; @Column(name="provider_reference_code") private String providerReferenceCode;
 @Column(name="raw_payload", columnDefinition="json") private String rawPayload; @Column(name="confirmed_at") private LocalDateTime confirmedAt;
 @Column(name="created_at", insertable=false, updatable=false) private LocalDateTime createdAt;
 public Long getId(){return id;} public BookingMaster getBooking(){return booking;} public void setBooking(BookingMaster v){booking=v;} public PaymentProvider getProvider(){return provider;} public void setProvider(PaymentProvider v){provider=v;}
 public PaymentPurpose getPaymentPurpose(){return paymentPurpose;} public void setPaymentPurpose(PaymentPurpose v){paymentPurpose=v;} public PaymentStatus getStatus(){return status;} public void setStatus(PaymentStatus v){status=v;}
 public long getExpectedAmount(){return expectedAmount;} public void setExpectedAmount(long v){expectedAmount=v;} public long getReceivedAmount(){return receivedAmount;} public void setReceivedAmount(long v){receivedAmount=v;}
 public String getBankCode(){return bankCode;} public void setBankCode(String v){bankCode=v;} public String getBankAccount(){return bankAccount;} public void setBankAccount(String v){bankAccount=v;}
 public String getTransferContent(){return transferContent;} public void setTransferContent(String v){transferContent=v;} public String getProviderTransactionId(){return providerTransactionId;} public void setProviderTransactionId(String v){providerTransactionId=v;}
 public String getProviderReferenceCode(){return providerReferenceCode;} public void setProviderReferenceCode(String v){providerReferenceCode=v;} public String getRawPayload(){return rawPayload;} public void setRawPayload(String v){rawPayload=v;}
 public LocalDateTime getConfirmedAt(){return confirmedAt;} public void setConfirmedAt(LocalDateTime v){confirmedAt=v;} public LocalDateTime getCreatedAt(){return createdAt;}
}
