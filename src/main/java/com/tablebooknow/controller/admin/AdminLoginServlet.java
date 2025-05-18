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
            // Show login page
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
