package com.bupt.ta.servlet.applicant;

import com.bupt.ta.model.Role;
import com.bupt.ta.service.ApplicationService;
import com.bupt.ta.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/applicant/status")
public class ApplicantStatusServlet extends HttpServlet {
    private final ApplicationService applicationService = new ApplicationService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!SessionUtil.requireRole(request, response, Role.TA_APPLICANT)) {
            return;
        }
        SessionUtil.exposeFlashMessage(request);
        request.setAttribute("pageTitle", "Application Status");
        request.setAttribute("currentUsername", SessionUtil.getCurrentUsername(request));
        request.setAttribute("summary", applicationService.countStatusSummary(SessionUtil.getCurrentUserId(request)));
        request.setAttribute("statusList", applicationService.getApplicantStatusList(SessionUtil.getCurrentUserId(request)));
        request.getRequestDispatcher("/WEB-INF/views/applicant/ta_status.jsp").forward(request, response);
    }
}
