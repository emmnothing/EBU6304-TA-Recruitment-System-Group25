<%@ page import="com.bupt.ta.dto.AdminOverview" %>
<%@ page import="com.bupt.ta.dto.WorkloadRow" %>
<%@ page import="com.bupt.ta.dto.AdminJobRecord" %>
<%@ page import="com.bupt.ta.dto.AdminApplicationRecord" %>
<%@ page import="com.bupt.ta.util.DisplayFormatUtil" %>
<%
String flashType = (String) request.getAttribute("flashType");
String flashMessage = (String) request.getAttribute("flashMessage");
String currentUsername = (String) request.getAttribute("currentUsername");
AdminOverview overview = (AdminOverview) request.getAttribute("overview");
%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>TA Recruitment System - Administrator Dashboard</title>
  <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/style.css?v=role-nav-20260513">
</head>
<body class="app-page">
  <div class="app-shell">
    <%
    request.setAttribute("roleNavPage", "dashboard");
    request.setAttribute("roleNavRoleLabel", "Administrator");
    request.setAttribute("roleNavTitle", "Administrator Dashboard");
    request.setAttribute("roleNavSubtitle", "Welcome, <strong>" + currentUsername + "</strong>. Review workload, system records, and overall recruitment progress.");
    request.setAttribute("roleNavItems", new String[][] {
        {"dashboard", "Dashboard", "/admin/dashboard"},
        {"users", "User Management", "/admin/users"},
        {"notifications", "Announcements", "/admin/notifications"}
    });
    %>
    <%@ include file="../shared/role_nav.jspf" %>

    <% if (flashMessage != null) { %>
      <div class="flash-message <%= flashType %>"><%= flashMessage %></div>
    <% } %>

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

    <div class="page-grid three-col" style="margin-top: 22px;">
      <div class="panel">
        <h2>User Management</h2>
        <p>Review all platform accounts, disable access when needed, and reset passwords back to the shared temporary credential.</p>
        <div class="decision-actions">
          <a class="btn-secondary" href="<%= request.getContextPath() %>/admin/users">Open User Management</a>
        </div>
      </div>

      <div class="panel">
        <h2>System Announcements</h2>
        <p>Send notices to active TA applicants and module organisers, then review delivery and read counts.</p>
        <div class="decision-actions">
          <a class="btn-secondary" href="<%= request.getContextPath() %>/admin/notifications">Open Announcements</a>
        </div>
      </div>

      <div class="panel">
        <h2>Global Export</h2>
        <p>Download system-wide CSV snapshots for users, jobs, applications, and workload reporting.</p>
        <div class="decision-actions">
          <a class="btn-secondary" href="<%= request.getContextPath() %>/admin/export?type=users">Users CSV</a>
          <a class="btn-secondary" href="<%= request.getContextPath() %>/admin/export?type=jobs">Jobs CSV</a>
          <a class="btn-secondary" href="<%= request.getContextPath() %>/admin/export?type=applications">Applications CSV</a>
          <a class="btn-secondary" href="<%= request.getContextPath() %>/admin/export?type=workload">Workload CSV</a>
        </div>
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
                    <td><%= DisplayFormatUtil.formatDateTime(row.getUpdatedAt()) %></td>
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
