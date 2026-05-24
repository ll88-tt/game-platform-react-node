package game.demo;

import game.demo.entity.Game;
import game.demo.entity.User;
import game.demo.repository.GameRepository;
import game.demo.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@SpringBootTest
class GameApplicationTests {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void contextLoads() {
    }

    @Test
    void initTestData() {
        initUsers();
        initGameData();
    }

    private void initUsers() {
        // 免费用户
        if (userRepository.findByUsername("test").isEmpty()) {
            User user = new User();
            user.setUsername("test");
            user.setPassword(passwordEncoder.encode("123456"));
            user.setRole("ROLE_USER");
            user.setVip(false);
            userRepository.save(user);
            System.out.println("✓ 免费用户已创建：用户名 test，密码 123456");
        }

        // VIP用户（月度）
        if (userRepository.findByUsername("vip").isEmpty()) {
            User user = new User();
            user.setUsername("vip");
            user.setPassword(passwordEncoder.encode("123456"));
            user.setRole("ROLE_USER");
            user.setVip(true);
            user.setVipStartTime(LocalDateTime.now());
            user.setVipExpiryTime(LocalDateTime.now().plusMonths(1));
            userRepository.save(user);
            System.out.println("✓ VIP用户已创建：用户名 vip，密码 123456，有效期至 " +
                    user.getVipExpiryTime().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        }

        // 管理员用户
        if (userRepository.findByUsername("admin").isEmpty()) {
            User user = new User();
            user.setUsername("admin");
            user.setPassword(passwordEncoder.encode("123456"));
            user.setRole("ROLE_ADMIN");
            user.setAdmin(true);
            user.setVip(true);
            user.setVipStartTime(LocalDateTime.now());
            user.setVipExpiryTime(null);
            userRepository.save(user);
            System.out.println("✓ 管理员已创建：用户名 admin，密码 123456，永久VIP，管理员权限");
        }


        System.out.println("\n=== 测试账号汇总 ===");
        System.out.println("免费用户: test / 123456");
        System.out.println("VIP用户:  vip / 123456");
        System.out.println("管理员:  admin / 123456");
        System.out.println("==================\n");
    }
// ... existing code ...


    private void initGameData() {
        if (gameRepository.count() == 0) {
            System.out.println("开始初始化游戏数据...");

            // 免费游戏
            gameRepository.save(new Game("俄罗斯方块", "https://example.com/game/tetris", "经典消除游戏", "https://picsum.photos/seed/tetris/400/250", "休闲", false));
            gameRepository.save(new Game("贪吃蛇", "https://example.com/game/snake", "经典街机游戏", "https://picsum.photos/seed/snake/400/250", "休闲", false));
            gameRepository.save(new Game("扫雷", "https://example.com/game/minesweeper", "益智解谜游戏", "https://picsum.photos/seed/minesweeper/400/250", "解谜", false));
            gameRepository.save(new Game("纸牌接龙", "https://example.com/game/solitaire", "经典纸牌游戏", "https://picsum.photos/seed/solitaire/400/250", "卡牌", false));
            gameRepository.save(new Game("五子棋", "https://example.com/game/gobang", "双人对战棋类游戏", "https://picsum.photos/seed/gobang/400/250", "策略", false));

            // VIP游戏
            gameRepository.save(new Game("超级马里奥", "https://example.com/game/mario", "经典横版闯关", "https://picsum.photos/seed/mario/400/250", "冒险", true));
            gameRepository.save(new Game("塞尔达传说", "https://example.com/game/zelda", "开放世界冒险RPG", "https://picsum.photos/seed/zelda/400/250", "RPG", true));
            gameRepository.save(new Game("极品飞车", "https://example.com/game/nfs", "赛车竞速游戏", "https://picsum.photos/seed/nfs/400/250", "竞速", true));
            gameRepository.save(new Game("使命召唤", "https://example.com/game/cod", "第一人称射击", "https://picsum.photos/seed/cod/400/250", "射击", true));
            gameRepository.save(new Game("文明6", "https://example.com/game/civ6", "回合制策略游戏", "https://picsum.photos/seed/civ6/400/250", "策略", true));
            gameRepository.save(new Game( "巫师3", "https://example.com/game/witcher3", "史诗级角色扮演", "https://picsum.photos/seed/witcher3/400/250", "RPG", true));
            gameRepository.save(new Game("鬼泣5", "https://example.com/game/dmc5", "动作格斗游戏", "https://picsum.photos/seed/dmc5/400/250", "格斗", true));

            System.out.println("✓ 游戏数据初始化完成！");
            System.out.println("  免费游戏: 5个");
            System.out.println("  VIP游戏: 7个");
            System.out.println("  总计: 12个\n");
        } else {
            System.out.println("数据库中已存在游戏数据，跳过初始化。\n");
        }
    }
}
