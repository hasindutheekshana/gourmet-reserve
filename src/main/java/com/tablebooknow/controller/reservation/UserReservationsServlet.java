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

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String userId = (String) session.getAttribute("userId");

        try {
            List<Reservation> userReservations = reservationDAO.findByUserId(userId);

            userReservations = mergeSortReservations(userReservations);

            request.setAttribute("userReservations", userReservations);

            request.getRequestDispatcher("/user-reservations.jsp").forward(request, response);

        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error loading reservations: " + e.getMessage());
            request.getRequestDispatcher("/user-reservations.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

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

                Reservation reservation = reservationDAO.findById(reservationId);

                if (reservation != null && reservation.getUserId().equals(userId)) {

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

            response.sendRedirect(request.getContextPath() + "/user/reservations");
            return;
        }

        response.sendRedirect(request.getContextPath() + "/user/reservations");
    }

    private List<Reservation> mergeSortReservations(List<Reservation> reservations) {

        if (reservations.size() <= 1) {
            return reservations;
        }

        int mid = reservations.size() / 2;
        List<Reservation> left = new ArrayList<>(reservations.subList(0, mid));
        List<Reservation> right = new ArrayList<>(reservations.subList(mid, reservations.size()));

        left = mergeSortReservations(left);
        right = mergeSortReservations(right);

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

                LocalDate leftDate = LocalDate.parse(leftRes.getReservationDate());
                LocalDate rightDate = LocalDate.parse(rightRes.getReservationDate());

                int dateComparison = rightDate.compareTo(leftDate); // Descending by date (newest first)

                if (dateComparison != 0) {

                    if (dateComparison > 0) {
                        result.add(leftRes);
                        leftIndex++;
                    } else {
                        result.add(rightRes);
                        rightIndex++;
                    }
                } else {

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

                System.err.println("Error parsing dates for sorting: " + e.getMessage());

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