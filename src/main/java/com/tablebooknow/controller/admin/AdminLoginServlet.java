package com.tablebooknow.controller.admin;

import com.tablebooknow.dao.AdminDAO;
import com.tablebooknow.dao.UserDAO;
import com.tablebooknow.model.user.User;
import com.tablebooknow.util.PasswordHasher;
import com.tablebooknow.model.admin.Admin;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet({"/admin/login", "/admin/logout"})
public class AdminLoginServlet extends HttpServlet {
    private AdminDAO adminDAO;
    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        adminDAO = new AdminDAO();
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String servletPath = request.getServletPath();

        if ("/admin/logout".equals(servletPath)) {
            logout(request, response);
        } else {
            request.getRequestDispatcher("/adminLogin.jsp").forward(request, response);
        }
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (username == null || password == null || username.trim().isEmpty() || password.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Username and password are required");
            request.getRequestDispatcher("/adminLogin.jsp").forward(request, response);
            return;
        }

        try {
            Admin admin = adminDAO.findByUsername(username);

            if (admin != null && PasswordHasher.checkPassword(password, admin.getPassword())) {
                HttpSession session = request.getSession();
                session.setAttribute("adminId", admin.getId());
                session.setAttribute("adminUsername", admin.getUsername());
                session.setAttribute("isAdmin", true);
                session.setAttribute("adminRole", admin.getRole());

                session.setAttribute("adminMenu", AdminDashboardConfig.getAdminMenu());

                response.sendRedirect(request.getContextPath() + "/admin/dashboard");
            } else {
                User user = userDAO.findByUsername(username);

                if (user != null && user.isAdmin() && PasswordHasher.checkPassword(password, user.getPassword())) {
                    HttpSession session = request.getSession();
                    session.setAttribute("adminId", user.getId());
                    session.setAttribute("adminUsername", user.getUsername());
                    session.setAttribute("isAdmin", true);
                    session.setAttribute("adminRole", "admin"); // Default role for user-admins

                    response.sendRedirect(request.getContextPath() + "/admin/dashboard");
                } else {
                    request.setAttribute("errorMessage", "Invalid username or password");
                    request.getRequestDispatcher("/adminLogin.jsp").forward(request, response);
                }
            }
        } catch (Exception e) {
            request.setAttribute("errorMessage", "An error occurred during login: " + e.getMessage());
            request.getRequestDispatcher("/adminLogin.jsp").forward(request, response);
        }
    }

    private void logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        response.sendRedirect(request.getContextPath() + "/admin/login");
    }
}
