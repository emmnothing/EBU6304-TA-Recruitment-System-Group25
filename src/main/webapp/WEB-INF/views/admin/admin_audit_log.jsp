<%@ page import="java.util.List" %>
<%@ page import="com.bupt.ta.dto.AdminAuditLogFilter" %>
<%@ page import="com.bupt.ta.dto.AdminAuditLogView" %>
<%@ page import="com.bupt.ta.model.AuditLogEntry" %>
<%@ page import="com.bupt.ta.service.AuditLogService" %>
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

private String selected(boolean condition) {
    return condition ? "selected" : "";
}

private String outcomeBadgeClass(String outcome) {
    return AuditLogService.OUTCOME_SUCCESS.equalsIgnoreCase(outcome) ? "active" : "inactive";
}
%>
<%
String currentUsername = (String) request.getAttribute("currentUsername");
AdminAuditLogFilter auditLogFilter = (AdminAuditLogFilter) request.getAttribute("auditLogFilter");
AdminAuditLogView auditLogView = (AdminAuditLogView) request.getAttribute("auditLogView");
List<AuditLogEntry> auditLogs = auditLogView == null ? List.of() : auditLogView.getAuditLogs();

String currentAction = auditLogFilter == null || auditLogFilter.getAction() == null ? "" : auditLogFilter.getAction();
String currentOutcome = auditLogFilter == null || auditLogFilter.getOutcome() == null ? "" : auditLogFilter.getOutcome();
String currentKeyword = auditLogFilter == null || auditLogFilter.getKeyword() == null ? "" : auditLogFilter.getKeyword();
String currentSortDirection = auditLogFilter == null || auditLogFilter.getSortDirection() == null ? "" : auditLogFilter.getSortDirection();
%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>TA Recruitment System - Audit Log</title>
  <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/style.css?v=admin-audit-log-20260519">
</head>
<body class="app-page admin-page">
  <div class="app-shell">
    <%
    request.setAttribute("roleNavPage", "auditLog");
    request.setAttribute("roleNavRoleLabel", "Administrator");
    request.setAttribute("roleNavTitle", "Audit Log");
    request.setAttribute("roleNavSubtitle", "Review security-sensitive administrator activity, <strong>" + escapeHtml(currentUsername) + "</strong>.");
    request.setAttribute("roleNavItems", new String[][] {
        {"dashboard", "Dashboard", "/admin/dashboard"},
        {"users", "User Management", "/admin/users"},
        {"notifications", "Announcements", "/admin/notifications"},
        {"auditLog", "Audit Log", "/admin/audit-log"},
        {"exportUsers", "Export Users", "/admin/export?type=users"},
        {"exportApplications", "Export Applications", "/admin/export?type=applications"}
    });
    %>
    <%@ include file="../shared/role_nav.jspf" %>

    <section class="stats-grid five admin-stats" aria-label="Audit log metrics">
      <div class="summary-card">
        <div class="number"><%= auditLogView == null ? 0 : auditLogView.getTotalLogs() %></div>
        <div class="label">Total events</div>
      </div>
      <div class="summary-card">
        <div class="number"><%= auditLogView == null ? 0 : auditLogView.getSuccessfulLogs() %></div>
        <div class="label">Successful events</div>
      </div>
      <div class="summary-card">
        <div class="number"><%= auditLogView == null ? 0 : auditLogView.getFailedLogs() %></div>
        <div class="label">Failed attempts</div>
      </div>
      <div class="summary-card">
        <div class="number"><%= auditLogView == null ? 0 : auditLogView.getExportLogs() %></div>
        <div class="label">Export events</div>
      </div>
      <div class="summary-card">
        <div class="number"><%= auditLogView == null ? 0 : auditLogView.getAccountChangeLogs() %></div>
        <div class="label">Account changes</div>
      </div>
    </section>

    <section class="panel admin-filter-panel">
      <div class="section-header">
        <div>
          <span class="dashboard-kicker">Traceability</span>
          <h2>Audit Filters</h2>
        </div>
      </div>
      <form method="get" action="<%= request.getContextPath() %>/admin/audit-log">
        <div class="form-grid">
          <div class="field">
            <label for="actionSelect">Action</label>
            <select id="actionSelect" name="action">
              <option value="">All actions</option>
              <option value="<%= AuditLogService.ACTION_USER_STATUS_CHANGED %>" <%= selected(AuditLogService.ACTION_USER_STATUS_CHANGED.equalsIgnoreCase(currentAction)) %>>User status changed</option>
              <option value="<%= AuditLogService.ACTION_PASSWORD_RESET %>" <%= selected(AuditLogService.ACTION_PASSWORD_RESET.equalsIgnoreCase(currentAction)) %>>Password reset</option>
              <option value="<%= AuditLogService.ACTION_SYSTEM_ANNOUNCEMENT_SENT %>" <%= selected(AuditLogService.ACTION_SYSTEM_ANNOUNCEMENT_SENT.equalsIgnoreCase(currentAction)) %>>System announcement sent</option>
              <option value="<%= AuditLogService.ACTION_CSV_EXPORT_DOWNLOADED %>" <%= selected(AuditLogService.ACTION_CSV_EXPORT_DOWNLOADED.equalsIgnoreCase(currentAction)) %>>CSV export downloaded</option>
              <option value="<%= AuditLogService.ACTION_CSV_EXPORT_REJECTED %>" <%= selected(AuditLogService.ACTION_CSV_EXPORT_REJECTED.equalsIgnoreCase(currentAction)) %>>CSV export rejected</option>
            </select>
          </div>
          <div class="field">
            <label for="outcomeSelect">Outcome</label>
            <select id="outcomeSelect" name="outcome">
              <option value="">All outcomes</option>
              <option value="<%= AuditLogService.OUTCOME_SUCCESS %>" <%= selected(AuditLogService.OUTCOME_SUCCESS.equalsIgnoreCase(currentOutcome)) %>>Success</option>
              <option value="<%= AuditLogService.OUTCOME_FAILURE %>" <%= selected(AuditLogService.OUTCOME_FAILURE.equalsIgnoreCase(currentOutcome)) %>>Failure</option>
            </select>
          </div>
          <div class="field">
            <label for="sortDirectionSelect">Sort Direction</label>
            <select id="sortDirectionSelect" name="sortDirection">
              <option value="desc" <%= selected(currentSortDirection.isBlank() || "desc".equalsIgnoreCase(currentSortDirection)) %>>Newest first</option>
              <option value="asc" <%= selected("asc".equalsIgnoreCase(currentSortDirection)) %>>Oldest first</option>
            </select>
          </div>
          <div class="field">
            <label for="keywordInput">Keyword</label>
            <input type="text" id="keywordInput" name="keyword" value="<%= escapeHtml(currentKeyword) %>" placeholder="Search actor, action, target, IP, or detail">
          </div>
          <div class="field full-width actions">
            <button class="btn-secondary" type="submit">Search</button>
            <a class="btn-ghost" href="<%= request.getContextPath() %>/admin/audit-log">Clear</a>
          </div>
        </div>
      </form>
    </section>

    <section class="panel admin-table-panel">
      <div class="section-header">
        <div>
          <span class="dashboard-kicker">Events</span>
          <h2>Audit Events</h2>
          <p class="hint">Showing <%= auditLogView == null ? 0 : auditLogView.getDisplayedLogs() %> most relevant event(s). The full record is stored in <strong>storage/json/auditLogs.json</strong>.</p>
        </div>
      </div>

      <% if (auditLogs == null || auditLogs.isEmpty()) { %>
        <div class="empty-state">No audit events match the current filter.</div>
      <% } else { %>
        <div class="table-wrapper">
          <table class="admin-table audit-log-table">
            <thead>
              <tr>
                <th>Time</th>
                <th>Actor</th>
                <th>Action</th>
                <th>Target</th>
                <th>Outcome</th>
                <th>Request</th>
                <th>Detail</th>
              </tr>
            </thead>
            <tbody>
              <% for (AuditLogEntry auditLog : auditLogs) { %>
                <tr>
                  <td>
                    <strong><%= DisplayFormatUtil.formatDateTime(auditLog.getCreatedAt()) %></strong>
                    <div class="list-item-sub"><%= escapeHtml(auditLog.getAuditLogId()) %></div>
                  </td>
                  <td>
                    <strong><%= escapeHtml(auditLog.getActorUsername()) %></strong>
                    <div class="list-item-sub"><%= escapeHtml(auditLog.getActorUserId()) %></div>
                    <div class="list-item-sub"><%= escapeHtml(DisplayFormatUtil.formatEnumName(auditLog.getActorRole())) %></div>
                  </td>
                  <td><span class="role-chip"><%= escapeHtml(DisplayFormatUtil.formatEnumName(auditLog.getAction())) %></span></td>
                  <td>
                    <strong><%= escapeHtml(auditLog.getTargetLabel()) %></strong>
                    <div class="list-item-sub"><%= escapeHtml(auditLog.getTargetType()) %> / <%= escapeHtml(auditLog.getTargetId()) %></div>
                  </td>
                  <td><span class="status-badge <%= outcomeBadgeClass(auditLog.getOutcome()) %>"><%= escapeHtml(DisplayFormatUtil.formatEnumName(auditLog.getOutcome())) %></span></td>
                  <td>
                    <strong><%= escapeHtml(auditLog.getRequestMethod()) %></strong>
                    <div class="list-item-sub"><%= escapeHtml(auditLog.getRequestPath()) %></div>
                    <div class="list-item-sub">IP: <%= escapeHtml(auditLog.getIpAddress()) %></div>
                  </td>
                  <td>
                    <div class="audit-detail"><%= escapeHtml(auditLog.getDetail()) %></div>
                    <div class="list-item-sub"><%= escapeHtml(auditLog.getUserAgent()) %></div>
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
