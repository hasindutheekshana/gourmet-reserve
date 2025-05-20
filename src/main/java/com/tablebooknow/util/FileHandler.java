package com.tablebooknow.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileHandler {

    public static void ensureFileExists(String filePath) throws IOException {
        Path path = Paths.get(filePath);

        System.out.println("Ensuring file exists at: " + path.toAbsolutePath());

        Path parent = path.getParent();
        if (parent != null && !Files.exists(parent)) {
            Files.createDirectories(parent);
            System.out.println("Created directory: " + parent.toAbsolutePath());
        }

        if (!Files.exists(path)) {
            Files.createFile(path);
            System.out.println("Created file: " + path.toAbsolutePath());
        }
    }

    public static boolean fileExists(String filePath) {
        return Files.exists(Paths.get(filePath));
    }
}
