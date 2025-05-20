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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;


public class ReservationDAO {
    private static final Logger logger = Logger.getLogger(ReservationDAO.class.getName());
    private static final String FILE_PATH = getDataFilePath("reservations.txt");

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

    public List<String> getReservedTables(String date, String time, int duration) throws IOException {
        List<String> reservedTables = new ArrayList<>();

        if (!FileHandler.fileExists(FILE_PATH)) {
            return reservedTables;
        }

        try {

            LocalTime requestedTime = LocalTime.parse(time);
            LocalTime requestedEndTime = requestedTime.plusHours(duration);

            List<Reservation> allReservations = findAll();
            logger.info("Found " + allReservations.size() + " total reservations");

            List<Reservation> dateReservations = allReservations.stream()
                    .filter(r -> date.equals(r.getReservationDate()) &&
                            (r.getStatus().equals("confirmed") || r.getStatus().equals("pending")))
                    .collect(Collectors.toList());

            logger.info("Found " + dateReservations.size() + " reservations for date " + date);

            for (Reservation reservation : dateReservations) {
                if (reservation.getTableId() == null || reservation.getTableId().isEmpty()) {
                    continue;
                }

                LocalTime reservationTime = LocalTime.parse(reservation.getReservationTime());
                LocalTime reservationEndTime = reservationTime.plusHours(reservation.getDuration());

                if (reservationTime.isBefore(requestedEndTime) &&
                        requestedTime.isBefore(reservationEndTime)) {

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

    public boolean isTableAvailable(String tableId, String date, String time, int duration) throws IOException {
        if (!FileHandler.fileExists(FILE_PATH)) {

            return true;
        }

        try {

            LocalTime requestedTime = LocalTime.parse(time);
            LocalTime requestedEndTime = requestedTime.plusHours(duration);

            List<Reservation> tableReservations = findAll().stream()
                    .filter(r -> tableId.equals(r.getTableId()) &&
                            date.equals(r.getReservationDate()) &&
                            (r.getStatus().equals("confirmed") || r.getStatus().equals("pending")))
                    .collect(Collectors.toList());

            logger.info("Found " + tableReservations.size() + " reservations for table " + tableId +
                    " on date " + date);

            for (Reservation reservation : tableReservations) {
                LocalTime reservationTime = LocalTime.parse(reservation.getReservationTime());
                LocalTime reservationEndTime = reservationTime.plusHours(reservation.getDuration());

                if (reservationTime.isBefore(requestedEndTime) &&
                        requestedTime.isBefore(reservationEndTime)) {

                    logger.info("Table " + tableId + " is NOT available due to existing reservation: " +
                            reservation.getId() + " at time " + reservationTime);
                    return false;
                }
            }

            logger.info("Table " + tableId + " is available at " + time + " for " + duration + " hours");
            return true;
        } catch (Exception e) {
            logger.severe("Error checking table availability: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(Reservation reservation) throws IOException {
        if (!FileHandler.fileExists(FILE_PATH)) {
            return false;
        }

        List<Reservation> reservations = findAll();
        boolean found = false;

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

    public List<Reservation> findByStatus(String status) throws IOException {
        List<Reservation> statusReservations = new ArrayList<>();

        if (!FileHandler.fileExists(FILE_PATH)) {
            return statusReservations;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    try {
                        Reservation reservation = Reservation.fromCsvString(line);
                        if (status.equals(reservation.getStatus())) {
                            statusReservations.add(reservation);
                        }
                    } catch (Exception e) {
                        logger.warning("Error parsing reservation line: " + line);
                    }
                }
            }
        }

        return statusReservations;
    }

    public List<Reservation> findByDateRange(String startDate, String endDate) throws IOException {
        List<Reservation> dateRangeReservations = new ArrayList<>();

        if (!FileHandler.fileExists(FILE_PATH)) {
            return dateRangeReservations;
        }

        try {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);

            List<Reservation> allReservations = findAll();

            for (Reservation reservation : allReservations) {
                try {
                    LocalDate reservationDate = LocalDate.parse(reservation.getReservationDate());
                    if ((reservationDate.isEqual(start) || reservationDate.isAfter(start)) &&
                            (reservationDate.isEqual(end) || reservationDate.isBefore(end))) {
                        dateRangeReservations.add(reservation);
                    }
                } catch (Exception e) {
                    logger.warning("Error parsing date for reservation: " + reservation.getId());
                }
            }
        } catch (Exception e) {
            logger.warning("Error finding reservations by date range: " + e.getMessage());
        }

        return dateRangeReservations;
    }

    public Map<String, Map<String, String>> getActiveTablesAt(String date, String time) throws IOException {
        Map<String, Map<String, String>> activeTablesMap = new HashMap<>();

        List<Reservation> allReservations = findAll();

        List<Reservation> activeReservations = allReservations.stream()
                .filter(r -> r.getReservationDate().equals(date) &&
                        (r.getStatus().equals("confirmed") || r.getStatus().equals("pending")))
                .collect(Collectors.toList());

        LocalTime targetTime = LocalTime.parse(time);

        for (Reservation res : activeReservations) {
            LocalTime resStartTime = LocalTime.parse(res.getReservationTime());
            LocalTime resEndTime = resStartTime.plusHours(res.getDuration());

            if (targetTime.isAfter(resStartTime) && targetTime.isBefore(resEndTime)) {

                Map<String, String> resInfo = new HashMap<>();
                resInfo.put("reservationId", res.getId());
                resInfo.put("startTime", res.getReservationTime());
                resInfo.put("endTime", resEndTime.toString());
                resInfo.put("status", res.getStatus());
                resInfo.put("userId", res.getUserId());

                activeTablesMap.put(res.getTableId(), resInfo);
            }
        }

        return activeTablesMap;
    }

    public List<Reservation> searchReservations(String userId, String tableId, String status, String date, int page, int pageSize) throws IOException {
        List<Reservation> allReservations = findAll();
        List<Reservation> filteredReservations = new ArrayList<>(allReservations);

        if (userId != null && !userId.isEmpty()) {
            filteredReservations = filteredReservations.stream()
                    .filter(r -> r.getUserId().equals(userId))
                    .collect(Collectors.toList());
        }

        if (tableId != null && !tableId.isEmpty()) {
            filteredReservations = filteredReservations.stream()
                    .filter(r -> r.getTableId() != null && r.getTableId().equals(tableId))
                    .collect(Collectors.toList());
        }

        if (status != null && !status.isEmpty()) {
            filteredReservations = filteredReservations.stream()
                    .filter(r -> r.getStatus().equals(status))
                    .collect(Collectors.toList());
        }

        if (date != null && !date.isEmpty()) {
            filteredReservations = filteredReservations.stream()
                    .filter(r -> r.getReservationDate().equals(date))
                    .collect(Collectors.toList());
        }

        int startIndex = (page - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, filteredReservations.size());

        if (startIndex >= filteredReservations.size()) {
            return new ArrayList<>();
        }

        return filteredReservations.subList(startIndex, endIndex);
    }

    public int countSearchResults(String userId, String tableId, String status, String date) throws IOException {
        List<Reservation> allReservations = findAll();
        List<Reservation> filteredReservations = new ArrayList<>(allReservations);

        if (userId != null && !userId.isEmpty()) {
            filteredReservations = filteredReservations.stream()
                    .filter(r -> r.getUserId().equals(userId))
                    .collect(Collectors.toList());
        }

        if (tableId != null && !tableId.isEmpty()) {
            filteredReservations = filteredReservations.stream()
                    .filter(r -> r.getTableId() != null && r.getTableId().equals(tableId))
                    .collect(Collectors.toList());
        }

        if (status != null && !status.isEmpty()) {
            filteredReservations = filteredReservations.stream()
                    .filter(r -> r.getStatus().equals(status))
                    .collect(Collectors.toList());
        }

        if (date != null && !date.isEmpty()) {
            filteredReservations = filteredReservations.stream()
                    .filter(r -> r.getReservationDate().equals(date))
                    .collect(Collectors.toList());
        }

        return filteredReservations.size();
    }
}