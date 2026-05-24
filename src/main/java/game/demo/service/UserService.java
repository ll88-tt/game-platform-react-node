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

    public void activateVip(Long userId, LocalDateTime expiryTime) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setVip(true);
            user.setVipExpiryTime(expiryTime);
            user.setVipStartTime(LocalDateTime.now());
            userRepository.save(user);
        }
    }

    public void updateUserEmail(Long userId, String email) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setEmail(email);
            userRepository.save(user);
        }
    }
}

