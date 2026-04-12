package com.bupt.ta.dto;

import java.util.ArrayList;
import java.util.List;

public class AdminUserManagementView {
    private int totalUsers;
    private int activeUsers;
    private int inactiveUsers;
    private int applicantCount;
    private int moduleOrganiserCount;
    private int administratorCount;
    private List<AdminUserRecord> userRecords = new ArrayList<>();

    public int getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(int totalUsers) {
        this.totalUsers = totalUsers;
    }

    public int getActiveUsers() {
        return activeUsers;
    }

    public void setActiveUsers(int activeUsers) {
        this.activeUsers = activeUsers;
    }

    public int getInactiveUsers() {
        return inactiveUsers;
    }

    public void setInactiveUsers(int inactiveUsers) {
        this.inactiveUsers = inactiveUsers;
    }

    public int getApplicantCount() {
        return applicantCount;
    }

    public void setApplicantCount(int applicantCount) {
        this.applicantCount = applicantCount;
    }

    public int getModuleOrganiserCount() {
        return moduleOrganiserCount;
    }

    public void setModuleOrganiserCount(int moduleOrganiserCount) {
        this.moduleOrganiserCount = moduleOrganiserCount;
    }

    public int getAdministratorCount() {
        return administratorCount;
    }

    public void setAdministratorCount(int administratorCount) {
        this.administratorCount = administratorCount;
    }

    public List<AdminUserRecord> getUserRecords() {
        return userRecords;
    }

    public void setUserRecords(List<AdminUserRecord> userRecords) {
        this.userRecords = userRecords;
    }
}
