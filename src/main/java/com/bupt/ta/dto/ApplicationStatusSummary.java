package com.bupt.ta.dto;

public class ApplicationStatusSummary {
    private int appliedCount;
    private int underReviewCount;
    private int interviewScheduledCount;
    private int selectedCount;
    private int rejectedCount;

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

    public int getRejectedCount() {
        return rejectedCount;
    }

    public void setRejectedCount(int rejectedCount) {
        this.rejectedCount = rejectedCount;
    }
}
