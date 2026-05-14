<%@ page import="com.bupt.ta.dto.MoDashboardSummary" %>
<%
MoDashboardSummary summary = (MoDashboardSummary) request.getAttribute("summary");
String currentUsername = (String) request.getAttribute("currentUsername");
%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>TA Recruitment System - Module Organiser Dashboard</title>
  <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/style.css">
</head>
<body class="app-page">
  <div class="app-shell">
    <div class="topbar panel">
      <div class="brand">
        <h1>Module Organiser Dashboard</h1>
        <p>Welcome, <strong><%= currentUsername %></strong>. Manage your TA positions and review applicants from one place.</p>
      </div>
      <div class="top-links">
        <a href="<%= request.getContextPath() %>/mo/post-job">Post TA Job</a>
        <a href="<%= request.getContextPath() %>/mo/applicants">View Applicants</a>
        <a href="<%= request.getContextPath() %>/auth/logout">Logout</a>
      </div>
    </div>

    <div class="summary-strip">
      <div class="summary-card">
        <div class="number"><%= summary.getOpenPostCount() %></div>
        <div class="label">Open posts</div>
      </div>
      <div class="summary-card">
        <div class="number"><%= summary.getApplicantsPendingCount() %></div>
        <div class="label">Applicants pending review</div>
      </div>
      <div class="summary-card">
        <div class="number">2</div>
        <div class="label">Core actions available</div>
      </div>
    </div>

    <div class="page-grid two-col" style="margin-top: 22px;">
      <div class="feature-card" id="postJobCard">
        <h3>Post a Job</h3>
        <p>Create a new TA recruitment post with module details, expected workload, deadline, and requirements.</p>
        <div class="actions">
          <a class="btn-secondary" href="<%= request.getContextPath() %>/mo/post-job">Open Job Form</a>
        </div>
      </div>

      <div class="feature-card" id="viewApplicantsCard">
        <h3>View Applicants</h3>
        <p>Filter applicants, review their profile and CV information, then record a final decision.</p>
        <div class="actions">
          <a class="btn-secondary" href="<%= request.getContextPath() %>/mo/applicants">Review Applicants</a>
        </div>
      </div>
    </div>
  </div>
</body>
</html>
