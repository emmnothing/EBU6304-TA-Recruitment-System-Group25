<%@ page import="com.bupt.ta.dto.AdminOverview" %>
<%@ page import="com.bupt.ta.dto.WorkloadRow" %>
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
        <h2>Records Overview</h2>
        <ul class="records-list" id="recordsList">
          <% for (String record : overview.getRecordsList()) { %>
            <li><%= record %></li>
          <% } %>
        </ul>
      </div>
    </div>
  </div>
</body>
</html>
