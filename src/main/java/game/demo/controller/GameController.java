package game.demo.controller;

import game.demo.entity.Game;
import game.demo.entity.User;
import game.demo.repository.GameRepository;
import game.demo.service.GameSearchService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/games")
public class GameController {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GameSearchService gameSearchService;

    @GetMapping
    public ResponseEntity<List<Game>> getAllGames(HttpServletRequest request) {
        User user = (User) request.getSession(false).getAttribute("currentUser");
        List<Game> games = gameRepository.findAll();

        games.forEach(game -> {
            if (game.isVipOnly() && !game.canAccessBy(user)) {
                game.setDescription("🔒 VIP专属游戏 - " + (user == null ? "请先登录" : "需要VIP权限"));
            }
        });


        return ResponseEntity.ok(games);
    }

    // ... existing code ...
    @GetMapping("/search")
    public ResponseEntity<List<Game>> searchGames(@RequestParam String name, HttpServletRequest request) {
        User user = (User) request.getSession(false).getAttribute("currentUser");
        List<Game> games = gameSearchService.searchGames(name);

        games.forEach(game -> {
            if (game.isVipOnly() && !game.canAccessBy(user)) {
                game.setDescription("🔒 VIP专属游戏 - " + (user == null ? "请先登录" : "需要VIP权限"));
            }
        });


        return ResponseEntity.ok(games);
    }

    @GetMapping("/autocomplete")
    public ResponseEntity<List<String>> getAutocompleteSuggestions(@RequestParam String prefix) {
        List<String> suggestions = gameSearchService.getAutocompleteSuggestions(prefix);
        return ResponseEntity.ok(suggestions);
    }

    @GetMapping("/category/{category}")
// ... existing code ...

    public ResponseEntity<List<Game>> getGamesByCategory(@PathVariable String category, HttpServletRequest request) {
        User user = (User) request.getSession(false).getAttribute("currentUser");
        List<Game> games = gameRepository.findByCategory(category);

        games.forEach(game -> {
            if (game.isVipOnly() && !game.canAccessBy(user)) {
                game.setDescription("🔒 VIP专属游戏 - " + (user == null ? "请先登录" : "需要VIP权限"));
            }
        });


        return ResponseEntity.ok(games);
    }

    @PostMapping
    public ResponseEntity<Game> createGame(@RequestBody Game game) {
        Game savedGame = gameRepository.save(game);
        gameSearchService.addGameToIndex(savedGame);
        return ResponseEntity.ok(savedGame);
    }

    @PostMapping("/{gameId}/check-access")
    public ResponseEntity<Map<String, Object>> checkGameAccess(@PathVariable Long gameId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        User user = (User) request.getSession(false).getAttribute("currentUser");
        Game game = gameRepository.findById(gameId).orElse(null);
        if (game == null) {
            response.put("success", false);
            response.put("message", "游戏不存在");
            return ResponseEntity.badRequest().body(response);
        }

        boolean canAccess = game.canAccessBy(user);
        response.put("success", true);
        response.put("canAccess", canAccess);

        if (!canAccess) {
            if (user == null) {
                response.put("message", "该游戏需要登录后访问");
                response.put("needLogin", true);
            } else {
                response.put("message", "该游戏需要VIP权限");
                response.put("needVip", true);
            }
        } else {
            response.put("message", "可以访问");
        }

        return ResponseEntity.ok(response);
    }

}
