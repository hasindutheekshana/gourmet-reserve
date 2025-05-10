package com.tablebooknow.controller.user;

import com.tablebooknow.dao.UserDAO;
import com.tablebooknow.model.user.User;
import com.tablebooknow.util.PasswordHasher;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/user/*")
public class UserServlet extends HttpServlet {
    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            response.sendRedirect(request.getContextPath() + "/user/login.jsp");
            return;
        }

        switch (pathInfo) {
            case "/logout":
                logout(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/user/login.jsp");
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            response.sendRedirect(request.getContextPath() + "/user/login.jsp");
            return;
        }

        switch (pathInfo) {
            case "/login":
                login(request, response);
                break;
            case "/register":
                register(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/user/login.jsp");
                break;
        }
    }

    private void login(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (username == null || password == null || username.trim().isEmpty() || password.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Username and password are required");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }

        try {
            User user = userDAO.findByUsername(username);

            if (user != null && PasswordHasher.checkPassword(password, user.getPassword())) {
                HttpSession session = request.getSession();
                session.setAttribute("user", user);
                session.setAttribute("userId", user.getId());
                session.setAttribute("username", user.getUsername());
                session.setAttribute("isAdmin", user.isAdmin());

                if (user.isAdmin()) {
                    response.sendRedirect(request.getContextPath() + "/admin/dashboard");
                } else {
                    response.sendRedirect(request.getContextPath() + "/?showPreloader=true");
                }
            } else {
                request.setAttribute("errorMessage", "Invalid username or password");
                request.getRequestDispatcher("/login.jsp").forward(request, response);
            }
        } catch (Exception e) {
            request.setAttribute("errorMessage", "An error occurred during login: " + e.getMessage());
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }

    private void register(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String phone = request.getParameter("phone");

        if (username == null || email == null || password == null ||
                username.trim().isEmpty() || email.trim().isEmpty() || password.trim().isEmpty()) {
            request.setAttribute("errorMessage", "All fields are required");
            request.getRequestDispatcher("/login.jsp?register=true").forward(request, response);
            return;
        }

        try {
            if (userDAO.findByUsername(username) != null) {
                request.setAttribute("errorMessage", "Username already exists");
                request.getRequestDispatcher("/login.jsp?register=true").forward(request, response);
                return;
            }


            if (userDAO.findByEmail(email) != null) {
                request.setAttribute("errorMessage", "Email already exists");
                request.getRequestDispatcher("/login.jsp?register=true").forward(request, response);
                return;
            }


            User newUser = new User();
            newUser.setUsername(username);
            newUser.setEmail(email);
            newUser.setPassword(PasswordHasher.hashPassword(password));
            if (phone != null && !phone.trim().isEmpty()) {
                newUser.setPhone(phone);
            }

            userDAO.create(newUser);


            HttpSession session = request.getSession();
            session.setAttribute("user", newUser);
            session.setAttribute("userId", newUser.getId());
            session.setAttribute("username", newUser.getUsername());
            session.setAttribute("isAdmin", false);


            response.sendRedirect(request.getContextPath() + "/?showPreloader=true");
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error during registration: " + e.getMessage());
            request.getRequestDispatcher("/login.jsp?register=true").forward(request, response);
        }
    }

    private void logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        response.sendRedirect(request.getContextPath() + "/login.jsp");
    }
}

