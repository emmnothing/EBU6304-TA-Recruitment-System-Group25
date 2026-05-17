package com.bupt.ta.dto;

/**
 * Recipient counts shown before an administrator sends an announcement.
 *
 * <p>The counts intentionally include active TA applicants and active module
 * organisers only, because those are the roles with notification inbox pages in
 * the current application.</p>
 */
public class AdminAnnouncementRecipientSummary {
    private int allRecipientCount;
    private int applicantCount;
    private int moduleOrganiserCount;

    public int getAllRecipientCount() {
        return allRecipientCount;
    }

    public void setAllRecipientCount(int allRecipientCount) {
        this.allRecipientCount = allRecipientCount;
    }

    public int getApplicantCount() {
        return applicantCount;
    }

    public void setApplicantCount(int applicantCount) {
        this.applicantCount = applicantCount;
    }

    public int getModuleOrganiserCount() {
        return moduleOrganiserCount;
    }

    public void setModuleOrganiserCount(int moduleOrganiserCount) {
        this.moduleOrganiserCount = moduleOrganiserCount;
    }
}
