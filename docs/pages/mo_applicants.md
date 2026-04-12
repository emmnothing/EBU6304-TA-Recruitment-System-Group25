# mo_applicants

## 1. Page purpose
- Review applicants for jobs posted by the current module organiser.
- Record review decisions and remarks.
- Export the filtered applicant list.
- Process decisions in batch.
- Schedule and update interview plans.

## 2. Route and access role
- View: `mo/mo_applicants.jsp`
- Servlet: `ModuleOrganiserApplicantsServlet`
- Routes: `GET /mo/applicants`, `GET /mo/applicants/export`, `POST /mo/applicants/decision`, `POST /mo/applicants/batch`, `POST /mo/applicants/interview`
- Access: `MODULE_ORGANISER`

## 3. Frontend structure
- Filter bar
- Applicant list with batch selection
- Applicant detail panel
- Decision form
- Interview scheduling form
- Export button

## 4. Form fields and variable names
- Form: `applicantFilterForm`, `decisionForm`, `batchDecisionForm`, `interviewForm`
- Inputs: `jobSelect`, `statusSelect`, `hasCvSelect`, `sortBySelect`, `sortDirectionSelect`, `keywordInput`, `remarksInput`, `batchRemarksInput`, `interviewScheduledAtInput`, `interviewModeInput`, `interviewLocationInput`, `interviewNotesInput`
- Container: `applicantTable`

## 5. Servlet methods and request parameters
- `doGet()`: read `jobId`, `status`, `keyword`, `hasCv`, `sortBy`, `sortDirection`, optional `applicationId`
- `doGet()` on export route: download filtered applicants as CSV
- `doPost()` decision route: read `applicationId`, `decision`, `remarks`
- `doPost()` batch route: read `applicationIds[]`, `decision`, `remarks`
- `doPost()` interview route: read `applicationId`, `interviewScheduledAt`, `interviewMode`, `interviewLocation`, `interviewNotes`

## 6. Service and repository functions used
- `ApplicationService.getApplicantsForMo(...)`
- `ApplicationService.getApplicationDetail(...)`
- `ApplicationService.updateDecision(applicationId, decision, remarks, moUserId)`
- `ApplicationService.batchUpdateDecision(applicationIds, decision, remarks, moUserId)`
- `ApplicationService.scheduleInterview(...)`

## 7. JSON fields read or written
- Reads `jobs.json`, `users.json`, `applicantProfiles.json`, `applications.json`
- Updates `applications.json` fields `status`, `reviewedByUserId`, `reviewedAt`, `remarks`, `statusUpdatedAt`
- Interview management also updates `interviewScheduledAt`, `interviewMode`, `interviewLocation`, `interviewNotes`

## 8. Validation, edge cases, and manual test checklist
- MO should only see their own job applications
- Empty filter result should show empty state
- Decision update should change applicant status for TA and admin pages
- Batch actions should skip invalid or unauthorized applications
- Interview scheduling should reject past timestamps
- CSV export should follow the current filter and sort conditions
