package game.demo.util;

import game.demo.entity.User;

import java.util.HashMap;
import java.util.Map;

public final class UserVipResponseHelper {

    private UserVipResponseHelper() {}

    public static void appendVipFields(User user, Map<String, Object> response) {
        boolean vipActive = user.hasValidPermission();
        response.put("vipActive", vipActive);
        response.put("vipExpired", user.isVipExpired());
        response.put("isVipLifetime", user.isVipLifetime());
        response.put("vipDaysRemaining", user.getVipDaysRemaining());
    }

    public static Map<String, Object> buildLoginVipFields(User user) {
        Map<String, Object> fields = new HashMap<>();
        appendVipFields(user, fields);
        return fields;
    }
}
