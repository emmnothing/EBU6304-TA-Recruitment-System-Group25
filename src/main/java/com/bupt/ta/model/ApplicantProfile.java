package com.bupt.ta.model;

import java.util.ArrayList;
import java.util.List;

public class ApplicantProfile {
    private String profileId;
    private String userId;
    private String studentId;
    private String programme;
    private String yearOfStudy;
    private String skills;
    private String preferredModules;
    private String personalStatement;
    private String cvFileName;
    private String cvRelativePath;
    private String updatedAt;
    private List<String> favoriteJobIds = new ArrayList<>();

    public ApplicantProfile() {
    }

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getProgramme() {
        return programme;
    }

    public void setProgramme(String programme) {
        this.programme = programme;
    }

    public String getYearOfStudy() {
        return yearOfStudy;
    }

    public void setYearOfStudy(String yearOfStudy) {
        this.yearOfStudy = yearOfStudy;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public String getPreferredModules() {
        return preferredModules;
    }

    public void setPreferredModules(String preferredModules) {
        this.preferredModules = preferredModules;
    }

    public String getPersonalStatement() {
        return personalStatement;
    }

    public void setPersonalStatement(String personalStatement) {
        this.personalStatement = personalStatement;
    }

    public String getCvFileName() {
        return cvFileName;
    }

    public void setCvFileName(String cvFileName) {
        this.cvFileName = cvFileName;
    }

    public String getCvRelativePath() {
        return cvRelativePath;
    }

    public void setCvRelativePath(String cvRelativePath) {
        this.cvRelativePath = cvRelativePath;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<String> getFavoriteJobIds() {
        if (favoriteJobIds == null) {
            favoriteJobIds = new ArrayList<>();
        }
        return favoriteJobIds;
    }

    public void setFavoriteJobIds(List<String> favoriteJobIds) {
        this.favoriteJobIds = favoriteJobIds == null ? new ArrayList<>() : favoriteJobIds;
    }
}
