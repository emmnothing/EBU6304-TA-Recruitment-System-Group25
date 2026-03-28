package com.bupt.ta.util;

import com.bupt.ta.model.Role;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

public final class SessionUtil {
    private SessionUtil() {
    }

    public static boolean requireLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute(AppConstants.SESSION_CURRENT_USER_ID) == null) {
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return false;
        }
        return true;
    }

    public static boolean requireRole(HttpServletRequest request, HttpServletResponse response, Role role) throws IOException {
        if (!requireLogin(request, response)) {
            return false;
        }
        Role currentRole = getCurrentUserRole(request);
        if (currentRole != role) {
            response.sendRedirect(request.getContextPath() + RouteUtil.getDashboardPath(currentRole));
            return false;
        }
        return true;
    }

    public static void createSession(HttpServletRequest request, String userId, String username, Role role) {
        HttpSession session = request.getSession(true);
        session.setAttribute(AppConstants.SESSION_CURRENT_USER_ID, userId);
        session.setAttribute(AppConstants.SESSION_CURRENT_USERNAME, username);
        session.setAttribute(AppConstants.SESSION_CURRENT_USER_ROLE, role.name());
    }

    public static void clearSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }

    public static String getCurrentUserId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session == null ? null : (String) session.getAttribute(AppConstants.SESSION_CURRENT_USER_ID);
    }

    public static String getCurrentUsername(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session == null ? null : (String) session.getAttribute(AppConstants.SESSION_CURRENT_USERNAME);
    }

    public static Role getCurrentUserRole(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String roleName = session == null ? null : (String) session.getAttribute(AppConstants.SESSION_CURRENT_USER_ROLE);
        return roleName == null ? null : Role.valueOf(roleName);
    }

    public static void setFlashMessage(HttpServletRequest request, String type, String message) {
        HttpSession session = request.getSession();
        session.setAttribute(AppConstants.SESSION_FLASH_TYPE, type);
        session.setAttribute(AppConstants.SESSION_FLASH_MESSAGE, message);
    }

    public static void exposeFlashMessage(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return;
        }
        Object type = session.getAttribute(AppConstants.SESSION_FLASH_TYPE);
        Object message = session.getAttribute(AppConstants.SESSION_FLASH_MESSAGE);
        if (type != null && message != null) {
            request.setAttribute("flashType", type);
            request.setAttribute("flashMessage", message);
            session.removeAttribute(AppConstants.SESSION_FLASH_TYPE);
            session.removeAttribute(AppConstants.SESSION_FLASH_MESSAGE);
        }
    }
}
