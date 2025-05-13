package com.tablebooknow.dao;

import com.tablebooknow.model.user.User;
import com.tablebooknow.util.FileHandler;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

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
}