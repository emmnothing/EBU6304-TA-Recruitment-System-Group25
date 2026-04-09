package com.bupt.ta.repository;

import com.bupt.ta.model.JobApplication;
import com.bupt.ta.util.JsonFileUtil;
import com.bupt.ta.util.StoragePathUtil;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ApplicationRepository {
    private static final Type APPLICATION_LIST_TYPE = new TypeToken<List<JobApplication>>() { }.getType();
    private final Path filePath = StoragePathUtil.getJsonFilePath("applications.json");

    public List<JobApplication> findAll() {
        return JsonFileUtil.readList(filePath, APPLICATION_LIST_TYPE);
    }

    public void saveAll(List<JobApplication> applications) {
        JsonFileUtil.writeList(filePath, applications);
    }

    public List<JobApplication> findByApplicantUserId(String userId) {
        List<JobApplication> applications = new ArrayList<>();
        for (JobApplication application : findAll()) {
            if (application.getApplicantUserId().equals(userId)) {
                applications.add(application);
            }
        }
        return applications;
    }

    public List<JobApplication> findByJobId(String jobId) {
        List<JobApplication> applications = new ArrayList<>();
        for (JobApplication application : findAll()) {
            if (application.getJobId().equals(jobId)) {
                applications.add(application);
            }
        }
        return applications;
    }
}
