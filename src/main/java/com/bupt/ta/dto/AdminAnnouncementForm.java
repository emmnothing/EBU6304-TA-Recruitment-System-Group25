package com.bupt.ta.dto;

/**
 * Form object for the administrator announcement page.
 *
 * <p>The audience value is kept as a String because it comes directly from a
 * JSP select field. NotificationService owns the validation so future pages or
 * APIs cannot bypass the same audience rules by posting arbitrary values.</p>
 */
public class AdminAnnouncementForm {
    private String audience;
    private String title;
    private String message;

    public String getAudience() {
        return audience;
    }

    public void setAudience(String audience) {
        this.audience = audience;
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
}
