package com.bupt.ta.servlet.mo;

import com.bupt.ta.dto.ApplicantFilter;
import com.bupt.ta.dto.ApplicantReviewItem;
import com.bupt.ta.dto.OperationResult;
import com.bupt.ta.model.JobPost;
import com.bupt.ta.model.Role;
import com.bupt.ta.service.ApplicationService;
import com.bupt.ta.service.JobService;
import com.bupt.ta.service.NotificationService;
import com.bupt.ta.util.AppConstants;
import com.bupt.ta.util.SessionUtil;
import com.bupt.ta.util.ValidationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/mo/interviews")
public class ModuleOrganiserInterviewServlet extends HttpServlet {
    private final JobService jobService = new JobService();
    private final ApplicationService applicationService = new ApplicationService();
    private final NotificationService notificationService = new NotificationService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!SessionUtil.requireRole(request, response, Role.MODULE_ORGANISER)) {
            return;
        }

        String userId = SessionUtil.getCurrentUserId(request);
        List<JobPost> moJobs = jobService.getJobsPostedByMo(userId);
        List<String> allowedJobIds = buildAllowedJobIds(moJobs);
        ApplicantFilter applicantFilter = buildApplicantFilter(request);

        SessionUtil.exposeFlashMessage(request);
        request.setAttribute("pageTitle", "Interview Planning");
        request.setAttribute("currentUsername", SessionUtil.getCurrentUsername(request));
        request.setAttribute("unreadNotificationCount", notificationService.countUnread(userId));
        request.setAttribute("jobOptions", moJobs);
        request.setAttribute("applicantFilter", applicantFilter);
        request.setAttribute("applicantList", applicationService.getApplicantsForMo(allowedJobIds, applicantFilter.getJobId(), applicantFilter));

        String selectedApplicationId = request.getParameter("applicationId");
        request.setAttribute("selectedApplicationId", selectedApplicationId);
        if (!ValidationUtil.isBlank(selectedApplicationId)) {
            request.setAttribute("applicationDetail", applicationService.getApplicationDetail(selectedApplicationId, allowedJobIds));
        }
        request.getRequestDispatcher("/WEB-INF/views/mo/mo_interviews.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!SessionUtil.requireRole(request, response, Role.MODULE_ORGANISER)) {
            return;
        }
        request.setCharacterEncoding("UTF-8");

        ApplicantFilter applicantFilter = buildApplicantFilter(request);
        String selectedApplicationId = request.getParameter("applicationId");
        OperationResult<?> result = applicationService.scheduleInterview(
            selectedApplicationId,
            request.getParameter("interviewScheduledAt"),
            request.getParameter("interviewMode"),
            request.getParameter("interviewLocation"),
            request.getParameter("interviewNotes"),
            SessionUtil.getCurrentUserId(request)
        );

        SessionUtil.setFlashMessage(
            request,
            result.isSuccess() ? AppConstants.FLASH_SUCCESS : AppConstants.FLASH_ERROR,
            result.getMessage()
        );
        response.sendRedirect(request.getContextPath() + buildInterviewRedirect(applicantFilter, selectedApplicationId));
    }

    private ApplicantFilter buildApplicantFilter(HttpServletRequest request) {
        ApplicantFilter applicantFilter = new ApplicantFilter();
        applicantFilter.setJobId(request.getParameter("jobId"));
        applicantFilter.setStatus(request.getParameter("status"));
        applicantFilter.setKeyword(request.getParameter("keyword"));
        applicantFilter.setHasCv(request.getParameter("hasCv"));
        applicantFilter.setSortBy(request.getParameter("sortBy"));
        applicantFilter.setSortDirection(request.getParameter("sortDirection"));
        return applicantFilter;
    }

    private List<String> buildAllowedJobIds(List<JobPost> moJobs) {
        List<String> allowedJobIds = new ArrayList<>();
        for (JobPost job : moJobs) {
            allowedJobIds.add(job.getJobId());
        }
        return allowedJobIds;
    }

    private String buildInterviewRedirect(ApplicantFilter applicantFilter, String applicationId) {
        StringBuilder builder = new StringBuilder("/mo/interviews?");
        appendQuery(builder, "jobId", applicantFilter == null ? null : applicantFilter.getJobId());
        appendQuery(builder, "status", applicantFilter == null ? null : applicantFilter.getStatus());
        appendQuery(builder, "keyword", applicantFilter == null ? null : applicantFilter.getKeyword());
        appendQuery(builder, "hasCv", applicantFilter == null ? null : applicantFilter.getHasCv());
        appendQuery(builder, "sortBy", applicantFilter == null ? null : applicantFilter.getSortBy());
        appendQuery(builder, "sortDirection", applicantFilter == null ? null : applicantFilter.getSortDirection());
        appendQuery(builder, "applicationId", applicationId);
        return builder.toString();
    }

    private void appendQuery(StringBuilder builder, String key, String value) {
        if (builder.charAt(builder.length() - 1) != '?') {
            builder.append('&');
        }
        builder.append(key).append('=').append(encode(value));
    }

    private String encode(String value) {
        return URLEncoder.encode(value == null ? "" : value, StandardCharsets.UTF_8);
    }
}
