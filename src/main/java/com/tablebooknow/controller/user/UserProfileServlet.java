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
}