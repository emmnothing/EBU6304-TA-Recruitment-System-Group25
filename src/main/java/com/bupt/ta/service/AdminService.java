package com.bupt.ta.service;

import com.bupt.ta.dto.AdminOverview;
import com.bupt.ta.dto.AdminApplicationRecord;
import com.bupt.ta.dto.AdminJobRecord;
import com.bupt.ta.dto.WorkloadRow;
import com.bupt.ta.model.ApplicationStatus;
import com.bupt.ta.model.JobApplication;
import com.bupt.ta.model.JobPost;
import com.bupt.ta.model.JobStatus;
import com.bupt.ta.model.Role;
import com.bupt.ta.model.User;
import com.bupt.ta.repository.JobRepository;
import com.bupt.ta.repository.UserRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AdminService {
    private final UserRepository userRepository = new UserRepository();
    private final JobRepository jobRepository = new JobRepository();
    private final ApplicationService applicationService = new ApplicationService();

    public AdminOverview getAdminOverview() {
        AdminOverview overview = new AdminOverview();
        List<User> users = userRepository.findAll();
        List<JobPost> jobs = jobRepository.findAll();
        List<JobApplication> applications = applicationService.findAllApplications();

        int totalApplicants = 0;
        for (User user : users) {
            if (user.getRole() == Role.TA_APPLICANT) {
                totalApplicants++;
            }
        }
        overview.setTotalApplicants(totalApplicants);
        overview.setTotalJobs(jobs.size());
        overview.setTotalApplications(applications.size());

        int openJobs = 0;
        int applicationsPendingReview = 0;
        for (JobPost job : jobs) {
            if (job.getStatus().isOpen()) {
                openJobs++;
            }
        }
        for (JobApplication application : applications) {
            if (application.getStatus() == ApplicationStatus.APPLIED || application.getStatus() == ApplicationStatus.UNDER_REVIEW) {
                applicationsPendingReview++;
            }
        }
        overview.setOpenJobs(openJobs);
        overview.setApplicationsPendingReview(applicationsPendingReview);

        List<WorkloadRow> workloadRows = buildWorkloadRows(users, applications);
        overview.setWorkloadRows(workloadRows);

        int assignedTas = 0;
        int highWorkloadAlerts = 0;
        for (WorkloadRow row : workloadRows) {
            if (row.getAssignedModules() > 0) {
                assignedTas++;
            }
            if ("High".equals(row.getWorkloadStatus())) {
                highWorkloadAlerts++;
            }
        }
        overview.setAssignedTas(assignedTas);
        overview.setHighWorkloadAlerts(highWorkloadAlerts);

        List<String> records = new ArrayList<>();
        records.add("Users file: storage/json/users.json");
        records.add("Profiles file: storage/json/applicantProfiles.json");
        records.add("Jobs file: storage/json/jobs.json");
        records.add("Applications file: storage/json/applications.json");
        records.add("Notifications file: storage/json/notifications.json");
        overview.setRecordsList(records);
        overview.setRecentJobs(buildRecentJobs(users, jobs));
        overview.setRecentApplications(buildRecentApplications(users, jobs, applications));

        return overview;
    }

    public List<WorkloadRow> buildWorkloadRows(List<User> users, List<JobApplication> applications) {
        List<WorkloadRow> workloadRows = new ArrayList<>();
        for (User user : users) {
            if (user.getRole() != Role.TA_APPLICANT) {
                continue;
            }
            int selectedCount = 0;
            for (JobApplication application : applications) {
                if (application.getApplicantUserId().equals(user.getUserId()) && application.getStatus() == ApplicationStatus.SELECTED) {
                    selectedCount++;
                }
            }

            WorkloadRow row = new WorkloadRow();
            row.setUsername(user.getUsername());
            row.setAssignedModules(selectedCount);
            row.setWeeklyHours(selectedCount * 4);
            row.setWorkloadStatus(selectedCount >= 3 ? "High" : (selectedCount >= 1 ? "Normal" : "Available"));
            workloadRows.add(row);
        }
        return workloadRows;
    }

    private List<AdminJobRecord> buildRecentJobs(List<User> users, List<JobPost> jobs) {
        List<JobPost> sortedJobs = new ArrayList<>(jobs);
        sortedJobs.sort(Comparator.comparing(JobPost::getCreatedAt, Comparator.nullsLast(String::compareTo)).reversed());

        List<AdminJobRecord> records = new ArrayList<>();
        int limit = Math.min(6, sortedJobs.size());
        for (int i = 0; i < limit; i++) {
            JobPost job = sortedJobs.get(i);
            AdminJobRecord record = new AdminJobRecord();
            record.setModuleCode(job.getModuleCode());
            record.setJobTitle(job.getJobTitle());
            record.setStatus(job.getStatus().name());
            record.setVacancies(job.getVacancies());
            record.setFilledCount(job.getFilledCount());
            record.setOwnerUsername(findUsername(users, job.getPostedByUserId()));
            records.add(record);
        }
        return records;
    }

    private List<AdminApplicationRecord> buildRecentApplications(List<User> users, List<JobPost> jobs, List<JobApplication> applications) {
        List<JobApplication> sortedApplications = new ArrayList<>(applications);
        sortedApplications.sort(Comparator.comparing(this::applicationSortTime, Comparator.nullsLast(String::compareTo)).reversed());

        List<AdminApplicationRecord> records = new ArrayList<>();
        int limit = Math.min(8, sortedApplications.size());
        for (int i = 0; i < limit; i++) {
            JobApplication application = sortedApplications.get(i);
            JobPost job = findJobById(jobs, application.getJobId());
            AdminApplicationRecord record = new AdminApplicationRecord();
            record.setApplicantUsername(findUsername(users, application.getApplicantUserId()));
            record.setJobTitle(job == null ? "-" : job.getJobTitle());
            record.setModuleCode(job == null ? "-" : job.getModuleCode());
            record.setStatus(application.getStatus().name());
            record.setUpdatedAt(applicationSortTime(application));
            records.add(record);
        }
        return records;
    }

    private String applicationSortTime(JobApplication application) {
        return application.getStatusUpdatedAt() == null ? application.getAppliedAt() : application.getStatusUpdatedAt();
    }

    private String findUsername(List<User> users, String userId) {
        for (User user : users) {
            if (user.getUserId().equals(userId)) {
                return user.getUsername();
            }
        }
        return "-";
    }

    private JobPost findJobById(List<JobPost> jobs, String jobId) {
        for (JobPost job : jobs) {
            if (job.getJobId().equals(jobId)) {
                return job;
            }
        }
        return null;
    }
}
