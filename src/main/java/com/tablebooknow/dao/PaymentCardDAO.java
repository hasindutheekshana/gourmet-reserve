package com.tablebooknow.dao;

import com.tablebooknow.model.payment.PaymentCard;
import com.tablebooknow.util.FileHandler;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class PaymentCardDAO {

    private static final String FILE_PATH = getDataFilePath("payment_cards.txt");


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

    public PaymentCard create(PaymentCard card) throws IOException {
        FileHandler.ensureFileExists(FILE_PATH);

        System.out.println("Creating payment card in file: " + FILE_PATH);

        card.setCreatedAt(LocalDateTime.now());
        card.setUpdatedAt(LocalDateTime.now());

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(card.toCsvString());
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
            throw e;
        }

        return card;
    }

    public PaymentCard findById(String id) throws IOException {
        if (!FileHandler.fileExists(FILE_PATH)) {
            return null;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    try {
                        PaymentCard card = PaymentCard.fromCsvString(line);
                        if (card.getId().equals(id)) {
                            return card;
                        }
                    } catch (Exception e) {
                        System.err.println("Error parsing payment card line: " + line);
                    }
                }
            }
        }
        return null;
    }

    public List<PaymentCard> findByUserId(String userId) throws IOException {
        List<PaymentCard> userCards = new ArrayList<>();

        if (!FileHandler.fileExists(FILE_PATH)) {
            return userCards;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    try {
                        PaymentCard card = PaymentCard.fromCsvString(line);
                        if (card.getUserId().equals(userId)) {
                            userCards.add(card);
                        }
                    } catch (Exception e) {
                        System.err.println("Error parsing payment card line: " + line);
                    }
                }
            }
        }

        return userCards;
    }

    public List<PaymentCard> findAll() throws IOException {
        List<PaymentCard> cards = new ArrayList<>();

        if (!FileHandler.fileExists(FILE_PATH)) {
            return cards;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    try {
                        cards.add(PaymentCard.fromCsvString(line));
                    } catch (Exception e) {
                        System.err.println("Error parsing payment card line: " + line);
                    }
                }
            }
        }

        return cards;
    }

    public PaymentCard findDefaultCard(String userId) throws IOException {
        List<PaymentCard> userCards = findByUserId(userId);

        for (PaymentCard card : userCards) {
            if (card.isDefaultCard()) {
                return card;
            }
        }

        if (!userCards.isEmpty()) {
            return userCards.get(0);
        }

        return null;
    }

    public boolean update(PaymentCard card) throws IOException {
        if (!FileHandler.fileExists(FILE_PATH)) {
            return false;
        }

        List<PaymentCard> cards = findAll();
        boolean found = false;

        card.setUpdatedAt(LocalDateTime.now());

        for (int i = 0; i < cards.size(); i++) {
            if (cards.get(i).getId().equals(card.getId())) {
                cards.set(i, card);
                found = true;
                break;
            }
        }

        if (!found) {
            return false;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (PaymentCard c : cards) {
                writer.write(c.toCsvString());
                writer.newLine();
            }
        }

        return true;
    }

    public boolean delete(String id) throws IOException {
        if (!FileHandler.fileExists(FILE_PATH)) {
            return false;
        }

        List<PaymentCard> cards = findAll();
        boolean found = false;

        for (int i = 0; i < cards.size(); i++) {
            if (cards.get(i).getId().equals(id)) {
                cards.remove(i);
                found = true;
                break;
            }
        }

        if (!found) {
            return false;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (PaymentCard c : cards) {
                writer.write(c.toCsvString());
                writer.newLine();
            }
        }
        return true;
    }
}
