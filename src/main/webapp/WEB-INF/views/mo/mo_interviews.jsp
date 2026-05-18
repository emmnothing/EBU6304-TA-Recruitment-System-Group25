<%@ page import="java.util.List" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="java.nio.charset.StandardCharsets" %>
<%@ page import="com.bupt.ta.dto.ApplicantFilter" %>
<%@ page import="com.bupt.ta.dto.ApplicantReviewItem" %>
<%@ page import="com.bupt.ta.model.JobPost" %>
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
ApplicantFilter applicantFilter = (ApplicantFilter) request.getAttribute("applicantFilter");
List<JobPost> jobOptions = (List<JobPost>) request.getAttribute("jobOptions");
List<ApplicantReviewItem> applicantList = (List<ApplicantReviewItem>) request.getAttribute("applicantList");
ApplicantReviewItem applicationDetail = (ApplicantReviewItem) request.getAttribute("applicationDetail");
String selectedApplicationId = (String) request.getAttribute("selectedApplicationId");

String currentJobId = applicantFilter == null || applicantFilter.getJobId() == null ? "" : applicantFilter.getJobId();
String currentStatus = applicantFilter == null || applicantFilter.getStatus() == null ? "" : applicantFilter.getStatus();
String currentKeyword = applicantFilter == null || applicantFilter.getKeyword() == null ? "" : applicantFilter.getKeyword();
String currentHasCv = applicantFilter == null || applicantFilter.getHasCv() == null ? "" : applicantFilter.getHasCv();
String currentSortBy = applicantFilter == null || applicantFilter.getSortBy() == null ? "" : applicantFilter.getSortBy();
String currentSortDirection = applicantFilter == null || applicantFilter.getSortDirection() == null ? "" : applicantFilter.getSortDirection();
String currentQueryBase =
    "jobId=" + encode(currentJobId)
    + "&status=" + encode(currentStatus)
    + "&keyword=" + encode(currentKeyword)
    + "&hasCv=" + encode(currentHasCv)
    + "&sortBy=" + encode(currentSortBy)
    + "&sortDirection=" + encode(currentSortDirection);
%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>TA Recruitment System - Interview Planning</title>
  <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/style.css?v=mo-interview-20260515">
</head>
<body class="app-page">
  <div class="app-shell">
    <%
    request.setAttribute("roleNavPage", "interviews");
    request.setAttribute("roleNavRoleLabel", "Module Organiser");
    request.setAttribute("roleNavTitle", "Interview Planning");
    request.setAttribute("roleNavSubtitle", "Schedule, revise, and review interview plans for your applicants, <strong>" + escapeHtml(currentUsername) + "</strong>.");
    request.setAttribute("roleNavNotificationKey", "notifications");
    request.setAttribute("roleNavItems", new String[][] {
        {"dashboard", "Dashboard", "/mo/dashboard"},
        {"jobs", "Post TA Job", "/mo/post-job"},
        {"applicants", "Applicants", "/mo/applicants"},
        {"interviews", "Interview", "/mo/interviews"},
        {"notifications", "Notifications", "/mo/notifications"}
    });
    %>
    <%@ include file="../shared/role_nav.jspf" %>

    <% if (flashMessage != null) { %>
      <div class="flash-message <%= flashType %>"><%= flashMessage %></div>
    <% } %>

    <div class="panel" style="margin-bottom: 22px;">
      <h2>Interview Queue Filter</h2>
      <form id="interviewFilterForm" method="get" action="<%= request.getContextPath() %>/mo/interviews">
        <div class="form-grid">
          <div class="field">
            <label for="jobSelect">Job</label>
            <select id="jobSelect" name="jobId">
              <option value="">All my jobs</option>
              <% for (JobPost job : jobOptions) { %>
                <option value="<%= escapeHtml(job.getJobId()) %>" <%= selected(job.getJobId().equals(currentJobId)) %>><%= escapeHtml(job.getModuleCode()) %> - <%= escapeHtml(job.getJobTitle()) %></option>
              <% } %>
            </select>
          </div>
          <div class="field">
            <label for="statusSelect">Status</label>
            <select id="statusSelect" name="status">
              <option value="">All interview-related</option>
              <option value="UNDER_REVIEW" <%= selected("UNDER_REVIEW".equalsIgnoreCase(currentStatus)) %>>Under Review</option>
              <option value="INTERVIEW_SCHEDULED" <%= selected("INTERVIEW_SCHEDULED".equalsIgnoreCase(currentStatus)) %>>Interview Scheduled</option>
            </select>
          </div>
          <div class="field">
            <label for="hasCvSelect">CV Uploaded</label>
            <select id="hasCvSelect" name="hasCv">
              <option value="">All</option>
              <option value="yes" <%= selected("yes".equalsIgnoreCase(currentHasCv)) %>>Yes</option>
              <option value="no" <%= selected("no".equalsIgnoreCase(currentHasCv)) %>>No</option>
            </select>
          </div>
          <div class="field">
            <label for="sortBySelect">Sort By</label>
            <select id="sortBySelect" name="sortBy">
              <option value="interviewAt" <%= selected("interviewAt".equalsIgnoreCase(currentSortBy)) %>>Interview Time</option>
              <option value="appliedAt" <%= selected(currentSortBy.isBlank() || "appliedAt".equalsIgnoreCase(currentSortBy)) %>>Applied Time</option>
              <option value="username" <%= selected("username".equalsIgnoreCase(currentSortBy)) %>>Applicant Name</option>
              <option value="status" <%= selected("status".equalsIgnoreCase(currentSortBy)) %>>Status</option>
            </select>
          </div>
          <div class="field">
            <label for="sortDirectionSelect">Sort Direction</label>
            <select id="sortDirectionSelect" name="sortDirection">
              <option value="asc" <%= selected("asc".equalsIgnoreCase(currentSortDirection)) %>>Ascending</option>
              <option value="desc" <%= selected(currentSortDirection.isBlank() || "desc".equalsIgnoreCase(currentSortDirection)) %>>Descending</option>
            </select>
          </div>
          <div class="field full-width">
            <label for="keywordInput">Keyword</label>
            <input type="text" id="keywordInput" name="keyword" placeholder="Username, student ID, module, job title" value="<%= escapeHtml(currentKeyword) %>">
          </div>
          <div class="field full-width actions">
            <button class="btn-secondary" type="submit">Refresh Queue</button>
          </div>
        </div>
      </form>
    </div>

    <div class="split-layout">
      <div class="detail-card">
        <h3>Interview Queue</h3>
        <% if (applicantList == null || applicantList.isEmpty()) { %>
          <div class="empty-state">No applicants match the current filter. Applicants marked for review or interview scheduling will appear here.</div>
        <% } else { %>
          <div class="table-wrapper">
            <table id="interviewQueueTable">
              <thead>
                <tr>
                  <th>Applicant</th>
                  <th>Job</th>
                  <th>Status</th>
                  <th>Interview</th>
                  <th>Action</th>
                </tr>
              </thead>
              <tbody>
                <% for (ApplicantReviewItem item : applicantList) { %>
                  <tr>
                    <td>
                      <strong><%= escapeHtml(item.getUsername()) %></strong><br>
                      <span class="list-item-sub"><%= escapeHtml(item.getEmail()) %></span>
                    </td>
                    <td><%= escapeHtml(item.getModuleCode()) %> - <%= escapeHtml(item.getJobTitle()) %></td>
                    <td><span class="status-badge <%= item.getStatus().getBadgeClass() %>"><%= item.getStatus().getDisplayName() %></span></td>
                    <td>
                      <% if (item.getInterviewScheduledAt() == null || item.getInterviewScheduledAt().isBlank()) { %>
                        -
                      <% } else { %>
                        <%= DisplayFormatUtil.formatDateTime(item.getInterviewScheduledAt()) %>
                      <% } %>
                    </td>
                    <td>
                      <a href="<%= request.getContextPath() %>/mo/interviews?<%= currentQueryBase %>&applicationId=<%= encode(item.getApplicationId()) %>">
                        <%= item.getApplicationId().equals(selectedApplicationId) ? "Viewing" : "Manage" %>
                      </a>
                    </td>
                  </tr>
                <% } %>
              </tbody>
            </table>
          </div>
        <% } %>
      </div>

      <div class="detail-card">
        <h3>Interview Detail</h3>
        <% if (applicationDetail == null) { %>
          <div class="empty-state">Select an applicant from the queue to set an interview date, revise interview details, or review the existing plan.</div>
        <% } else { %>
          <div class="detail-grid">
            <div><strong>Applicant</strong><%= escapeHtml(applicationDetail.getUsername()) %></div>
            <div><strong>Status</strong><%= applicationDetail.getStatus().getDisplayName() %></div>
            <div><strong>Email</strong><%= escapeHtml(applicationDetail.getEmail()) %></div>
            <div><strong>Phone</strong><%= escapeHtml(applicationDetail.getPhoneNumber()) %></div>
            <div><strong>Module</strong><%= escapeHtml(applicationDetail.getModuleCode()) %> - <%= escapeHtml(applicationDetail.getModuleName()) %></div>
            <div><strong>Applied At</strong><%= DisplayFormatUtil.formatDateTime(applicationDetail.getAppliedAt()) %></div>
          </div>

          <div style="margin-top: 18px;">
            <strong>Current Interview Plan</strong>
            <% if (applicationDetail.getInterviewScheduledAt() == null || applicationDetail.getInterviewScheduledAt().isBlank()) { %>
              <p>No interview has been scheduled yet.</p>
            <% } else { %>
              <p><strong>Time:</strong> <%= DisplayFormatUtil.formatDateTime(applicationDetail.getInterviewScheduledAt()) %></p>
              <p><strong>Mode:</strong> <%= applicationDetail.getInterviewMode() == null || applicationDetail.getInterviewMode().isBlank() ? "-" : escapeHtml(applicationDetail.getInterviewMode()) %></p>
              <p><strong>Location:</strong> <%= applicationDetail.getInterviewLocation() == null || applicationDetail.getInterviewLocation().isBlank() ? "-" : escapeHtml(applicationDetail.getInterviewLocation()) %></p>
              <p><strong>Notes:</strong> <%= applicationDetail.getInterviewNotes() == null || applicationDetail.getInterviewNotes().isBlank() ? "-" : escapeHtml(applicationDetail.getInterviewNotes()) %></p>
            <% } %>
          </div>

          <form id="interviewForm" method="post" action="<%= request.getContextPath() %>/mo/interviews" style="margin-top: 18px;">
            <input type="hidden" name="applicationId" value="<%= escapeHtml(applicationDetail.getApplicationId()) %>">
            <input type="hidden" name="jobId" value="<%= escapeHtml(currentJobId) %>">
            <input type="hidden" name="status" value="<%= escapeHtml(currentStatus) %>">
            <input type="hidden" name="keyword" value="<%= escapeHtml(currentKeyword) %>">
            <input type="hidden" name="hasCv" value="<%= escapeHtml(currentHasCv) %>">
            <input type="hidden" name="sortBy" value="<%= escapeHtml(currentSortBy) %>">
            <input type="hidden" name="sortDirection" value="<%= escapeHtml(currentSortDirection) %>">
            <div class="form-grid">
              <div class="field">
                <label for="interviewScheduledAtInput">Interview Date & Time</label>
                <input type="datetime-local" id="interviewScheduledAtInput" name="interviewScheduledAt" value="<%= applicationDetail.getInterviewScheduledAt() == null ? "" : escapeHtml(applicationDetail.getInterviewScheduledAt()) %>" required>
              </div>
              <div class="field">
                <label for="interviewModeInput">Mode</label>
                <input type="text" id="interviewModeInput" name="interviewMode" placeholder="Online / On campus / Hybrid" value="<%= applicationDetail.getInterviewMode() == null ? "" : escapeHtml(applicationDetail.getInterviewMode()) %>">
              </div>
              <div class="field full-width">
                <label for="interviewLocationInput">Location / Meeting Link</label>
                <input type="text" id="interviewLocationInput" name="interviewLocation" placeholder="Room name or meeting link" value="<%= applicationDetail.getInterviewLocation() == null ? "" : escapeHtml(applicationDetail.getInterviewLocation()) %>">
              </div>
              <div class="field full-width">
                <label for="interviewNotesInput">Interview Notes</label>
                <textarea id="interviewNotesInput" name="interviewNotes" placeholder="Optional instructions or interview note"><%= applicationDetail.getInterviewNotes() == null ? "" : escapeHtml(applicationDetail.getInterviewNotes()) %></textarea>
              </div>
              <div class="field full-width actions">
                <button class="btn-secondary" type="submit">Save Interview Plan</button>
                <a class="btn-ghost" href="<%= request.getContextPath() %>/mo/applicants?<%= currentQueryBase %>&applicationId=<%= encode(applicationDetail.getApplicationId()) %>">Back to Applicant Detail</a>
              </div>
            </div>
          </form>
        <% } %>
      </div>
    </div>
  </div>
</body>
</html>
