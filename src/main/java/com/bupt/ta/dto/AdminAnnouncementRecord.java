package com.bupt.ta.dto;

/**
 * Grouped history row for one announcement broadcast.
 *
 * <p>The storage layer keeps one Notification per recipient. This DTO groups
 * those copies back into one administrator-facing record by announcementId.</p>
 */
public class AdminAnnouncementRecord {
    private String announcementId;
    private String title;
    private String message;
    private String audienceLabel;
    private String sentByUsername;
    private String createdAt;
    private int recipientCount;
    private int readCount;
    private int unreadCount;

    public String getAnnouncementId() {
        return announcementId;
    }

    public void setAnnouncementId(String announcementId) {
        this.announcementId = announcementId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAudienceLabel() {
        return audienceLabel;
    }

    public void setAudienceLabel(String audienceLabel) {
        this.audienceLabel = audienceLabel;
    }

    public String getSentByUsername() {
        return sentByUsername;
    }

    public void setSentByUsername(String sentByUsername) {
        this.sentByUsername = sentByUsername;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public int getRecipientCount() {
        return recipientCount;
    }

    public void setRecipientCount(int recipientCount) {
        this.recipientCount = recipientCount;
    }

    public int getReadCount() {
        return readCount;
    }

    public void setReadCount(int readCount) {
        this.readCount = readCount;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }
}
