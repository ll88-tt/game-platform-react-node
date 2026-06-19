package game.demo.repository;

import game.demo.entity.MembershipPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MembershipPlanRepository extends JpaRepository<MembershipPlan, Long> {

    Optional<MembershipPlan> findByPlanCode(String planCode);

    boolean existsByPlanCode(String planCode);

    List<MembershipPlan> findAllByOrderBySortOrderAsc();

    List<MembershipPlan> findAllByEnabledTrueOrderBySortOrderAsc();
}
