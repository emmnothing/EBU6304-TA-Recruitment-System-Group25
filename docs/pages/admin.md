# admin

## 1. Page purpose
- Show system-wide overview for administrators.

## 2. Route and access role
- View: `admin/admin.jsp`
- Servlet: `AdminDashboardServlet`
- Route: `GET /admin/dashboard`
- Access: `ADMINISTRATOR`

## 3. Frontend structure
- Four summary cards
- Workload overview table
- Records overview list

## 4. Form fields and variable names
- Summary container: `adminSummary`
- Table: `workloadTable`
- Records list: `recordsList`

## 5. Servlet methods and request parameters
- `doGet()`: loads admin overview without form parameters

## 6. Service and repository functions used
- `AdminService.getAdminOverview()`
- `AdminService.buildWorkloadRows(users, applications)`

## 7. JSON fields read or written
- Reads `users.json`, `jobs.json`, `applications.json`
- Uses applicant role counts, open jobs, selected applications, workload status labels

## 8. Validation, edge cases, and manual test checklist
- Admin-only access
- Summary numbers should reflect current JSON data
- Workload should increase when selected applications increase
