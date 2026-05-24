package game.demo.repository;

import game.demo.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    List<Feedback> findByIsResolvedOrderByCreateTimeDesc(Boolean isResolved);

    List<Feedback> findByProblemTypeOrderByCreateTimeDesc(String problemType);
}
