<%@ page import="java.util.List" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="java.nio.charset.StandardCharsets" %>
<%@ page import="com.bupt.ta.dto.AdminUserFilter" %>
<%@ page import="com.bupt.ta.dto.AdminUserManagementView" %>
<%@ page import="com.bupt.ta.dto.AdminUserRecord" %>
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

private String encode(String value) {
    return URLEncoder.encode(value == null ? "" : value, StandardCharsets.UTF_8);
}

private String selected(boolean condition) {
    return condition ? "selected" : "";
}
%>
<%
String flashType = (String) request.getAttribute("flashType");
String flashMessage = (String) request.getAttribute("flashMessage");
String currentUsername = (String) request.getAttribute("currentUsername");
String defaultResetPassword = (String) request.getAttribute("defaultResetPassword");
AdminUserFilter userFilter = (AdminUserFilter) request.getAttribute("userFilter");
AdminUserManagementView userManagementView = (AdminUserManagementView) request.getAttribute("userManagementView");
List<AdminUserRecord> userRecords = userManagementView == null ? List.of() : userManagementView.getUserRecords();

String currentRole = userFilter == null || userFilter.getRole() == null ? "" : userFilter.getRole();
String currentStatus = userFilter == null || userFilter.getStatus() == null ? "" : userFilter.getStatus();
String currentKeyword = userFilter == null || userFilter.getKeyword() == null ? "" : userFilter.getKeyword();
String currentSortBy = userFilter == null || userFilter.getSortBy() == null ? "" : userFilter.getSortBy();
String currentSortDirection = userFilter == null || userFilter.getSortDirection() == null ? "" : userFilter.getSortDirection();
%>
<!DOCTYPE html>
<html lang="en">
<head>
	  <meta charset="UTF-8">
	  <meta name="viewport" content="width=device-width, initial-scale=1.0">
	  <title>TA Recruitment System - User Management</title>
	  <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/style.css?v=admin-polish-20260518">
	</head>
	<body class="app-page admin-page">
	  <div class="app-shell">
    <%
    request.setAttribute("roleNavPage", "users");
    request.setAttribute("roleNavRoleLabel", "Administrator");
    request.setAttribute("roleNavTitle", "User Management");
    request.setAttribute("roleNavSubtitle", "Filter accounts, reset passwords, and control who can access the platform, <strong>" + escapeHtml(currentUsername) + "</strong>.");
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

	    <section class="stats-grid four admin-stats" aria-label="User management metrics">
	      <div class="summary-card">
	        <div class="number"><%= userManagementView == null ? 0 : userManagementView.getTotalUsers() %></div>
        <div class="label">Total users</div>
      </div>
      <div class="summary-card">
        <div class="number"><%= userManagementView == null ? 0 : userManagementView.getActiveUsers() %></div>
        <div class="label">Active users</div>
      </div>
      <div class="summary-card">
        <div class="number"><%= userManagementView == null ? 0 : userManagementView.getInactiveUsers() %></div>
        <div class="label">Inactive users</div>
      </div>
      <div class="summary-card">
        <div class="number"><%= userManagementView == null ? 0 : userManagementView.getApplicantCount() %></div>
        <div class="label">TA applicants</div>
      </div>
      <div class="summary-card">
        <div class="number"><%= userManagementView == null ? 0 : userManagementView.getModuleOrganiserCount() %></div>
        <div class="label">Module organisers</div>
      </div>
      <div class="summary-card">
	        <div class="number"><%= userManagementView == null ? 0 : userManagementView.getAdministratorCount() %></div>
	        <div class="label">Administrators</div>
	      </div>
	    </section>

	    <section class="panel admin-directory-panel admin-user-directory-panel">
	      <div class="section-header">
	        <div>
	          <span class="dashboard-kicker">Directory controls</span>
	          <h2>User Filters</h2>
	          <p class="hint">Filter the directory below without leaving the account list context.</p>
	        </div>
	      </div>
	      <form method="get" action="<%= request.getContextPath() %>/admin/users">
	        <div class="form-grid">
          <div class="field">
            <label for="roleSelect">Role</label>
            <select id="roleSelect" name="role">
              <option value="">All roles</option>
              <option value="TA_APPLICANT" <%= selected("TA_APPLICANT".equalsIgnoreCase(currentRole)) %>>TA Applicant</option>
              <option value="MODULE_ORGANISER" <%= selected("MODULE_ORGANISER".equalsIgnoreCase(currentRole)) %>>Module Organiser</option>
              <option value="ADMINISTRATOR" <%= selected("ADMINISTRATOR".equalsIgnoreCase(currentRole)) %>>Administrator</option>
            </select>
          </div>
          <div class="field">
            <label for="statusSelect">Status</label>
            <select id="statusSelect" name="status">
              <option value="">All statuses</option>
              <option value="active" <%= selected("active".equalsIgnoreCase(currentStatus)) %>>Active</option>
              <option value="inactive" <%= selected("inactive".equalsIgnoreCase(currentStatus)) %>>Inactive</option>
            </select>
          </div>
          <div class="field">
            <label for="sortBySelect">Sort By</label>
            <select id="sortBySelect" name="sortBy">
              <option value="createdAt" <%= selected(currentSortBy.isBlank() || "createdAt".equalsIgnoreCase(currentSortBy)) %>>Created Time</option>
              <option value="username" <%= selected("username".equalsIgnoreCase(currentSortBy)) %>>Username</option>
              <option value="role" <%= selected("role".equalsIgnoreCase(currentSortBy)) %>>Role</option>
              <option value="status" <%= selected("status".equalsIgnoreCase(currentSortBy)) %>>Status</option>
              <option value="statusUpdatedAt" <%= selected("statusUpdatedAt".equalsIgnoreCase(currentSortBy)) %>>Status Updated Time</option>
            </select>
          </div>
          <div class="field">
            <label for="sortDirectionSelect">Sort Direction</label>
            <select id="sortDirectionSelect" name="sortDirection">
              <option value="desc" <%= selected(currentSortDirection.isBlank() || "desc".equalsIgnoreCase(currentSortDirection)) %>>Descending</option>
              <option value="asc" <%= selected("asc".equalsIgnoreCase(currentSortDirection)) %>>Ascending</option>
            </select>
          </div>
          <div class="field full-width">
            <label for="keywordInput">Keyword</label>
            <input type="text" id="keywordInput" name="keyword" value="<%= escapeHtml(currentKeyword) %>" placeholder="Search by username, email, phone, role, or user ID">
          </div>
          <div class="field full-width actions">
            <button class="btn-secondary" type="submit">Search</button>
            <a class="btn-ghost" href="<%= request.getContextPath() %>/admin/users">Clear</a>
	          </div>
	        </div>
	      </form>

	      <div class="section-header admin-directory-heading">
	        <div>
	          <span class="dashboard-kicker">Accounts</span>
	          <h2>User Directory</h2>
	          <p class="hint">Password reset currently sets the selected account back to <strong><%= escapeHtml(defaultResetPassword) %></strong>.</p>
	        </div>
      </div>

      <% if (userRecords == null || userRecords.isEmpty()) { %>
	        <div class="empty-state">No user accounts match the current filter.</div>
	      <% } else { %>
	        <div class="table-wrapper">
	          <table class="admin-table">
	            <thead>
              <tr>
                <th>User</th>
                <th>Role</th>
                <th>Contact</th>
                <th>Status</th>
                <th>Created</th>
                <th>Activity</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              <% for (AdminUserRecord record : userRecords) { %>
                <tr>
                  <td>
                    <strong><%= escapeHtml(record.getUsername()) %></strong>
                    <% if (record.isCurrentUser()) { %>
                      <span class="list-item-sub">(You)</span>
                    <% } %>
                    <br>
                    <span class="list-item-sub"><%= escapeHtml(record.getUserId()) %></span>
                  </td>
	                  <td><span class="role-chip"><%= record.getRole() == null ? "-" : escapeHtml(record.getRole().getDisplayName()) %></span></td>
                  <td>
                    <%= escapeHtml(record.getEmail()) %><br>
                    <span class="list-item-sub"><%= escapeHtml(record.getPhoneNumber()) %></span>
                  </td>
                  <td>
                    <span class="status-badge <%= record.isActive() ? "active" : "inactive" %>"><%= record.isActive() ? "Active" : "Inactive" %></span><br>
                    <span class="list-item-sub"><%= record.getStatusUpdatedAt() == null || record.getStatusUpdatedAt().isBlank() ? "No status change yet." : DisplayFormatUtil.formatDateTime(record.getStatusUpdatedAt()) %></span>
                  </td>
                  <td><%= DisplayFormatUtil.formatDateTime(record.getCreatedAt()) %></td>
                  <td><%= escapeHtml(record.getActivitySummary()) %></td>
                  <td>
                    <div class="table-actions">
                      <form method="post" action="<%= request.getContextPath() %>/admin/users/toggle-status">
                        <input type="hidden" name="userId" value="<%= escapeHtml(record.getUserId()) %>">
                        <input type="hidden" name="role" value="<%= escapeHtml(currentRole) %>">
                        <input type="hidden" name="status" value="<%= escapeHtml(currentStatus) %>">
                        <input type="hidden" name="keyword" value="<%= escapeHtml(currentKeyword) %>">
                        <input type="hidden" name="sortBy" value="<%= escapeHtml(currentSortBy) %>">
                        <input type="hidden" name="sortDirection" value="<%= escapeHtml(currentSortDirection) %>">
                        <button class="btn-secondary" type="submit" <%= record.isCurrentUser() ? "disabled" : "" %>><%= record.isActive() ? "Disable" : "Enable" %></button>
                      </form>
                      <form method="post" action="<%= request.getContextPath() %>/admin/users/reset-password">
                        <input type="hidden" name="userId" value="<%= escapeHtml(record.getUserId()) %>">
                        <input type="hidden" name="role" value="<%= escapeHtml(currentRole) %>">
                        <input type="hidden" name="status" value="<%= escapeHtml(currentStatus) %>">
                        <input type="hidden" name="keyword" value="<%= escapeHtml(currentKeyword) %>">
                        <input type="hidden" name="sortBy" value="<%= escapeHtml(currentSortBy) %>">
                        <input type="hidden" name="sortDirection" value="<%= escapeHtml(currentSortDirection) %>">
                        <button class="btn-ghost" type="submit">Reset Password</button>
                      </form>
                    </div>
                  </td>
                </tr>
              <% } %>
            </tbody>
          </table>
        </div>
      <% } %>
	    </section>
	  </div>
	</body>
	</html>
