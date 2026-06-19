package game.demo.repository;

import game.demo.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    List<Feedback> findByIsResolvedOrderByCreateTimeDesc(Boolean isResolved);

    List<Feedback> findByProblemTypeOrderByCreateTimeDesc(String problemType);

    @Query("SELECT COUNT(f) FROM Feedback f WHERE f.isResolved IS NULL OR f.isResolved = false")
    long countPending();
}
