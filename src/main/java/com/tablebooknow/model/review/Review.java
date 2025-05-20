package com.tablebooknow.model.review;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;


public class Review implements Serializable {
    private String id;
    private String userId;
    private String reservationId;
    private int rating;
    private String title;
    private String comment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    public Review() {
        this.id = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }


    public Review(String id, String userId, String reservationId, int rating, String title,
                  String comment, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.reservationId = reservationId;
        this.rating = rating;
        this.title = title;
        this.comment = comment;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

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

    public String getReservationId() {
        return reservationId;
    }

    public void setReservationId(String reservationId) {
        this.reservationId = reservationId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        if (rating < 1) {
            this.rating = 1;
        } else if (rating > 5) {
            this.rating = 5;
        } else {
            this.rating = rating;
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }


    public String toCsvString() {
        return String.format("%s,%s,%s,%d,%s,%s,%s,%s",
                id,
                userId != null ? userId : "",
                reservationId != null ? reservationId : "",
                rating,
                title != null ? escapeCommas(title) : "",
                comment != null ? escapeCommas(comment) : "",
                createdAt != null ? createdAt.toString() : "",
                updatedAt != null ? updatedAt.toString() : "");
    }


    private String escapeCommas(String str) {
        return str.replace(",", ";;");
    }

    private static String unescapeCommas(String str) {
        return str.replace(";;", ",");
    }

    public static Review fromCsvString(String csvLine) {
        String[] parts = csvLine.split(",");
        if (parts.length < 8) {
            throw new IllegalArgumentException("Invalid CSV format for Review: " + csvLine);
        }

        int rating = 5;
        try {
            rating = Integer.parseInt(parts[3]);
        } catch (NumberFormatException e) {
        }

        LocalDateTime createdAt = null;
        LocalDateTime updatedAt = null;
        try {
            if (!parts[6].isEmpty()) {
                createdAt = LocalDateTime.parse(parts[6]);
            }
            if (!parts[7].isEmpty()) {
                updatedAt = LocalDateTime.parse(parts[7]);
            }
        } catch (Exception e) {
            LocalDateTime now = LocalDateTime.now();
            createdAt = now;
            updatedAt = now;
        }

        return new Review(
                parts[0],
                parts[1],
                parts[2],
                rating,
                unescapeCommas(parts[4]),
                unescapeCommas(parts[5]),
                createdAt,
                updatedAt
        );
    }

    @Override
    public String toString() {
        return "Review{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", reservationId='" + reservationId + '\'' +
                ", rating=" + rating +
                ", title='" + title + '\'' +
                ", comment='" + comment + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}