<%@ page import="java.util.List" %>
<%@ page import="com.bupt.ta.dto.ApplicantFilter" %>
<%@ page import="com.bupt.ta.dto.ApplicantReviewItem" %>
<%@ page import="com.bupt.ta.model.JobPost" %>
<%
String flashType = (String) request.getAttribute("flashType");
String flashMessage = (String) request.getAttribute("flashMessage");
String currentUsername = (String) request.getAttribute("currentUsername");
ApplicantFilter applicantFilter = (ApplicantFilter) request.getAttribute("applicantFilter");
List<JobPost> jobOptions = (List<JobPost>) request.getAttribute("jobOptions");
List<ApplicantReviewItem> applicantList = (List<ApplicantReviewItem>) request.getAttribute("applicantList");
ApplicantReviewItem applicationDetail = (ApplicantReviewItem) request.getAttribute("applicationDetail");
String selectedApplicationId = (String) request.getAttribute("selectedApplicationId");
%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>TA Recruitment System - Applicants Review</title>
  <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/style.css">
</head>
<body class="app-page">
  <div class="app-shell">
    <div class="topbar panel">
      <div class="brand">
        <h1>Applicants Review</h1>
        <p>Filter submissions, inspect candidate details, and record your decision, <strong><%= currentUsername %></strong>.</p>
      </div>
      <div class="top-links">
        <a href="<%= request.getContextPath() %>/mo/dashboard">Back to Dashboard</a>
        <a href="<%= request.getContextPath() %>/mo/post-job">Post TA Job</a>
        <a href="<%= request.getContextPath() %>/auth/logout">Logout</a>
      </div>
    </div>

    <% if (flashMessage != null) { %>
      <div class="flash-message <%= flashType %>"><%= flashMessage %></div>
    <% } %>

    <div class="panel" style="margin-bottom: 22px;">
      <h2>Applicant Filter</h2>
      <form id="applicantFilterForm" class="filter-form" method="get" action="<%= request.getContextPath() %>/mo/applicants">
        <div class="field">
          <label for="jobSelect">Job</label>
          <select id="jobSelect" name="jobId">
            <option value="">All my jobs</option>
            <% for (JobPost job : jobOptions) { %>
              <option value="<%= job.getJobId() %>" <%= applicantFilter != null && job.getJobId().equals(applicantFilter.getJobId()) ? "selected" : "" %>><%= job.getModuleCode() %> - <%= job.getJobTitle() %></option>
            <% } %>
          </select>
        </div>
        <div class="field">
          <label for="statusSelect">Status</label>
          <select id="statusSelect" name="status">
            <option value="">All</option>
            <option value="APPLIED" <%= applicantFilter != null && "APPLIED".equalsIgnoreCase(applicantFilter.getStatus()) ? "selected" : "" %>>Applied</option>
            <option value="UNDER_REVIEW" <%= applicantFilter != null && "UNDER_REVIEW".equalsIgnoreCase(applicantFilter.getStatus()) ? "selected" : "" %>>Under Review</option>
            <option value="SELECTED" <%= applicantFilter != null && "SELECTED".equalsIgnoreCase(applicantFilter.getStatus()) ? "selected" : "" %>>Selected</option>
            <option value="REJECTED" <%= applicantFilter != null && "REJECTED".equalsIgnoreCase(applicantFilter.getStatus()) ? "selected" : "" %>>Rejected</option>
          </select>
        </div>
        <div class="field">
          <label for="keywordInput">Keyword</label>
          <input type="text" id="keywordInput" name="keyword" placeholder="Username, email, student ID" value="<%= applicantFilter == null || applicantFilter.getKeyword() == null ? "" : applicantFilter.getKeyword() %>">
        </div>
        <div class="field">
          <button class="btn-secondary" type="submit">Search</button>
        </div>
      </form>
    </div>

    <div class="split-layout">
      <div class="detail-card">
        <h3>Applicant List</h3>
        <% if (applicantList == null || applicantList.isEmpty()) { %>
          <div class="empty-state">No applicants match the current filter. After a TA applicant submits an application, it will appear here.</div>
        <% } else { %>
          <div class="stack-list" id="applicantTable">
            <% for (ApplicantReviewItem item : applicantList) { %>
              <a class="list-item <%= item.getApplicationId().equals(selectedApplicationId) ? "active" : "" %>"
                 href="<%= request.getContextPath() %>/mo/applicants?jobId=<%= applicantFilter != null && applicantFilter.getJobId() != null ? applicantFilter.getJobId() : "" %>&status=<%= applicantFilter != null && applicantFilter.getStatus() != null ? applicantFilter.getStatus() : "" %>&keyword=<%= applicantFilter != null && applicantFilter.getKeyword() != null ? applicantFilter.getKeyword() : "" %>&applicationId=<%= item.getApplicationId() %>">
                <div class="list-item-title"><%= item.getUsername() %> - <%= item.getJobTitle() %></div>
                <div class="list-item-sub"><%= item.getModuleCode() %> | <%= item.getEmail() %> | <%= item.getStatus() %></div>
              </a>
            <% } %>
          </div>
        <% } %>
      </div>

      <div class="detail-card">
        <h3>Applicant Detail</h3>
        <% if (applicationDetail == null) { %>
          <div class="empty-state">Select an applicant from the list to view profile details and make a decision.</div>
        <% } else { %>
          <div class="detail-grid">
            <div><strong>Username</strong><%= applicationDetail.getUsername() %></div>
            <div><strong>Email</strong><%= applicationDetail.getEmail() %></div>
            <div><strong>Phone</strong><%= applicationDetail.getPhoneNumber() %></div>
            <div><strong>Student ID</strong><%= applicationDetail.getStudentId() == null ? "-" : applicationDetail.getStudentId() %></div>
            <div><strong>Programme</strong><%= applicationDetail.getProgramme() == null ? "-" : applicationDetail.getProgramme() %></div>
            <div><strong>Status</strong><%= applicationDetail.getStatus() %></div>
            <div><strong>Module</strong><%= applicationDetail.getModuleCode() %> - <%= applicationDetail.getModuleName() %></div>
            <div><strong>Applied At</strong><%= applicationDetail.getAppliedAt() %></div>
          </div>

          <div style="margin-top: 18px;">
            <strong>Skills</strong>
            <p><%= applicationDetail.getSkills() == null || applicationDetail.getSkills().isBlank() ? "No skills submitted." : applicationDetail.getSkills() %></p>
          </div>

          <div style="margin-top: 18px;">
            <strong>Personal Statement</strong>
            <p><%= applicationDetail.getPersonalStatement() == null || applicationDetail.getPersonalStatement().isBlank() ? "No personal statement submitted." : applicationDetail.getPersonalStatement() %></p>
          </div>

          <div style="margin-top: 18px;">
            <strong>CV Path</strong>
            <p><%= applicationDetail.getCvRelativePath() == null || applicationDetail.getCvRelativePath().isBlank() ? "No CV uploaded yet." : applicationDetail.getCvRelativePath() %></p>
          </div>

          <form id="decisionForm" method="post" action="<%= request.getContextPath() %>/mo/applicants/decision" style="margin-top: 18px;">
            <input type="hidden" name="applicationId" value="<%= applicationDetail.getApplicationId() %>">
            <input type="hidden" name="jobId" value="<%= applicantFilter == null || applicantFilter.getJobId() == null ? "" : applicantFilter.getJobId() %>">
            <div class="field">
              <label for="remarksInput">Remarks</label>
              <textarea id="remarksInput" name="remarks" placeholder="Add a short decision note"><%= applicationDetail.getRemarks() == null ? "" : applicationDetail.getRemarks() %></textarea>
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
