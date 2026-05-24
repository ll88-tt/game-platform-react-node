package game.demo.controller;

import game.demo.entity.Feedback;
import game.demo.entity.User;
import game.demo.repository.FeedbackRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {

    @Autowired
    private FeedbackRepository feedbackRepository;

    private boolean isAdmin(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }
        User user = (User) session.getAttribute("currentUser");
        return user != null && user.isAdmin();
    }

    private ResponseEntity<Map<String, Object>> forbiddenResponse() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", "无权操作：仅管理员可执行此操作");
        return ResponseEntity.status(403).body(response);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> submitFeedback(@RequestBody Feedback feedback) {
        Map<String, Object> response = new HashMap<>();

        try {
            feedback.setCreateTime(LocalDateTime.now());
            feedback.setIsResolved(false);

            Feedback savedFeedback = feedbackRepository.save(feedback);

            response.put("success", true);
            response.put("message", "反馈提交成功");
            response.put("id", savedFeedback.getId());

            System.out.println("收到新的反馈 #" + savedFeedback.getId());
            System.out.println("类型: " + savedFeedback.getProblemType());
            System.out.println("描述: " + savedFeedback.getProblemDesc());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "提交失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping
    public ResponseEntity<List<Feedback>> getAllFeedbacks(HttpServletRequest request) {
        if (!isAdmin(request)) {
            return ResponseEntity.status(403).build();
        }
        List<Feedback> feedbacks = feedbackRepository.findAll();
        return ResponseEntity.ok(feedbacks);
    }

    @GetMapping("/unresolved")
    public ResponseEntity<List<Feedback>> getUnresolvedFeedbacks(HttpServletRequest request) {
        if (!isAdmin(request)) {
            return ResponseEntity.status(403).build();
        }
        List<Feedback> feedbacks = feedbackRepository.findByIsResolvedOrderByCreateTimeDesc(false);
        return ResponseEntity.ok(feedbacks);
    }

    @PutMapping("/{id}/resolve")
    public ResponseEntity<Map<String, Object>> markAsResolved(@PathVariable Long id, HttpServletRequest request) {
        if (!isAdmin(request)) {
            return forbiddenResponse().getBody() != null ?
                    ResponseEntity.status(403).body(forbiddenResponse().getBody()) :
                    ResponseEntity.status(403).build();
        }

        Map<String, Object> response = new HashMap<>();

        try {
            Feedback feedback = feedbackRepository.findById(id).orElse(null);
            if (feedback == null) {
                response.put("success", false);
                response.put("message", "反馈不存在");
                return ResponseEntity.badRequest().body(response);
            }

            feedback.setIsResolved(true);
            feedbackRepository.save(feedback);

            response.put("success", true);
            response.put("message", "已标记为已解决");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "操作失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteFeedback(@PathVariable Long id, HttpServletRequest request) {
        if (!isAdmin(request)) {
            return forbiddenResponse().getBody() != null ?
                    ResponseEntity.status(403).body(forbiddenResponse().getBody()) :
                    ResponseEntity.status(403).build();
        }

        Map<String, Object> response = new HashMap<>();

        try {
            feedbackRepository.deleteById(id);
            response.put("success", true);
            response.put("message", "反馈已删除");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "删除失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}
