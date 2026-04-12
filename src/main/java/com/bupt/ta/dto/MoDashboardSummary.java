package com.bupt.ta.dto;

public class MoDashboardSummary {
    private int openPostCount;
    private int applicantsPendingCount;
    private int interviewsScheduledCount;

    public int getOpenPostCount() {
        return openPostCount;
    }

    public void setOpenPostCount(int openPostCount) {
        this.openPostCount = openPostCount;
    }

    public int getApplicantsPendingCount() {
        return applicantsPendingCount;
    }

    public void setApplicantsPendingCount(int applicantsPendingCount) {
        this.applicantsPendingCount = applicantsPendingCount;
    }

    public int getInterviewsScheduledCount() {
        return interviewsScheduledCount;
    }

    public void setInterviewsScheduledCount(int interviewsScheduledCount) {
        this.interviewsScheduledCount = interviewsScheduledCount;
    }
}
