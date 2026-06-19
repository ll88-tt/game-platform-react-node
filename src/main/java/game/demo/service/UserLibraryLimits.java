package game.demo.service;

import game.demo.entity.User;

public final class UserLibraryLimits {

    public static final int FREE_MAX_FAVORITES = 100;
    public static final int FREE_MAX_BROWSE_HISTORY = 20;
    public static final int VIP_MAX_FAVORITES = 100;
    public static final int VIP_MAX_BROWSE_HISTORY = 100;
    public static final int ADMIN_MAX_FAVORITES = 500;
    public static final int ADMIN_MAX_BROWSE_HISTORY = 500;

    private UserLibraryLimits() {}

    public static int maxFavorites(User user) {
        if (user.isAdmin()) {
            return ADMIN_MAX_FAVORITES;
        }
        if (user.hasValidPermission()) {
            return VIP_MAX_FAVORITES;
        }
        return FREE_MAX_FAVORITES;
    }

    public static int maxBrowseHistory(User user) {
        if (user.isAdmin()) {
            return ADMIN_MAX_BROWSE_HISTORY;
        }
        if (user.hasValidPermission()) {
            return VIP_MAX_BROWSE_HISTORY;
        }
        return FREE_MAX_BROWSE_HISTORY;
    }

    public static String tierLabel(User user) {
        if (user.isAdmin()) {
            return "ADMIN";
        }
        if (user.isVipLifetime()) {
            return "LIFETIME";
        }
        if (user.hasValidPermission()) {
            return "VIP";
        }
        return "FREE";
    }
}
