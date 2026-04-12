package com.bupt.ta.dto;

public class ApplicantDashboardSummary {
    private int appliedCount;
    private int underReviewCount;
    private int interviewScheduledCount;
    private int selectedCount;

    public int getAppliedCount() {
        return appliedCount;
    }

    public void setAppliedCount(int appliedCount) {
        this.appliedCount = appliedCount;
    }

    public int getUnderReviewCount() {
        return underReviewCount;
    }

    public void setUnderReviewCount(int underReviewCount) {
        this.underReviewCount = underReviewCount;
    }

    public int getSelectedCount() {
        return selectedCount;
    }

    public void setSelectedCount(int selectedCount) {
        this.selectedCount = selectedCount;
    }

    public int getInterviewScheduledCount() {
        return interviewScheduledCount;
    }

    public void setInterviewScheduledCount(int interviewScheduledCount) {
        this.interviewScheduledCount = interviewScheduledCount;
    }
}
