package com.tablebooknow.dao;

import com.tablebooknow.model.menu.MenuItem;
import com.tablebooknow.model.menu.ReservationMenuItem;
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


public class ReservationMenuItemDAO {
    private static final Logger logger = Logger.getLogger(ReservationMenuItemDAO.class.getName());
    private static final String FILE_PATH = getDataFilePath("reservation_menu_items.txt");
    private MenuItemDAO menuItemDAO;


    public ReservationMenuItemDAO() {
        this.menuItemDAO = new MenuItemDAO();
    }

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

    public ReservationMenuItem create(ReservationMenuItem item) throws IOException {
        FileHandler.ensureFileExists(FILE_PATH);

        logger.info("Creating reservation menu item in file: " + FILE_PATH);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(item.toCsvString());
            writer.newLine();
        } catch (IOException e) {
            logger.severe("Error writing to file: " + e.getMessage());
            throw e;
        }

        return item;
    }

    public ReservationMenuItem findById(String id) throws IOException {
        if (!FileHandler.fileExists(FILE_PATH)) {
            return null;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    try {
                        ReservationMenuItem item = ReservationMenuItem.fromCsvString(line);
                        if (item.getId().equals(id)) {
                            return item;
                        }
                    } catch (Exception e) {
                        logger.warning("Error parsing reservation menu item line: " + line);
                    }
                }
            }
        }

        return null;
    }


    public List<ReservationMenuItem> findByReservationId(String reservationId) throws IOException {
        List<ReservationMenuItem> items = new ArrayList<>();

        if (!FileHandler.fileExists(FILE_PATH)) {
            return items;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    try {
                        ReservationMenuItem item = ReservationMenuItem.fromCsvString(line);
                        if (item.getReservationId().equals(reservationId)) {
                            items.add(item);
                        }
                    } catch (Exception e) {
                        logger.warning("Error parsing reservation menu item line: " + line);
                    }
                }
            }
        }

        return items;
    }

    public Map<MenuItem, Integer> findMenuItemsForReservation(String reservationId) throws IOException {
        Map<MenuItem, Integer> menuItems = new HashMap<>();
        List<ReservationMenuItem> reservationMenuItems = findByReservationId(reservationId);

        for (ReservationMenuItem rmi : reservationMenuItems) {
            try {
                MenuItem menuItem = menuItemDAO.findById(rmi.getMenuItemId());
                if (menuItem != null) {
                    menuItems.put(menuItem, rmi.getQuantity());
                }
            } catch (IOException e) {
                logger.warning("Error retrieving menu item: " + e.getMessage());
            }
        }

        return menuItems;
    }

    public boolean update(ReservationMenuItem item) throws IOException {
        if (!FileHandler.fileExists(FILE_PATH)) {
            return false;
        }

        List<ReservationMenuItem> items = findAll();
        boolean found = false;

        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getId().equals(item.getId())) {
                items.set(i, item);
                found = true;
                break;
            }
        }

        if (!found) {
            return false;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (ReservationMenuItem i : items) {
                writer.write(i.toCsvString());
                writer.newLine();
            }
        }

        return true;
    }

    public boolean delete(String id) throws IOException {
        if (!FileHandler.fileExists(FILE_PATH)) {
            return false;
        }

        List<ReservationMenuItem> items = findAll();
        boolean found = false;

        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getId().equals(id)) {
                items.remove(i);
                found = true;
                break;
            }
        }

        if (!found) {
            return false;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (ReservationMenuItem i : items) {
                writer.write(i.toCsvString());
                writer.newLine();
            }
        }

        return true;
    }

    public boolean deleteByReservationId(String reservationId) throws IOException {
        if (!FileHandler.fileExists(FILE_PATH)) {
            return false;
        }

        List<ReservationMenuItem> allItems = findAll();
        List<ReservationMenuItem> remainingItems = new ArrayList<>();

        for (ReservationMenuItem item : allItems) {
            if (!item.getReservationId().equals(reservationId)) {
                remainingItems.add(item);
            }
        }

        if (allItems.size() == remainingItems.size()) {
            return false;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (ReservationMenuItem i : remainingItems) {
                writer.write(i.toCsvString());
                writer.newLine();
            }
        }

        return true;
    }

    public List<ReservationMenuItem> findAll() throws IOException {
        List<ReservationMenuItem> items = new ArrayList<>();

        if (!FileHandler.fileExists(FILE_PATH)) {
            return items;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    try {
                        items.add(ReservationMenuItem.fromCsvString(line));
                    } catch (Exception e) {
                        logger.warning("Error parsing reservation menu item line: " + line);
                    }
                }
            }
        }

        return items;
    }

    public java.math.BigDecimal getTotalPriceForReservation(String reservationId) throws IOException {
        java.math.BigDecimal totalPrice = java.math.BigDecimal.ZERO;
        Map<MenuItem, Integer> menuItems = findMenuItemsForReservation(reservationId);

        for (Map.Entry<MenuItem, Integer> entry : menuItems.entrySet()) {
            MenuItem item = entry.getKey();
            Integer quantity = entry.getValue();

            if (item.getPrice() != null) {
                java.math.BigDecimal itemTotal = item.getPrice().multiply(new java.math.BigDecimal(quantity));
                totalPrice = totalPrice.add(itemTotal);
            }
        }

        return totalPrice;
    }
}