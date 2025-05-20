package com.tablebooknow.controller.admin;

import com.tablebooknow.dao.ReservationDAO;
import com.tablebooknow.dao.UserDAO;
import com.tablebooknow.model.reservation.Reservation;
import com.tablebooknow.model.user.User;
import com.tablebooknow.util.ReservationQueue;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/admin/reservations/*")
public class AdminReservationServlet extends HttpServlet {
    private ReservationDAO reservationDAO;
    private UserDAO userDAO;
    private ReservationQueue reservationQueue;

    @Override
    public void init() throws ServletException {
        reservationDAO = new ReservationDAO();
        userDAO = new UserDAO();

        try {
            List<Reservation> allReservations = reservationDAO.findAll();
            reservationQueue = new ReservationQueue(allReservations);
        } catch (IOException e) {
            reservationQueue = new ReservationQueue();
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("adminId") == null) {
            response.sendRedirect(request.getContextPath() + "/admin/login");
            return;
        }

        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            listAllReservations(request, response);
            return;
        }

        switch (pathInfo) {
            case "/view":
                viewReservation(request, response);
                break;
            case "/edit":
                showEditForm(request, response);
                break;
            case "/queue":
                showQueueManagement(request, response);
                break;
            case "/sorted":
                showSortedReservations(request, response);
                break;
            default:
                listAllReservations(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("adminId") == null) {
            response.sendRedirect(request.getContextPath() + "/admin/login");
            return;
        }

        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            response.sendRedirect(request.getContextPath() + "/admin/reservations");
            return;
        }

        switch (pathInfo) {
            case "/update":
                updateReservation(request, response);
                break;
            case "/cancel":
                cancelReservation(request, response);
                break;
            case "/delete":
                deleteReservation(request, response);
                break;
            case "/queue/process":
                processNextInQueue(request, response);
                break;
            case "/queue/prioritize":
                prioritizeReservation(request, response);
                break;
            case "/queue/refresh":
                refreshQueue(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/admin/reservations");
                break;
        }
    }

    private void listAllReservations(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {

            String statusFilter = request.getParameter("status");
            String dateFilter = request.getParameter("date");

            List<Reservation> filteredReservations;

            if ((statusFilter == null || statusFilter.isEmpty()) && (dateFilter == null || dateFilter.isEmpty())) {
                filteredReservations = reservationDAO.findAll();
            } else {

                List<Reservation> allReservations = reservationDAO.findAll();
                ReservationQueue queue = new ReservationQueue(allReservations);

                if (statusFilter != null && !statusFilter.isEmpty()) {
                    allReservations = queue.filterByStatus(statusFilter);
                    queue = new ReservationQueue(allReservations);
                }

                if (dateFilter != null && !dateFilter.isEmpty()) {
                    filteredReservations = new ArrayList<>();
                    for (Reservation res : allReservations) {
                        if (dateFilter.equals(res.getReservationDate())) {
                            filteredReservations.add(res);
                        }
                    }
                } else {
                    filteredReservations = allReservations;
                }
            }

            request.setAttribute("reservations", filteredReservations);
            request.setAttribute("statusFilter", statusFilter);
            request.setAttribute("dateFilter", dateFilter);

            request.getRequestDispatcher("/admin-reservations.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error loading reservations: " + e.getMessage());
            request.getRequestDispatcher("/admin-reservations.jsp").forward(request, response);
        }
    }

    private void viewReservation(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String id = request.getParameter("id");
        if (id == null || id.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/reservations");
            return;
        }

        try {
            Reservation reservation = reservationDAO.findById(id);
            if (reservation == null) {
                request.setAttribute("errorMessage", "Reservation not found");
                response.sendRedirect(request.getContextPath() + "/admin/reservations");
                return;
            }

            User user = userDAO.findById(reservation.getUserId());

            request.setAttribute("reservation", reservation);
            request.setAttribute("user", user);
            request.getRequestDispatcher("/admin-reservation-details.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error loading reservation: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/admin/reservations");
        }
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String id = request.getParameter("id");
        if (id == null || id.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/reservations");
            return;
        }

        try {
            Reservation reservation = reservationDAO.findById(id);
            if (reservation == null) {
                request.setAttribute("errorMessage", "Reservation not found");
                response.sendRedirect(request.getContextPath() + "/admin/reservations");
                return;
            }

            request.setAttribute("reservation", reservation);
            request.getRequestDispatcher("/admin-reservation-edit.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error loading reservation for editing: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/admin/reservations");
        }
    }

    private void updateReservation(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String reservationId = request.getParameter("reservationId");
        if (reservationId == null || reservationId.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/reservations");
            return;
        }

        try {
            Reservation reservation = reservationDAO.findById(reservationId);
            if (reservation == null) {
                request.setAttribute("errorMessage", "Reservation not found");
                response.sendRedirect(request.getContextPath() + "/admin/reservations");
                return;
            }

            String status = request.getParameter("status");
            String tableId = request.getParameter("tableId");
            String date = request.getParameter("reservationDate");
            String time = request.getParameter("reservationTime");
            String specialRequests = request.getParameter("specialRequests");
            String bookingType = request.getParameter("bookingType");
            String durationStr = request.getParameter("duration");

            if (status != null && !status.isEmpty()) {
                reservation.setStatus(status);
            }

            if (tableId != null && !tableId.isEmpty()) {
                reservation.setTableId(tableId);
            }

            if (date != null && !date.isEmpty()) {
                reservation.setReservationDate(date);
            }

            if (time != null && !time.isEmpty()) {
                reservation.setReservationTime(time);
            }

            if (specialRequests != null) {
                reservation.setSpecialRequests(specialRequests);
            }

            if (bookingType != null && !bookingType.isEmpty()) {
                reservation.setBookingType(bookingType);
            }

            if (durationStr != null && !durationStr.isEmpty()) {
                try {
                    int duration = Integer.parseInt(durationStr);
                    if (duration > 0) {
                        reservation.setDuration(duration);
                    }
                } catch (NumberFormatException e) {

                    System.err.println("Invalid duration format: " + durationStr);
                }
            }

            boolean success = reservationDAO.update(reservation);
            if (success) {

                refreshReservationQueue();

                request.setAttribute("successMessage", "Reservation updated successfully");

                response.sendRedirect(request.getContextPath() + "/admin/reservations/view?id=" + reservationId);
            } else {
                request.setAttribute("errorMessage", "Failed to update reservation");
                response.sendRedirect(request.getContextPath() + "/admin/reservations");
            }
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error updating reservation: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/admin/reservations");
        }
    }

    private void cancelReservation(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String reservationId = request.getParameter("reservationId");
        if (reservationId == null || reservationId.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/reservations");
            return;
        }

        try {
            boolean success = reservationDAO.cancelReservation(reservationId);
            if (success) {

                refreshReservationQueue();

                request.setAttribute("successMessage", "Reservation cancelled successfully");
            } else {
                request.setAttribute("errorMessage", "Failed to cancel reservation");
            }

            String referrer = request.getHeader("referer");
            if (referrer != null && referrer.contains("/queue")) {
                response.sendRedirect(request.getContextPath() + "/admin/reservations/queue");
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/reservations");
            }
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error cancelling reservation: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/admin/reservations");
        }
    }

    private void deleteReservation(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String reservationId = request.getParameter("reservationId");
        if (reservationId == null || reservationId.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/reservations");
            return;
        }

        try {
            boolean success = reservationDAO.delete(reservationId);
            if (success) {
                // Refresh the queue
                refreshReservationQueue();

                request.setAttribute("successMessage", "Reservation deleted successfully");
            } else {
                request.setAttribute("errorMessage", "Failed to delete reservation");
            }

            String referrer = request.getHeader("referer");
            if (referrer != null && referrer.contains("/queue")) {
                response.sendRedirect(request.getContextPath() + "/admin/reservations/queue");
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/reservations");
            }
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error deleting reservation: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/admin/reservations");
        }
    }

    private void showQueueManagement(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            List<Reservation> pendingReservations = reservationQueue.findPendingReservations();

            String sort = request.getParameter("sort");
            if ("time".equals(sort)) {
                ReservationQueue sortedQueue = new ReservationQueue(pendingReservations).sortByTime();
                pendingReservations = sortedQueue.getAllReservations();
                request.setAttribute("sorted", "true");
            }

            request.setAttribute("pendingReservations", pendingReservations);

            Reservation nextPending = reservationQueue.peekNextPending();
            request.setAttribute("nextPending", nextPending);

            request.getRequestDispatcher("/admin-reservation-queue.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error loading reservation queue: " + e.getMessage());
            request.getRequestDispatcher("/admin-reservation-queue.jsp").forward(request, response);
        }
    }

    private void showSortedReservations(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {

            List<Reservation> allReservations = reservationDAO.findAll();

            ReservationQueue queue = new ReservationQueue(allReservations);
            ReservationQueue sortedQueue = queue.sortByTime();

            List<Reservation> sortedReservations = sortedQueue.getAllReservations();

            request.setAttribute("reservations", sortedReservations);
            request.setAttribute("sortedByTime", "true");

            request.getRequestDispatcher("/admin-reservations.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error sorting reservations: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/admin/reservations");
        }
    }

    private void processNextInQueue(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {

            Reservation processed = reservationQueue.processNextReservation();

            if (processed != null) {

                processed.setStatus("confirmed");
                boolean success = reservationDAO.update(processed);

                if (success) {
                    request.setAttribute("successMessage", "Reservation " + processed.getId() + " has been processed and confirmed");
                } else {
                    request.setAttribute("errorMessage", "Error updating reservation status in database");
                }
            } else {
                request.setAttribute("warningMessage", "No pending reservations in the queue");
            }

            response.sendRedirect(request.getContextPath() + "/admin/reservations/queue");
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error processing reservation: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/admin/reservations/queue");
        }
    }

    private void prioritizeReservation(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String reservationId = request.getParameter("reservationId");
        if (reservationId == null || reservationId.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/reservations/queue");
            return;
        }

        try {
            boolean success = reservationQueue.prioritize(reservationId);

            if (success) {
                request.setAttribute("successMessage", "Reservation prioritized successfully");
            } else {
                request.setAttribute("errorMessage", "Failed to prioritize reservation");
            }

            response.sendRedirect(request.getContextPath() + "/admin/reservations/queue");
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error prioritizing reservation: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/admin/reservations/queue");
        }
    }

    private void refreshQueue(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            refreshReservationQueue();
            request.setAttribute("successMessage", "Reservation queue refreshed successfully");
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error refreshing queue: " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/admin/reservations/queue");
    }

    private void refreshReservationQueue() throws IOException {
        List<Reservation> latestReservations = reservationDAO.findAll();

        reservationQueue = new ReservationQueue(latestReservations);
    }
}