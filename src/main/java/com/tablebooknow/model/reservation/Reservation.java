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