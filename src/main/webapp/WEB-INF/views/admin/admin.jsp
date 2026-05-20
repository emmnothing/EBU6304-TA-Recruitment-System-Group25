<%@ page import="com.bupt.ta.dto.AdminOverview" %>
<%@ page import="com.bupt.ta.dto.WorkloadRow" %>
<%@ page import="com.bupt.ta.dto.AdminJobRecord" %>
<%@ page import="com.bupt.ta.dto.AdminApplicationRecord" %>
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

private String workloadBadgeClass(String status) {
    if ("High".equalsIgnoreCase(status)) {
        return "high";
    }
    if ("Normal".equalsIgnoreCase(status)) {
        return "normal";
    }
    return "available";
}

private String jobBadgeClass(String status) {
    return "OPEN".equalsIgnoreCase(status) ? "open" : "closed";
}

private String applicationBadgeClass(String status) {
    if ("UNDER_REVIEW".equalsIgnoreCase(status)) {
        return "review";
    }
    if ("INTERVIEW_SCHEDULED".equalsIgnoreCase(status)) {
        return "interview";
    }
    if ("SELECTED".equalsIgnoreCase(status)) {
        return "selected";
    }
    if ("REJECTED".equalsIgnoreCase(status)) {
        return "rejected";
    }
    return "applied";
}
%>
<%
String flashType = (String) request.getAttribute("flashType");
String flashMessage = (String) request.getAttribute("flashMessage");
String currentUsername = (String) request.getAttribute("currentUsername");
AdminOverview overview = (AdminOverview) request.getAttribute("overview");
int totalApplicants = overview == null ? 0 : overview.getTotalApplicants();
int openJobs = overview == null ? 0 : overview.getOpenJobs();
int pendingReview = overview == null ? 0 : overview.getApplicationsPendingReview();
int highWorkloadAlerts = overview == null ? 0 : overview.getHighWorkloadAlerts();
int operationalAttention = pendingReview + highWorkloadAlerts;
%>
<!DOCTYPE html>
<html lang="en">
<head>
	  <meta charset="UTF-8">
	  <meta name="viewport" content="width=device-width, initial-scale=1.0">
	  <title>TA Recruitment System - Administrator Dashboard</title>
	  <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/style.css?v=admin-dashboard-20260519">
	</head>
	<body class="app-page admin-page admin-dashboard-page">
	  <div class="app-shell">
	    <%
	    request.setAttribute("roleNavPage", "dashboard");
	    request.setAttribute("roleNavRoleLabel", "Administrator");
	    request.setAttribute("roleNavTitle", "Administrator Dashboard");
	    request.setAttribute("roleNavSubtitle", "Welcome, <strong>" + escapeHtml(currentUsername) + "</strong>. Review workload, system records, and overall recruitment progress.");
    request.setAttribute("roleNavItems", new String[][] {
        {"dashboard", "Dashboard", "/admin/dashboard"},
        {"users", "User Management", "/admin/users"},
        {"notifications", "Announcements", "/admin/notifications"}
    });
    %>
	    <%@ include file="../shared/role_nav.jspf" %>

	    <% if (flashMessage != null) { %>
	      <div class="flash-message <%= escapeHtml(flashType) %>"><%= escapeHtml(flashMessage) %></div>
	    <% } %>

	    <div class="admin-dashboard-pulse-row">
	      <section class="panel dashboard-pulse-card admin-dashboard-pulse-card">
	        <div class="profile-progress-header">
	          <div>
	            <span class="dashboard-kicker">System pulse</span>
	            <h2><%= operationalAttention %> item<%= operationalAttention == 1 ? "" : "s" %> need attention</h2>
	            <p>A compact snapshot of platform activity across applicants, jobs, reviews, and workload pressure.</p>
	          </div>
	        </div>
	        <div class="dashboard-metric-row">
	          <div>
	            <strong><%= totalApplicants %></strong>
	            <span>Applicants</span>
	          </div>
	          <div>
	            <strong><%= openJobs %></strong>
	            <span>Open jobs</span>
	          </div>
	          <div>
	            <strong><%= pendingReview %></strong>
	            <span>Pending review</span>
	          </div>
	          <div>
	            <strong><%= highWorkloadAlerts %></strong>
	            <span>Workload alerts</span>
	          </div>
	        </div>
	      </section>
	    </div>

	    <div class="dashboard-insight-grid admin-dashboard-actions">
	      <section class="panel dashboard-insight-card">
	        <div>
	          <span class="dashboard-kicker">Accounts</span>
	          <h3>User Management</h3>
	          <p>Filter accounts, disable access when needed, and reset passwords back to the shared temporary credential.</p>
	        </div>
	        <a class="btn-secondary" href="<%= request.getContextPath() %>/admin/users">Open User Management</a>
	      </section>

	      <section class="panel dashboard-insight-card">
	        <div>
	          <span class="dashboard-kicker">Communication</span>
	          <h3>System Announcements</h3>
	          <p>Broadcast platform updates to applicants and module organisers, then review delivery and read status.</p>
	        </div>
	        <a class="btn-secondary" href="<%= request.getContextPath() %>/admin/notifications">Open Announcements</a>
	      </section>

	      <section class="panel dashboard-insight-card admin-export-card">
	        <div>
	          <span class="dashboard-kicker">Export center</span>
	          <h3>Download Reports</h3>
	          <p>Export the core CSV files from the dashboard instead of crowding the navigation bar.</p>
	        </div>
	        <div class="admin-export-actions" aria-label="Administrator export actions">
	          <a class="btn-secondary" href="<%= request.getContextPath() %>/admin/export?type=users">Users</a>
	          <a class="btn-secondary" href="<%= request.getContextPath() %>/admin/export?type=applications">Applications</a>
	          <a class="btn-secondary" href="<%= request.getContextPath() %>/admin/export?type=jobs">Jobs</a>
	          <a class="btn-secondary" href="<%= request.getContextPath() %>/admin/export?type=workload">Workload</a>
	        </div>
	      </section>
	    </div>

	    <section class="page-grid two-col admin-content-grid">
	      <div class="panel admin-table-panel">
	        <div class="section-header">
	          <div>
	            <span class="dashboard-kicker">Recent activity</span>
	            <h2>Recent Jobs</h2>
	          </div>
	        </div>
	        <div class="table-wrapper">
	          <% if (overview.getRecentJobs().isEmpty()) { %>
	            <div class="empty-state">No job posts available yet.</div>
	          <% } else { %>
	            <table class="admin-table">
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
	                    <td><strong><%= escapeHtml(row.getModuleCode()) %></strong></td>
	                    <td><%= escapeHtml(row.getJobTitle()) %></td>
	                    <td><span class="status-badge <%= jobBadgeClass(row.getStatus()) %>"><%= escapeHtml(DisplayFormatUtil.formatEnumName(row.getStatus())) %></span></td>
	                    <td><%= row.getFilledCount() %> / <%= row.getVacancies() %></td>
	                    <td><%= escapeHtml(row.getOwnerUsername()) %></td>
	                  </tr>
	                <% } %>
              </tbody>
            </table>
          <% } %>
        </div>
	      </div>

	      <div class="panel admin-table-panel">
	        <div class="section-header">
	          <div>
	            <span class="dashboard-kicker">Review queue</span>
	            <h2>Recent Applications</h2>
	          </div>
	        </div>
	        <div class="table-wrapper">
	          <% if (overview.getRecentApplications().isEmpty()) { %>
	            <div class="empty-state">No applications available yet.</div>
	          <% } else { %>
	            <table class="admin-table">
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
	                    <td><strong><%= escapeHtml(row.getApplicantUsername()) %></strong></td>
	                    <td><%= escapeHtml(row.getModuleCode()) %></td>
	                    <td><%= escapeHtml(row.getJobTitle()) %></td>
	                    <td><span class="status-badge <%= applicationBadgeClass(row.getStatus()) %>"><%= escapeHtml(DisplayFormatUtil.formatEnumName(row.getStatus())) %></span></td>
	                    <td><%= DisplayFormatUtil.formatDateTime(row.getUpdatedAt()) %></td>
	                  </tr>
                <% } %>
              </tbody>
            </table>
          <% } %>
	        </div>
	      </div>
	    </section>

	    <section class="page-grid two-col admin-content-grid admin-storage-workload-grid">
	      <div class="panel admin-table-panel admin-scroll-panel">
	        <div class="section-header">
	          <div>
	            <span class="dashboard-kicker">Workload</span>
	            <h2>Workload Overview</h2>
	          </div>
	        </div>
	        <div class="table-wrapper admin-scroll-body">
	          <% if (overview.getWorkloadRows().isEmpty()) { %>
	            <div class="empty-state">No workload records are available yet.</div>
	          <% } else { %>
	            <table id="workloadTable" class="admin-table">
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
	                    <td><strong><%= escapeHtml(row.getUsername()) %></strong></td>
	                    <td><%= row.getAssignedModules() %></td>
	                    <td><%= row.getWeeklyHours() %></td>
	                    <td><span class="status-badge <%= workloadBadgeClass(row.getWorkloadStatus()) %>"><%= escapeHtml(row.getWorkloadStatus()) %></span></td>
	                  </tr>
	                <% } %>
	              </tbody>
            </table>
          <% } %>
	        </div>
	      </div>

	      <div class="panel admin-record-panel admin-scroll-panel">
	        <div class="section-header">
	          <div>
	            <span class="dashboard-kicker">Storage</span>
	            <h2>System Records</h2>
	          </div>
	        </div>
	        <ul class="records-list admin-records admin-scroll-body" id="recordsList">
	          <% for (String record : overview.getRecordsList()) { %>
	            <li><%= escapeHtml(record) %></li>
	          <% } %>
	        </ul>
	      </div>
	    </section>
	  </div>
	</body>
	</html>
