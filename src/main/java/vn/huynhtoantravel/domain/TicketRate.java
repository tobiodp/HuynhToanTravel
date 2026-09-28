package vn.huynhtoantravel.domain;
import jakarta.persistence.*;
import vn.huynhtoantravel.domain.enums.TicketType;
@Entity @Table(name="ticket_rates")
public class TicketRate {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Enumerated(EnumType.STRING) @Column(name="ticket_type", nullable=false, unique=true) private TicketType ticketType;
 @Column(nullable=false) private String name;
 @Column(nullable=false) private String description;
 @Column(nullable=false) private long price;
 @Column(name="local_price") private Long localPrice; // Giá cho người địa phương (Đà Nẵng/Quảng Nam), nếu có
 @Column(nullable=false) private boolean active=true;
 public Long getId(){return id;}
 public TicketType getTicketType(){return ticketType;} public void setTicketType(TicketType v){ticketType=v;}
 public String getName(){return name;} public void setName(String v){name=v;}
 public String getDescription(){return description;} public void setDescription(String v){description=v;}
 public long getPrice(){return price;} public void setPrice(long v){price=v;}
 public Long getLocalPrice(){return localPrice;} public void setLocalPrice(Long v){localPrice=v;}
 public boolean isActive(){return active;} public void setActive(boolean v){active=v;}
}
