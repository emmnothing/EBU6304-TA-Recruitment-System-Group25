package com.bupt.ta.repository;

import com.bupt.ta.model.ApplicantProfile;
import com.bupt.ta.util.JsonFileUtil;
import com.bupt.ta.util.StoragePathUtil;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.List;

public class ApplicantProfileRepository {
    private static final Type PROFILE_LIST_TYPE = new TypeToken<List<ApplicantProfile>>() { }.getType();
    private final Path filePath = StoragePathUtil.getJsonFilePath("applicantProfiles.json");

    public List<ApplicantProfile> findAll() {
        return JsonFileUtil.readList(filePath, PROFILE_LIST_TYPE);
    }

    public void saveAll(List<ApplicantProfile> profiles) {
        JsonFileUtil.writeList(filePath, profiles);
    }
}
