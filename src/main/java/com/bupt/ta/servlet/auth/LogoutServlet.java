package com.bupt.ta.servlet.auth;

import com.bupt.ta.util.CookieUtil;
import com.bupt.ta.util.SessionUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/auth/logout")
public class LogoutServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        SessionUtil.clearSession(request);
        CookieUtil.clearRememberMeCookie(request, response);
        response.sendRedirect(request.getContextPath() + "/auth/login?logout=1");
    }
}
