package com.tablebooknow.dao;

import com.tablebooknow.model.menu.MenuItem;
import com.tablebooknow.util.FileHandler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


public class MenuItemDAO {
    private static final Logger logger = Logger.getLogger(MenuItemDAO.class.getName());
    private static final String FILE_PATH = getDataFilePath("menu_items.txt");

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


    public MenuItem create(MenuItem menuItem) throws IOException {
        FileHandler.ensureFileExists(FILE_PATH);

        logger.info("Creating menu item in file: " + FILE_PATH);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(menuItem.toCsvString());
            writer.newLine();
        } catch (IOException e) {
            logger.severe("Error writing to file: " + e.getMessage());
            throw e;
        }

        return menuItem;
    }

    public MenuItem findById(String id) throws IOException {
        if (!FileHandler.fileExists(FILE_PATH)) {
            return null;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    try {
                        MenuItem menuItem = MenuItem.fromCsvString(line);
                        if (menuItem.getId().equals(id)) {
                            return menuItem;
                        }
                    } catch (Exception e) {
                        logger.warning("Error parsing menu item line: " + line + ", error: " + e.getMessage());
                    }
                }
            }
        }

        return null;
    }

    public List<MenuItem> findByCategory(String category) throws IOException {
        List<MenuItem> categoryItems = new ArrayList<>();

        if (!FileHandler.fileExists(FILE_PATH)) {
            return categoryItems;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    try {
                        MenuItem menuItem = MenuItem.fromCsvString(line);
                        if (menuItem.getCategory() != null &&
                                menuItem.getCategory().equalsIgnoreCase(category)) {
                            categoryItems.add(menuItem);
                        }
                    } catch (Exception e) {
                        logger.warning("Error parsing menu item line: " + line + ", error: " + e.getMessage());
                    }
                }
            }
        }

        return categoryItems;
    }


    public List<MenuItem> findAllAvailable() throws IOException {
        List<MenuItem> availableItems = new ArrayList<>();

        if (!FileHandler.fileExists(FILE_PATH)) {
            logger.warning("Menu items file does not exist: " + FILE_PATH);
            return availableItems;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (!line.trim().isEmpty()) {
                    try {
                        MenuItem menuItem = MenuItem.fromCsvString(line);
                        if (menuItem.isAvailable()) {
                            availableItems.add(menuItem);
                        }
                    } catch (Exception e) {
                        logger.warning("Error parsing menu item line " + lineNumber + ": " + line);
                        logger.warning("Error details: " + e.getMessage());
                        // Continue to next line on error
                    }
                }
            }
            logger.info("Found " + availableItems.size() + " available menu items");
        } catch (IOException e) {
            logger.severe("Error reading menu items file: " + e.getMessage());
            throw e;
        }

        return availableItems;
    }

    public boolean update(MenuItem menuItem) throws IOException {
        if (!FileHandler.fileExists(FILE_PATH)) {
            return false;
        }

        List<MenuItem> menuItems = findAll();
        boolean found = false;

        for (int i = 0; i < menuItems.size(); i++) {
            if (menuItems.get(i).getId().equals(menuItem.getId())) {
                menuItems.set(i, menuItem);
                found = true;
                break;
            }
        }

        if (!found) {
            return false;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (MenuItem item : menuItems) {
                writer.write(item.toCsvString());
                writer.newLine();
            }
        }

        return true;
    }

    public boolean delete(String id) throws IOException {
        if (!FileHandler.fileExists(FILE_PATH)) {
            return false;
        }

        List<MenuItem> menuItems = findAll();
        boolean found = false;

        for (int i = 0; i < menuItems.size(); i++) {
            if (menuItems.get(i).getId().equals(id)) {
                menuItems.remove(i);
                found = true;
                break;
            }
        }

        if (!found) {
            return false;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (MenuItem item : menuItems) {
                writer.write(item.toCsvString());
                writer.newLine();
            }
        }

        return true;
    }

    public List<MenuItem> findAll() throws IOException {
        List<MenuItem> menuItems = new ArrayList<>();

        if (!FileHandler.fileExists(FILE_PATH)) {
            logger.warning("Menu items file does not exist: " + FILE_PATH);
            return menuItems;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (!line.trim().isEmpty()) {
                    try {
                        menuItems.add(MenuItem.fromCsvString(line));
                    } catch (Exception e) {
                        logger.warning("Error parsing menu item line " + lineNumber + ": " + line);
                        logger.warning("Error details: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            logger.severe("Error reading menu items file: " + e.getMessage());
            throw e;
        }

        return menuItems;
    }

    public void initializeDefaultMenuItems() throws IOException {
        FileHandler.ensureFileExists(FILE_PATH);

        List<MenuItem> existingItems = findAll();
        if (!existingItems.isEmpty()) {
            return;
        }

        logger.info("Initializing default menu items...");

        MenuItem bruschetta = new MenuItem();
        bruschetta.setName("Bruschetta");
        bruschetta.setDescription("Grilled bread rubbed with garlic and topped with olive oil, salt, tomatoes, and basil");
        bruschetta.setPrice(new java.math.BigDecimal("8.99"));
        bruschetta.setCategory("appetizer");
        create(bruschetta);

        MenuItem calamari = new MenuItem();
        calamari.setName("Fried Calamari");
        calamari.setDescription("Lightly breaded and fried calamari served with marinara sauce");
        calamari.setPrice(new java.math.BigDecimal("12.99"));
        calamari.setCategory("appetizer");
        create(calamari);

        MenuItem steak = new MenuItem();
        steak.setName("Filet Mignon");
        steak.setDescription("8oz premium beef tenderloin grilled to perfection");
        steak.setPrice(new java.math.BigDecimal("36.99"));
        steak.setCategory("main");
        create(steak);

        MenuItem salmon = new MenuItem();
        salmon.setName("Grilled Atlantic Salmon");
        salmon.setDescription("Fresh Atlantic salmon served with lemon butter sauce");
        salmon.setPrice(new java.math.BigDecimal("28.99"));
        salmon.setCategory("main");
        create(salmon);

        MenuItem pasta = new MenuItem();
        pasta.setName("Fettuccine Alfredo");
        pasta.setDescription("Homemade pasta in a rich, creamy Parmesan sauce");
        pasta.setPrice(new java.math.BigDecimal("18.99"));
        pasta.setCategory("main");
        create(pasta);

        MenuItem cheesecake = new MenuItem();
        cheesecake.setName("New York Cheesecake");
        cheesecake.setDescription("Classic creamy cheesecake with graham cracker crust");
        cheesecake.setPrice(new java.math.BigDecimal("8.99"));
        cheesecake.setCategory("dessert");
        create(cheesecake);

        MenuItem tiramisu = new MenuItem();
        tiramisu.setName("Tiramisu");
        tiramisu.setDescription("Coffee-soaked ladyfingers layered with mascarpone cream");
        tiramisu.setPrice(new java.math.BigDecimal("9.99"));
        tiramisu.setCategory("dessert");
        create(tiramisu);

        MenuItem wine = new MenuItem();
        wine.setName("House Red Wine");
        wine.setDescription("Glass of our premium house red blend");
        wine.setPrice(new java.math.BigDecimal("8.99"));
        wine.setCategory("drink");
        create(wine);

        MenuItem cocktail = new MenuItem();
        cocktail.setName("Signature Martini");
        cocktail.setDescription("Our signature martini with premium vodka");
        cocktail.setPrice(new java.math.BigDecimal("12.99"));
        cocktail.setCategory("drink");
        create(cocktail);

        logger.info("Default menu items initialized successfully.");
    }
}