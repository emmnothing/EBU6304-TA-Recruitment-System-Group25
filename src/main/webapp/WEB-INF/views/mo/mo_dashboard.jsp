<%@ page import="com.bupt.ta.dto.MoDashboardSummary" %>
<%
MoDashboardSummary summary = (MoDashboardSummary) request.getAttribute("summary");
String currentUsername = (String) request.getAttribute("currentUsername");
Integer unreadNotificationCount = (Integer) request.getAttribute("unreadNotificationCount");
%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>TA Recruitment System - Module Organiser Dashboard</title>
  <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/style.css?v=role-nav-20260513">
</head>
<body class="app-page">
  <div class="app-shell">
    <%
    request.setAttribute("roleNavPage", "dashboard");
    request.setAttribute("roleNavRoleLabel", "Module Organiser");
    request.setAttribute("roleNavTitle", "Module Organiser Dashboard");
    request.setAttribute("roleNavSubtitle", "Welcome, <strong>" + currentUsername + "</strong>. Manage your TA positions and review applicants from one place.");
    request.setAttribute("roleNavNotificationKey", "notifications");
    request.setAttribute("roleNavItems", new String[][] {
        {"dashboard", "Dashboard", "/mo/dashboard"},
        {"jobs", "Post TA Job", "/mo/post-job"},
        {"applicants", "Applicants", "/mo/applicants"},
        {"notifications", "Notifications", "/mo/notifications"}
    });
    %>
    <%@ include file="../shared/role_nav.jspf" %>

    <div class="summary-strip">
      <div class="summary-card">
        <div class="number"><%= summary.getOpenPostCount() %></div>
        <div class="label">Open posts</div>
      </div>
      <div class="summary-card">
        <div class="number"><%= summary.getApplicantsPendingCount() %></div>
        <div class="label">Applicants pending review</div>
      </div>
      <div class="summary-card">
        <div class="number"><%= summary.getInterviewsScheduledCount() %></div>
        <div class="label">Interviews scheduled</div>
      </div>
      <div class="summary-card">
        <div class="number"><%= unreadNotificationCount == null ? 0 : unreadNotificationCount %></div>
        <div class="label">Unread notifications</div>
      </div>
    </div>

    <div class="page-grid two-col" style="margin-top: 22px;">
      <div class="feature-card" id="postJobCard">
        <h3>Post a Job</h3>
        <p>Create a new TA recruitment post with module details, expected workload, deadline, and requirements.</p>
        <div class="actions">
          <a class="btn-secondary" href="<%= request.getContextPath() %>/mo/post-job">Open Job Form</a>
        </div>
      </div>

      <div class="feature-card" id="viewApplicantsCard">
        <h3>View Applicants</h3>
        <p>Filter applicants, export lists, schedule interviews, and process single or batch decisions.</p>
        <div class="actions">
          <a class="btn-secondary" href="<%= request.getContextPath() %>/mo/applicants">Review Applicants</a>
        </div>
      </div>

      <div class="feature-card" id="notificationCard">
        <h3>Notifications</h3>
        <p>Track new applicant submissions and other activity updates for the jobs you manage.</p>
        <div class="actions">
          <a class="btn-secondary" href="<%= request.getContextPath() %>/mo/notifications">Open Notifications</a>
        </div>
      </div>
    </div>
  </div>
</body>
</html>
