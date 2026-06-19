package game.demo.repository;

import game.demo.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {

    List<Game> findByNameContainingIgnoreCase(String name);

    List<Game> findByCategory(String category);

    List<Game> findByPublishedTrue();

    List<Game> findByCategoryAndPublishedTrue(String category);

    @Query("SELECT g FROM Game g WHERE g.published = true AND (" +
            "LOWER(g.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(g.description) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(g.category) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "ORDER BY g.searchCount DESC")
    List<Game> searchByKeyword(@Param("keyword") String keyword);

    List<Game> findTop10ByPublishedTrueOrderBySearchCountDesc();
}
