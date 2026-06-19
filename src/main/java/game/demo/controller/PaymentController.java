package game.demo.controller;

import game.demo.entity.Order;
import game.demo.entity.User;
import game.demo.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createPayment(
            @RequestBody Map<String, String> body,
            HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();
        User user = getSessionUser(request);
        if (user == null) {
            response.put("success", false);
            response.put("message", "请先登录");
            return ResponseEntity.status(401).body(response);
        }

        String planCode = body.get("plan");
        if (planCode == null || planCode.isBlank()) {
            response.put("success", false);
            response.put("message", "缺少套餐参数");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            Order order = paymentService.createPendingOrder(user.getId(), planCode.trim());
            response.put("success", true);
            response.put("message", "订单创建成功，请完成支付");
            response.put("orderId", order.getId());
            response.put("planCode", order.getPlanCode());
            response.put("planName", order.getPlanName());
            response.put("amount", order.getAmount());
            response.put("status", order.getStatus());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "创建订单失败：" + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/confirm")
    public ResponseEntity<Map<String, Object>> confirmPayment(
            @RequestBody Map<String, Object> body,
            HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();
        User user = getSessionUser(request);
        if (user == null) {
            response.put("success", false);
            response.put("message", "请先登录");
            return ResponseEntity.status(401).body(response);
        }

        Object orderIdObj = body.get("orderId");
        if (orderIdObj == null) {
            response.put("success", false);
            response.put("message", "缺少订单号");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            Long orderId = Long.valueOf(orderIdObj.toString());
            HttpSession session = request.getSession(false);
            PaymentService.PaymentResult result = paymentService.confirmPayment(orderId, user.getId(), session);

            response.put("success", true);
            response.put("message", "支付成功！VIP 已激活");
            response.put("orderId", result.order().getId());
            response.put("status", result.order().getStatus());
            response.put("vipExpiryTime", result.vipExpiryTime() != null
                    ? result.vipExpiryTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    : "永久");
            response.put("vipStatus", result.user().getPermissionStatus());
            response.put("isVip", result.user().hasValidPermission());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException | IllegalStateException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "支付失败：" + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    private User getSessionUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        return (User) session.getAttribute("currentUser");
    }
}
