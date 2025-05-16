package com.tablebooknow.dao;

import com.tablebooknow.util.FileHandler;
import com.tablebooknow.model.admin.Admin;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class AdminDAO {

    private static final String FILE_PATH = getDataFilePath("admins.txt");

    private static String getDataFilePath(String fileName) {
        String dataPath = System.getProperty("app.datapath");

        if (dataPath == null) {
            dataPath = System.getProperty("user.dir") + File.separator + "data";
            File dir = new File(dataPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
        }
        return dataPath + File.separator + fileName;
    }

    public Admin create(Admin admin) throws IOException {
        FileHandler.ensureFileExists(FILE_PATH);

        System.out.println("Creating admin in file: " + FILE_PATH);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(admin.toCsvString());
            writer.newLine();
        }

        return admin;
    }

    public void initializeDefaultAdmin() throws IOException {
        FileHandler.ensureFileExists(FILE_PATH);

        List<Admin> existingAdmins = findAll();
        if (!existingAdmins.isEmpty()) {
            return;
        }

        Admin defaultAdmin = new Admin();
        defaultAdmin.setUsername("admin");
        defaultAdmin.setPassword(com.tablebooknow.util.PasswordHasher.hashPassword("admin123"));
        defaultAdmin.setEmail("admin@gourmetreserve.com");
        defaultAdmin.setFullName("System Administrator");
        defaultAdmin.setRole("superadmin");

        create(defaultAdmin);

        System.out.println("Created default admin account. Username: admin, Password: admin123");
    }

    public Admin findById(String id) throws IOException {
        if (!FileHandler.fileExists(FILE_PATH)) {
            return null;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    try {
                        Admin admin = Admin.fromCsvString(line);
                        if (admin.getId().equals(id)) {
                            return admin;
                        }
                    } catch (Exception e) {
                        System.err.println("Error parsing admin line: " + line);
                    }
                }
            }
        }

        return null;
    }


    public Admin findByUsername(String username) throws IOException {
        if (!FileHandler.fileExists(FILE_PATH)) {
            return null;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    try {
                        Admin admin = Admin.fromCsvString(line);
                        if (admin.getUsername().equalsIgnoreCase(username)) {
                            return admin;
                        }
                    } catch (Exception e) {
                        System.err.println("Error parsing admin line: " + line);
                    }
                }
            }
        }

        return null;
    }


    public Admin findByEmail(String email) throws IOException {
        if (!FileHandler.fileExists(FILE_PATH)) {
            return null;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    try {
                        Admin admin = Admin.fromCsvString(line);
                        if (admin.getEmail() != null && admin.getEmail().equalsIgnoreCase(email)) {
                            return admin;
                        }
                    } catch (Exception e) {
                        System.err.println("Error parsing admin line: " + line);
                    }
                }
            }
        }

        return null;
    }



    public List<Admin> findAll() throws IOException {
        List<Admin> admins = new ArrayList<>();

        if (!FileHandler.fileExists(FILE_PATH)) {
            return admins;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    try {
                        admins.add(Admin.fromCsvString(line));
                    } catch (Exception e) {
                        System.err.println("Error parsing admin line: " + line);
                    }
                }
            }
        }

        return admins;
    }


}
