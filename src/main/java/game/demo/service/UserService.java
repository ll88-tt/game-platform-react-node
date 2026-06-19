package game.demo.service;

import game.demo.entity.User;
import game.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User registerUser(String username, String rawPassword) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole("ROLE_USER");
        user.setVip(false);
        return userRepository.save(user);
    }

    public boolean authenticate(String username, String rawPassword) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty()) {
            return false;
        }
        User user = optionalUser.get();
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }

    public User getUserByUsername(String username) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        return optionalUser.orElse(null);
    }

    // ... existing code ...

    public LocalDateTime activateVip(Long userId, String planCode) {
        return userRepository.findById(userId)
                .map(user -> {
                    LocalDateTime expiryTime = calculateExpiryTime(user, planCode);
                    if (user.getVipStartTime() == null) {
                        user.setVipStartTime(LocalDateTime.now());
                    }
                    user.setVip(true);
                    user.setVipExpiryTime(expiryTime);
                    userRepository.save(user);
                    return expiryTime;
                })
                .orElse(null);
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    private LocalDateTime calculateExpiryTime(User user, String planCode) {
        if ("lifetime".equals(planCode)) {
            return null;
        }

        LocalDateTime base = LocalDateTime.now();
        if (user.isVip() && user.getVipExpiryTime() != null && user.getVipExpiryTime().isAfter(base)) {
            base = user.getVipExpiryTime();
        }

        return switch (planCode) {
            case "monthly" -> base.plusMonths(1);
            case "quarterly" -> base.plusMonths(3);
            case "yearly" -> base.plusYears(1);
            default -> throw new IllegalArgumentException("无效的订阅方案");
        };
    }

    // 保留旧方法签名供兼容
    public void activateVip(Long userId, LocalDateTime expiryTime) {
        userRepository.findById(userId).ifPresent(user -> {
            if (user.getVipStartTime() == null) {
                user.setVipStartTime(LocalDateTime.now());
            }
            user.setVip(true);
            user.setVipExpiryTime(expiryTime);
            userRepository.save(user);
        });
    }

    public void updateUserEmail(Long userId, String email) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setEmail(email);
            userRepository.save(user);
        }
    }

    public boolean changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return false;
        }
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return false;
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return true;
    }
}

