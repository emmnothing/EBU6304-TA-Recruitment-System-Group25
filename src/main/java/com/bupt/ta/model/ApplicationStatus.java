package com.bupt.ta.model;

public enum ApplicationStatus {
    APPLIED("Submitted", "applied"),
    UNDER_REVIEW("Under Review", "review"),
    INTERVIEW_SCHEDULED("Interview Scheduled", "interview"),
    SELECTED("Selected", "selected"),
    REJECTED("Rejected", "rejected");

    private final String displayName;
    private final String badgeClass;

    ApplicationStatus(String displayName, String badgeClass) {
        this.displayName = displayName;
        this.badgeClass = badgeClass;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getBadgeClass() {
        return badgeClass;
    }

    public boolean isPendingAction() {
        return this == APPLIED || this == UNDER_REVIEW || this == INTERVIEW_SCHEDULED;
    }
}
