# TA Recruitment System

TA Recruitment System is a Jakarta Servlet + JSP web application for managing TA recruitment workflows across three roles:

- `TA_APPLICANT`
- `MODULE_ORGANISER`
- `ADMINISTRATOR`

The project currently uses file-based JSON storage under `storage/` and is packaged as a Maven `war` application for deployment on Tomcat.

## Tech Stack

- Java 25
- Maven 3.9+
- Jakarta Servlet 6.0 / JSP 3.1
- Gson
- Apache Tomcat 10.1

## Project Structure

- `src/main/java/com/bupt/ta/model`: domain models and enums
- `src/main/java/com/bupt/ta/dto`: form/view DTOs
- `src/main/java/com/bupt/ta/repository`: JSON-backed repositories
- `src/main/java/com/bupt/ta/service`: business logic
- `src/main/java/com/bupt/ta/servlet`: route handlers
- `src/main/webapp/WEB-INF/views`: JSP pages
- `storage/json`: persistent data files
- `storage/uploads/cv`: uploaded CV files
- `docs`: architecture and page notes

## Demo Accounts

- Applicant: `applicant1` / `Password123`
- Module Organiser: `mo1` / `Password123`
- Administrator: `admin1` / `Password123`

## Iteration 2 Issue Progress

The current `wsh_update` branch addresses the Iteration 2 plan as follows.

### Completed

1. Enforce profile completion before job application, including required CV upload.
   - Applicant profile must contain required academic fields.
   - CV upload is required before application submission.
   - Applicant jobs page disables apply actions and shows the blocking reason.

2. Add job lifecycle management, such as manual closing, automatic closing after deadline, and vacancy limit handling.
   - Module organisers can manually close jobs.
   - Jobs auto-close after the deadline.
   - Jobs auto-close after selected applicants reach the vacancy limit.
   - Remaining vacancies and closure reasons are surfaced in the UI.

3. Allow module organisers to view and download applicant CV files directly from the review page.
   - Protected CV download route added.
   - Applicants can also download their own uploaded CV from the profile page.

4. Improve applicant status tracking with clearer progress states and timestamped updates.
   - Applicant status page now shows clearer labels such as `Submitted`, `Under Review`, `Selected`, and `Rejected`.
   - Application records now show `Last Updated`.
   - Review details expose `Last Reviewed At`.

5. Add in-system notifications for application submission, review updates, selection, and rejection.
   - Notifications are stored in `storage/json/notifications.json`.
   - Submission creates a notification.
   - Review decisions create a notification.
   - Applicant dashboard shows unread notification count.
   - Applicant notification page is available in the UI.

### Partially Completed

6. Expand administrator functions with basic user management, job monitoring, and recruitment overview controls.
   - Recruitment overview and monitoring are implemented on the administrator dashboard.
   - Recent jobs, recent applications, workload overview, and summary metrics are implemented.
   - Direct admin management operations for users/jobs/applications are not implemented yet.

### Not Yet Completed

7. Add sorting, pagination, and export options for job lists, applicant lists, and admin tables.

8. Improve form validation and feedback on both client and server sides.
   - Basic server-side validation has been strengthened for profile completion, CV upload, deadline checks, and vacancy checks.
   - Broader validation coverage and richer field-level feedback are still pending.

9. Enhance UI usability with disabled applied buttons, clearer status badges, and better empty/error states.
   - Disabled apply buttons, status labels, and several empty states are implemented.
   - A broader UI polish pass across all pages is still pending.

10. Strengthen security with stricter file validation, stronger password rules, and better session handling.
   - CV size and extension checks were improved.
   - Protected CV access was added.
   - Password rule upgrades and stronger session hardening are still pending.

## Local Run

### Build

```powershell
mvn clean package
```

Output:

```text
target/ta-recruitment-system.war
```

### Run on Tomcat

Example with local storage root bound to the repository:

```powershell
cmd /c "set CATALINA_HOME=D:\tools\apache-tomcat-10.1.53&& set CATALINA_BASE=D:\tools\apache-tomcat-10.1.53&& set CATALINA_OPTS=-Dta.storage.root=D:\code\fuckingsoftware\fuckingsoftware\storage&& D:\tools\apache-tomcat-10.1.53\bin\catalina.bat start"
```

Default local access URL:

- `http://localhost:8081/ta-recruitment-system/`

## Storage Files

- `storage/json/users.json`
- `storage/json/applicantProfiles.json`
- `storage/json/jobs.json`
- `storage/json/applications.json`
- `storage/json/notifications.json`

## Notes

- Uploaded CV files are stored under `storage/uploads/cv/`
- Notification storage file is created automatically on first notification write
- The project currently remains JSON-file-based; no database is required for local development
