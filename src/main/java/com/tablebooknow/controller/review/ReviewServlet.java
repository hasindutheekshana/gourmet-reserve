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
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;


@WebServlet("/reviews/*")
public class ReviewServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(ReviewServlet.class.getName());

    private ReviewDAO reviewDAO;
    private ReservationDAO reservationDAO;

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
            case "/add":
                showAddReviewForm(request, response, userId);
                break;
            case "/edit":
                showEditReviewForm(request, response, userId);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/reviews/list");
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

        String userId = (String) session.getAttribute("userId");
        String action = request.getParameter("action");

        if (action == null) {
            response.sendRedirect(request.getContextPath() + "/reviews/list");
            return;
        }

        switch (action) {
            case "create":
                createReview(request, response, userId);
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

    private void showAddReviewForm(HttpServletRequest request, HttpServletResponse response, String userId)
            throws ServletException, IOException {
        String reservationId = request.getParameter("reservationId");

        if (reservationId == null || reservationId.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Reservation ID is required");
            response.sendRedirect(request.getContextPath() + "/reviews/list");
            return;
        }

        try {
            Reservation reservation = reservationDAO.findById(reservationId);

            if (reservation == null) {
                request.setAttribute("errorMessage", "Reservation not found");
                response.sendRedirect(request.getContextPath() + "/reviews/list");
                return;
            }

            if (!reservation.getUserId().equals(userId)) {
                request.setAttribute("errorMessage", "You can only review your own reservations");
                response.sendRedirect(request.getContextPath() + "/reviews/list");
                return;
            }


            if (!("completed".equals(reservation.getStatus()) || "confirmed".equals(reservation.getStatus()))) {
                request.setAttribute("errorMessage", "You can only review completed reservations");
                response.sendRedirect(request.getContextPath() + "/reviews/list");
                return;
            }

            if (reviewDAO.hasReview(reservationId, userId)) {
                request.setAttribute("errorMessage", "You have already reviewed this reservation");
                response.sendRedirect(request.getContextPath() + "/reviews/list");
                return;
            }

            request.setAttribute("reservation", reservation);
            request.getRequestDispatcher("/review-form.jsp").forward(request, response);

        } catch (Exception e) {
            logger.severe("Error showing add review form: " + e.getMessage());
            request.setAttribute("errorMessage", "Error preparing review form: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/reviews/list");
        }
    }

    private void showEditReviewForm(HttpServletRequest request, HttpServletResponse response, String userId)
            throws ServletException, IOException {
        String reviewId = request.getParameter("reviewId");

        if (reviewId == null || reviewId.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Review ID is required");
            response.sendRedirect(request.getContextPath() + "/reviews/list");
            return;
        }

        try {
            Review review = reviewDAO.findById(reviewId);

            if (review == null) {
                request.setAttribute("errorMessage", "Review not found");
                response.sendRedirect(request.getContextPath() + "/reviews/list");
                return;
            }


            if (!review.getUserId().equals(userId)) {
                request.setAttribute("errorMessage", "You can only edit your own reviews");
                response.sendRedirect(request.getContextPath() + "/reviews/list");
                return;
            }

            Reservation reservation = reservationDAO.findById(review.getReservationId());

            request.setAttribute("review", review);
            request.setAttribute("reservation", reservation);
            request.setAttribute("editMode", true);

            request.getRequestDispatcher("/review-form.jsp").forward(request, response);

        } catch (Exception e) {
            logger.severe("Error showing edit review form: " + e.getMessage());
            request.setAttribute("errorMessage", "Error preparing edit form: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/reviews/list");
        }
    }

    private void createReview(HttpServletRequest request, HttpServletResponse response, String userId)
            throws ServletException, IOException {
        String reservationId = request.getParameter("reservationId");
        String ratingStr = request.getParameter("rating");
        String title = request.getParameter("title");
        String comment = request.getParameter("comment");

        if (reservationId == null || ratingStr == null || title == null || comment == null) {
            request.setAttribute("errorMessage", "All fields are required");
            response.sendRedirect(request.getContextPath() + "/reviews/add?reservationId=" + reservationId);
            return;
        }

        try {
            Reservation reservation = reservationDAO.findById(reservationId);

            if (reservation == null) {
                request.setAttribute("errorMessage", "Reservation not found");
                response.sendRedirect(request.getContextPath() + "/reviews/list");
                return;
            }

            if (!reservation.getUserId().equals(userId)) {
                request.setAttribute("errorMessage", "You can only review your own reservations");
                response.sendRedirect(request.getContextPath() + "/reviews/list");
                return;
            }

            if (reviewDAO.hasReview(reservationId, userId)) {
                request.setAttribute("errorMessage", "You have already reviewed this reservation");
                response.sendRedirect(request.getContextPath() + "/reviews/list");
                return;
            }

            int rating = 5;
            try {
                rating = Integer.parseInt(ratingStr);
                if (rating < 1) rating = 1;
                if (rating > 5) rating = 5;
            } catch (NumberFormatException e) {
            }


            Review review = new Review();
            review.setUserId(userId);
            review.setReservationId(reservationId);
            review.setRating(rating);
            review.setTitle(title);
            review.setComment(comment);
            review.setCreatedAt(LocalDateTime.now());
            review.setUpdatedAt(LocalDateTime.now());


            reviewDAO.create(review);

            request.setAttribute("successMessage", "Your review has been submitted");
            response.sendRedirect(request.getContextPath() + "/reviews/list");

        } catch (Exception e) {
            logger.severe("Error creating review: " + e.getMessage());
            request.setAttribute("errorMessage", "Error creating review: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/reviews/add?reservationId=" + reservationId);
        }
    }




}