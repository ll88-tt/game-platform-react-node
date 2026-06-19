package game.demo.controller;

import game.demo.dto.ChangePasswordRequest;
import game.demo.dto.SyncBrowseHistoryRequest;
import game.demo.entity.User;
import game.demo.service.UserLibraryService;
import game.demo.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserLibraryService userLibraryService;

    @PutMapping("/password")
    public ResponseEntity<Map<String, Object>> changePassword(
            @RequestBody ChangePasswordRequest request,
            HttpServletRequest httpRequest) {

        Map<String, Object> response = new HashMap<>();
        User user = getSessionUser(httpRequest);
        if (user == null) {
            response.put("success", false);
            response.put("message", "请先登录");
            return ResponseEntity.status(401).body(response);
        }

        if (request.getOldPassword() == null || request.getOldPassword().isEmpty()) {
            response.put("success", false);
            response.put("message", "请输入当前密码");
            return ResponseEntity.badRequest().body(response);
        }

        if (request.getNewPassword() == null || request.getNewPassword().length() < 6) {
            response.put("success", false);
            response.put("message", "新密码长度不能少于6位");
            return ResponseEntity.badRequest().body(response);
        }

        if (request.getOldPassword().equals(request.getNewPassword())) {
            response.put("success", false);
            response.put("message", "新密码不能与当前密码相同");
            return ResponseEntity.badRequest().body(response);
        }

        boolean changed = userService.changePassword(user.getId(), request.getOldPassword(), request.getNewPassword());
        if (!changed) {
            response.put("success", false);
            response.put("message", "当前密码错误");
            return ResponseEntity.badRequest().body(response);
        }

        response.put("success", true);
        response.put("message", "密码修改成功");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/library-limits")
    public ResponseEntity<?> getLibraryLimits(HttpServletRequest httpRequest) {
        User user = getSessionUser(httpRequest);
        if (user == null) {
            return unauthorized();
        }
        return ResponseEntity.ok(userLibraryService.getLibraryLimits(user));
    }

    @GetMapping("/favorites")
    public ResponseEntity<?> getFavorites(HttpServletRequest httpRequest) {
        User user = getSessionUser(httpRequest);
        if (user == null) {
            return unauthorized();
        }
        return ResponseEntity.ok(userLibraryService.getFavorites(user));
    }

    @GetMapping("/favorites/ids")
    public ResponseEntity<?> getFavoriteIds(HttpServletRequest httpRequest) {
        User user = getSessionUser(httpRequest);
        if (user == null) {
            return unauthorized();
        }
        return ResponseEntity.ok(userLibraryService.getFavoriteIds(user));
    }

    @PostMapping("/favorites/{gameId}")
    public ResponseEntity<Map<String, Object>> addFavorite(
            @PathVariable Long gameId,
            HttpServletRequest httpRequest) {

        Map<String, Object> response = new HashMap<>();
        User user = getSessionUser(httpRequest);
        if (user == null) {
            response.put("success", false);
            response.put("message", "请先登录");
            return ResponseEntity.status(401).body(response);
        }

        try {
            userLibraryService.addFavorite(user, gameId);
            response.put("success", true);
            response.put("message", "收藏成功");
            response.put("favorited", true);
            response.putAll(userLibraryService.getLibraryLimits(user));
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            response.put("success", false);
            response.put("message", ex.getMessage());
            response.putAll(userLibraryService.getLibraryLimits(user));
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/favorites/{gameId}")
    public ResponseEntity<Map<String, Object>> removeFavorite(
            @PathVariable Long gameId,
            HttpServletRequest httpRequest) {

        Map<String, Object> response = new HashMap<>();
        User user = getSessionUser(httpRequest);
        if (user == null) {
            response.put("success", false);
            response.put("message", "请先登录");
            return ResponseEntity.status(401).body(response);
        }

        userLibraryService.removeFavorite(user, gameId);

        response.put("success", true);
        response.put("message", "已取消收藏");
        response.put("favorited", false);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/browse-history")
    public ResponseEntity<?> getBrowseHistory(HttpServletRequest httpRequest) {
        User user = getSessionUser(httpRequest);
        if (user == null) {
            return unauthorized();
        }
        return ResponseEntity.ok(userLibraryService.getBrowseHistory(user));
    }

    @PostMapping("/browse-history/{gameId}")
    public ResponseEntity<Map<String, Object>> recordBrowse(
            @PathVariable Long gameId,
            HttpServletRequest httpRequest) {

        Map<String, Object> response = new HashMap<>();
        User user = getSessionUser(httpRequest);
        if (user == null) {
            response.put("success", false);
            response.put("message", "请先登录");
            return ResponseEntity.status(401).body(response);
        }

        try {
            response.put("success", true);
            response.put("item", userLibraryService.recordBrowse(user, gameId));
            response.putAll(userLibraryService.getLibraryLimits(user));
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            response.put("success", false);
            response.put("message", ex.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/browse-history/sync")
    public ResponseEntity<Map<String, Object>> syncBrowseHistory(
            @RequestBody SyncBrowseHistoryRequest request,
            HttpServletRequest httpRequest) {

        Map<String, Object> response = new HashMap<>();
        User user = getSessionUser(httpRequest);
        if (user == null) {
            response.put("success", false);
            response.put("message", "请先登录");
            return ResponseEntity.status(401).body(response);
        }

        int synced = userLibraryService.syncBrowseHistory(user, request);
        response.put("success", true);
        response.put("synced", synced);
        response.put("items", userLibraryService.getBrowseHistory(user));
        response.putAll(userLibraryService.getLibraryLimits(user));
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/browse-history/{gameId}")
    public ResponseEntity<Map<String, Object>> removeBrowseHistory(
            @PathVariable Long gameId,
            HttpServletRequest httpRequest) {

        Map<String, Object> response = new HashMap<>();
        User user = getSessionUser(httpRequest);
        if (user == null) {
            response.put("success", false);
            response.put("message", "请先登录");
            return ResponseEntity.status(401).body(response);
        }

        userLibraryService.removeBrowseHistory(user, gameId);
        response.put("success", true);
        response.put("message", "已删除浏览记录");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/browse-history")
    public ResponseEntity<Map<String, Object>> clearBrowseHistory(HttpServletRequest httpRequest) {
        Map<String, Object> response = new HashMap<>();
        User user = getSessionUser(httpRequest);
        if (user == null) {
            response.put("success", false);
            response.put("message", "请先登录");
            return ResponseEntity.status(401).body(response);
        }

        userLibraryService.clearBrowseHistory(user);
        response.put("success", true);
        response.put("message", "浏览记录已清空");
        return ResponseEntity.ok(response);
    }

    private User getSessionUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        User sessionUser = (User) session.getAttribute("currentUser");
        if (sessionUser == null) {
            return null;
        }
        return userService.getUserById(sessionUser.getId());
    }

    private ResponseEntity<Map<String, Object>> unauthorized() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", "请先登录");
        return ResponseEntity.status(401).body(response);
    }
}
