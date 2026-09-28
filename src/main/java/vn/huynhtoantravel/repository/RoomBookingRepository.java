package vn.huynhtoantravel.repository;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import vn.huynhtoantravel.domain.RoomBooking;
import vn.huynhtoantravel.domain.enums.BookingStatus;
import java.time.LocalDate;
import java.util.Collection;
public interface RoomBookingRepository extends JpaRepository<RoomBooking,Long>{
 @Query("""
   select coalesce(sum(rb.roomsCount),0) from RoomBooking rb
   where rb.room.id=:roomId
     and rb.booking.status in :activeStatuses
     and rb.checkIn < :checkOut
     and rb.checkOut > :checkIn
 """)
 long countReservedInventory(@Param("roomId") Long roomId,
                             @Param("checkIn") LocalDate checkIn,
                             @Param("checkOut") LocalDate checkOut,
                             @Param("activeStatuses") Collection<BookingStatus> activeStatuses);
}
