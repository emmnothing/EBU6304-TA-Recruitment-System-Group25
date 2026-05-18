package com.bupt.ta.servlet.applicant;

import com.bupt.ta.model.Role;
import com.bupt.ta.service.ApplicantProfileService;
import com.bupt.ta.service.DashboardService;
import com.bupt.ta.service.NotificationService;
import com.bupt.ta.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/applicant/dashboard")
public class ApplicantDashboardServlet extends HttpServlet {
    private final DashboardService dashboardService = new DashboardService();
    private final NotificationService notificationService = new NotificationService();
    private final ApplicantProfileService applicantProfileService = new ApplicantProfileService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!SessionUtil.requireRole(request, response, Role.TA_APPLICANT)) {
            return;
        }
        SessionUtil.exposeFlashMessage(request);
        request.setAttribute("pageTitle", "TA Applicant Dashboard");
        request.setAttribute("currentUsername", SessionUtil.getCurrentUsername(request));
        String userId = SessionUtil.getCurrentUserId(request);
        request.setAttribute("summary", dashboardService.getApplicantSummary(userId));
        request.setAttribute("upcomingInterview", dashboardService.getUpcomingInterview(userId));
        request.setAttribute("unreadNotificationCount", notificationService.countUnread(userId));
        request.setAttribute("profileCompleteness", applicantProfileService.getProfileCompleteness(userId));
        request.getRequestDispatcher("/WEB-INF/views/applicant/ta_dashboard.jsp").forward(request, response);
    }
}
