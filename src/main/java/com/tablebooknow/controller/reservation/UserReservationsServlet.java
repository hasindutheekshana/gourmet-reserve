package com.tablebooknow.controller.reservation;

import com.tablebooknow.dao.ReservationDAO;
import com.tablebooknow.model.reservation.Reservation;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/user/reservations")
public class UserReservationsServlet extends HttpServlet {
    private ReservationDAO reservationDAO;

    @Override
    public void init() throws ServletException {
        reservationDAO = new ReservationDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Check if user is logged in
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String userId = (String) session.getAttribute("userId");

        try {
            // Get all reservations for this user
            List<Reservation> userReservations = reservationDAO.findByUserId(userId);

            // Sort reservations using merge sort - by date and time
            userReservations = mergeSortReservations(userReservations);

            // Set as request attribute
            request.setAttribute("userReservations", userReservations);

            // Forward to the JSP
            request.getRequestDispatcher("/user-reservations.jsp").forward(request, response);

        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error loading reservations: " + e.getMessage());
            request.getRequestDispatcher("/user-reservations.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Check if user is logged in
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String userId = (String) session.getAttribute("userId");
        String action = request.getParameter("action");
        String reservationId = request.getParameter("reservationId");

        if ("cancel".equals(action) && reservationId != null) {
            try {
                // Get the reservation
                Reservation reservation = reservationDAO.findById(reservationId);

                // Verify the reservation belongs to this user
                if (reservation != null && reservation.getUserId().equals(userId)) {
                    // Cancel the reservation
                    boolean success = reservationDAO.cancelReservation(reservationId);

                    if (success) {
                        request.setAttribute("successMessage", "Reservation successfully cancelled.");
                    } else {
                        request.setAttribute("errorMessage", "Failed to cancel reservation.");
                    }
                } else {
                    request.setAttribute("errorMessage", "Invalid reservation or permission denied.");
                }
            } catch (Exception e) {
                request.setAttribute("errorMessage", "Error cancelling reservation: " + e.getMessage());
            }

            // Redirect to GET to refresh the list
            response.sendRedirect(request.getContextPath() + "/user/reservations");
            return;
        }

        // Default: redirect to GET
        response.sendRedirect(request.getContextPath() + "/user/reservations");
    }

    private List<Reservation> mergeSortReservations(List<Reservation> reservations) {
        // Base case: if the list has 0 or 1 elements, it's already sorted
        if (reservations.size() <= 1) {
            return reservations;
        }

        // Divide the list into two halves
        int mid = reservations.size() / 2;
        List<Reservation> left = new ArrayList<>(reservations.subList(0, mid));
        List<Reservation> right = new ArrayList<>(reservations.subList(mid, reservations.size()));

        // Recursively sort both halves
        left = mergeSortReservations(left);
        right = mergeSortReservations(right);

        // Merge the sorted halves
        return mergeReservations(left, right);
    }

    private List<Reservation> mergeReservations(List<Reservation> left, List<Reservation> right) {
        List<Reservation> result = new ArrayList<>();
        int leftIndex = 0;
        int rightIndex = 0;

        while (leftIndex < left.size() && rightIndex < right.size()) {
            Reservation leftRes = left.get(leftIndex);
            Reservation rightRes = right.get(rightIndex);

            try {
                // First compare by date
                LocalDate leftDate = LocalDate.parse(leftRes.getReservationDate());
                LocalDate rightDate = LocalDate.parse(rightRes.getReservationDate());

                int dateComparison = rightDate.compareTo(leftDate); // Descending by date (newest first)

                if (dateComparison != 0) {
                    // Dates are different, so we can decide based on the date comparison
                    if (dateComparison > 0) {
                        result.add(leftRes);
                        leftIndex++;
                    } else {
                        result.add(rightRes);
                        rightIndex++;
                    }
                } else {
                    // Dates are the same, so compare by time
                    LocalTime leftTime = LocalTime.parse(leftRes.getReservationTime());
                    LocalTime rightTime = LocalTime.parse(rightRes.getReservationTime());

                    if (leftTime.compareTo(rightTime) <= 0) {
                        result.add(leftRes);
                        leftIndex++;
                    } else {
                        result.add(rightRes);
                        rightIndex++;
                    }
                }
            } catch (DateTimeParseException e) {
                // Handle parsing errors gracefully
                System.err.println("Error parsing dates for sorting: " + e.getMessage());

                // Fall back to string comparison if parsing fails
                int dateComparison = rightRes.getReservationDate().compareTo(leftRes.getReservationDate());

                if (dateComparison != 0) {
                    if (dateComparison > 0) {
                        result.add(leftRes);
                        leftIndex++;
                    } else {
                        result.add(rightRes);
                        rightIndex++;
                    }
                } else {
                    int timeComparison = leftRes.getReservationTime().compareTo(rightRes.getReservationTime());
                    if (timeComparison <= 0) {
                        result.add(leftRes);
                        leftIndex++;
                    } else {
                        result.add(rightRes);
                        rightIndex++;
                    }
                }
            }
        }

        // Add any remaining elements
        while (leftIndex < left.size()) {
            result.add(left.get(leftIndex));
            leftIndex++;
        }

        while (rightIndex < right.size()) {
            result.add(right.get(rightIndex));
            rightIndex++;
        }

        return result;
    }
}