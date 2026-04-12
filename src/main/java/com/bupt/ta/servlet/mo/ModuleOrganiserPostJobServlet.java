package com.bupt.ta.servlet.mo;

import com.bupt.ta.dto.JobForm;
import com.bupt.ta.dto.OperationResult;
import com.bupt.ta.model.JobPost;
import com.bupt.ta.model.Role;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet({"/mo/post-job", "/mo/post-job/close", "/mo/post-job/edit", "/mo/post-job/delete"})
public class ModuleOrganiserPostJobServlet extends HttpServlet {
    private final JobService jobService = new JobService();
    private final NotificationService notificationService = new NotificationService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!SessionUtil.requireRole(request, response, Role.MODULE_ORGANISER)) {
            return;
        }
        SessionUtil.exposeFlashMessage(request);
        request.setAttribute("pageTitle", "Post TA Job");
        request.setAttribute("currentUsername", SessionUtil.getCurrentUsername(request));
        String userId = SessionUtil.getCurrentUserId(request);
        List<JobPost> myJobs = jobService.getJobsPostedByMo(userId);
        Map<String, Integer> applicationCounts = new HashMap<>();
        for (JobPost job : myJobs) {
            applicationCounts.put(job.getJobId(), jobService.countApplications(job.getJobId()));
        }
        request.setAttribute("myJobs", myJobs);
        request.setAttribute("applicationCounts", applicationCounts);
        request.setAttribute("unreadNotificationCount", notificationService.countUnread(userId));

        String editJobId = request.getParameter("editJobId");
        if (!ValidationUtil.isBlank(editJobId)) {
            JobPost editingJob = findOwnedJob(myJobs, editJobId);
            if (editingJob == null) {
                SessionUtil.setFlashMessage(request, AppConstants.FLASH_ERROR, "The selected job could not be found.");
                response.sendRedirect(request.getContextPath() + "/mo/post-job");
                return;
            }
            if (!editingJob.getStatus().isOpen()) {
                SessionUtil.setFlashMessage(request, AppConstants.FLASH_ERROR, "Only open job posts can be edited.");
                response.sendRedirect(request.getContextPath() + "/mo/post-job");
                return;
            }
            request.setAttribute("pageTitle", "Edit TA Job");
            request.setAttribute("editingJob", editingJob);
        }

        request.getRequestDispatcher("/WEB-INF/views/mo/mo_post_job.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!SessionUtil.requireRole(request, response, Role.MODULE_ORGANISER)) {
            return;
        }
        request.setCharacterEncoding("UTF-8");

        String requestPath = request.getRequestURI();
        String currentUserId = SessionUtil.getCurrentUserId(request);
        OperationResult<?> result;
        if (requestPath.endsWith("/close")) {
            result = jobService.closeJobManually(request.getParameter("jobId"), currentUserId);
        } else if (requestPath.endsWith("/delete")) {
            result = jobService.deleteJob(request.getParameter("jobId"), currentUserId);
        } else if (requestPath.endsWith("/edit")) {
            result = jobService.updateJob(request.getParameter("jobId"), buildJobForm(request), currentUserId);
        } else {
            result = jobService.createJob(buildJobForm(request), currentUserId);
        }
        SessionUtil.setFlashMessage(
            request,
            result.isSuccess() ? AppConstants.FLASH_SUCCESS : AppConstants.FLASH_ERROR,
            result.getMessage()
        );
        response.sendRedirect(request.getContextPath() + buildRedirectPath(requestPath, request.getParameter("jobId"), result.isSuccess()));
    }

    private JobForm buildJobForm(HttpServletRequest request) {
        JobForm jobForm = new JobForm();
        jobForm.setModuleCode(request.getParameter("moduleCode"));
        jobForm.setModuleName(request.getParameter("moduleName"));
        jobForm.setJobTitle(request.getParameter("jobTitle"));
        jobForm.setVacancies(request.getParameter("vacancies"));
        jobForm.setWeeklyHours(request.getParameter("weeklyHours"));
        jobForm.setApplicationDeadline(request.getParameter("applicationDeadline"));
        jobForm.setLocationMode(request.getParameter("locationMode"));
        jobForm.setDescription(request.getParameter("description"));
        jobForm.setRequirements(request.getParameter("requirements"));
        return jobForm;
    }

    private JobPost findOwnedJob(List<JobPost> jobs, String jobId) {
        for (JobPost job : jobs) {
            if (job.getJobId().equals(jobId)) {
                return job;
            }
        }
        return null;
    }

    private String buildRedirectPath(String requestPath, String jobId, boolean success) {
        if (requestPath.endsWith("/edit") && !success && !ValidationUtil.isBlank(jobId)) {
            return "/mo/post-job?editJobId=" + jobId;
        }
        return "/mo/post-job";
    }
}
