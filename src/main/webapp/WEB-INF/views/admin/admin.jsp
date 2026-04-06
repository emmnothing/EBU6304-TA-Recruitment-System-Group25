<%@ page import="com.bupt.ta.dto.AdminOverview" %>
<%@ page import="com.bupt.ta.dto.WorkloadRow" %>
<%@ page import="com.bupt.ta.dto.AdminJobRecord" %>
<%@ page import="com.bupt.ta.dto.AdminApplicationRecord" %>
<%
String currentUsername = (String) request.getAttribute("currentUsername");
AdminOverview overview = (AdminOverview) request.getAttribute("overview");
%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>TA Recruitment System - Administrator Dashboard</title>
  <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/style.css">
</head>
<body class="app-page">
  <div class="app-shell">
    <div class="topbar panel">
      <div class="brand">
        <h1>Administrator Dashboard</h1>
        <p>Welcome, <strong><%= currentUsername %></strong>. Review workload, system records, and overall recruitment progress.</p>
      </div>
      <div class="top-links">
        <a href="<%= request.getContextPath() %>/auth/logout">Logout</a>
      </div>
    </div>

    <div class="stats-grid four">
      <div class="summary-card" id="adminSummary">
        <div class="number"><%= overview.getTotalApplicants() %></div>
        <div class="label">Total applicants</div>
      </div>
      <div class="summary-card">
        <div class="number"><%= overview.getOpenJobs() %></div>
        <div class="label">Open jobs</div>
      </div>
      <div class="summary-card">
        <div class="number"><%= overview.getTotalApplications() %></div>
        <div class="label">Total applications</div>
      </div>
      <div class="summary-card">
        <div class="number"><%= overview.getApplicationsPendingReview() %></div>
        <div class="label">Pending review</div>
      </div>
      <div class="summary-card">
        <div class="number"><%= overview.getAssignedTas() %></div>
        <div class="label">Assigned TAs</div>
      </div>
      <div class="summary-card">
        <div class="number"><%= overview.getHighWorkloadAlerts() %></div>
        <div class="label">High workload alerts</div>
      </div>
    </div>

    <div class="page-grid two-col" style="margin-top: 22px;">
      <div class="panel">
        <h2>Workload Overview</h2>
        <div class="table-wrapper">
          <% if (overview.getWorkloadRows().isEmpty()) { %>
            <div class="empty-state">No workload records are available yet.</div>
          <% } else { %>
            <table id="workloadTable">
              <thead>
                <tr>
                  <th>Applicant</th>
                  <th>Assigned Modules</th>
                  <th>Weekly Hours</th>
                  <th>Status</th>
                </tr>
              </thead>
              <tbody>
                <% for (WorkloadRow row : overview.getWorkloadRows()) { %>
                  <tr>
                    <td><%= row.getUsername() %></td>
                    <td><%= row.getAssignedModules() %></td>
                    <td><%= row.getWeeklyHours() %></td>
                    <td><%= row.getWorkloadStatus() %></td>
                  </tr>
                <% } %>
              </tbody>
            </table>
          <% } %>
        </div>
      </div>

      <div class="panel">
        <h2>System Records</h2>
        <ul class="records-list" id="recordsList">
          <% for (String record : overview.getRecordsList()) { %>
            <li><%= record %></li>
          <% } %>
        </ul>
      </div>
    </div>

    <div class="page-grid two-col" style="margin-top: 22px;">
      <div class="panel">
        <h2>Recent Jobs</h2>
        <div class="table-wrapper">
          <% if (overview.getRecentJobs().isEmpty()) { %>
            <div class="empty-state">No job posts available yet.</div>
          <% } else { %>
            <table>
              <thead>
                <tr>
                  <th>Module</th>
                  <th>Job</th>
                  <th>Status</th>
                  <th>Filled</th>
                  <th>Owner</th>
                </tr>
              </thead>
              <tbody>
                <% for (AdminJobRecord row : overview.getRecentJobs()) { %>
                  <tr>
                    <td><%= row.getModuleCode() %></td>
                    <td><%= row.getJobTitle() %></td>
                    <td><%= row.getStatus() %></td>
                    <td><%= row.getFilledCount() %> / <%= row.getVacancies() %></td>
                    <td><%= row.getOwnerUsername() %></td>
                  </tr>
                <% } %>
              </tbody>
            </table>
          <% } %>
        </div>
      </div>

      <div class="panel">
        <h2>Recent Applications</h2>
        <div class="table-wrapper">
          <% if (overview.getRecentApplications().isEmpty()) { %>
            <div class="empty-state">No applications available yet.</div>
          <% } else { %>
            <table>
              <thead>
                <tr>
                  <th>Applicant</th>
                  <th>Module</th>
                  <th>Job</th>
                  <th>Status</th>
                  <th>Updated</th>
                </tr>
              </thead>
              <tbody>
                <% for (AdminApplicationRecord row : overview.getRecentApplications()) { %>
                  <tr>
                    <td><%= row.getApplicantUsername() %></td>
                    <td><%= row.getModuleCode() %></td>
                    <td><%= row.getJobTitle() %></td>
                    <td><%= row.getStatus() %></td>
                    <td><%= row.getUpdatedAt() == null ? "-" : row.getUpdatedAt() %></td>
                  </tr>
                <% } %>
              </tbody>
            </table>
          <% } %>
        </div>
      </div>
    </div>
  </div>
</body>
</html>
