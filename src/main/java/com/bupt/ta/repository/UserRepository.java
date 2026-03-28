package com.bupt.ta.repository;

import com.bupt.ta.model.User;
import com.bupt.ta.util.JsonFileUtil;
import com.bupt.ta.util.StoragePathUtil;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.List;

public class UserRepository {
    private static final Type USER_LIST_TYPE = new TypeToken<List<User>>() { }.getType();
    private final Path filePath = StoragePathUtil.getJsonFilePath("users.json");

    public List<User> findAll() {
        return JsonFileUtil.readList(filePath, USER_LIST_TYPE);
    }

    public void saveAll(List<User> users) {
        JsonFileUtil.writeList(filePath, users);
    }
}
