package game.demo.repository;

import game.demo.entity.GameBrowseHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GameBrowseHistoryRepository extends JpaRepository<GameBrowseHistory, Long> {

    List<GameBrowseHistory> findByUserIdOrderByViewedAtDesc(Long userId);

    Optional<GameBrowseHistory> findByUserIdAndGameId(Long userId, Long gameId);

    long countByUserId(Long userId);

    void deleteByUserIdAndGameId(Long userId, Long gameId);

    void deleteByUserId(Long userId);
}
