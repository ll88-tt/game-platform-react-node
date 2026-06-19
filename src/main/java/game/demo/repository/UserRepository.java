package game.demo.repository;

import game.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    @Query("SELECT COUNT(u) FROM User u WHERE u.vip = true AND (u.vipExpiryTime IS NULL OR u.vipExpiryTime > :now)")
    long countActiveVip(@Param("now") LocalDateTime now);

    @Query("SELECT u FROM User u WHERE u.vip = true AND u.isAdmin = false " +
            "AND u.vipExpiryTime IS NOT NULL AND u.vipExpiryTime <= :now")
    List<User> findExpiredVipUsers(@Param("now") LocalDateTime now);
}