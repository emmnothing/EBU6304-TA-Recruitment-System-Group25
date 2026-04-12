package com.bupt.ta.service;

import com.bupt.ta.dto.ApplicantFilter;
import com.bupt.ta.dto.ApplicantReviewItem;
import com.bupt.ta.dto.ApplicationStatusSummary;
import com.bupt.ta.dto.ApplicationStatusViewItem;
import com.bupt.ta.dto.OperationResult;
import com.bupt.ta.model.ApplicantProfile;
import com.bupt.ta.model.ApplicationStatus;
import com.bupt.ta.model.JobApplication;
import com.bupt.ta.model.JobPost;
import com.bupt.ta.model.Role;
import com.bupt.ta.model.User;
import com.bupt.ta.repository.ApplicantProfileRepository;
import com.bupt.ta.repository.ApplicationRepository;
import com.bupt.ta.repository.UserRepository;
import com.bupt.ta.util.IdGenerator;
import com.bupt.ta.util.ValidationUtil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class ApplicationService {
    private final ApplicationRepository applicationRepository = new ApplicationRepository();
    private final UserRepository userRepository = new UserRepository();
    private final ApplicantProfileRepository applicantProfileRepository = new ApplicantProfileRepository();
    private final JobService jobService = new JobService();
    private final ApplicantProfileService applicantProfileService = new ApplicantProfileService();
    private final NotificationService notificationService = new NotificationService();

    public OperationResult<JobApplication> applyForJob(String userId, String jobId) {
        if (ValidationUtil.isBlank(jobId)) {
            return OperationResult.failure("Please choose a job before applying.");
        }
        if (hasAlreadyApplied(userId, jobId)) {
            return OperationResult.failure("You have already applied for this job.");
        }

        JobPost matchedJob = jobService.findById(jobId);
        if (matchedJob == null) {
            return OperationResult.failure("Selected job could not be found.");
        }
        String profileIncompleteReason = applicantProfileService.getProfileIncompleteReason(userId);
        if (profileIncompleteReason != null) {
            return OperationResult.failure(profileIncompleteReason);
        }
        String jobBlockReason = jobService.getJobApplicationBlockReason(matchedJob);
        if (jobBlockReason != null) {
            return OperationResult.failure(jobBlockReason);
        }

        JobApplication application = new JobApplication();
        String now = LocalDateTime.now().toString();
        application.setApplicationId(IdGenerator.generateId("app"));
        application.setJobId(jobId);
        application.setApplicantUserId(userId);
        application.setStatus(ApplicationStatus.APPLIED);
        application.setAppliedAt(now);
        application.setStatusUpdatedAt(now);
        application.setRemarks("Submitted");

        List<JobApplication> applications = applicationRepository.findAll();
        applications.add(application);
        applicationRepository.saveAll(applications);
        User applicantUser = findUserById(userRepository.findAll(), userId);
        notificationService.createNotification(
            userId,
            "APPLICATION_SUBMITTED",
            "Application submitted",
            "Your application for " + matchedJob.getJobTitle() + " has been submitted successfully.",
            "application",
            application.getApplicationId()
        );
        if (matchedJob.getPostedByUserId() != null && !matchedJob.getPostedByUserId().isBlank()) {
            String applicantName = applicantUser == null ? "A TA applicant" : applicantUser.getUsername();
            notificationService.createNotification(
                matchedJob.getPostedByUserId(),
                "NEW_APPLICATION_RECEIVED",
                "New application received",
                applicantName + " applied for " + matchedJob.getJobTitle() + ".",
                "application",
                application.getApplicationId()
            );
        }
        return OperationResult.success("Application submitted successfully.", application);
    }

    public boolean hasAlreadyApplied(String userId, String jobId) {
        for (JobApplication application : applicationRepository.findAll()) {
            if (application.getApplicantUserId().equals(userId) && application.getJobId().equals(jobId)) {
                return true;
            }
        }
        return false;
    }

    public List<ApplicationStatusViewItem> getApplicantStatusList(String userId) {
        List<ApplicationStatusViewItem> items = new ArrayList<>();
        List<JobPost> jobs = jobService.synchronizeJobStatuses();

        for (JobApplication application : applicationRepository.findAll()) {
            if (!application.getApplicantUserId().equals(userId)) {
                continue;
            }
            JobPost matchedJob = findJobById(jobs, application.getJobId());
            ApplicationStatusViewItem item = new ApplicationStatusViewItem();
            item.setApplicationId(application.getApplicationId());
            item.setAppliedAt(application.getAppliedAt());
            item.setStatus(application.getStatus());
            item.setStatusUpdatedAt(application.getStatusUpdatedAt());
            item.setRemarks(application.getRemarks());
            item.setInterviewScheduledAt(application.getInterviewScheduledAt());
            item.setInterviewMode(application.getInterviewMode());
            item.setInterviewLocation(application.getInterviewLocation());
            item.setInterviewNotes(application.getInterviewNotes());
            if (matchedJob != null) {
                item.setJobTitle(matchedJob.getJobTitle());
                item.setModuleCode(matchedJob.getModuleCode());
                item.setModuleName(matchedJob.getModuleName());
            }
            items.add(item);
        }
        return items;
    }

    public ApplicationStatusSummary countStatusSummary(String userId) {
        ApplicationStatusSummary summary = new ApplicationStatusSummary();
        for (JobApplication application : applicationRepository.findAll()) {
            if (!application.getApplicantUserId().equals(userId)) {
                continue;
            }
            switch (application.getStatus()) {
                case APPLIED -> summary.setAppliedCount(summary.getAppliedCount() + 1);
                case UNDER_REVIEW -> summary.setUnderReviewCount(summary.getUnderReviewCount() + 1);
                case INTERVIEW_SCHEDULED -> summary.setInterviewScheduledCount(summary.getInterviewScheduledCount() + 1);
                case SELECTED -> summary.setSelectedCount(summary.getSelectedCount() + 1);
                case REJECTED -> summary.setRejectedCount(summary.getRejectedCount() + 1);
                default -> {
                }
            }
        }
        return summary;
    }

    public List<ApplicantReviewItem> getApplicantsForMo(String jobId, ApplicantFilter applicantFilter) {
        return getApplicantsForMo(null, jobId, applicantFilter);
    }

    public List<ApplicantReviewItem> getApplicantsForMo(List<String> allowedJobIds, String jobId, ApplicantFilter applicantFilter) {
        List<ApplicantReviewItem> reviewItems = new ArrayList<>();
        List<JobPost> jobs = jobService.synchronizeJobStatuses();
        List<User> users = userRepository.findAll();
        List<ApplicantProfile> profiles = applicantProfileRepository.findAll();
        String keyword = applicantFilter == null ? "" : normalize(applicantFilter.getKeyword());
        String requestedStatus = applicantFilter == null ? "" : normalize(applicantFilter.getStatus());
        String requestedHasCv = applicantFilter == null ? "" : normalize(applicantFilter.getHasCv());
        String requestedJobId = applicantFilter == null ? "" : safeTrim(applicantFilter.getJobId());

        for (JobApplication application : applicationRepository.findAll()) {
            if (allowedJobIds != null && !allowedJobIds.isEmpty() && !allowedJobIds.contains(application.getJobId())) {
                continue;
            }
            if (!ValidationUtil.isBlank(jobId) && !application.getJobId().equals(jobId)) {
                continue;
            }
            JobPost job = findJobById(jobs, application.getJobId());
            if (job == null) {
                continue;
            }
            if (!requestedJobId.isEmpty() && !job.getJobId().equals(requestedJobId)) {
                continue;
            }
            if (!requestedStatus.isEmpty() && !application.getStatus().name().equalsIgnoreCase(requestedStatus)) {
                continue;
            }

            User user = findUserById(users, application.getApplicantUserId());
            ApplicantProfile profile = findProfileByUserId(profiles, application.getApplicantUserId());
            if (user == null || user.getRole() != Role.TA_APPLICANT) {
                continue;
            }
            if (!matchesHasCv(profile, requestedHasCv)) {
                continue;
            }
            if (!keyword.isEmpty() && !matchesKeyword(keyword, user, profile, job, application)) {
                continue;
            }

            ApplicantReviewItem item = new ApplicantReviewItem();
            item.setApplicationId(application.getApplicationId());
            item.setApplicantUserId(user.getUserId());
            item.setUsername(user.getUsername());
            item.setEmail(user.getEmail());
            item.setPhoneNumber(user.getPhoneNumber());
            item.setModuleCode(job.getModuleCode());
            item.setModuleName(job.getModuleName());
            item.setJobTitle(job.getJobTitle());
            item.setStatus(application.getStatus());
            item.setAppliedAt(application.getAppliedAt());
            item.setReviewedAt(application.getReviewedAt());
            item.setStatusUpdatedAt(application.getStatusUpdatedAt());
            item.setRemarks(application.getRemarks());
            item.setInterviewScheduledAt(application.getInterviewScheduledAt());
            item.setInterviewMode(application.getInterviewMode());
            item.setInterviewLocation(application.getInterviewLocation());
            item.setInterviewNotes(application.getInterviewNotes());
            if (profile != null) {
                item.setProgramme(profile.getProgramme());
                item.setStudentId(profile.getStudentId());
                item.setSkills(profile.getSkills());
                item.setPersonalStatement(profile.getPersonalStatement());
                item.setCvFileName(profile.getCvFileName());
                item.setCvRelativePath(profile.getCvRelativePath());
            }
            reviewItems.add(item);
        }

        sortApplicantReviewItems(reviewItems, applicantFilter);
        return reviewItems;
    }

    public ApplicantReviewItem getApplicationDetail(String applicationId) {
        for (ApplicantReviewItem item : getApplicantsForMo((List<String>) null, null, new ApplicantFilter())) {
            if (item.getApplicationId().equals(applicationId)) {
                return item;
            }
        }
        return null;
    }

    public ApplicantReviewItem getApplicationDetail(String applicationId, List<String> allowedJobIds) {
        for (ApplicantReviewItem item : getApplicantsForMo(allowedJobIds, null, new ApplicantFilter())) {
            if (item.getApplicationId().equals(applicationId)) {
                return item;
            }
        }
        return null;
    }

    public OperationResult<JobApplication> updateDecision(String applicationId, String decision, String remarks, String moUserId) {
        ApplicationStatus updatedStatus = parseDecisionStatus(decision);
        if (updatedStatus == null) {
            return OperationResult.failure("Unsupported decision value.");
        }
        return updateApplicationStatus(applicationId, updatedStatus, remarks, moUserId);
    }

    public OperationResult<Integer> batchUpdateDecision(String[] applicationIds, String decision, String remarks, String moUserId) {
        ApplicationStatus updatedStatus = parseDecisionStatus(decision);
        if (updatedStatus == null) {
            return OperationResult.failure("Unsupported decision value.");
        }
        Set<String> uniqueIds = new LinkedHashSet<>();
        if (applicationIds != null) {
            for (String applicationId : applicationIds) {
                if (!ValidationUtil.isBlank(applicationId)) {
                    uniqueIds.add(applicationId.trim());
                }
            }
        }
        if (uniqueIds.isEmpty()) {
            return OperationResult.failure("Please select at least one application.");
        }

        int updatedCount = 0;
        int failedCount = 0;
        String firstFailureMessage = null;
        for (String applicationId : uniqueIds) {
            OperationResult<JobApplication> result = updateApplicationStatus(applicationId, updatedStatus, remarks, moUserId);
            if (result.isSuccess()) {
                updatedCount++;
            } else {
                failedCount++;
                if (firstFailureMessage == null) {
                    firstFailureMessage = result.getMessage();
                }
            }
        }

        if (updatedCount == 0) {
            return OperationResult.failure(firstFailureMessage == null ? "No applications were updated." : firstFailureMessage);
        }

        String message = updatedCount + " application(s) updated to " + updatedStatus.getDisplayName() + ".";
        if (failedCount > 0) {
            message += " " + failedCount + " application(s) were skipped.";
        }
        return OperationResult.success(message, updatedCount);
    }

    public OperationResult<JobApplication> scheduleInterview(
        String applicationId,
        String interviewScheduledAt,
        String interviewMode,
        String interviewLocation,
        String interviewNotes,
        String moUserId
    ) {
        if (ValidationUtil.isBlank(applicationId)) {
            return OperationResult.failure("Application ID is required.");
        }
        if (ValidationUtil.isBlank(interviewScheduledAt)) {
            return OperationResult.failure("Please choose an interview date and time.");
        }

        LocalDateTime scheduledTime;
        try {
            scheduledTime = LocalDateTime.parse(interviewScheduledAt.trim());
        } catch (Exception exception) {
            return OperationResult.failure("Interview date and time must be valid.");
        }

        LocalDateTime now = LocalDateTime.now();
        if (scheduledTime.isBefore(now.minusMinutes(1))) {
            return OperationResult.failure("Interview date and time cannot be in the past.");
        }

        List<JobApplication> applications = applicationRepository.findAll();
        for (JobApplication application : applications) {
            if (!application.getApplicationId().equals(applicationId)) {
                continue;
            }

            JobPost job = jobService.findById(application.getJobId());
            if (job == null) {
                return OperationResult.failure("Related job post could not be found.");
            }
            if (!moUserId.equals(job.getPostedByUserId())) {
                return OperationResult.failure("You can only manage applications for your own job posts.");
            }
            if (application.getStatus() == ApplicationStatus.SELECTED || application.getStatus() == ApplicationStatus.REJECTED) {
                return OperationResult.failure("Interviews cannot be scheduled for applications with a final decision.");
            }

            String nowValue = now.toString();
            application.setStatus(ApplicationStatus.INTERVIEW_SCHEDULED);
            application.setReviewedByUserId(moUserId);
            application.setReviewedAt(nowValue);
            application.setStatusUpdatedAt(nowValue);
            application.setInterviewScheduledAt(interviewScheduledAt.trim());
            application.setInterviewMode(safeTrim(interviewMode));
            application.setInterviewLocation(safeTrim(interviewLocation));
            application.setInterviewNotes(safeTrim(interviewNotes));
            application.setRemarks(
                ValidationUtil.isBlank(interviewNotes)
                    ? defaultStatusRemark(ApplicationStatus.INTERVIEW_SCHEDULED)
                    : interviewNotes.trim()
            );
            applicationRepository.saveAll(applications);
            notificationService.createNotification(
                application.getApplicantUserId(),
                "INTERVIEW_SCHEDULED",
                "Interview scheduled",
                "Your interview for " + job.getJobTitle() + " has been scheduled for " + interviewScheduledAt.trim() + ".",
                "application",
                application.getApplicationId()
            );
            return OperationResult.success("Interview scheduled successfully.", application);
        }
        return OperationResult.failure("Application not found.");
    }

    public List<JobApplication> findAllApplications() {
        return applicationRepository.findAll();
    }

    public String getApplyDisabledReason(String userId, JobPost job) {
        if (job == null) {
            return "Selected job could not be found.";
        }
        if (hasAlreadyApplied(userId, job.getJobId())) {
            return "You have already applied for this job.";
        }
        String profileIncompleteReason = applicantProfileService.getProfileIncompleteReason(userId);
        if (profileIncompleteReason != null) {
            return profileIncompleteReason;
        }
        return jobService.getJobApplicationBlockReason(job);
    }

    private OperationResult<JobApplication> updateApplicationStatus(
        String applicationId,
        ApplicationStatus updatedStatus,
        String remarks,
        String moUserId
    ) {
        if (ValidationUtil.isBlank(applicationId)) {
            return OperationResult.failure("Application and decision are required.");
        }

        List<JobApplication> applications = applicationRepository.findAll();
        for (JobApplication application : applications) {
            if (!application.getApplicationId().equals(applicationId)) {
                continue;
            }

            JobPost job = jobService.findById(application.getJobId());
            if (job == null) {
                return OperationResult.failure("Related job post could not be found.");
            }
            if (!moUserId.equals(job.getPostedByUserId())) {
                return OperationResult.failure("You can only manage applications for your own job posts.");
            }
            if (updatedStatus == ApplicationStatus.SELECTED && !job.getStatus().isOpen()) {
                return OperationResult.failure(jobService.getJobApplicationBlockReason(job));
            }
            if (updatedStatus == ApplicationStatus.SELECTED) {
                int selectedApplicants = jobService.countSelectedApplicants(job.getJobId());
                boolean alreadySelected = application.getStatus() == ApplicationStatus.SELECTED;
                if (!alreadySelected && selectedApplicants >= job.getVacancies()) {
                    jobService.synchronizeJobStatuses();
                    return OperationResult.failure("This job has already reached its vacancy limit.");
                }
            }

            String now = LocalDateTime.now().toString();
            application.setStatus(updatedStatus);
            application.setReviewedByUserId(moUserId);
            application.setReviewedAt(now);
            application.setStatusUpdatedAt(now);
            application.setRemarks(ValidationUtil.isBlank(remarks) ? defaultStatusRemark(updatedStatus) : remarks.trim());
            applicationRepository.saveAll(applications);
            jobService.synchronizeJobStatuses();
            notificationService.createNotification(
                application.getApplicantUserId(),
                "APPLICATION_STATUS_UPDATED",
                "Application status updated",
                "Your application status is now " + updatedStatus.getDisplayName() + " for " + job.getJobTitle() + ".",
                "application",
                application.getApplicationId()
            );
            return OperationResult.success("Application decision updated successfully.", application);
        }
        return OperationResult.failure("Application not found.");
    }

    private ApplicationStatus parseDecisionStatus(String decision) {
        if ("SELECTED".equalsIgnoreCase(decision)) {
            return ApplicationStatus.SELECTED;
        }
        if ("REJECTED".equalsIgnoreCase(decision)) {
            return ApplicationStatus.REJECTED;
        }
        if ("UNDER_REVIEW".equalsIgnoreCase(decision)) {
            return ApplicationStatus.UNDER_REVIEW;
        }
        return null;
    }

    private void sortApplicantReviewItems(List<ApplicantReviewItem> reviewItems, ApplicantFilter applicantFilter) {
        String sortBy = normalize(applicantFilter == null ? null : applicantFilter.getSortBy());
        String sortDirection = normalize(applicantFilter == null ? null : applicantFilter.getSortDirection());

        Comparator<ApplicantReviewItem> comparator = switch (sortBy) {
            case "username" -> Comparator.comparing(item -> safeSortValue(item.getUsername()), String.CASE_INSENSITIVE_ORDER);
            case "status" -> Comparator.comparing(
                item -> item.getStatus() == null ? "" : item.getStatus().getDisplayName(),
                String.CASE_INSENSITIVE_ORDER
            );
            case "reviewedat" -> Comparator.comparing(item -> safeSortValue(item.getReviewedAt()));
            case "interviewat" -> Comparator.comparing(item -> safeSortValue(item.getInterviewScheduledAt()));
            case "studentid" -> Comparator.comparing(item -> safeSortValue(item.getStudentId()), String.CASE_INSENSITIVE_ORDER);
            default -> Comparator.comparing(item -> safeSortValue(item.getAppliedAt()));
        };

        comparator = comparator
            .thenComparing(item -> safeSortValue(item.getAppliedAt()))
            .thenComparing(item -> safeSortValue(item.getUsername()), String.CASE_INSENSITIVE_ORDER);

        if (!"asc".equals(sortDirection)) {
            comparator = comparator.reversed();
        }
        reviewItems.sort(comparator);
    }

    private boolean matchesKeyword(
        String keyword,
        User user,
        ApplicantProfile profile,
        JobPost job,
        JobApplication application
    ) {
        return containsIgnoreCase(user.getUsername(), keyword)
            || containsIgnoreCase(user.getEmail(), keyword)
            || containsIgnoreCase(user.getPhoneNumber(), keyword)
            || containsIgnoreCase(job.getModuleCode(), keyword)
            || containsIgnoreCase(job.getModuleName(), keyword)
            || containsIgnoreCase(job.getJobTitle(), keyword)
            || containsIgnoreCase(application.getRemarks(), keyword)
            || (profile != null && (
                containsIgnoreCase(profile.getStudentId(), keyword)
                    || containsIgnoreCase(profile.getProgramme(), keyword)
                    || containsIgnoreCase(profile.getSkills(), keyword)
                    || containsIgnoreCase(profile.getPersonalStatement(), keyword)
            ));
    }

    private boolean matchesHasCv(ApplicantProfile profile, String requestedHasCv) {
        if (requestedHasCv.isEmpty()) {
            return true;
        }
        boolean hasCv = profile != null && !ValidationUtil.isBlank(profile.getCvRelativePath());
        if ("yes".equals(requestedHasCv)) {
            return hasCv;
        }
        if ("no".equals(requestedHasCv)) {
            return !hasCv;
        }
        return true;
    }

    private JobPost findJobById(List<JobPost> jobs, String jobId) {
        for (JobPost job : jobs) {
            if (job.getJobId().equals(jobId)) {
                return job;
            }
        }
        return null;
    }

    private User findUserById(List<User> users, String userId) {
        for (User user : users) {
            if (user.getUserId().equals(userId)) {
                return user;
            }
        }
        return null;
    }

    private ApplicantProfile findProfileByUserId(List<ApplicantProfile> profiles, String userId) {
        for (ApplicantProfile profile : profiles) {
            if (profile.getUserId().equals(userId)) {
                return profile;
            }
        }
        return null;
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ENGLISH);
    }

    private String safeSortValue(String value) {
        return value == null ? "" : value;
    }

    private String safeTrim(String value) {
        return value == null ? "" : value.trim();
    }

    private boolean containsIgnoreCase(String source, String keyword) {
        return source != null && source.toLowerCase(Locale.ENGLISH).contains(keyword.toLowerCase(Locale.ENGLISH));
    }

    private String defaultStatusRemark(ApplicationStatus status) {
        return switch (status) {
            case APPLIED -> "Application submitted.";
            case UNDER_REVIEW -> "Application is under review.";
            case INTERVIEW_SCHEDULED -> "Interview scheduled.";
            case SELECTED -> "Applicant selected for the role.";
            case REJECTED -> "Application was rejected.";
        };
    }
}
