# admin_notifications

## 1. Page purpose
- Provide administrator-only system announcement management.
- Send one announcement to active TA applicants, active module organisers, or both groups.
- Show recent announcement delivery history grouped from recipient-level notification rows.

## 2. Route and access role
- View: `admin/admin_notifications.jsp`
- Servlet: `AdminNotificationManagementServlet`
- Routes:
  - `GET /admin/notifications`
  - `POST /admin/notifications/send`
- Access: `ADMINISTRATOR`

## 3. Frontend structure
- Recipient count cards for all recipients, TA applicants, and module organisers
- Announcement compose form
- Delivery rule summary
- Recent announcements table with audience, sender, sent time, sent count, read count, and unread count

## 4. Form fields and variable names
- `audience`: `all`, `ta_applicants`, or `module_organisers`
- `title`: announcement title, max 120 characters
- `message`: announcement body, max 1000 characters
- View attributes:
  - `recipientSummary`
  - `recentAnnouncements`

## 5. Servlet methods and request parameters
- `doGet()`: loads recipient counts and recent announcement history
- `doPost()`: builds `AdminAnnouncementForm`, sends the announcement, stores a flash message, and redirects back to `/admin/notifications`
- POST/Redirect/GET is used so refreshing the browser does not send duplicate announcements

## 6. Service and repository functions used
- `NotificationService.sendSystemAnnouncement(form, adminUserId)`
- `NotificationService.getAnnouncementRecipientSummary()`
- `NotificationService.getRecentSystemAnnouncements()`
- `NotificationRepository.findAll()`
- `NotificationRepository.saveAll(notifications)`
- `UserRepository.findAll()`

## 7. JSON fields read or written
- Reads active users and roles from `users.json`
- Writes one notification row per recipient into `notifications.json`
- System announcements use:
  - `type = SYSTEM_ANNOUNCEMENT`
  - `relatedEntityType = system-announcement`
  - shared `relatedEntityId` for grouping
  - `createdByUserId` for the sender
  - `audience` for the selected target group

## 8. Validation, edge cases, and manual test checklist
- Only administrators can access the page or send announcements
- Invalid audience values are rejected by the service layer
- Blank title or message is rejected
- Disabled accounts are excluded from recipients
- If no active recipients match the selected audience, no notification rows are written
- After sending to applicants, login as a TA applicant and confirm the announcement appears in `/applicant/notifications`
- After sending to module organisers, login as an MO and confirm the announcement appears in `/mo/notifications`
