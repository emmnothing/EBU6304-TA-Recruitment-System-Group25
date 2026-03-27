package com.bupt.ta.repository;

import com.bupt.ta.model.JobPost;
import com.bupt.ta.util.JsonFileUtil;
import com.bupt.ta.util.StoragePathUtil;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.List;

public class JobRepository {
    private static final Type JOB_LIST_TYPE = new TypeToken<List<JobPost>>() { }.getType();
    private final Path filePath = StoragePathUtil.getJsonFilePath("jobs.json");

    public List<JobPost> findAll() {
        return JsonFileUtil.readList(filePath, JOB_LIST_TYPE);
    }

    public void saveAll(List<JobPost> jobs) {
        JsonFileUtil.writeList(filePath, jobs);
    }
}
