<%@ page import="java.util.List" %>
<%@ page import="com.bupt.ta.dto.ApplicationStatusSummary" %>
<%@ page import="com.bupt.ta.dto.ApplicationStatusViewItem" %>
<%@ page import="com.bupt.ta.model.ApplicationStatus" %>
<%@ page import="com.bupt.ta.util.DisplayFormatUtil" %>
<%
String currentUsername = (String) request.getAttribute("currentUsername");
ApplicationStatusSummary summary = (ApplicationStatusSummary) request.getAttribute("summary");
List<ApplicationStatusViewItem> statusList = (List<ApplicationStatusViewItem>) request.getAttribute("statusList");
%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>TA Recruitment System - Application Status</title>
  <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/style.css?v=role-nav-20260513">
</head>
<body class="app-page">
  <div class="app-shell">
    <%
    request.setAttribute("roleNavPage", "status");
    request.setAttribute("roleNavRoleLabel", "Applicant");
    request.setAttribute("roleNavTitle", "Application Status");
    request.setAttribute("roleNavSubtitle", "Review your submitted applications and their current progress, <strong>" + currentUsername + "</strong>.");
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

    <div class="stats-grid five">
      <div class="summary-card">
        <div class="number"><%= summary.getAppliedCount() %></div>
        <div class="label">Applied</div>
      </div>
      <div class="summary-card">
        <div class="number"><%= summary.getUnderReviewCount() %></div>
        <div class="label">Under Review</div>
      </div>
      <div class="summary-card">
        <div class="number"><%= summary.getInterviewScheduledCount() %></div>
        <div class="label">Interview Scheduled</div>
      </div>
      <div class="summary-card">
        <div class="number"><%= summary.getSelectedCount() %></div>
        <div class="label">Selected</div>
      </div>
      <div class="summary-card">
        <div class="number"><%= summary.getRejectedCount() %></div>
        <div class="label">Rejected</div>
      </div>
    </div>

    <div class="panel" style="margin-top: 22px;">
      <h2>Application Records</h2>
      <div class="table-wrapper">
        <% if (statusList == null || statusList.isEmpty()) { %>
          <div class="empty-state">No application records yet. Browse available jobs and submit your first application.</div>
        <% } else { %>
          <table id="applicationStatusTable">
            <thead>
              <tr>
                <th>Job Title</th>
                <th>Module</th>
                <th>Date Applied</th>
                <th>Status</th>
                <th>Last Updated</th>
                <th>Interview</th>
                <th>Remarks</th>
              </tr>
            </thead>
            <tbody>
              <% for (ApplicationStatusViewItem item : statusList) { %>
                <tr>
                  <td><%= item.getJobTitle() %></td>
                  <td><%= item.getModuleCode() %> - <%= item.getModuleName() %></td>
                  <td><%= DisplayFormatUtil.formatDateTime(item.getAppliedAt()) %></td>
                  <td>
                    <span class="status-badge <%= item.getStatus().getBadgeClass() %>">
                      <%= item.getStatus().getDisplayName() %>
                    </span>
                  </td>
                  <td><%= DisplayFormatUtil.formatDateTime(item.getStatusUpdatedAt()) %></td>
                  <td>
                    <% if (item.getInterviewScheduledAt() == null || item.getInterviewScheduledAt().isBlank()) { %>
                      -
                    <% } else { %>
                      <%= DisplayFormatUtil.formatDateTime(item.getInterviewScheduledAt()) %>
                      <%= item.getInterviewMode() == null || item.getInterviewMode().isBlank() ? "" : " | " + item.getInterviewMode() %>
                      <%= item.getInterviewLocation() == null || item.getInterviewLocation().isBlank() ? "" : " | " + item.getInterviewLocation() %>
                    <% } %>
                  </td>
                  <td><%= item.getRemarks() == null ? "-" : item.getRemarks() %></td>
                </tr>
              <% } %>
            </tbody>
          </table>
        <% } %>
      </div>
    </div>
  </div>
</body>
</html>
