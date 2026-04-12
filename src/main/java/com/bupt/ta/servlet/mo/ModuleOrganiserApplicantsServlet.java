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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@WebServlet({
    "/mo/applicants",
    "/mo/applicants/decision",
    "/mo/applicants/batch",
    "/mo/applicants/interview",
    "/mo/applicants/export"
})
public class ModuleOrganiserApplicantsServlet extends HttpServlet {
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

        if (request.getRequestURI().endsWith("/export")) {
            exportApplicantsCsv(response, allowedJobIds, applicantFilter);
            return;
        }

        SessionUtil.exposeFlashMessage(request);
        request.setAttribute("pageTitle", "Applicants Review");
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
        request.getRequestDispatcher("/WEB-INF/views/mo/mo_applicants.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!SessionUtil.requireRole(request, response, Role.MODULE_ORGANISER)) {
            return;
        }
        request.setCharacterEncoding("UTF-8");

        ApplicantFilter applicantFilter = buildApplicantFilter(request);
        String requestPath = request.getRequestURI();
        String currentUserId = SessionUtil.getCurrentUserId(request);
        String selectedApplicationId = request.getParameter("applicationId");
        OperationResult<?> result;

        if (requestPath.endsWith("/batch")) {
            result = applicationService.batchUpdateDecision(
                request.getParameterValues("applicationIds"),
                request.getParameter("decision"),
                request.getParameter("remarks"),
                currentUserId
            );
            selectedApplicationId = request.getParameter("selectedApplicationId");
        } else if (requestPath.endsWith("/interview")) {
            result = applicationService.scheduleInterview(
                request.getParameter("applicationId"),
                request.getParameter("interviewScheduledAt"),
                request.getParameter("interviewMode"),
                request.getParameter("interviewLocation"),
                request.getParameter("interviewNotes"),
                currentUserId
            );
        } else {
            result = applicationService.updateDecision(
                request.getParameter("applicationId"),
                request.getParameter("decision"),
                request.getParameter("remarks"),
                currentUserId
            );
        }

        SessionUtil.setFlashMessage(
            request,
            result.isSuccess() ? AppConstants.FLASH_SUCCESS : AppConstants.FLASH_ERROR,
            result.getMessage()
        );
        response.sendRedirect(request.getContextPath() + buildApplicantsRedirect(applicantFilter, selectedApplicationId));
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

    private String buildApplicantsRedirect(ApplicantFilter applicantFilter, String applicationId) {
        StringBuilder builder = new StringBuilder("/mo/applicants?");
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

    private void exportApplicantsCsv(HttpServletResponse response, List<String> allowedJobIds, ApplicantFilter applicantFilter) throws IOException {
        List<ApplicantReviewItem> applicantList = applicationService.getApplicantsForMo(allowedJobIds, applicantFilter.getJobId(), applicantFilter);
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader(
            "Content-Disposition",
            "attachment; filename*=UTF-8''" + URLEncoder.encode("applicants-export-" + timestamp + ".csv", StandardCharsets.UTF_8)
        );

        var writer = response.getWriter();
        writer.write('\uFEFF');
        writer.write(
            "Application ID,Applicant,Email,Phone,Student ID,Programme,Module Code,Module Name,Job Title,Status,Applied At,Interview At,Interview Mode,Interview Location,Remarks\r\n"
        );
        for (ApplicantReviewItem item : applicantList) {
            writer.write(csv(item.getApplicationId()));
            writer.write(',');
            writer.write(csv(item.getUsername()));
            writer.write(',');
            writer.write(csv(item.getEmail()));
            writer.write(',');
            writer.write(csv(item.getPhoneNumber()));
            writer.write(',');
            writer.write(csv(item.getStudentId()));
            writer.write(',');
            writer.write(csv(item.getProgramme()));
            writer.write(',');
            writer.write(csv(item.getModuleCode()));
            writer.write(',');
            writer.write(csv(item.getModuleName()));
            writer.write(',');
            writer.write(csv(item.getJobTitle()));
            writer.write(',');
            writer.write(csv(item.getStatus() == null ? "" : item.getStatus().getDisplayName()));
            writer.write(',');
            writer.write(csv(item.getAppliedAt()));
            writer.write(',');
            writer.write(csv(item.getInterviewScheduledAt()));
            writer.write(',');
            writer.write(csv(item.getInterviewMode()));
            writer.write(',');
            writer.write(csv(item.getInterviewLocation()));
            writer.write(',');
            writer.write(csv(item.getRemarks()));
            writer.write("\r\n");
        }
        writer.flush();
    }

    private String csv(String value) {
        if (value == null) {
            return "\"\"";
        }
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }
}
