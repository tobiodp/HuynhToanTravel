package vn.huynhtoantravel.domain;
import jakarta.persistence.*;
import vn.huynhtoantravel.domain.enums.TicketType;
import java.time.*;
@Entity @Table(name="ticket_bookings")
public class TicketBooking {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="booking_master_id") private BookingMaster booking;
 @Enumerated(EnumType.STRING) @Column(name="ticket_type", nullable=false) private TicketType ticketType;
 @Column(name="service_date", nullable=false) private LocalDate serviceDate;
 @Column(name="local_resident", nullable=false) private boolean localResident;
 @Column(nullable=false) private int quantity;
 @Column(name="unit_price", nullable=false) private long unitPrice;
 @Column(name="line_total", nullable=false) private long lineTotal;
 @Column(name="qr_token") private String qrToken; @Column(name="qr_issued_at") private LocalDateTime qrIssuedAt;
 public Long getId(){return id;} public BookingMaster getBooking(){return booking;} public void setBooking(BookingMaster v){booking=v;}
 public TicketType getTicketType(){return ticketType;} public void setTicketType(TicketType v){ticketType=v;} public LocalDate getServiceDate(){return serviceDate;} public void setServiceDate(LocalDate v){serviceDate=v;}
 public boolean isLocalResident(){return localResident;} public void setLocalResident(boolean v){localResident=v;} public int getQuantity(){return quantity;} public void setQuantity(int v){quantity=v;}
 public long getUnitPrice(){return unitPrice;} public void setUnitPrice(long v){unitPrice=v;} public long getLineTotal(){return lineTotal;} public void setLineTotal(long v){lineTotal=v;}
 public String getQrToken(){return qrToken;} public void setQrToken(String v){qrToken=v;} public LocalDateTime getQrIssuedAt(){return qrIssuedAt;} public void setQrIssuedAt(LocalDateTime v){qrIssuedAt=v;}
}
