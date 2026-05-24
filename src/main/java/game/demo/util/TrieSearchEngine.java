package game.demo.util;

import java.util.*;

public class TrieSearchEngine {
    private TrieNode root;

    public TrieSearchEngine() {
        this.root = new TrieNode();
    }

    public void insert(String word, Long gameId) {
        if (word == null || word.isEmpty()) {
            return;
        }

        String lowerWord = word.toLowerCase();
        TrieNode node = root;

        for (char c : lowerWord.toCharArray()) {
            node.getChildren().putIfAbsent(c, new TrieNode());
            node = node.getChildren().get(c);
            node.addGameId(gameId);
        }

        node.setEndOfWord(true);
        node.setCompleteWord(lowerWord);
    }

    public List<Long> search(String prefix) {
        if (prefix == null || prefix.isEmpty()) {
            return Collections.emptyList();
        }

        String lowerPrefix = prefix.toLowerCase();
        TrieNode node = root;

        for (char c : lowerPrefix.toCharArray()) {
            if (!node.getChildren().containsKey(c)) {
                return Collections.emptyList();
            }
            node = node.getChildren().get(c);
        }

        return new ArrayList<>(node.getGameIds());
    }

    public List<String> getAutocompleteSuggestions(String prefix, int limit) {
        if (prefix == null || prefix.isEmpty()) {
            return Collections.emptyList();
        }

        String lowerPrefix = prefix.toLowerCase();
        TrieNode node = root;

        for (char c : lowerPrefix.toCharArray()) {
            if (!node.getChildren().containsKey(c)) {
                return Collections.emptyList();
            }
            node = node.getChildren().get(c);
        }

        List<String> suggestions = new ArrayList<>();
        collectWords(node, suggestions);

        return suggestions.stream()
                .distinct()
                .limit(limit)
                .toList();
    }

    private void collectWords(TrieNode node, List<String> words) {
        if (node.isEndOfWord() && node.getCompleteWord() != null) {
            words.add(node.getCompleteWord());
        }

        for (TrieNode child : node.getChildren().values()) {
            collectWords(child, words);
        }
    }

    public void clear() {
        root = new TrieNode();
    }
}
