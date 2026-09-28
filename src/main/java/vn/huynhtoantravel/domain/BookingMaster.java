package vn.huynhtoantravel.domain;

import jakarta.persistence.*;
import vn.huynhtoantravel.domain.enums.BookingStatus;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity @Table(name="bookings_master")
public class BookingMaster {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(name="booking_code", nullable=false, unique=true, length=20) private String bookingCode;
    @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="user_id") private User user;
    @Enumerated(EnumType.STRING) @Column(nullable=false) private BookingStatus status=BookingStatus.PENDING_PAYMENT;
    @Column(nullable=false) private long subtotal;
    @Column(name="discount_amount", nullable=false) private long discountAmount;
    @Column(name="grand_total", nullable=false) private long grandTotal;
    @Column(name="paid_amount", nullable=false) private long paidAmount;
    @Column(name="customer_note", length=1000) private String customerNote;
    @Column(name="created_at", insertable=false, updatable=false) private LocalDateTime createdAt;
    @OneToMany(mappedBy="booking", cascade=CascadeType.ALL, orphanRemoval=true) private List<VehicleBooking> vehicleBookings=new ArrayList<>();
    @OneToMany(mappedBy="booking", cascade=CascadeType.ALL, orphanRemoval=true) private List<TicketBooking> ticketBookings=new ArrayList<>();
    @OneToMany(mappedBy="booking", cascade=CascadeType.ALL, orphanRemoval=true) private List<RoomBooking> roomBookings=new ArrayList<>();
    @OneToMany(mappedBy="booking", cascade=CascadeType.ALL, orphanRemoval=true) private List<Payment> payments=new ArrayList<>();
    public Long getId(){return id;} public void setId(Long id){this.id=id;}
    public String getBookingCode(){return bookingCode;} public void setBookingCode(String bookingCode){this.bookingCode=bookingCode;}
    public User getUser(){return user;} public void setUser(User user){this.user=user;}
    public BookingStatus getStatus(){return status;} public void setStatus(BookingStatus status){this.status=status;}
    public long getSubtotal(){return subtotal;} public void setSubtotal(long subtotal){this.subtotal=subtotal;}
    public long getDiscountAmount(){return discountAmount;} public void setDiscountAmount(long v){this.discountAmount=v;}
    public long getGrandTotal(){return grandTotal;} public void setGrandTotal(long v){this.grandTotal=v;}
    public long getPaidAmount(){return paidAmount;} public void setPaidAmount(long v){this.paidAmount=v;}
    public String getCustomerNote(){return customerNote;} public void setCustomerNote(String v){this.customerNote=v;}
    public LocalDateTime getCreatedAt(){return createdAt;}
    public List<VehicleBooking> getVehicleBookings(){return vehicleBookings;}
    public List<TicketBooking> getTicketBookings(){return ticketBookings;}
    public List<RoomBooking> getRoomBookings(){return roomBookings;}
    public List<Payment> getPayments(){return payments;}
}
