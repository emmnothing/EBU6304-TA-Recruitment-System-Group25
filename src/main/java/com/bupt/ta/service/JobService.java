package com.bupt.ta.service;

import com.bupt.ta.dto.JobFilter;
import com.bupt.ta.dto.JobForm;
import com.bupt.ta.dto.OperationResult;
import com.bupt.ta.model.JobPost;
import com.bupt.ta.model.JobStatus;
import com.bupt.ta.repository.JobRepository;
import com.bupt.ta.util.IdGenerator;
import com.bupt.ta.util.ValidationUtil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class JobService {
    private final JobRepository jobRepository = new JobRepository();

    public List<JobPost> getOpenJobs(JobFilter jobFilter) {
        List<JobPost> matchedJobs = new ArrayList<>();
        for (JobPost job : jobRepository.findAll()) {
            if (job.getStatus() != JobStatus.OPEN) {
                continue;
            }
            if (jobFilter != null) {
                String keyword = normalize(jobFilter.getKeyword());
                String moduleCode = normalize(jobFilter.getModuleCode());
                String status = normalize(jobFilter.getStatus());

                if (!keyword.isEmpty()
                    && !containsIgnoreCase(job.getJobTitle(), keyword)
                    && !containsIgnoreCase(job.getModuleName(), keyword)
                    && !containsIgnoreCase(job.getDescription(), keyword)) {
                    continue;
                }
                if (!moduleCode.isEmpty()
                    && !containsIgnoreCase(job.getModuleCode(), moduleCode)
                    && !containsIgnoreCase(job.getModuleName(), moduleCode)) {
                    continue;
                }
                if (!status.isEmpty() && !"open".equals(status)) {
                    continue;
                }
            }
            matchedJobs.add(job);
        }
        return matchedJobs;
    }

    public OperationResult<JobPost> createJob(JobForm jobForm, String moUserId) {
        if (ValidationUtil.isBlank(jobForm.getModuleCode())
            || ValidationUtil.isBlank(jobForm.getModuleName())
            || ValidationUtil.isBlank(jobForm.getJobTitle())
            || ValidationUtil.isBlank(jobForm.getApplicationDeadline())
            || ValidationUtil.isBlank(jobForm.getLocationMode())
            || ValidationUtil.isBlank(jobForm.getDescription())
            || ValidationUtil.isBlank(jobForm.getRequirements())) {
            return OperationResult.failure("Please complete all required job fields.");
        }
        if (!ValidationUtil.isPositiveInteger(jobForm.getVacancies()) || !ValidationUtil.isPositiveInteger(jobForm.getWeeklyHours())) {
            return OperationResult.failure("Vacancies and weekly hours must be positive numbers.");
        }

        JobPost jobPost = new JobPost();
        jobPost.setJobId(IdGenerator.generateId("job"));
        jobPost.setModuleCode(jobForm.getModuleCode().trim());
        jobPost.setModuleName(jobForm.getModuleName().trim());
        jobPost.setJobTitle(jobForm.getJobTitle().trim());
        jobPost.setVacancies(Integer.parseInt(jobForm.getVacancies().trim()));
        jobPost.setWeeklyHours(Integer.parseInt(jobForm.getWeeklyHours().trim()));
        jobPost.setApplicationDeadline(jobForm.getApplicationDeadline().trim());
        jobPost.setLocationMode(jobForm.getLocationMode().trim());
        jobPost.setDescription(jobForm.getDescription().trim());
        jobPost.setRequirements(jobForm.getRequirements().trim());
        jobPost.setPostedByUserId(moUserId);
        jobPost.setStatus(JobStatus.OPEN);
        jobPost.setCreatedAt(LocalDateTime.now().toString());

        List<JobPost> jobs = jobRepository.findAll();
        jobs.add(jobPost);
        jobRepository.saveAll(jobs);
        return OperationResult.success("Job post created successfully.", jobPost);
    }

    public JobPost findById(String jobId) {
        for (JobPost job : jobRepository.findAll()) {
            if (job.getJobId().equals(jobId)) {
                return job;
            }
        }
        return null;
    }

    public List<JobPost> getJobsPostedByMo(String moUserId) {
        List<JobPost> jobs = new ArrayList<>();
        for (JobPost job : jobRepository.findAll()) {
            if (job.getPostedByUserId().equals(moUserId)) {
                jobs.add(job);
            }
        }
        return jobs;
    }

    private boolean containsIgnoreCase(String source, String keyword) {
        return source != null && source.toLowerCase(Locale.ENGLISH).contains(keyword.toLowerCase(Locale.ENGLISH));
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ENGLISH);
    }
}
