package com.bupt.ta.servlet.mo;

import com.bupt.ta.model.Role;
import com.bupt.ta.service.NotificationService;
import com.bupt.ta.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/mo/notifications")
public class ModuleOrganiserNotificationsServlet extends HttpServlet {
    private final NotificationService notificationService = new NotificationService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!SessionUtil.requireRole(request, response, Role.MODULE_ORGANISER)) {
            return;
        }
        SessionUtil.exposeFlashMessage(request);
        String userId = SessionUtil.getCurrentUserId(request);
        notificationService.markAllAsRead(userId);
        request.setAttribute("pageTitle", "Module Organiser Notifications");
        request.setAttribute("currentUsername", SessionUtil.getCurrentUsername(request));
        request.setAttribute("notifications", notificationService.getNotificationsForUser(userId));
        request.getRequestDispatcher("/WEB-INF/views/mo/mo_notifications.jsp").forward(request, response);
    }
}
