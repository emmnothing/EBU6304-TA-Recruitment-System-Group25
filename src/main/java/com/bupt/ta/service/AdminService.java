package com.bupt.ta.service;

import com.bupt.ta.dto.AdminOverview;
import com.bupt.ta.dto.AdminApplicationRecord;
import com.bupt.ta.dto.AdminJobRecord;
import com.bupt.ta.dto.AdminUserFilter;
import com.bupt.ta.dto.AdminUserManagementView;
import com.bupt.ta.dto.AdminUserRecord;
import com.bupt.ta.dto.OperationResult;
import com.bupt.ta.dto.WorkloadRow;
import com.bupt.ta.model.ApplicantProfile;
import com.bupt.ta.model.ApplicationStatus;
import com.bupt.ta.model.JobApplication;
import com.bupt.ta.model.JobPost;
import com.bupt.ta.model.JobStatus;
import com.bupt.ta.model.Role;
import com.bupt.ta.model.User;
import com.bupt.ta.repository.ApplicantProfileRepository;
import com.bupt.ta.repository.ApplicationRepository;
import com.bupt.ta.repository.JobRepository;
import com.bupt.ta.repository.UserRepository;
import com.bupt.ta.util.PasswordUtil;
import com.bupt.ta.util.ValidationUtil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class AdminService {
    private static final String DEFAULT_RESET_PASSWORD = "Password123";

    private final UserRepository userRepository = new UserRepository();
    private final JobRepository jobRepository = new JobRepository();
    private final ApplicationRepository applicationRepository = new ApplicationRepository();
    private final ApplicantProfileRepository applicantProfileRepository = new ApplicantProfileRepository();
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
            if (application.getStatus().isPendingAction()) {
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

    public AdminUserManagementView getUserManagementView(AdminUserFilter filter, String currentAdminId) {
        List<User> users = userRepository.findAll();
        List<JobPost> jobs = jobRepository.findAll();
        List<JobApplication> applications = applicationRepository.findAll();

        AdminUserManagementView view = new AdminUserManagementView();
        view.setTotalUsers(users.size());

        int activeUsers = 0;
        int inactiveUsers = 0;
        int applicantCount = 0;
        int moduleOrganiserCount = 0;
        int administratorCount = 0;
        for (User user : users) {
            if (user.isActive()) {
                activeUsers++;
            } else {
                inactiveUsers++;
            }
            if (user.getRole() == Role.TA_APPLICANT) {
                applicantCount++;
            } else if (user.getRole() == Role.MODULE_ORGANISER) {
                moduleOrganiserCount++;
            } else if (user.getRole() == Role.ADMINISTRATOR) {
                administratorCount++;
            }
        }

        view.setActiveUsers(activeUsers);
        view.setInactiveUsers(inactiveUsers);
        view.setApplicantCount(applicantCount);
        view.setModuleOrganiserCount(moduleOrganiserCount);
        view.setAdministratorCount(administratorCount);

        List<AdminUserRecord> userRecords = new ArrayList<>();
        for (User user : users) {
            if (!matchesUserFilter(user, filter)) {
                continue;
            }
            AdminUserRecord record = new AdminUserRecord();
            record.setUserId(user.getUserId());
            record.setUsername(user.getUsername());
            record.setEmail(user.getEmail());
            record.setPhoneNumber(user.getPhoneNumber());
            record.setRole(user.getRole());
            record.setActive(user.isActive());
            record.setCreatedAt(user.getCreatedAt());
            record.setStatusUpdatedAt(user.getStatusUpdatedAt());
            record.setCurrentUser(user.getUserId().equals(currentAdminId));
            record.setActivitySummary(buildActivitySummary(user, jobs, applications));
            userRecords.add(record);
        }

        userRecords.sort(buildUserComparator(filter));
        view.setUserRecords(userRecords);
        return view;
    }

    public OperationResult<User> toggleUserStatus(String targetUserId, String adminUserId) {
        if (ValidationUtil.isBlank(targetUserId)) {
            return OperationResult.failure("Please choose a user account to update.");
        }

        List<User> users = userRepository.findAll();
        User targetUser = findUserById(users, targetUserId);
        if (targetUser == null) {
            return OperationResult.failure("The selected user account could not be found.");
        }
        if (targetUserId.equals(adminUserId)) {
            return OperationResult.failure("You cannot disable or enable your own administrator account here.");
        }

        boolean shouldActivate = !targetUser.isActive();
        if (!shouldActivate && targetUser.getRole() == Role.ADMINISTRATOR && countActiveAdministrators(users) <= 1) {
            return OperationResult.failure("At least one active administrator account must remain available.");
        }

        targetUser.setActive(shouldActivate);
        targetUser.setStatusUpdatedAt(LocalDateTime.now().toString());
        userRepository.saveAll(users);

        String message = shouldActivate
            ? "The user account has been enabled."
            : "The user account has been disabled.";
        return OperationResult.success(message, targetUser);
    }

    public OperationResult<String> resetUserPassword(String targetUserId) {
        if (ValidationUtil.isBlank(targetUserId)) {
            return OperationResult.failure("Please choose a user account to reset.");
        }

        List<User> users = userRepository.findAll();
        User targetUser = findUserById(users, targetUserId);
        if (targetUser == null) {
            return OperationResult.failure("The selected user account could not be found.");
        }

        targetUser.setPasswordHash(PasswordUtil.hashPassword(DEFAULT_RESET_PASSWORD));
        userRepository.saveAll(users);
        return OperationResult.success(
            "Password reset successfully. Temporary password: " + DEFAULT_RESET_PASSWORD,
            DEFAULT_RESET_PASSWORD
        );
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public List<JobPost> findAllJobs() {
        return jobRepository.findAll();
    }

    public List<JobApplication> findAllApplications() {
        return applicationRepository.findAll();
    }

    public List<ApplicantProfile> findAllApplicantProfiles() {
        return applicantProfileRepository.findAll();
    }

    public String getDefaultResetPassword() {
        return DEFAULT_RESET_PASSWORD;
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

    private boolean matchesUserFilter(User user, AdminUserFilter filter) {
        if (filter == null) {
            return true;
        }
        if (!ValidationUtil.isBlank(filter.getRole())) {
            try {
                Role role = Role.valueOf(filter.getRole().trim().toUpperCase(Locale.ROOT));
                if (user.getRole() != role) {
                    return false;
                }
            } catch (IllegalArgumentException exception) {
                return false;
            }
        }
        if (!ValidationUtil.isBlank(filter.getStatus())) {
            String status = filter.getStatus().trim().toLowerCase(Locale.ROOT);
            if ("active".equals(status) && !user.isActive()) {
                return false;
            }
            if ("inactive".equals(status) && user.isActive()) {
                return false;
            }
        }
        if (!ValidationUtil.isBlank(filter.getKeyword())) {
            String keyword = filter.getKeyword().trim().toLowerCase(Locale.ROOT);
            return containsIgnoreCase(user.getUserId(), keyword)
                || containsIgnoreCase(user.getUsername(), keyword)
                || containsIgnoreCase(user.getEmail(), keyword)
                || containsIgnoreCase(user.getPhoneNumber(), keyword)
                || containsIgnoreCase(user.getRole() == null ? null : user.getRole().getDisplayName(), keyword);
        }
        return true;
    }

    private Comparator<AdminUserRecord> buildUserComparator(AdminUserFilter filter) {
        String sortBy = filter == null || ValidationUtil.isBlank(filter.getSortBy()) ? "createdAt" : filter.getSortBy().trim();
        Comparator<AdminUserRecord> comparator;
        if ("username".equalsIgnoreCase(sortBy)) {
            comparator = Comparator.comparing(AdminUserRecord::getUsername, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
        } else if ("role".equalsIgnoreCase(sortBy)) {
            comparator = Comparator.comparing(
                record -> record.getRole() == null ? "" : record.getRole().getDisplayName(),
                Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)
            );
        } else if ("status".equalsIgnoreCase(sortBy)) {
            comparator = Comparator.comparing(AdminUserRecord::isActive);
        } else if ("statusUpdatedAt".equalsIgnoreCase(sortBy)) {
            comparator = Comparator.comparing(AdminUserRecord::getStatusUpdatedAt, Comparator.nullsLast(String::compareTo));
        } else {
            comparator = Comparator.comparing(AdminUserRecord::getCreatedAt, Comparator.nullsLast(String::compareTo));
        }

        String direction = filter == null || ValidationUtil.isBlank(filter.getSortDirection()) ? "desc" : filter.getSortDirection().trim();
        if (!"asc".equalsIgnoreCase(direction)) {
            comparator = comparator.reversed();
        }
        return comparator;
    }

    private String buildActivitySummary(User user, List<JobPost> jobs, List<JobApplication> applications) {
        if (user.getRole() == Role.TA_APPLICANT) {
            int applicationCount = 0;
            int selectedCount = 0;
            for (JobApplication application : applications) {
                if (!user.getUserId().equals(application.getApplicantUserId())) {
                    continue;
                }
                applicationCount++;
                if (application.getStatus() == ApplicationStatus.SELECTED) {
                    selectedCount++;
                }
            }
            return applicationCount + " applications / " + selectedCount + " selected";
        }
        if (user.getRole() == Role.MODULE_ORGANISER) {
            int jobCount = 0;
            int openJobCount = 0;
            for (JobPost job : jobs) {
                if (!user.getUserId().equals(job.getPostedByUserId())) {
                    continue;
                }
                jobCount++;
                if (job.getStatus() == JobStatus.OPEN) {
                    openJobCount++;
                }
            }
            return jobCount + " jobs posted / " + openJobCount + " open";
        }
        return "Administrator access";
    }

    private int countActiveAdministrators(List<User> users) {
        int count = 0;
        for (User user : users) {
            if (user.getRole() == Role.ADMINISTRATOR && user.isActive()) {
                count++;
            }
        }
        return count;
    }

    private User findUserById(List<User> users, String userId) {
        for (User user : users) {
            if (user.getUserId().equals(userId)) {
                return user;
            }
        }
        return null;
    }

    private boolean containsIgnoreCase(String source, String keyword) {
        return source != null && source.toLowerCase(Locale.ROOT).contains(keyword);
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
