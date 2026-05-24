package game.demo.controller;

import game.demo.entity.User;
import game.demo.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class PurchaseController {

    @Autowired
    private UserService userService;

    @PostMapping("/purchase")
    public ResponseEntity<Map<String, Object>> purchase(
            @RequestBody Map<String, Object> purchaseRequest,
            HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();

        User user = (User) request.getAttribute("currentUser");
        if (user == null) {
            response.put("success", false);
            response.put("message", "请先登录");
            return ResponseEntity.status(401).body(response);
        }

        String plan = (String) purchaseRequest.get("plan");
        Double amount = Double.valueOf(purchaseRequest.get("amount").toString());

        try {
            LocalDateTime expiryTime;

            switch (plan) {
                case "monthly":
                    expiryTime = LocalDateTime.now().plusMonths(1);
                    break;
                case "quarterly":
                    expiryTime = LocalDateTime.now().plusMonths(3);
                    break;
                case "yearly":
                    expiryTime = LocalDateTime.now().plusYears(1);
                    break;
                case "lifetime":
                    expiryTime = null;
                    break;
                default:
                    response.put("success", false);
                    response.put("message", "无效的订阅方案");
                    return ResponseEntity.badRequest().body(response);
            }

            userService.activateVip(user.getId(), expiryTime);

            response.put("success", true);
            response.put("message", "购买成功！VIP已激活");
            response.put("vipExpiryTime", expiryTime != null ? expiryTime.toString() : "永久");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "购买失败：" + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}
