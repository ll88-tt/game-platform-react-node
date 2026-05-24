package game.demo.repository;

import game.demo.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {

    List<Game> findByNameContainingIgnoreCase(String name);

    List<Game> findByCategory(String category);

    @Query("SELECT g FROM Game g ORDER BY g.searchCount DESC")
    List<Game> findTopBySearchCountDesc();
}
