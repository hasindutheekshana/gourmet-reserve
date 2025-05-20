<%@ page import="com.tablebooknow.model.user.User" %>
<%@ include file="admin-header.jsp" %>

<%
    User user = (User) request.getAttribute("user");
    boolean editMode = (Boolean) request.getAttribute("editMode");
    String formTitle = editMode ? "Edit User" : "Add New User";
    String formAction = editMode ?
                        request.getContextPath() + "/admin/users/update" :
                        request.getContextPath() + "/admin/users/create";
%>

<h1 style="color: var(--gold); margin-bottom: 2rem;"><%= formTitle %></h1>

<div class="card" style="max-width: 800px; margin: 0 auto;">
    <form id="userForm" method="post" action="<%= formAction %>" onsubmit="return validateForm()">
        <% if (editMode && user != null) { %>
            <input type="hidden" name="userId" value="<%= user.getId() %>">
        <% } %>

        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1.5rem;">
            <div class="form-group">
                <label for="username" style="display: block; margin-bottom: 0.5rem; color: var(--gold);">Username*</label>
                <input type="text" id="username" name="username" value="<%= editMode && user != null ? user.getUsername() : "" %>"
                       style="width: 100%; padding: 0.8rem; background: rgba(255, 255, 255, 0.1); border: 1px solid var(--gold); border-radius: 6px; color: var(--text); font-size: 1rem;" required>
            </div>

            <div class="form-group">
                <label for="password" style="display: block; margin-bottom: 0.5rem; color: var(--gold);"><%= editMode ? "Password (leave blank to keep current)" : "Password*" %></label>
                <input type="password" id="password" name="password"
                       style="width: 100%; padding: 0.8rem; background: rgba(255, 255, 255, 0.1); border: 1px solid var(--gold); border-radius: 6px; color: var(--text); font-size: 1rem;" <%= editMode ? "" : "required" %>>
            </div>

            <% if (!editMode) { %>
            <div class="form-group">
                <label for="confirmPassword" style="display: block; margin-bottom: 0.5rem; color: var(--gold);">Confirm Password*</label>
                <input type="password" id="confirmPassword" name="confirmPassword"
                       style="width: 100%; padding: 0.8rem; background: rgba(255, 255, 255, 0.1); border: 1px solid var(--gold); border-radius: 6px; color: var(--text); font-size: 1rem;" required>
            </div>
            <% } %>

            <div class="form-group">
                <label for="email" style="display: block; margin-bottom: 0.5rem; color: var(--gold);">Email</label>
                <input type="email" id="email" name="email" value="<%= editMode && user != null && user.getEmail() != null ? user.getEmail() : "" %>"
                       style="width: 100%; padding: 0.8rem; background: rgba(255, 255, 255, 0.1); border: 1px solid var(--gold); border-radius: 6px; color: var(--text); font-size: 1rem;">
            </div>

            <div class="form-group">
                <label for="phone" style="display: block; margin-bottom: 0.5rem; color: var(--gold);">Phone Number</label>
                <input type="tel" id="phone" name="phone" value="<%= editMode && user != null && user.getPhone() != null ? user.getPhone() : "" %>"
                       style="width: 100%; padding: 0.8rem; background: rgba(255, 255, 255, 0.1); border: 1px solid var(--gold); border-radius: 6px; color: var(--text); font-size: 1rem;">
            </div>

            <div class="form-group" style="display: flex; align-items: center;">
                <input type="checkbox" id="isAdmin" name="isAdmin" <%= editMode && user != null && user.isAdmin() ? "checked" : "" %>
                       style="width: 18px; height: 18px; margin-right: 10px;">
                <label for="isAdmin" style="color: var(--gold); cursor: pointer;">Admin Privileges</label>
            </div>
        </div>

        <div style="display: flex; gap: 1rem; margin-top: 2rem;">
            <button type="submit" class="action-btn edit-btn" style="flex: 1;">Save User</button>
            <a href="${pageContext.request.contextPath}/admin/users" class="action-btn delete-btn" style="flex: 1; text-align: center; text-decoration: none;">Cancel</a>
        </div>
    </form>
</div>

<script>
    function validateForm() {
        const password = document.getElementById('password').value;
        <% if (!editMode) { %>
        const confirmPassword = document.getElementById('confirmPassword').value;

        if (password !== confirmPassword) {
            alert('Passwords do not match');
            return false;
        }

        if (password.length < 6) {
            alert('Password must be at least 6 characters long');
            return false;
        }
        <% } else if (editMode) { %>
        // In edit mode, only validate password if it's provided
        if (password.trim() !== '' && password.length < 6) {
            alert('Password must be at least 6 characters long');
            return false;
        }
        <% } %>

        return true;
    }
</script>

<%@ include file="admin-footer.jsp" %>