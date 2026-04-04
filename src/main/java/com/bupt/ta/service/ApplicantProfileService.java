package com.bupt.ta.service;

import com.bupt.ta.dto.ApplicantProfileForm;
import com.bupt.ta.dto.OperationResult;
import com.bupt.ta.model.ApplicantProfile;
import com.bupt.ta.repository.ApplicantProfileRepository;
import com.bupt.ta.util.IdGenerator;
import com.bupt.ta.util.StoragePathUtil;
import com.bupt.ta.util.ValidationUtil;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class ApplicantProfileService {
    private final ApplicantProfileRepository applicantProfileRepository = new ApplicantProfileRepository();

    public ApplicantProfile getProfileByUserId(String userId) {
        return applicantProfileRepository.findByUserId(userId);
    }

    public OperationResult<ApplicantProfile> saveProfile(ApplicantProfileForm profileForm, Part cvPart, String userId) {
        if (ValidationUtil.isBlank(profileForm.getStudentId())
            || ValidationUtil.isBlank(profileForm.getProgramme())
            || ValidationUtil.isBlank(profileForm.getYearOfStudy())) {
            return OperationResult.failure("Student ID, programme, and year of study are required.");
        }

        List<ApplicantProfile> profiles = applicantProfileRepository.findAll();
        Optional<ApplicantProfile> existingProfile = profiles.stream()
            .filter(profile -> profile.getUserId().equals(userId))
            .findFirst();

        ApplicantProfile profile = existingProfile.orElseGet(ApplicantProfile::new);
        if (profile.getProfileId() == null) {
            profile.setProfileId(IdGenerator.generateId("profile"));
            profile.setUserId(userId);
        }

        profile.setStudentId(profileForm.getStudentId().trim());
        profile.setProgramme(profileForm.getProgramme().trim());
        profile.setYearOfStudy(profileForm.getYearOfStudy().trim());
        profile.setSkills(safeValue(profileForm.getSkills()));
        profile.setPreferredModules(safeValue(profileForm.getPreferredModules()));
        profile.setPersonalStatement(safeValue(profileForm.getPersonalStatement()));
        profile.setUpdatedAt(LocalDateTime.now().toString());

        if (cvPart != null && cvPart.getSize() > 0) {
            OperationResult<String> cvPathResult = storeCvFile(cvPart, userId);
            if (!cvPathResult.isSuccess()) {
                return OperationResult.failure(cvPathResult.getMessage());
            }
            profile.setCvRelativePath(cvPathResult.getData());
            profile.setCvFileName(extractFileName(cvPart));
        }

        existingProfile.ifPresent(profiles::remove);
        profiles.add(profile);
        applicantProfileRepository.saveAll(profiles);
        return OperationResult.success("Profile saved successfully.", profile);
    }

    public boolean isProfileCompleteForApplication(String userId) {
        return getProfileIncompleteReason(userId) == null;
    }

    public String getProfileIncompleteReason(String userId) {
        return getProfileIncompleteReason(getProfileByUserId(userId));
    }

    public String getProfileIncompleteReason(ApplicantProfile profile) {
        if (profile == null) {
            return "Complete your applicant profile and upload a CV before applying.";
        }
        if (ValidationUtil.isBlank(profile.getStudentId())
            || ValidationUtil.isBlank(profile.getProgramme())
            || ValidationUtil.isBlank(profile.getYearOfStudy())) {
            return "Student ID, programme, and year of study must be completed before applying.";
        }
        if (ValidationUtil.isBlank(profile.getCvRelativePath())) {
            return "Upload a CV before applying for a TA job.";
        }
        return null;
    }

    public Path resolveStoredCvPath(String cvRelativePath) {
        if (ValidationUtil.isBlank(cvRelativePath)) {
            return null;
        }
        Path root = StoragePathUtil.getStorageRoot().normalize();
        Path resolvedPath = root.resolve(cvRelativePath).normalize();
        if (!resolvedPath.startsWith(root)) {
            return null;
        }
        if (!Files.exists(resolvedPath) || !Files.isRegularFile(resolvedPath)) {
            return null;
        }
        return resolvedPath;
    }

    public OperationResult<String> storeCvFile(Part cvPart, String userId) {
        String fileName = extractFileName(cvPart);
        if (ValidationUtil.isBlank(fileName)) {
            return OperationResult.failure("Please choose a CV file to upload.");
        }
        if (cvPart.getSize() > 5L * 1024L * 1024L) {
            return OperationResult.failure("CV file must be 5 MB or smaller.");
        }

        String lowerCaseName = fileName.toLowerCase();
        if (!(lowerCaseName.endsWith(".pdf") || lowerCaseName.endsWith(".doc") || lowerCaseName.endsWith(".docx"))) {
            return OperationResult.failure("CV file must be PDF, DOC, or DOCX.");
        }

        String savedName = userId + "_" + System.currentTimeMillis() + "_" + fileName.replaceAll("[^a-zA-Z0-9._-]", "_");
        Path targetPath = StoragePathUtil.getCvUploadDirectory().resolve(savedName);

        try {
            Files.copy(cvPart.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException exception) {
            return OperationResult.failure("Unable to store the CV file.");
        }

        return OperationResult.success("CV stored successfully.", "uploads/cv/" + savedName);
    }

    private String extractFileName(Part cvPart) {
        String submittedFileName = cvPart.getSubmittedFileName();
        return submittedFileName == null ? "" : submittedFileName.trim();
    }

    private String safeValue(String value) {
        return value == null ? "" : value.trim();
    }
}
