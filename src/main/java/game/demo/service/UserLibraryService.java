package game.demo.service;

import game.demo.dto.BrowseHistoryView;
import game.demo.dto.FavoriteGameView;
import game.demo.dto.SyncBrowseHistoryRequest;
import game.demo.entity.Game;
import game.demo.entity.GameBrowseHistory;
import game.demo.entity.GameFavorite;
import game.demo.entity.User;
import game.demo.repository.GameBrowseHistoryRepository;
import game.demo.repository.GameFavoriteRepository;
import game.demo.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserLibraryService {

    @Autowired
    private GameFavoriteRepository gameFavoriteRepository;

    @Autowired
    private GameBrowseHistoryRepository gameBrowseHistoryRepository;

    @Autowired
    private GameRepository gameRepository;

    public Map<String, Object> getLibraryLimits(User user) {
        Map<String, Object> limits = new HashMap<>();
        limits.put("tier", UserLibraryLimits.tierLabel(user));
        limits.put("maxFavorites", UserLibraryLimits.maxFavorites(user));
        limits.put("maxBrowseHistory", UserLibraryLimits.maxBrowseHistory(user));
        limits.put("favoriteCount", gameFavoriteRepository.findByUserIdOrderByCreatedAtDesc(user.getId()).size());
        limits.put("browseHistoryCount", gameBrowseHistoryRepository.countByUserId(user.getId()));
        return limits;
    }

    public List<FavoriteGameView> getFavorites(User user) {
        return gameFavoriteRepository.findByUserIdOrderByCreatedAtDesc(user.getId()).stream()
                .map(favorite -> gameRepository.findById(favorite.getGameId())
                        .filter(Game::isPublished)
                        .map(game -> FavoriteGameView.from(
                                game,
                                favorite.getId(),
                                favorite.getCreatedAt(),
                                game.canAccessBy(user)))
                        .orElse(null))
                .filter(view -> view != null)
                .collect(Collectors.toList());
    }

    @Transactional
    public FavoriteGameView addFavorite(User user, Long gameId) {
        Game game = gameRepository.findById(gameId)
                .filter(Game::isPublished)
                .orElseThrow(() -> new IllegalArgumentException("游戏不存在或已下架"));

        if (gameFavoriteRepository.existsByUserIdAndGameId(user.getId(), gameId)) {
            return gameFavoriteRepository.findByUserIdAndGameId(user.getId(), gameId)
                    .flatMap(favorite -> gameRepository.findById(gameId)
                            .map(g -> FavoriteGameView.from(g, favorite.getId(), favorite.getCreatedAt(), g.canAccessBy(user))))
                    .orElseThrow(() -> new IllegalStateException("收藏记录异常"));
        }

        int maxFavorites = UserLibraryLimits.maxFavorites(user);
        long currentCount = gameFavoriteRepository.findByUserIdOrderByCreatedAtDesc(user.getId()).size();
        if (currentCount >= maxFavorites) {
            throw new IllegalStateException("收藏数量已达上限（" + maxFavorites + " 款）");
        }

        GameFavorite favorite = new GameFavorite(user.getId(), gameId);
        gameFavoriteRepository.save(favorite);
        return FavoriteGameView.from(game, favorite.getId(), favorite.getCreatedAt(), game.canAccessBy(user));
    }

    public void removeFavorite(User user, Long gameId) {
        gameFavoriteRepository.deleteByUserIdAndGameId(user.getId(), gameId);
    }

    public List<Long> getFavoriteIds(User user) {
        return gameFavoriteRepository.findByUserIdOrderByCreatedAtDesc(user.getId()).stream()
                .map(GameFavorite::getGameId)
                .collect(Collectors.toList());
    }

    public List<BrowseHistoryView> getBrowseHistory(User user) {
        return gameBrowseHistoryRepository.findByUserIdOrderByViewedAtDesc(user.getId()).stream()
                .map(history -> gameRepository.findById(history.getGameId())
                        .filter(Game::isPublished)
                        .map(game -> BrowseHistoryView.from(
                                game,
                                history.getId(),
                                history.getViewedAt(),
                                game.canAccessBy(user)))
                        .orElse(null))
                .filter(view -> view != null)
                .collect(Collectors.toList());
    }

    @Transactional
    public BrowseHistoryView recordBrowse(User user, Long gameId) {
        Game game = gameRepository.findById(gameId)
                .filter(Game::isPublished)
                .orElseThrow(() -> new IllegalArgumentException("游戏不存在或已下架"));

        LocalDateTime now = LocalDateTime.now();
        GameBrowseHistory history = gameBrowseHistoryRepository.findByUserIdAndGameId(user.getId(), gameId)
                .orElse(null);

        if (history != null) {
            history.setViewedAt(now);
            gameBrowseHistoryRepository.save(history);
        } else {
            ensureBrowseHistoryCapacity(user);
            history = new GameBrowseHistory(user.getId(), gameId);
            history.setViewedAt(now);
            gameBrowseHistoryRepository.save(history);
        }

        return BrowseHistoryView.from(game, history.getId(), history.getViewedAt(), game.canAccessBy(user));
    }

    @Transactional
    public int syncBrowseHistory(User user, SyncBrowseHistoryRequest request) {
        if (request.getItems() == null || request.getItems().isEmpty()) {
            return 0;
        }

        List<SyncBrowseHistoryRequest.SyncItem> sortedItems = request.getItems().stream()
                .filter(item -> item.getGameId() != null)
                .sorted(Comparator.comparing(
                        item -> item.getViewedAt() != null ? item.getViewedAt() : LocalDateTime.MIN))
                .collect(Collectors.toList());

        int synced = 0;
        for (SyncBrowseHistoryRequest.SyncItem item : sortedItems) {
            Game game = gameRepository.findById(item.getGameId())
                    .filter(Game::isPublished)
                    .orElse(null);
            if (game == null) {
                continue;
            }

            LocalDateTime viewedAt = item.getViewedAt() != null ? item.getViewedAt() : LocalDateTime.now();
            GameBrowseHistory history = gameBrowseHistoryRepository.findByUserIdAndGameId(user.getId(), item.getGameId())
                    .orElse(null);

            if (history != null) {
                if (history.getViewedAt().isBefore(viewedAt)) {
                    history.setViewedAt(viewedAt);
                    gameBrowseHistoryRepository.save(history);
                }
            } else {
                ensureBrowseHistoryCapacity(user);
                history = new GameBrowseHistory(user.getId(), item.getGameId());
                history.setViewedAt(viewedAt);
                gameBrowseHistoryRepository.save(history);
            }
            synced++;
        }

        trimBrowseHistory(user);
        return synced;
    }

    public void removeBrowseHistory(User user, Long gameId) {
        gameBrowseHistoryRepository.deleteByUserIdAndGameId(user.getId(), gameId);
    }

    public void clearBrowseHistory(User user) {
        gameBrowseHistoryRepository.deleteByUserId(user.getId());
    }

    private void ensureBrowseHistoryCapacity(User user) {
        int maxHistory = UserLibraryLimits.maxBrowseHistory(user);
        List<GameBrowseHistory> histories = gameBrowseHistoryRepository.findByUserIdOrderByViewedAtDesc(user.getId());
        if (histories.size() >= maxHistory) {
            GameBrowseHistory oldest = histories.get(histories.size() - 1);
            gameBrowseHistoryRepository.delete(oldest);
        }
    }

    private void trimBrowseHistory(User user) {
        int maxHistory = UserLibraryLimits.maxBrowseHistory(user);
        List<GameBrowseHistory> histories = gameBrowseHistoryRepository.findByUserIdOrderByViewedAtDesc(user.getId());
        if (histories.size() <= maxHistory) {
            return;
        }

        List<GameBrowseHistory> toRemove = histories.subList(maxHistory, histories.size());
        gameBrowseHistoryRepository.deleteAll(toRemove);
    }
}
