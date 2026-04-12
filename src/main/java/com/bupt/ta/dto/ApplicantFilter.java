package com.bupt.ta.dto;

public class ApplicantFilter {
    private String jobId;
    private String status;
    private String keyword;
    private String hasCv;
    private String sortBy;
    private String sortDirection;

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getHasCv() {
        return hasCv;
    }

    public void setHasCv(String hasCv) {
        this.hasCv = hasCv;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getSortDirection() {
        return sortDirection;
    }

    public void setSortDirection(String sortDirection) {
        this.sortDirection = sortDirection;
    }
}
