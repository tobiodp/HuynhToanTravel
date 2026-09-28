package vn.huynhtoantravel.repository;
import org.springframework.data.jpa.repository.JpaRepository; import vn.huynhtoantravel.domain.User; import java.util.Optional;
public interface UserRepository extends JpaRepository<User,Long>{ Optional<User> findByEmailIgnoreCase(String email); }
