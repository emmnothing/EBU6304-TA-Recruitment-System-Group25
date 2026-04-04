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

## Iteration 2 Progress

The current `wsh_update` branch includes the following iteration updates.

### Phase 1

- Enforced profile completion before job application
- Enforced CV upload before job application
- Added job lifecycle handling:
  - manual closing
  - automatic closing after deadline
  - automatic closing after vacancy limit is reached
- Added remaining vacancy display and apply-button disable reasons
- Added lifecycle metadata to jobs and status update timestamps to applications

### Phase 2

- Added protected CV download support
- Applicant can download their current uploaded CV
- Module organisers can download applicant CVs from the review page
- Improved application status presentation with clearer labels and last-updated timestamps

### Phase 3

- Added in-system applicant notifications
- Notifications are created for:
  - successful application submission
  - review decision updates
- Added applicant notification page and unread count on dashboard
- Expanded administrator dashboard with:
  - total applicants
  - total jobs
  - total applications
  - open jobs
  - pending review count
  - workload overview
  - recent jobs
  - recent applications

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
