package game.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "games")
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String link;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String imageUrl;

    private String category;

    // ... existing code ...
    @Column(nullable = false)
    private boolean vipOnly = false;

    @Column(nullable = false)
    private boolean published = true;

    @Column(nullable = false)
    private Long searchCount = 0L;

    public Game() {}
// ... existing code ...


    public Game(String name, String link, String description, String imageUrl) {
        this.name = name;
        this.link = link;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    public Game(String name, String link, String description, String imageUrl, String category) {
        this.name = name;
        this.link = link;
        this.description = description;
        this.imageUrl = imageUrl;
        this.category = category;
    }

    public Game(String name, String link, String description, String imageUrl, String category, boolean vipOnly) {
        this.name = name;
        this.link = link;
        this.description = description;
        this.imageUrl = imageUrl;
        this.category = category;
        this.vipOnly = vipOnly;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    // ... existing code ...
    public void setVipOnly(boolean vipOnly) {
        this.vipOnly = vipOnly;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public Long getSearchCount() {
        return searchCount;
    }

    public void setSearchCount(Long searchCount) {
        this.searchCount = searchCount;
    }

    public void incrementSearchCount() {
        this.searchCount++;
    }

    public boolean canAccessBy(User user) {
// ... existing code ...

        if (!vipOnly) {
            return true;
        }

        if (user == null) {
            return false;
        }

        return user.hasValidPermission();
    }
}
