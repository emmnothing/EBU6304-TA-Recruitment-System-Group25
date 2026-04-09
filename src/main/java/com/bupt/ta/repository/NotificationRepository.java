package com.bupt.ta.repository;

import com.bupt.ta.model.Notification;
import com.bupt.ta.util.JsonFileUtil;
import com.bupt.ta.util.StoragePathUtil;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.List;

public class NotificationRepository {
    private static final Type NOTIFICATION_LIST_TYPE = new TypeToken<List<Notification>>() { }.getType();
    private final Path filePath = StoragePathUtil.getJsonFilePath("notifications.json");

    public List<Notification> findAll() {
        return JsonFileUtil.readList(filePath, NOTIFICATION_LIST_TYPE);
    }

    public void saveAll(List<Notification> notifications) {
        JsonFileUtil.writeList(filePath, notifications);
    }
}
