package com.tablebooknow.controller.review;

import com.tablebooknow.dao.ReviewDAO;
import com.tablebooknow.model.review.Review;
import com.tablebooknow.model.reservation.Reservation;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;


@WebServlet("/reviews/*")
public class ReviewServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(ReviewServlet.class.getName());

    private ReviewDAO reviewDAO;

    @Override
    public void init() throws ServletException {
        reviewDAO = new ReviewDAO();
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
        String pathInfo = request.getPathInfo();

        if (pathInfo == null) {
            pathInfo = "/";
        }

        switch (pathInfo) {
            case "/":
            case "/list":
                showUserReviews(request, response, userId);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/reviews/list");
                break;
        }
    }

    private void showUserReviews(HttpServletRequest request, HttpServletResponse response, String userId)
            throws ServletException, IOException {
        try {
            List<Review> userReviews = reviewDAO.findByUserId(userId);
            request.setAttribute("reviews", userReviews);

            // Get completed reservations that don't have reviews yet
            List<Reservation> userReservations = reservationDAO.findByUserId(userId);

            List<Reservation> completedReservationsWithoutReviews = userReservations.stream()
                    .filter(reservation -> "completed".equals(reservation.getStatus()) || "confirmed".equals(reservation.getStatus()))
                    .filter(reservation -> {
                        try {
                            return !reviewDAO.hasReview(reservation.getId(), userId);
                        } catch (IOException e) {
                            return false;
                        }
                    })
                    .collect(java.util.stream.Collectors.toList());

            request.setAttribute("completedReservations", completedReservationsWithoutReviews);

            request.getRequestDispatcher("/user-reviews.jsp").forward(request, response);
        } catch (Exception e) {
            logger.severe("Error showing user reviews: " + e.getMessage());
            request.setAttribute("errorMessage", "Error retrieving reviews: " + e.getMessage());
            request.getRequestDispatcher("/user-reviews.jsp").forward(request, response);
        }
    }




}