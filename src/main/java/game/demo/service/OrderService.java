package game.demo.service;

import game.demo.entity.MembershipPlan;
import game.demo.entity.Order;
import game.demo.entity.User;
import game.demo.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    public Order createPendingOrder(Long userId, MembershipPlan plan) {
        Order order = new Order();
        order.setUserId(userId);
        order.setPlanCode(plan.getPlanCode());
        order.setPlanName(plan.getName());
        order.setAmount(plan.getPrice());
        order.setStatus("PENDING");
        order.setCreatedAt(LocalDateTime.now());
        return orderRepository.save(order);
    }

    public Order findOrderForUser(Long orderId, Long userId) {
        return orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new IllegalArgumentException("订单不存在"));
    }

    public Order findPendingOrderForUser(Long orderId, Long userId) {
        Order order = findOrderForUser(orderId, userId);
        if (!"PENDING".equals(order.getStatus())) {
            throw new IllegalStateException("订单已处理，请勿重复支付");
        }
        return order;
    }

    public Order markAsPaid(Order order, LocalDateTime vipExpiryAfter) {
        order.setStatus("PAID");
        order.setPaidAt(LocalDateTime.now());
        order.setVipExpiryAfter(vipExpiryAfter);
        return orderRepository.save(order);
    }

    public List<Order> getUserOrders(Long userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
}
