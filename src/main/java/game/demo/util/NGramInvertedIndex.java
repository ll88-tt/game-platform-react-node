package game.demo.util;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class NGramInvertedIndex {
    private static final int N = 3;

    private Map<String, Set<Long>> invertedIndex;

    public NGramInvertedIndex() {
        this.invertedIndex = new ConcurrentHashMap<>();
    }

    public void insert(String text, Long gameId) {
        if (text == null || text.isEmpty()) {
            return;
        }

        String lowerText = text.toLowerCase();
        Set<String> ngrams = generateNGrams(lowerText);

        for (String ngram : ngrams) {
            invertedIndex.computeIfAbsent(ngram, k -> ConcurrentHashMap.newKeySet())
                    .add(gameId);
        }
    }

    private Set<String> generateNGrams(String text) {
        Set<String> ngrams = new HashSet<>();

        for (int i = 0; i <= text.length() - N; i++) {
            ngrams.add(text.substring(i, i + N));
        }

        if (text.length() < N && text.length() > 0) {
            ngrams.add(text);
        }

        return ngrams;
    }

    public List<Long> search(String query) {
        if (query == null || query.trim().isEmpty()) {
            return Collections.emptyList();
        }

        String lowerQuery = query.toLowerCase().trim();
        Set<String> queryNgrams = generateNGrams(lowerQuery);

        if (queryNgrams.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, Integer> scoreMap = new HashMap<>();

        for (String ngram : queryNgrams) {
            Set<Long> gameIds = invertedIndex.get(ngram);
            if (gameIds != null) {
                for (Long gameId : gameIds) {
                    scoreMap.merge(gameId, 1, Integer::sum);
                }
            }
        }

        return scoreMap.entrySet().stream()
                .sorted(Map.Entry.<Long, Integer>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .toList();
    }

    public void clear() {
        invertedIndex.clear();
    }
}
