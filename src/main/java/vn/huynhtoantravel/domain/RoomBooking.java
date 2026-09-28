package vn.huynhtoantravel.domain;
import jakarta.persistence.*;
import java.time.LocalDate;
@Entity @Table(name="room_bookings")
public class RoomBooking {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="booking_master_id") private BookingMaster booking;
 @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="room_id") private Room room;
 @Column(name="check_in", nullable=false) private LocalDate checkIn; @Column(name="check_out", nullable=false) private LocalDate checkOut;
 @Column(name="rooms_count", nullable=false) private int roomsCount=1; @Column(name="guests_count", nullable=false) private int guestsCount=1;
 @Column(nullable=false) private int nights; @Column(name="unit_price", nullable=false) private long unitPrice; @Column(name="line_total", nullable=false) private long lineTotal;
 public Long getId(){return id;} public BookingMaster getBooking(){return booking;} public void setBooking(BookingMaster v){booking=v;} public Room getRoom(){return room;} public void setRoom(Room v){room=v;}
 public LocalDate getCheckIn(){return checkIn;} public void setCheckIn(LocalDate v){checkIn=v;} public LocalDate getCheckOut(){return checkOut;} public void setCheckOut(LocalDate v){checkOut=v;}
 public int getRoomsCount(){return roomsCount;} public void setRoomsCount(int v){roomsCount=v;} public int getGuestsCount(){return guestsCount;} public void setGuestsCount(int v){guestsCount=v;}
 public int getNights(){return nights;} public void setNights(int v){nights=v;} public long getUnitPrice(){return unitPrice;} public void setUnitPrice(long v){unitPrice=v;} public long getLineTotal(){return lineTotal;} public void setLineTotal(long v){lineTotal=v;}
}
