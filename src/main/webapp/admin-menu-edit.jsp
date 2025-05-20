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

<h1 style="color: var(--gold); margin-bottom: 2rem;">Edit Menu Item</h1>

<div class="card" style="margin-bottom: 2rem;">
    <form method="post" action="${pageContext.request.contextPath}/admin/menu/update">
        <input type="hidden" name="menuItemId" value="<%= menuItem.getId() %>">

        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 2rem;">
            <div class="form-group">
                <label style="display: block; margin-bottom: 0.5rem; color: var(--gold);">Name *</label>
                <input type="text" name="name" value="<%= menuItem.getName() %>" required style="width: 100%; padding: 0.8rem; background: rgba(255, 255, 255, 0.1); border: 1px solid var(--gold); border-radius: 6px; color: var(--text);">
            </div>

            <div class="form-group">
                <label style="display: block; margin-bottom: 0.5rem; color: var(--gold);">Category *</label>
                <select name="category" required style="width: 100%; padding: 0.8rem; background: rgba(255, 255, 255, 0.1); border: 1px solid var(--gold); border-radius: 6px; color: var(--text);">
                    <option value="appetizer" <%= "appetizer".equals(menuItem.getCategory()) ? "selected" : "" %>>Appetizer</option>
                    <option value="main" <%= "main".equals(menuItem.getCategory()) ? "selected" : "" %>>Main Course</option>
                    <option value="dessert" <%= "dessert".equals(menuItem.getCategory()) ? "selected" : "" %>>Dessert</option>
                    <option value="drink" <%= "drink".equals(menuItem.getCategory()) ? "selected" : "" %>>Drink</option>
                </select>
            </div>

            <div class="form-group">
                <label style="display: block; margin-bottom: 0.5rem; color: var(--gold);">Price ($) *</label>
                <input type="number" name="price" step="0.01" min="0" value="<%= menuItem.getPrice() %>" required style="width: 100%; padding: 0.8rem; background: rgba(255, 255, 255, 0.1); border: 1px solid var(--gold); border-radius: 6px; color: var(--text);">
            </div>

            <div class="form-group">
                <label style="display: block; margin-bottom: 0.5rem; color: var(--gold);">Available</label>
                <div style="display: flex; align-items: center;">
                    <input type="checkbox" name="available" id="available" <%= menuItem.isAvailable() ? "checked" : "" %> style="margin-right: 0.5rem;">
                    <label for="available">Item is available for ordering</label>
                </div>
            </div>
        </div>

        <div class="form-group" style="margin-top: 1.5rem;">
            <label style="display: block; margin-bottom: 0.5rem; color: var(--gold);">Description</label>
            <textarea name="description" style="width: 100%; padding: 0.8rem; background: rgba(255, 255, 255, 0.1); border: 1px solid var(--gold); border-radius: 6px; color: var(--text); min-height: 100px;"><%= menuItem.getDescription() != null ? menuItem.getDescription() : "" %></textarea>
        </div>

        <div class="form-group">
            <label style="display: block; margin-bottom: 0.5rem; color: var(--gold);">Image URL (Optional)</label>
            <input type="url" name="imageUrl" value="<%= menuItem.getImageUrl() != null ? menuItem.getImageUrl() : "" %>" style="width: 100%; padding: 0.8rem; background: rgba(255, 255, 255, 0.1); border: 1px solid var(--gold); border-radius: 6px; color: var(--text);">
            <small style="color: #aaa; margin-top: 0.3rem; display: block;">Enter a URL for an image of this menu item (for future use)</small>
        </div>

        <div style="display: flex; gap: 1rem; margin-top: 2rem;">
            <button type="submit" class="action-btn edit-btn" style="flex: 1;">Save Changes</button>
            <a href="${pageContext.request.contextPath}/admin/menu/view?id=<%= menuItem.getId() %>" class="action-btn delete-btn" style="flex: 1; text-align: center; text-decoration: none;">Cancel</a>
        </div>
    </form>
</div>

<% } %>

<%@ include file="admin-footer.jsp" %>