package com.bupt.ta.dto;

public class JobFilter {
    private String keyword;
    private String moduleCode;
    private String status;
    private String locationMode;
    private String maxWeeklyHours;
    private String minRemainingVacancies;
    private String sortBy;
    private String sortDirection;

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getModuleCode() {
        return moduleCode;
    }

    public void setModuleCode(String moduleCode) {
        this.moduleCode = moduleCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLocationMode() {
        return locationMode;
    }

    public void setLocationMode(String locationMode) {
        this.locationMode = locationMode;
    }

    public String getMaxWeeklyHours() {
        return maxWeeklyHours;
    }

    public void setMaxWeeklyHours(String maxWeeklyHours) {
        this.maxWeeklyHours = maxWeeklyHours;
    }

    public String getMinRemainingVacancies() {
        return minRemainingVacancies;
    }

    public void setMinRemainingVacancies(String minRemainingVacancies) {
        this.minRemainingVacancies = minRemainingVacancies;
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
