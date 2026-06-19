package game.demo.dto;

import game.demo.entity.Game;

import java.time.LocalDateTime;

public class BrowseHistoryView {

    private Long historyId;
    private Long gameId;
    private String name;
    private String link;
    private String description;
    private String imageUrl;
    private String category;
    private boolean vipOnly;
    private boolean published;
    private boolean canAccess;
    private LocalDateTime viewedAt;

    public static BrowseHistoryView from(Game game, Long historyId, LocalDateTime viewedAt, boolean canAccess) {
        BrowseHistoryView view = new BrowseHistoryView();
        view.setHistoryId(historyId);
        view.setGameId(game.getId());
        view.setName(game.getName());
        view.setLink(game.getLink());
        view.setDescription(game.getDescription());
        view.setImageUrl(game.getImageUrl());
        view.setCategory(game.getCategory());
        view.setVipOnly(game.isVipOnly());
        view.setPublished(game.isPublished());
        view.setCanAccess(canAccess);
        view.setViewedAt(viewedAt);
        return view;
    }

    public Long getHistoryId() {
        return historyId;
    }

    public void setHistoryId(Long historyId) {
        this.historyId = historyId;
    }

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isVipOnly() {
        return vipOnly;
    }

    public void setVipOnly(boolean vipOnly) {
        this.vipOnly = vipOnly;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public boolean isCanAccess() {
        return canAccess;
    }

    public void setCanAccess(boolean canAccess) {
        this.canAccess = canAccess;
    }

    public LocalDateTime getViewedAt() {
        return viewedAt;
    }

    public void setViewedAt(LocalDateTime viewedAt) {
        this.viewedAt = viewedAt;
    }
}
