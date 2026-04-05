# EBU6304 TA Recruitment System --- Group 25

A lightweight web-based Teaching Assistant Recruitment System developed for the EBU6304 Software Engineering Group Project.

## Group Members

- Norman-Ou: 2019213212 (Support TA)
- N0thing: 231222752 (Lead)
- HIROTO: 231220840 (Member)
- null428: 231220817 (Member)
- SouthwestAsiaFloat: 231220389 (Member)
- wscn04: 231221814 (Member)
- junhaoc987-cmd: 231222408 (Member)

## Project Overview

This project supports the TA recruitment workflow of BUPT International School. It aims to replace spreadsheet-heavy and form-based manual work with a lightweight web application that helps applicants, module organisers, and administrators manage recruitment more clearly and efficiently.

The current repository already contains a runnable first usable prototype built with Java Servlet/JSP, Maven, and Apache Tomcat.

## Current Prototype Scope

The current version includes the following core workflows:

- User login, registration, logout, and password reset
- TA applicant dashboard
- Applicant profile creation and CV upload
- Job browsing and application submission
- Application status checking
- Module organiser dashboard
- TA job posting
- Applicant review and decision handling
- Administrator dashboard and workload overview

## Technology Stack

### Frontend

- HTML
- CSS
- JavaScript
- JSP views

### Backend

- Java Servlet
- JSP
- Maven

### Server

- Apache Tomcat 10

### Data Storage

- Local JSON files under `storage/json`
- Uploaded CV files under `storage/uploads/cv`

## Repository Structure

```text
.
├── background/                  # backlog, early prototype files, survey results, user stories
├── docs/                        # architecture notes and page-level documentation
├── src/
│   └── main/
│       ├── java/com/bupt/ta/    # models, DTOs, repositories, services, servlets, utilities
│       └── webapp/              # JSP pages, CSS, JS, entry page
├── storage/
│   ├── json/                    # local JSON data store
│   └── uploads/cv/              # uploaded CV files
└── pom.xml                      # Maven build configuration
```

## Implemented Pages

### Authentication

- `/auth/login`
- `/auth/register`
- `/auth/forgot`

### TA Applicant

- `/applicant/dashboard`
- `/applicant/profile`
- `/applicant/jobs`
- `/applicant/status`

### Module Organiser

- `/mo/dashboard`
- `/mo/post-job`
- `/mo/applicants`

### Administrator

- `/admin/dashboard`

## How to Build and Run

### Prerequisites

- JDK 25
- Maven 3.9+
- Apache Tomcat 10.1+

### Build the Project

Run the following command in the project root:

```bash
mvn clean package -DskipTests
```

After a successful build, the deployable package will be generated at:

```text
target/ta-recruitment-system.war
```

### Deploy to Tomcat

1. Build the project with Maven.
2. Copy `target/ta-recruitment-system.war` into your Tomcat `webapps/` directory.
3. Start Tomcat.
4. Open the application in your browser:

```text
http://localhost:8080/ta-recruitment-system/
```

The application entry page redirects automatically to the login page.

## Local Data Notes

- User, job, application, and applicant profile data are stored in JSON files under `storage/json`.
- CV files are saved under `storage/uploads/cv`.
- If needed, the storage root can be overridden with the JVM property `ta.storage.root`.

## Demo Accounts

The repository currently includes three seeded demo users in `storage/json/users.json`:

- `applicant1` with role `TA_APPLICANT`
- `mo1` with role `MODULE_ORGANISER`
- `admin1` with role `ADMINISTRATOR`

If you change seeded data manually, keep role values consistent with the enum definitions in the codebase.

## Development Notes

- The project uses GitHub for collaborative branch-based development.
- `main` should remain the stable integration branch.
- Feature work should continue on separate branches and be merged through pull requests.
- Build output such as `target/` and deployed artifacts should not be committed.

## License

This repository is created for coursework and academic use.
