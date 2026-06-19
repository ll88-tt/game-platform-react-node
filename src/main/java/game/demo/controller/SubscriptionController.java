package game.demo.controller;

import game.demo.entity.Order;
import game.demo.entity.User;
import game.demo.repository.OrderRepository;
import game.demo.service.OrderService;
import game.demo.service.UserService;
import game.demo.service.VipExpiryService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class SubscriptionController {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private VipExpiryService vipExpiryService;

    @GetMapping("/subscription")
    public ResponseEntity<Map<String, Object>> getSubscription(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        User sessionUser = getSessionUser(request);
        if (sessionUser == null) {
            response.put("success", false);
            response.put("message", "请先登录");
            return ResponseEntity.status(401).body(response);
        }

        vipExpiryService.processExpiredVipUsers();
        User user = userService.getUserById(sessionUser.getId());
        if (user == null) {
            response.put("success", false);
            response.put("message", "用户不存在");
            return ResponseEntity.badRequest().body(response);
        }

        boolean activeVip = user.hasValidPermission();
        String latestPlanCode = orderRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .findFirst()
                .map(Order::getPlanCode)
                .orElse(null);

        response.put("success", true);
        response.put("username", user.getUsername());
        response.put("isAdmin", user.isAdmin());
        response.put("isVip", user.isVip() || user.isAdmin());
        response.put("vipActive", activeVip);
        response.put("vipStatus", user.getPermissionStatus());
        response.put("vipStartTime", formatDateTime(user.getVipStartTime()));
        response.put("vipExpiryTime", user.getVipExpiryTime() == null
                ? (activeVip ? "永久" : null)
                : user.getVipExpiryTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        response.put("daysRemaining", calculateDaysRemaining(user));
        response.put("latestPlanCode", latestPlanCode);
        response.put("orderCount", orderRepository.findByUserIdOrderByCreatedAtDesc(user.getId()).size());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/orders")
    public ResponseEntity<?> getOrders(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        User sessionUser = getSessionUser(request);
        if (sessionUser == null) {
            response.put("success", false);
            response.put("message", "请先登录");
            return ResponseEntity.status(401).body(response);
        }

        List<Order> orders = orderService.getUserOrders(sessionUser.getId());
        return ResponseEntity.ok(orders);
    }

    private User getSessionUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        return (User) session.getAttribute("currentUser");
    }

    private String formatDateTime(LocalDateTime time) {
        if (time == null) {
            return null;
        }
        return time.format(DATE_FORMAT);
    }

    private Long calculateDaysRemaining(User user) {
        if (!user.hasValidPermission()) {
            return 0L;
        }
        if (user.getVipExpiryTime() == null) {
            return null;
        }
        return ChronoUnit.DAYS.between(LocalDateTime.now(), user.getVipExpiryTime());
    }
}
