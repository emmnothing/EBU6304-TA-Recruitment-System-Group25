package com.bupt.ta.servlet.auth;

import com.bupt.ta.dto.OperationResult;
import com.bupt.ta.dto.ResetPasswordForm;
import com.bupt.ta.model.User;
import com.bupt.ta.service.AuthService;
import com.bupt.ta.util.AppConstants;
import com.bupt.ta.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/auth/forgot")
public class ForgotPasswordServlet extends HttpServlet {
    private final AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        SessionUtil.exposeFlashMessage(request);
        request.getRequestDispatcher("/WEB-INF/views/auth/forgot.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        ResetPasswordForm resetPasswordForm = new ResetPasswordForm();
        resetPasswordForm.setPhoneNumber(request.getParameter("phoneNumber"));
        resetPasswordForm.setEmail(request.getParameter("email"));
        resetPasswordForm.setNewPassword(request.getParameter("newPassword"));
        resetPasswordForm.setConfirmPassword(request.getParameter("confirmPassword"));

        request.setAttribute("resetForm", resetPasswordForm);

        OperationResult<User> result = authService.resetPassword(resetPasswordForm);
        if (result.isSuccess()) {
            SessionUtil.setFlashMessage(request, AppConstants.FLASH_SUCCESS, result.getMessage());
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }

        request.setAttribute("resetError", result.getMessage());
        request.getRequestDispatcher("/WEB-INF/views/auth/forgot.jsp").forward(request, response);
    }
}
