package game.demo.controller;

import game.demo.dto.RegisterRequest;
import game.demo.entity.User;
import game.demo.service.RedisSessionService;
import game.demo.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class RegisterController {

    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private RedisSessionService redisSessionService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody RegisterRequest request) {
        Map<String, Object> response = new HashMap<>();
        String username = request.getUsername();
        String password = request.getPassword();
        String email = request.getEmail();

        log.info("尝试注册 - 用户名: {}", username);

        if (username == null || username.trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "用户名不能为空");
            return ResponseEntity.badRequest().body(response);
        }

        if (password == null || password.length() < 6) {
            response.put("success", false);
            response.put("message", "密码长度至少为6位");
            return ResponseEntity.badRequest().body(response);
        }

        User existingUser = userService.getUserByUsername(username);
        if (existingUser != null) {
            log.warn("注册失败 - 用户名已存在: {}", username);
            response.put("success", false);
            response.put("message", "用户名已存在");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }

        try {
            User newUser = userService.registerUser(username, password);
            if (email != null && !email.isEmpty()) {
                newUser.setEmail(email);
                userService.updateUserEmail(newUser.getId(), email);
            }

            String sessionId = UUID.randomUUID().toString();
            redisSessionService.createSession(sessionId, newUser);

            response.put("success", true);
            response.put("message", "注册成功");
            response.put("username", newUser.getUsername());
            response.put("sessionId", sessionId);
            response.put("isVip", newUser.isVip());
            response.put("permissionLevel", "FREE");

            log.info("注册成功 - 用户名: {}, SessionId: {}", username, sessionId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("注册异常 - 用户名: {}, 错误: {}", username, e.getMessage());
            response.put("success", false);
            response.put("message", "注册失败，请稍后重试");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
