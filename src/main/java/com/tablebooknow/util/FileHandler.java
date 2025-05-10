package com.tablebooknow.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Utility class for handling file operations.
 */
public class FileHandler {

    /**
     * Ensures that a file exists. If the file doesn't exist, it creates the file
     * and any necessary parent directories.
     *
     * @param filePath The path to the file
     * @throws IOException If there's an error creating the file
     */
    public static void ensureFileExists(String filePath) throws IOException {
        Path path = Paths.get(filePath);

        System.out.println("Ensuring file exists at: " + path.toAbsolutePath());

        // Create parent directories if they don't exist
        Path parent = path.getParent();
        if (parent != null && !Files.exists(parent)) {
            Files.createDirectories(parent);
            System.out.println("Created directory: " + parent.toAbsolutePath());
        }

        // Create the file if it doesn't exist
        if (!Files.exists(path)) {
            Files.createFile(path);
            System.out.println("Created file: " + path.toAbsolutePath());
        }
    }

    /**
     * Checks if a file exists.
     *
     * @param filePath The path to the file
     * @return true if the file exists, false otherwise
     */
    public static boolean fileExists(String filePath) {
        return Files.exists(Paths.get(filePath));
    }

    /**
     * Creates a backup of a file by copying it with a timestamp suffix.
     *
     * @param filePath The path to the file to backup
     * @throws IOException If there's an error creating the backup
     */
    public static void createBackup(String filePath) throws IOException {
        if (!fileExists(filePath)) {
            return; // Nothing to backup
        }

        Path source = Paths.get(filePath);
        String timestamp = String.valueOf(System.currentTimeMillis());
        Path target = Paths.get(filePath + "." + timestamp + ".bak");

        Files.copy(source, target);
        System.out.println("Created backup at: " + target.toAbsolutePath());
    }

    /**
     * Deletes a file if it exists.
     *
     * @param filePath The path to the file to delete
     * @return true if the file was deleted, false if it didn't exist
     * @throws IOException If there's an error deleting the file
     */
    public static boolean deleteFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        if (Files.exists(path)) {
            Files.delete(path);
            System.out.println("Deleted file: " + path.toAbsolutePath());
            return true;
        }
        return false;
    }
}