<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
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
%>
<%
String flashType = (String) request.getAttribute("flashType");
String flashMessage = (String) request.getAttribute("flashMessage");
String currentUsername = (String) request.getAttribute("currentUsername");
Integer unreadNotificationCount = (Integer) request.getAttribute("unreadNotificationCount");
List<JobPost> myJobs = (List<JobPost>) request.getAttribute("myJobs");
Map<String, Integer> applicationCounts = (Map<String, Integer>) request.getAttribute("applicationCounts");
JobPost editingJob = (JobPost) request.getAttribute("editingJob");
boolean editMode = editingJob != null;
%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>TA Recruitment System - <%= editMode ? "Edit TA Job" : "Post TA Job" %></title>
  <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/style.css?v=role-nav-20260513">
</head>
<body class="app-page">
  <div class="app-shell">
    <%
    request.setAttribute("roleNavPage", "jobs");
    request.setAttribute("roleNavRoleLabel", "Module Organiser");
    request.setAttribute("roleNavTitle", editMode ? "Edit TA Job" : "Post TA Job");
    request.setAttribute(
        "roleNavSubtitle",
        editMode
            ? "Update the selected TA position for your module, <strong>" + escapeHtml(currentUsername) + "</strong>."
            : "Create a structured TA position for your module, <strong>" + escapeHtml(currentUsername) + "</strong>."
    );
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

    <div class="panel">
      <h2><%= editMode ? "Edit Job Details" : "Job Details" %></h2>
      <% if (editMode) { %>
        <div class="hint">You are editing <strong><%= escapeHtml(editingJob.getJobTitle()) %></strong> for <strong><%= escapeHtml(editingJob.getModuleCode()) %></strong>.</div>
      <% } %>
      <form id="postJobForm" method="post" action="<%= request.getContextPath() %><%= editMode ? "/mo/post-job/edit" : "/mo/post-job" %>">
        <% if (editMode) { %>
          <input type="hidden" name="jobId" value="<%= escapeHtml(editingJob.getJobId()) %>">
        <% } %>
        <div class="form-grid">
          <div class="field">
            <label for="moduleCodeInput">Module Code</label>
            <input type="text" id="moduleCodeInput" name="moduleCode" placeholder="e.g. EBU6304" value="<%= editMode ? escapeHtml(editingJob.getModuleCode()) : "" %>" required>
          </div>

          <div class="field">
            <label for="moduleNameInput">Module Name</label>
            <input type="text" id="moduleNameInput" name="moduleName" placeholder="Enter module name" value="<%= editMode ? escapeHtml(editingJob.getModuleName()) : "" %>" required>
          </div>

          <div class="field">
            <label for="jobTitleInput">Job Title</label>
            <input type="text" id="jobTitleInput" name="jobTitle" placeholder="Teaching Assistant" value="<%= editMode ? escapeHtml(editingJob.getJobTitle()) : "" %>" required>
          </div>

          <div class="field">
            <label for="vacanciesInput">Vacancies</label>
            <input type="number" id="vacanciesInput" name="vacancies" min="1" placeholder="1" value="<%= editMode ? String.valueOf(editingJob.getVacancies()) : "" %>" required>
          </div>

          <div class="field">
            <label for="weeklyHoursInput">Weekly Hours</label>
            <input type="number" id="weeklyHoursInput" name="weeklyHours" min="1" placeholder="4" value="<%= editMode ? String.valueOf(editingJob.getWeeklyHours()) : "" %>" required>
          </div>

          <div class="field">
            <label for="applicationDeadlineInput">Application Deadline</label>
            <input type="date" id="applicationDeadlineInput" name="applicationDeadline" value="<%= editMode ? escapeHtml(editingJob.getApplicationDeadline()) : "" %>" required>
          </div>

          <div class="field full-width">
            <label for="locationModeInput">Location / Mode</label>
            <input type="text" id="locationModeInput" name="locationMode" placeholder="On campus / Hybrid / Online" value="<%= editMode ? escapeHtml(editingJob.getLocationMode()) : "" %>" required>
          </div>

          <div class="field full-width">
            <label for="descriptionInput">Description</label>
            <textarea id="descriptionInput" name="description" placeholder="Describe the duties and support expected from the TA" required><%= editMode ? escapeHtml(editingJob.getDescription()) : "" %></textarea>
          </div>

          <div class="field full-width">
            <label for="requirementsInput">Requirements</label>
            <textarea id="requirementsInput" name="requirements" placeholder="List required skills, grades, or experience" required><%= editMode ? escapeHtml(editingJob.getRequirements()) : "" %></textarea>
          </div>

          <div class="field full-width actions">
            <button class="btn-secondary" type="submit"><%= editMode ? "Save Changes" : "Create Job Post" %></button>
            <% if (editMode) { %>
              <a class="btn-ghost" href="<%= request.getContextPath() %>/mo/post-job">Cancel Editing</a>
            <% } %>
          </div>
        </div>
      </form>
    </div>

    <div class="panel" style="margin-top: 22px;">
      <h2>My Job Posts</h2>
      <% if (myJobs == null || myJobs.isEmpty()) { %>
        <div class="empty-state">You have not created any TA jobs yet.</div>
      <% } else { %>
        <div class="cards-list">
          <% for (JobPost job : myJobs) { %>
            <div class="job-card">
              <h3><%= job.getJobTitle() %></h3>
              <p><strong><%= job.getModuleCode() %></strong> - <%= job.getModuleName() %></p>
              <div class="job-meta">
                <span><strong>Status</strong><%= job.getStatus() %></span>
                <span><strong>Filled</strong><%= job.getFilledCount() %> / <%= job.getVacancies() %></span>
                <span><strong>Applications</strong><%= applicationCounts == null || applicationCounts.get(job.getJobId()) == null ? 0 : applicationCounts.get(job.getJobId()) %></span>
                <span><strong>Deadline</strong><%= job.getApplicationDeadline() %></span>
                <span><strong>Updated</strong><%= DisplayFormatUtil.formatDateTime(job.getStatusUpdatedAt()) %></span>
              </div>
              <% if (job.getClosedReason() != null && !job.getClosedReason().isBlank()) { %>
                <div class="hint"><%= job.getClosedReason() %></div>
              <% } %>
              <div class="actions">
                <% if (job.getStatus().isOpen()) { %>
                  <a class="btn-secondary" href="<%= request.getContextPath() %>/mo/post-job?editJobId=<%= escapeHtml(job.getJobId()) %>">Edit Job</a>
                  <form method="post" action="<%= request.getContextPath() %>/mo/post-job/close">
                    <input type="hidden" name="jobId" value="<%= escapeHtml(job.getJobId()) %>">
                    <button class="btn-ghost" type="submit">Close Job</button>
                  </form>
                <% } %>
                <% if (applicationCounts != null && applicationCounts.get(job.getJobId()) != null && applicationCounts.get(job.getJobId()) == 0) { %>
                  <form method="post" action="<%= request.getContextPath() %>/mo/post-job/delete">
                    <input type="hidden" name="jobId" value="<%= escapeHtml(job.getJobId()) %>">
                    <button class="btn-ghost" type="submit">Delete Job</button>
                  </form>
                <% } else { %>
                  <div class="hint">Delete is disabled once applications have been submitted.</div>
                <% } %>
              </div>
            </div>
          <% } %>
        </div>
      <% } %>
    </div>
  </div>
</body>
</html>
