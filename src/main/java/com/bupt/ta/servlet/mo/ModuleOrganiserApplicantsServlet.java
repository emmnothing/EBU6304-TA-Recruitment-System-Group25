package com.bupt.ta.servlet.mo;

import com.bupt.ta.dto.ApplicantFilter;
import com.bupt.ta.dto.OperationResult;
import com.bupt.ta.model.JobPost;
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
import java.util.ArrayList;
import java.util.List;

@WebServlet({"/mo/applicants", "/mo/applicants/decision"})
public class ModuleOrganiserApplicantsServlet extends HttpServlet {
    private final JobService jobService = new JobService();
    private final ApplicationService applicationService = new ApplicationService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!SessionUtil.requireRole(request, response, Role.MODULE_ORGANISER)) {
            return;
        }
        SessionUtil.exposeFlashMessage(request);

        List<JobPost> moJobs = jobService.getJobsPostedByMo(SessionUtil.getCurrentUserId(request));
        List<String> allowedJobIds = new ArrayList<>();
        for (JobPost job : moJobs) {
            allowedJobIds.add(job.getJobId());
        }
        ApplicantFilter applicantFilter = new ApplicantFilter();
        applicantFilter.setJobId(request.getParameter("jobId"));
        applicantFilter.setStatus(request.getParameter("status"));
        applicantFilter.setKeyword(request.getParameter("keyword"));

        request.setAttribute("pageTitle", "Applicants Review");
        request.setAttribute("currentUsername", SessionUtil.getCurrentUsername(request));
        request.setAttribute("jobOptions", moJobs);
        request.setAttribute("applicantFilter", applicantFilter);
        request.setAttribute("applicantList", applicationService.getApplicantsForMo(allowedJobIds, applicantFilter.getJobId(), applicantFilter));
        request.setAttribute("selectedApplicationId", request.getParameter("applicationId"));
        if (request.getParameter("applicationId") != null && !request.getParameter("applicationId").isBlank()) {
            request.setAttribute("applicationDetail", applicationService.getApplicationDetail(request.getParameter("applicationId"), allowedJobIds));
        }
        request.getRequestDispatcher("/WEB-INF/views/mo/mo_applicants.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!SessionUtil.requireRole(request, response, Role.MODULE_ORGANISER)) {
            return;
        }
        request.setCharacterEncoding("UTF-8");
        OperationResult<?> result = applicationService.updateDecision(
            request.getParameter("applicationId"),
            request.getParameter("decision"),
            request.getParameter("remarks"),
            SessionUtil.getCurrentUserId(request)
        );
        SessionUtil.setFlashMessage(
            request,
            result.isSuccess() ? AppConstants.FLASH_SUCCESS : AppConstants.FLASH_ERROR,
            result.getMessage()
        );
        response.sendRedirect(request.getContextPath() + "/mo/applicants?jobId=" + safeQuery(request.getParameter("jobId")));
    }

    private String safeQuery(String value) {
        return value == null ? "" : value;
    }
}
