package game.demo.service;

import game.demo.dto.MembershipPlanView;
import game.demo.entity.MembershipPlan;
import game.demo.repository.MembershipPlanRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Service
public class MembershipPlanService {

    @Autowired
    private MembershipPlanRepository membershipPlanRepository;

    @PostConstruct
    public void initDefaultPlans() {
        if (membershipPlanRepository.count() > 0) {
            return;
        }

        membershipPlanRepository.save(createPlan("monthly", "月度会员", new BigDecimal("29.00"),
                null, "/月", null, false, 1));
        membershipPlanRepository.save(createPlan("quarterly", "季度会员", new BigDecimal("79.00"),
                null, "/季", "省 ¥8（相当于 ¥26/月）", true, 2));
        membershipPlanRepository.save(createPlan("yearly", "年度会员", new BigDecimal("299.00"),
                null, "/年", "省 ¥49（相当于 ¥25/月）", false, 3));
        membershipPlanRepository.save(createPlan("lifetime", "终身会员", new BigDecimal("999.00"),
                null, "一次性", "永久有效，无限畅玩", false, 4));
    }

    private MembershipPlan createPlan(String planCode, String name, BigDecimal price,
                                      BigDecimal originalPrice, String periodLabel,
                                      String savingsText, boolean recommended, int sortOrder) {
        MembershipPlan plan = new MembershipPlan();
        plan.setPlanCode(planCode);
        plan.setName(name);
        plan.setPrice(price);
        plan.setOriginalPrice(originalPrice);
        plan.setPeriodLabel(periodLabel);
        plan.setSavingsText(savingsText);
        plan.setRecommended(recommended);
        plan.setEnabled(true);
        plan.setSortOrder(sortOrder);
        return plan;
    }

    public List<MembershipPlan> getAllPlans() {
        return membershipPlanRepository.findAllByOrderBySortOrderAsc();
    }

    public List<MembershipPlan> getEnabledPlans() {
        return membershipPlanRepository.findAllByEnabledTrueOrderBySortOrderAsc();
    }

    public List<MembershipPlanView> getAllPlanViews() {
        BigDecimal monthlyPrice = findByPlanCode("monthly")
                .map(MembershipPlan::getPrice)
                .orElse(null);
        return getAllPlans().stream()
                .map(plan -> toView(plan, monthlyPrice))
                .toList();
    }

    public List<MembershipPlanView> getEnabledPlanViews() {
        BigDecimal monthlyPrice = findByPlanCode("monthly")
                .map(MembershipPlan::getPrice)
                .orElse(null);
        return getEnabledPlans().stream()
                .map(plan -> toView(plan, monthlyPrice))
                .toList();
    }

    private MembershipPlanView toView(MembershipPlan plan, BigDecimal monthlyPrice) {
        MembershipPlanView view = new MembershipPlanView();
        view.setId(plan.getId());
        view.setPlanCode(plan.getPlanCode());
        view.setName(plan.getName());
        view.setPrice(plan.getPrice());
        view.setOriginalPrice(plan.getOriginalPrice());
        view.setPeriodLabel(plan.getPeriodLabel());
        view.setSavingsText(plan.getSavingsText());
        view.setRecommended(plan.isRecommended());
        view.setEnabled(plan.isEnabled());
        view.setSortOrder(plan.getSortOrder());

        BigDecimal equivalentMonthly = calculateEquivalentMonthly(plan);
        view.setEquivalentMonthlyPrice(equivalentMonthly);
        view.setComputedSavingsText(buildSavingsText(plan, monthlyPrice, equivalentMonthly));
        return view;
    }

    private BigDecimal calculateEquivalentMonthly(MembershipPlan plan) {
        return switch (plan.getPlanCode()) {
            case "monthly" -> plan.getPrice();
            case "quarterly" -> plan.getPrice().divide(BigDecimal.valueOf(3), 2, RoundingMode.HALF_UP);
            case "yearly" -> plan.getPrice().divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP);
            case "lifetime" -> plan.getPrice().divide(BigDecimal.valueOf(36), 2, RoundingMode.HALF_UP);
            default -> null;
        };
    }

    private String buildSavingsText(MembershipPlan plan, BigDecimal monthlyPrice, BigDecimal equivalentMonthly) {
        if ("lifetime".equals(plan.getPlanCode())) {
            return "永久有效，相当于 ¥" + formatPrice(equivalentMonthly) + "/月（按3年计算）";
        }
        if ("monthly".equals(plan.getPlanCode()) || monthlyPrice == null || equivalentMonthly == null) {
            return plan.getSavingsText();
        }

        int months = switch (plan.getPlanCode()) {
            case "quarterly" -> 3;
            case "yearly" -> 12;
            default -> 0;
        };
        if (months == 0) {
            return plan.getSavingsText();
        }

        BigDecimal fullPrice = monthlyPrice.multiply(BigDecimal.valueOf(months));
        BigDecimal saved = fullPrice.subtract(plan.getPrice());
        if (saved.compareTo(BigDecimal.ZERO) <= 0) {
            return "相当于 ¥" + formatPrice(equivalentMonthly) + "/月";
        }
        return "省 ¥" + formatPrice(saved) + "（相当于 ¥" + formatPrice(equivalentMonthly) + "/月）";
    }

    private String formatPrice(BigDecimal value) {
        if (value == null) {
            return "0";
        }
        BigDecimal rounded = value.setScale(2, RoundingMode.HALF_UP).stripTrailingZeros();
        return rounded.toPlainString();
    }

    public Optional<MembershipPlan> findById(Long id) {
        return membershipPlanRepository.findById(id);
    }

    public Optional<MembershipPlan> findByPlanCode(String planCode) {
        return membershipPlanRepository.findByPlanCode(planCode);
    }

    public MembershipPlan save(MembershipPlan plan) {
        return membershipPlanRepository.save(plan);
    }

    public void deleteById(Long id) {
        membershipPlanRepository.deleteById(id);
    }

    public boolean existsById(Long id) {
        return membershipPlanRepository.existsById(id);
    }

    public boolean existsByPlanCode(String planCode) {
        return membershipPlanRepository.existsByPlanCode(planCode);
    }
}
