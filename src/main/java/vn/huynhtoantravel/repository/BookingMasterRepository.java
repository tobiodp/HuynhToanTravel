package vn.huynhtoantravel.repository;
import org.springframework.data.jpa.repository.JpaRepository; 
import org.springframework.data.jpa.repository.Query;
import vn.huynhtoantravel.domain.BookingMaster; 
import java.util.Optional;
import java.util.List;

public interface BookingMasterRepository extends JpaRepository<BookingMaster,Long> { 
    Optional<BookingMaster> findByBookingCode(String bookingCode); 
    Optional<BookingMaster> findByBookingCodeIgnoreCase(String bookingCode); 
    
    @Query("SELECT COALESCE(SUM(b.paidAmount), 0) FROM BookingMaster b")
    long sumAllPaidAmount();
    
    List<BookingMaster> findAllByOrderByCreatedAtDesc();
}
