package com.bupt.ta.servlet.mo;

import com.bupt.ta.dto.JobForm;
import com.bupt.ta.dto.OperationResult;
import com.bupt.ta.model.Role;
import com.bupt.ta.service.JobService;
import com.bupt.ta.util.AppConstants;
import com.bupt.ta.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/mo/post-job")
public class ModuleOrganiserPostJobServlet extends HttpServlet {
    private final JobService jobService = new JobService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!SessionUtil.requireRole(request, response, Role.MODULE_ORGANISER)) {
            return;
        }
        SessionUtil.exposeFlashMessage(request);
        request.setAttribute("pageTitle", "Post TA Job");
        request.setAttribute("currentUsername", SessionUtil.getCurrentUsername(request));
        request.getRequestDispatcher("/WEB-INF/views/mo/mo_post_job.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!SessionUtil.requireRole(request, response, Role.MODULE_ORGANISER)) {
            return;
        }
        request.setCharacterEncoding("UTF-8");

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

        OperationResult<?> result = jobService.createJob(jobForm, SessionUtil.getCurrentUserId(request));
        SessionUtil.setFlashMessage(
            request,
            result.isSuccess() ? AppConstants.FLASH_SUCCESS : AppConstants.FLASH_ERROR,
            result.getMessage()
        );
        response.sendRedirect(request.getContextPath() + "/mo/post-job");
    }
}
