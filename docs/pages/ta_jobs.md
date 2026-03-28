# ta_jobs

## 1. Page purpose
- Show open TA jobs and allow applicants to submit an application.

## 2. Route and access role
- View: `applicant/ta_jobs.jsp`
- Servlet: `ApplicantJobsServlet`
- Routes: `GET /applicant/jobs`, `POST /applicant/jobs/apply`
- Access: `TA_APPLICANT`

## 3. Frontend structure
- Filter form
- Job cards list
- Apply button per job

## 4. Form fields and variable names
- Form: `jobFilterForm`
- Inputs: `keywordInput`, `moduleInput`, `statusSelect`
- Containers: `jobList`
- Button: `applyButton`

## 5. Servlet methods and request parameters
- `doGet()`: read `keyword`, `moduleCode`, `status`
- `doPost()`: read `jobId`

## 6. Service and repository functions used
- `JobService.getOpenJobs(jobFilter)`
- `ApplicationService.applyForJob(userId, jobId)`
- `ApplicationService.hasAlreadyApplied(userId, jobId)`

## 7. JSON fields read or written
- Reads `jobs.json`
- Reads and writes `applications.json`
- Uses `jobId`, `jobTitle`, `moduleCode`, `status`, `applicantUserId`, `appliedAt`

## 8. Validation, edge cases, and manual test checklist
- Empty job list should show empty state
- Duplicate application should fail
- Successful apply should create an `APPLIED` record
