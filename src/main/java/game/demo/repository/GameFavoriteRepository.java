package game.demo.repository;

import game.demo.entity.GameFavorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GameFavoriteRepository extends JpaRepository<GameFavorite, Long> {

    List<GameFavorite> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<GameFavorite> findByUserIdAndGameId(Long userId, Long gameId);

    boolean existsByUserIdAndGameId(Long userId, Long gameId);

    void deleteByUserIdAndGameId(Long userId, Long gameId);
}
