package com.bupt.ta.service;

import com.bupt.ta.dto.OperationResult;
import com.bupt.ta.model.ApplicantProfile;
import com.bupt.ta.model.JobApplication;
import com.bupt.ta.model.JobPost;
import com.bupt.ta.model.Notification;
import com.bupt.ta.model.Role;
import com.bupt.ta.model.User;
import com.bupt.ta.repository.ApplicantProfileRepository;
import com.bupt.ta.repository.ApplicationRepository;
import com.bupt.ta.repository.JobRepository;
import com.bupt.ta.repository.NotificationRepository;
import com.bupt.ta.repository.UserRepository;
import com.bupt.ta.util.StoragePathUtil;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AccountService {
    private final UserRepository userRepository = new UserRepository();
    private final ApplicantProfileRepository applicantProfileRepository = new ApplicantProfileRepository();
    private final ApplicationRepository applicationRepository = new ApplicationRepository();
    private final JobRepository jobRepository = new JobRepository();
    private final NotificationRepository notificationRepository = new NotificationRepository();

    public OperationResult<Void> cancelAccount(String userId) {
        User user = userRepository.findById(userId);
        if (user == null) {
            return OperationResult.failure("The current account could not be found.");
        }

        Set<String> removedApplicationIds = new HashSet<>();
        switch (user.getRole()) {
            case TA_APPLICANT -> removeApplicantData(userId, removedApplicationIds);
            case MODULE_ORGANISER -> removeModuleOrganiserData(userId, removedApplicationIds);
            case ADMINISTRATOR -> {
                // Administrator accounts only need user and notification cleanup.
            }
        }

        removeNotifications(userId, removedApplicationIds);
        removeUser(userId);
        return OperationResult.success("Your account has been deleted successfully.", null);
    }

    private void removeApplicantData(String userId, Set<String> removedApplicationIds) {
        List<ApplicantProfile> profiles = applicantProfileRepository.findAll();
        ApplicantProfile removedProfile = null;
        for (ApplicantProfile profile : profiles) {
            if (userId.equals(profile.getUserId())) {
                removedProfile = profile;
                break;
            }
        }
        if (removedProfile != null) {
            profiles.remove(removedProfile);
            applicantProfileRepository.saveAll(profiles);
            deleteCvFileQuietly(removedProfile.getCvRelativePath());
        }

        List<JobApplication> applications = applicationRepository.findAll();
        boolean applicationsChanged = false;
        for (int index = applications.size() - 1; index >= 0; index--) {
            JobApplication application = applications.get(index);
            if (userId.equals(application.getApplicantUserId())) {
                removedApplicationIds.add(application.getApplicationId());
                applications.remove(index);
                applicationsChanged = true;
            }
        }
        if (applicationsChanged) {
            applicationRepository.saveAll(applications);
        }
    }

    private void removeModuleOrganiserData(String userId, Set<String> removedApplicationIds) {
        List<JobPost> jobs = jobRepository.findAll();
        Set<String> removedJobIds = new HashSet<>();
        boolean jobsChanged = false;
        for (int index = jobs.size() - 1; index >= 0; index--) {
            JobPost job = jobs.get(index);
            if (userId.equals(job.getPostedByUserId())) {
                removedJobIds.add(job.getJobId());
                jobs.remove(index);
                jobsChanged = true;
            }
        }
        if (jobsChanged) {
            jobRepository.saveAll(jobs);
        }

        if (removedJobIds.isEmpty()) {
            return;
        }

        List<JobApplication> applications = applicationRepository.findAll();
        boolean applicationsChanged = false;
        for (int index = applications.size() - 1; index >= 0; index--) {
            JobApplication application = applications.get(index);
            if (removedJobIds.contains(application.getJobId())) {
                removedApplicationIds.add(application.getApplicationId());
                applications.remove(index);
                applicationsChanged = true;
            }
        }
        if (applicationsChanged) {
            applicationRepository.saveAll(applications);
        }
    }

    private void removeNotifications(String userId, Set<String> removedApplicationIds) {
        List<Notification> notifications = notificationRepository.findAll();
        boolean changed = false;
        for (int index = notifications.size() - 1; index >= 0; index--) {
            Notification notification = notifications.get(index);
            boolean removeForCurrentUser = userId.equals(notification.getUserId());
            boolean removeForDeletedApplication = "application".equalsIgnoreCase(notification.getRelatedEntityType())
                && removedApplicationIds.contains(notification.getRelatedEntityId());
            if (removeForCurrentUser || removeForDeletedApplication) {
                notifications.remove(index);
                changed = true;
            }
        }
        if (changed) {
            notificationRepository.saveAll(notifications);
        }
    }

    private void removeUser(String userId) {
        List<User> users = userRepository.findAll();
        for (int index = users.size() - 1; index >= 0; index--) {
            if (userId.equals(users.get(index).getUserId())) {
                users.remove(index);
                userRepository.saveAll(users);
                return;
            }
        }
    }

    private void deleteCvFileQuietly(String cvRelativePath) {
        if (cvRelativePath == null || cvRelativePath.isBlank()) {
            return;
        }
        try {
            Path resolvedPath = StoragePathUtil.getStorageRoot().resolve(cvRelativePath).normalize();
            if (resolvedPath.startsWith(StoragePathUtil.getStorageRoot().normalize())) {
                Files.deleteIfExists(resolvedPath);
            }
        } catch (Exception ignored) {
        }
    }
}
