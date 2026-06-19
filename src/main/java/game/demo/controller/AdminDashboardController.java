package game.demo.controller;

import game.demo.entity.User;
import game.demo.service.AdminDashboardService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/dashboard")
public class AdminDashboardController {

    @Autowired
    private AdminDashboardService adminDashboardService;

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

    @GetMapping("/stats")
    public ResponseEntity<?> getStats(HttpServletRequest request) {
        if (!isAdmin(request)) {
            return forbiddenResponse();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("stats", adminDashboardService.getStats());
        return ResponseEntity.ok(response);
    }
}
