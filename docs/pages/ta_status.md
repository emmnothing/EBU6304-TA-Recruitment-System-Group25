# ta_status

## 1. Page purpose
- Show application summary and detailed application records for the current applicant.

## 2. Route and access role
- View: `applicant/ta_status.jsp`
- Servlet: `ApplicantStatusServlet`
- Route: `GET /applicant/status`
- Access: `TA_APPLICANT`

## 3. Frontend structure
- Four summary cards
- Application status table

## 4. Form fields and variable names
- Cards container: `summaryCards`
- Table: `applicationStatusTable`

## 5. Servlet methods and request parameters
- `doGet()`: no request parameters, only current session user

## 6. Service and repository functions used
- `ApplicationService.getApplicantStatusList(userId)`
- `ApplicationService.countStatusSummary(userId)`

## 7. JSON fields read or written
- Reads `applications.json`
- Reads `jobs.json`
- Uses `jobId`, `status`, `appliedAt`, `remarks`

## 8. Validation, edge cases, and manual test checklist
- Empty list should show empty state
- Selected or rejected decisions should appear after MO review
- Table rows should match application history
