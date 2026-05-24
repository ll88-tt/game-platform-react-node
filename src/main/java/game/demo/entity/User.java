package game.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    private String email;

    public User() {}

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Column(nullable = false)
    private String role = "ROLE_USER";

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Column(nullable = false)
    private boolean vip = false;

    @Column
    private LocalDateTime vipExpiryTime;

    @Column
    private LocalDateTime vipStartTime;

    public boolean isVip() {
        return vip;
    }

    public void setVip(boolean vip) {
        this.vip = vip;
    }

    public LocalDateTime getVipExpiryTime() {
        return vipExpiryTime;
    }

    public void setVipExpiryTime(LocalDateTime vipExpiryTime) {
        this.vipExpiryTime = vipExpiryTime;
    }

    public LocalDateTime getVipStartTime() {
        return vipStartTime;
    }

    public void setVipStartTime(LocalDateTime vipStartTime) {
        this.vipStartTime = vipStartTime;
    }

    @Column(nullable = false)
    private boolean isAdmin = false;

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public boolean hasValidPermission() {
        if (!vip) {
            return false;
        }

        if (vipExpiryTime == null) {
            return true;
        }

        return LocalDateTime.now().isBefore(vipExpiryTime);
    }

    public String getPermissionStatus() {
        if (!hasValidPermission()) {
            return "非VIP用户";
        }
        if (vipExpiryTime == null) {
            return "终身VIP";
        }
        return "VIP (至 " + vipExpiryTime.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ")";
    }
}
