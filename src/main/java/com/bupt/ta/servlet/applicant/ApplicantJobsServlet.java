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
        request.setAttribute("jobs", jobService.getOpenJobs(jobFilter));
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
