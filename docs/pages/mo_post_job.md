# mo_post_job

## 1. Page purpose
- Create a new TA recruitment job post.
- Edit an existing open TA job post created by the current module organiser.
- Delete a job post that has no applications yet.

## 2. Route and access role
- View: `mo/mo_post_job.jsp`
- Servlet: `ModuleOrganiserPostJobServlet`
- Routes: `GET /mo/post-job`, `POST /mo/post-job`, `POST /mo/post-job/edit`, `POST /mo/post-job/close`, `POST /mo/post-job/delete`
- Access: `MODULE_ORGANISER`

## 3. Frontend structure
- Job details form in create or edit mode
- Edit-mode prompt and cancel action
- My job posts list
- Edit button for open jobs
- Delete button for jobs with zero applications
- Requirements textarea
- Submit button

## 4. Form fields and variable names
- Form: `postJobForm`
- Hidden input when editing: `jobId`
- Inputs: `moduleCodeInput`, `moduleNameInput`, `jobTitleInput`, `vacanciesInput`, `weeklyHoursInput`, `applicationDeadlineInput`, `locationModeInput`, `descriptionInput`, `requirementsInput`

## 5. Servlet methods and request parameters
- `doGet()`: render create form or load edit mode by `editJobId`
- `doPost()`: create, update, or close a job based on route
- Edit request parameters: `jobId`, `moduleCode`, `moduleName`, `jobTitle`, `vacancies`, `weeklyHours`, `applicationDeadline`, `locationMode`, `description`, `requirements`

## 6. Service and repository functions used
- `JobService.createJob(jobForm, moUserId)`
- `JobService.updateJob(jobId, jobForm, moUserId)`
- `JobService.closeJobManually(jobId, moUserId)`
- `JobService.deleteJob(jobId, moUserId)`
- `JobRepository.findAll()`
- `JobRepository.saveAll(jobs)`

## 7. JSON fields read or written
- Writes `jobs.json`
- Stores `jobId`, `moduleCode`, `moduleName`, `jobTitle`, `vacancies`, `weeklyHours`, `applicationDeadline`, `locationMode`, `description`, `requirements`, `postedByUserId`, `status`, `createdAt`
- Updates job fields in place when editing an open post
- Prevents vacancies from dropping below already selected applicants

## 8. Validation, edge cases, and manual test checklist
- Required fields cannot be blank
- Vacancies and weekly hours must be positive integers
- Success should create an `OPEN` job
- Only the job owner can edit the job
- Closed jobs cannot be edited
- Editing should prefill the existing form values
- Deletion should only be allowed when the job has no applications
