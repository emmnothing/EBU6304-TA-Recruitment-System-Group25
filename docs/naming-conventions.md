# Naming Conventions

## Java
- Class names: `PascalCase`
- Methods and fields: `camelCase`
- Constants: `UPPER_SNAKE_CASE`
- Packages: lowercase

## Frontend
- Form IDs: `xxxForm`
- Input IDs: `xxxInput`
- Buttons: `xxxButton`
- Error blocks: `xxxError`
- Success or flash blocks: `xxxMessage`
- List containers: `xxxList`
- Table containers: `xxxTable`
- Filter objects: `xxxFilter`

## Session Keys
- `currentUserId`
- `currentUsername`
- `currentUserRole`

## Route Groups
- Auth: `/auth/...`
- Applicant: `/applicant/...`
- Module Organiser: `/mo/...`
- Admin: `/admin/...`

## JSON Entity Fields
- `User`: `userId`, `username`, `email`, `phoneNumber`, `passwordHash`, `role`, `createdAt`
- `ApplicantProfile`: `profileId`, `userId`, `studentId`, `programme`, `yearOfStudy`, `skills`, `preferredModules`, `personalStatement`, `cvFileName`, `cvRelativePath`, `updatedAt`
- `JobPost`: `jobId`, `moduleCode`, `moduleName`, `jobTitle`, `vacancies`, `weeklyHours`, `applicationDeadline`, `locationMode`, `description`, `requirements`, `postedByUserId`, `status`, `createdAt`
- `JobApplication`: `applicationId`, `jobId`, `applicantUserId`, `status`, `appliedAt`, `reviewedByUserId`, `reviewedAt`, `statusUpdatedAt`, `remarks`, `interviewScheduledAt`, `interviewMode`, `interviewLocation`, `interviewNotes`

## Status Enums
- `JobStatus`: `OPEN`, `CLOSED`
- `ApplicationStatus`: `APPLIED`, `UNDER_REVIEW`, `INTERVIEW_SCHEDULED`, `SELECTED`, `REJECTED`

## README Template
Each page README should contain:
1. Page purpose
2. Route and access role
3. Frontend structure
4. Form fields and variable names
5. Servlet methods and request parameters
6. Service and repository functions used
7. JSON fields read or written
8. Validation, edge cases, and manual test checklist
