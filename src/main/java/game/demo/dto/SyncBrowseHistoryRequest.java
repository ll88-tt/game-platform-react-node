package game.demo.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SyncBrowseHistoryRequest {

    private List<SyncItem> items = new ArrayList<>();

    public List<SyncItem> getItems() {
        return items;
    }

    public void setItems(List<SyncItem> items) {
        this.items = items != null ? items : new ArrayList<>();
    }

    public static class SyncItem {
        private Long gameId;
        private LocalDateTime viewedAt;

        public Long getGameId() {
            return gameId;
        }

        public void setGameId(Long gameId) {
            this.gameId = gameId;
        }

        public LocalDateTime getViewedAt() {
            return viewedAt;
        }

        public void setViewedAt(LocalDateTime viewedAt) {
            this.viewedAt = viewedAt;
        }
    }
}
