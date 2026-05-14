package com.bupt.ta.service;

import com.bupt.ta.dto.JobFilter;
import com.bupt.ta.dto.JobForm;
import com.bupt.ta.dto.OperationResult;
import com.bupt.ta.model.ApplicationStatus;
import com.bupt.ta.model.JobPost;
import com.bupt.ta.model.JobStatus;
import com.bupt.ta.repository.ApplicationRepository;
import com.bupt.ta.repository.JobRepository;
import com.bupt.ta.util.IdGenerator;
import com.bupt.ta.util.ValidationUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class JobService {
    private final JobRepository jobRepository = new JobRepository();
    private final ApplicationRepository applicationRepository = new ApplicationRepository();

    public List<JobPost> getOpenJobs(JobFilter jobFilter) {
        List<JobPost> matchedJobs = new ArrayList<>();
        for (JobPost job : synchronizeJobStatuses()) {
            if (!job.getStatus().isOpen()) {
                continue;
            }
            if (jobFilter != null) {
                String keyword = normalize(jobFilter.getKeyword());
                String moduleCode = normalize(jobFilter.getModuleCode());
                String locationMode = normalize(jobFilter.getLocationMode());
                String status = normalize(jobFilter.getStatus());

                if (!keyword.isEmpty()
                    && !containsIgnoreCase(job.getJobTitle(), keyword)
                    && !containsIgnoreCase(job.getModuleName(), keyword)
                    && !containsIgnoreCase(job.getModuleCode(), keyword)
                    && !containsIgnoreCase(job.getDescription(), keyword)
                    && !containsIgnoreCase(job.getRequirements(), keyword)) {
                    continue;
                }
                if (!moduleCode.isEmpty()
                    && !containsIgnoreCase(job.getModuleCode(), moduleCode)
                    && !containsIgnoreCase(job.getModuleName(), moduleCode)) {
                    continue;
                }
                if (!locationMode.isEmpty() && !containsIgnoreCase(job.getLocationMode(), locationMode)) {
                    continue;
                }
                if (!matchesMaxWeeklyHours(job, jobFilter.getMaxWeeklyHours())) {
                    continue;
                }
                if (!matchesMinimumRemainingVacancies(job, jobFilter.getMinRemainingVacancies())) {
                    continue;
                }
                if (!status.isEmpty() && !"open".equals(status)) {
                    continue;
                }
            }
            matchedJobs.add(job);
        }
        sortJobs(matchedJobs, jobFilter);
        return matchedJobs;
    }

    public OperationResult<JobPost> createJob(JobForm jobForm, String moUserId) {
        String validationMessage = validateJobForm(jobForm);
        if (validationMessage != null) {
            return OperationResult.failure(validationMessage);
        }
        LocalDate deadline = parseDeadline(jobForm.getApplicationDeadline());
        if (deadline == null) {
            return OperationResult.failure("Application deadline must be a valid date.");
        }
        if (deadline.isBefore(LocalDate.now())) {
            return OperationResult.failure("Application deadline cannot be in the past.");
        }

        JobPost jobPost = new JobPost();
        jobPost.setJobId(IdGenerator.generateId("job"));
        applyJobForm(jobPost, jobForm);
        jobPost.setPostedByUserId(moUserId);
        jobPost.setStatus(JobStatus.OPEN);
        jobPost.setCreatedAt(LocalDateTime.now().toString());
        jobPost.setStatusUpdatedAt(LocalDateTime.now().toString());
        jobPost.setFilledCount(0);

        List<JobPost> jobs = jobRepository.findAll();
        jobs.add(jobPost);
        jobRepository.saveAll(jobs);
        return OperationResult.success("Job post created successfully.", jobPost);
    }

    public OperationResult<JobPost> updateJob(String jobId, JobForm jobForm, String moUserId) {
        if (ValidationUtil.isBlank(jobId)) {
            return OperationResult.failure("Job ID is required.");
        }

        String validationMessage = validateJobForm(jobForm);
        if (validationMessage != null) {
            return OperationResult.failure(validationMessage);
        }

        LocalDate deadline = parseDeadline(jobForm.getApplicationDeadline());
        if (deadline == null) {
            return OperationResult.failure("Application deadline must be a valid date.");
        }
        if (deadline.isBefore(LocalDate.now())) {
            return OperationResult.failure("Application deadline cannot be in the past.");
        }

        List<JobPost> jobs = synchronizeJobStatuses();
        for (JobPost job : jobs) {
            if (!job.getJobId().equals(jobId)) {
                continue;
            }
            if (!job.getPostedByUserId().equals(moUserId)) {
                return OperationResult.failure("You can only edit your own job posts.");
            }
            if (!job.getStatus().isOpen()) {
                return OperationResult.failure("Only open job posts can be edited.");
            }

            int selectedCount = countSelectedApplicants(jobId);
            int requestedVacancies = Integer.parseInt(jobForm.getVacancies().trim());
            if (requestedVacancies < selectedCount) {
                return OperationResult.failure("Vacancies cannot be less than the number of selected applicants.");
            }

            applyJobForm(job, jobForm);
            job.setFilledCount(selectedCount);

            if (selectedCount >= job.getVacancies()) {
                updateJobStatus(job, JobStatus.CLOSED_FILLED, "Closed automatically after all vacancies were filled.");
                jobRepository.saveAll(jobs);
                return OperationResult.success(
                    "Job post updated successfully. The job is now closed because all vacancies are filled.",
                    job
                );
            }

            job.setStatus(JobStatus.OPEN);
            job.setClosedAt(null);
            job.setClosedReason(null);
            job.setStatusUpdatedAt(LocalDateTime.now().toString());
            jobRepository.saveAll(jobs);
            return OperationResult.success("Job post updated successfully.", job);
        }
        return OperationResult.failure("Job post not found.");
    }

    public JobPost findById(String jobId) {
        for (JobPost job : synchronizeJobStatuses()) {
            if (job.getJobId().equals(jobId)) {
                return job;
            }
        }
        return null;
    }

    public List<JobPost> getJobsPostedByMo(String moUserId) {
        List<JobPost> jobs = new ArrayList<>();
        for (JobPost job : synchronizeJobStatuses()) {
            if (job.getPostedByUserId().equals(moUserId)) {
                jobs.add(job);
            }
        }
        return jobs;
    }

    public OperationResult<JobPost> closeJobManually(String jobId, String moUserId) {
        if (ValidationUtil.isBlank(jobId)) {
            return OperationResult.failure("Job ID is required.");
        }
        List<JobPost> jobs = synchronizeJobStatuses();
        for (JobPost job : jobs) {
            if (!job.getJobId().equals(jobId)) {
                continue;
            }
            if (!job.getPostedByUserId().equals(moUserId)) {
                return OperationResult.failure("You can only close your own job posts.");
            }
            if (!job.getStatus().isOpen()) {
                return OperationResult.failure("This job is already closed.");
            }
            updateJobStatus(job, JobStatus.CLOSED_MANUAL, "Closed manually by the module organiser.");
            jobRepository.saveAll(jobs);
            return OperationResult.success("Job post closed successfully.", job);
        }
        return OperationResult.failure("Job post not found.");
    }

    public int countSelectedApplicants(String jobId) {
        int selectedCount = 0;
        for (var application : applicationRepository.findByJobId(jobId)) {
            if (application.getStatus() == ApplicationStatus.SELECTED) {
                selectedCount++;
            }
        }
        return selectedCount;
    }

    public int countApplications(String jobId) {
        return applicationRepository.findByJobId(jobId).size();
    }

    public OperationResult<Void> deleteJob(String jobId, String moUserId) {
        if (ValidationUtil.isBlank(jobId)) {
            return OperationResult.failure("Job ID is required.");
        }

        List<JobPost> jobs = jobRepository.findAll();
        for (int index = 0; index < jobs.size(); index++) {
            JobPost job = jobs.get(index);
            if (!job.getJobId().equals(jobId)) {
                continue;
            }
            if (!job.getPostedByUserId().equals(moUserId)) {
                return OperationResult.failure("You can only delete your own job posts.");
            }
            if (countApplications(jobId) > 0) {
                return OperationResult.failure("Only job posts without any applications can be deleted.");
            }
            jobs.remove(index);
            jobRepository.saveAll(jobs);
            return OperationResult.success("Job post deleted successfully.", null);
        }
        return OperationResult.failure("Job post not found.");
    }

    public int countRemainingVacancies(JobPost job) {
        return Math.max(0, job.getVacancies() - countSelectedApplicants(job.getJobId()));
    }

    public String getJobApplicationBlockReason(JobPost job) {
        if (job == null) {
            return "Selected job could not be found.";
        }
        if (job.getStatus().isOpen()) {
            return null;
        }
        return switch (job.getStatus()) {
            case CLOSED_DEADLINE -> "Applications are closed because the deadline has passed.";
            case CLOSED_FILLED -> "Applications are closed because all vacancies have been filled.";
            case CLOSED_MANUAL -> "Applications are closed because the job was manually closed.";
            case CLOSED -> "Applications are closed for this job.";
            default -> "Applications are closed for this job.";
        };
    }

    public List<JobPost> synchronizeJobStatuses() {
        List<JobPost> jobs = jobRepository.findAll();
        boolean changed = false;
        for (JobPost job : jobs) {
            changed |= refreshLifecycle(job);
        }
        if (changed) {
            jobRepository.saveAll(jobs);
        }
        return jobs;
    }

    private boolean refreshLifecycle(JobPost job) {
        job.setFilledCount(countSelectedApplicants(job.getJobId()));
        if (job.getStatus() == null) {
            job.setStatus(JobStatus.OPEN);
            job.setStatusUpdatedAt(LocalDateTime.now().toString());
            return true;
        }
        if (!job.getStatus().isOpen()) {
            return false;
        }
        LocalDate deadline = parseDeadline(job.getApplicationDeadline());
        if (deadline != null && LocalDate.now().isAfter(deadline)) {
            updateJobStatus(job, JobStatus.CLOSED_DEADLINE, "Closed automatically after the application deadline.");
            return true;
        }
        if (job.getFilledCount() >= job.getVacancies()) {
            updateJobStatus(job, JobStatus.CLOSED_FILLED, "Closed automatically after all vacancies were filled.");
            return true;
        }
        return false;
    }

    private void updateJobStatus(JobPost job, JobStatus status, String reason) {
        job.setStatus(status);
        job.setStatusUpdatedAt(LocalDateTime.now().toString());
        if (status.isClosed()) {
            job.setClosedAt(LocalDateTime.now().toString());
            job.setClosedReason(reason);
        }
    }

    private String validateJobForm(JobForm jobForm) {
        if (ValidationUtil.isBlank(jobForm.getModuleCode())
            || ValidationUtil.isBlank(jobForm.getModuleName())
            || ValidationUtil.isBlank(jobForm.getJobTitle())
            || ValidationUtil.isBlank(jobForm.getApplicationDeadline())
            || ValidationUtil.isBlank(jobForm.getLocationMode())
            || ValidationUtil.isBlank(jobForm.getDescription())
            || ValidationUtil.isBlank(jobForm.getRequirements())) {
            return "Please complete all required job fields.";
        }
        if (!ValidationUtil.isPositiveInteger(jobForm.getVacancies()) || !ValidationUtil.isPositiveInteger(jobForm.getWeeklyHours())) {
            return "Vacancies and weekly hours must be positive numbers.";
        }
        return null;
    }

    private void applyJobForm(JobPost jobPost, JobForm jobForm) {
        jobPost.setModuleCode(jobForm.getModuleCode().trim());
        jobPost.setModuleName(jobForm.getModuleName().trim());
        jobPost.setJobTitle(jobForm.getJobTitle().trim());
        jobPost.setVacancies(Integer.parseInt(jobForm.getVacancies().trim()));
        jobPost.setWeeklyHours(Integer.parseInt(jobForm.getWeeklyHours().trim()));
        jobPost.setApplicationDeadline(jobForm.getApplicationDeadline().trim());
        jobPost.setLocationMode(jobForm.getLocationMode().trim());
        jobPost.setDescription(jobForm.getDescription().trim());
        jobPost.setRequirements(jobForm.getRequirements().trim());
    }

    private LocalDate parseDeadline(String value) {
        try {
            return ValidationUtil.isBlank(value) ? null : LocalDate.parse(value.trim());
        } catch (Exception exception) {
            return null;
        }
    }

    private boolean containsIgnoreCase(String source, String keyword) {
        return source != null && source.toLowerCase(Locale.ENGLISH).contains(keyword.toLowerCase(Locale.ENGLISH));
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ENGLISH);
    }

    private boolean matchesMaxWeeklyHours(JobPost job, String maxWeeklyHours) {
        if (ValidationUtil.isBlank(maxWeeklyHours)) {
            return true;
        }
        try {
            return job.getWeeklyHours() <= Integer.parseInt(maxWeeklyHours.trim());
        } catch (NumberFormatException exception) {
            return true;
        }
    }

    private boolean matchesMinimumRemainingVacancies(JobPost job, String minRemainingVacancies) {
        if (ValidationUtil.isBlank(minRemainingVacancies)) {
            return true;
        }
        try {
            return countRemainingVacancies(job) >= Integer.parseInt(minRemainingVacancies.trim());
        } catch (NumberFormatException exception) {
            return true;
        }
    }

    private void sortJobs(List<JobPost> jobs, JobFilter jobFilter) {
        String sortBy = jobFilter == null || ValidationUtil.isBlank(jobFilter.getSortBy()) ? "deadline" : normalize(jobFilter.getSortBy());
        Comparator<JobPost> comparator = switch (sortBy) {
            case "newest" -> Comparator.comparing(job -> safeSortValue(job.getCreatedAt()));
            case "hours" -> Comparator.comparingInt(JobPost::getWeeklyHours);
            case "remaining" -> Comparator.comparingInt(this::countRemainingVacancies);
            case "module" -> Comparator.comparing(job -> safeSortValue(job.getModuleCode()), String.CASE_INSENSITIVE_ORDER);
            default -> Comparator.comparing(job -> safeSortValue(job.getApplicationDeadline()));
        };

        String direction = jobFilter == null || ValidationUtil.isBlank(jobFilter.getSortDirection()) ? "asc" : normalize(jobFilter.getSortDirection());
        if ("desc".equals(direction) || "newest".equals(sortBy)) {
            comparator = comparator.reversed();
        }
        jobs.sort(comparator.thenComparing(job -> safeSortValue(job.getJobTitle()), String.CASE_INSENSITIVE_ORDER));
    }

    private String safeSortValue(String value) {
        return value == null ? "" : value;
    }
}
