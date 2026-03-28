# ta_profile

## 1. Page purpose
- View and edit applicant profile data.
- Store CV metadata and uploaded CV file.

## 2. Route and access role
- View: `applicant/ta_profile.jsp`
- Servlet: `ApplicantProfileServlet`
- Routes: `GET /applicant/profile`, `POST /applicant/profile`
- Access: `TA_APPLICANT`

## 3. Frontend structure
- Profile form card
- Student and programme fields
- Skills, preferred modules, statement textareas
- CV upload field

## 4. Form fields and variable names
- Form: `profileForm`
- Inputs: `studentIdInput`, `programmeInput`, `yearOfStudyInput`, `skillsInput`, `preferredModulesInput`, `personalStatementInput`, `cvFileInput`

## 5. Servlet methods and request parameters
- `doGet()`: load current user profile
- `doPost()`: read `studentId`, `programme`, `yearOfStudy`, `skills`, `preferredModules`, `personalStatement`, multipart `cvFile`

## 6. Service and repository functions used
- `ApplicantProfileService.getProfileByUserId(userId)`
- `ApplicantProfileService.saveProfile(profileForm, cvPart, userId)`
- `ApplicantProfileService.storeCvFile(cvPart, userId)`

## 7. JSON fields read or written
- Reads and writes `applicantProfiles.json`
- Stores `profileId`, `studentId`, `programme`, `yearOfStudy`, `skills`, `preferredModules`, `personalStatement`, `cvFileName`, `cvRelativePath`, `updatedAt`

## 8. Validation, edge cases, and manual test checklist
- Student ID, programme, and year of study are required
- CV format must be PDF, DOC, or DOCX
- Saving twice should update the same profile rather than duplicate it
