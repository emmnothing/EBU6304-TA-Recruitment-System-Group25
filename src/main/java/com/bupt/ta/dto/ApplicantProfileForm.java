package com.bupt.ta.dto;

public class ApplicantProfileForm {
    private String studentId;
    private String programme;
    private String yearOfStudy;
    private String skills;
    private String preferredModules;
    private String personalStatement;

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
}
