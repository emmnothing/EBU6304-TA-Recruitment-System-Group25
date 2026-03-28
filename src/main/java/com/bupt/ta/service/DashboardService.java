package com.bupt.ta.service;

import com.bupt.ta.dto.ApplicantDashboardSummary;
import com.bupt.ta.dto.ApplicationStatusSummary;
import com.bupt.ta.dto.MoDashboardSummary;
import com.bupt.ta.model.ApplicationStatus;
import com.bupt.ta.model.JobPost;
import com.bupt.ta.model.JobStatus;

public class DashboardService {
    private final ApplicationService applicationService = new ApplicationService();
    private final JobService jobService = new JobService();

    public ApplicantDashboardSummary getApplicantSummary(String userId) {
        ApplicationStatusSummary statusSummary = applicationService.countStatusSummary(userId);
        ApplicantDashboardSummary summary = new ApplicantDashboardSummary();
        summary.setAppliedCount(statusSummary.getAppliedCount());
        summary.setUnderReviewCount(statusSummary.getUnderReviewCount());
        summary.setSelectedCount(statusSummary.getSelectedCount());
        return summary;
    }

    public MoDashboardSummary getMoSummary(String userId) {
        MoDashboardSummary summary = new MoDashboardSummary();
        var moJobs = jobService.getJobsPostedByMo(userId);
        int openPostCount = 0;
        for (JobPost job : moJobs) {
            if (job.getStatus() == JobStatus.OPEN) {
                openPostCount++;
            }
        }

        int applicantsPendingCount = 0;
        for (var application : applicationService.findAllApplications()) {
            boolean belongsToMo = false;
            for (JobPost job : moJobs) {
                if (job.getJobId().equals(application.getJobId())) {
                    belongsToMo = true;
                    break;
                }
            }
            if (belongsToMo && (application.getStatus() == ApplicationStatus.APPLIED || application.getStatus() == ApplicationStatus.UNDER_REVIEW)) {
                applicantsPendingCount++;
            }
        }

        summary.setOpenPostCount(openPostCount);
        summary.setApplicantsPendingCount(applicantsPendingCount);
        return summary;
    }
}
