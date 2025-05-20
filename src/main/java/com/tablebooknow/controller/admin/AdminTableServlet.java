package com.tablebooknow.controller.admin;

import com.tablebooknow.dao.TableDAO;
import com.tablebooknow.dao.ReservationDAO;
import com.tablebooknow.model.table.Table;
import com.tablebooknow.model.reservation.Reservation;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// Servlet for admin table management with CRUD functionality
@WebServlet("/admin/tables/*")
public class AdminTableServlet extends HttpServlet {
    private TableDAO tableDAO; // DAO for table database operations
    private ReservationDAO reservationDAO; // DAO for reservation database operations

    // Initialize DAOs when servlet starts
    @Override
    public void init() throws ServletException {
        tableDAO = new TableDAO(); // Create TableDAO instance
        reservationDAO = new ReservationDAO(); // Create ReservationDAO instance
    }

    // Handle GET requests for viewing tables or forms
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false); // Get current session
        if (session == null || session.getAttribute("adminId") == null) {
            response.sendRedirect(request.getContextPath() + "/admin/login"); // Redirect to login if not authenticated
            return;
        }

        String pathInfo = request.getPathInfo(); // Get URL path after "/admin/tables/"

        if (pathInfo == null || pathInfo.equals("/")) {
            listAllTables(request, response); // List all tables for default path
            return;
        }

        // Route to methods based on URL path
        switch (pathInfo) {
            case "/view":
                viewTable(request, response); // View specific table details
                break;
            case "/edit":
                showEditForm(request, response); // Show edit table form
                break;
            case "/add":
                showAddForm(request, response); // Show add table form
                break;
            case "/floor":
                listTablesByFloor(request, response); // List tables by floor
                break;
            default:
                listAllTables(request, response); // Default to listing all tables
                break;
        }
    }

    // Handle POST requests for creating, updating, or deleting tables
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false); // Get current session
        if (session == null || session.getAttribute("adminId") == null) {
            response.sendRedirect(request.getContextPath() + "/admin/login"); // Redirect to login if not authenticated
            return;
        }

        String pathInfo = request.getPathInfo(); // Get URL path after "/admin/tables/"

        if (pathInfo == null || pathInfo.equals("/")) {
            response.sendRedirect(request.getContextPath() + "/admin/tables"); // Redirect to table list for default path
            return;
        }

        // Route to methods based on URL path
        switch (pathInfo) {
            case "/create":
                createTable(request, response); // Create new table
                break;
            case "/update":
                updateTable(request, response); // Update existing table
                break;
            case "/delete":
                deleteTable(request, response); // Delete table
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/admin/tables"); // Default redirect to table list
                break;
        }
    }

    // List all tables with optional filtering and searching
    private void listAllTables(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String searchTerm = request.getParameter("search"); // Get search term
            String floorFilter = request.getParameter("floor"); // Get floor filter
            String typeFilter = request.getParameter("type"); // Get type filter

            List<Table> allTables = tableDAO.findAll(); // Fetch all tables
            List<Table> filteredTables = new ArrayList<>(allTables); // Copy tables for filtering

            if (typeFilter != null && !typeFilter.isEmpty()) {
                filteredTables = filteredTables.stream()
                        .filter(table -> table.getTableType() != null && table.getTableType().equalsIgnoreCase(typeFilter)) // Filter by table type
                        .collect(java.util.stream.Collectors.toList());
                request.setAttribute("typeFilter", typeFilter); // Set type filter for JSP
            }

            if (floorFilter != null && !floorFilter.isEmpty()) {
                try {
                    int floor = Integer.parseInt(floorFilter); // Parse floor number
                    filteredTables = filteredTables.stream()
                            .filter(table -> table.getFloor() == floor) // Filter by floor
                            .collect(java.util.stream.Collectors.toList());
                    request.setAttribute("floorFilter", floorFilter); // Set floor filter for JSP
                } catch (NumberFormatException e) {
                    // Ignore invalid floor filter
                }
            }

            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                String search = searchTerm.toLowerCase(); // Convert search term to lowercase
                filteredTables = filteredTables.stream()
                        .filter(table -> (table.getTableNumber() != null && table.getTableNumber().toLowerCase().contains(search)) ||
                                (table.getTableType() != null && table.getTableType().toLowerCase().contains(search)) ||
                                (table.getLocationDescription() != null && table.getLocationDescription().toLowerCase().contains(search)) ||
                                (table.getId() != null && table.getId().toLowerCase().contains(search))) // Search across fields
                        .collect(java.util.stream.Collectors.toList());
                request.setAttribute("searchTerm", searchTerm); // Set search term for JSP
            }

            request.setAttribute("tables", filteredTables); // Set filtered tables for JSP
            request.setAttribute("tableCount", filteredTables.size()); // Set filtered table count
            request.setAttribute("totalTables", allTables.size()); // Set total table count

            request.getRequestDispatcher("/admin-tables.jsp").forward(request, response); // Forward to JSP

        } catch (Exception e) {
            System.err.println("Error in listAllTables: " + e.getMessage()); // Log error
            e.printStackTrace(); // Print stack trace
            request.setAttribute("errorMessage", "Error loading tables: " + e.getMessage()); // Set error message
            request.getRequestDispatcher("/admin-tables.jsp").forward(request, response); // Forward to JSP with error
        }
    }

    // List tables for a specific floor
    private void listTablesByFloor(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String floorParam = request.getParameter("floorNumber"); // Get floor number parameter

        if (floorParam == null || floorParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/tables"); // Redirect if floor is missing
            return;
        }

        try {
            int floor = Integer.parseInt(floorParam); // Parse floor number
            List<Table> floorTables = tableDAO.findByFloor(floor); // Fetch tables for floor

            request.setAttribute("tables", floorTables); // Set tables for JSP
            request.setAttribute("tableCount", floorTables.size()); // Set table count
            request.setAttribute("floorFilter", floorParam); // Set floor filter
            request.setAttribute("floorTitle", "Floor " + floor + " Tables"); // Set floor title

            request.getRequestDispatcher("/admin-tables.jsp").forward(request, response); // Forward to JSP

        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Invalid floor number"); // Set error for invalid floor
            response.sendRedirect(request.getContextPath() + "/admin/tables"); // Redirect to table list
        }
    }

    // View details of a specific table and its reservations
    private void viewTable(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String tableId = request.getParameter("id"); // Get table ID
        if (tableId == null || tableId.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/tables"); // Redirect if ID is missing
            return;
        }

        try {
            Table table = tableDAO.findById(tableId); // Fetch table by ID
            if (table == null) {
                request.setAttribute("errorMessage", "Table not found"); // Set error if table not found
                response.sendRedirect(request.getContextPath() + "/admin/tables"); // Redirect to table list
                return;
            }

            List<Reservation> tableReservations = new ArrayList<>(); // List for table reservations
            List<Reservation> allReservations = reservationDAO.findAll(); // Fetch all reservations

            for (Reservation reservation : allReservations) {
                if (tableId.equals(reservation.getTableId())) {
                    tableReservations.add(reservation); // Add reservations for this table
                }
            }

            request.setAttribute("table", table); // Set table for JSP
            request.setAttribute("tableReservations", tableReservations); // Set reservations for JSP
            request.getRequestDispatcher("/admin-table-details.jsp").forward(request, response); // Forward to JSP

        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error loading table: " + e.getMessage()); // Set error message
            response.sendRedirect(request.getContextPath() + "/admin/tables"); // Redirect to table list
        }
    }

    // Show form to edit a table
    private void showEditForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String tableId = request.getParameter("id"); // Get table ID
        if (tableId == null || tableId.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/tables"); // Redirect if ID is missing
            return;
        }

        try {
            Table table = tableDAO.findById(tableId); // Fetch table by ID
            if (table == null) {
                request.setAttribute("errorMessage", "Table not found"); // Set error if table not found
                response.sendRedirect(request.getContextPath() + "/admin/tables"); // Redirect to table list
                return;
            }

            request.setAttribute("table", table); // Set table for JSP
            request.setAttribute("editMode", true); // Set edit mode
            request.getRequestDispatcher("/admin-table-form.jsp").forward(request, response); // Forward to JSP

        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error loading table for editing: " + e.getMessage()); // Set error message
            response.sendRedirect(request.getContextPath() + "/admin/tables"); // Redirect to table list
        }
    }

    // Show form to add a new table
    private void showAddForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("editMode", false); // Set add mode
        request.getRequestDispatcher("/admin-table-form.jsp").forward(request, response); // Forward to JSP
    }

    // Create a new table from form data
    private void createTable(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String tableNumber = request.getParameter("tableNumber"); // Get table number
            String tableType = request.getParameter("tableType"); // Get table type
            String floorStr = request.getParameter("floor"); // Get floor
            String capacityStr = request.getParameter("capacity"); // Get capacity
            String locationDescription = request.getParameter("locationDescription"); // Get location description
            String isActiveStr = request.getParameter("isActive"); // Get active status

            // Validate required fields
            if (tableNumber == null || tableNumber.trim().isEmpty() ||
                    tableType == null || tableType.trim().isEmpty() ||
                    floorStr == null || floorStr.trim().isEmpty() ||
                    capacityStr == null || capacityStr.trim().isEmpty()) {
                request.setAttribute("errorMessage", "Table number, type, floor and capacity are required"); // Set error for missing fields
                request.setAttribute("editMode", false); // Set add mode
                request.getRequestDispatcher("/admin-table-form.jsp").forward(request, response); // Forward to form
                return;
            }

            int floor = Integer.parseInt(floorStr); // Parse floor number
            int capacity = Integer.parseInt(capacityStr); // Parse capacity
            boolean isActive = "on".equals(isActiveStr) || "true".equals(isActiveStr); // Convert active status to boolean

            Table newTable = new Table(); // Create new table object
            newTable.setTableNumber(tableNumber); // Set table number
            newTable.setTableType(tableType); // Set table type
            newTable.setFloor(floor); // Set floor
            newTable.setCapacity(capacity); // Set capacity
            newTable.setLocationDescription(locationDescription); // Set location description
            newTable.setActive(isActive); // Set active status

            String systemId = generateSystemTableId(tableType, floor, tableNumber); // Generate unique table ID
            newTable.setId(systemId); // Set table ID

            if (tableDAO.findById(systemId) != null) {
                request.setAttribute("errorMessage", "A table with this number and floor already exists"); // Set error for duplicate ID
                request.setAttribute("editMode", false); // Set add mode
                request.getRequestDispatcher("/admin-table-form.jsp").forward(request, response); // Forward to form
                return;
            }

            Table createdTable = tableDAO.create(newTable); // Save table to database

            if (createdTable != null && createdTable.getId() != null) {
                request.setAttribute("successMessage", "Table created successfully"); // Set success message
                response.sendRedirect(request.getContextPath() + "/admin/tables/view?id=" + createdTable.getId()); // Redirect to table details
            } else {
                request.setAttribute("errorMessage", "Failed to create table"); // Set error message
                request.setAttribute("editMode", false); // Set add mode
                request.getRequestDispatcher("/admin-table-form.jsp").forward(request, response); // Forward to form
            }
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Invalid number format for floor or capacity"); // Set error for invalid numbers
            request.setAttribute("editMode", false); // Set add mode
            request.getRequestDispatcher("/admin-table-form.jsp").forward(request, response); // Forward to form
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error creating table: " + e.getMessage()); // Set error message
            request.setAttribute("editMode", false); // Set add mode
            request.getRequestDispatcher("/admin-table-form.jsp").forward(request, response); // Forward to form
        }
    }

    // Generate unique table ID (e.g., f1-3)
    private String generateSystemTableId(String tableType, int floor, String tableNumber) {
        String prefix; // Prefix for table ID
        if (tableType != null && !tableType.isEmpty()) {
            prefix = tableType.substring(0, 1).toLowerCase(); // Use first letter of table type
        } else {
            prefix = "t"; // Default to 't' if type is empty
        }
        return prefix + floor + "-" + tableNumber; // Combine prefix, floor, and table number
    }

    // Update an existing table from form data
    private void updateTable(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String tableId = request.getParameter("tableId"); // Get table ID
        if (tableId == null || tableId.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/tables"); // Redirect if ID is missing
            return;
        }

        try {
            Table table = tableDAO.findById(tableId); // Fetch table by ID
            if (table == null) {
                request.setAttribute("errorMessage", "Table not found"); // Set error if table not found
                response.sendRedirect(request.getContextPath() + "/admin/tables"); // Redirect to table list
                return;
            }

            String tableNumber = request.getParameter("tableNumber"); // Get table number
            String tableType = request.getParameter("tableType"); // Get table type
            String floorStr = request.getParameter("floor"); // Get floor
            String capacityStr = request.getParameter("capacity"); // Get capacity
            String locationDescription = request.getParameter("locationDescription"); // Get location description
            String isActiveStr = request.getParameter("isActive"); // Get active status

            // Validate required fields
            if (tableNumber == null || tableNumber.trim().isEmpty() ||
                    tableType == null || tableType.trim().isEmpty() ||
                    floorStr == null || floorStr.trim().isEmpty() ||
                    capacityStr == null || capacityStr.trim().isEmpty()) {
                request.setAttribute("errorMessage", "Table number, type, floor and capacity are required"); // Set error for missing fields
                request.setAttribute("table", table); // Set table for form
                request.setAttribute("editMode", true); // Set edit mode
                request.getRequestDispatcher("/admin-table-form.jsp").forward(request, response); // Forward to form
                return;
            }

            int floor = Integer.parseInt(floorStr); // Parse floor number
            int capacity = Integer.parseInt(capacityStr); // Parse capacity
            boolean isActive = "on".equals(isActiveStr) || "true".equals(isActiveStr); // Convert active status to boolean

            table.setTableNumber(tableNumber); // Update table number
            table.setTableType(tableType); // Update table type
            table.setFloor(floor); // Update floor
            table.setCapacity(capacity); // Update capacity
            table.setLocationDescription(locationDescription); // Update location description
            table.setActive(isActive); // Update active status

            boolean success = tableDAO.update(table); // Save updates to database

            if (success) {
                request.setAttribute("successMessage", "Table updated successfully"); // Set success message
                response.sendRedirect(request.getContextPath() + "/admin/tables/view?id=" + tableId); // Redirect to table details
            } else {
                request.setAttribute("errorMessage", "Failed to update table"); // Set error message
                request.setAttribute("table", table); // Set table for form
                request.setAttribute("editMode", true); // Set edit mode
                request.getRequestDispatcher("/admin-table-form.jsp").forward(request, response); // Forward to form
            }
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Invalid number format for floor or capacity"); // Set error for invalid numbers
            response.sendRedirect(request.getContextPath() + "/admin/tables/edit?id=" + tableId); // Redirect to edit form
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error updating table: " + e.getMessage()); // Set error message
            response.sendRedirect(request.getContextPath() + "/admin/tables"); // Redirect to table list
        }
    }

    // Delete a table if no active reservations exist
    private void deleteTable(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String tableId = request.getParameter("tableId"); // Get table ID
        if (tableId == null || tableId.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/tables"); // Redirect if ID is missing
            return;
        }

        try {
            List<Reservation> allReservations = reservationDAO.findAll(); // Fetch all reservations
            List<Reservation> tableReservations = new ArrayList<>(); // List for table reservations

            for (Reservation reservation : allReservations) {
                if (tableId.equals(reservation.getTableId())) {
                    tableReservations.add(reservation); // Add reservations for this table
                }
            }

            if (!tableReservations.isEmpty()) {
                request.setAttribute("errorMessage", "Cannot delete table with active reservations..."); // Set error for active reservations
                response.sendRedirect(request.getContextPath() + "/admin/tables/view?id=" + tableId); // Redirect to table details
                return;
            }

            boolean success = tableDAO.delete(tableId); // Delete table from database

            if (success) {
                request.setAttribute("successMessage", "Table deleted successfully"); // Set success message
                response.sendRedirect(request.getContextPath() + "/admin/tables"); // Redirect to table list
            } else {
                request.setAttribute("errorMessage", "Failed to delete table"); // Set error message
                response.sendRedirect(request.getContextPath() + "/admin/tables/view?id=" + tableId); // Redirect to table details
            }
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error deleting table: " + e.getMessage()); // Set error message
            response.sendRedirect(request.getContextPath() + "/admin/tables"); // Redirect to table list
        }
    }
}