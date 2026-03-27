<%@ page import="com.bupt.ta.dto.ResetPasswordForm" %>
<%
String flashType = (String) request.getAttribute("flashType");
String flashMessage = (String) request.getAttribute("flashMessage");
String resetError = (String) request.getAttribute("resetError");
ResetPasswordForm resetForm = (ResetPasswordForm) request.getAttribute("resetForm");
%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>TA Recruitment System - Forgot Password</title>
  <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/style.css">
</head>
<body class="auth-page">
  <div class="auth-wrapper">
    <div class="auth-stack wide-card">
      <div class="auth-card">
        <h2 class="page-heading">Forgot Password</h2>
        <p class="page-subtitle">Please verify your account information and reset your password</p>

        <% if (flashMessage != null) { %>
          <div class="flash-message <%= flashType %>"><%= flashMessage %></div>
        <% } %>
        <% if (resetError != null) { %>
          <div class="form-error"><%= resetError %></div>
        <% } %>
        <div id="forgotClientError" class="form-error" style="display:none;"></div>

        <form id="forgotForm" method="post" action="<%= request.getContextPath() %>/auth/forgot" novalidate>
          <div class="form-grid">
            <div class="field">
              <label for="phoneInput">Phone Number</label>
              <input type="text" id="phoneInput" name="phoneNumber" placeholder="Enter your registered phone number" value="<%= resetForm == null || resetForm.getPhoneNumber() == null ? "" : resetForm.getPhoneNumber() %>" maxlength="11" data-digits-only="true" required>
              <div class="hint">Phone number must contain exactly 11 digits.</div>
            </div>

            <div class="field">
              <label for="emailInput">Email</label>
              <input type="email" id="emailInput" name="email" placeholder="Enter your registered email" value="<%= resetForm == null || resetForm.getEmail() == null ? "" : resetForm.getEmail() %>" required>
              <div class="hint">Example: student@example.com</div>
            </div>

            <div class="field">
              <label for="newPasswordInput">New Password</label>
              <input type="password" id="newPasswordInput" name="newPassword" placeholder="Enter new password" required>
            </div>

            <div class="field">
              <label for="confirmPasswordInput">Confirm New Password</label>
              <input type="password" id="confirmPasswordInput" name="confirmPassword" placeholder="Confirm new password" required>
            </div>

            <div class="field full-width actions">
              <button class="btn" type="submit">Reset Password</button>
            </div>
          </div>
        </form>

        <div class="auth-footer">
          Back to <a href="<%= request.getContextPath() %>/auth/login">login</a>
        </div>
      </div>
    </div>
  </div>
  <script src="<%= request.getContextPath() %>/assets/js/app.js"></script>
</body>
</html>
