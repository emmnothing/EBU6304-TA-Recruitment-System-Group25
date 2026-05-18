package com.bupt.ta.servlet.auth;

import com.bupt.ta.dto.OperationResult;
import com.bupt.ta.model.Role;
import com.bupt.ta.model.User;
import com.bupt.ta.service.AuthService;
import com.bupt.ta.util.AppConstants;
import com.bupt.ta.util.CookieUtil;
import com.bupt.ta.util.JwtUtil;
import com.bupt.ta.util.RouteUtil;
import com.bupt.ta.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/auth/login")
public class LoginServlet extends HttpServlet {
    private final AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (SessionUtil.restoreLoginFromRememberMe(request, response) || SessionUtil.getCurrentUserId(request) != null) {
            response.sendRedirect(request.getContextPath() + RouteUtil.getDashboardPath(SessionUtil.getCurrentUserRole(request)));
            return;
        }

        SessionUtil.exposeFlashMessage(request);
        if ("1".equals(request.getParameter("logout"))) {
            request.setAttribute("flashType", "success");
            request.setAttribute("flashMessage", "You have logged out successfully.");
        } else if ("1".equals(request.getParameter("accountDeleted"))) {
            request.setAttribute("flashType", "success");
            request.setAttribute("flashMessage", "Your account has been deleted successfully.");
        }
        request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String roleValue = request.getParameter("role");
        boolean rememberMe = "on".equals(request.getParameter("rememberMe"));

        request.setAttribute("usernameValue", username);
        request.setAttribute("roleValue", roleValue);

        Role role = null;
        try {
            role = Role.valueOf(roleValue);
        } catch (Exception ignored) {
        }

        OperationResult<User> result = authService.authenticateUser(username, password, role);
        if (result.isSuccess()) {
            User user = result.getData();
            SessionUtil.createSession(request, user.getUserId(), user.getUsername(), user.getRole());
            if (rememberMe) {
                int maxAgeSeconds = getRememberMeMaxAgeSeconds(user);
                String token = JwtUtil.createRememberMeToken(user, maxAgeSeconds);
                CookieUtil.writeRememberMeCookie(request, response, token, maxAgeSeconds);
            } else {
                CookieUtil.clearRememberMeCookie(request, response);
            }
            response.sendRedirect(request.getContextPath() + RouteUtil.getDashboardPath(user.getRole()));
            return;
        }

        request.setAttribute("loginError", result.getMessage());
        request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
    }

    private int getRememberMeMaxAgeSeconds(User user) {
        // 管理员权限更高，持久登录有效期短于普通用户。
        return user.getRole() == Role.ADMINISTRATOR
            ? AppConstants.ADMIN_REMEMBER_ME_MAX_AGE_SECONDS
            : AppConstants.REMEMBER_ME_MAX_AGE_SECONDS;
    }
}
