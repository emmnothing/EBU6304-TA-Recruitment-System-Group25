<%@ page import="java.util.List" %>
<%@ page import="com.bupt.ta.dto.ApplicationStatusSummary" %>
<%@ page import="com.bupt.ta.dto.ApplicationStatusViewItem" %>
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
  <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/style.css">
</head>
<body class="app-page">
  <div class="app-shell">
    <div class="topbar panel">
      <div class="brand">
        <h1>Application Status</h1>
        <p>Review your submitted applications and their current progress, <strong><%= currentUsername %></strong>.</p>
      </div>
      <div class="top-links">
        <a href="<%= request.getContextPath() %>/applicant/dashboard">Back to Dashboard</a>
        <a href="<%= request.getContextPath() %>/applicant/jobs">Available Jobs</a>
        <a href="<%= request.getContextPath() %>/auth/logout">Logout</a>
      </div>
    </div>

    <div class="stats-grid four">
      <div class="summary-card">
        <div class="number"><%= summary.getAppliedCount() %></div>
        <div class="label">Applied</div>
      </div>
      <div class="summary-card">
        <div class="number"><%= summary.getUnderReviewCount() %></div>
        <div class="label">Under Review</div>
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
                <th>Remarks</th>
              </tr>
            </thead>
            <tbody>
              <% for (ApplicationStatusViewItem item : statusList) { %>
                <tr>
                  <td><%= item.getJobTitle() %></td>
                  <td><%= item.getModuleCode() %> - <%= item.getModuleName() %></td>
                  <td><%= item.getAppliedAt() %></td>
                  <td>
                    <span class="status-badge <%= item.getStatus() == com.bupt.ta.model.ApplicationStatus.APPLIED ? "applied" : item.getStatus() == com.bupt.ta.model.ApplicationStatus.UNDER_REVIEW ? "review" : item.getStatus() == com.bupt.ta.model.ApplicationStatus.SELECTED ? "selected" : "rejected" %>">
                      <%= item.getStatus() %>
                    </span>
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
