package vn.huynhtoantravel.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.huynhtoantravel.domain.TicketRate;
import java.util.List;
public interface TicketRateRepository extends JpaRepository<TicketRate, Long> {
    List<TicketRate> findByActiveTrue();
}
