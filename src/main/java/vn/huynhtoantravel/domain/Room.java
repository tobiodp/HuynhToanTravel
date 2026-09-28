package vn.huynhtoantravel.domain;
import jakarta.persistence.*;
@Entity @Table(name="rooms")
public class Room {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="hotel_id") private Hotel hotel;
 @Column(nullable=false) private String code; @Column(name="room_type", nullable=false) private String roomType;
 @Column(name="max_guests", nullable=false) private int maxGuests; @Column(name="price_per_night", nullable=false) private long pricePerNight;
 @Column(name="inventory_count", nullable=false) private int inventoryCount=1; @Column(nullable=false) private boolean active=true;
 public Long getId(){return id;} public Hotel getHotel(){return hotel;} public void setHotel(Hotel v){hotel=v;} public String getCode(){return code;} public void setCode(String v){code=v;}
 public String getRoomType(){return roomType;} public void setRoomType(String v){roomType=v;} public int getMaxGuests(){return maxGuests;} public void setMaxGuests(int v){maxGuests=v;}
 public long getPricePerNight(){return pricePerNight;} public void setPricePerNight(long v){pricePerNight=v;} public int getInventoryCount(){return inventoryCount;} public void setInventoryCount(int v){inventoryCount=v;}
 public boolean isActive(){return active;} public void setActive(boolean v){active=v;}
}
