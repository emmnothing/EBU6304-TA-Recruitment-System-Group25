# admin

## 1. Page purpose
- Show system-wide overview for administrators.
- Provide quick entry points into user management, system announcements, and global CSV export.

## 2. Route and access role
- View: `admin/admin.jsp`
- Servlet: `AdminDashboardServlet`
- Route: `GET /admin/dashboard`
- Access: `ADMINISTRATOR`
- Related admin routes:
  - `GET /admin/users`
  - `GET /admin/notifications`
  - `POST /admin/notifications/send`
  - `GET /admin/export?type=users|jobs|applications|workload`

## 3. Frontend structure
- Four summary cards
- Workload overview table
- Records overview list
- Quick action panels for user management, system announcements, and export

## 4. Form fields and variable names
- Summary container: `adminSummary`
- Table: `workloadTable`
- Records list: `recordsList`
- User management link: `/admin/users`
- Announcement management link: `/admin/notifications`
- Export links: `/admin/export`

## 5. Servlet methods and request parameters
- `doGet()`: loads admin overview without form parameters

## 6. Service and repository functions used
- `AdminService.getAdminOverview()`
- `AdminService.buildWorkloadRows(users, applications)`
- `SessionUtil.exposeFlashMessage(request)`

## 7. JSON fields read or written
- Reads `users.json`, `jobs.json`, `applications.json`
- Uses applicant role counts, open jobs, selected applications, workload status labels

## 8. Validation, edge cases, and manual test checklist
- Admin-only access
- Summary numbers should reflect current JSON data
- Workload should increase when selected applications increase
- Export links should trigger CSV download
- User management shortcut should open the account administration page
- Announcement shortcut should open the system announcement management page
