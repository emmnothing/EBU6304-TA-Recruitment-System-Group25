package com.bupt.ta.servlet.admin;

import com.bupt.ta.dto.AdminAnnouncementForm;
import com.bupt.ta.dto.OperationResult;
import com.bupt.ta.model.Role;
import com.bupt.ta.service.AuditLogService;
import com.bupt.ta.service.NotificationService;
import com.bupt.ta.util.AppConstants;
import com.bupt.ta.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet({
    "/admin/notifications",
    "/admin/notifications/send"
})
public class AdminNotificationManagementServlet extends HttpServlet {
    private final NotificationService notificationService = new NotificationService();
    private final AuditLogService auditLogService = new AuditLogService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!SessionUtil.requireRole(request, response, Role.ADMINISTRATOR)) {
            return;
        }

        SessionUtil.exposeFlashMessage(request);
        request.setAttribute("pageTitle", "System Announcements");
        request.setAttribute("currentUsername", SessionUtil.getCurrentUsername(request));
        request.setAttribute("recipientSummary", notificationService.getAnnouncementRecipientSummary());
        request.setAttribute("recentAnnouncements", notificationService.getRecentSystemAnnouncements());
        request.getRequestDispatcher("/WEB-INF/views/admin/admin_notifications.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!SessionUtil.requireRole(request, response, Role.ADMINISTRATOR)) {
            return;
        }
        request.setCharacterEncoding("UTF-8");

        /*
         * The servlet keeps request parsing small and delegates every business
         * rule to NotificationService. That keeps validation identical if a
         * future API endpoint or page needs to send the same announcement type.
         */
        AdminAnnouncementForm form = new AdminAnnouncementForm();
        form.setAudience(request.getParameter("audience"));
        form.setTitle(request.getParameter("title"));
        form.setMessage(request.getParameter("message"));

        OperationResult<Integer> result = notificationService.sendSystemAnnouncement(
            form,
            SessionUtil.getCurrentUserId(request)
        );
        auditSystemAnnouncement(request, form, result);
        SessionUtil.setFlashMessage(
            request,
            result.isSuccess() ? AppConstants.FLASH_SUCCESS : AppConstants.FLASH_ERROR,
            result.getMessage()
        );

        /*
         * Use Post/Redirect/Get so a browser refresh after sending does not
         * create duplicate notification rows for every recipient.
         */
        response.sendRedirect(request.getContextPath() + "/admin/notifications");
    }

    private void auditSystemAnnouncement(
        HttpServletRequest request,
        AdminAnnouncementForm form,
        OperationResult<Integer> result
    ) {
        String audienceLabel = notificationService.getAudienceDisplayName(form.getAudience());
        String detail = result.isSuccess()
            ? "Audience: " + audienceLabel + "; recipients: " + result.getData()
            : result.getMessage();
        auditLogService.recordAdminAction(
            request,
            AuditLogService.ACTION_SYSTEM_ANNOUNCEMENT_SENT,
            AuditLogService.TARGET_ANNOUNCEMENT,
            form.getAudience(),
            form.getTitle(),
            result.isSuccess(),
            detail
        );
    }
}
