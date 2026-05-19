package com.bupt.ta.servlet.admin;

import com.bupt.ta.dto.WorkloadRow;
import com.bupt.ta.model.ApplicantProfile;
import com.bupt.ta.model.JobApplication;
import com.bupt.ta.model.JobPost;
import com.bupt.ta.model.Role;
import com.bupt.ta.model.User;
import com.bupt.ta.service.AdminService;
import com.bupt.ta.service.AuditLogService;
import com.bupt.ta.util.AppConstants;
import com.bupt.ta.util.SessionUtil;
import com.bupt.ta.util.ValidationUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@WebServlet("/admin/export")
public class AdminExportServlet extends HttpServlet {
    private final AdminService adminService = new AdminService();
    private final AuditLogService auditLogService = new AuditLogService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!SessionUtil.requireRole(request, response, Role.ADMINISTRATOR)) {
            return;
        }

        String exportType = request.getParameter("type");
        if (ValidationUtil.isBlank(exportType)) {
            auditExport(request, "", false, "Missing export type.");
            SessionUtil.setFlashMessage(request, AppConstants.FLASH_ERROR, "Please choose an export type.");
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
            return;
        }

        List<User> users = adminService.findAllUsers();
        List<JobPost> jobs = adminService.findAllJobs();
        List<JobApplication> applications = adminService.findAllApplications();

        switch (exportType.trim().toLowerCase()) {
            case "users":
                exportUsers(response, users);
                auditExport(request, "users", true, "Rows exported: " + users.size());
                return;
            case "jobs":
                exportJobs(response, users, jobs, applications);
                auditExport(request, "jobs", true, "Rows exported: " + jobs.size());
                return;
            case "applications":
                exportApplications(response, users, jobs, applications);
                auditExport(request, "applications", true, "Rows exported: " + applications.size());
                return;
            case "workload":
                exportWorkload(response, users, applications, adminService.findAllApplicantProfiles());
                auditExport(
                    request,
                    "workload",
                    true,
                    "Rows exported: " + adminService.buildWorkloadRows(users, applications).size()
                );
                return;
            default:
                auditExport(request, exportType, false, "Unsupported export type.");
                SessionUtil.setFlashMessage(request, AppConstants.FLASH_ERROR, "Unsupported export type.");
                response.sendRedirect(request.getContextPath() + "/admin/dashboard");
        }
    }

    private void auditExport(HttpServletRequest request, String exportType, boolean success, String detail) {
        auditLogService.recordAdminAction(
            request,
            success ? AuditLogService.ACTION_CSV_EXPORT_DOWNLOADED : AuditLogService.ACTION_CSV_EXPORT_REJECTED,
            AuditLogService.TARGET_EXPORT,
            exportType,
            exportType == null || exportType.isBlank() ? "CSV export" : exportType + " CSV export",
            success,
            detail
        );
    }

    private void exportUsers(HttpServletResponse response, List<User> users) throws IOException {
        PrintWriter writer = beginCsvResponse(response, "users");
        writer.write("User ID,Username,Role,Email,Phone,Status,Created At,Status Updated At\r\n");
        for (User user : users) {
            writer.write(csv(user.getUserId()));
            writer.write(',');
            writer.write(csv(user.getUsername()));
            writer.write(',');
            writer.write(csv(user.getRole() == null ? "" : user.getRole().getDisplayName()));
            writer.write(',');
            writer.write(csv(user.getEmail()));
            writer.write(',');
            writer.write(csv(user.getPhoneNumber()));
            writer.write(',');
            writer.write(csv(user.isActive() ? "Active" : "Inactive"));
            writer.write(',');
            writer.write(csv(user.getCreatedAt()));
            writer.write(',');
            writer.write(csv(user.getStatusUpdatedAt()));
            writer.write("\r\n");
        }
        writer.flush();
    }

    private void exportJobs(
        HttpServletResponse response,
        List<User> users,
        List<JobPost> jobs,
        List<JobApplication> applications
    ) throws IOException {
        PrintWriter writer = beginCsvResponse(response, "jobs");
        writer.write(
            "Job ID,Module Code,Module Name,Job Title,Owner,Status,Vacancies,Filled Count,Application Count,Weekly Hours,Deadline,Location Mode,Created At,Status Updated At,Closed At,Closed Reason\r\n"
        );
        for (JobPost job : jobs) {
            writer.write(csv(job.getJobId()));
            writer.write(',');
            writer.write(csv(job.getModuleCode()));
            writer.write(',');
            writer.write(csv(job.getModuleName()));
            writer.write(',');
            writer.write(csv(job.getJobTitle()));
            writer.write(',');
            writer.write(csv(findUsername(users, job.getPostedByUserId())));
            writer.write(',');
            writer.write(csv(job.getStatus() == null ? "" : job.getStatus().name()));
            writer.write(',');
            writer.write(csv(String.valueOf(job.getVacancies())));
            writer.write(',');
            writer.write(csv(String.valueOf(job.getFilledCount())));
            writer.write(',');
            writer.write(csv(String.valueOf(countApplicationsForJob(applications, job.getJobId()))));
            writer.write(',');
            writer.write(csv(String.valueOf(job.getWeeklyHours())));
            writer.write(',');
            writer.write(csv(job.getApplicationDeadline()));
            writer.write(',');
            writer.write(csv(job.getLocationMode()));
            writer.write(',');
            writer.write(csv(job.getCreatedAt()));
            writer.write(',');
            writer.write(csv(job.getStatusUpdatedAt()));
            writer.write(',');
            writer.write(csv(job.getClosedAt()));
            writer.write(',');
            writer.write(csv(job.getClosedReason()));
            writer.write("\r\n");
        }
        writer.flush();
    }

    private void exportApplications(
        HttpServletResponse response,
        List<User> users,
        List<JobPost> jobs,
        List<JobApplication> applications
    ) throws IOException {
        PrintWriter writer = beginCsvResponse(response, "applications");
        writer.write(
            "Application ID,Applicant,Applicant Email,Module Code,Module Name,Job Title,Module Organiser,Status,Applied At,Reviewed At,Status Updated At,Interview At,Interview Mode,Interview Location,Remarks\r\n"
        );
        for (JobApplication application : applications) {
            User applicant = findUser(users, application.getApplicantUserId());
            JobPost job = findJob(jobs, application.getJobId());
            writer.write(csv(application.getApplicationId()));
            writer.write(',');
            writer.write(csv(applicant == null ? "" : applicant.getUsername()));
            writer.write(',');
            writer.write(csv(applicant == null ? "" : applicant.getEmail()));
            writer.write(',');
            writer.write(csv(job == null ? "" : job.getModuleCode()));
            writer.write(',');
            writer.write(csv(job == null ? "" : job.getModuleName()));
            writer.write(',');
            writer.write(csv(job == null ? "" : job.getJobTitle()));
            writer.write(',');
            writer.write(csv(job == null ? "" : findUsername(users, job.getPostedByUserId())));
            writer.write(',');
            writer.write(csv(application.getStatus() == null ? "" : application.getStatus().getDisplayName()));
            writer.write(',');
            writer.write(csv(application.getAppliedAt()));
            writer.write(',');
            writer.write(csv(application.getReviewedAt()));
            writer.write(',');
            writer.write(csv(application.getStatusUpdatedAt()));
            writer.write(',');
            writer.write(csv(application.getInterviewScheduledAt()));
            writer.write(',');
            writer.write(csv(application.getInterviewMode()));
            writer.write(',');
            writer.write(csv(application.getInterviewLocation()));
            writer.write(',');
            writer.write(csv(application.getRemarks()));
            writer.write("\r\n");
        }
        writer.flush();
    }

    private void exportWorkload(
        HttpServletResponse response,
        List<User> users,
        List<JobApplication> applications,
        List<ApplicantProfile> profiles
    ) throws IOException {
        PrintWriter writer = beginCsvResponse(response, "workload");
        List<WorkloadRow> workloadRows = adminService.buildWorkloadRows(users, applications);
        writer.write("Applicant,Email,Student ID,Programme,Assigned Modules,Weekly Hours,Workload Status\r\n");
        for (WorkloadRow row : workloadRows) {
            User user = findUserByUsername(users, row.getUsername());
            ApplicantProfile profile = user == null ? null : findProfile(profiles, user.getUserId());
            writer.write(csv(row.getUsername()));
            writer.write(',');
            writer.write(csv(user == null ? "" : user.getEmail()));
            writer.write(',');
            writer.write(csv(profile == null ? "" : profile.getStudentId()));
            writer.write(',');
            writer.write(csv(profile == null ? "" : profile.getProgramme()));
            writer.write(',');
            writer.write(csv(String.valueOf(row.getAssignedModules())));
            writer.write(',');
            writer.write(csv(String.valueOf(row.getWeeklyHours())));
            writer.write(',');
            writer.write(csv(row.getWorkloadStatus()));
            writer.write("\r\n");
        }
        writer.flush();
    }

    private PrintWriter beginCsvResponse(HttpServletResponse response, String prefix) throws IOException {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader(
            "Content-Disposition",
            "attachment; filename*=UTF-8''" + URLEncoder.encode(prefix + "-export-" + timestamp + ".csv", StandardCharsets.UTF_8)
        );
        PrintWriter writer = response.getWriter();
        writer.write('\uFEFF');
        return writer;
    }

    private int countApplicationsForJob(List<JobApplication> applications, String jobId) {
        int count = 0;
        for (JobApplication application : applications) {
            if (jobId.equals(application.getJobId())) {
                count++;
            }
        }
        return count;
    }

    private String findUsername(List<User> users, String userId) {
        User user = findUser(users, userId);
        return user == null ? "" : user.getUsername();
    }

    private User findUser(List<User> users, String userId) {
        for (User user : users) {
            if (user.getUserId().equals(userId)) {
                return user;
            }
        }
        return null;
    }

    private User findUserByUsername(List<User> users, String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    private JobPost findJob(List<JobPost> jobs, String jobId) {
        for (JobPost job : jobs) {
            if (job.getJobId().equals(jobId)) {
                return job;
            }
        }
        return null;
    }

    private ApplicantProfile findProfile(List<ApplicantProfile> profiles, String userId) {
        for (ApplicantProfile profile : profiles) {
            if (profile.getUserId().equals(userId)) {
                return profile;
            }
        }
        return null;
    }

    private String csv(String value) {
        if (value == null) {
            return "\"\"";
        }
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }
}
