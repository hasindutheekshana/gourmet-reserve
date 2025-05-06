package com.tablebooknow.controller.reservation;

import com.tablebooknow.dao.ReservationDAO;
import com.tablebooknow.model.reservation.Reservation;
import com.tablebooknow.dao.TableDAO;
import com.tablebooknow.model.table.Table;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@WebServlet("/reservation/*")
public class ReservationServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(ReservationServlet.class.getName());

    private ReservationDAO reservationDAO;
    private TableDAO tableDAO;

    @Override
    public void init() throws ServletException {
        logger.info("Initializing ReservationServlet");
        reservationDAO = new ReservationDAO();
        tableDAO = new TableDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        logger.info("GET request to: " + pathInfo);

        // Check if user is logged in
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            logger.info("User not logged in, redirecting to login page");
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        if (pathInfo == null || pathInfo.equals("/")) {
            logger.info("No path info, redirecting to date selection page");
            response.sendRedirect(request.getContextPath() + "/dateSelection.jsp");
            return;
        }

        logger.info("Processing path: " + pathInfo);
        switch (pathInfo) {
            case "/dateSelection":
                logger.info("Forwarding to date selection page");
                request.getRequestDispatcher("/dateSelection.jsp").forward(request, response);
                break;
            case "/tableSelection":
                handleTableSelectionRequest(request, response);
                break;
            case "/getReservedTables":
                handleGetReservedTablesRequest(request, response);
                break;
            case "/getAllTables":
                handleGetAllTablesRequest(request, response);
                break;
            default:
                logger.info("Unknown path: " + pathInfo + ", redirecting to date selection page");
                response.sendRedirect(request.getContextPath() + "/dateSelection.jsp");
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        logger.info("POST request to: " + pathInfo);

        // Check if user is logged in
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            logger.info("User not logged in, redirecting to login page");
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        if (pathInfo == null) {
            logger.info("No path info in POST request, redirecting to date selection page");
            response.sendRedirect(request.getContextPath() + "/dateSelection.jsp");
            return;
        }

        logger.info("Processing POST path: " + pathInfo);
        switch (pathInfo) {
            case "/createReservation":
                logger.info("Creating reservation");
                processDateTimeSelection(request, response);
                break;
            case "/confirmReservation":
                logger.info("Confirming reservation");
                confirmReservation(request, response);
                break;
            default:
                logger.info("Unknown POST path: " + pathInfo + ", redirecting to date selection page");
                response.sendRedirect(request.getContextPath() + "/dateSelection.jsp");
                break;
        }
    }

    private void handleTableSelectionRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Get reservation date and time from session or request
        HttpSession session = request.getSession();
        String reservationDate = (String) session.getAttribute("reservationDate");
        String reservationTime = (String) session.getAttribute("reservationTime");
        String bookingType = (String) session.getAttribute("bookingType");
        String reservationDuration = (String) session.getAttribute("reservationDuration");

        logger.info("Table selection requested for date=" + reservationDate + ", time=" + reservationTime);

        if (reservationDate == null || reservationTime == null) {
            // Try to get from request parameters
            reservationDate = request.getParameter("reservationDate");
            reservationTime = request.getParameter("reservationTime");
            bookingType = request.getParameter("bookingType");
            reservationDuration = request.getParameter("reservationDuration");

            logger.info("Using request parameters: date=" + reservationDate + ", time=" + reservationTime);
        }

        if (reservationDate == null || reservationTime == null) {
            logger.info("Missing date or time, redirecting to date selection page");
            response.sendRedirect(request.getContextPath() + "/dateSelection.jsp");
            return;
        }

        // Set defaults if not provided
        if (bookingType == null) {
            bookingType = "normal";
        }

        if (reservationDuration == null) {
            reservationDuration = (bookingType.equals("special")) ? "3" : "2";
        }

        int duration;
        try {
            duration = Integer.parseInt(reservationDuration);
        } catch (NumberFormatException e) {
            duration = (bookingType.equals("special")) ? 3 : 2;
        }

        // Get all tables from database
        List<Table> allTables;
        try {
            allTables = tableDAO.findAllActive();
            logger.info("Found " + allTables.size() + " active tables");
        } catch (Exception e) {
            logger.severe("Error getting active tables: " + e.getMessage());
            e.printStackTrace();
            allTables = new ArrayList<>();
        }

        // Get all reserved tables for this date and time
        List<String> reservedTables;
        try {
            reservedTables = reservationDAO.getReservedTables(reservationDate, reservationTime, duration);
            logger.info("Found " + reservedTables.size() + " reserved tables: " + reservedTables);
        } catch (Exception e) {
            logger.severe("Error getting reserved tables: " + e.getMessage());
            e.printStackTrace();
            reservedTables = new ArrayList<>();
        }

        // Store the data in session for use by the JSP
        session.setAttribute("reservationDate", reservationDate);
        session.setAttribute("reservationTime", reservationTime);
        session.setAttribute("bookingType", bookingType);
        session.setAttribute("reservationDuration", reservationDuration);
        request.setAttribute("reservedTables", reservedTables);
        request.setAttribute("allTables", allTables);

        logger.info("Forwarding to table selection JSP with " + allTables.size() + " tables and " + reservedTables.size() + " reserved tables");
        request.getRequestDispatcher("/tableSelection.jsp").forward(request, response);
    }

    private void handleGetAllTablesRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            List<Table> allTables = tableDAO.findAllActive();
            logger.info("Retrieved " + allTables.size() + " active tables for getAllTables request");

            // Group tables by floor and type
            Map<Integer, Map<String, List<Table>>> tablesByFloorAndType = new HashMap<>();

            for (Table table : allTables) {
                int floor = table.getFloor();
                String type = table.getTableType();

                if (!tablesByFloorAndType.containsKey(floor)) {
                    tablesByFloorAndType.put(floor, new HashMap<>());
                }

                Map<String, List<Table>> floorTables = tablesByFloorAndType.get(floor);
                if (!floorTables.containsKey(type)) {
                    floorTables.put(type, new ArrayList<>());
                }

                floorTables.get(type).add(table);
            }

            // Create response JSON
            Gson gson = new GsonBuilder().create();
            String json = gson.toJson(tablesByFloorAndType);

            PrintWriter out = response.getWriter();
            out.print(json);
            out.flush();
            logger.info("Sent tables JSON response");
        } catch (Exception e) {
            logger.severe("Error in handleGetAllTablesRequest: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    private void handleGetReservedTablesRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");

        String date = request.getParameter("date");
        String time = request.getParameter("time");
        String durationStr = request.getParameter("duration");

        if (date == null || time == null || durationStr == null) {
            logger.warning("Missing parameters for getReservedTables request");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Missing parameters\"}");
            return;
        }

        int duration;
        try {
            duration = Integer.parseInt(durationStr);
        } catch (NumberFormatException e) {
            duration = 2; // Default duration
        }

        try {
            List<String> reservedTables = reservationDAO.getReservedTables(date, time, duration);
            logger.info("Found " + reservedTables.size() + " reserved tables for date=" + date + ", time=" + time + ", duration=" + duration);

            // Build JSON response
            StringBuilder json = new StringBuilder("{\"reservedTables\":[");
            for (int i = 0; i < reservedTables.size(); i++) {
                json.append("\"").append(reservedTables.get(i)).append("\"");
                if (i < reservedTables.size() - 1) {
                    json.append(",");
                }
            }
            json.append("]}");

            response.getWriter().write(json.toString());
            logger.info("Sent reserved tables JSON response");
        } catch (Exception e) {
            logger.severe("Error in handleGetReservedTablesRequest: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    private void processDateTimeSelection(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        logger.info("Processing date and time selection");
        String reservationDate = request.getParameter("reservationDate");
        String reservationTime = request.getParameter("reservationTime");
        String bookingType = request.getParameter("bookingType");
        String reservationDuration = request.getParameter("reservationDuration");

        logger.info("Parameters received: date=" + reservationDate + ", time=" + reservationTime +
                ", type=" + bookingType + ", duration=" + reservationDuration);

        // Default values
        if (bookingType == null) {
            bookingType = "normal";
            logger.info("Using default booking type: normal");
        }

        if (reservationDuration == null) {
            reservationDuration = (bookingType.equals("special")) ? "3" : "2";
            logger.info("Using default duration: " + reservationDuration);
        }

        // Validate date and time
        if (reservationDate == null || reservationTime == null ||
                reservationDate.trim().isEmpty() || reservationTime.trim().isEmpty()) {

            logger.info("Missing date or time, returning to date selection page with error");
            request.setAttribute("errorMessage", "Please select both date and time");
            request.getRequestDispatcher("/dateSelection.jsp").forward(request, response);
            return;
        }

        try {
            // Parse and validate the date
            LocalDate date = LocalDate.parse(reservationDate);
            LocalDate today = LocalDate.now();
            logger.info("Date parsed: " + date + ", today is: " + today);

            if (date.isBefore(today)) {
                logger.info("Date is in the past");
                request.setAttribute("errorMessage", "Please select a future date");
                request.getRequestDispatcher("/dateSelection.jsp").forward(request, response);
                return;
            }

            // Parse and validate the time
            LocalTime time = LocalTime.parse(reservationTime);
            LocalTime openingTime = LocalTime.of(10, 0); // 10:00 AM
            LocalTime closingTime = LocalTime.of(22, 0); // 10:00 PM
            logger.info("Time parsed: " + time + ", opening: " + openingTime + ", closing: " + closingTime);

            if (time.isBefore(openingTime) || time.isAfter(closingTime)) {
                logger.info("Time is outside business hours");
                request.setAttribute("errorMessage", "Please select a time between 10:00 AM and 10:00 PM");
                request.getRequestDispatcher("/dateSelection.jsp").forward(request, response);
                return;
            }

            // If today, check if time is in the past
            if (date.isEqual(today) && time.isBefore(LocalTime.now())) {
                logger.info("Time is in the past on today's date");
                request.setAttribute("errorMessage", "Please select a future time");
                request.getRequestDispatcher("/dateSelection.jsp").forward(request, response);
                return;
            }

            // Check closing time based on duration
            int duration = Integer.parseInt(reservationDuration);
            LocalTime endTime = time.plusHours(duration);
            logger.info("End time calculated: " + endTime);

            if (endTime.isAfter(closingTime)) {
                logger.info("Reservation would end after closing time");
                request.setAttribute("errorMessage", "Your booking would end after our closing time (10:00 PM). Please select an earlier time or reduce duration.");
                request.getRequestDispatcher("/dateSelection.jsp").forward(request, response);
                return;
            }

            // Store the date, time and booking details in the session for later use
            HttpSession session = request.getSession();
            session.setAttribute("reservationDate", reservationDate);
            session.setAttribute("reservationTime", reservationTime);
            session.setAttribute("bookingType", bookingType);
            session.setAttribute("reservationDuration", reservationDuration);

            logger.info("Successfully stored in session: date=" + reservationDate +
                    ", time=" + reservationTime + ", type=" + bookingType + ", duration=" + reservationDuration);
            logger.info("Redirecting to table selection page");

            // Redirect to table selection page
            response.sendRedirect(request.getContextPath() + "/reservation/tableSelection");

        } catch (DateTimeParseException e) {
            logger.severe("Date/time parsing error: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("errorMessage", "Invalid date or time format: " + e.getMessage());
            request.getRequestDispatcher("/dateSelection.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            logger.severe("Number format error: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("errorMessage", "Invalid duration format: " + e.getMessage());
            request.getRequestDispatcher("/dateSelection.jsp").forward(request, response);
        } catch (Exception e) {
            logger.severe("Unexpected error: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("errorMessage", "An unexpected error occurred: " + e.getMessage());
            request.getRequestDispatcher("/dateSelection.jsp").forward(request, response);
        }
    }

    private void confirmReservation(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        logger.info("Confirming reservation");
        HttpSession session = request.getSession();
        String userId = (String) session.getAttribute("userId");
        String reservationDate = (String) session.getAttribute("reservationDate");
        String reservationTime = (String) session.getAttribute("reservationTime");
        String bookingType = (String) session.getAttribute("bookingType");
        String reservationDuration = (String) session.getAttribute("reservationDuration");

        logger.info("Session data: userId=" + userId + ", date=" + reservationDate +
                ", time=" + reservationTime + ", type=" + bookingType + ", duration=" + reservationDuration);

        String tableId = request.getParameter("tableId");
        String specialRequests = request.getParameter("specialRequests");
        logger.info("Request parameters: tableId=" + tableId + ", specialRequests=" +
                (specialRequests != null ? specialRequests.substring(0, Math.min(20, specialRequests.length())) + "..." : "null"));

        if (userId == null || reservationDate == null || reservationTime == null || tableId == null) {
            logger.warning("Missing required data, redirecting to date selection page");
            response.sendRedirect(request.getContextPath() + "/dateSelection.jsp");
            return;
        }

        // Default values if null
        if (bookingType == null) {
            bookingType = "normal";
            logger.info("Using default booking type: normal");
        }

        if (reservationDuration == null) {
            reservationDuration = (bookingType.equals("special")) ? "3" : "2";
            logger.info("Using default duration: " + reservationDuration);
        }

        try {
            // Check if table is available at this time
            LocalTime startTime = LocalTime.parse(reservationTime);
            int duration = Integer.parseInt(reservationDuration);
            logger.info("Checking if table " + tableId + " is available at " + startTime + " for " + duration + " hours");

            if (!isTableAvailable(tableId, reservationDate, startTime, duration)) {
                logger.warning("Table is not available");
                request.setAttribute("errorMessage", "This table is no longer available at the selected time. Please choose another table.");
                request.getRequestDispatcher("/tableSelection.jsp").forward(request, response);
                return;
            }

            logger.info("Table is available, creating reservation");
            // Create a new reservation
            Reservation reservation = new Reservation();
            reservation.setUserId(userId);
            reservation.setReservationDate(reservationDate);
            reservation.setReservationTime(reservationTime);
            reservation.setDuration(Integer.parseInt(reservationDuration));
            reservation.setTableId(tableId);
            reservation.setBookingType(bookingType);
            reservation.setSpecialRequests(specialRequests);
            reservation.setStatus("pending"); // Change status to pending until payment

            // Save the reservation
            logger.info("Saving reservation with ID: " + reservation.getId());
            Reservation createdReservation = reservationDAO.create(reservation);
            logger.info("Reservation created successfully");

            // Clear the session attributes related to the reservation form
            session.removeAttribute("reservationDate");
            session.removeAttribute("reservationTime");
            session.removeAttribute("bookingType");
            session.removeAttribute("reservationDuration");
            logger.info("Cleared session attributes");

            // Set reservation ID in session for payment process
            session.setAttribute("reservationId", createdReservation.getId());
            logger.info("Set reservation ID in session: " + createdReservation.getId());

            // Redirect directly to payment initiation instead of confirmation
            logger.info("Redirecting to payment initiation");
            response.sendRedirect(request.getContextPath() + "/payment/initiate");

        } catch (Exception e) {
            logger.severe("Error creating reservation: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("errorMessage", "Error creating reservation: " + e.getMessage());
            request.getRequestDispatcher("/tableSelection.jsp").forward(request, response);
        }
    }

    private boolean isTableAvailable(String tableId, String date, LocalTime startTime, int duration) throws IOException {
        logger.info("Checking table availability: tableId=" + tableId + ", date=" + date +
                ", startTime=" + startTime + ", duration=" + duration);

        return reservationDAO.isTableAvailable(tableId, date, startTime.toString(), duration);
    }
}