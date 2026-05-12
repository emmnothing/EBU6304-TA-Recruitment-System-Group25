<%@ page import="com.bupt.ta.model.ApplicantProfile" %>
<%
ApplicantProfile profile = (ApplicantProfile) request.getAttribute("profile");
String flashType = (String) request.getAttribute("flashType");
String flashMessage = (String) request.getAttribute("flashMessage");
String currentUsername = (String) request.getAttribute("currentUsername");
%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>TA Recruitment System - My Profile</title>
  <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/style.css?v=cv-upload-20260512">
</head>
<body class="app-page">
  <div class="app-shell">
    <div class="topbar panel">
      <div class="brand">
        <h1>My Profile</h1>
        <p>Signed in as <strong><%= currentUsername %></strong>. Complete your profile before applying for jobs.</p>
      </div>
      <div class="top-links">
        <a href="<%= request.getContextPath() %>/applicant/dashboard">Back to Dashboard</a>
        <a href="<%= request.getContextPath() %>/applicant/jobs">Available Jobs</a>
        <a href="<%= request.getContextPath() %>/applicant/notifications">Notifications</a>
        <a href="<%= request.getContextPath() %>/account/delete">Delete Account</a>
        <a href="<%= request.getContextPath() %>/auth/logout">Logout</a>
      </div>
    </div>

    <% if (flashMessage != null) { %>
      <div class="flash-message <%= flashType %>"><%= flashMessage %></div>
    <% } %>

    <div class="panel">
      <h2>Applicant Profile Form</h2>
      <p style="margin-bottom:18px;">The fields below map directly to the later Servlet/JSP backend and JSON storage.</p>

      <form id="profileForm" method="post" action="<%= request.getContextPath() %>/applicant/profile" enctype="multipart/form-data">
        <div class="form-grid">
          <div class="field">
            <label for="studentIdInput">Student ID</label>
            <input type="text" id="studentIdInput" name="studentId" value="<%= profile == null ? "" : profile.getStudentId() %>" required>
          </div>

          <div class="field">
            <label for="programmeInput">Programme</label>
            <input type="text" id="programmeInput" name="programme" value="<%= profile == null ? "" : profile.getProgramme() %>" required>
          </div>

          <div class="field">
            <label for="yearOfStudyInput">Year of Study</label>
            <input type="text" id="yearOfStudyInput" name="yearOfStudy" value="<%= profile == null ? "" : profile.getYearOfStudy() %>" required>
          </div>

          <div class="field">
            <label for="cvFileInput">CV Upload</label>
            <div class="file-upload-control">
              <input class="file-upload-input" type="file" id="cvFileInput" name="cvFile" accept=".pdf,.doc,.docx">
              <label class="file-upload-surface" for="cvFileInput">
                <span class="file-upload-button">Choose CV File</span>
                <span class="file-upload-name" id="cvFileName">No file selected</span>
              </label>
            </div>
            <div class="hint">Accepted formats: PDF, DOC, DOCX.</div>
          </div>

          <div class="field full-width">
            <label for="skillsInput">Skills</label>
            <textarea id="skillsInput" name="skills" placeholder="List relevant skills, experience, or tools"><%= profile == null || profile.getSkills() == null ? "" : profile.getSkills() %></textarea>
          </div>

          <div class="field full-width">
            <label for="preferredModulesInput">Preferred Modules</label>
            <textarea id="preferredModulesInput" name="preferredModules" placeholder="State the modules you prefer to support"><%= profile == null || profile.getPreferredModules() == null ? "" : profile.getPreferredModules() %></textarea>
          </div>

          <div class="field full-width">
            <label for="personalStatementInput">Personal Statement</label>
            <textarea id="personalStatementInput" name="personalStatement" placeholder="Briefly introduce your strengths and motivation"><%= profile == null || profile.getPersonalStatement() == null ? "" : profile.getPersonalStatement() %></textarea>
          </div>

          <div class="field full-width">
            <div class="inline-row wrap">
              <div>
                <% if (profile != null && profile.getCvFileName() != null && !profile.getCvFileName().isBlank()) { %>
                  <div class="hint">Current CV file: <strong><%= profile.getCvFileName() %></strong></div>
                  <div class="hint"><a href="<%= request.getContextPath() %>/cv/download">Download current CV</a></div>
                <% } else { %>
                  <div class="hint">No CV uploaded yet.</div>
                <% } %>
              </div>
              <button class="btn-secondary" type="submit">Save Profile</button>
            </div>
          </div>
        </div>
      </form>
    </div>
  </div>
  <script src="<%= request.getContextPath() %>/assets/js/app.js?v=cv-upload-20260512"></script>
</body>
</html>
