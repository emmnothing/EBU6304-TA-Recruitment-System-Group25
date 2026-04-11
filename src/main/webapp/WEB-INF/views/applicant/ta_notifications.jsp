<%@ page import="java.util.List" %>
<%@ page import="com.bupt.ta.model.Notification" %>
<%
String currentUsername = (String) request.getAttribute("currentUsername");
List<Notification> notifications = (List<Notification>) request.getAttribute("notifications");
%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>TA Recruitment System - Notifications</title>
  <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/style.css">
</head>
<body class="app-page">
  <div class="app-shell">
    <div class="topbar panel">
      <div class="brand">
        <h1>Notifications</h1>
        <p>Review recent system updates for your applications, <strong><%= currentUsername %></strong>.</p>
      </div>
      <div class="top-links">
        <a href="<%= request.getContextPath() %>/applicant/dashboard">Back to Dashboard</a>
        <a href="<%= request.getContextPath() %>/applicant/status">Application Status</a>
        <a href="<%= request.getContextPath() %>/account/delete">Delete Account</a>
        <a href="<%= request.getContextPath() %>/auth/logout">Logout</a>
      </div>
    </div>

    <div class="panel">
      <h2>Recent Notifications</h2>
      <% if (notifications == null || notifications.isEmpty()) { %>
        <div class="empty-state">No notifications yet. Submission and review updates will appear here.</div>
      <% } else { %>
        <ul class="records-list">
          <% for (Notification notification : notifications) { %>
            <li>
              <strong><%= notification.getTitle() %></strong>
              <div class="hint"><%= notification.getCreatedAt() %></div>
              <div style="margin-top: 8px;"><%= notification.getMessage() %></div>
            </li>
          <% } %>
        </ul>
      <% } %>
    </div>
  </div>
</body>
</html>
