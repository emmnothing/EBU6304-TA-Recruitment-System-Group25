# ta_dashboard

## 1. Page purpose
- Main dashboard for TA applicants.
- Entry point to profile, jobs, and status pages.

## 2. Route and access role
- View: `applicant/ta_dashboard.jsp`
- Servlet: `ApplicantDashboardServlet`
- Route: `GET /applicant/dashboard`
- Access: `TA_APPLICANT`

## 3. Frontend structure
- Top bar with dashboard links
- Summary cards
- Three feature cards for profile, jobs, and status

## 4. Form fields and variable names
- Cards: `profileCard`, `jobsCard`, `statusCard`

## 5. Servlet methods and request parameters
- `doGet()`: no form parameters, loads summary for current session user

## 6. Service and repository functions used
- `DashboardService.getApplicantSummary(userId)`
- `ApplicationService.countStatusSummary(userId)`

## 7. JSON fields read or written
- Reads `applications.json`
- Uses `applicantUserId`, `status`

## 8. Validation, edge cases, and manual test checklist
- Unauthenticated access should redirect to login
- Wrong role should redirect to the correct dashboard
- Summary values should update after applications are created
