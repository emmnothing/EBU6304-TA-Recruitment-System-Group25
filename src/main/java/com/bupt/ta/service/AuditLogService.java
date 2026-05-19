package com.bupt.ta.service;

import com.bupt.ta.dto.AdminAuditLogFilter;
import com.bupt.ta.dto.AdminAuditLogView;
import com.bupt.ta.model.AuditLogEntry;
import com.bupt.ta.model.Role;
import com.bupt.ta.repository.AuditLogRepository;
import com.bupt.ta.util.IdGenerator;
import com.bupt.ta.util.SessionUtil;
import com.bupt.ta.util.ValidationUtil;
import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AuditLogService {
    private static final Logger LOGGER = Logger.getLogger(AuditLogService.class.getName());

    public static final String ACTION_USER_STATUS_CHANGED = "USER_STATUS_CHANGED";
    public static final String ACTION_PASSWORD_RESET = "PASSWORD_RESET";
    public static final String ACTION_SYSTEM_ANNOUNCEMENT_SENT = "SYSTEM_ANNOUNCEMENT_SENT";
    public static final String ACTION_CSV_EXPORT_DOWNLOADED = "CSV_EXPORT_DOWNLOADED";
    public static final String ACTION_CSV_EXPORT_REJECTED = "CSV_EXPORT_REJECTED";

    public static final String TARGET_USER = "USER";
    public static final String TARGET_ANNOUNCEMENT = "ANNOUNCEMENT";
    public static final String TARGET_EXPORT = "EXPORT";

    public static final String OUTCOME_SUCCESS = "SUCCESS";
    public static final String OUTCOME_FAILURE = "FAILURE";

    private static final int DISPLAY_LIMIT = 100;
    private static final int MAX_DETAIL_LENGTH = 500;
    private static final int MAX_USER_AGENT_LENGTH = 180;

    private final AuditLogRepository auditLogRepository = new AuditLogRepository();

    public AdminAuditLogView getAuditLogView(AdminAuditLogFilter filter) {
        List<AuditLogEntry> auditLogEntries = auditLogRepository.findAll();
        AdminAuditLogView view = new AdminAuditLogView();
        view.setTotalLogs(auditLogEntries.size());

        List<AuditLogEntry> filteredEntries = new ArrayList<>();
        for (AuditLogEntry auditLogEntry : auditLogEntries) {
            collectSummary(view, auditLogEntry);
            if (matchesFilter(auditLogEntry, filter)) {
                filteredEntries.add(auditLogEntry);
            }
        }

        filteredEntries.sort(buildCreatedAtComparator(filter));
        if (filteredEntries.size() > DISPLAY_LIMIT) {
            filteredEntries = new ArrayList<>(filteredEntries.subList(0, DISPLAY_LIMIT));
        }

        view.setDisplayedLogs(filteredEntries.size());
        view.setAuditLogs(filteredEntries);
        return view;
    }

    public void recordAdminAction(
        HttpServletRequest request,
        String action,
        String targetType,
        String targetId,
        String targetLabel,
        boolean success,
        String detail
    ) {
        /*
         * Most callers log after their business JSON write has already
         * completed. Because this demo app has no transaction manager, audit
         * persistence must not turn a completed admin operation into an error
         * page; failures are isolated to the server log for diagnosis.
         */
        try {
            AuditLogEntry auditLogEntry = new AuditLogEntry();
            auditLogEntry.setAuditLogId(IdGenerator.generateId("audit"));
            auditLogEntry.setActorUserId(request == null ? "" : SessionUtil.getCurrentUserId(request));
            auditLogEntry.setActorUsername(request == null ? "" : SessionUtil.getCurrentUsername(request));

            Role actorRole = request == null ? null : SessionUtil.getCurrentUserRole(request);
            auditLogEntry.setActorRole(actorRole == null ? "" : actorRole.name());
            auditLogEntry.setAction(action);
            auditLogEntry.setTargetType(targetType);
            auditLogEntry.setTargetId(targetId);
            auditLogEntry.setTargetLabel(targetLabel);
            auditLogEntry.setOutcome(success ? OUTCOME_SUCCESS : OUTCOME_FAILURE);
            auditLogEntry.setDetail(limit(detail, MAX_DETAIL_LENGTH));
            auditLogEntry.setRequestMethod(request == null ? "" : request.getMethod());
            auditLogEntry.setRequestPath(request == null ? "" : request.getRequestURI());
            auditLogEntry.setIpAddress(resolveClientIp(request));
            auditLogEntry.setUserAgent(limit(request == null ? "" : request.getHeader("User-Agent"), MAX_USER_AGENT_LENGTH));
            auditLogEntry.setCreatedAt(LocalDateTime.now().toString());
            auditLogRepository.append(auditLogEntry);
        } catch (RuntimeException exception) {
            LOGGER.log(Level.SEVERE, "Unable to write administrator audit log entry.", exception);
        }
    }

    private void collectSummary(AdminAuditLogView view, AuditLogEntry auditLogEntry) {
        if (OUTCOME_SUCCESS.equals(auditLogEntry.getOutcome())) {
            view.setSuccessfulLogs(view.getSuccessfulLogs() + 1);
        } else if (OUTCOME_FAILURE.equals(auditLogEntry.getOutcome())) {
            view.setFailedLogs(view.getFailedLogs() + 1);
        }

        if (ACTION_CSV_EXPORT_DOWNLOADED.equals(auditLogEntry.getAction())
            || ACTION_CSV_EXPORT_REJECTED.equals(auditLogEntry.getAction())) {
            view.setExportLogs(view.getExportLogs() + 1);
        }
        if (ACTION_USER_STATUS_CHANGED.equals(auditLogEntry.getAction())
            || ACTION_PASSWORD_RESET.equals(auditLogEntry.getAction())) {
            view.setAccountChangeLogs(view.getAccountChangeLogs() + 1);
        }
    }

    private boolean matchesFilter(AuditLogEntry auditLogEntry, AdminAuditLogFilter filter) {
        if (filter == null) {
            return true;
        }
        if (!ValidationUtil.isBlank(filter.getAction())
            && !safeEquals(auditLogEntry.getAction(), filter.getAction())) {
            return false;
        }
        if (!ValidationUtil.isBlank(filter.getOutcome())
            && !safeEquals(auditLogEntry.getOutcome(), filter.getOutcome())) {
            return false;
        }
        if (!ValidationUtil.isBlank(filter.getKeyword())) {
            String keyword = filter.getKeyword().trim().toLowerCase(Locale.ROOT);
            return containsIgnoreCase(auditLogEntry.getAuditLogId(), keyword)
                || containsIgnoreCase(auditLogEntry.getActorUserId(), keyword)
                || containsIgnoreCase(auditLogEntry.getActorUsername(), keyword)
                || containsIgnoreCase(auditLogEntry.getAction(), keyword)
                || containsIgnoreCase(auditLogEntry.getTargetType(), keyword)
                || containsIgnoreCase(auditLogEntry.getTargetId(), keyword)
                || containsIgnoreCase(auditLogEntry.getTargetLabel(), keyword)
                || containsIgnoreCase(auditLogEntry.getDetail(), keyword)
                || containsIgnoreCase(auditLogEntry.getRequestPath(), keyword)
                || containsIgnoreCase(auditLogEntry.getIpAddress(), keyword);
        }
        return true;
    }

    private Comparator<AuditLogEntry> buildCreatedAtComparator(AdminAuditLogFilter filter) {
        Comparator<AuditLogEntry> comparator = Comparator.comparing(
            AuditLogEntry::getCreatedAt,
            Comparator.nullsLast(String::compareTo)
        );
        String direction = filter == null ? null : filter.getSortDirection();
        if (!"asc".equalsIgnoreCase(direction)) {
            comparator = comparator.reversed();
        }
        return comparator;
    }

    private String resolveClientIp(HttpServletRequest request) {
        if (request == null) {
            return "";
        }

        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (!ValidationUtil.isBlank(forwardedFor)) {
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private boolean safeEquals(String first, String second) {
        return first != null && second != null && first.equalsIgnoreCase(second.trim());
    }

    private boolean containsIgnoreCase(String source, String keyword) {
        return source != null && source.toLowerCase(Locale.ROOT).contains(keyword);
    }

    private String limit(String value, int maxLength) {
        if (value == null) {
            return "";
        }
        if (value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength - 3) + "...";
    }
}
