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
import com.bupt.ta.repository.JobRepository;
import com.bupt.ta.repository.UserRepository;
import com.bupt.ta.util.IdGenerator;
import com.bupt.ta.util.ValidationUtil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ApplicationService {
    private final ApplicationRepository applicationRepository = new ApplicationRepository();
    private final JobRepository jobRepository = new JobRepository();
    private final UserRepository userRepository = new UserRepository();
    private final ApplicantProfileRepository applicantProfileRepository = new ApplicantProfileRepository();

    public OperationResult<JobApplication> applyForJob(String userId, String jobId) {
        if (ValidationUtil.isBlank(jobId)) {
            return OperationResult.failure("Please choose a job before applying.");
        }
        if (hasAlreadyApplied(userId, jobId)) {
            return OperationResult.failure("You have already applied for this job.");
        }

        JobPost matchedJob = null;
        for (JobPost job : jobRepository.findAll()) {
            if (job.getJobId().equals(jobId)) {
                matchedJob = job;
                break;
            }
        }
        if (matchedJob == null) {
            return OperationResult.failure("Selected job could not be found.");
        }

        JobApplication application = new JobApplication();
        application.setApplicationId(IdGenerator.generateId("app"));
        application.setJobId(jobId);
        application.setApplicantUserId(userId);
        application.setStatus(ApplicationStatus.APPLIED);
        application.setAppliedAt(LocalDateTime.now().toString());
        application.setRemarks("Submitted");

        List<JobApplication> applications = applicationRepository.findAll();
        applications.add(application);
        applicationRepository.saveAll(applications);
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
        List<JobPost> jobs = jobRepository.findAll();

        for (JobApplication application : applicationRepository.findAll()) {
            if (!application.getApplicantUserId().equals(userId)) {
                continue;
            }
            JobPost matchedJob = findJobById(jobs, application.getJobId());
            ApplicationStatusViewItem item = new ApplicationStatusViewItem();
            item.setApplicationId(application.getApplicationId());
            item.setAppliedAt(application.getAppliedAt());
            item.setStatus(application.getStatus());
            item.setRemarks(application.getRemarks());
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
        List<JobPost> jobs = jobRepository.findAll();
        List<User> users = userRepository.findAll();
        List<ApplicantProfile> profiles = applicantProfileRepository.findAll();

        for (JobApplication application : applicationRepository.findAll()) {
            if (allowedJobIds != null && !allowedJobIds.isEmpty() && !allowedJobIds.contains(application.getJobId())) {
                continue;
            }
            if (jobId != null && !jobId.isBlank() && !application.getJobId().equals(jobId)) {
                continue;
            }
            JobPost job = findJobById(jobs, application.getJobId());
            if (job == null) {
                continue;
            }
            if (applicantFilter != null && applicantFilter.getJobId() != null && !applicantFilter.getJobId().isBlank()
                && !job.getJobId().equals(applicantFilter.getJobId())) {
                continue;
            }
            if (applicantFilter != null && applicantFilter.getStatus() != null && !applicantFilter.getStatus().isBlank()
                && !application.getStatus().name().equalsIgnoreCase(applicantFilter.getStatus())) {
                continue;
            }

            User user = findUserById(users, application.getApplicantUserId());
            ApplicantProfile profile = findProfileByUserId(profiles, application.getApplicantUserId());
            if (user == null || user.getRole() != Role.TA_APPLICANT) {
                continue;
            }

            String keyword = applicantFilter == null ? "" : normalize(applicantFilter.getKeyword());
            if (!keyword.isEmpty()
                && !containsIgnoreCase(user.getUsername(), keyword)
                && !containsIgnoreCase(user.getEmail(), keyword)
                && !(profile != null && containsIgnoreCase(profile.getStudentId(), keyword))) {
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
            item.setRemarks(application.getRemarks());
            if (profile != null) {
                item.setProgramme(profile.getProgramme());
                item.setStudentId(profile.getStudentId());
                item.setSkills(profile.getSkills());
                item.setPersonalStatement(profile.getPersonalStatement());
                item.setCvRelativePath(profile.getCvRelativePath());
            }
            reviewItems.add(item);
        }
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
        if (ValidationUtil.isBlank(applicationId) || ValidationUtil.isBlank(decision)) {
            return OperationResult.failure("Application and decision are required.");
        }

        ApplicationStatus updatedStatus;
        if ("SELECTED".equalsIgnoreCase(decision)) {
            updatedStatus = ApplicationStatus.SELECTED;
        } else if ("REJECTED".equalsIgnoreCase(decision)) {
            updatedStatus = ApplicationStatus.REJECTED;
        } else if ("UNDER_REVIEW".equalsIgnoreCase(decision)) {
            updatedStatus = ApplicationStatus.UNDER_REVIEW;
        } else {
            return OperationResult.failure("Unsupported decision value.");
        }

        List<JobApplication> applications = applicationRepository.findAll();
        for (JobApplication application : applications) {
            if (application.getApplicationId().equals(applicationId)) {
                application.setStatus(updatedStatus);
                application.setReviewedByUserId(moUserId);
                application.setReviewedAt(LocalDateTime.now().toString());
                application.setRemarks(ValidationUtil.isBlank(remarks) ? updatedStatus.name() : remarks.trim());
                applicationRepository.saveAll(applications);
                return OperationResult.success("Application decision updated successfully.", application);
            }
        }
        return OperationResult.failure("Application not found.");
    }

    public List<JobApplication> findAllApplications() {
        return applicationRepository.findAll();
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

    private boolean containsIgnoreCase(String source, String keyword) {
        return source != null && source.toLowerCase(Locale.ENGLISH).contains(keyword.toLowerCase(Locale.ENGLISH));
    }
}
