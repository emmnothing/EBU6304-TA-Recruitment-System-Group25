package com.bupt.ta.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class JsonFileUtil {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private JsonFileUtil() {
    }

    public static <T> List<T> readList(Path path, Type type) {
        try {
            if (!Files.exists(path) || Files.size(path) == 0) {
                return new ArrayList<>();
            }
            String content = Files.readString(path, StandardCharsets.UTF_8);
            if (content.isBlank()) {
                return new ArrayList<>();
            }
            List<T> items = GSON.fromJson(content, type);
            return items == null ? new ArrayList<>() : items;
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to read JSON file: " + path, exception);
        }
    }

    public static void writeList(Path path, Object data) {
        try {
            Files.createDirectories(path.getParent());
            Files.writeString(path, GSON.toJson(data), StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to write JSON file: " + path, exception);
        }
    }
}
