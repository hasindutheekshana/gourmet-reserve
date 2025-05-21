package com.tablebooknow.controller.reservation;

import com.tablebooknow.dao.MenuItemDAO;
import com.tablebooknow.dao.ReservationDAO;
import com.tablebooknow.dao.ReservationMenuItemDAO;
import com.tablebooknow.model.menu.MenuItem;
import com.tablebooknow.model.menu.ReservationMenuItem;
import com.tablebooknow.model.reservation.Reservation;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


@WebServlet("/reservationMenu/*")
public class ReservationMenuServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(ReservationMenuServlet.class.getName());

    private MenuItemDAO menuItemDAO;
    private ReservationDAO reservationDAO;
    private ReservationMenuItemDAO reservationMenuItemDAO;

    @Override
    public void init() throws ServletException {
        menuItemDAO = new MenuItemDAO();
        reservationDAO = new ReservationDAO();
        reservationMenuItemDAO = new ReservationMenuItemDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String pathInfo = request.getPathInfo();
        if (pathInfo == null) {
            pathInfo = "/";
        }

        switch (pathInfo) {
            case "/":
            case "/select":
                showMenuSelectionPage(request, response);
                break;
            case "/getAvailableItems":
                getAvailableMenuItems(request, response);
                break;
            case "/getSelectedItems":
                getSelectedMenuItems(request, response);
                break;
            default:
                showMenuSelectionPage(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String pathInfo = request.getPathInfo();
        if (pathInfo == null) {
            pathInfo = "/";
        }

        switch (pathInfo) {
            case "/addItem":
                addMenuItem(request, response);
                break;
            case "/updateQuantity":
                updateMenuItemQuantity(request, response);
                break;
            case "/removeItem":
                removeMenuItem(request, response);
                break;
            case "/saveSelections":
                saveMenuSelections(request, response);
                break;
            case "/skipSelection":
                skipMenuSelection(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/reservationMenu/select");
                break;
        }
    }

    private void showMenuSelectionPage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String reservationId = request.getParameter("reservationId");

        if (reservationId == null) {
            reservationId = (String) request.getSession().getAttribute("reservationId");
        }

        if (reservationId == null) {
            response.sendRedirect(request.getContextPath() + "/user/reservations");
            return;
        }

        try {
            Reservation reservation = reservationDAO.findById(reservationId);
            if (reservation == null) {
                request.setAttribute("errorMessage", "Reservation not found");
                response.sendRedirect(request.getContextPath() + "/user/reservations");
                return;
            }

            String userId = (String) request.getSession().getAttribute("userId");
            if (!reservation.getUserId().equals(userId)) {
                request.setAttribute("errorMessage", "Access denied: This reservation does not belong to your account");
                response.sendRedirect(request.getContextPath() + "/user/reservations");
                return;
            }

            List<MenuItem> availableItems = menuItemDAO.findAllAvailable();

            Map<String, List<MenuItem>> itemsByCategory = new HashMap<>();
            for (MenuItem item : availableItems) {
                String category = item.getCategory();
                if (!itemsByCategory.containsKey(category)) {
                    itemsByCategory.put(category, new ArrayList<>());
                }
                itemsByCategory.get(category).add(item);
            }

            Map<MenuItem, Integer> selectedItems = reservationMenuItemDAO.findMenuItemsForReservation(reservationId);

            request.setAttribute("reservation", reservation);
            request.setAttribute("itemsByCategory", itemsByCategory);
            request.setAttribute("selectedItems", selectedItems);

            request.getRequestDispatcher("/menu-selection.jsp").forward(request, response);

        } catch (Exception e) {
            logger.severe("Error showing menu selection page: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("errorMessage", "Error loading menu selection page: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/user/reservations");
        }
    }

    private void getAvailableMenuItems(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            List<MenuItem> availableItems = menuItemDAO.findAllAvailable();

            Gson gson = new GsonBuilder().create();
            String json = gson.toJson(availableItems);

            PrintWriter out = response.getWriter();
            out.print(json);
            out.flush();

        } catch (Exception e) {
            logger.severe("Error getting available menu items: " + e.getMessage());
            e.printStackTrace();

            PrintWriter out = response.getWriter();
            out.print("{\"error\": \"" + e.getMessage() + "\"}");
            out.flush();
        }
    }

    private void getSelectedMenuItems(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String reservationId = request.getParameter("reservationId");
        if (reservationId == null || reservationId.isEmpty()) {
            PrintWriter out = response.getWriter();
            out.print("{\"error\": \"Reservation ID is required\"}");
            out.flush();
            return;
        }

        try {
            List<ReservationMenuItem> selectedItems = reservationMenuItemDAO.findByReservationId(reservationId);

            Gson gson = new GsonBuilder().create();
            String json = gson.toJson(selectedItems);

            PrintWriter out = response.getWriter();
            out.print(json);
            out.flush();

        } catch (Exception e) {
            logger.severe("Error getting selected menu items: " + e.getMessage());
            e.printStackTrace();

            PrintWriter out = response.getWriter();
            out.print("{\"error\": \"" + e.getMessage() + "\"}");
            out.flush();
        }
    }

    private void addMenuItem(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String reservationId = request.getParameter("reservationId");
        String menuItemId = request.getParameter("menuItemId");
        String quantityStr = request.getParameter("quantity");
        String specialInstructions = request.getParameter("specialInstructions");

        if (reservationId == null || menuItemId == null || quantityStr == null) {
            PrintWriter out = response.getWriter();
            out.print("{\"success\": false, \"message\": \"Missing required parameters\"}");
            out.flush();
            return;
        }

        try {
            int quantity = Integer.parseInt(quantityStr);
            if (quantity <= 0) {
                throw new NumberFormatException("Quantity must be greater than zero");
            }

            ReservationMenuItem item = new ReservationMenuItem();
            item.setReservationId(reservationId);
            item.setMenuItemId(menuItemId);
            item.setQuantity(quantity);
            item.setSpecialInstructions(specialInstructions);

            reservationMenuItemDAO.create(item);

            PrintWriter out = response.getWriter();
            out.print("{\"success\": true, \"message\": \"Menu item added successfully\", \"itemId\": \"" + item.getId() + "\"}");
            out.flush();

        } catch (NumberFormatException e) {
            logger.warning("Invalid quantity format: " + e.getMessage());

            PrintWriter out = response.getWriter();
            out.print("{\"success\": false, \"message\": \"Invalid quantity: " + e.getMessage() + "\"}");
            out.flush();

        } catch (Exception e) {
            logger.severe("Error adding menu item: " + e.getMessage());
            e.printStackTrace();

            PrintWriter out = response.getWriter();
            out.print("{\"success\": false, \"message\": \"Error adding menu item: " + e.getMessage() + "\"}");
            out.flush();
        }
    }

    private void updateMenuItemQuantity(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String reservationMenuItemId = request.getParameter("reservationMenuItemId");
        String quantityStr = request.getParameter("quantity");

        if (reservationMenuItemId == null || quantityStr == null) {
            PrintWriter out = response.getWriter();
            out.print("{\"success\": false, \"message\": \"Missing required parameters\"}");
            out.flush();
            return;
        }

        try {
            int quantity = Integer.parseInt(quantityStr);
            if (quantity <= 0) {
                throw new NumberFormatException("Quantity must be greater than zero");
            }

            ReservationMenuItem item = reservationMenuItemDAO.findById(reservationMenuItemId);
            if (item == null) {
                PrintWriter out = response.getWriter();
                out.print("{\"success\": false, \"message\": \"Item not found\"}");
                out.flush();
                return;
            }

            item.setQuantity(quantity);

            boolean success = reservationMenuItemDAO.update(item);

            PrintWriter out = response.getWriter();
            if (success) {
                out.print("{\"success\": true, \"message\": \"Quantity updated successfully\"}");
            } else {
                out.print("{\"success\": false, \"message\": \"Failed to update quantity\"}");
            }
            out.flush();

        } catch (NumberFormatException e) {
            logger.warning("Invalid quantity format: " + e.getMessage());

            PrintWriter out = response.getWriter();
            out.print("{\"success\": false, \"message\": \"Invalid quantity: " + e.getMessage() + "\"}");
            out.flush();

        } catch (Exception e) {
            logger.severe("Error updating menu item quantity: " + e.getMessage());
            e.printStackTrace();

            PrintWriter out = response.getWriter();
            out.print("{\"success\": false, \"message\": \"Error updating quantity: " + e.getMessage() + "\"}");
            out.flush();
        }
    }

    private void removeMenuItem(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String reservationMenuItemId = request.getParameter("reservationMenuItemId");

        if (reservationMenuItemId == null) {
            PrintWriter out = response.getWriter();
            out.print("{\"success\": false, \"message\": \"Missing item ID parameter\"}");
            out.flush();
            return;
        }

        try {
            boolean success = reservationMenuItemDAO.delete(reservationMenuItemId);

            PrintWriter out = response.getWriter();
            if (success) {
                out.print("{\"success\": true, \"message\": \"Menu item removed successfully\"}");
            } else {
                out.print("{\"success\": false, \"message\": \"Failed to remove menu item\"}");
            }
            out.flush();

        } catch (Exception e) {
            logger.severe("Error removing menu item: " + e.getMessage());
            e.printStackTrace();

            PrintWriter out = response.getWriter();
            out.print("{\"success\": false, \"message\": \"Error removing menu item: " + e.getMessage() + "\"}");
            out.flush();
        }
    }

    private void saveMenuSelections(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String reservationId = request.getParameter("reservationId");

        if (reservationId == null) {
            request.getSession().setAttribute("errorMessage", "Missing reservation ID");
            response.sendRedirect(request.getContextPath() + "/user/reservations");
            return;
        }

        try {
            List<ReservationMenuItem> selectedItems = reservationMenuItemDAO.findByReservationId(reservationId);
            if (selectedItems.isEmpty()) {
                request.setAttribute("errorMessage", "Please select at least one menu item or click 'Skip Menu Selection'");
                request.getRequestDispatcher("/reservationMenu/select?reservationId=" + reservationId).forward(request, response);
                return;
            }

            request.getSession().setAttribute("successMessage", "Menu selections saved successfully!");

            response.sendRedirect(request.getContextPath() + "/user/reservations");

        } catch (Exception e) {
            logger.severe("Error saving menu selections: " + e.getMessage());
            e.printStackTrace();

            request.setAttribute("errorMessage", "Error saving menu selections: " + e.getMessage());
            request.getRequestDispatcher("/reservationMenu/select?reservationId=" + reservationId).forward(request, response);
        }
    }

    private void skipMenuSelection(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String reservationId = request.getParameter("reservationId");

        if (reservationId == null) {
            request.getSession().setAttribute("errorMessage", "Missing reservation ID");
            response.sendRedirect(request.getContextPath() + "/user/reservations");
            return;
        }

        try {
            reservationMenuItemDAO.deleteByReservationId(reservationId);

            request.getSession().setAttribute("successMessage", "Menu selection skipped. You can add menu items later if you wish.");

            response.sendRedirect(request.getContextPath() + "/user/reservations");

        } catch (Exception e) {
            logger.severe("Error skipping menu selection: " + e.getMessage());
            e.printStackTrace();

            request.setAttribute("errorMessage", "Error skipping menu selection: " + e.getMessage());
            request.getRequestDispatcher("/reservationMenu/select?reservationId=" + reservationId).forward(request, response);
        }
    }
}