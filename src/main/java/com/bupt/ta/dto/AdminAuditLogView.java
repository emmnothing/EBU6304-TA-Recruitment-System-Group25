package com.bupt.ta.dto;

import com.bupt.ta.model.AuditLogEntry;

import java.util.ArrayList;
import java.util.List;

public class AdminAuditLogView {
    private int totalLogs;
    private int successfulLogs;
    private int failedLogs;
    private int exportLogs;
    private int accountChangeLogs;
    private int displayedLogs;
    private List<AuditLogEntry> auditLogs = new ArrayList<>();

    public int getTotalLogs() {
        return totalLogs;
    }

    public void setTotalLogs(int totalLogs) {
        this.totalLogs = totalLogs;
    }

    public int getSuccessfulLogs() {
        return successfulLogs;
    }

    public void setSuccessfulLogs(int successfulLogs) {
        this.successfulLogs = successfulLogs;
    }

    public int getFailedLogs() {
        return failedLogs;
    }

    public void setFailedLogs(int failedLogs) {
        this.failedLogs = failedLogs;
    }

    public int getExportLogs() {
        return exportLogs;
    }

    public void setExportLogs(int exportLogs) {
        this.exportLogs = exportLogs;
    }

    public int getAccountChangeLogs() {
        return accountChangeLogs;
    }

    public void setAccountChangeLogs(int accountChangeLogs) {
        this.accountChangeLogs = accountChangeLogs;
    }

    public int getDisplayedLogs() {
        return displayedLogs;
    }

    public void setDisplayedLogs(int displayedLogs) {
        this.displayedLogs = displayedLogs;
    }

    public List<AuditLogEntry> getAuditLogs() {
        return auditLogs;
    }

    public void setAuditLogs(List<AuditLogEntry> auditLogs) {
        this.auditLogs = auditLogs;
    }
}
