package game.demo.service;

import game.demo.repository.FeedbackRepository;
import game.demo.repository.OrderRepository;
import game.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class AdminDashboardService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private FeedbackRepository feedbackRepository;

    public Map<String, Object> getStats() {
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();

        Map<String, Object> stats = new HashMap<>();
        stats.put("userCount", userRepository.count());
        stats.put("vipCount", userRepository.countActiveVip(LocalDateTime.now()));
        stats.put("todayOrderCount", orderRepository.countByStatusAndCreatedAtGreaterThanEqual("PAID", todayStart));
        stats.put("pendingFeedbackCount", feedbackRepository.countPending());
        return stats;
    }
}
