package com.bupt.ta.servlet.mo;

import com.bupt.ta.model.Role;
import com.bupt.ta.service.DashboardService;
import com.bupt.ta.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/mo/dashboard")
public class ModuleOrganiserDashboardServlet extends HttpServlet {
    private final DashboardService dashboardService = new DashboardService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!SessionUtil.requireRole(request, response, Role.MODULE_ORGANISER)) {
            return;
        }
        SessionUtil.exposeFlashMessage(request);
        request.setAttribute("pageTitle", "Module Organiser Dashboard");
        request.setAttribute("currentUsername", SessionUtil.getCurrentUsername(request));
        request.setAttribute("summary", dashboardService.getMoSummary(SessionUtil.getCurrentUserId(request)));
        request.getRequestDispatcher("/WEB-INF/views/mo/mo_dashboard.jsp").forward(request, response);
    }
}
