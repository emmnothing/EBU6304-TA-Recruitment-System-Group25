package com.bupt.ta.servlet.applicant;

import com.bupt.ta.dto.JobFilter;
import com.bupt.ta.dto.OperationResult;
import com.bupt.ta.model.Role;
import com.bupt.ta.service.ApplicationService;
import com.bupt.ta.service.JobService;
import com.bupt.ta.util.AppConstants;
import com.bupt.ta.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet({"/applicant/jobs", "/applicant/jobs/apply"})
public class ApplicantJobsServlet extends HttpServlet {
    private final JobService jobService = new JobService();
    private final ApplicationService applicationService = new ApplicationService();

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

        request.setAttribute("pageTitle", "Available Jobs");
        request.setAttribute("currentUsername", SessionUtil.getCurrentUsername(request));
        request.setAttribute("jobFilter", jobFilter);
        var jobs = jobService.getOpenJobs(jobFilter);
        Map<String, String> applyDisabledReasons = new HashMap<>();
        Map<String, Integer> remainingVacancies = new HashMap<>();
        String currentUserId = SessionUtil.getCurrentUserId(request);
        for (var job : jobs) {
            applyDisabledReasons.put(job.getJobId(), applicationService.getApplyDisabledReason(currentUserId, job));
            remainingVacancies.put(job.getJobId(), jobService.countRemainingVacancies(job));
        }
        request.setAttribute("jobs", jobs);
        request.setAttribute("applyDisabledReasons", applyDisabledReasons);
        request.setAttribute("remainingVacancies", remainingVacancies);
        request.getRequestDispatcher("/WEB-INF/views/applicant/ta_jobs.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!SessionUtil.requireRole(request, response, Role.TA_APPLICANT)) {
            return;
        }

        request.setCharacterEncoding("UTF-8");
        OperationResult<?> result = applicationService.applyForJob(
            SessionUtil.getCurrentUserId(request),
            request.getParameter("jobId")
        );
        SessionUtil.setFlashMessage(
            request,
            result.isSuccess() ? AppConstants.FLASH_SUCCESS : AppConstants.FLASH_ERROR,
            result.getMessage()
        );
        response.sendRedirect(request.getContextPath() + "/applicant/jobs");
    }
}
