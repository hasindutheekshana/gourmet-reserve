package com.tablebooknow.model.menu;

import java.io.Serializable;
import java.util.UUID;


public class ReservationMenuItem implements Serializable {
    private String id;
    private String reservationId;
    private String menuItemId;
    private int quantity;
    private String specialInstructions;


    public ReservationMenuItem() {
        this.id = UUID.randomUUID().toString();
        this.quantity = 1;
    }

    public ReservationMenuItem(String id, String reservationId, String menuItemId,
                               int quantity, String specialInstructions) {
        this.id = id;
        this.reservationId = reservationId;
        this.menuItemId = menuItemId;
        this.quantity = quantity;
        this.specialInstructions = specialInstructions;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReservationId() {
        return reservationId;
    }

    public void setReservationId(String reservationId) {
        this.reservationId = reservationId;
    }

    public String getMenuItemId() {
        return menuItemId;
    }

    public void setMenuItemId(String menuItemId) {
        this.menuItemId = menuItemId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getSpecialInstructions() {
        return specialInstructions;
    }

    public void setSpecialInstructions(String specialInstructions) {
        this.specialInstructions = specialInstructions;
    }


    public String toCsvString() {
        return String.format("%s,%s,%s,%d,%s",
                id,
                reservationId != null ? reservationId : "",
                menuItemId != null ? menuItemId : "",
                quantity,
                specialInstructions != null ? specialInstructions.replace(",", ";;") : "");
    }


    public static ReservationMenuItem fromCsvString(String csvLine) {
        String[] parts = csvLine.split(",");
        if (parts.length < 5) {
            throw new IllegalArgumentException("Invalid CSV format for ReservationMenuItem");
        }

        int quantity = 1;
        try {
            quantity = Integer.parseInt(parts[3]);
        } catch (NumberFormatException e) {
            System.err.println("Error parsing quantity: " + parts[3]);
        }

        String specialInstructions = parts[4];
        if (parts.length > 5) {
            StringBuilder sb = new StringBuilder(specialInstructions);
            for (int i = 5; i < parts.length; i++) {
                sb.append(",").append(parts[i]);
            }
            specialInstructions = sb.toString();
        }

        specialInstructions = specialInstructions.replace(";;", ",");

        return new ReservationMenuItem(
                parts[0],
                parts[1],
                parts[2],
                quantity,
                specialInstructions
        );
    }

    @Override
    public String toString() {
        return "ReservationMenuItem{" +
                "id='" + id + '\'' +
                ", reservationId='" + reservationId + '\'' +
                ", menuItemId='" + menuItemId + '\'' +
                ", quantity=" + quantity +
                '}';
    }
}