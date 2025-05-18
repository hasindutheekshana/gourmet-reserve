package com.tablebooknow.controller.admin;

import com.tablebooknow.dao.UserDAO;
import com.tablebooknow.dao.ReservationDAO;
import com.tablebooknow.model.user.User;
import com.tablebooknow.model.reservation.Reservation;
import com.tablebooknow.util.PasswordHasher;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@WebServlet("/admin/users/*")
public class AdminUserServlet extends HttpServlet {
    private UserDAO userDAO;
    private ReservationDAO reservationDAO;

    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
        reservationDAO = new ReservationDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("adminId") == null) {
            response.sendRedirect(request.getContextPath() + "/admin/login");
            return;
        }

        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            listAllUsers(request, response);
            return;
        }

        switch (pathInfo) {
            case "/view":
                viewUser(request, response);
                break;
            case "/edit":
                showEditForm(request, response);
                break;
            case "/add":
                showAddForm(request, response);
                break;
            default:
                listAllUsers(request, response);
                break;
        }
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("adminId") == null) {
            response.sendRedirect(request.getContextPath() + "/admin/login");
            return;
        }

        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            response.sendRedirect(request.getContextPath() + "/admin/users");
            return;
        }

        switch (pathInfo) {

            default:
                response.sendRedirect(request.getContextPath() + "/admin/users");
                break;
        }
    }

    private void listAllUsers(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String searchTerm = request.getParameter("search");
            String filter = request.getParameter("filter");

            List<User> users;

            try {
                users = userDAO.findAll();
                System.out.println("Total users before filtering: " + users.size());

                if ("admin".equals(filter)) {
                    List<User> filteredUsers = new ArrayList<>();
                    for (User user : users) {
                        if (user.isAdmin()) {
                            filteredUsers.add(user);
                        }
                    }
                    users = filteredUsers;
                    System.out.println("Admin users after filtering: " + users.size());
                } else if ("regular".equals(filter)) {
                    List<User> filteredUsers = new ArrayList<>();
                    for (User user : users) {
                        if (!user.isAdmin()) {
                            filteredUsers.add(user);
                        }
                    }
                    users = filteredUsers;
                    System.out.println("Regular users after filtering: " + users.size());
                }

                if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                    List<User> searchResults = new ArrayList<>();
                    searchTerm = searchTerm.toLowerCase();

                    for (User user : users) {
                        boolean matchesUsername = user.getUsername().toLowerCase().contains(searchTerm);
                        boolean matchesEmail = user.getEmail() != null && user.getEmail().toLowerCase().contains(searchTerm);
                        boolean matchesPhone = user.getPhone() != null && user.getPhone().toLowerCase().contains(searchTerm);

                        if (matchesUsername || matchesEmail || matchesPhone) {
                            searchResults.add(user);
                        }
                    }

                    users = searchResults;
                    request.setAttribute("searchTerm", searchTerm);
                }
            } catch (Exception e) {
                System.err.println("Error processing users: " + e.getMessage());
                users = new ArrayList<>(); // Empty list on error
            }

            request.setAttribute("users", users);
            request.setAttribute("userCount", users.size());
            request.setAttribute("filter", filter);

            request.getRequestDispatcher("/admin-users.jsp").forward(request, response);
        } catch (Exception e) {
            System.err.println("Error in listAllUsers: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("errorMessage", "Error loading users: " + e.getMessage());
            request.getRequestDispatcher("/admin-users.jsp").forward(request, response);
        }
    }

    private void viewUser(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String userId = request.getParameter("id");
        if (userId == null || userId.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/users");
            return;
        }

        try {
            User user = userDAO.findById(userId);
            if (user == null) {
                request.setAttribute("errorMessage", "User not found");
                response.sendRedirect(request.getContextPath() + "/admin/users");
                return;
            }

            List<Reservation> userReservations = reservationDAO.findByUserId(userId);

            request.setAttribute("user", user);
            request.setAttribute("userReservations", userReservations);
            request.getRequestDispatcher("/admin-user-details.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error loading user: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/admin/users");
        }
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String userId = request.getParameter("id");
        if (userId == null || userId.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/users");
            return;
        }

        try {
            User user = userDAO.findById(userId);
            if (user == null) {
                request.setAttribute("errorMessage", "User not found");
                response.sendRedirect(request.getContextPath() + "/admin/users");
                return;
            }

            request.setAttribute("user", user);
            request.setAttribute("editMode", true);
            request.getRequestDispatcher("/admin-user-form.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error loading user for editing: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/admin/users");
        }
    }

    private void showAddForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("editMode", false);
        request.getRequestDispatcher("/admin-user-form.jsp").forward(request, response);
    }

}
