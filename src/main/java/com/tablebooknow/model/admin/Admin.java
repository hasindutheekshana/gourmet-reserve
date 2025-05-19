package com.tablebooknow.model.admin;

import java.io.Serializable;
import java.util.UUID;

public class Admin implements Serializable {
    private String id;
    private String username;
    private String password;
    private String email;
    private String fullName;
    private String role;

    public Admin() {
        this.id = UUID.randomUUID().toString();
        this.role = "admin";
    }


    public Admin(String id, String username, String password, String email, String fullName, String role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.fullName = fullName;
        this.role = role != null ? role : "admin";
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }


    public String toCsvString() {
        return String.format("%s,%s,%s,%s,%s,%s",
                id,
                username,
                password,
                email != null ? email : "",
                fullName != null ? fullName : "",
                role != null ? role : "admin");
    }


    public static Admin fromCsvString(String csvLine) {
        String[] parts = csvLine.split(",");
        if (parts.length < 6) {
            throw new IllegalArgumentException("Invalid CSV format for Admin");
        }

        return new Admin(
                parts[0],
                parts[1],
                parts[2],
                parts[3],
                parts[4],
                parts[5]
        );
    }

    @Override
    public String toString() {
        return "Admin{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", fullName='" + fullName + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
