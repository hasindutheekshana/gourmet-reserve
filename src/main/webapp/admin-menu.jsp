<%@ include file="admin-header.jsp" %>
<%@ page import="java.util.List" %>
<%@ page import="com.tablebooknow.model.menu.MenuItem" %>

<h1 style="color: var(--gold); margin-bottom: 2rem;">Menu Management</h1>

<div style="display: flex; gap: 1rem; margin-bottom: 2rem;">
    <a href="${pageContext.request.contextPath}/admin/menu/create" class="action-btn edit-btn" style="display: flex; align-items: center; justify-content: center; padding: 1rem; background-color: #4CAF50;">
        <span style="margin-right: 0.5rem;">➕</span> Add New Menu Item
    </a>
</div>

<div class="card" style="margin-bottom: 1rem;">
    <h3 style="margin-bottom: 1rem;">Filter Menu Items</h3>
    <form method="get" action="${pageContext.request.contextPath}/admin/menu" style="display: flex; gap: 1rem; align-items: center;">
        <div style="flex: 1;">
            <label for="category" style="display: block; margin-bottom: 0.5rem;">By Category:</label>
            <select id="category" name="category" style="width: 100%; padding: 0.5rem; border-radius: 5px; background: rgba(255, 255, 255, 0.1); color: var(--text); border: 1px solid rgba(212, 175, 55, 0.3);">
                <option value="">All Categories</option>
                <option value="appetizer" <%= "appetizer".equals(request.getAttribute("categoryFilter")) ? "selected" : "" %>>Appetizers</option>
                <option value="main" <%= "main".equals(request.getAttribute("categoryFilter")) ? "selected" : "" %>>Main Courses</option>
                <option value="dessert" <%= "dessert".equals(request.getAttribute("categoryFilter")) ? "selected" : "" %>>Desserts</option>
                <option value="drink" <%= "drink".equals(request.getAttribute("categoryFilter")) ? "selected" : "" %>>Drinks</option>
            </select>
        </div>
        <div style="align-self: flex-end;">
            <button type="submit" class="action-btn edit-btn">Apply Filter</button>
            <a href="${pageContext.request.contextPath}/admin/menu" class="action-btn edit-btn" style="background-color: #607D8B;">Reset</a>
        </div>
    </form>
</div>

<div class="card">
    <table class="data-table">
        <thead>
            <tr>
                <th>Name</th>
                <th>Category</th>
                <th>Price</th>
                <th>Status</th>
                <th>Actions</th>
            </tr>
        </thead>
        <tbody>
            <%
            List<MenuItem> menuItems = (List<MenuItem>) request.getAttribute("menuItems");
            if (menuItems != null && !menuItems.isEmpty()) {
                for (MenuItem menuItem : menuItems) {
                    String statusColor = menuItem.isAvailable() ? "#4CAF50" : "#F44336";
                    String statusText = menuItem.isAvailable() ? "Available" : "Unavailable";
            %>
            <tr>
                <td><%= menuItem.getName() %></td>
                <td style="text-transform: capitalize;"><%= menuItem.getCategory() %></td>
                <td>$<%= menuItem.getPrice() %></td>
                <td><span style="color: <%= statusColor %>;"><%= statusText %></span></td>
                <td style="display: flex; gap: 5px;">
                    <a href="${pageContext.request.contextPath}/admin/menu/view?id=<%= menuItem.getId() %>" class="action-btn edit-btn" style="padding: 0.3rem 0.7rem;">View</a>
                    <a href="${pageContext.request.contextPath}/admin/menu/edit?id=<%= menuItem.getId() %>" class="action-btn edit-btn" style="padding: 0.3rem 0.7rem; background-color: #2196F3;">Edit</a>

                    <form method="post" action="${pageContext.request.contextPath}/admin/menu/toggle-availability" style="display: inline;">
                        <input type="hidden" name="menuItemId" value="<%= menuItem.getId() %>">
                        <button type="submit" class="action-btn edit-btn" style="padding: 0.3rem 0.7rem; background-color: <%= menuItem.isAvailable() ? "#FF9800" : "#4CAF50" %>;">
                            <%= menuItem.isAvailable() ? "Disable" : "Enable" %>
                        </button>
                    </form>

                    <form method="post" action="${pageContext.request.contextPath}/admin/menu/delete" style="display: inline;">
                        <input type="hidden" name="menuItemId" value="<%= menuItem.getId() %>">
                        <button type="submit" class="action-btn delete-btn" style="padding: 0.3rem 0.7rem;" onclick="return confirm('Are you sure you want to delete this menu item? This action cannot be undone.')">Delete</button>
                    </form>
                </td>
            </tr>
            <%
                }
            } else {
            %>
            <tr>
                <td colspan="5" style="text-align: center;">No menu items found</td>
            </tr>
            <% } %>
        </tbody>
    </table>
</div>

<%@ include file="admin-footer.jsp" %>