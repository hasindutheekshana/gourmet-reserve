package com.tablebooknow.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordHasher {
    private static final String ALGORITHM = "SHA-256";
    private static final int SALT_LENGTH = 16;
    private static final String DELIMITER = ":";


    public static String hashPassword(String password) {
        try {
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[SALT_LENGTH];
            random.nextBytes(salt);

            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            md.update(salt);
            byte[] hashedPassword = md.digest(password.getBytes());

            String saltString = Base64.getEncoder().encodeToString(salt);
            String hashString = Base64.getEncoder().encodeToString(hashedPassword);

            return saltString + DELIMITER + hashString;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    public static boolean checkPassword(String password, String storedHash) {
        try {
            String[] parts = storedHash.split(DELIMITER);
            if (parts.length != 2) {
                return false;
            }

            byte[] salt = Base64.getDecoder().decode(parts[0]);
            byte[] hash = Base64.getDecoder().decode(parts[1]);

            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            md.update(salt);
            byte[] hashedPassword = md.digest(password.getBytes());

            if (hash.length != hashedPassword.length) {
                return false;
            }

            int diff = 0;
            for (int i = 0; i < hash.length; i++) {
                diff |= hash[i] ^ hashedPassword[i];
            }

            return diff == 0;
        } catch (NoSuchAlgorithmException | IllegalArgumentException e) {
            return false;
        }
    }
}