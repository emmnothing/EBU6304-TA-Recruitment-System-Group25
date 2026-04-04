package com.bupt.ta.dto;

import java.util.ArrayList;
import java.util.List;

public class AdminOverview {
    private int totalApplicants;
    private int totalJobs;
    private int totalApplications;
    private int openJobs;
    private int applicationsPendingReview;
    private int assignedTas;
    private int highWorkloadAlerts;
    private List<WorkloadRow> workloadRows = new ArrayList<>();
    private List<String> recordsList = new ArrayList<>();
    private List<AdminJobRecord> recentJobs = new ArrayList<>();
    private List<AdminApplicationRecord> recentApplications = new ArrayList<>();

    public int getTotalApplicants() {
        return totalApplicants;
    }

    public void setTotalApplicants(int totalApplicants) {
        this.totalApplicants = totalApplicants;
    }

    public int getOpenJobs() {
        return openJobs;
    }

    public void setOpenJobs(int openJobs) {
        this.openJobs = openJobs;
    }

    public int getTotalJobs() {
        return totalJobs;
    }

    public void setTotalJobs(int totalJobs) {
        this.totalJobs = totalJobs;
    }

    public int getTotalApplications() {
        return totalApplications;
    }

    public void setTotalApplications(int totalApplications) {
        this.totalApplications = totalApplications;
    }

    public int getApplicationsPendingReview() {
        return applicationsPendingReview;
    }

    public void setApplicationsPendingReview(int applicationsPendingReview) {
        this.applicationsPendingReview = applicationsPendingReview;
    }

    public int getAssignedTas() {
        return assignedTas;
    }

    public void setAssignedTas(int assignedTas) {
        this.assignedTas = assignedTas;
    }

    public int getHighWorkloadAlerts() {
        return highWorkloadAlerts;
    }

    public void setHighWorkloadAlerts(int highWorkloadAlerts) {
        this.highWorkloadAlerts = highWorkloadAlerts;
    }

    public List<WorkloadRow> getWorkloadRows() {
        return workloadRows;
    }

    public void setWorkloadRows(List<WorkloadRow> workloadRows) {
        this.workloadRows = workloadRows;
    }

    public List<String> getRecordsList() {
        return recordsList;
    }

    public void setRecordsList(List<String> recordsList) {
        this.recordsList = recordsList;
    }

    public List<AdminJobRecord> getRecentJobs() {
        return recentJobs;
    }

    public void setRecentJobs(List<AdminJobRecord> recentJobs) {
        this.recentJobs = recentJobs;
    }

    public List<AdminApplicationRecord> getRecentApplications() {
        return recentApplications;
    }

    public void setRecentApplications(List<AdminApplicationRecord> recentApplications) {
        this.recentApplications = recentApplications;
    }
}
