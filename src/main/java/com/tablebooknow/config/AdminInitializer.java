package com.tablebooknow.config;

import com.tablebooknow.dao.AdminDAO;
import com.tablebooknow.util.PasswordHasher;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.IOException;

@WebListener
public class AdminInitializer implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("Initializing admin accounts");

        try {
            // Initialize admin accounts
            AdminDAO adminDAO = new AdminDAO();
            adminDAO.initializeDefaultAdmin();

            System.out.println("Admin initialization completed successfully");
        } catch (IOException e) {
            System.err.println("Failed to initialize admin accounts: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Cleanup if needed
    }
}