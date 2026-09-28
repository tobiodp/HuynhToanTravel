package vn.huynhtoantravel.service;
import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;
import vn.huynhtoantravel.domain.Room; import vn.huynhtoantravel.domain.enums.BookingStatus; import vn.huynhtoantravel.repository.*;
import java.time.LocalDate; import java.util.List;
@Service
public class RoomAvailabilityService {
 private final RoomRepository rooms; private final RoomBookingRepository roomBookings;
 public RoomAvailabilityService(RoomRepository rooms, RoomBookingRepository roomBookings){this.rooms=rooms;this.roomBookings=roomBookings;}
 @Transactional(readOnly=true)
 public int availableInventory(Long roomId, LocalDate checkIn, LocalDate checkOut){
   if(checkIn==null || checkOut==null || !checkOut.isAfter(checkIn)) throw new IllegalArgumentException("Khoảng ngày không hợp lệ");
   Room room=rooms.findById(roomId).orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phòng"));
   long reserved=roomBookings.countReservedInventory(roomId, checkIn, checkOut,
     List.of(BookingStatus.DEPOSITED, BookingStatus.PAID_FULL, BookingStatus.DISPATCHED));
   return Math.max(0, room.getInventoryCount()-(int)reserved);
 }
 public void assertAvailable(Long roomId, LocalDate checkIn, LocalDate checkOut, int requested){
   if(requested<1 || availableInventory(roomId,checkIn,checkOut)<requested) throw new IllegalStateException("Phòng đã hết hoặc bị trùng lịch");
 }
}
