package com.tablebooknow.dao;

import com.tablebooknow.model.payment.Payment;
import com.tablebooknow.util.FileHandler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PaymentDAO {

    private static final String FILE_PATH = getDataFilePath("payments.txt");

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


    public Payment create(Payment payment) throws IOException {
        FileHandler.ensureFileExists(FILE_PATH);

        System.out.println("Creating payment record in file: " + FILE_PATH);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(payment.toCsvString());
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
            throw e;
        }

        return payment;
    }

    public Payment findById(String id) throws IOException {
        if (!FileHandler.fileExists(FILE_PATH)) {
            return null;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    try {
                        Payment payment = Payment.fromCsvString(line);
                        if (payment.getId().equals(id)) {
                            return payment;
                        }
                    } catch (Exception e) {
                        System.err.println("Error parsing payment line: " + line);
                    }
                }
            }
        }

        return null;
    }

    public List<Payment> findAll() throws IOException {
        List<Payment> payments = new ArrayList<>();

        if (!FileHandler.fileExists(FILE_PATH)) {
            return payments;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    try {
                        payments.add(Payment.fromCsvString(line));
                    } catch (Exception e) {
                        System.err.println("Error parsing payment line: " + line);
                    }
                }
            }
        }

        return payments;
    }


}
