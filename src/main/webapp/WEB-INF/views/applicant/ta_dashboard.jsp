<%@ page import="com.bupt.ta.dto.ApplicantDashboardSummary" %>
<%
ApplicantDashboardSummary summary = (ApplicantDashboardSummary) request.getAttribute("summary");
String currentUsername = (String) request.getAttribute("currentUsername");
%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>TA Applicant Dashboard</title>
  <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/style.css">
</head>
<body class="app-page">
  <div class="app-shell">
    <div class="topbar panel">
      <div class="brand">
        <h1>TA Recruitment System</h1>
        <p>Welcome, <strong><%= currentUsername %></strong>. Use this dashboard to manage your application journey.</p>
      </div>
      <div class="top-links">
        <a href="<%= request.getContextPath() %>/applicant/profile">My Profile</a>
        <a href="<%= request.getContextPath() %>/applicant/jobs">Available Jobs</a>
        <a href="<%= request.getContextPath() %>/applicant/status">Application Status</a>
        <a href="<%= request.getContextPath() %>/auth/logout">Logout</a>
      </div>
    </div>

    <div class="summary-strip">
      <div class="summary-card">
        <div class="number"><%= summary.getAppliedCount() %></div>
        <div class="label">Applications submitted</div>
      </div>
      <div class="summary-card">
        <div class="number"><%= summary.getUnderReviewCount() %></div>
        <div class="label">Applications under review</div>
      </div>
      <div class="summary-card">
        <div class="number"><%= summary.getSelectedCount() %></div>
        <div class="label">Applications selected</div>
      </div>
    </div>

    <div class="page-grid three-col" style="margin-top: 22px;">
      <div class="feature-card" id="profileCard">
        <h3>My Profile</h3>
        <p>Update your applicant profile, academic information, preferred modules, and CV file before applying.</p>
        <div class="actions">
          <a class="btn-secondary" href="<%= request.getContextPath() %>/applicant/profile">Open Profile</a>
        </div>
      </div>

      <div class="feature-card" id="jobsCard">
        <h3>Available Jobs</h3>
        <p>Browse open TA posts, read requirements, and submit your application when a module matches your skills.</p>
        <div class="actions">
          <a class="btn-secondary" href="<%= request.getContextPath() %>/applicant/jobs">Browse Jobs</a>
        </div>
      </div>

      <div class="feature-card" id="statusCard">
        <h3>Application Status</h3>
        <p>Track each application from initial submission to review and final selection decisions.</p>
        <div class="actions">
          <a class="btn-secondary" href="<%= request.getContextPath() %>/applicant/status">View Status</a>
        </div>
      </div>
    </div>
  </div>
</body>
</html>
