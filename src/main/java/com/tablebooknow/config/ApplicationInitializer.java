package com.tablebooknow.config;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@WebListener
public class ApplicationInitializer implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            // Get the real path to the WEB-INF directory
            String webInfPath = sce.getServletContext().getRealPath("/WEB-INF");
            System.out.println("WEB-INF path: " + webInfPath);

            // Set up data directory path
            String dataPath = webInfPath + File.separator + "data";
            System.out.println("Setting data directory to: " + dataPath);

            // Ensure the directory exists
            File dataDir = new File(dataPath);
            if (!dataDir.exists()) {
                boolean created = dataDir.mkdirs();
                System.out.println("Created data directory: " + created);
            } else {
                System.out.println("Data directory already exists");
            }

            // Set as a system property so it can be accessed from anywhere
            System.setProperty("app.datapath", dataPath);

            // Print all system properties for debugging
            System.out.println("app.datapath property: " + System.getProperty("app.datapath"));

            // Create sample files if they don't exist
            ensureFileExists(dataPath + File.separator + "users.txt");
            ensureFileExists(dataPath + File.separator + "reservations.txt");

            System.out.println("Application initialization completed successfully");
        } catch (Exception e) {
            System.err.println("Failed to initialize data directory: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void ensureFileExists(String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                boolean created = file.createNewFile();
                System.out.println("Created file " + filePath + ": " + created);
            } else {
                System.out.println("File already exists: " + filePath);
            }
        } catch (IOException e) {
            System.err.println("Error creating file " + filePath + ": " + e.getMessage());
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Clean up if needed
    }
}