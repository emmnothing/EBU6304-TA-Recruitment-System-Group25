package com.bupt.ta.servlet.applicant;

import com.bupt.ta.dto.JobFilter;
import com.bupt.ta.dto.OperationResult;
import com.bupt.ta.model.JobPost;
import com.bupt.ta.model.Role;
import com.bupt.ta.service.ApplicantProfileService;
import com.bupt.ta.service.ApplicationService;
import com.bupt.ta.service.JobService;
import com.bupt.ta.util.ValidationUtil;
import com.bupt.ta.util.AppConstants;
import com.bupt.ta.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@WebServlet({"/applicant/jobs", "/applicant/jobs/apply", "/applicant/jobs/favorite"})
public class ApplicantJobsServlet extends HttpServlet {
    private final JobService jobService = new JobService();
    private final ApplicationService applicationService = new ApplicationService();
    private final ApplicantProfileService applicantProfileService = new ApplicantProfileService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!SessionUtil.requireRole(request, response, Role.TA_APPLICANT)) {
            return;
        }

        SessionUtil.exposeFlashMessage(request);
        JobFilter jobFilter = new JobFilter();
        jobFilter.setKeyword(request.getParameter("keyword"));
        jobFilter.setModuleCode(request.getParameter("moduleCode"));
        jobFilter.setStatus(request.getParameter("status"));
        jobFilter.setLocationMode(request.getParameter("locationMode"));
        jobFilter.setMaxWeeklyHours(request.getParameter("maxWeeklyHours"));
        jobFilter.setMinRemainingVacancies(request.getParameter("minRemainingVacancies"));
        jobFilter.setSortBy(request.getParameter("sortBy"));
        jobFilter.setSortDirection(request.getParameter("sortDirection"));

        request.setAttribute("pageTitle", "Available Jobs");
        request.setAttribute("currentUsername", SessionUtil.getCurrentUsername(request));
        request.setAttribute("jobFilter", jobFilter);
        List<JobPost> jobs = jobService.getOpenJobs(jobFilter);
        List<JobPost> allOpenJobs = jobService.getOpenJobs(new JobFilter());
        Map<String, String> applyDisabledReasons = new HashMap<>();
        Map<String, Integer> remainingVacancies = new HashMap<>();
        String currentUserId = SessionUtil.getCurrentUserId(request);
        Set<String> favoriteJobIds = new HashSet<>(applicantProfileService.getFavoriteJobIds(currentUserId));
        List<JobPost> favoriteJobs = new ArrayList<>();
        for (JobPost job : allOpenJobs) {
            if (favoriteJobIds.contains(job.getJobId())) {
                favoriteJobs.add(job);
            }
            remainingVacancies.put(job.getJobId(), jobService.countRemainingVacancies(job));
            applyDisabledReasons.put(job.getJobId(), applicationService.getApplyDisabledReason(currentUserId, job));
        }
        for (var job : jobs) {
            applyDisabledReasons.put(job.getJobId(), applicationService.getApplyDisabledReason(currentUserId, job));
            remainingVacancies.put(job.getJobId(), jobService.countRemainingVacancies(job));
        }
        request.setAttribute("jobs", jobs);
        request.setAttribute("favoriteJobs", favoriteJobs);
        request.setAttribute("favoriteJobIds", favoriteJobIds);
        request.setAttribute("applyDisabledReasons", applyDisabledReasons);
        request.setAttribute("remainingVacancies", remainingVacancies);
        request.setAttribute("filterQuery", request.getQueryString() == null ? "" : request.getQueryString());
        request.getRequestDispatcher("/WEB-INF/views/applicant/ta_jobs.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!SessionUtil.requireRole(request, response, Role.TA_APPLICANT)) {
            return;
        }

        request.setCharacterEncoding("UTF-8");
        String requestPath = request.getServletPath();
        OperationResult<?> result;
        if (requestPath.endsWith("/favorite")) {
            String jobId = request.getParameter("jobId");
            if (jobService.findById(jobId) == null) {
                result = OperationResult.failure("Selected job could not be found.");
            } else {
                result = applicantProfileService.toggleFavoriteJob(SessionUtil.getCurrentUserId(request), jobId);
            }
        } else {
            result = applicationService.applyForJob(
                SessionUtil.getCurrentUserId(request),
                request.getParameter("jobId")
            );
        }
        SessionUtil.setFlashMessage(
            request,
            result.isSuccess() ? AppConstants.FLASH_SUCCESS : AppConstants.FLASH_ERROR,
            result.getMessage()
        );
        response.sendRedirect(request.getContextPath() + buildJobsRedirect(request.getParameter("returnQuery")));
    }

    private String buildJobsRedirect(String returnQuery) {
        if (ValidationUtil.isBlank(returnQuery) || returnQuery.contains("://") || returnQuery.startsWith("/")) {
            return "/applicant/jobs";
        }
        return "/applicant/jobs?" + returnQuery;
    }
}
