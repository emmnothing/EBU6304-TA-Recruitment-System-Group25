<%@ page import="java.util.List" %>
<%@ page import="com.bupt.ta.model.Notification" %>
<%@ page import="com.bupt.ta.util.DisplayFormatUtil" %>
<%!
private String escapeHtml(String value) {
    if (value == null) {
        return "";
    }
    return value
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;")
        .replace("'", "&#39;");
}
%>
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
  <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/style.css?v=role-nav-20260513">
</head>
<body class="app-page">
  <div class="app-shell">
    <%
    request.setAttribute("roleNavPage", "notifications");
    request.setAttribute("roleNavRoleLabel", "Applicant");
    request.setAttribute("roleNavTitle", "Notifications");
    request.setAttribute("roleNavSubtitle", "Review recent system updates for your applications, <strong>" + escapeHtml(currentUsername) + "</strong>.");
    request.setAttribute("roleNavNotificationKey", "notifications");
    request.setAttribute("roleNavItems", new String[][] {
        {"dashboard", "Dashboard", "/applicant/dashboard"},
        {"profile", "Profile", "/applicant/profile"},
        {"jobs", "Available Jobs", "/applicant/jobs"},
        {"status", "My Applications", "/applicant/status"},
        {"notifications", "Notifications", "/applicant/notifications"}
    });
    %>
    <%@ include file="../shared/role_nav.jspf" %>

    <div class="panel">
      <h2>Recent Notifications</h2>
      <% if (notifications == null || notifications.isEmpty()) { %>
        <div class="empty-state">No notifications yet. Submission and review updates will appear here.</div>
      <% } else { %>
        <ul class="records-list">
          <% for (Notification notification : notifications) { %>
            <li>
              <strong><%= escapeHtml(notification.getTitle()) %></strong>
              <div class="hint"><%= DisplayFormatUtil.formatDateTime(notification.getCreatedAt()) %></div>
              <div class="announcement-body"><%= escapeHtml(notification.getMessage()) %></div>
            </li>
          <% } %>
        </ul>
      <% } %>
    </div>
  </div>
</body>
</html>
