package com.bupt.ta.servlet.account;

import com.bupt.ta.dto.OperationResult;
import com.bupt.ta.model.Role;
import com.bupt.ta.service.AccountService;
import com.bupt.ta.util.CookieUtil;
import com.bupt.ta.util.RouteUtil;
import com.bupt.ta.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/account/delete")
public class AccountDeletionServlet extends HttpServlet {
    private final AccountService accountService = new AccountService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!SessionUtil.requireLogin(request, response)) {
            return;
        }
        request.setAttribute("pageTitle", "Delete Account");
        request.setAttribute("currentUsername", SessionUtil.getCurrentUsername(request));
        request.setAttribute("dashboardPath", RouteUtil.getDashboardPath(SessionUtil.getCurrentUserRole(request)));
        request.setAttribute("impactItems", getImpactItems(SessionUtil.getCurrentUserRole(request)));
        request.getRequestDispatcher("/WEB-INF/views/account/delete_account.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!SessionUtil.requireLogin(request, response)) {
            return;
        }
        request.setCharacterEncoding("UTF-8");

        String currentUsername = SessionUtil.getCurrentUsername(request);
        String confirmation = request.getParameter("confirmUsername");
        if (confirmation == null || !confirmation.trim().equals(currentUsername)) {
            request.setAttribute("pageTitle", "Delete Account");
            request.setAttribute("currentUsername", currentUsername);
            request.setAttribute("dashboardPath", RouteUtil.getDashboardPath(SessionUtil.getCurrentUserRole(request)));
            request.setAttribute("impactItems", getImpactItems(SessionUtil.getCurrentUserRole(request)));
            request.setAttribute("deleteError", "Please type your current username exactly to confirm account deletion.");
            request.getRequestDispatcher("/WEB-INF/views/account/delete_account.jsp").forward(request, response);
            return;
        }

        OperationResult<Void> result = accountService.cancelAccount(SessionUtil.getCurrentUserId(request));
        if (!result.isSuccess()) {
            request.setAttribute("pageTitle", "Delete Account");
            request.setAttribute("currentUsername", currentUsername);
            request.setAttribute("dashboardPath", RouteUtil.getDashboardPath(SessionUtil.getCurrentUserRole(request)));
            request.setAttribute("impactItems", getImpactItems(SessionUtil.getCurrentUserRole(request)));
            request.setAttribute("deleteError", result.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/account/delete_account.jsp").forward(request, response);
            return;
        }

        SessionUtil.clearSession(request);
        CookieUtil.clearRememberMeCookie(request, response);
        response.sendRedirect(request.getContextPath() + "/auth/login?accountDeleted=1");
    }

    private List<String> getImpactItems(Role role) {
        if (role == Role.TA_APPLICANT) {
            return List.of(
                "Your login account will be removed permanently.",
                "Your applicant profile and uploaded CV will be deleted.",
                "Your submitted applications and related notifications will be removed."
            );
        }
        if (role == Role.MODULE_ORGANISER) {
            return List.of(
                "Your login account will be removed permanently.",
                "All TA jobs posted under your account will be deleted.",
                "Applications and notifications linked to those jobs will also be removed."
            );
        }
        return List.of(
            "Your administrator account will be removed permanently.",
            "Any notifications linked directly to your account will be deleted.",
            "This action cannot be undone."
        );
    }
}
