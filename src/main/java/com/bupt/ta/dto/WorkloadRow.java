package com.bupt.ta.dto;

public class WorkloadRow {
    private String username;
    private int assignedModules;
    private int weeklyHours;
    private String workloadStatus;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getAssignedModules() {
        return assignedModules;
    }

    public void setAssignedModules(int assignedModules) {
        this.assignedModules = assignedModules;
    }

    public int getWeeklyHours() {
        return weeklyHours;
    }

    public void setWeeklyHours(int weeklyHours) {
        this.weeklyHours = weeklyHours;
    }

    public String getWorkloadStatus() {
        return workloadStatus;
    }

    public void setWorkloadStatus(String workloadStatus) {
        this.workloadStatus = workloadStatus;
    }
}
