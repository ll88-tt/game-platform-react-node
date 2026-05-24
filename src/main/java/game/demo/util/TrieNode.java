package game.demo.util;

import java.util.*;

public class TrieNode {
    private Map<Character, TrieNode> children;
    private List<Long> gameIds;
    private boolean isEndOfWord;
    private String completeWord;

    public TrieNode() {
        this.children = new HashMap<>();
        this.gameIds = new ArrayList<>();
        this.isEndOfWord = false;
        this.completeWord = null;
    }

    public Map<Character, TrieNode> getChildren() {
        return children;
    }

    public List<Long> getGameIds() {
        return gameIds;
    }

    public void addGameId(Long gameId) {
        if (!gameIds.contains(gameId)) {
            gameIds.add(gameId);
        }
    }

    public boolean isEndOfWord() {
        return isEndOfWord;
    }

    public void setEndOfWord(boolean endOfWord) {
        isEndOfWord = endOfWord;
    }

    public String getCompleteWord() {
        return completeWord;
    }

    public void setCompleteWord(String completeWord) {
        this.completeWord = completeWord;
    }
}
