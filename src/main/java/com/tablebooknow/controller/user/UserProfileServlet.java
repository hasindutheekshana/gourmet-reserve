package com.tablebooknow.controller.user;

import com.tablebooknow.dao.UserDAO;
import com.tablebooknow.model.user.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/user/profile")
public class UserProfileServlet extends HttpServlet {
    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
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
            User user = userDAO.findById(userId);
            if (user == null) {
                // If user not found, invalidate session and redirect to login
                session.invalidate();
                response.sendRedirect(request.getContextPath() + "/login.jsp");
                return;
            }

            request.setAttribute("user", user);
            request.getRequestDispatcher("/user-profile.jsp").forward(request, response);
        } catch (Exception e) {
            System.err.println("Error processing user profile: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/login.jsp");
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

        if ("update-profile".equals(action)) {
            updateUserProfile(request, response, userId);
        } else {
            response.sendRedirect(request.getContextPath() + "/user/profile");
        }
    }

    private void updateUserProfile(HttpServletRequest request, HttpServletResponse response, String userId) throws ServletException, IOException {
        try {
            // Get current user data
            User user = userDAO.findById(userId);
            if (user == null) {
                request.setAttribute("errorMessage", "User not found");
                request.getRequestDispatcher("/user-profile.jsp").forward(request, response);
                return;
            }

            String email = request.getParameter("email");
            String phone = request.getParameter("phone");
            String currentPassword = request.getParameter("currentPassword");
            String newPassword = request.getParameter("newPassword");
            String confirmPassword = request.getParameter("confirmPassword");

            if (email != null && !email.trim().isEmpty()) {
                user.setEmail(email);
            }

            if (phone != null && !phone.trim().isEmpty()) {
                user.setPhone(phone);
            }

            if (currentPassword != null && !currentPassword.isEmpty() &&
                    newPassword != null && !newPassword.isEmpty() &&
                    confirmPassword != null && !confirmPassword.isEmpty()) {

                if (!com.tablebooknow.util.PasswordHasher.checkPassword(currentPassword, user.getPassword())) {
                    request.setAttribute("errorMessage", "Current password is incorrect");
                    request.setAttribute("user", user);
                    request.getRequestDispatcher("/user-profile.jsp").forward(request, response);
                    return;
                }

                if (!newPassword.equals(confirmPassword)) {
                    request.setAttribute("errorMessage", "New passwords do not match");
                    request.setAttribute("user", user);
                    request.getRequestDispatcher("/user-profile.jsp").forward(request, response);
                    return;
                }

                user.setPassword(com.tablebooknow.util.PasswordHasher.hashPassword(newPassword));
            }

            boolean updated = userDAO.update(user);
            if (updated) {
                request.setAttribute("successMessage", "Profile updated successfully");
            } else {
                request.setAttribute("errorMessage", "Failed to update profile");
            }

            request.setAttribute("user", user);
            request.getRequestDispatcher("/user-profile.jsp").forward(request, response);
        } catch (Exception e) {
            System.err.println("Error updating user profile: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("errorMessage", "An error occurred: " + e.getMessage());
            request.getRequestDispatcher("/user-profile.jsp").forward(request, response);
        }
    }
}
