package vn.huynhtoantravel.domain;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.*;
@Entity @Table(name="hotels")
public class Hotel {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false) private String name; @Column(nullable=false, unique=true) private String slug;
 @Column(nullable=false) private String area; @Column(nullable=false) private String address;
 @Column(nullable=false, precision=10, scale=7) private BigDecimal latitude; @Column(nullable=false, precision=10, scale=7) private BigDecimal longitude;
 @Column(name="star_rating", nullable=false) private int starRating; @Column(columnDefinition="TEXT") private String description;
 @Column(name="image_url") private String imageUrl; @Column(nullable=false) private boolean active=true;
 @OneToMany(mappedBy="hotel", fetch=FetchType.LAZY) private List<Room> rooms=new ArrayList<>();
 public Long getId(){return id;} public String getName(){return name;} public void setName(String v){name=v;} public String getSlug(){return slug;} public void setSlug(String v){slug=v;}
 public String getArea(){return area;} public void setArea(String v){area=v;} public String getAddress(){return address;} public void setAddress(String v){address=v;}
 public BigDecimal getLatitude(){return latitude;} public void setLatitude(BigDecimal v){latitude=v;} public BigDecimal getLongitude(){return longitude;} public void setLongitude(BigDecimal v){longitude=v;}
 public int getStarRating(){return starRating;} public void setStarRating(int v){starRating=v;} public String getDescription(){return description;} public void setDescription(String v){description=v;}
 public String getImageUrl(){return imageUrl;} public void setImageUrl(String v){imageUrl=v;} public boolean isActive(){return active;} public void setActive(boolean v){active=v;} public List<Room> getRooms(){return rooms;}
}
