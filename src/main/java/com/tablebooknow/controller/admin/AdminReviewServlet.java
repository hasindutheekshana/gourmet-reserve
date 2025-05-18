package com.tablebooknow.controller.admin;

import com.tablebooknow.dao.ReviewDAO;
import com.tablebooknow.model.review.Review;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.logging.Logger;


@WebServlet("/admin/reviews/*")
public class AdminReviewServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(AdminReviewServlet.class.getName());
    private ReviewDAO reviewDAO;

    @Override
    public void init() throws ServletException {
        reviewDAO = new ReviewDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("adminId") == null) {
            response.sendRedirect(request.getContextPath() + "/admin/login");
            return;
        }

        String pathInfo = request.getPathInfo();
        if (pathInfo == null) {
            pathInfo = "/";
        }

        switch (pathInfo) {
            case "/delete":
                deleteReview(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/admin/reviews");
                break;
        }
    }


    private void deleteReview(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String reviewId = request.getParameter("reviewId");

        if (reviewId == null || reviewId.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Review ID is required");
            response.sendRedirect(request.getContextPath() + "/admin/reviews");
            return;
        }

        try {
            Review review = reviewDAO.findById(reviewId);

            if (review == null) {
                request.setAttribute("errorMessage", "Review not found");
                response.sendRedirect(request.getContextPath() + "/admin/reviews");
                return;
            }
            reviewDAO.delete(reviewId);

            request.setAttribute("successMessage", "Review has been deleted");
            response.sendRedirect(request.getContextPath() + "/admin/reviews");

        } catch (Exception e) {
            logger.severe("Error deleting review: " + e.getMessage());
            request.setAttribute("errorMessage", "Error deleting review: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/admin/reviews");
        }
    }
}