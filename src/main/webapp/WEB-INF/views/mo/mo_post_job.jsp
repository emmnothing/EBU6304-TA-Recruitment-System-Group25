<%@ page import="java.util.List" %>
<%@ page import="com.bupt.ta.model.JobPost" %>
<%
String flashType = (String) request.getAttribute("flashType");
String flashMessage = (String) request.getAttribute("flashMessage");
String currentUsername = (String) request.getAttribute("currentUsername");
Integer unreadNotificationCount = (Integer) request.getAttribute("unreadNotificationCount");
List<JobPost> myJobs = (List<JobPost>) request.getAttribute("myJobs");
%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>TA Recruitment System - Post TA Job</title>
  <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/style.css">
</head>
<body class="app-page">
  <div class="app-shell">
    <div class="topbar panel">
      <div class="brand">
        <h1>Post TA Job</h1>
        <p>Create a structured TA position for your module, <strong><%= currentUsername %></strong>.</p>
      </div>
      <div class="top-links">
        <a href="<%= request.getContextPath() %>/mo/dashboard">Back to Dashboard</a>
        <a href="<%= request.getContextPath() %>/mo/applicants">View Applicants</a>
        <a href="<%= request.getContextPath() %>/mo/notifications">Notifications<%= unreadNotificationCount != null && unreadNotificationCount > 0 ? " (" + unreadNotificationCount + ")" : "" %></a>
        <a href="<%= request.getContextPath() %>/account/delete">Delete Account</a>
        <a href="<%= request.getContextPath() %>/auth/logout">Logout</a>
      </div>
    </div>

    <% if (flashMessage != null) { %>
      <div class="flash-message <%= flashType %>"><%= flashMessage %></div>
    <% } %>

    <div class="panel">
      <h2>Job Details</h2>
      <form id="postJobForm" method="post" action="<%= request.getContextPath() %>/mo/post-job">
        <div class="form-grid">
          <div class="field">
            <label for="moduleCodeInput">Module Code</label>
            <input type="text" id="moduleCodeInput" name="moduleCode" placeholder="e.g. EBU6304" required>
          </div>

          <div class="field">
            <label for="moduleNameInput">Module Name</label>
            <input type="text" id="moduleNameInput" name="moduleName" placeholder="Enter module name" required>
          </div>

          <div class="field">
            <label for="jobTitleInput">Job Title</label>
            <input type="text" id="jobTitleInput" name="jobTitle" placeholder="Teaching Assistant" required>
          </div>

          <div class="field">
            <label for="vacanciesInput">Vacancies</label>
            <input type="number" id="vacanciesInput" name="vacancies" min="1" placeholder="1" required>
          </div>

          <div class="field">
            <label for="weeklyHoursInput">Weekly Hours</label>
            <input type="number" id="weeklyHoursInput" name="weeklyHours" min="1" placeholder="4" required>
          </div>

          <div class="field">
            <label for="applicationDeadlineInput">Application Deadline</label>
            <input type="date" id="applicationDeadlineInput" name="applicationDeadline" required>
          </div>

          <div class="field full-width">
            <label for="locationModeInput">Location / Mode</label>
            <input type="text" id="locationModeInput" name="locationMode" placeholder="On campus / Hybrid / Online" required>
          </div>

          <div class="field full-width">
            <label for="descriptionInput">Description</label>
            <textarea id="descriptionInput" name="description" placeholder="Describe the duties and support expected from the TA" required></textarea>
          </div>

          <div class="field full-width">
            <label for="requirementsInput">Requirements</label>
            <textarea id="requirementsInput" name="requirements" placeholder="List required skills, grades, or experience" required></textarea>
          </div>

          <div class="field full-width actions">
            <button class="btn-secondary" type="submit">Create Job Post</button>
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
                <span><strong>Deadline</strong><%= job.getApplicationDeadline() %></span>
                <span><strong>Updated</strong><%= job.getStatusUpdatedAt() == null ? "-" : job.getStatusUpdatedAt() %></span>
              </div>
              <% if (job.getClosedReason() != null && !job.getClosedReason().isBlank()) { %>
                <div class="hint"><%= job.getClosedReason() %></div>
              <% } %>
              <% if (job.getStatus().isOpen()) { %>
                <div class="actions">
                  <form method="post" action="<%= request.getContextPath() %>/mo/post-job/close">
                    <input type="hidden" name="jobId" value="<%= job.getJobId() %>">
                    <button class="btn-ghost" type="submit">Close Job</button>
                  </form>
                </div>
              <% } %>
            </div>
          <% } %>
        </div>
      <% } %>
    </div>
  </div>
</body>
</html>
