package com.bupt.ta.util;

public final class AppConstants {
    public static final String SESSION_CURRENT_USER_ID = "currentUserId";
    public static final String SESSION_CURRENT_USERNAME = "currentUsername";
    public static final String SESSION_CURRENT_USER_ROLE = "currentUserRole";
    public static final String SESSION_FLASH_MESSAGE = "flashMessage";
    public static final String SESSION_FLASH_TYPE = "flashType";

    public static final String REMEMBER_ME_COOKIE_NAME = "ta_remember_me";
    public static final int REMEMBER_ME_MAX_AGE_SECONDS = 7 * 24 * 60 * 60;
    public static final int ADMIN_REMEMBER_ME_MAX_AGE_SECONDS = 24 * 60 * 60;

    public static final String FLASH_SUCCESS = "success";
    public static final String FLASH_ERROR = "error";

    private AppConstants() {
    }
}
