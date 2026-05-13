<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.bupt.ta.dto.JobFilter" %>
<%@ page import="com.bupt.ta.model.JobPost" %>
<%
String flashType = (String) request.getAttribute("flashType");
String flashMessage = (String) request.getAttribute("flashMessage");
String currentUsername = (String) request.getAttribute("currentUsername");
JobFilter jobFilter = (JobFilter) request.getAttribute("jobFilter");
List<JobPost> jobs = (List<JobPost>) request.getAttribute("jobs");
Map<String, String> applyDisabledReasons = (Map<String, String>) request.getAttribute("applyDisabledReasons");
Map<String, Integer> remainingVacancies = (Map<String, Integer>) request.getAttribute("remainingVacancies");
%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>TA Recruitment System - Available Jobs</title>
  <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/style.css?v=role-nav-20260513">
</head>
<body class="app-page">
  <div class="app-shell">
    <%
    request.setAttribute("roleNavPage", "jobs");
    request.setAttribute("roleNavRoleLabel", "Applicant");
    request.setAttribute("roleNavTitle", "Available Jobs");
    request.setAttribute("roleNavSubtitle", "Welcome, <strong>" + currentUsername + "</strong>. Browse open TA jobs and apply directly from this page.");
    request.setAttribute("roleNavNotificationKey", "notifications");
    request.setAttribute("roleNavItems", new String[][] {
        {"dashboard", "Dashboard", "/applicant/dashboard"},
        {"profile", "Profile", "/applicant/profile"},
        {"jobs", "Available Jobs", "/applicant/jobs"},
        {"status", "My Applications", "/applicant/status"},
        {"notifications", "Notifications", "/applicant/notifications"}
    });
    %>
    <%@ include file="../shared/role_nav.jspf" %>

    <% if (flashMessage != null) { %>
      <div class="flash-message <%= flashType %>"><%= flashMessage %></div>
    <% } %>

    <div class="panel" style="margin-bottom: 22px;">
      <h2>Application Rules</h2>
      <p>You must complete your profile and upload a CV before submitting any TA application. Jobs close automatically after their deadline or once all vacancies are filled.</p>
      <div class="hint">If an Apply button is disabled, the page will show the specific reason.</div>
    </div>

    <div class="panel" style="margin-bottom: 22px;">
      <h2>Job Filter</h2>
      <form id="jobFilterForm" class="filter-form" method="get" action="<%= request.getContextPath() %>/applicant/jobs">
        <div class="field">
          <label for="keywordInput">Keyword</label>
          <input type="text" id="keywordInput" name="keyword" placeholder="Module name or job title" value="<%= jobFilter == null || jobFilter.getKeyword() == null ? "" : jobFilter.getKeyword() %>">
        </div>
        <div class="field">
          <label for="moduleInput">Module</label>
          <input type="text" id="moduleInput" name="moduleCode" placeholder="e.g. EBU6304" value="<%= jobFilter == null || jobFilter.getModuleCode() == null ? "" : jobFilter.getModuleCode() %>">
        </div>
        <div class="field">
          <label for="statusSelect">Status</label>
          <select id="statusSelect" name="status">
            <option value="">All open jobs</option>
            <option value="open" <%= jobFilter != null && "open".equalsIgnoreCase(jobFilter.getStatus()) ? "selected" : "" %>>Open</option>
          </select>
        </div>
        <div class="field">
          <button class="btn-secondary" type="submit">Apply Filter</button>
        </div>
      </form>
    </div>

    <div class="cards-list" id="jobList">
      <% if (jobs == null || jobs.isEmpty()) { %>
        <div class="empty-state">No open TA jobs are available yet. Please check back later after a module organiser publishes a job.</div>
      <% } else { %>
        <% for (JobPost job : jobs) { %>
          <div class="job-card">
            <h3><%= job.getJobTitle() %></h3>
            <p><strong><%= job.getModuleCode() %></strong> - <%= job.getModuleName() %></p>
            <div class="job-meta">
              <span><strong>Vacancies</strong><%= job.getVacancies() %></span>
              <span><strong>Remaining</strong><%= remainingVacancies != null && remainingVacancies.get(job.getJobId()) != null ? remainingVacancies.get(job.getJobId()) : job.getVacancies() %></span>
              <span><strong>Weekly Hours</strong><%= job.getWeeklyHours() %></span>
              <span><strong>Deadline</strong><%= job.getApplicationDeadline() %></span>
              <span><strong>Mode</strong><%= job.getLocationMode() %></span>
            </div>
            <div class="detail-grid">
              <div><strong>Description</strong><%= job.getDescription() %></div>
              <div><strong>Requirements</strong><%= job.getRequirements() %></div>
            </div>
            <div class="actions">
              <% String disabledReason = applyDisabledReasons == null ? null : applyDisabledReasons.get(job.getJobId()); %>
              <form method="post" action="<%= request.getContextPath() %>/applicant/jobs/apply">
                <input type="hidden" name="jobId" value="<%= job.getJobId() %>">
                <button class="btn-secondary" id="applyButton" type="submit" <%= disabledReason != null ? "disabled" : "" %>><%= disabledReason == null ? "Apply" : "Unavailable" %></button>
              </form>
              <% if (disabledReason != null) { %>
                <div class="hint"><%= disabledReason %></div>
              <% } %>
            </div>
          </div>
        <% } %>
      <% } %>
    </div>
  </div>
</body>
</html>
