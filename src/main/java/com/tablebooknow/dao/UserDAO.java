package com.tablebooknow.dao;

import com.tablebooknow.model.user.User;
import com.tablebooknow.util.FileHandler;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    private static final String FILE_PATH = getDataFilePath("users.txt");

    private static String getDataFilePath(String fileName) {
        String dataPath = System.getProperty("app.datapath");


        if (dataPath == null) {
            dataPath = System.getProperty("user.dir") + File.separator + "data";
        }

        return dataPath + File.separator + fileName;
    }

    public User create(User user) throws IOException {

        FileHandler.ensureFileExists(FILE_PATH);


        System.out.println("Creating user in file: " + FILE_PATH);


        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(user.toCsvString());
            writer.newLine();
        }

        return user;
    }

    public User findById(String id) throws IOException {
        if (!FileHandler.fileExists(FILE_PATH)) {
            return null;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                User user = User.fromCsvString(line);
                if (user.getId().equals(id)) {
                    return user;
                }
            }
        }

        return null;
    }


    public User findByUsername(String username) throws IOException {
        if (!FileHandler.fileExists(FILE_PATH)) {
            return null;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                User user = User.fromCsvString(line);
                if (user.getUsername().equalsIgnoreCase(username)) {
                    return user;
                }
            }
        }

        return null;
    }

    public User findByEmail(String email) throws IOException {
        if (!FileHandler.fileExists(FILE_PATH)) {
            return null;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                User user = User.fromCsvString(line);
                if (user.getEmail() != null && user.getEmail().equalsIgnoreCase(email)) {
                    return user;
                }
            }
        }

        return null;
    }

    public List<User> findAll() throws IOException {
        List<User> users = new ArrayList<>();

        if (!FileHandler.fileExists(FILE_PATH)) {
            return users;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    users.add(User.fromCsvString(line));
                }
            }
        }

        return users;
    }

    public boolean delete(String id) throws IOException {
        if (!FileHandler.fileExists(FILE_PATH)) {
            return false;
        }

        List<User> users = findAll();
        boolean found = false;


        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId().equals(id)) {
                users.remove(i);
                found = true;
                break;
            }
        }

        if (!found) {
            return false;
        }


        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (User u : users) {
                writer.write(u.toCsvString());
                writer.newLine();
            }
        }

        return true;
    }


    public boolean update(User user) throws IOException {
        if (!FileHandler.fileExists(FILE_PATH)) {
            return false;
        }

        List<User> users = findAll();
        boolean found = false;


        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId().equals(user.getId())) {
                users.set(i, user);
                found = true;
                break;
            }
        }

        if (!found) {
            return false;
        }


        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (User u : users) {
                writer.write(u.toCsvString());
                writer.newLine();
            }
        }

        return true;
    }
}

