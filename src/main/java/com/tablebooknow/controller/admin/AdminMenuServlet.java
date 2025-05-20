package com.tablebooknow.controller.admin;

import com.tablebooknow.dao.MenuItemDAO;
import com.tablebooknow.model.menu.MenuItem;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;


@WebServlet("/admin/menu/*")
public class AdminMenuServlet extends HttpServlet {
    private MenuItemDAO menuItemDAO;

    @Override
    public void init() throws ServletException {
        menuItemDAO = new MenuItemDAO();
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
            listAllMenuItems(request, response);
            return;
        }

        switch (pathInfo) {
            case "/view":
                viewMenuItem(request, response);
                break;
            case "/create":
                showCreateForm(request, response);
                break;
            case "/edit":
                showEditForm(request, response);
                break;
            default:
                listAllMenuItems(request, response);
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
            response.sendRedirect(request.getContextPath() + "/admin/menu");
            return;
        }

        switch (pathInfo) {
            case "/create":
                createMenuItem(request, response);
                break;
            case "/update":
                updateMenuItem(request, response);
                break;
            case "/delete":
                deleteMenuItem(request, response);
                break;
            case "/toggle-availability":
                toggleAvailability(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/admin/menu");
                break;
        }
    }

    private void listAllMenuItems(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String categoryFilter = request.getParameter("category");

            List<MenuItem> menuItems;

            if (categoryFilter == null || categoryFilter.isEmpty()) {
                menuItems = menuItemDAO.findAll();
            } else {
                menuItems = menuItemDAO.findByCategory(categoryFilter);
            }

            request.setAttribute("menuItems", menuItems);
            request.setAttribute("categoryFilter", categoryFilter);

            request.getRequestDispatcher("/admin-menu.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error loading menu items: " + e.getMessage());
            request.getRequestDispatcher("/admin-menu.jsp").forward(request, response);
        }
    }

    private void viewMenuItem(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String id = request.getParameter("id");
        if (id == null || id.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/menu");
            return;
        }

        try {
            MenuItem menuItem = menuItemDAO.findById(id);
            if (menuItem == null) {
                request.setAttribute("errorMessage", "Menu item not found");
                response.sendRedirect(request.getContextPath() + "/admin/menu");
                return;
            }

            request.setAttribute("menuItem", menuItem);
            request.getRequestDispatcher("/admin-menu-details.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error loading menu item: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/admin/menu");
        }
    }

    private void showCreateForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/admin-menu-create.jsp").forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String id = request.getParameter("id");
        if (id == null || id.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/menu");
            return;
        }

        try {
            MenuItem menuItem = menuItemDAO.findById(id);
            if (menuItem == null) {
                request.setAttribute("errorMessage", "Menu item not found");
                response.sendRedirect(request.getContextPath() + "/admin/menu");
                return;
            }

            request.setAttribute("menuItem", menuItem);
            request.getRequestDispatcher("/admin-menu-edit.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error loading menu item for editing: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/admin/menu");
        }
    }

    private void createMenuItem(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String name = request.getParameter("name");
            String description = request.getParameter("description");
            String priceStr = request.getParameter("price");
            String category = request.getParameter("category");
            String imageUrl = request.getParameter("imageUrl");
            String availableStr = request.getParameter("available");

            if (name == null || name.trim().isEmpty() ||
                    priceStr == null || priceStr.trim().isEmpty() ||
                    category == null || category.trim().isEmpty()) {

                request.setAttribute("errorMessage", "Name, price, and category are required fields");
                request.getRequestDispatcher("/admin-menu-create.jsp").forward(request, response);
                return;
            }

            BigDecimal price;
            try {
                price = new BigDecimal(priceStr);
                if (price.compareTo(BigDecimal.ZERO) < 0) {
                    throw new NumberFormatException("Price cannot be negative");
                }
            } catch (NumberFormatException e) {
                request.setAttribute("errorMessage", "Invalid price format: " + e.getMessage());
                request.getRequestDispatcher("/admin-menu-create.jsp").forward(request, response);
                return;
            }

            boolean available = availableStr != null && "on".equals(availableStr);

            MenuItem menuItem = new MenuItem();
            menuItem.setName(name);
            menuItem.setDescription(description);
            menuItem.setPrice(price);
            menuItem.setCategory(category);
            menuItem.setAvailable(available);
            menuItem.setImageUrl(imageUrl);

            menuItemDAO.create(menuItem);

            request.getSession().setAttribute("successMessage", "Menu item added successfully");
            response.sendRedirect(request.getContextPath() + "/admin/menu");
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error creating menu item: " + e.getMessage());
            request.getRequestDispatcher("/admin-menu-create.jsp").forward(request, response);
        }
    }

    private void updateMenuItem(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String menuItemId = request.getParameter("menuItemId");
        if (menuItemId == null || menuItemId.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/menu");
            return;
        }

        try {
            MenuItem menuItem = menuItemDAO.findById(menuItemId);
            if (menuItem == null) {
                request.setAttribute("errorMessage", "Menu item not found");
                response.sendRedirect(request.getContextPath() + "/admin/menu");
                return;
            }

            String name = request.getParameter("name");
            String description = request.getParameter("description");
            String priceStr = request.getParameter("price");
            String category = request.getParameter("category");
            String imageUrl = request.getParameter("imageUrl");
            String availableStr = request.getParameter("available");

            if (name == null || name.trim().isEmpty() ||
                    priceStr == null || priceStr.trim().isEmpty() ||
                    category == null || category.trim().isEmpty()) {

                request.setAttribute("errorMessage", "Name, price, and category are required fields");
                request.setAttribute("menuItem", menuItem);
                request.getRequestDispatcher("/admin-menu-edit.jsp").forward(request, response);
                return;
            }

            BigDecimal price;
            try {
                price = new BigDecimal(priceStr);
                if (price.compareTo(BigDecimal.ZERO) < 0) {
                    throw new NumberFormatException("Price cannot be negative");
                }
            } catch (NumberFormatException e) {
                request.setAttribute("errorMessage", "Invalid price format: " + e.getMessage());
                request.setAttribute("menuItem", menuItem);
                request.getRequestDispatcher("/admin-menu-edit.jsp").forward(request, response);
                return;
            }

            boolean available = availableStr != null && "on".equals(availableStr);

            menuItem.setName(name);
            menuItem.setDescription(description);
            menuItem.setPrice(price);
            menuItem.setCategory(category);
            menuItem.setAvailable(available);
            menuItem.setImageUrl(imageUrl);

            boolean success = menuItemDAO.update(menuItem);

            if (success) {
                request.getSession().setAttribute("successMessage", "Menu item updated successfully");
                response.sendRedirect(request.getContextPath() + "/admin/menu/view?id=" + menuItemId);
            } else {
                request.setAttribute("errorMessage", "Failed to update menu item");
                request.setAttribute("menuItem", menuItem);
                request.getRequestDispatcher("/admin-menu-edit.jsp").forward(request, response);
            }
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error updating menu item: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/admin/menu");
        }
    }

    private void deleteMenuItem(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String menuItemId = request.getParameter("menuItemId");
        if (menuItemId == null || menuItemId.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/menu");
            return;
        }

        try {
            boolean success = menuItemDAO.delete(menuItemId);

            if (success) {
                request.getSession().setAttribute("successMessage", "Menu item deleted successfully");
            } else {
                request.getSession().setAttribute("errorMessage", "Failed to delete menu item");
            }

            response.sendRedirect(request.getContextPath() + "/admin/menu");
        } catch (Exception e) {
            request.getSession().setAttribute("errorMessage", "Error deleting menu item: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/admin/menu");
        }
    }

    private void toggleAvailability(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String menuItemId = request.getParameter("menuItemId");
        if (menuItemId == null || menuItemId.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/menu");
            return;
        }

        try {
            MenuItem menuItem = menuItemDAO.findById(menuItemId);
            if (menuItem == null) {
                request.getSession().setAttribute("errorMessage", "Menu item not found");
                response.sendRedirect(request.getContextPath() + "/admin/menu");
                return;
            }

            menuItem.setAvailable(!menuItem.isAvailable());

            boolean success = menuItemDAO.update(menuItem);

            if (success) {
                String status = menuItem.isAvailable() ? "available" : "unavailable";
                request.getSession().setAttribute("successMessage",
                        "Menu item \"" + menuItem.getName() + "\" is now " + status);
            } else {
                request.getSession().setAttribute("errorMessage", "Failed to toggle menu item availability");
            }

            String referer = request.getHeader("Referer");
            if (referer != null && !referer.isEmpty()) {
                response.sendRedirect(referer);
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/menu");
            }
        } catch (Exception e) {
            request.getSession().setAttribute("errorMessage", "Error updating menu item: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/admin/menu");
        }
    }
}