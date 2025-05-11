package com.tablebooknow.model.reservation;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a table reservation in the system.
 */
public class Reservation implements Serializable {
    private String id;
    private String userId;
    private String tableId;
    private String reservationDate;
    private String reservationTime;
    private int duration;
    private String bookingType;
    private String specialRequests;
    private String status;
    private String createdAt;

    /**
     * Default constructor with UUID generation
     */
    public Reservation() {
        this.id = UUID.randomUUID().toString();
        this.status = "pending";  // Default status
        this.createdAt = LocalDateTime.now().toString();
    }

    /**
     * Full constructor
     */
    public Reservation(String id, String userId, String tableId, String reservationDate,
                       String reservationTime, int duration, String bookingType,
                       String specialRequests, String status, String createdAt) {
        this.id = id;
        this.userId = userId;
        this.tableId = tableId;
        this.reservationDate = reservationDate;
        this.reservationTime = reservationTime;
        this.duration = duration;
        this.bookingType = bookingType;
        this.specialRequests = specialRequests;
        this.status = status;
        this.createdAt = createdAt;
    }

    // Getters and Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTableId() {
        return tableId;
    }

    public void setTableId(String tableId) {
        this.tableId = tableId;
    }

    public String getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(String reservationDate) {
        this.reservationDate = reservationDate;
    }

    public String getReservationTime() {
        return reservationTime;
    }

    public void setReservationTime(String reservationTime) {
        this.reservationTime = reservationTime;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getBookingType() {
        return bookingType;
    }

    public void setBookingType(String bookingType) {
        this.bookingType = bookingType;
    }

    public String getSpecialRequests() {
        return specialRequests;
    }

    public void setSpecialRequests(String specialRequests) {
        this.specialRequests = specialRequests;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Convert the reservation to a CSV string for file storage
     */
    public String toCsvString() {
        return String.format("%s,%s,%s,%s,%s,%d,%s,%s,%s,%s",
                id,
                userId,
                tableId != null ? tableId : "",
                reservationDate,
                reservationTime,
                duration,
                bookingType,
                specialRequests != null ? specialRequests.replace(",", ";;") : "",
                status,
                createdAt);
    }

    /**
     * Create a reservation from a CSV string
     */
    public static Reservation fromCsvString(String csvLine) {
        String[] parts = csvLine.split(",");
        if (parts.length < 9) {
            // Not enough parts
            throw new IllegalArgumentException("Invalid CSV format for Reservation: " + csvLine);
        }

        int duration = 2; // Default duration
        try {
            duration = Integer.parseInt(parts[5]);
        } catch (NumberFormatException e) {
            // Use default if parsing fails
        }

        // Restore commas in special requests
        String specialRequests = parts[7].replace(";;", ",");

        // Use default createdAt if not provided
        String createdAt = parts.length >= 10 ? parts[9] : LocalDateTime.now().toString();

        return new Reservation(
                parts[0], // id
                parts[1], // userId
                parts[2], // tableId
                parts[3], // reservationDate
                parts[4], // reservationTime
                duration, // duration
                parts[6], // bookingType
                specialRequests, // specialRequests
                parts[8], // status
                createdAt // createdAt
        );
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", tableId='" + tableId + '\'' +
                ", reservationDate='" + reservationDate + '\'' +
                ", reservationTime='" + reservationTime + '\'' +
                ", duration=" + duration +
                ", bookingType='" + bookingType + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}