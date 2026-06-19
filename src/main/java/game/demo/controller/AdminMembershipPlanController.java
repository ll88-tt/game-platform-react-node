package game.demo.controller;

import game.demo.entity.MembershipPlan;
import game.demo.entity.User;
import game.demo.service.MembershipPlanService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/admin/membership-plans")
public class AdminMembershipPlanController {

    private static final Set<String> VALID_PLAN_CODES = Set.of(
            "monthly", "quarterly", "yearly", "lifetime"
    );

    @Autowired
    private MembershipPlanService membershipPlanService;

    private boolean isAdmin(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }
        User user = (User) session.getAttribute("currentUser");
        return user != null && user.isAdmin();
    }

    private ResponseEntity<Map<String, Object>> forbiddenResponse() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", "无权操作：仅管理员可执行此操作");
        return ResponseEntity.status(403).body(response);
    }

    @GetMapping
    public ResponseEntity<?> getAllPlans(HttpServletRequest request) {
        if (!isAdmin(request)) {
            return forbiddenResponse();
        }
        return ResponseEntity.ok(membershipPlanService.getAllPlanViews());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPlan(@PathVariable Long id, HttpServletRequest request) {
        if (!isAdmin(request)) {
            return forbiddenResponse();
        }

        return membershipPlanService.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", false);
                    response.put("message", "会员套餐不存在");
                    return ResponseEntity.badRequest().body(response);
                });
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createPlan(
            @RequestBody MembershipPlan plan,
            HttpServletRequest request) {

        if (!isAdmin(request)) {
            return forbiddenResponse();
        }

        Map<String, Object> response = new HashMap<>();
        String validationError = validatePlan(plan, null);
        if (validationError != null) {
            response.put("success", false);
            response.put("message", validationError);
            return ResponseEntity.badRequest().body(response);
        }

        if (membershipPlanService.existsByPlanCode(plan.getPlanCode().trim())) {
            response.put("success", false);
            response.put("message", "套餐代码已存在");
            return ResponseEntity.badRequest().body(response);
        }

        plan.setId(null);
        applyPlanFields(plan, plan);
        MembershipPlan savedPlan = membershipPlanService.save(plan);

        response.put("success", true);
        response.put("message", "会员套餐创建成功");
        response.put("plan", savedPlan);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updatePlan(
            @PathVariable Long id,
            @RequestBody MembershipPlan plan,
            HttpServletRequest request) {

        if (!isAdmin(request)) {
            return forbiddenResponse();
        }

        Map<String, Object> response = new HashMap<>();
        String validationError = validatePlan(plan, id);
        if (validationError != null) {
            response.put("success", false);
            response.put("message", validationError);
            return ResponseEntity.badRequest().body(response);
        }

        MembershipPlan existing = membershipPlanService.findById(id).orElse(null);
        if (existing == null) {
            response.put("success", false);
            response.put("message", "会员套餐不存在");
            return ResponseEntity.badRequest().body(response);
        }

        if (!existing.getPlanCode().equals(plan.getPlanCode().trim())) {
            response.put("success", false);
            response.put("message", "套餐代码不可修改");
            return ResponseEntity.badRequest().body(response);
        }

        applyPlanFields(existing, plan);
        MembershipPlan updatedPlan = membershipPlanService.save(existing);

        response.put("success", true);
        response.put("message", "会员套餐更新成功");
        response.put("plan", updatedPlan);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/enabled")
    public ResponseEntity<Map<String, Object>> toggleEnabled(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> body,
            HttpServletRequest request) {

        if (!isAdmin(request)) {
            return forbiddenResponse();
        }

        Map<String, Object> response = new HashMap<>();
        MembershipPlan existing = membershipPlanService.findById(id).orElse(null);
        if (existing == null) {
            response.put("success", false);
            response.put("message", "会员套餐不存在");
            return ResponseEntity.badRequest().body(response);
        }

        Boolean enabled = body.get("enabled");
        if (enabled == null) {
            response.put("success", false);
            response.put("message", "缺少 enabled 参数");
            return ResponseEntity.badRequest().body(response);
        }

        existing.setEnabled(enabled);
        MembershipPlan updatedPlan = membershipPlanService.save(existing);

        response.put("success", true);
        response.put("message", enabled ? "套餐已启用" : "套餐已停用");
        response.put("plan", updatedPlan);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deletePlan(
            @PathVariable Long id,
            HttpServletRequest request) {

        if (!isAdmin(request)) {
            return forbiddenResponse();
        }

        Map<String, Object> response = new HashMap<>();
        if (!membershipPlanService.existsById(id)) {
            response.put("success", false);
            response.put("message", "会员套餐不存在");
            return ResponseEntity.badRequest().body(response);
        }

        membershipPlanService.deleteById(id);

        response.put("success", true);
        response.put("message", "会员套餐已删除");
        return ResponseEntity.ok(response);
    }

    private void applyPlanFields(MembershipPlan target, MembershipPlan source) {
        target.setPlanCode(source.getPlanCode().trim());
        target.setName(source.getName().trim());
        target.setPrice(source.getPrice());
        target.setOriginalPrice(source.getOriginalPrice());
        target.setPeriodLabel(trimToNull(source.getPeriodLabel()));
        target.setSavingsText(trimToNull(source.getSavingsText()));
        target.setRecommended(source.isRecommended());
        target.setEnabled(source.isEnabled());
        target.setSortOrder(source.getSortOrder());
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String validatePlan(MembershipPlan plan, Long excludeId) {
        if (plan.getPlanCode() == null || plan.getPlanCode().trim().isEmpty()) {
            return "套餐代码不能为空";
        }
        if (!VALID_PLAN_CODES.contains(plan.getPlanCode().trim())) {
            return "无效的套餐代码，仅支持 monthly、quarterly、yearly、lifetime";
        }
        if (plan.getName() == null || plan.getName().trim().isEmpty()) {
            return "套餐名称不能为空";
        }
        if (plan.getPrice() == null || plan.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            return "售价必须大于 0";
        }
        if (plan.getOriginalPrice() != null && plan.getOriginalPrice().compareTo(BigDecimal.ZERO) <= 0) {
            return "原价必须大于 0";
        }
        return null;
    }
}
