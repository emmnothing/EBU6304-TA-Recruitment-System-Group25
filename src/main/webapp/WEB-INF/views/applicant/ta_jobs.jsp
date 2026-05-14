<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Set" %>
<%@ page import="com.bupt.ta.dto.JobFilter" %>
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

private String selected(boolean condition) {
    return condition ? "selected" : "";
}

private String valueOrEmpty(String value) {
    return value == null ? "" : escapeHtml(value);
}
%>
<%
String flashType = (String) request.getAttribute("flashType");
String flashMessage = (String) request.getAttribute("flashMessage");
String currentUsername = (String) request.getAttribute("currentUsername");
String filterQuery = (String) request.getAttribute("filterQuery");
JobFilter jobFilter = (JobFilter) request.getAttribute("jobFilter");
List<JobPost> jobs = (List<JobPost>) request.getAttribute("jobs");
List<JobPost> favoriteJobs = (List<JobPost>) request.getAttribute("favoriteJobs");
Set<String> favoriteJobIds = (Set<String>) request.getAttribute("favoriteJobIds");
Map<String, String> applyDisabledReasons = (Map<String, String>) request.getAttribute("applyDisabledReasons");
Map<String, Integer> remainingVacancies = (Map<String, Integer>) request.getAttribute("remainingVacancies");

String currentKeyword = jobFilter == null ? "" : valueOrEmpty(jobFilter.getKeyword());
String currentModuleCode = jobFilter == null ? "" : valueOrEmpty(jobFilter.getModuleCode());
String currentLocationMode = jobFilter == null ? "" : valueOrEmpty(jobFilter.getLocationMode());
String currentMaxWeeklyHours = jobFilter == null ? "" : valueOrEmpty(jobFilter.getMaxWeeklyHours());
String currentMinRemainingVacancies = jobFilter == null ? "" : valueOrEmpty(jobFilter.getMinRemainingVacancies());
String currentSortBy = jobFilter == null || jobFilter.getSortBy() == null ? "" : jobFilter.getSortBy();
String currentSortDirection = jobFilter == null || jobFilter.getSortDirection() == null ? "" : jobFilter.getSortDirection();
int favoriteCount = favoriteJobs == null ? 0 : favoriteJobs.size();
int jobCount = jobs == null ? 0 : jobs.size();
%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>TA Recruitment System - Available Jobs</title>
  <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/style.css?v=jobs-favorites-20260514">
</head>
<body class="app-page">
  <div class="app-shell">
    <%
    request.setAttribute("roleNavPage", "jobs");
    request.setAttribute("roleNavRoleLabel", "Applicant");
    request.setAttribute("roleNavTitle", "Available Jobs");
    request.setAttribute("roleNavSubtitle", "Welcome, <strong>" + escapeHtml(currentUsername) + "</strong>. Browse open TA jobs and apply directly from this page.");
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
      <div class="flash-message <%= escapeHtml(flashType) %>"><%= escapeHtml(flashMessage) %></div>
    <% } %>

    <div class="panel jobs-guide-panel">
      <div>
        <span class="dashboard-kicker">Before applying</span>
        <h2>Application Rules</h2>
        <p>You must complete your profile and upload a CV before submitting any TA application. Jobs close automatically after their deadline or once all vacancies are filled.</p>
      </div>
      <a class="btn-secondary" href="<%= request.getContextPath() %>/applicant/profile">Check Profile</a>
    </div>

    <details class="panel favorite-jobs-panel">
      <summary>
        <span>
          <span class="dashboard-kicker">Saved roles</span>
          <strong>Favorite Jobs</strong>
          <small><%= favoriteCount %> saved open role<%= favoriteCount == 1 ? "" : "s" %></small>
        </span>
        <span class="favorite-expand-label">Click to expand</span>
      </summary>

      <% if (favoriteJobs == null || favoriteJobs.isEmpty()) { %>
        <div class="empty-state">No favorite jobs yet. Use the Save button on any job card to keep interesting roles here.</div>
      <% } else { %>
        <div class="favorite-job-list">
          <% for (JobPost job : favoriteJobs) { %>
            <% String disabledReason = applyDisabledReasons == null ? null : applyDisabledReasons.get(job.getJobId()); %>
            <article class="favorite-job-item">
              <div>
                <strong><%= escapeHtml(job.getModuleCode()) %> - <%= escapeHtml(job.getJobTitle()) %></strong>
                <p><%= escapeHtml(job.getModuleName()) %> · <%= escapeHtml(job.getLocationMode()) %> · Deadline <%= DisplayFormatUtil.formatDateTime(job.getApplicationDeadline()) %></p>
              </div>
              <div class="table-actions">
                <form method="post" action="<%= request.getContextPath() %>/applicant/jobs/favorite">
                  <input type="hidden" name="jobId" value="<%= escapeHtml(job.getJobId()) %>">
                  <input type="hidden" name="returnQuery" value="<%= escapeHtml(filterQuery) %>">
                  <button class="btn-ghost" type="submit">Remove</button>
                </form>
                <form method="post" action="<%= request.getContextPath() %>/applicant/jobs/apply">
                  <input type="hidden" name="jobId" value="<%= escapeHtml(job.getJobId()) %>">
                  <input type="hidden" name="returnQuery" value="<%= escapeHtml(filterQuery) %>">
                  <button class="btn-secondary" type="submit" <%= disabledReason != null ? "disabled" : "" %>><%= disabledReason == null ? "Apply" : "Unavailable" %></button>
                </form>
              </div>
            </article>
          <% } %>
        </div>
      <% } %>
    </details>

    <div class="panel job-filter-panel">
      <div class="job-section-heading">
        <div>
          <span class="dashboard-kicker">Search and sort</span>
          <h2>Job Filter</h2>
          <p class="hint">Filter by workload, module, mode, or vacancies, then sort by what matters most right now.</p>
        </div>
        <a class="btn-ghost" href="<%= request.getContextPath() %>/applicant/jobs">Reset</a>
      </div>

      <form id="jobFilterForm" class="job-filter-form" method="get" action="<%= request.getContextPath() %>/applicant/jobs">
        <div class="field">
          <label for="keywordInput">Keyword</label>
          <input type="text" id="keywordInput" name="keyword" placeholder="Title, module, description, requirement" value="<%= currentKeyword %>">
        </div>
        <div class="field">
          <label for="moduleInput">Module</label>
          <input type="text" id="moduleInput" name="moduleCode" placeholder="e.g. EBU6304" value="<%= currentModuleCode %>">
        </div>
        <div class="field">
          <label for="locationModeInput">Mode / Location</label>
          <input type="text" id="locationModeInput" name="locationMode" placeholder="Online / Campus / Hybrid" value="<%= currentLocationMode %>">
        </div>
        <div class="field">
          <label for="maxWeeklyHoursInput">Max Weekly Hours</label>
          <input type="number" min="1" id="maxWeeklyHoursInput" name="maxWeeklyHours" placeholder="e.g. 6" value="<%= currentMaxWeeklyHours %>">
        </div>
        <div class="field">
          <label for="minRemainingInput">Min Remaining Vacancies</label>
          <input type="number" min="1" id="minRemainingInput" name="minRemainingVacancies" placeholder="e.g. 1" value="<%= currentMinRemainingVacancies %>">
        </div>
        <div class="field">
          <label for="sortBySelect">Sort By</label>
          <select id="sortBySelect" name="sortBy">
            <option value="deadline" <%= selected(currentSortBy.isBlank() || "deadline".equalsIgnoreCase(currentSortBy)) %>>Deadline soonest</option>
            <option value="newest" <%= selected("newest".equalsIgnoreCase(currentSortBy)) %>>Newest posted</option>
            <option value="hours" <%= selected("hours".equalsIgnoreCase(currentSortBy)) %>>Weekly hours</option>
            <option value="remaining" <%= selected("remaining".equalsIgnoreCase(currentSortBy)) %>>Remaining vacancies</option>
            <option value="module" <%= selected("module".equalsIgnoreCase(currentSortBy)) %>>Module code</option>
          </select>
        </div>
        <div class="field">
          <label for="sortDirectionSelect">Direction</label>
          <select id="sortDirectionSelect" name="sortDirection">
            <option value="asc" <%= selected(currentSortDirection.isBlank() || "asc".equalsIgnoreCase(currentSortDirection)) %>>Ascending</option>
            <option value="desc" <%= selected("desc".equalsIgnoreCase(currentSortDirection)) %>>Descending</option>
          </select>
        </div>
        <div class="field job-filter-actions">
          <button class="btn-secondary" type="submit">Apply Filter</button>
        </div>
      </form>
    </div>

    <div class="job-results-heading">
      <div>
        <span class="dashboard-kicker">Open roles</span>
        <h2><%= jobCount %> matching job<%= jobCount == 1 ? "" : "s" %></h2>
      </div>
      <p class="hint">Save roles for later comparison, or apply directly when your profile is ready.</p>
    </div>

    <div class="cards-list" id="jobList">
      <% if (jobs == null || jobs.isEmpty()) { %>
        <div class="empty-state">No open TA jobs match the current filters. Try relaxing keyword, workload, or mode filters.</div>
      <% } else { %>
        <% for (JobPost job : jobs) { %>
          <%
          String disabledReason = applyDisabledReasons == null ? null : applyDisabledReasons.get(job.getJobId());
          boolean isFavorite = favoriteJobIds != null && favoriteJobIds.contains(job.getJobId());
          int remaining = remainingVacancies != null && remainingVacancies.get(job.getJobId()) != null ? remainingVacancies.get(job.getJobId()) : job.getVacancies();
          %>
          <article class="job-card applicant-job-card">
            <div class="job-card-header">
              <div>
                <span class="dashboard-kicker"><%= escapeHtml(job.getModuleCode()) %></span>
                <h3><%= escapeHtml(job.getJobTitle()) %></h3>
                <p><strong><%= escapeHtml(job.getModuleCode()) %></strong> - <%= escapeHtml(job.getModuleName()) %></p>
              </div>
              <form method="post" action="<%= request.getContextPath() %>/applicant/jobs/favorite">
                <input type="hidden" name="jobId" value="<%= escapeHtml(job.getJobId()) %>">
                <input type="hidden" name="returnQuery" value="<%= escapeHtml(filterQuery) %>">
                <button class="favorite-button <%= isFavorite ? "saved" : "" %>" type="submit"><%= isFavorite ? "Saved" : "Save" %></button>
              </form>
            </div>

            <div class="job-meta">
              <span><strong>Vacancies</strong><%= job.getVacancies() %></span>
              <span><strong>Remaining</strong><%= remaining %></span>
              <span><strong>Weekly Hours</strong><%= job.getWeeklyHours() %></span>
              <span><strong>Deadline</strong><%= DisplayFormatUtil.formatDateTime(job.getApplicationDeadline()) %></span>
              <span><strong>Mode</strong><%= escapeHtml(job.getLocationMode()) %></span>
            </div>
            <div class="detail-grid">
              <div><strong>Description</strong><%= escapeHtml(job.getDescription()) %></div>
              <div><strong>Requirements</strong><%= escapeHtml(job.getRequirements()) %></div>
            </div>
            <div class="actions job-card-actions">
              <form method="post" action="<%= request.getContextPath() %>/applicant/jobs/apply">
                <input type="hidden" name="jobId" value="<%= escapeHtml(job.getJobId()) %>">
                <input type="hidden" name="returnQuery" value="<%= escapeHtml(filterQuery) %>">
                <button class="btn-secondary" type="submit" <%= disabledReason != null ? "disabled" : "" %>><%= disabledReason == null ? "Apply" : "Unavailable" %></button>
              </form>
              <% if (disabledReason != null) { %>
                <div class="hint"><%= escapeHtml(disabledReason) %></div>
              <% } else { %>
                <div class="hint">Profile ready? Apply now or save this role for comparison.</div>
              <% } %>
            </div>
          </article>
        <% } %>
      <% } %>
    </div>
  </div>
</body>
</html>
