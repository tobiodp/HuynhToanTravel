package vn.huynhtoantravel.repository;
import org.springframework.data.jpa.repository.*; import vn.huynhtoantravel.domain.Hotel; import java.util.*;
public interface HotelRepository extends JpaRepository<Hotel,Long>{
 @Query("select distinct h from Hotel h left join fetch h.rooms where h.active=true order by h.starRating desc")
 List<Hotel> findByActiveTrueOrderByStarRatingDesc();
 
 @Query("select h from Hotel h left join fetch h.rooms where h.id = :id")
 Optional<Hotel> findByIdWithRooms(@org.springframework.data.repository.query.Param("id") Long id);
}
