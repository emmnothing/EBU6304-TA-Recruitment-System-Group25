package com.bupt.ta.servlet.auth;

import com.bupt.ta.dto.OperationResult;
import com.bupt.ta.dto.RegisterForm;
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

@WebServlet("/auth/register")
public class RegisterServlet extends HttpServlet {
    private final AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        SessionUtil.exposeFlashMessage(request);
        request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        RegisterForm registerForm = new RegisterForm();
        registerForm.setUsername(request.getParameter("username"));
        registerForm.setEmail(request.getParameter("email"));
        registerForm.setPhoneNumber(request.getParameter("phoneNumber"));
        registerForm.setRole(request.getParameter("role"));
        registerForm.setPassword(request.getParameter("password"));
        registerForm.setConfirmPassword(request.getParameter("confirmPassword"));

        request.setAttribute("registerForm", registerForm);

        OperationResult<User> result = authService.registerUser(registerForm);
        if (result.isSuccess()) {
            SessionUtil.setFlashMessage(request, AppConstants.FLASH_SUCCESS, result.getMessage());
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }

        request.setAttribute("registerError", result.getMessage());
        request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(request, response);
    }
}
