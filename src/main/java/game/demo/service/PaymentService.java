package game.demo.service;

import game.demo.entity.MembershipPlan;
import game.demo.entity.Order;
import game.demo.entity.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class PaymentService {

    @Autowired
    private MembershipPlanService membershipPlanService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    public Order createPendingOrder(Long userId, String planCode) {
        MembershipPlan plan = membershipPlanService.findByPlanCode(planCode)
                .filter(MembershipPlan::isEnabled)
                .orElseThrow(() -> new IllegalArgumentException("无效的订阅方案"));
        return orderService.createPendingOrder(userId, plan);
    }

    public PaymentResult confirmPayment(Long orderId, Long userId, HttpSession session) {
        Order order = orderService.findPendingOrderForUser(orderId, userId);

        MembershipPlan plan = membershipPlanService.findByPlanCode(order.getPlanCode())
                .filter(MembershipPlan::isEnabled)
                .orElseThrow(() -> new IllegalStateException("套餐已下架，无法完成支付"));

        if (order.getAmount().compareTo(plan.getPrice()) != 0) {
            throw new IllegalStateException("套餐价格已变更，请重新下单");
        }

        // 模拟支付网关异步回调：确认收款后开通 VIP
        LocalDateTime expiryTime = userService.activateVip(userId, order.getPlanCode());
        Order paidOrder = orderService.markAsPaid(order, expiryTime);

        User updatedUser = userService.getUserById(userId);
        if (session != null) {
            session.setAttribute("currentUser", updatedUser);
        }

        return new PaymentResult(paidOrder, updatedUser, expiryTime);
    }

    public record PaymentResult(Order order, User user, LocalDateTime vipExpiryTime) {}
}
