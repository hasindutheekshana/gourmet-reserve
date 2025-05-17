package com.tablebooknow.dao;

import com.tablebooknow.model.table.Table;
import com.tablebooknow.util.FileHandler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Data Access Object for Table entities to handle file-based storage operations.
 */
public class TableDAO {
    private static final Logger logger = Logger.getLogger(TableDAO.class.getName());
    private static final String FILE_PATH = getDataFilePath("tables.txt");

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
     * Creates a new table by appending to the tables file.
     * @param table The table to create
     * @return The created table with assigned ID
     */
    public Table create(Table table) throws IOException {
        // Make sure the file exists
        FileHandler.ensureFileExists(FILE_PATH);

        logger.info("Creating table in file: " + FILE_PATH);

        // Set the system ID if it's not already set
        if (table.getId() == null || table.getId().trim().isEmpty()) {
            table.setId(table.generateSystemId());
        }

        // Append table to the file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(table.toCsvString());
            writer.newLine();
        } catch (IOException e) {
            logger.severe("Error writing to file: " + e.getMessage());
            throw e;
        }

        return table;
    }

    /**
     * Finds a table by its ID.
     * @param id The ID to search for
     * @return The table or null if not found
     */
    public Table findById(String id) throws IOException {
        if (!FileHandler.fileExists(FILE_PATH)) {
            return null;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    try {
                        Table table = Table.fromCsvString(line);
                        if (table.getId().equals(id)) {
                            return table;
                        }
                    } catch (Exception e) {
                        logger.warning("Error parsing table line: " + line + ", error: " + e.getMessage());
                        // Continue to next line on error
                    }
                }
            }
        }

        return null;
    }

    /**
     * Finds tables by floor.
     * @param floor The floor number
     * @return List of tables on the specified floor
     */
    public List<Table> findByFloor(int floor) throws IOException {
        List<Table> floorTables = new ArrayList<>();

        if (!FileHandler.fileExists(FILE_PATH)) {
            return floorTables;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    try {
                        Table table = Table.fromCsvString(line);
                        if (table.getFloor() == floor) {
                            floorTables.add(table);
                        }
                    } catch (Exception e) {
                        logger.warning("Error parsing table line: " + line + ", error: " + e.getMessage());
                        // Continue to next line on error
                    }
                }
            }
        }

        return floorTables;
    }

    /**
     * Finds tables by type.
     * @param tableType The table type (family, luxury, regular, couple)
     * @return List of tables of the specified type
     */
    public List<Table> findByType(String tableType) throws IOException {
        List<Table> typeTables = new ArrayList<>();

        if (!FileHandler.fileExists(FILE_PATH)) {
            return typeTables;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    try {
                        Table table = Table.fromCsvString(line);
                        if (table.getTableType() != null && table.getTableType().equalsIgnoreCase(tableType)) {
                            typeTables.add(table);
                        }
                    } catch (Exception e) {
                        logger.warning("Error parsing table line: " + line + ", error: " + e.getMessage());
                        // Continue to next line on error
                    }
                }
            }
        }

        return typeTables;
    }

    /**
     * Gets all active tables.
     * @return List of all active tables
     */
    public List<Table> findAllActive() throws IOException {
        List<Table> activeTables = new ArrayList<>();

        if (!FileHandler.fileExists(FILE_PATH)) {
            logger.warning("Tables file does not exist: " + FILE_PATH);
            return activeTables;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (!line.trim().isEmpty()) {
                    try {
                        Table table = Table.fromCsvString(line);
                        if (table.isActive()) {
                            activeTables.add(table);
                        }
                    } catch (Exception e) {
                        logger.warning("Error parsing table line " + lineNumber + ": " + line);
                        logger.warning("Error details: " + e.getMessage());
                        // Continue to next line on error
                    }
                }
            }
            logger.info("Found " + activeTables.size() + " active tables");
        } catch (IOException e) {
            logger.severe("Error reading tables file: " + e.getMessage());
            throw e;
        }

        return activeTables;
    }

    /**
     * Updates a table's information.
     * @param table The table to update
     * @return true if successful, false otherwise
     */
    public boolean update(Table table) throws IOException {
        if (!FileHandler.fileExists(FILE_PATH)) {
            return false;
        }

        List<Table> tables = findAll();
        boolean found = false;

        // Replace the table in the list
        for (int i = 0; i < tables.size(); i++) {
            if (tables.get(i).getId().equals(table.getId())) {
                tables.set(i, table);
                found = true;
                break;
            }
        }

        if (!found) {
            return false;
        }

        // Write all tables back to the file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Table t : tables) {
                writer.write(t.toCsvString());
                writer.newLine();
            }
        }

        return true;
    }

    /**
     * Deletes a table by its ID (soft delete, sets active=false).
     * @param id The ID of the table to delete
     * @return true if successful, false otherwise
     */
    public boolean softDelete(String id) throws IOException {
        Table table = findById(id);
        if (table == null) {
            return false;
        }

        table.setActive(false);
        return update(table);
    }

    /**
     * Deletes a table by its ID (hard delete, removes from file).
     * @param id The ID of the table to delete
     * @return true if successful, false otherwise
     */
    public boolean delete(String id) throws IOException {
        if (!FileHandler.fileExists(FILE_PATH)) {
            return false;
        }

        List<Table> tables = findAll();
        boolean found = false;

        // Remove the table from the list
        for (int i = 0; i < tables.size(); i++) {
            if (tables.get(i).getId().equals(id)) {
                tables.remove(i);
                found = true;
                break;
            }
        }

        if (!found) {
            return false;
        }

        // Write all tables back to the file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Table t : tables) {
                writer.write(t.toCsvString());
                writer.newLine();
            }
        }

        return true;
    }

    /**
     * Gets all tables from the file.
     * @return List of all tables
     */
    public List<Table> findAll() throws IOException {
        List<Table> tables = new ArrayList<>();

        if (!FileHandler.fileExists(FILE_PATH)) {
            logger.warning("Tables file does not exist: " + FILE_PATH);
            return tables;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (!line.trim().isEmpty()) {
                    try {
                        tables.add(Table.fromCsvString(line));
                    } catch (Exception e) {
                        logger.warning("Error parsing table line " + lineNumber + ": " + line);
                        logger.warning("Error details: " + e.getMessage());
                        // Continue to next line on error
                    }
                }
            }
        } catch (IOException e) {
            logger.severe("Error reading tables file: " + e.getMessage());
            throw e;
        }

        return tables;
    }

    /**
     * Get tables grouped by floor and type
     * @return Map of tables organized by floor and type
     */
    public Map<Integer, Map<String, List<Table>>> getTablesGroupedByFloorAndType() throws IOException {
        List<Table> allTables = findAllActive();
        Map<Integer, Map<String, List<Table>>> result = new HashMap<>();

        for (Table table : allTables) {
            int floor = table.getFloor();
            String type = table.getTableType();

            // Create floor entry if it doesn't exist
            if (!result.containsKey(floor)) {
                result.put(floor, new HashMap<>());
            }

            // Create type entry if it doesn't exist
            Map<String, List<Table>> floorMap = result.get(floor);
            if (!floorMap.containsKey(type)) {
                floorMap.put(type, new ArrayList<>());
            }

            // Add table to appropriate list
            floorMap.get(type).add(table);
        }

        return result;
    }

    /**
     * Initializes default tables if none exist.
     * This is useful for creating default tables on first run.
     */
    public void initializeDefaultTables() throws IOException {
        // Make sure the file exists
        FileHandler.ensureFileExists(FILE_PATH);

        // Check if we already have any tables
        List<Table> existingTables = findAll();
        if (!existingTables.isEmpty()) {
            return; // Tables already exist, no need to create defaults
        }

        // Create default tables
        logger.info("Initializing default tables...");

        // First floor tables
        for (int i = 1; i <= 4; i++) {
            Table familyTable = new Table();
            familyTable.setTableNumber(String.valueOf(i));
            familyTable.setTableType("family");
            familyTable.setCapacity(6);
            familyTable.setFloor(1);
            familyTable.setLocationDescription("First floor family section");
            familyTable.setId("f1-" + i);
            familyTable.setActive(true);
            create(familyTable);
        }

        for (int i = 1; i <= 10; i++) {
            Table regularTable = new Table();
            regularTable.setTableNumber(String.valueOf(i));
            regularTable.setTableType("regular");
            regularTable.setCapacity(4);
            regularTable.setFloor(1);
            regularTable.setLocationDescription("First floor main section");
            regularTable.setId("r1-" + i);
            regularTable.setActive(true);
            create(regularTable);
        }

        for (int i = 1; i <= 4; i++) {
            Table coupleTable = new Table();
            coupleTable.setTableNumber(String.valueOf(i));
            coupleTable.setTableType("couple");
            coupleTable.setCapacity(2);
            coupleTable.setFloor(1);
            coupleTable.setLocationDescription("First floor window section");
            coupleTable.setId("c1-" + i);
            coupleTable.setActive(true);
            create(coupleTable);
        }

        // Second floor tables
        for (int i = 1; i <= 6; i++) {
            Table familyTable = new Table();
            familyTable.setTableNumber(String.valueOf(i));
            familyTable.setTableType("family");
            familyTable.setCapacity(6);
            familyTable.setFloor(2);
            familyTable.setLocationDescription("Second floor family section");
            familyTable.setId("f2-" + i);
            familyTable.setActive(true);
            create(familyTable);
        }

        for (int i = 1; i <= 4; i++) {
            Table luxuryTable = new Table();
            luxuryTable.setTableNumber(String.valueOf(i));
            luxuryTable.setTableType("luxury");
            luxuryTable.setCapacity(10);
            luxuryTable.setFloor(2);
            luxuryTable.setLocationDescription("Second floor VIP section");
            luxuryTable.setId("l2-" + i);
            luxuryTable.setActive(true);
            create(luxuryTable);
        }

        for (int i = 1; i <= 6; i++) {
            Table coupleTable = new Table();
            coupleTable.setTableNumber(String.valueOf(i));
            coupleTable.setTableType("couple");
            coupleTable.setCapacity(2);
            coupleTable.setFloor(2);
            coupleTable.setLocationDescription("Second floor balcony section");
            coupleTable.setId("c2-" + i);
            coupleTable.setActive(true);
            create(coupleTable);
        }

        logger.info("Default tables initialized successfully.");
    }
}
