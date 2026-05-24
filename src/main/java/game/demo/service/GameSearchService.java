package game.demo.service;

import game.demo.entity.Game;
import game.demo.repository.GameRepository;
import game.demo.util.NGramInvertedIndex;
import game.demo.util.TrieSearchEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class GameSearchService {

    @Autowired
    private GameRepository gameRepository;

    private volatile TrieSearchEngine trieEngine;
    private volatile NGramInvertedIndex ngramIndex;

    private TrieSearchEngine buildingTrieEngine;
    private NGramInvertedIndex buildingNgramIndex;

    private final Object rebuildLock = new Object();

    private static final long REBUILD_INTERVAL_MINUTES = 30;

    @PostConstruct
    public void init() {
        rebuildIndexes();
        startPeriodicRebuild();
        System.out.println("游戏搜索服务已启动，重建间隔: " + REBUILD_INTERVAL_MINUTES + " 分钟");
    }

    private void startPeriodicRebuild() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread thread = new Thread(r, "SearchIndexRebuilder");
            thread.setDaemon(true);
            return thread;
        });

        scheduler.scheduleAtFixedRate(() -> {
            try {
                System.out.println("开始定期重建搜索索引...");
                rebuildIndexes();
                System.out.println("定期重建搜索索引完成");
            } catch (Exception e) {
                System.err.println("重建搜索索引失败: " + e.getMessage());
                e.printStackTrace();
            }
        }, REBUILD_INTERVAL_MINUTES, REBUILD_INTERVAL_MINUTES, TimeUnit.MINUTES);
    }

    public void rebuildIndexes() {
        synchronized (rebuildLock) {
            buildingTrieEngine = new TrieSearchEngine();
            buildingNgramIndex = new NGramInvertedIndex();

            List<Game> allGames = gameRepository.findAll();

            for (Game game : allGames) {
                if (game.getName() != null && !game.getName().isEmpty()) {
                    buildingTrieEngine.insert(game.getName(), game.getId());
                    buildingNgramIndex.insert(game.getName(), game.getId());

                    if (game.getDescription() != null && !game.getDescription().isEmpty()) {
                        buildingNgramIndex.insert(game.getDescription(), game.getId());
                    }

                    if (game.getCategory() != null && !game.getCategory().isEmpty()) {
                        buildingNgramIndex.insert(game.getCategory(), game.getId());
                    }
                }
            }

            trieEngine = buildingTrieEngine;
            ngramIndex = buildingNgramIndex;

            buildingTrieEngine = null;
            buildingNgramIndex = null;

            System.out.println("搜索索引重建完成，共索引 " + allGames.size() + " 个游戏");
        }
    }

    public List<Game> searchGames(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return gameRepository.findAll();
        }

        String trimmedKeyword = keyword.trim();

        List<Long> trieResults = trieEngine.search(trimmedKeyword);
        List<Long> ngramResults = ngramIndex.search(trimmedKeyword);

        Set<Long> combinedResults = new LinkedHashSet<>();
        combinedResults.addAll(trieResults);
        combinedResults.addAll(ngramResults);

        if (combinedResults.isEmpty()) {
            return List.of();
        }

        List<Game> games = gameRepository.findAllById(combinedResults);

        games.forEach(game -> game.incrementSearchCount());
        gameRepository.saveAll(games);

        return games;
    }

    public List<String> getAutocompleteSuggestions(String prefix) {
        if (prefix == null || prefix.trim().isEmpty()) {
            return Collections.emptyList();
        }

        return trieEngine.getAutocompleteSuggestions(prefix.trim(), 5);
    }

    public void addGameToIndex(Game game) {
        synchronized (rebuildLock) {
            if (game.getName() != null && !game.getName().isEmpty()) {
                trieEngine.insert(game.getName(), game.getId());
                ngramIndex.insert(game.getName(), game.getId());

                if (game.getDescription() != null && !game.getDescription().isEmpty()) {
                    ngramIndex.insert(game.getDescription(), game.getId());
                }

                if (game.getCategory() != null && !game.getCategory().isEmpty()) {
                    ngramIndex.insert(game.getCategory(), game.getId());
                }
            }
        }
    }

    public void removeGameFromIndex(Long gameId) {
        rebuildIndexes();
    }
}
