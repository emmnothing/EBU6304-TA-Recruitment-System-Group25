package com.bupt.ta.service;

import com.bupt.ta.dto.AdminOverview;
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

        int openJobs = 0;
        for (JobPost job : jobs) {
            if (job.getStatus() == JobStatus.OPEN) {
                openJobs++;
            }
        }
        overview.setOpenJobs(openJobs);

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
        overview.setRecordsList(records);

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
}
