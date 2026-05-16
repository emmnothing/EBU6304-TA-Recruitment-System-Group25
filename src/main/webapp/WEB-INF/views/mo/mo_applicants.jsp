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
Integer unreadNotificationCount = (Integer) request.getAttribute("unreadNotificationCount");
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
  <title>TA Recruitment System - Applicants Review</title>
  <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/style.css?v=role-nav-20260513">
</head>
<body class="app-page">
  <div class="app-shell">
    <%
    request.setAttribute("roleNavPage", "applicants");
    request.setAttribute("roleNavRoleLabel", "Module Organiser");
    request.setAttribute("roleNavTitle", "Applicants Review");
    request.setAttribute("roleNavSubtitle", "Filter submissions, review applicant details, and process decisions for your TA jobs, <strong>" + escapeHtml(currentUsername) + "</strong>.");
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
      <h2>Applicant Filter</h2>
      <form id="applicantFilterForm" method="get" action="<%= request.getContextPath() %>/mo/applicants">
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
              <option value="">All</option>
              <option value="APPLIED" <%= selected("APPLIED".equalsIgnoreCase(currentStatus)) %>>Applied</option>
              <option value="UNDER_REVIEW" <%= selected("UNDER_REVIEW".equalsIgnoreCase(currentStatus)) %>>Under Review</option>
              <option value="INTERVIEW_SCHEDULED" <%= selected("INTERVIEW_SCHEDULED".equalsIgnoreCase(currentStatus)) %>>Interview Scheduled</option>
              <option value="SELECTED" <%= selected("SELECTED".equalsIgnoreCase(currentStatus)) %>>Selected</option>
              <option value="REJECTED" <%= selected("REJECTED".equalsIgnoreCase(currentStatus)) %>>Rejected</option>
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
              <option value="appliedAt" <%= selected(currentSortBy.isBlank() || "appliedAt".equalsIgnoreCase(currentSortBy)) %>>Applied Time</option>
              <option value="username" <%= selected("username".equalsIgnoreCase(currentSortBy)) %>>Applicant Name</option>
              <option value="status" <%= selected("status".equalsIgnoreCase(currentSortBy)) %>>Status</option>
              <option value="interviewAt" <%= selected("interviewAt".equalsIgnoreCase(currentSortBy)) %>>Interview Time</option>
              <option value="reviewedAt" <%= selected("reviewedAt".equalsIgnoreCase(currentSortBy)) %>>Reviewed Time</option>
              <option value="studentId" <%= selected("studentId".equalsIgnoreCase(currentSortBy)) %>>Student ID</option>
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
            <input type="text" id="keywordInput" name="keyword" placeholder="Username, email, student ID, programme, module, skills" value="<%= escapeHtml(currentKeyword) %>">
          </div>
          <div class="field full-width actions">
            <button class="btn-secondary" type="submit">Search</button>
            <button class="btn-ghost" type="submit" formaction="<%= request.getContextPath() %>/mo/applicants/export">Export CSV</button>
          </div>
        </div>
      </form>
    </div>

    <div class="split-layout">
      <div class="detail-card">
        <h3>Applicant List & Batch Actions</h3>
        <% if (applicantList == null || applicantList.isEmpty()) { %>
          <div class="empty-state">No applicants match the current filter. After a TA applicant submits an application, it will appear here.</div>
        <% } else { %>
          <form id="batchDecisionForm" method="post" action="<%= request.getContextPath() %>/mo/applicants/batch">
            <input type="hidden" name="jobId" value="<%= escapeHtml(currentJobId) %>">
            <input type="hidden" name="status" value="<%= escapeHtml(currentStatus) %>">
            <input type="hidden" name="keyword" value="<%= escapeHtml(currentKeyword) %>">
            <input type="hidden" name="hasCv" value="<%= escapeHtml(currentHasCv) %>">
            <input type="hidden" name="sortBy" value="<%= escapeHtml(currentSortBy) %>">
            <input type="hidden" name="sortDirection" value="<%= escapeHtml(currentSortDirection) %>">
            <input type="hidden" name="selectedApplicationId" value="<%= escapeHtml(selectedApplicationId == null ? "" : selectedApplicationId) %>">

            <div class="table-wrapper">
              <table id="applicantTable">
                <thead>
                  <tr>
                    <th>Select</th>
                    <th>Applicant</th>
                    <th>Job</th>
                    <th>Status</th>
                    <th>Applied</th>
                    <th>Interview</th>
                    <th>CV</th>
                    <th>Action</th>
                  </tr>
                </thead>
                <tbody>
                  <% for (ApplicantReviewItem item : applicantList) { %>
                    <tr>
                      <td><input type="checkbox" name="applicationIds" value="<%= escapeHtml(item.getApplicationId()) %>"></td>
                      <td>
                        <strong><%= escapeHtml(item.getUsername()) %></strong><br>
                        <span class="list-item-sub"><%= escapeHtml(item.getEmail()) %></span>
                      </td>
                      <td><%= escapeHtml(item.getModuleCode()) %> - <%= escapeHtml(item.getJobTitle()) %></td>
                      <td><span class="status-badge <%= item.getStatus().getBadgeClass() %>"><%= item.getStatus().getDisplayName() %></span></td>
                      <td><%= DisplayFormatUtil.formatDateTime(item.getAppliedAt()) %></td>
                      <td>
                        <% if (item.getInterviewScheduledAt() == null || item.getInterviewScheduledAt().isBlank()) { %>
                          -
                        <% } else { %>
                          <%= DisplayFormatUtil.formatDateTime(item.getInterviewScheduledAt()) %>
                        <% } %>
                      </td>
                      <td><%= item.getCvRelativePath() == null || item.getCvRelativePath().isBlank() ? "No" : "Yes" %></td>
                      <td>
                        <a href="<%= request.getContextPath() %>/mo/applicants?<%= currentQueryBase %>&applicationId=<%= encode(item.getApplicationId()) %>">
                          <%= item.getApplicationId().equals(selectedApplicationId) ? "Viewing" : "Open" %>
                        </a>
                      </td>
                    </tr>
                  <% } %>
                </tbody>
              </table>
            </div>

            <div style="margin-top: 18px;">
              <strong>Batch Remarks</strong>
              <div class="hint">These remarks will be applied to all selected applications if the batch action succeeds.</div>
              <div class="field" style="margin-top: 10px;">
                <textarea id="batchRemarksInput" name="remarks" placeholder="Optional batch note"></textarea>
              </div>
            </div>

            <div class="decision-actions">
              <button class="btn-secondary" type="submit" name="decision" value="UNDER_REVIEW">Batch Under Review</button>
              <button class="btn-secondary" type="submit" name="decision" value="SELECTED">Batch Select</button>
              <button class="btn-ghost" type="submit" name="decision" value="REJECTED">Batch Reject</button>
            </div>
          </form>
        <% } %>
      </div>

      <div class="detail-card">
        <h3>Applicant Detail</h3>
        <% if (applicationDetail == null) { %>
          <div class="empty-state">Select an applicant from the list to view profile details, open interview planning, or make a decision.</div>
        <% } else { %>
          <div class="detail-grid">
            <div><strong>Username</strong><%= escapeHtml(applicationDetail.getUsername()) %></div>
            <div><strong>Email</strong><%= escapeHtml(applicationDetail.getEmail()) %></div>
            <div><strong>Phone</strong><%= escapeHtml(applicationDetail.getPhoneNumber()) %></div>
            <div><strong>Student ID</strong><%= applicationDetail.getStudentId() == null ? "-" : escapeHtml(applicationDetail.getStudentId()) %></div>
            <div><strong>Programme</strong><%= applicationDetail.getProgramme() == null ? "-" : escapeHtml(applicationDetail.getProgramme()) %></div>
            <div><strong>Status</strong><%= applicationDetail.getStatus().getDisplayName() %></div>
            <div><strong>Module</strong><%= escapeHtml(applicationDetail.getModuleCode()) %> - <%= escapeHtml(applicationDetail.getModuleName()) %></div>
            <div><strong>Applied At</strong><%= DisplayFormatUtil.formatDateTime(applicationDetail.getAppliedAt()) %></div>
            <div><strong>Last Reviewed At</strong><%= applicationDetail.getReviewedAt() == null || applicationDetail.getReviewedAt().isBlank() ? "Not reviewed yet." : DisplayFormatUtil.formatDateTime(applicationDetail.getReviewedAt()) %></div>
            <div><strong>Last Updated</strong><%= DisplayFormatUtil.formatDateTime(applicationDetail.getStatusUpdatedAt()) %></div>
          </div>

          <div style="margin-top: 18px;">
            <strong>Skills</strong>
            <p><%= applicationDetail.getSkills() == null || applicationDetail.getSkills().isBlank() ? "No skills submitted." : escapeHtml(applicationDetail.getSkills()) %></p>
          </div>

          <div style="margin-top: 18px;">
            <strong>Personal Statement</strong>
            <p><%= applicationDetail.getPersonalStatement() == null || applicationDetail.getPersonalStatement().isBlank() ? "No personal statement submitted." : escapeHtml(applicationDetail.getPersonalStatement()) %></p>
          </div>

          <div style="margin-top: 18px;">
            <strong>CV</strong>
            <% if (applicationDetail.getCvRelativePath() == null || applicationDetail.getCvRelativePath().isBlank()) { %>
              <p>No CV uploaded yet.</p>
            <% } else { %>
              <p><%= applicationDetail.getCvFileName() == null || applicationDetail.getCvFileName().isBlank() ? escapeHtml(applicationDetail.getCvRelativePath()) : escapeHtml(applicationDetail.getCvFileName()) %></p>
              <p><a href="<%= request.getContextPath() %>/cv/download?applicationId=<%= encode(applicationDetail.getApplicationId()) %>">Download CV</a></p>
            <% } %>
          </div>

          <div style="margin-top: 18px;">
            <strong>Interview Plan</strong>
            <% if (applicationDetail.getInterviewScheduledAt() == null || applicationDetail.getInterviewScheduledAt().isBlank()) { %>
              <p>No interview has been scheduled yet.</p>
            <% } else { %>
              <p><strong>Time:</strong> <%= DisplayFormatUtil.formatDateTime(applicationDetail.getInterviewScheduledAt()) %></p>
              <p><strong>Mode:</strong> <%= applicationDetail.getInterviewMode() == null || applicationDetail.getInterviewMode().isBlank() ? "-" : escapeHtml(applicationDetail.getInterviewMode()) %></p>
              <p><strong>Location:</strong> <%= applicationDetail.getInterviewLocation() == null || applicationDetail.getInterviewLocation().isBlank() ? "-" : escapeHtml(applicationDetail.getInterviewLocation()) %></p>
              <p><strong>Notes:</strong> <%= applicationDetail.getInterviewNotes() == null || applicationDetail.getInterviewNotes().isBlank() ? "-" : escapeHtml(applicationDetail.getInterviewNotes()) %></p>
            <% } %>
          </div>

          <div class="actions" style="margin-top: 18px;">
            <a class="btn-secondary" href="<%= request.getContextPath() %>/mo/interviews?<%= currentQueryBase %>&applicationId=<%= encode(applicationDetail.getApplicationId()) %>">Open Interview Planning</a>
          </div>

          <form id="decisionForm" method="post" action="<%= request.getContextPath() %>/mo/applicants/decision" style="margin-top: 18px;">
            <input type="hidden" name="applicationId" value="<%= escapeHtml(applicationDetail.getApplicationId()) %>">
            <input type="hidden" name="jobId" value="<%= escapeHtml(currentJobId) %>">
            <input type="hidden" name="status" value="<%= escapeHtml(currentStatus) %>">
            <input type="hidden" name="keyword" value="<%= escapeHtml(currentKeyword) %>">
            <input type="hidden" name="hasCv" value="<%= escapeHtml(currentHasCv) %>">
            <input type="hidden" name="sortBy" value="<%= escapeHtml(currentSortBy) %>">
            <input type="hidden" name="sortDirection" value="<%= escapeHtml(currentSortDirection) %>">
            <div class="field">
              <label for="remarksInput">Decision Remarks</label>
              <textarea id="remarksInput" name="remarks" placeholder="Add a short decision note"><%= applicationDetail.getRemarks() == null ? "" : escapeHtml(applicationDetail.getRemarks()) %></textarea>
            </div>
            <div class="decision-actions">
              <button class="btn-secondary" type="submit" name="decision" value="UNDER_REVIEW">Mark Under Review</button>
              <button class="btn-secondary" type="submit" name="decision" value="SELECTED">Select</button>
              <button class="btn-ghost" type="submit" name="decision" value="REJECTED">Reject</button>
            </div>
          </form>
        <% } %>
      </div>
    </div>
  </div>
</body>
</html>
