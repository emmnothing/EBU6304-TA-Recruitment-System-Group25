package com.bupt.ta.service;

import com.bupt.ta.dto.AdminAnnouncementForm;
import com.bupt.ta.dto.AdminAnnouncementRecipientSummary;
import com.bupt.ta.dto.AdminAnnouncementRecord;
import com.bupt.ta.dto.OperationResult;
import com.bupt.ta.model.Notification;
import com.bupt.ta.model.Role;
import com.bupt.ta.model.User;
import com.bupt.ta.repository.NotificationRepository;
import com.bupt.ta.repository.UserRepository;
import com.bupt.ta.util.IdGenerator;
import com.bupt.ta.util.ValidationUtil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class NotificationService {
    public static final String TYPE_SYSTEM_ANNOUNCEMENT = "SYSTEM_ANNOUNCEMENT";
    public static final String ENTITY_SYSTEM_ANNOUNCEMENT = "system-announcement";
    public static final String AUDIENCE_ALL = "all";
    public static final String AUDIENCE_TA_APPLICANTS = "ta_applicants";
    public static final String AUDIENCE_MODULE_ORGANISERS = "module_organisers";

    private static final int MAX_ANNOUNCEMENT_TITLE_LENGTH = 120;
    private static final int MAX_ANNOUNCEMENT_MESSAGE_LENGTH = 1000;
    private static final int RECENT_ANNOUNCEMENT_LIMIT = 10;

    private final NotificationRepository notificationRepository = new NotificationRepository();
    private final UserRepository userRepository = new UserRepository();

    public void createNotification(String userId, String type, String title, String message, String relatedEntityType, String relatedEntityId) {
        Notification notification = new Notification();
        notification.setNotificationId(IdGenerator.generateId("notif"));
        notification.setUserId(userId);
        notification.setType(type);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setRelatedEntityType(relatedEntityType);
        notification.setRelatedEntityId(relatedEntityId);
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now().toString());

        List<Notification> notifications = notificationRepository.findAll();
        notifications.add(notification);
        notificationRepository.saveAll(notifications);
    }

    public List<Notification> getNotificationsForUser(String userId) {
        List<Notification> notifications = new ArrayList<>();
        for (Notification notification : notificationRepository.findAll()) {
            if (notification.getUserId().equals(userId)) {
                notifications.add(notification);
            }
        }
        notifications.sort(Comparator.comparing(Notification::getCreatedAt).reversed());
        return notifications;
    }

    public int countUnread(String userId) {
        int unreadCount = 0;
        for (Notification notification : notificationRepository.findAll()) {
            if (notification.getUserId().equals(userId) && !notification.isRead()) {
                unreadCount++;
            }
        }
        return unreadCount;
    }

    public void markAllAsRead(String userId) {
        List<Notification> notifications = notificationRepository.findAll();
        boolean changed = false;
        for (Notification notification : notifications) {
            if (notification.getUserId().equals(userId) && !notification.isRead()) {
                notification.setRead(true);
                changed = true;
            }
        }
        if (changed) {
            notificationRepository.saveAll(notifications);
        }
    }

    /**
     * Sends one administrator announcement to every active recipient in the
     * selected audience.
     *
     * <p>The application stores notifications as per-user records. Therefore a
     * broadcast is saved as multiple Notification rows that share the same
     * relatedEntityId. That shared id lets the admin history screen display one
     * "announcement" even though each recipient still gets an independent read
     * state in their own inbox.</p>
     */
    public OperationResult<Integer> sendSystemAnnouncement(AdminAnnouncementForm form, String adminUserId) {
        if (form == null) {
            return OperationResult.failure("Please complete the announcement form.");
        }

        String title = safeTrim(form.getTitle());
        String message = safeTrim(form.getMessage());
        String audience = normalizeAudience(form.getAudience());

        if (audience == null) {
            return OperationResult.failure("Please choose a valid announcement audience.");
        }
        if (ValidationUtil.isBlank(title)) {
            return OperationResult.failure("Please enter an announcement title.");
        }
        if (ValidationUtil.isBlank(message)) {
            return OperationResult.failure("Please enter an announcement message.");
        }
        if (title.length() > MAX_ANNOUNCEMENT_TITLE_LENGTH) {
            return OperationResult.failure("Announcement title must be 120 characters or fewer.");
        }
        if (message.length() > MAX_ANNOUNCEMENT_MESSAGE_LENGTH) {
            return OperationResult.failure("Announcement message must be 1000 characters or fewer.");
        }

        List<User> recipients = findAnnouncementRecipients(userRepository.findAll(), audience);
        if (recipients.isEmpty()) {
            return OperationResult.failure("No active users match the selected audience.");
        }

        String announcementId = IdGenerator.generateId("announce");
        String now = LocalDateTime.now().toString();
        List<Notification> notifications = notificationRepository.findAll();

        for (User recipient : recipients) {
            Notification notification = new Notification();
            notification.setNotificationId(IdGenerator.generateId("notif"));
            notification.setUserId(recipient.getUserId());
            notification.setType(TYPE_SYSTEM_ANNOUNCEMENT);
            notification.setTitle(title);
            notification.setMessage(message);
            notification.setRelatedEntityType(ENTITY_SYSTEM_ANNOUNCEMENT);
            notification.setRelatedEntityId(announcementId);
            notification.setCreatedByUserId(adminUserId);
            notification.setAudience(audience);
            notification.setRead(false);
            notification.setCreatedAt(now);
            notifications.add(notification);
        }

        notificationRepository.saveAll(notifications);
        return OperationResult.success("Announcement sent to " + recipients.size() + " user(s).", recipients.size());
    }

    /**
     * Builds live recipient counts for the compose page.
     *
     * <p>Disabled accounts are excluded for the same reason login excludes
     * them: the announcement should reach people who can actually access their
     * notification inbox.</p>
     */
    public AdminAnnouncementRecipientSummary getAnnouncementRecipientSummary() {
        AdminAnnouncementRecipientSummary summary = new AdminAnnouncementRecipientSummary();
        for (User user : userRepository.findAll()) {
            if (!user.isActive()) {
                continue;
            }
            if (user.getRole() == Role.TA_APPLICANT) {
                summary.setApplicantCount(summary.getApplicantCount() + 1);
            } else if (user.getRole() == Role.MODULE_ORGANISER) {
                summary.setModuleOrganiserCount(summary.getModuleOrganiserCount() + 1);
            }
        }
        summary.setAllRecipientCount(summary.getApplicantCount() + summary.getModuleOrganiserCount());
        return summary;
    }

    /**
     * Reconstructs administrator announcement history from recipient-level
     * notification rows.
     *
     * <p>No separate announcement table exists in this project. The shared
     * relatedEntityId, audience, and createdByUserId fields added to each copied
     * notification provide enough metadata to group the rows back into one
     * administrator-facing record.</p>
     */
    public List<AdminAnnouncementRecord> getRecentSystemAnnouncements() {
        Map<String, User> usersById = buildUsersById(userRepository.findAll());
        Map<String, AdminAnnouncementRecord> recordsByAnnouncementId = new LinkedHashMap<>();

        for (Notification notification : notificationRepository.findAll()) {
            if (!TYPE_SYSTEM_ANNOUNCEMENT.equals(notification.getType())) {
                continue;
            }

            String announcementId = ValidationUtil.isBlank(notification.getRelatedEntityId())
                ? notification.getNotificationId()
                : notification.getRelatedEntityId();
            AdminAnnouncementRecord record = recordsByAnnouncementId.get(announcementId);
            if (record == null) {
                record = new AdminAnnouncementRecord();
                record.setAnnouncementId(announcementId);
                record.setTitle(notification.getTitle());
                record.setMessage(notification.getMessage());
                record.setAudienceLabel(getAudienceDisplayName(notification.getAudience()));
                record.setSentByUsername(findUsername(usersById, notification.getCreatedByUserId()));
                record.setCreatedAt(notification.getCreatedAt());
                recordsByAnnouncementId.put(announcementId, record);
            }

            record.setRecipientCount(record.getRecipientCount() + 1);
            if (notification.isRead()) {
                record.setReadCount(record.getReadCount() + 1);
            } else {
                record.setUnreadCount(record.getUnreadCount() + 1);
            }
        }

        List<AdminAnnouncementRecord> records = new ArrayList<>(recordsByAnnouncementId.values());
        records.sort(Comparator.comparing(
            AdminAnnouncementRecord::getCreatedAt,
            Comparator.nullsLast(String::compareTo)
        ).reversed());
        if (records.size() > RECENT_ANNOUNCEMENT_LIMIT) {
            return new ArrayList<>(records.subList(0, RECENT_ANNOUNCEMENT_LIMIT));
        }
        return records;
    }

    public String getAudienceDisplayName(String audience) {
        String normalizedAudience = normalizeAudience(audience);
        if (AUDIENCE_TA_APPLICANTS.equals(normalizedAudience)) {
            return "TA applicants";
        }
        if (AUDIENCE_MODULE_ORGANISERS.equals(normalizedAudience)) {
            return "Module organisers";
        }
        return "TA applicants and module organisers";
    }

    private List<User> findAnnouncementRecipients(List<User> users, String audience) {
        List<User> recipients = new ArrayList<>();
        for (User user : users) {
            if (!user.isActive() || !isRoleInAudience(user.getRole(), audience)) {
                continue;
            }
            recipients.add(user);
        }
        return recipients;
    }

    private boolean isRoleInAudience(Role role, String audience) {
        if (role == null) {
            return false;
        }
        if (AUDIENCE_ALL.equals(audience)) {
            return role == Role.TA_APPLICANT || role == Role.MODULE_ORGANISER;
        }
        if (AUDIENCE_TA_APPLICANTS.equals(audience)) {
            return role == Role.TA_APPLICANT;
        }
        if (AUDIENCE_MODULE_ORGANISERS.equals(audience)) {
            return role == Role.MODULE_ORGANISER;
        }
        return false;
    }

    private String normalizeAudience(String audience) {
        if (ValidationUtil.isBlank(audience)) {
            return AUDIENCE_ALL;
        }

        String normalizedAudience = audience.trim().toLowerCase(Locale.ROOT);
        if (AUDIENCE_ALL.equals(normalizedAudience)
            || AUDIENCE_TA_APPLICANTS.equals(normalizedAudience)
            || AUDIENCE_MODULE_ORGANISERS.equals(normalizedAudience)) {
            return normalizedAudience;
        }
        return null;
    }

    private Map<String, User> buildUsersById(List<User> users) {
        Map<String, User> usersById = new LinkedHashMap<>();
        for (User user : users) {
            usersById.put(user.getUserId(), user);
        }
        return usersById;
    }

    private String findUsername(Map<String, User> usersById, String userId) {
        User user = usersById.get(userId);
        return user == null ? "-" : user.getUsername();
    }

    private String safeTrim(String value) {
        return value == null ? "" : value.trim();
    }
}
