# mo_applicants

## 1. Page purpose
- Review applicants for jobs posted by the current module organiser.
- Record review decisions and remarks.

## 2. Route and access role
- View: `mo/mo_applicants.jsp`
- Servlet: `ModuleOrganiserApplicantsServlet`
- Routes: `GET /mo/applicants`, `POST /mo/applicants/decision`
- Access: `MODULE_ORGANISER`

## 3. Frontend structure
- Filter bar
- Applicant list
- Applicant detail panel
- Decision form

## 4. Form fields and variable names
- Form: `applicantFilterForm`, `decisionForm`
- Inputs: `jobSelect`, `statusSelect`, `keywordInput`, `remarksInput`
- Container: `applicantTable`

## 5. Servlet methods and request parameters
- `doGet()`: read `jobId`, `status`, `keyword`, optional `applicationId`
- `doPost()`: read `applicationId`, `decision`, `remarks`, `jobId`

## 6. Service and repository functions used
- `ApplicationService.getApplicantsForMo(...)`
- `ApplicationService.getApplicationDetail(...)`
- `ApplicationService.updateDecision(applicationId, decision, remarks, moUserId)`

## 7. JSON fields read or written
- Reads `jobs.json`, `users.json`, `applicantProfiles.json`, `applications.json`
- Updates `applications.json` fields `status`, `reviewedByUserId`, `reviewedAt`, `remarks`

## 8. Validation, edge cases, and manual test checklist
- MO should only see their own job applications
- Empty filter result should show empty state
- Decision update should change applicant status for TA and admin pages
