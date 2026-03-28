package com.bupt.ta.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class StoragePathUtil {
    private StoragePathUtil() {
    }

    public static Path getStorageRoot() {
        String customRoot = System.getProperty("ta.storage.root");
        Path root = customRoot == null || customRoot.isBlank()
            ? Paths.get(System.getProperty("user.dir"), "storage")
            : Paths.get(customRoot);

        ensureDirectory(root);
        ensureDirectory(root.resolve("json"));
        ensureDirectory(root.resolve("uploads"));
        ensureDirectory(root.resolve("uploads").resolve("cv"));
        return root;
    }

    public static Path getJsonFilePath(String fileName) {
        return getStorageRoot().resolve("json").resolve(fileName);
    }

    public static Path getCvUploadDirectory() {
        return getStorageRoot().resolve("uploads").resolve("cv");
    }

    private static void ensureDirectory(Path directory) {
        try {
            Files.createDirectories(directory);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to create directory: " + directory, exception);
        }
    }
}
