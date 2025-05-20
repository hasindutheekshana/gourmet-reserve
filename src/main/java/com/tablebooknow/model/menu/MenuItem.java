package com.tablebooknow.model.menu;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;


public class MenuItem implements Serializable {
    private String id;
    private String name;
    private String description;
    private BigDecimal price;
    private String category;
    private boolean isAvailable;
    private String imageUrl;


    public MenuItem() {
        this.id = UUID.randomUUID().toString();
        this.isAvailable = true;
    }


    public MenuItem(String id, String name, String description, BigDecimal price,
                    String category, boolean isAvailable, String imageUrl) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.isAvailable = isAvailable;
        this.imageUrl = imageUrl;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }


    public String toCsvString() {
        return String.format("%s,%s,%s,%s,%s,%b,%s",
                id,
                escapeCommas(name),
                escapeCommas(description),
                price != null ? price.toString() : "",
                category != null ? category : "",
                isAvailable,
                imageUrl != null ? imageUrl : "");
    }

    public static MenuItem fromCsvString(String csvLine) {
        String[] parts = splitCsvLine(csvLine);

        if (parts.length < 7) {
            throw new IllegalArgumentException("Invalid CSV format for MenuItem");
        }

        BigDecimal price = null;
        if (parts[3] != null && !parts[3].isEmpty()) {
            try {
                price = new BigDecimal(parts[3]);
            } catch (NumberFormatException e) {
                System.err.println("Error parsing price: " + parts[3]);
                price = BigDecimal.ZERO;
            }
        }

        boolean isAvailable = true;
        if (parts[5] != null && !parts[5].isEmpty()) {
            isAvailable = Boolean.parseBoolean(parts[5]);
        }

        return new MenuItem(
                parts[0],
                unescapeCommas(parts[1]),
                unescapeCommas(parts[2]),
                price,
                parts[4],
                isAvailable,
                parts[6]
        );
    }

    private static String[] splitCsvLine(String line) {
        String[] result = new String[7];
        StringBuilder field = new StringBuilder();
        boolean inQuotes = false;
        int fieldIndex = 0;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == ',' && !inQuotes) {
                result[fieldIndex++] = field.toString();
                field = new StringBuilder();

                if (fieldIndex >= result.length) {
                    break;
                }
            } else if (c == '"') {
                inQuotes = !inQuotes;
                field.append(c);
            } else {
                field.append(c);
            }
        }

        if (fieldIndex < result.length) {
            result[fieldIndex] = field.toString();
        }

        return result;
    }

    private static String escapeCommas(String input) {
        if (input == null) {
            return "";
        }
        return input.replace(",", ";;");
    }

    private static String unescapeCommas(String input) {
        if (input == null) {
            return "";
        }
        return input.replace(";;", ",");
    }

    @Override
    public String toString() {
        return "MenuItem{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", category='" + category + '\'' +
                ", isAvailable=" + isAvailable +
                '}';
    }
}