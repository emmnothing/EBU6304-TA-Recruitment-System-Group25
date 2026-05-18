# admin_users

## 1. Page purpose
- Provide administrator-only user management for all platform accounts.
- Support filtering, account enable/disable, password reset, and quick global exports.

## 2. Route and access role
- View: `admin/admin_users.jsp`
- Servlet: `AdminUserManagementServlet`
- Routes:
  - `GET /admin/users`
  - `POST /admin/users/toggle-status`
  - `POST /admin/users/reset-password`
- Access: `ADMINISTRATOR`

## 3. Frontend structure
- Summary cards for total, active, inactive, and role-based user counts
- Filter form for role, status, keyword, and sorting
- Global export action strip
- User directory table with per-user actions

## 4. Form fields and variable names
- Filter object: `userFilter`
- View model: `userManagementView`
- Table list: `userRecords`
- Target user field: `userId`
- Default password display: `defaultResetPassword`

## 5. Servlet methods and request parameters
- `doGet()`: loads filtered user list and summary counts
- `doPost()`: dispatches to toggle account status or reset password
- Request parameters:
  - `role`
  - `status`
  - `keyword`
  - `sortBy`
  - `sortDirection`
  - `userId`

## 6. Service and repository functions used
- `AdminService.getUserManagementView(filter, currentAdminId)`
- `AdminService.toggleUserStatus(targetUserId, adminUserId)`
- `AdminService.resetUserPassword(targetUserId)`
- `UserRepository.findAll()`
- `UserRepository.saveAll(users)`

## 7. JSON fields read or written
- Reads `users.json`
- Reads `jobs.json` and `applications.json` for activity summaries
- Writes updated `active`, `statusUpdatedAt`, `passwordHash`, and `tokenVersion` values to `users.json`

## 8. Validation, edge cases, and manual test checklist
- Only administrators can access the page or perform actions
- Administrators cannot disable or enable their own account through this page
- The final active administrator account cannot be disabled
- Disabled users are blocked from future authenticated access
- Password reset currently restores the shared temporary password `Password123`
- Account status changes and password resets invalidate old remember-me JWT cookies by incrementing `tokenVersion`
