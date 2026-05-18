<%@ page import="com.bupt.ta.dto.ApplicantDashboardSummary" %>
<%@ page import="com.bupt.ta.dto.ApplicantProfileCompleteness" %>
<%@ page import="com.bupt.ta.dto.ApplicationStatusViewItem" %>
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

private String displayOrFallback(String value, String fallback) {
    return value == null || value.isBlank() ? fallback : escapeHtml(value);
}
%>
<%
ApplicantDashboardSummary summary = (ApplicantDashboardSummary) request.getAttribute("summary");
ApplicantProfileCompleteness profileCompleteness = (ApplicantProfileCompleteness) request.getAttribute("profileCompleteness");
ApplicationStatusViewItem upcomingInterview = (ApplicationStatusViewItem) request.getAttribute("upcomingInterview");
String currentUsername = (String) request.getAttribute("currentUsername");
Integer unreadNotificationCount = (Integer) request.getAttribute("unreadNotificationCount");
int appliedCount = summary == null ? 0 : summary.getAppliedCount();
int underReviewCount = summary == null ? 0 : summary.getUnderReviewCount();
int interviewScheduledCount = summary == null ? 0 : summary.getInterviewScheduledCount();
int selectedCount = summary == null ? 0 : summary.getSelectedCount();
int unreadCount = unreadNotificationCount == null ? 0 : unreadNotificationCount;
int activeApplicationCount = underReviewCount + interviewScheduledCount;
int totalApplicationCount = appliedCount + underReviewCount + interviewScheduledCount + selectedCount;
String priorityTitle = "Browse suitable TA openings";
String priorityText = "Your dashboard is ready. Review current openings and apply when a module matches your skills.";
String priorityHref = "/applicant/jobs";
String priorityAction = "Browse Jobs";

if (profileCompleteness != null && !profileCompleteness.isComplete()) {
    priorityTitle = "Finish your profile first";
    priorityText = "Applications are stronger when your programme, skills, statement, and CV are complete.";
    priorityHref = "/applicant/profile";
    priorityAction = "Update Profile";
} else if (interviewScheduledCount > 0) {
    priorityTitle = "Prepare for scheduled interviews";
    priorityText = "You have interview activity waiting in your application status timeline.";
    priorityHref = "/applicant/status";
    priorityAction = "View Applications";
} else if (unreadCount > 0) {
    priorityTitle = "Review new notifications";
    priorityText = "There are unread updates about your submissions or review progress.";
    priorityHref = "/applicant/notifications";
    priorityAction = "Open Notifications";
} else if (totalApplicationCount == 0) {
    priorityTitle = "Submit your first TA application";
    priorityText = "Start from available jobs, compare requirements, and apply once your profile is complete.";
    priorityHref = "/applicant/jobs";
    priorityAction = "Find Jobs";
}
%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>TA Applicant Dashboard</title>
  <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/style.css?v=dashboard-interview-20260514">
</head>
<body class="app-page">
  <div class="app-shell">
    <%
    request.setAttribute("roleNavPage", "dashboard");
    request.setAttribute("roleNavRoleLabel", "Applicant");
    request.setAttribute("roleNavTitle", "TA Recruitment System");
    request.setAttribute("roleNavSubtitle", "Welcome, <strong>" + currentUsername + "</strong>. Use this dashboard to manage your application journey.");
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

    <div class="dashboard-overview-grid">
      <% if (profileCompleteness != null) { %>
        <section class="panel profile-progress-panel dashboard-overview-card">
          <div>
            <div class="profile-progress-header">
              <div>
                <span class="dashboard-kicker">Profile readiness</span>
                <h2><%= profileCompleteness.getCompletionLabel() %></h2>
                <p>
                  <%= profileCompleteness.isComplete()
                    ? "Your applicant profile is ready for TA applications."
                    : "Complete the remaining items before submitting stronger TA applications." %>
                </p>
              </div>
              <a class="btn-secondary" href="<%= request.getContextPath() %>/applicant/profile">Update Profile</a>
            </div>
            <div class="profile-progress-track" aria-label="<%= profileCompleteness.getCompletionLabel() %>">
              <div class="profile-progress-fill" style="width:<%= profileCompleteness.getCompletionPercentage() %>%;"></div>
            </div>
            <% if (!profileCompleteness.isComplete()) { %>
              <div class="missing-items">
                <% for (String missingItem : profileCompleteness.getMissingItems()) { %>
                  <span><%= escapeHtml(missingItem) %></span>
                <% } %>
              </div>
            <% } %>
          </div>
        </section>
      <% } %>

      <section class="panel profile-progress-panel dashboard-overview-card upcoming-interview-card">
        <div>
          <div class="profile-progress-header">
            <div>
              <span class="dashboard-kicker">Upcoming Interview</span>
              <% if (upcomingInterview == null) { %>
                <h2>No interview scheduled</h2>
                <p>When an organiser schedules your next interview, the key details will appear here.</p>
              <% } else { %>
                <h2><%= DisplayFormatUtil.formatDateTime(upcomingInterview.getInterviewScheduledAt()) %></h2>
                <p>
                  <%= escapeHtml(upcomingInterview.getModuleCode()) %>
                  <%= upcomingInterview.getJobTitle() == null || upcomingInterview.getJobTitle().isBlank() ? "" : " - " + escapeHtml(upcomingInterview.getJobTitle()) %>
                </p>
              <% } %>
            </div>
            <a class="btn-secondary" href="<%= request.getContextPath() %>/applicant/status">View Status</a>
          </div>

          <div class="interview-detail-grid">
            <div>
              <strong>Mode</strong>
              <span><%= upcomingInterview == null ? "-" : displayOrFallback(upcomingInterview.getInterviewMode(), "To be confirmed") %></span>
            </div>
            <div>
              <strong>Location</strong>
              <span><%= upcomingInterview == null ? "-" : displayOrFallback(upcomingInterview.getInterviewLocation(), "To be confirmed") %></span>
            </div>
          </div>
        </div>
      </section>
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
            <span class="dashboard-kicker">Application pulse</span>
            <h2><%= activeApplicationCount %> active item<%= activeApplicationCount == 1 ? "" : "s" %></h2>
            <p><%= totalApplicationCount == 0 ? "No submissions yet. New applications will appear here after you apply." : "A quick snapshot of where your TA applications currently stand." %></p>
          </div>
          <a class="btn-secondary" href="<%= request.getContextPath() %>/applicant/status">Details</a>
        </div>
        <div class="dashboard-metric-row">
          <div>
            <strong><%= appliedCount %></strong>
            <span>New</span>
          </div>
          <div>
            <strong><%= underReviewCount %></strong>
            <span>Under review</span>
          </div>
          <div>
            <strong><%= interviewScheduledCount %></strong>
            <span>Interviews</span>
          </div>
          <div>
            <strong><%= selectedCount %></strong>
            <span>Selected</span>
          </div>
        </div>
      </section>
    </div>

    <div class="dashboard-insight-grid">
      <section class="panel dashboard-insight-card">
        <div>
          <span class="dashboard-kicker">Jobs</span>
          <h3>Find posts that match your profile</h3>
          <p>
            <%= profileCompleteness != null && profileCompleteness.isComplete()
              ? "Your profile is complete, so you can focus on comparing requirements and deadlines."
              : "Finish the missing profile fields first, then use the jobs page to apply with fewer blockers." %>
          </p>
        </div>
        <a class="btn-secondary" href="<%= request.getContextPath() %>/applicant/jobs">Browse Openings</a>
      </section>

      <section class="panel dashboard-insight-card">
        <div>
          <span class="dashboard-kicker">Applications</span>
          <h3><%= totalApplicationCount == 0 ? "No applications submitted yet" : totalApplicationCount + " total submission" + (totalApplicationCount == 1 ? "" : "s") %></h3>
          <p>
            <%= interviewScheduledCount > 0
              ? "Interview updates should be checked first so you do not miss scheduling details."
              : "Use the status page when you need a full timeline of each submitted application." %>
          </p>
        </div>
        <a class="btn-secondary" href="<%= request.getContextPath() %>/applicant/status">Track Progress</a>
      </section>

      <section class="panel dashboard-insight-card">
        <div>
          <span class="dashboard-kicker">Notifications</span>
          <h3><%= unreadCount %> unread update<%= unreadCount == 1 ? "" : "s" %></h3>
          <p>
            <%= unreadCount > 0
              ? "Review these updates before submitting more applications."
              : "No unread notifications right now. New review and decision updates will appear here." %>
          </p>
        </div>
        <a class="btn-secondary" href="<%= request.getContextPath() %>/applicant/notifications">Review Updates</a>
      </section>
    </div>
  </div>
</body>
</html>
