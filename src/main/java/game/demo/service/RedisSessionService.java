package game.demo.service;

import game.demo.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisSessionService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String SESSION_PREFIX = "session:";
    private static final long SESSION_EXPIRE_TIME = 30; // 会话过期时间（分钟）

    /**
     * 创建用户会话
     */
    public void createSession(String sessionId, User user) {
        String key = SESSION_PREFIX + sessionId;
        redisTemplate.opsForValue().set(key, user, SESSION_EXPIRE_TIME, TimeUnit.MINUTES);
    }

    /**
     * 获取会话中的用户信息
     */
    public User getSessionUser(String sessionId) {
        String key = SESSION_PREFIX + sessionId;
        return (User) redisTemplate.opsForValue().get(key);
    }

    /**
     * 删除会话（登出）
     */
    public void deleteSession(String sessionId) {
        String key = SESSION_PREFIX + sessionId;
        redisTemplate.delete(key);
    }

    /**
     * 刷新会话过期时间
     */
    public void refreshSession(String sessionId) {
        String key = SESSION_PREFIX + sessionId;
        redisTemplate.expire(key, SESSION_EXPIRE_TIME, TimeUnit.MINUTES);
    }

    /**
     * 检查会话是否存在
     */
    public boolean hasSession(String sessionId) {
        String key = SESSION_PREFIX + sessionId;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
}
