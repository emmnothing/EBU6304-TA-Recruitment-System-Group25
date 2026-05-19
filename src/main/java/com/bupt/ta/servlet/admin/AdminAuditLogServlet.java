package com.bupt.ta.servlet.admin;

import com.bupt.ta.dto.AdminAuditLogFilter;
import com.bupt.ta.model.Role;
import com.bupt.ta.service.AuditLogService;
import com.bupt.ta.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/admin/audit-log")
public class AdminAuditLogServlet extends HttpServlet {
    private final AuditLogService auditLogService = new AuditLogService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!SessionUtil.requireRole(request, response, Role.ADMINISTRATOR)) {
            return;
        }

        AdminAuditLogFilter filter = buildFilter(request);
        request.setAttribute("pageTitle", "Audit Log");
        request.setAttribute("currentUsername", SessionUtil.getCurrentUsername(request));
        request.setAttribute("auditLogFilter", filter);
        request.setAttribute("auditLogView", auditLogService.getAuditLogView(filter));
        request.getRequestDispatcher("/WEB-INF/views/admin/admin_audit_log.jsp").forward(request, response);
    }

    private AdminAuditLogFilter buildFilter(HttpServletRequest request) {
        AdminAuditLogFilter filter = new AdminAuditLogFilter();
        filter.setAction(request.getParameter("action"));
        filter.setOutcome(request.getParameter("outcome"));
        filter.setKeyword(request.getParameter("keyword"));
        filter.setSortDirection(request.getParameter("sortDirection"));
        return filter;
    }
}
