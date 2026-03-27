<%@ page import="com.bupt.ta.model.Role" %>
<%
String flashType = (String) request.getAttribute("flashType");
String flashMessage = (String) request.getAttribute("flashMessage");
String loginError = (String) request.getAttribute("loginError");
String usernameValue = (String) request.getAttribute("usernameValue");
String roleValue = (String) request.getAttribute("roleValue");
%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>TA Recruitment System - Login</title>
  <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/style.css">
</head>
<body class="auth-page">
  <div class="auth-wrapper">
    <div class="auth-stack" style="max-width: 420px;">
      <div class="system-title">TA Recruitment System</div>
      <div class="auth-card">
        <h2 class="page-heading">Login</h2>
        <p class="page-subtitle">Please enter your account details</p>

        <% if (flashMessage != null) { %>
          <div class="flash-message <%= flashType %>"><%= flashMessage %></div>
        <% } %>
        <% if (loginError != null) { %>
          <div class="form-error"><%= loginError %></div>
        <% } %>

        <form id="loginForm" method="post" action="<%= request.getContextPath() %>/auth/login">
          <div class="field">
            <label for="usernameInput">Username</label>
            <input type="text" id="usernameInput" name="username" placeholder="Enter username" value="<%= usernameValue == null ? "" : usernameValue %>">
          </div>

          <div class="field">
            <label for="passwordInput">Password</label>
            <input type="password" id="passwordInput" name="password" placeholder="Enter password">
          </div>

          <div class="field">
            <label for="roleSelect">User Type</label>
            <select id="roleSelect" name="role">
              <% for (Role role : Role.values()) { %>
                <option value="<%= role.name() %>" <%= role.name().equals(roleValue) ? "selected" : "" %>><%= role.getDisplayName() %></option>
              <% } %>
            </select>
          </div>

          <div class="inline-row">
            <label><input type="checkbox" id="rememberCheckbox" name="rememberMe"> Remember me</label>
            <a href="<%= request.getContextPath() %>/auth/forgot">Forgot?</a>
          </div>

          <div class="actions">
            <button class="btn" type="submit">Login</button>
          </div>
        </form>

        <div class="auth-footer">
          No account? <a href="<%= request.getContextPath() %>/auth/register">Register here</a>
        </div>
      </div>
    </div>
  </div>
</body>
</html>
