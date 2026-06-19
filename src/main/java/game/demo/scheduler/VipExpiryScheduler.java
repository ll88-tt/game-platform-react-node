package game.demo.scheduler;

import game.demo.service.VipExpiryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class VipExpiryScheduler {

    @Autowired
    private VipExpiryService vipExpiryService;

    /**
     * 每小时检查一次，自动降级已到期的 VIP 用户。
     */
    @Scheduled(cron = "0 0 * * * *")
    public void downgradeExpiredVipUsers() {
        vipExpiryService.processExpiredVipUsers();
    }
}
