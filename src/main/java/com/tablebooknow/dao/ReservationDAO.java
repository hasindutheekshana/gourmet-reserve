package com.tablebooknow.dao;

import com.tablebooknow.model.reservation.Reservation;
import com.tablebooknow.util.FileHandler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Data Access Object for Reservation entities to handle file-based storage operations.
 */
public class ReservationDAO {
    private static final Logger logger = Logger.getLogger(ReservationDAO.class.getName());
    private static final String FILE_PATH = getDataFilePath("reservations.txt");

    /**
     * Gets the path to a data file, using the application's data directory.
     */
    private static String getDataFilePath(String fileName) {
        String dataPath = System.getProperty("app.datapath");

        // Fallback to user.dir/data if app.datapath is not set
        if (dataPath == null) {
            dataPath = System.getProperty("user.dir") + File.separator + "data";
            // Ensure the directory exists
            File dir = new File(dataPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
        }

        return dataPath + File.separator + fileName;
    }

    /**
     * Creates a new reservation by appending to the reservations file.
     * @param reservation The reservation to create
     * @return The created reservation with assigned ID
     */
    public Reservation create(Reservation reservation) throws IOException {
        // Make sure the file exists
        FileHandler.ensureFileExists(FILE_PATH);

        // Append reservation to the file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(reservation.toCsvString());
            writer.newLine();
        }

        return reservation;
    }

    /**
     * Finds a reservation by its ID.
     * @param id The ID to search for
     * @return The reservation or null if not found
     */
    public Reservation findById(String id) throws IOException {
        if (!FileHandler.fileExists(FILE_PATH)) {
            return null;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    try {
                        Reservation reservation = Reservation.fromCsvString(line);
                        if (reservation.getId().equals(id)) {
                            return reservation;
                        }
                    } catch (Exception e) {
                        logger.warning("Error parsing reservation line: " + line);
                        // Continue to next line on error
                    }
                }
            }
        }

        return null;
    }

    /**
     * Find reservations by user ID.
     * @param userId The user ID to search for
     * @return List of reservations for the specified user
     */
    public List<Reservation> findByUserId(String userId) throws IOException {
        List<Reservation> userReservations = new ArrayList<>();

        if (!FileHandler.fileExists(FILE_PATH)) {
            return userReservations;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    try {
                        Reservation reservation = Reservation.fromCsvString(line);
                        if (reservation.getUserId().equals(userId)) {
                            userReservations.add(reservation);
                        }
                    } catch (Exception e) {
                        logger.warning("Error parsing reservation line: " + line);
                    }
                }
            }
        }

        return userReservations;
    }

    /**
     * Updates a reservation's information.
     * @param reservation The reservation to update
     * @return true if successful, false otherwise
     */
    public boolean update(Reservation reservation) throws IOException {
        if (!FileHandler.fileExists(FILE_PATH)) {
            return false;
        }

        List<Reservation> reservations = findAll();
        boolean found = false;

        // Replace the reservation in the list
        for (int i = 0; i < reservations.size(); i++) {
            if (reservations.get(i).getId().equals(reservation.getId())) {
                reservations.set(i, reservation);
                found = true;
                break;
            }
        }

        if (!found) {
            return false;
        }

        // Write all reservations back to the file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Reservation r : reservations) {
                writer.write(r.toCsvString());
                writer.newLine();
            }
        }

        return true;
    }

    /**
     * Gets all reservations from the file.
     * @return List of all reservations
     */
    public List<Reservation> findAll() throws IOException {
        List<Reservation> reservations = new ArrayList<>();

        if (!FileHandler.fileExists(FILE_PATH)) {
            return reservations;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    try {
                        reservations.add(Reservation.fromCsvString(line));
                    } catch (Exception e) {
                        logger.warning("Error parsing reservation line: " + line);
                        // Continue to next line on error
                    }
                }
            }
        }

        return reservations;
    }
}