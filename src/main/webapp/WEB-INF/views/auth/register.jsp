<%@ page import="com.bupt.ta.dto.RegisterForm" %>
<%@ page import="com.bupt.ta.model.Role" %>
<%
String flashType = (String) request.getAttribute("flashType");
String flashMessage = (String) request.getAttribute("flashMessage");
String registerError = (String) request.getAttribute("registerError");
RegisterForm registerForm = (RegisterForm) request.getAttribute("registerForm");
%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>TA Recruitment System - Register</title>
  <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/style.css">
</head>
<body class="auth-page">
  <div class="auth-wrapper">
    <div class="auth-stack wide-card">
      <div class="auth-card">
        <h2 class="page-heading">Register</h2>
        <p class="page-subtitle">Please create your account details</p>

        <% if (flashMessage != null) { %>
          <div class="flash-message <%= flashType %>"><%= flashMessage %></div>
        <% } %>
        <% if (registerError != null) { %>
          <div class="form-error"><%= registerError %></div>
        <% } %>
        <div id="registerClientError" class="form-error" style="display:none;"></div>

        <form id="registerForm" method="post" action="<%= request.getContextPath() %>/auth/register" novalidate>
          <div class="form-grid">
            <div class="field">
              <label for="usernameInput">Username</label>
              <input type="text" id="usernameInput" name="username" placeholder="Enter your username" value="<%= registerForm == null || registerForm.getUsername() == null ? "" : registerForm.getUsername() %>" required>
            </div>

            <div class="field">
              <label for="emailInput">Email</label>
              <input type="email" id="emailInput" name="email" placeholder="Enter your email" value="<%= registerForm == null || registerForm.getEmail() == null ? "" : registerForm.getEmail() %>" required>
              <div class="hint">Example: student@example.com</div>
            </div>

            <div class="field">
              <label for="phoneInput">Phone Number</label>
              <input type="text" id="phoneInput" name="phoneNumber" placeholder="Enter 11-digit phone number" value="<%= registerForm == null || registerForm.getPhoneNumber() == null ? "" : registerForm.getPhoneNumber() %>" maxlength="11" data-digits-only="true" required>
              <div class="hint">Phone number must contain exactly 11 digits.</div>
            </div>

            <div class="field">
              <label for="roleSelect">User Type</label>
              <select id="roleSelect" name="role" required>
                <% for (Role role : Role.values()) { %>
                  <% if (role == Role.ADMINISTRATOR) { continue; } %>
                  <option value="<%= role.name() %>" <%= registerForm != null && role.name().equals(registerForm.getRole()) ? "selected" : (registerForm == null && role == Role.TA_APPLICANT ? "selected" : "") %>>
                    <%= role.getDisplayName() %>
                  </option>
                <% } %>
              </select>
            </div>

            <div class="field">
              <label for="passwordInput">Password</label>
              <input type="password" id="passwordInput" name="password" placeholder="Enter password" required>
            </div>

            <div class="field">
              <label for="confirmPasswordInput">Confirm Password</label>
              <input type="password" id="confirmPasswordInput" name="confirmPassword" placeholder="Confirm password" required>
            </div>

            <div class="field full-width">
              <label for="verificationAnswerInput">Verification</label>
              <div class="captcha-row">
                <div>
                  <input type="text" id="verificationAnswerInput" name="verificationAnswer" placeholder="Enter the answer" required>
                </div>
                <div class="captcha-box" id="captchaQuestion">3 + 4 = ?</div>
              </div>
            </div>

            <div class="field full-width actions">
              <button class="btn" type="submit">Register</button>
            </div>
          </div>
        </form>

        <div class="auth-footer">
          Already have an account? <a href="<%= request.getContextPath() %>/auth/login">Back to login</a>
        </div>
      </div>
    </div>
  </div>
  <script src="<%= request.getContextPath() %>/assets/js/app.js"></script>
</body>
</html>
