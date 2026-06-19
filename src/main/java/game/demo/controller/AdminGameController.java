package game.demo.controller;

import game.demo.entity.Game;
import game.demo.entity.User;
import game.demo.repository.GameRepository;
import game.demo.service.GameSearchService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/games")
public class AdminGameController {

    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of(
            "image/jpeg", "image/png", "image/webp", "image/gif"
    );
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GameSearchService gameSearchService;

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
    public ResponseEntity<?> getAllGames(HttpServletRequest request) {
        if (!isAdmin(request)) {
            return forbiddenResponse();
        }
        return ResponseEntity.ok(gameRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getGame(@PathVariable Long id, HttpServletRequest request) {
        if (!isAdmin(request)) {
            return forbiddenResponse();
        }

        return gameRepository.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", false);
                    response.put("message", "游戏不存在");
                    return ResponseEntity.badRequest().body(response);
                });
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createGame(@RequestBody Game game, HttpServletRequest request) {
        if (!isAdmin(request)) {
            return forbiddenResponse();
        }

        Map<String, Object> response = new HashMap<>();
        String validationError = validateGame(game);
        if (validationError != null) {
            response.put("success", false);
            response.put("message", validationError);
            return ResponseEntity.badRequest().body(response);
        }

        game.setId(null);
        if (game.getSearchCount() == null) {
            game.setSearchCount(0L);
        }

        Game savedGame = gameRepository.save(game);
        gameSearchService.rebuildIndexes();

        response.put("success", true);
        response.put("message", "游戏创建成功");
        response.put("game", savedGame);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateGame(
            @PathVariable Long id,
            @RequestBody Game game,
            HttpServletRequest request) {

        if (!isAdmin(request)) {
            return forbiddenResponse();
        }

        Map<String, Object> response = new HashMap<>();
        String validationError = validateGame(game);
        if (validationError != null) {
            response.put("success", false);
            response.put("message", validationError);
            return ResponseEntity.badRequest().body(response);
        }

        Game existing = gameRepository.findById(id).orElse(null);
        if (existing == null) {
            response.put("success", false);
            response.put("message", "游戏不存在");
            return ResponseEntity.badRequest().body(response);
        }

        existing.setName(game.getName().trim());
        existing.setLink(game.getLink().trim());
        existing.setDescription(game.getDescription());
        existing.setImageUrl(game.getImageUrl());
        existing.setCategory(game.getCategory());
        existing.setVipOnly(game.isVipOnly());
        existing.setPublished(game.isPublished());

        Game updatedGame = gameRepository.save(existing);
        gameSearchService.rebuildIndexes();

        response.put("success", true);
        response.put("message", "游戏更新成功");
        response.put("game", updatedGame);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/publish")
    public ResponseEntity<Map<String, Object>> togglePublish(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> body,
            HttpServletRequest request) {

        if (!isAdmin(request)) {
            return forbiddenResponse();
        }

        Map<String, Object> response = new HashMap<>();
        Game existing = gameRepository.findById(id).orElse(null);
        if (existing == null) {
            response.put("success", false);
            response.put("message", "游戏不存在");
            return ResponseEntity.badRequest().body(response);
        }

        Boolean published = body.get("published");
        if (published == null) {
            response.put("success", false);
            response.put("message", "缺少 published 参数");
            return ResponseEntity.badRequest().body(response);
        }

        existing.setPublished(published);
        Game updatedGame = gameRepository.save(existing);
        gameSearchService.rebuildIndexes();

        response.put("success", true);
        response.put("message", published ? "游戏已上架" : "游戏已下架");
        response.put("game", updatedGame);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/vip")
    public ResponseEntity<Map<String, Object>> toggleVip(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> body,
            HttpServletRequest request) {

        if (!isAdmin(request)) {
            return forbiddenResponse();
        }

        Map<String, Object> response = new HashMap<>();
        Game existing = gameRepository.findById(id).orElse(null);
        if (existing == null) {
            response.put("success", false);
            response.put("message", "游戏不存在");
            return ResponseEntity.badRequest().body(response);
        }

        Boolean vipOnly = body.get("vipOnly");
        if (vipOnly == null) {
            response.put("success", false);
            response.put("message", "缺少 vipOnly 参数");
            return ResponseEntity.badRequest().body(response);
        }

        existing.setVipOnly(vipOnly);
        Game updatedGame = gameRepository.save(existing);

        response.put("success", true);
        response.put("message", vipOnly ? "已设为 VIP 专属" : "已设为免费游戏");
        response.put("game", updatedGame);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteGame(@PathVariable Long id, HttpServletRequest request) {
        if (!isAdmin(request)) {
            return forbiddenResponse();
        }

        Map<String, Object> response = new HashMap<>();

        if (!gameRepository.existsById(id)) {
            response.put("success", false);
            response.put("message", "游戏不存在");
            return ResponseEntity.badRequest().body(response);
        }

        gameRepository.deleteById(id);
        gameSearchService.rebuildIndexes();

        response.put("success", true);
        response.put("message", "游戏已删除");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/upload-cover")
    public ResponseEntity<Map<String, Object>> uploadCover(
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request) throws IOException {

        if (!isAdmin(request)) {
            return forbiddenResponse();
        }

        Map<String, Object> response = new HashMap<>();

        if (file == null || file.isEmpty()) {
            response.put("success", false);
            response.put("message", "请选择要上传的图片");
            return ResponseEntity.badRequest().body(response);
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            response.put("success", false);
            response.put("message", "图片大小不能超过 5MB");
            return ResponseEntity.badRequest().body(response);
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType)) {
            response.put("success", false);
            response.put("message", "仅支持 JPG、PNG、WEBP、GIF 格式");
            return ResponseEntity.badRequest().body(response);
        }

        String extension = switch (contentType) {
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            case "image/gif" -> ".gif";
            default -> ".jpg";
        };

        Path uploadDir = Paths.get("uploads", "games").toAbsolutePath().normalize();
        Files.createDirectories(uploadDir);

        String filename = UUID.randomUUID() + extension;
        Path targetPath = uploadDir.resolve(filename);
        Files.copy(file.getInputStream(), targetPath);

        String url = "/uploads/games/" + filename;
        response.put("success", true);
        response.put("message", "封面上传成功");
        response.put("url", url);
        return ResponseEntity.ok(response);
    }

    private String validateGame(Game game) {
        if (game.getName() == null || game.getName().trim().isEmpty()) {
            return "游戏名称不能为空";
        }
        if (game.getLink() == null || game.getLink().trim().isEmpty()) {
            return "游戏链接不能为空";
        }
        return null;
    }
}
