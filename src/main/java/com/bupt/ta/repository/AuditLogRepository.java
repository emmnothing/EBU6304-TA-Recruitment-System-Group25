package com.bupt.ta.repository;

import com.bupt.ta.model.AuditLogEntry;
import com.bupt.ta.util.JsonFileUtil;
import com.bupt.ta.util.StoragePathUtil;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.List;

public class AuditLogRepository {
    private static final Type AUDIT_LOG_LIST_TYPE = new TypeToken<List<AuditLogEntry>>() { }.getType();

    /*
     * Audit writes are append-only from the application point of view. A single
     * process-level lock avoids lost updates when two admin requests append to
     * the JSON file at the same time.
     */
    private static final Object WRITE_LOCK = new Object();

    private final Path filePath = StoragePathUtil.getJsonFilePath("auditLogs.json");

    public List<AuditLogEntry> findAll() {
        return JsonFileUtil.readList(filePath, AUDIT_LOG_LIST_TYPE);
    }

    public void append(AuditLogEntry auditLogEntry) {
        synchronized (WRITE_LOCK) {
            List<AuditLogEntry> auditLogEntries = findAll();
            auditLogEntries.add(auditLogEntry);
            JsonFileUtil.writeList(filePath, auditLogEntries);
        }
    }
}
