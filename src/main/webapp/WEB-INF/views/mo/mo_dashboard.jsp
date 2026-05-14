<%@ page import="com.bupt.ta.dto.MoDashboardSummary" %>
<%
MoDashboardSummary summary = (MoDashboardSummary) request.getAttribute("summary");
String currentUsername = (String) request.getAttribute("currentUsername");
int openPostCount = summary == null ? 0 : summary.getOpenPostCount();
int pendingReviewCount = summary == null ? 0 : summary.getApplicantsPendingCount();
int activeHiringCount = pendingReviewCount;
int totalAttentionCount = openPostCount + pendingReviewCount;
String priorityTitle = "Keep your hiring pipeline moving";
String priorityText = "Start from your applicant queue, review new submissions, and keep each TA post progressing toward a final decision.";
String priorityHref = "/mo/applicants";
String priorityAction = "Review Applicants";

if (openPostCount == 0) {
    priorityTitle = "Create a TA post first";
    priorityText = "There are no open recruitment posts right now. Publish a new position to begin collecting applications.";
    priorityHref = "/mo/post-job";
    priorityAction = "Post a Job";
} else if (pendingReviewCount > 0) {
    priorityTitle = "Applicants are waiting for review";
    priorityText = "Your current queue needs attention. Review new candidates before the application pipeline slows down.";
    priorityHref = "/mo/applicants";
    priorityAction = "Open Applicant Queue";
}
%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>TA Recruitment System - Module Organiser Dashboard</title>
  <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/style.css?v=mo-dashboard-polish-20260514">
</head>
<body class="app-page">
  <div class="app-shell">
    <div class="topbar panel">
      <div class="brand">
        <h1>TA Recruitment System</h1>
        <p>Welcome, <strong><%= currentUsername %></strong>. Oversee open TA posts, applicant flow, and review activity from one dashboard.</p>
      </div>
      <div class="top-links">
        <a href="<%= request.getContextPath() %>/mo/dashboard">Dashboard</a>
        <a href="<%= request.getContextPath() %>/mo/post-job">Post TA Job</a>
        <a href="<%= request.getContextPath() %>/mo/applicants">Applicants</a>
        <a href="<%= request.getContextPath() %>/auth/logout">Logout</a>
      </div>
    </div>

    <div class="dashboard-focus-grid">
      <section class="panel dashboard-focus-card">
        <span class="dashboard-kicker">Next best step</span>
        <h2><%= priorityTitle %></h2>
        <p><%= priorityText %></p>
        <a class="btn-secondary" href="<%= request.getContextPath() %><%= priorityHref %>"><%= priorityAction %></a>
      </section>

      <section class="panel dashboard-pulse-card">
        <div class="profile-progress-header">
          <div>
            <span class="dashboard-kicker">Hiring pulse</span>
            <h2><%= activeHiringCount %> active item<%= activeHiringCount == 1 ? "" : "s" %></h2>
            <p><%= totalAttentionCount == 0 ? "No open TA activity right now. New jobs and applicants will appear here once hiring resumes." : "A quick snapshot of current recruitment pressure across your open posts and applicant queue." %></p>
          </div>
          <a class="btn-secondary" href="<%= request.getContextPath() %>/mo/applicants">Details</a>
        </div>
        <div class="dashboard-metric-row">
          <div>
            <strong><%= openPostCount %></strong>
            <span>Open posts</span>
          </div>
          <div>
            <strong><%= pendingReviewCount %></strong>
            <span>Pending review</span>
          </div>
        </div>
      </section>
    </div>

    <div class="dashboard-insight-grid">
      <section class="panel dashboard-insight-card">
        <div>
          <span class="dashboard-kicker">Posts</span>
          <h3><%= openPostCount == 0 ? "No active TA post yet" : openPostCount + " open TA post" + (openPostCount == 1 ? "" : "s") %></h3>
          <p>
            <%= openPostCount == 0
              ? "Publish a new TA role with a clear deadline and requirements to reopen the pipeline."
              : "Keep post details, deadlines, and vacancy expectations current so applicants understand what you need." %>
          </p>
        </div>
        <a class="btn-secondary" href="<%= request.getContextPath() %>/mo/post-job">Manage Job Posts</a>
      </section>

      <section class="panel dashboard-insight-card">
        <div>
          <span class="dashboard-kicker">Applicants</span>
          <h3><%= pendingReviewCount == 0 ? "Applicant queue is under control" : pendingReviewCount + " applicant" + (pendingReviewCount == 1 ? "" : "s") + " waiting" %></h3>
          <p>
            <%= pendingReviewCount > 0
              ? "Review the newest submissions first so strong candidates do not sit idle in the queue."
              : "Your current queue is clear. Use the applicant page when you need to revisit decisions or review progress." %>
          </p>
        </div>
        <a class="btn-secondary" href="<%= request.getContextPath() %>/mo/applicants">Review Applicants</a>
      </section>

      <section class="panel dashboard-insight-card">
        <div>
          <span class="dashboard-kicker">Workflow</span>
          <h3><%= totalAttentionCount == 0 ? "Ready for the next hiring round" : "Recruitment work is active" %></h3>
          <p>
            <%= totalAttentionCount == 0
              ? "Create a post when the next module needs TA support."
              : "Use this dashboard as the starting point before opening detailed applicant and job workflows." %>
          </p>
        </div>
        <a class="btn-secondary" href="<%= request.getContextPath() %>/mo/post-job">Open Job Form</a>
      </section>
    </div>
  </div>
</body>
</html>
