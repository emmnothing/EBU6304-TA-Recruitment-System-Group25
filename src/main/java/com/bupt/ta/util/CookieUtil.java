package com.bupt.ta.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public final class CookieUtil {
    private CookieUtil() {
    }

    public static String getRememberMeToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if (AppConstants.REMEMBER_ME_COOKIE_NAME.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    public static void writeRememberMeCookie(HttpServletRequest request, HttpServletResponse response, String token, int maxAgeSeconds) {
        response.addHeader(
            "Set-Cookie",
            buildRememberMeCookie(request, token, maxAgeSeconds)
        );
    }

    public static void clearRememberMeCookie(HttpServletRequest request, HttpServletResponse response) {
        response.addHeader(
            "Set-Cookie",
            buildRememberMeCookie(request, "", 0)
        );
    }

    private static String buildRememberMeCookie(HttpServletRequest request, String value, int maxAgeSeconds) {
        StringBuilder cookie = new StringBuilder();
        cookie.append(AppConstants.REMEMBER_ME_COOKIE_NAME).append("=").append(value == null ? "" : value);
        cookie.append("; Max-Age=").append(maxAgeSeconds);
        cookie.append("; Path=").append(resolveCookiePath(request));
        cookie.append("; HttpOnly");
        cookie.append("; SameSite=Lax");
        if (request.isSecure()) {
            // 只有 HTTPS 请求才加 Secure，避免本地 HTTP 开发环境无法回传 cookie。
            cookie.append("; Secure");
        }
        return cookie.toString();
    }

    private static String resolveCookiePath(HttpServletRequest request) {
        String contextPath = request.getContextPath();
        return ValidationUtil.isBlank(contextPath) ? "/" : contextPath;
    }
}
