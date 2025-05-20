<%@ include file="admin-header.jsp" %>
<%@ page import="com.tablebooknow.model.menu.MenuItem" %>

<%
    MenuItem menuItem = (MenuItem) request.getAttribute("menuItem");

    if (menuItem == null) {
%>
    <div class="message error-message">
        Menu item not found. It may have been deleted or the ID is invalid.
    </div>
    <p><a href="${pageContext.request.contextPath}/admin/menu" class="action-btn edit-btn">Back to Menu Items</a></p>
<%
    } else {
%>

<h1 style="color: var(--gold); margin-bottom: 2rem;">Menu Item Details</h1>

<div class="card" style="margin-bottom: 2rem;">
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 2rem;">
        <h2><%= menuItem.getName() %></h2>
        <span class="reservation-status" style="font-size: 1.1rem; color: <%= menuItem.isAvailable() ? "#4CAF50" : "#F44336" %>; padding: 0.5rem 1rem; border-radius: 5px; background: rgba(0,0,0,0.2);">
            <%= menuItem.isAvailable() ? "AVAILABLE" : "UNAVAILABLE" %>
        </span>
    </div>

    <div class="details-grid" style="display: grid; grid-template-columns: 1fr 1fr; gap: 2rem; margin-bottom: 2rem;">
        <div>
            <p><strong>ID:</strong> <%= menuItem.getId() %></p>
            <p><strong>Category:</strong> <span style="text-transform: capitalize;"><%= menuItem.getCategory() %></span></p>
            <p><strong>Price:</strong> $<%= menuItem.getPrice() %></p>
        </div>
        <div>
            <p><strong>Availability:</strong> <span style="color: <%= menuItem.isAvailable() ? "#4CAF50" : "#F44336" %>;"><%= menuItem.isAvailable() ? "Available" : "Unavailable" %></span></p>
            <% if (menuItem.getImageUrl() != null && !menuItem.getImageUrl().isEmpty()) { %>
                <p><strong>Image URL:</strong> <%= menuItem.getImageUrl() %></p>
            <% } else { %>
                <p><strong>Image URL:</strong> Not provided</p>
            <% } %>
        </div>
    </div>

    <div class="description" style="margin-bottom: 2rem;">
        <h3 style="margin-bottom: 0.5rem;">Description</h3>
        <div style="background: rgba(0,0,0,0.2); padding: 1rem; border-radius: 5px;">
            <%= menuItem.getDescription() != null && !menuItem.getDescription().isEmpty() ? menuItem.getDescription() : "No description provided" %>
        </div>
    </div>
</div>

<div class="actions" style="display: flex; gap: 1rem; margin-bottom: 2rem;">
    <a href="${pageContext.request.contextPath}/admin/menu" class="action-btn edit-btn">Back to List</a>

    <a href="${pageContext.request.contextPath}/admin/menu/edit?id=<%= menuItem.getId() %>" class="action-btn edit-btn" style="background-color: #2196F3;">Edit Menu Item</a>

    <form method="post" action="${pageContext.request.contextPath}/admin/menu/toggle-availability">
        <input type="hidden" name="menuItemId" value="<%= menuItem.getId() %>">
        <button type="submit" class="action-btn edit-btn" style="background-color: <%= menuItem.isAvailable() ? "#FF9800" : "#4CAF50" %>;">
            <%= menuItem.isAvailable() ? "Mark as Unavailable" : "Mark as Available" %>
        </button>
    </form>

    <form method="post" action="${pageContext.request.contextPath}/admin/menu/delete">
        <input type="hidden" name="menuItemId" value="<%= menuItem.getId() %>">
        <button type="submit" class="action-btn delete-btn" style="background-color: #d32f2f;" onclick="return confirm('Are you sure you want to permanently delete this menu item? This action cannot be undone.')">Delete Permanently</button>
    </form>
</div>

<% } %>

<%@ include file="admin-footer.jsp" %>