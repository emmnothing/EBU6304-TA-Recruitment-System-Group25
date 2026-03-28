package com.bupt.ta.servlet.applicant;

import com.bupt.ta.dto.ApplicantProfileForm;
import com.bupt.ta.dto.OperationResult;
import com.bupt.ta.model.Role;
import com.bupt.ta.service.ApplicantProfileService;
import com.bupt.ta.util.AppConstants;
import com.bupt.ta.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.IOException;

@WebServlet("/applicant/profile")
@MultipartConfig
public class ApplicantProfileServlet extends HttpServlet {
    private final ApplicantProfileService applicantProfileService = new ApplicantProfileService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!SessionUtil.requireRole(request, response, Role.TA_APPLICANT)) {
            return;
        }
        SessionUtil.exposeFlashMessage(request);
        request.setAttribute("pageTitle", "My Profile");
        request.setAttribute("currentUsername", SessionUtil.getCurrentUsername(request));
        request.setAttribute("profile", applicantProfileService.getProfileByUserId(SessionUtil.getCurrentUserId(request)));
        request.getRequestDispatcher("/WEB-INF/views/applicant/ta_profile.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!SessionUtil.requireRole(request, response, Role.TA_APPLICANT)) {
            return;
        }

        request.setCharacterEncoding("UTF-8");
        ApplicantProfileForm profileForm = new ApplicantProfileForm();
        profileForm.setStudentId(request.getParameter("studentId"));
        profileForm.setProgramme(request.getParameter("programme"));
        profileForm.setYearOfStudy(request.getParameter("yearOfStudy"));
        profileForm.setSkills(request.getParameter("skills"));
        profileForm.setPreferredModules(request.getParameter("preferredModules"));
        profileForm.setPersonalStatement(request.getParameter("personalStatement"));
        Part cvPart = request.getPart("cvFile");

        OperationResult<?> result = applicantProfileService.saveProfile(profileForm, cvPart, SessionUtil.getCurrentUserId(request));
        SessionUtil.setFlashMessage(
            request,
            result.isSuccess() ? AppConstants.FLASH_SUCCESS : AppConstants.FLASH_ERROR,
            result.getMessage()
        );
        response.sendRedirect(request.getContextPath() + "/applicant/profile");
    }
}
