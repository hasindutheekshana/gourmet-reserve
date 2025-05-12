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

public class ReservationDAO {
    private static final Logger logger = Logger.getLogger(ReservationDAO.class.getName());
    private static final String FILE_PATH = getDataFilePath("reservations.txt");


    private static String getDataFilePath(String fileName) {
        String dataPath = System.getProperty("app.datapath");

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

    public Reservation create(Reservation reservation) throws IOException {
        FileHandler.ensureFileExists(FILE_PATH);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(reservation.toCsvString());
            writer.newLine();
        }

        return reservation;
    }

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
                    }
                }
            }
        }

        return null;
    }


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


    public List<Reservation> findUpcomingReservations(String currentDate, String currentTime) throws IOException {
        List<Reservation> upcomingReservations = new ArrayList<>();

        if (!FileHandler.fileExists(FILE_PATH)) {
            return upcomingReservations;
        }

        try {
            LocalDate today = LocalDate.parse(currentDate);
            LocalTime now = LocalTime.parse(currentTime);

            List<Reservation> allReservations = findAll();

            for (Reservation reservation : allReservations) {
                try {
                    LocalDate reservationDate = LocalDate.parse(reservation.getReservationDate());
                    LocalTime reservationTime = LocalTime.parse(reservation.getReservationTime());

                    if (reservationDate.isAfter(today) ||
                            (reservationDate.isEqual(today) && reservationTime.isAfter(now))) {
                        upcomingReservations.add(reservation);
                    }
                } catch (Exception e) {
                    logger.warning("Error parsing date/time for reservation: " + reservation.getId());
                }
            }
        } catch (Exception e) {
            logger.warning("Error finding upcoming reservations: " + e.getMessage());
        }

        return upcomingReservations;
    }


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

    public boolean cancelReservation(String id) throws IOException {
        Reservation reservation = findById(id);
        if (reservation == null) {
            return false;
        }

        reservation.setStatus("cancelled");
        return update(reservation);
    }

    public boolean delete(String id) throws IOException {
        if (!FileHandler.fileExists(FILE_PATH)) {
            return false;
        }

        List<Reservation> reservations = findAll();
        boolean found = false;

        // Remove the reservation from the list
        for (int i = 0; i < reservations.size(); i++) {
            if (reservations.get(i).getId().equals(id)) {
                reservations.remove(i);
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

                    }
                }
            }
        }

        return reservations;
    }
}