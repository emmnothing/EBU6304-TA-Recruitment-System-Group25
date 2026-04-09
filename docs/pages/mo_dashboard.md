# mo_dashboard

## 1. Page purpose
- Main dashboard for module organisers.
- Entry point to posting jobs and reviewing applicants.

## 2. Route and access role
- View: `mo/mo_dashboard.jsp`
- Servlet: `ModuleOrganiserDashboardServlet`
- Route: `GET /mo/dashboard`
- Access: `MODULE_ORGANISER`

## 3. Frontend structure
- Top bar with quick links
- Summary cards
- Two feature cards

## 4. Form fields and variable names
- Cards: `postJobCard`, `viewApplicantsCard`, `summaryCard`

## 5. Servlet methods and request parameters
- `doGet()`: loads summary using session user ID

## 6. Service and repository functions used
- `DashboardService.getMoSummary(userId)`

## 7. JSON fields read or written
- Reads `jobs.json`
- Reads `applications.json`

## 8. Validation, edge cases, and manual test checklist
- Only MO users can access
- Counts should only include jobs posted by the current MO
