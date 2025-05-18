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

            case "/create":
                createUser(request, response);
                break;
            case "/update":
                updateUser(request, response);
                break;
            case "/updateAdmin":
                toggleAdminStatus(request, response);
                break;
            case "/delete":
                deleteUser(request, response);
                break;

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

    private void createUser(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String username = request.getParameter("username");
            String password = request.getParameter("password");
            String email = request.getParameter("email");
            String phone = request.getParameter("phone");
            String isAdminStr = request.getParameter("isAdmin");

            if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
                request.setAttribute("errorMessage", "Username and password are required");
                request.setAttribute("editMode", false);
                request.getRequestDispatcher("/admin-user-form.jsp").forward(request, response);
                return;
            }

            User existingUser = userDAO.findByUsername(username);
            if (existingUser != null) {
                request.setAttribute("errorMessage", "Username already exists");
                request.setAttribute("editMode", false);
                request.getRequestDispatcher("/admin-user-form.jsp").forward(request, response);
                return;
            }

            if (email != null && !email.trim().isEmpty()) {
                User userWithEmail = userDAO.findByEmail(email);
                if (userWithEmail != null) {
                    request.setAttribute("errorMessage", "Email already in use");
                    request.setAttribute("editMode", false);
                    request.getRequestDispatcher("/admin-user-form.jsp").forward(request, response);
                    return;
                }
            }

            User newUser = new User();
            newUser.setUsername(username);
            newUser.setPassword(PasswordHasher.hashPassword(password));
            newUser.setEmail(email);
            newUser.setPhone(phone);
            newUser.setAdmin("on".equals(isAdminStr) || "true".equals(isAdminStr));

            User createdUser = userDAO.create(newUser);

            if (createdUser != null && createdUser.getId() != null) {
                request.setAttribute("successMessage", "User created successfully");
                response.sendRedirect(request.getContextPath() + "/admin/users/view?id=" + createdUser.getId());
            } else {
                request.setAttribute("errorMessage", "Failed to create user");
                request.setAttribute("editMode", false);
                request.getRequestDispatcher("/admin-user-form.jsp").forward(request, response);
            }
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error creating user: " + e.getMessage());
            request.setAttribute("editMode", false);
            request.getRequestDispatcher("/admin-user-form.jsp").forward(request, response);
        }
    }

    private void updateUser(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String userId = request.getParameter("userId");
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

            String username = request.getParameter("username");
            String password = request.getParameter("password");
            String email = request.getParameter("email");
            String phone = request.getParameter("phone");
            String isAdminStr = request.getParameter("isAdmin");

            if (username == null || username.trim().isEmpty()) {
                request.setAttribute("errorMessage", "Username is required");
                request.setAttribute("user", user);
                request.setAttribute("editMode", true);
                request.getRequestDispatcher("/admin-user-form.jsp").forward(request, response);
                return;
            }

            if (!username.equals(user.getUsername())) {
                User existingUser = userDAO.findByUsername(username);
                if (existingUser != null) {
                    request.setAttribute("errorMessage", "Username already exists");
                    request.setAttribute("user", user);
                    request.setAttribute("editMode", true);
                    request.getRequestDispatcher("/admin-user-form.jsp").forward(request, response);
                    return;
                }
            }

            if (email != null && !email.trim().isEmpty() && !email.equals(user.getEmail())) {
                User userWithEmail = userDAO.findByEmail(email);
                if (userWithEmail != null && !userWithEmail.getId().equals(userId)) {
                    request.setAttribute("errorMessage", "Email already in use");
                    request.setAttribute("user", user);
                    request.setAttribute("editMode", true);
                    request.getRequestDispatcher("/admin-user-form.jsp").forward(request, response);
                    return;
                }
            }

            user.setUsername(username);

            if (password != null && !password.trim().isEmpty()) {
                user.setPassword(PasswordHasher.hashPassword(password));
            }

            user.setEmail(email);
            user.setPhone(phone);
            user.setAdmin("on".equals(isAdminStr) || "true".equals(isAdminStr));

            boolean success = userDAO.update(user);

            if (success) {
                request.setAttribute("successMessage", "User updated successfully");
                response.sendRedirect(request.getContextPath() + "/admin/users/view?id=" + userId);
            } else {
                request.setAttribute("errorMessage", "Failed to update user");
                request.setAttribute("user", user);
                request.setAttribute("editMode", true);
                request.getRequestDispatcher("/admin-user-form.jsp").forward(request, response);
            }
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error updating user: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/admin/users");
        }
    }

    private void toggleAdminStatus(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String userId = request.getParameter("userId");
        String isAdminStr = request.getParameter("isAdmin");

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

            boolean isAdmin = "on".equals(isAdminStr) || "true".equals(isAdminStr);
            user.setAdmin(isAdmin);

            boolean success = userDAO.update(user);

            if (success) {
                request.setAttribute("successMessage",
                        isAdmin ? "User is now an administrator" : "Administrator privileges revoked");
            } else {
                request.setAttribute("errorMessage", "Failed to update user admin status");
            }

            response.sendRedirect(request.getContextPath() + "/admin/users");
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error updating user admin status: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/admin/users");
        }
    }

    private void deleteUser(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String userId = request.getParameter("userId");
        if (userId == null || userId.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/users");
            return;
        }

        try {
            System.out.println("Attempting to delete user with ID: " + userId);

            List<Reservation> userReservations = reservationDAO.findByUserId(userId);

            System.out.println("User has " + (userReservations != null ? userReservations.size() : 0) + " reservations");

            if (userReservations != null && !userReservations.isEmpty()) {
                // Debug log
                System.out.println("Cannot delete - user has active reservations");

                request.setAttribute("errorMessage",
                        "Cannot delete user with active reservations. Please cancel or delete all user reservations first.");
                response.sendRedirect(request.getContextPath() + "/admin/users/view?id=" + userId);
                return;
            }

            System.out.println("Proceeding with user deletion");

            boolean success = userDAO.delete(userId);

            System.out.println("User deletion result: " + success);

            if (success) {
                request.setAttribute("successMessage", "User deleted successfully");
                response.sendRedirect(request.getContextPath() + "/admin/users");
            } else {
                request.setAttribute("errorMessage", "Failed to delete user");
                response.sendRedirect(request.getContextPath() + "/admin/users/view?id=" + userId);
            }
        } catch (Exception e) {
            System.err.println("Error deleting user: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("errorMessage", "Error deleting user: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/admin/users");
        }
    }

}
