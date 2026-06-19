package game.demo.service;

import game.demo.entity.User;
import game.demo.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class VipExpiryService {

    private static final Logger log = LoggerFactory.getLogger(VipExpiryService.class);

    @Autowired
    private UserRepository userRepository;

    /**
     * 将已过期的 VIP 用户降级为普通用户（管理员与终身 VIP 不受影响）。
     */
    @Transactional
    public int processExpiredVipUsers() {
        LocalDateTime now = LocalDateTime.now();
        List<User> expiredUsers = userRepository.findExpiredVipUsers(now);

        if (expiredUsers.isEmpty()) {
            return 0;
        }

        for (User user : expiredUsers) {
            user.setVip(false);
        }
        userRepository.saveAll(expiredUsers);

        log.info("VIP 到期自动降级完成，共处理 {} 个用户", expiredUsers.size());
        return expiredUsers.size();
    }
}
