package com.bupt.ta.servlet;

import com.bupt.ta.model.ApplicantProfile;
import com.bupt.ta.model.Role;
import com.bupt.ta.service.ApplicantProfileService;
import com.bupt.ta.service.ApplicationService;
import com.bupt.ta.service.JobService;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/cv/download")
public class CvDownloadServlet extends HttpServlet {
    private final ApplicantProfileService applicantProfileService = new ApplicantProfileService();
    private final ApplicationService applicationService = new ApplicationService();
    private final JobService jobService = new JobService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!SessionUtil.requireLogin(request, response)) {
            return;
        }

        Role role = SessionUtil.getCurrentUserRole(request);
        ApplicantProfile profile = null;
        String downloadFileName = null;

        if (role == Role.TA_APPLICANT) {
            profile = applicantProfileService.getProfileByUserId(SessionUtil.getCurrentUserId(request));
            downloadFileName = profile == null ? null : profile.getCvFileName();
        } else {
            String applicationId = request.getParameter("applicationId");
            if (ValidationUtil.isBlank(applicationId)) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Application ID is required.");
                return;
            }

            List<String> allowedJobIds = null;
            if (role == Role.MODULE_ORGANISER) {
                allowedJobIds = new ArrayList<>();
                for (var job : jobService.getJobsPostedByMo(SessionUtil.getCurrentUserId(request))) {
                    allowedJobIds.add(job.getJobId());
                }
            }

            var applicationDetail = allowedJobIds == null
                ? applicationService.getApplicationDetail(applicationId)
                : applicationService.getApplicationDetail(applicationId, allowedJobIds);
            if (applicationDetail == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "CV file not found for this application.");
                return;
            }

            profile = applicantProfileService.getProfileByUserId(applicationDetail.getApplicantUserId());
            downloadFileName = applicationDetail.getCvFileName();
        }

        if (profile == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Applicant profile not found.");
            return;
        }

        Path cvPath = applicantProfileService.resolveStoredCvPath(profile.getCvRelativePath());
        if (cvPath == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "CV file could not be located.");
            return;
        }

        String contentType = Files.probeContentType(cvPath);
        response.setContentType(contentType == null ? "application/octet-stream" : contentType);
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + URLEncoder.encode(
            ValidationUtil.isBlank(downloadFileName) ? cvPath.getFileName().toString() : downloadFileName,
            StandardCharsets.UTF_8
        ));
        response.setContentLengthLong(Files.size(cvPath));
        Files.copy(cvPath, response.getOutputStream());
    }
}
