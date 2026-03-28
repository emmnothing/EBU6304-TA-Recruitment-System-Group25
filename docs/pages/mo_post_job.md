# mo_post_job

## 1. Page purpose
- Create a new TA recruitment job post.

## 2. Route and access role
- View: `mo/mo_post_job.jsp`
- Servlet: `ModuleOrganiserPostJobServlet`
- Routes: `GET /mo/post-job`, `POST /mo/post-job`
- Access: `MODULE_ORGANISER`

## 3. Frontend structure
- Job details form
- Requirements textarea
- Submit button

## 4. Form fields and variable names
- Form: `postJobForm`
- Inputs: `moduleCodeInput`, `moduleNameInput`, `jobTitleInput`, `vacanciesInput`, `weeklyHoursInput`, `applicationDeadlineInput`, `locationModeInput`, `descriptionInput`, `requirementsInput`

## 5. Servlet methods and request parameters
- `doGet()`: render form
- `doPost()`: read all job form fields

## 6. Service and repository functions used
- `JobService.createJob(jobForm, moUserId)`
- `JobRepository.findAll()`
- `JobRepository.saveAll(jobs)`

## 7. JSON fields read or written
- Writes `jobs.json`
- Stores `jobId`, `moduleCode`, `moduleName`, `jobTitle`, `vacancies`, `weeklyHours`, `applicationDeadline`, `locationMode`, `description`, `requirements`, `postedByUserId`, `status`, `createdAt`

## 8. Validation, edge cases, and manual test checklist
- Required fields cannot be blank
- Vacancies and weekly hours must be positive integers
- Success should create an `OPEN` job
