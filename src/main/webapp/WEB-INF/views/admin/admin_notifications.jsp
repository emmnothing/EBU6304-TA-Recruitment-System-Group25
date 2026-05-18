<%@ page import="java.util.List" %>
<%@ page import="com.bupt.ta.dto.AdminAnnouncementRecipientSummary" %>
<%@ page import="com.bupt.ta.dto.AdminAnnouncementRecord" %>
<%@ page import="com.bupt.ta.service.NotificationService" %>
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
String flashType = (String) request.getAttribute("flashType");
String flashMessage = (String) request.getAttribute("flashMessage");
String currentUsername = (String) request.getAttribute("currentUsername");
AdminAnnouncementRecipientSummary recipientSummary = (AdminAnnouncementRecipientSummary) request.getAttribute("recipientSummary");
List<AdminAnnouncementRecord> recentAnnouncements = (List<AdminAnnouncementRecord>) request.getAttribute("recentAnnouncements");

int allRecipientCount = recipientSummary == null ? 0 : recipientSummary.getAllRecipientCount();
int applicantCount = recipientSummary == null ? 0 : recipientSummary.getApplicantCount();
int moduleOrganiserCount = recipientSummary == null ? 0 : recipientSummary.getModuleOrganiserCount();
%>
<!DOCTYPE html>
<html lang="en">
<head>
	  <meta charset="UTF-8">
	  <meta name="viewport" content="width=device-width, initial-scale=1.0">
	  <title>TA Recruitment System - System Announcements</title>
	  <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/style.css?v=admin-polish-20260518">
	</head>
	<body class="app-page admin-page">
  <div class="app-shell">
    <%
    request.setAttribute("roleNavPage", "notifications");
    request.setAttribute("roleNavRoleLabel", "Administrator");
    request.setAttribute("roleNavTitle", "System Announcements");
    request.setAttribute("roleNavSubtitle", "Send platform notices to applicants and module organisers, <strong>" + escapeHtml(currentUsername) + "</strong>.");
    request.setAttribute("roleNavItems", new String[][] {
        {"dashboard", "Dashboard", "/admin/dashboard"},
        {"users", "User Management", "/admin/users"},
        {"notifications", "Announcements", "/admin/notifications"},
        {"exportUsers", "Export Users", "/admin/export?type=users"},
        {"exportApplications", "Export Applications", "/admin/export?type=applications"}
    });
    %>
    <%@ include file="../shared/role_nav.jspf" %>

    <% if (flashMessage != null) { %>
      <div class="flash-message <%= flashType %>"><%= escapeHtml(flashMessage) %></div>
    <% } %>

	    <section class="stats-grid three admin-stats" aria-label="Announcement recipient counts">
	      <div class="summary-card">
	        <div class="number"><%= allRecipientCount %></div>
        <div class="label">All recipients</div>
      </div>
      <div class="summary-card">
        <div class="number"><%= applicantCount %></div>
        <div class="label">TA applicants</div>
      </div>
      <div class="summary-card">
	        <div class="number"><%= moduleOrganiserCount %></div>
	        <div class="label">Module organisers</div>
	      </div>
	    </section>

	    <section class="page-grid two-col admin-compose-layout">
	      <div class="panel admin-compose-panel">
	        <div class="section-header">
	          <div>
	            <span class="dashboard-kicker">Broadcast</span>
	            <h2>Create Announcement</h2>
	          </div>
	        </div>

	        <%-- The POST action is handled by AdminNotificationManagementServlet.doPost(). --%>
	        <form method="post" action="<%= request.getContextPath() %>/admin/notifications/send">
          <div class="form-grid">
            <div class="field full-width">
              <label for="audienceSelect">Audience</label>
              <select id="audienceSelect" name="audience" required>
                <option value="<%= NotificationService.AUDIENCE_ALL %>">TA applicants and module organisers (<%= allRecipientCount %>)</option>
                <option value="<%= NotificationService.AUDIENCE_TA_APPLICANTS %>">TA applicants only (<%= applicantCount %>)</option>
                <option value="<%= NotificationService.AUDIENCE_MODULE_ORGANISERS %>">Module organisers only (<%= moduleOrganiserCount %>)</option>
              </select>
            </div>

            <div class="field full-width">
              <label for="titleInput">Title</label>
              <input type="text" id="titleInput" name="title" maxlength="120" placeholder="Application deadline reminder" required>
            </div>

            <div class="field full-width">
              <label for="messageInput">Message</label>
              <textarea id="messageInput" name="message" maxlength="1000" placeholder="Write the announcement content here." required></textarea>
              <div class="hint">Messages are delivered to each recipient's notification page.</div>
            </div>

            <div class="field full-width actions">
              <button class="btn-primary" type="submit">Send Announcement</button>
            </div>
	          </div>
	        </form>
	      </div>

	      <div class="panel admin-rules-panel">
	        <span class="dashboard-kicker">Delivery rules</span>
	        <h2>Audience and Read Status</h2>
	        <ul class="records-list">
	          <li>Only active TA applicants and active module organisers are included.</li>
	          <li>Every recipient receives an individual notification with its own read status.</li>
	          <li>Announcements are grouped by one shared announcement ID in the history below.</li>
	          <li>Expired notification rows are pruned lazily when notification features are used.</li>
	        </ul>
	      </div>
	    </section>

	    <section class="panel admin-table-panel">
	      <div class="section-header">
	        <div>
	          <span class="dashboard-kicker">History</span>
	          <h2>Recent Announcements</h2>
	        </div>
	      </div>
	      <% if (recentAnnouncements == null || recentAnnouncements.isEmpty()) { %>
	        <div class="empty-state">No system announcements have been sent yet.</div>
	      <% } else { %>
	        <div class="table-wrapper">
	          <table class="admin-table">
            <thead>
              <tr>
                <th>Announcement</th>
                <th>Audience</th>
                <th>Sent By</th>
                <th>Sent At</th>
                <th>Delivery</th>
              </tr>
            </thead>
            <tbody>
              <% for (AdminAnnouncementRecord record : recentAnnouncements) { %>
                <tr>
                  <td>
                    <strong><%= escapeHtml(record.getTitle()) %></strong>
                    <div class="announcement-body"><%= escapeHtml(record.getMessage()) %></div>
                    <div class="list-item-sub"><%= escapeHtml(record.getAnnouncementId()) %></div>
                  </td>
                  <td><%= escapeHtml(record.getAudienceLabel()) %></td>
                  <td><%= escapeHtml(record.getSentByUsername()) %></td>
                  <td><%= DisplayFormatUtil.formatDateTime(record.getCreatedAt()) %></td>
                  <td>
                    <span class="status-badge active"><%= record.getRecipientCount() %> sent</span>
                    <div class="list-item-sub"><%= record.getReadCount() %> read / <%= record.getUnreadCount() %> unread</div>
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
