package com.tablebooknow.config;

import com.tablebooknow.dao.MenuItemDAO;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.IOException;

@WebListener
public class MenuInitializer implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("Initializing default menu items");

        try {
            MenuItemDAO menuItemDAO = new MenuItemDAO();
            menuItemDAO.initializeDefaultMenuItems();

            System.out.println("Menu items initialization completed successfully");
        } catch (IOException e) {
            System.err.println("Failed to initialize menu items: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}