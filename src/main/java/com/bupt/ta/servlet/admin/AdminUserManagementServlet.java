package com.bupt.ta.servlet.admin;

import com.bupt.ta.dto.AdminUserFilter;
import com.bupt.ta.dto.OperationResult;
import com.bupt.ta.model.Role;
import com.bupt.ta.service.AdminService;
import com.bupt.ta.util.AppConstants;
import com.bupt.ta.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@WebServlet({
    "/admin/users",
    "/admin/users/toggle-status",
    "/admin/users/reset-password"
})
public class AdminUserManagementServlet extends HttpServlet {
    private final AdminService adminService = new AdminService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!SessionUtil.requireRole(request, response, Role.ADMINISTRATOR)) {
            return;
        }

        SessionUtil.exposeFlashMessage(request);
        AdminUserFilter filter = buildUserFilter(request);
        request.setAttribute("pageTitle", "User Management");
        request.setAttribute("currentUsername", SessionUtil.getCurrentUsername(request));
        request.setAttribute("userFilter", filter);
        request.setAttribute(
            "userManagementView",
            adminService.getUserManagementView(filter, SessionUtil.getCurrentUserId(request))
        );
        request.setAttribute("defaultResetPassword", adminService.getDefaultResetPassword());
        request.getRequestDispatcher("/WEB-INF/views/admin/admin_users.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!SessionUtil.requireRole(request, response, Role.ADMINISTRATOR)) {
            return;
        }
        request.setCharacterEncoding("UTF-8");

        String requestPath = request.getRequestURI();
        String targetUserId = request.getParameter("userId");
        OperationResult<?> result;

        if (requestPath.endsWith("/toggle-status")) {
            result = adminService.toggleUserStatus(targetUserId, SessionUtil.getCurrentUserId(request));
        } else {
            result = adminService.resetUserPassword(targetUserId);
        }

        SessionUtil.setFlashMessage(
            request,
            result.isSuccess() ? AppConstants.FLASH_SUCCESS : AppConstants.FLASH_ERROR,
            result.getMessage()
        );
        response.sendRedirect(request.getContextPath() + buildUsersRedirect(buildUserFilter(request)));
    }

    private AdminUserFilter buildUserFilter(HttpServletRequest request) {
        AdminUserFilter filter = new AdminUserFilter();
        filter.setRole(request.getParameter("role"));
        filter.setStatus(request.getParameter("status"));
        filter.setKeyword(request.getParameter("keyword"));
        filter.setSortBy(request.getParameter("sortBy"));
        filter.setSortDirection(request.getParameter("sortDirection"));
        return filter;
    }

    private String buildUsersRedirect(AdminUserFilter filter) {
        StringBuilder builder = new StringBuilder("/admin/users?");
        appendQuery(builder, "role", filter == null ? null : filter.getRole());
        appendQuery(builder, "status", filter == null ? null : filter.getStatus());
        appendQuery(builder, "keyword", filter == null ? null : filter.getKeyword());
        appendQuery(builder, "sortBy", filter == null ? null : filter.getSortBy());
        appendQuery(builder, "sortDirection", filter == null ? null : filter.getSortDirection());
        return builder.toString();
    }

    private void appendQuery(StringBuilder builder, String key, String value) {
        if (builder.charAt(builder.length() - 1) != '?') {
            builder.append('&');
        }
        builder.append(key).append('=').append(URLEncoder.encode(value == null ? "" : value, StandardCharsets.UTF_8));
    }
}
