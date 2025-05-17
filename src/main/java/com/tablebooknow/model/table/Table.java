package com.tablebooknow.model.table;

import java.io.Serializable;
import java.util.UUID;

/**
 * Represents a restaurant table in the system.
 * This class models a table with its properties and implements Serializable
 * to allow objects of this class to be converted to a byte stream for storage or transmission.
 */
public class Table implements Serializable {
    private String id;
    private String tableNumber;
    private String tableType;  // family, luxury, regular, couple
    private int capacity;
    private int floor;
    private String locationDescription;
    private boolean active;

    /**
     * Default constructor
     * Initializes a new Table object with a unique ID and sets the table as active by default.
     */
    public Table() {
        this.id = UUID.randomUUID().toString();
        this.active = true;  // Tables are active by default
    }

    /**
     * Constructor with all fields except ID (generates new ID)
     * Initializes a Table object with specified values for all fields except the ID,
     * which is automatically generated as a unique identifier.
     */
    public Table(String tableNumber, String tableType, int capacity, int floor, String locationDescription, boolean active) {
        this.id = UUID.randomUUID().toString();
        this.tableNumber = tableNumber;
        this.tableType = tableType;
        this.capacity = capacity;
        this.floor = floor;
        this.locationDescription = locationDescription;
        this.active = active;
    }

    /**
     * Constructor with all fields
     * Initializes a Table object with specified values for all fields, including the ID.
     * Useful when the unique ID is already known, such as when retrieving or recreating a table from a database.
     */
    public Table(String id, String tableNumber, String tableType, int capacity,
                 int floor, String locationDescription, boolean active) {
        this.id = id;
        this.tableNumber = tableNumber;
        this.tableType = tableType;
        this.capacity = capacity;
        this.floor = floor;
        this.locationDescription = locationDescription;
        this.active = active;
    }

    // Getters and Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(String tableNumber) {
        this.tableNumber = tableNumber;
    }

    public String getTableType() {
        return tableType;
    }

    public void setTableType(String tableType) {
        this.tableType = tableType;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public String getLocationDescription() {
        return locationDescription;
    }

    public void setLocationDescription(String locationDescription) {
        this.locationDescription = locationDescription;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Gets the table's display name based on its type and number
     * Returns a formatted string combining the table type and number (e.g., "Family Table 1").
     * If type or number is null, returns "Unknown Table".
     */
    public String getDisplayName() {
        if (tableType == null || tableNumber == null) {
            return "Unknown Table";
        }

        String typeLabel;

        switch (tableType.toLowerCase()) {
            case "family":
                typeLabel = "Family";
                break;
            case "luxury":
                typeLabel = "Luxury";
                break;
            case "couple":
                typeLabel = "Couple";
                break;
            case "regular":
                typeLabel = "Regular";
                break;
            default:
                typeLabel = "Table";
        }

        return typeLabel + " Table " + tableNumber;
    }

    /**
     * Generate a table ID based on the type and number
     * Format: f1-3 (for family table floor 1, number 3)
     * Returns a formatted ID combining a type prefix, floor number, and table number.
     * If tableType or tableNumber is null, returns the existing ID.
     */
    public String generateSystemId() {
        if (tableType == null || tableNumber == null) {
            return id;
        }

        String prefix;
        switch (tableType.toLowerCase()) {
            case "family":
                prefix = "f";
                break;
            case "luxury":
                prefix = "l";
                break;
            case "couple":
                prefix = "c";
                break;
            case "regular":
                prefix = "r";
                break;
            default:
                prefix = "t";
        }

        return prefix + floor + "-" + tableNumber;
    }

    /**
     * Converts the Table object to a CSV format string for file storage.
     * Format: id,tableNumber,tableType,capacity,floor,locationDescription,active
     * Handles null values and ensures commas in locationDescription are escaped to avoid CSV parsing issues.
     */
    public String toCsvString() {
        return String.format("%s,%s,%s,%d,%d,%s,%b",
                id,
                tableNumber != null ? tableNumber : "",
                tableType != null ? tableType : "",
                capacity,
                floor,
                locationDescription != null ? locationDescription.replace(",", ";;") : "",
                active);
    }

    /**
     * Creates a Table object from a CSV format string.
     * Expects the format: id,tableNumber,tableType,capacity,floor,locationDescription,active
     * Throws IllegalArgumentException if the CSV string is invalid or malformed.
     */
    public static Table fromCsvString(String csvLine) {
        // Validate that the CSV string is not null or empty (after trimming whitespace)
        if (csvLine == null || csvLine.trim().isEmpty()) {
            throw new IllegalArgumentException("Empty or null CSV line");
        }

        // Split the CSV string into parts using comma as the delimiter
        String[] parts = csvLine.split(",");
        // Ensure the CSV string has at least 7 parts to match the expected Table fields
        if (parts.length < 7) {
            throw new IllegalArgumentException("Invalid CSV format for Table: " + csvLine);
        }

        // Parse capacity and floor, defaulting to 0 if parsing fails
        int capacity = 0;
        int floor = 0;
        try {
            capacity = Integer.parseInt(parts[3]); // Parse capacity from the 4th field
            floor = Integer.parseInt(parts[4]);
        } catch (NumberFormatException e) {
            // Use default values (0) if parsing fails, allowing the method to continue
        }

        // Restore commas in locationDescription by replacing ";;" with ","
        String locationDescription = parts[5].replace(";;", ",");

        // Parse active status from the 7th field as a boolean
        boolean active = Boolean.parseBoolean(parts[6]);

        return new Table(
                parts[0],                  // id: Unique identifier (e.g., UUID)
                parts[1],                  // tableNumber: Table identifier (e.g., "Table 1")
                parts[2],                  // tableType: Type of table (e.g., "family")
                capacity,                  // capacity: Maximum seating capacity
                floor,                     // floor: Floor number where the table is located
                locationDescription,       // locationDescription: Description of table location
                active                     // active: Availability status (true/false)
        );
    }

    /**
     * Returns a string representation of the Table object.
     * Includes all fields in a readable format, useful for debugging or logging.
     * Format: Table{id='...', tableNumber='...', tableType='...', capacity=..., floor=..., active=...}
     */
    @Override
    public String toString() {
        return "Table{" +
                "id='" + id + '\'' + // Unique identifier (e.g., UUID), enclosed in single quotes
                ", tableNumber='" + tableNumber + '\'' + // Table number, enclosed in single quotes
                ", tableType='" + tableType + '\'' + // Table type, enclosed in single quotes
                ", capacity=" + capacity + // Seating capacity
                ", floor=" + floor + // Floor number
                ", active=" + active + // Active status (true/false)
                '}';
    }
}
