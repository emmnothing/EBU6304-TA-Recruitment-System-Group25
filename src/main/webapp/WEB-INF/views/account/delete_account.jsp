<%@ page import="java.util.List" %>
<%
String currentUsername = (String) request.getAttribute("currentUsername");
String dashboardPath = (String) request.getAttribute("dashboardPath");
String deleteError = (String) request.getAttribute("deleteError");
List<String> impactItems = (List<String>) request.getAttribute("impactItems");
%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>TA Recruitment System - Delete Account</title>
  <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/style.css">
</head>
<body class="app-page">
  <div class="app-shell" style="max-width: 860px;">
    <div class="topbar panel">
      <div class="brand">
        <h1>Delete Account</h1>
        <p>This will permanently remove your account and related records, <strong><%= currentUsername %></strong>.</p>
      </div>
      <div class="top-links">
        <a href="<%= request.getContextPath() %><%= dashboardPath %>">Back to Dashboard</a>
        <a href="<%= request.getContextPath() %>/auth/logout">Logout</a>
      </div>
    </div>

    <div class="panel">
      <h2>What will happen</h2>
      <ul class="records-list">
        <% if (impactItems != null) { %>
          <% for (String item : impactItems) { %>
            <li><%= item %></li>
          <% } %>
        <% } %>
      </ul>
    </div>

    <div class="panel" style="margin-top: 22px;">
      <h2>Confirmation</h2>
      <p>To continue, type your current username exactly as shown below. This action cannot be undone.</p>

      <% if (deleteError != null) { %>
        <div class="form-error" style="margin-top: 16px;"><%= deleteError %></div>
      <% } %>

      <form method="post" action="<%= request.getContextPath() %>/account/delete" style="margin-top: 18px;">
        <div class="field">
          <label for="confirmUsernameInput">Type username to confirm</label>
          <input type="text" id="confirmUsernameInput" name="confirmUsername" placeholder="<%= currentUsername %>">
        </div>
        <div class="decision-actions">
          <button class="btn-ghost" type="submit">Delete Account Permanently</button>
          <a class="btn-secondary" href="<%= request.getContextPath() %><%= dashboardPath %>">Cancel</a>
        </div>
      </form>
    </div>
  </div>
</body>
</html>
