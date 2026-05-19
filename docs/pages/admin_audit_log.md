# admin_audit_log

## 1. Page purpose
- Provide administrator-only visibility into security-sensitive platform activity.
- Track account status changes, password resets, system announcement sends, CSV export downloads, and rejected CSV export attempts.

## 2. Route and access role
- View: `admin/admin_audit_log.jsp`
- Servlet: `AdminAuditLogServlet`
- Route: `GET /admin/audit-log`
- Access: `ADMINISTRATOR`

## 3. Frontend structure
- Summary cards for total events, successful events, failed attempts, export events, and account changes
- Filter form for action, outcome, keyword, and sort direction
- Audit event table with timestamp, actor, action, target, outcome, request metadata, and detail

## 4. Form fields and variable names
- Filter object: `auditLogFilter`
- View model: `auditLogView`
- Table list: `auditLogs`
- Request parameters:
  - `action`
  - `outcome`
  - `keyword`
  - `sortDirection`

## 5. Servlet methods and request parameters
- `doGet()`: validates administrator access, builds the filter, loads the audit log view, and forwards to the JSP.

## 6. Service and repository functions used
- `AuditLogService.getAuditLogView(filter)`
- `AuditLogService.recordAdminAction(...)`
- `AuditLogRepository.findAll()`
- `AuditLogRepository.append(entry)`

## 7. JSON fields read or written
- Reads and writes `storage/json/auditLogs.json`
- Each audit entry stores:
  - `auditLogId`
  - `actorUserId`
  - `actorUsername`
  - `actorRole`
  - `action`
  - `targetType`
  - `targetId`
  - `targetLabel`
  - `outcome`
  - `detail`
  - `requestMethod`
  - `requestPath`
  - `ipAddress`
  - `userAgent`
  - `createdAt`

## 8. Validation, edge cases, and manual test checklist
- Only administrators can view the audit log page.
- Successful and failed administrator account actions should both create audit entries.
- Password reset audit detail must not expose the temporary password.
- System announcement sends should record the selected audience and recipient count.
- CSV exports should record the export type and row count.
- Unsupported export types should create failed audit entries.
- The page displays at most the latest 100 matching entries to keep the table usable.
- Concurrent audit writes use a repository-level lock to reduce lost updates in JSON storage.
