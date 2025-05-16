package com.tablebooknow.dao;

import com.tablebooknow.model.review.Review;
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
import java.util.logging.Logger;


public class ReviewDAO {
    private static final Logger logger = Logger.getLogger(ReviewDAO.class.getName());
    private static final String FILE_PATH = getDataFilePath("reviews.txt");

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


    public Review create(Review review) throws IOException {
        FileHandler.ensureFileExists(FILE_PATH);

        logger.info("Creating review in file: " + FILE_PATH);

        review.setCreatedAt(LocalDateTime.now());
        review.setUpdatedAt(LocalDateTime.now());

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(review.toCsvString());
            writer.newLine();
        } catch (IOException e) {
            logger.severe("Error writing to file: " + e.getMessage());
            throw e;
        }

        return review;
    }


    public Review findById(String id) throws IOException {
        if (!FileHandler.fileExists(FILE_PATH)) {
            return null;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    try {
                        Review review = Review.fromCsvString(line);
                        if (review.getId().equals(id)) {
                            return review;
                        }
                    } catch (Exception e) {
                        logger.warning("Error parsing review line: " + line);
                    }
                }
            }
        }

        return null;
    }

    public List<Review> findByUserId(String userId) throws IOException {
        List<Review> userReviews = new ArrayList<>();

        if (!FileHandler.fileExists(FILE_PATH)) {
            return userReviews;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    try {
                        Review review = Review.fromCsvString(line);
                        if (review.getUserId().equals(userId)) {
                            userReviews.add(review);
                        }
                    } catch (Exception e) {
                        logger.warning("Error parsing review line: " + line);
                    }
                }
            }
        }

        return userReviews;
    }

    public List<Review> findByReservationId(String reservationId) throws IOException {
        List<Review> reservationReviews = new ArrayList<>();

        if (!FileHandler.fileExists(FILE_PATH)) {
            return reservationReviews;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    try {
                        Review review = Review.fromCsvString(line);
                        if (reservationId.equals(review.getReservationId())) {
                            reservationReviews.add(review);
                        }
                    } catch (Exception e) {
                        logger.warning("Error parsing review line: " + line);
                    }
                }
            }
        }

        return reservationReviews;
    }


    public boolean hasReview(String reservationId, String userId) throws IOException {
        if (!FileHandler.fileExists(FILE_PATH)) {
            return false;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    try {
                        Review review = Review.fromCsvString(line);
                        if (review.getReservationId().equals(reservationId) &&
                                review.getUserId().equals(userId)) {
                            return true;
                        }
                    } catch (Exception e) {
                        logger.warning("Error parsing review line: " + line);
                    }
                }
            }
        }

        return false;
    }


    public boolean update(Review review) throws IOException {
        if (!FileHandler.fileExists(FILE_PATH)) {
            return false;
        }

        List<Review> reviews = findAll();
        boolean found = false;

        review.setUpdatedAt(LocalDateTime.now());

        for (int i = 0; i < reviews.size(); i++) {
            if (reviews.get(i).getId().equals(review.getId())) {
                reviews.set(i, review);
                found = true;
                break;
            }
        }

        if (!found) {
            return false;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Review r : reviews) {
                writer.write(r.toCsvString());
                writer.newLine();
            }
        }

        return true;
    }

    public boolean delete(String id) throws IOException {
        if (!FileHandler.fileExists(FILE_PATH)) {
            return false;
        }

        List<Review> reviews = findAll();
        boolean found = false;

        for (int i = 0; i < reviews.size(); i++) {
            if (reviews.get(i).getId().equals(id)) {
                reviews.remove(i);
                found = true;
                break;
            }
        }

        if (!found) {
            return false;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Review r : reviews) {
                writer.write(r.toCsvString());
                writer.newLine();
            }
        }

        return true;
    }

    public List<Review> findAll() throws IOException {
        List<Review> reviews = new ArrayList<>();

        if (!FileHandler.fileExists(FILE_PATH)) {
            return reviews;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    try {
                        reviews.add(Review.fromCsvString(line));
                    } catch (Exception e) {
                        logger.warning("Error parsing review line: " + line);
                    }
                }
            }
        }

        return reviews;
    }

    public double getAverageRating() throws IOException {
        List<Review> reviews = findAll();

        if (reviews.isEmpty()) {
            return 0;
        }

        int sum = 0;
        for (Review review : reviews) {
            sum += review.getRating();
        }

        return (double) sum / reviews.size();
    }
}

