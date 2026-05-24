package game.demo.controller;

import game.demo.dto.LoginRequest;
import game.demo.entity.User;
import game.demo.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class LoginController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        Map<String, Object> response = new HashMap<>();
        String username = request.getUsername();
        String password = request.getPassword();

        log.info("尝试登录 - 用户名: {}", username);

        User user = userService.getUserByUsername(username);
        if (user == null) {
            log.warn("登录失败 - 用户不存在: {}", username);
            response.put("success", false);
            response.put("message", "用户名或密码错误");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        log.info("用户存在，检查密码 - 用户名: {}", username);
        boolean isAuthenticated = userService.authenticate(username, password);

        if (isAuthenticated) {
            HttpSession session = httpRequest.getSession(true);
            session.setAttribute("currentUser", user);
            session.setMaxInactiveInterval(1800);

            response.put("success", true);
            response.put("message", "登录成功");
            response.put("username", user.getUsername());

            boolean isVip = user.isVip() || user.isAdmin();
            response.put("isVip", isVip);
            response.put("isAdmin", user.isAdmin());

            response.put("vipExpiryTime", user.getVipExpiryTime() != null
                    ? user.getVipExpiryTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    : null);
            response.put("vipStatus", user.getPermissionStatus());
            log.info("登录成功 - 用户名: {}, VIP状态: {}, SessionId: {}", username, user.getPermissionStatus(), session.getId());
            return ResponseEntity.ok(response);
        } else {
            log.warn("登录失败 - 密码错误，用户名: {}", username);
            response.put("success", false);
            response.put("message", "用户名或密码错误");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    @GetMapping("/check-login")
    public ResponseEntity<Map<String, Object>> checkLogin(HttpServletRequest httpRequest) {
        Map<String, Object> response = new HashMap<>();
        HttpSession session = httpRequest.getSession(false);

        if (session == null) {
            log.debug("检查登录状态 - 用户未登录（无会话）");
            response.put("loggedIn", false);
            return ResponseEntity.ok(response);
        }

        User user = (User) session.getAttribute("currentUser");
        if (user != null) {
            session.setMaxInactiveInterval(1800);
            log.debug("检查登录状态 - 用户已登录: {}", user.getUsername());
            response.put("loggedIn", true);
            response.put("username", user.getUsername());
            response.put("isAdmin", user.isAdmin());

            boolean isVip = user.isVip() || user.isAdmin();
            response.put("isVip", isVip);
            response.put("vipExpiryTime", user.getVipExpiryTime() != null
                    ? user.getVipExpiryTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    : null);
            response.put("vipStatus", user.getPermissionStatus());
        } else {
            log.debug("检查登录状态 - 用户未登录（会话中无用户）");
            response.put("loggedIn", false);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(HttpServletRequest httpRequest) {
        HttpSession session = httpRequest.getSession(false);
        if (session != null) {
            User user = (User) session.getAttribute("currentUser");
            if (user != null) {
                log.info("用户登出: {}", user.getUsername());
            }
            session.invalidate();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "已登出");
        return ResponseEntity.ok(response);
    }
}
