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
     * Find reservations by table ID.
     * @param tableId The table ID to search for
     * @return List of reservations for the specified table
     */
    public List<Reservation> findByTableId(String tableId) throws IOException {
        List<Reservation> tableReservations = new ArrayList<>();

        if (!FileHandler.fileExists(FILE_PATH)) {
            return tableReservations;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    try {
                        Reservation reservation = Reservation.fromCsvString(line);
                        if (tableId.equals(reservation.getTableId())) {
                            tableReservations.add(reservation);
                        }
                    } catch (Exception e) {
                        logger.warning("Error parsing reservation line: " + line);
                    }
                }
            }
        }

        return tableReservations;
    }

    /**
     * Get a list of tables that are reserved for a specific date and time.
     * This method checks for any reservations that would conflict with the given time slot.
     *
     * @param date The date to check
     * @param time The starting time to check
     * @param duration The duration in hours
     * @return A list of table IDs that are reserved during the specified time slot
     * @throws IOException If there's an error reading the reservation file
     */
    public List<String> getReservedTables(String date, String time, int duration) throws IOException {
        List<String> reservedTables = new ArrayList<>();

        if (!FileHandler.fileExists(FILE_PATH)) {
            return reservedTables;
        }

        try {
            // Parse the requested time
            LocalTime requestedTime = LocalTime.parse(time);
            LocalTime requestedEndTime = requestedTime.plusHours(duration);

            // Get all reservations for the date
            List<Reservation> allReservations = findAll();
            logger.info("Found " + allReservations.size() + " total reservations");

            // Filter reservations for the specified date with confirmed or pending status
            List<Reservation> dateReservations = allReservations.stream()
                    .filter(r -> date.equals(r.getReservationDate()) &&
                            (r.getStatus().equals("confirmed") || r.getStatus().equals("pending")))
                    .collect(Collectors.toList());

            logger.info("Found " + dateReservations.size() + " reservations for date " + date);

            // Check each reservation for time conflict
            for (Reservation reservation : dateReservations) {
                if (reservation.getTableId() == null || reservation.getTableId().isEmpty()) {
                    continue;
                }

                LocalTime reservationTime = LocalTime.parse(reservation.getReservationTime());
                LocalTime reservationEndTime = reservationTime.plusHours(reservation.getDuration());

                // Check if the time slots overlap
                if (reservationTime.isBefore(requestedEndTime) &&
                        requestedTime.isBefore(reservationEndTime)) {
                    // Table is reserved during requested time slot
                    logger.info("Table " + reservation.getTableId() + " is reserved at " +
                            reservationTime + " (ending at " + reservationEndTime +
                            ") which conflicts with requested time " + requestedTime +
                            " (ending at " + requestedEndTime + ")");
                    reservedTables.add(reservation.getTableId());
                }
            }

            logger.info("Returning " + reservedTables.size() + " reserved tables for date " + date +
                    " at time " + time + " for duration " + duration);
        } catch (Exception e) {
            logger.severe("Error finding reserved tables: " + e.getMessage());
            e.printStackTrace();
        }

        return reservedTables;
    }

    /**
     * Check if a table is available at a specific date and time.
     *
     * @param tableId The table ID to check
     * @param date The date to check
     * @param time The starting time to check
     * @param duration The duration in hours
     * @return true if the table is available, false otherwise
     * @throws IOException If there's an error reading the reservation file
     */
    public boolean isTableAvailable(String tableId, String date, String time, int duration) throws IOException {
        if (!FileHandler.fileExists(FILE_PATH)) {
            // If the file doesn't exist, no reservations exist, so the table is available
            return true;
        }

        try {
            // Parse the requested time
            LocalTime requestedTime = LocalTime.parse(time);
            LocalTime requestedEndTime = requestedTime.plusHours(duration);

            // Get all reservations for the specified table and date
            List<Reservation> tableReservations = findAll().stream()
                    .filter(r -> tableId.equals(r.getTableId()) &&
                            date.equals(r.getReservationDate()) &&
                            (r.getStatus().equals("confirmed") || r.getStatus().equals("pending")))
                    .collect(Collectors.toList());

            logger.info("Found " + tableReservations.size() + " reservations for table " + tableId +
                    " on date " + date);

            // Check for time conflicts
            for (Reservation reservation : tableReservations) {
                LocalTime reservationTime = LocalTime.parse(reservation.getReservationTime());
                LocalTime reservationEndTime = reservationTime.plusHours(reservation.getDuration());

                // Check if the time slots overlap
                if (reservationTime.isBefore(requestedEndTime) &&
                        requestedTime.isBefore(reservationEndTime)) {
                    // Time conflict, table is not available
                    logger.info("Table " + tableId + " is NOT available due to existing reservation: " +
                            reservation.getId() + " at time " + reservationTime);
                    return false;
                }
            }

            // No conflicts found, table is available
            logger.info("Table " + tableId + " is available at " + time + " for " + duration + " hours");
            return true;
        } catch (Exception e) {
            logger.severe("Error checking table availability: " + e.getMessage());
            e.printStackTrace();
            // If an error occurs, conservatively return false (not available)
            return false;
        }
    }