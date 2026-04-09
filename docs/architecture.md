# TA Recruitment System Architecture

## Overview
- Frontend views are JSP pages under `src/main/webapp/WEB-INF/views/`.
- Request handling uses Jakarta Servlet classes under `src/main/java/com/bupt/ta/servlet/`.
- Business logic lives in `service/`.
- JSON file access lives in `repository/`.
- Persistent storage is file-based under `storage/json/` and `storage/uploads/cv/`.

## Request Flow
1. Browser sends request to a servlet route such as `/auth/login` or `/applicant/profile`.
2. Servlet reads request parameters and session data.
3. Servlet calls the matching service.
4. Service validates business rules and uses repositories to read or write JSON files.
5. Servlet forwards to JSP or redirects to the next route.

## Storage Files
- `storage/json/users.json`: account records and roles
- `storage/json/applicantProfiles.json`: applicant profile and CV metadata
- `storage/json/jobs.json`: posted TA jobs
- `storage/json/applications.json`: job applications and decisions
- `storage/uploads/cv/`: uploaded CV files

## Main Packages
- `model`: core entities and enums
- `dto`: form objects and view summary objects
- `util`: session, hashing, validation, ID generation, storage helpers
- `repository`: file-based JSON persistence
- `service`: business logic orchestration
- `servlet`: route handlers by role/module

## Auth And Roles
- Authentication uses `HttpSession`.
- Session keys:
  - `currentUserId`
  - `currentUsername`
  - `currentUserRole`
- Supported roles:
  - `TA_APPLICANT`
  - `MODULE_ORGANISER`
  - `ADMINISTRATOR`

## Initial Demo Accounts
- Username: `applicant1`, role: `TA_APPLICANT`, password: `Password123`
- Username: `mo1`, role: `MODULE_ORGANISER`, password: `Password123`
- Username: `admin1`, role: `ADMINISTRATOR`, password: `Password123`

## Notes
- Existing static prototypes are preserved in `prototype/html/`.
- Runtime storage root defaults to `storage/` under the project root, or can be overridden by system property `ta.storage.root`.
