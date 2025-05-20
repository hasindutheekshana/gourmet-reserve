<%@ include file="admin-header.jsp" %>

<%
    User viewUser = (User) request.getAttribute("user");
    List<Reservation> userReservations = (List<Reservation>) request.getAttribute("userReservations");

    if (viewUser == null) {
        // Handle case where user is not found
%>
    <div class="message error-message">
        User not found. They may have been deleted or the ID is invalid.
    </div>
    <p><a href="${pageContext.request.contextPath}/admin/users" class="action-btn edit-btn">Back to Users</a></p>
<%
    } else {
%>

<h1 style="color: var(--gold); margin-bottom: 2rem;">User Details</h1>

<div class="card" style="margin-bottom: 2rem;">
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 2rem;">
        <h2>User Information</h2>
        <span class="user-status" style="font-size: 1.1rem; padding: 0.5rem 1rem; border-radius: 5px; background: <%= viewUser.isAdmin() ? "rgba(33, 150, 243, 0.2)" : "rgba(76, 175, 80, 0.2)" %>; color: <%= viewUser.isAdmin() ? "#2196F3" : "#4CAF50" %>;">
            <%= viewUser.isAdmin() ? "Administrator" : "Regular User" %>
        </span>
    </div>

    <div class="details-grid" style="display: grid; grid-template-columns: 1fr 1fr; gap: 2rem; margin-bottom: 2rem;">
        <div>
            <p><strong>User ID:</strong> <%= viewUser.getId() %></p>
            <p><strong>Username:</strong> <%= viewUser.getUsername() %></p>
        </div>
        <div>
            <p><strong>Email:</strong> <%= viewUser.getEmail() != null ? viewUser.getEmail() : "N/A" %></p>
            <p><strong>Phone:</strong> <%= viewUser.getPhone() != null ? viewUser.getPhone() : "N/A" %></p>
        </div>
    </div>
</div>

<% if (userReservations != null && !userReservations.isEmpty()) { %>
<div class="card" style="margin-bottom: 2rem;">
    <h2 style="margin-bottom: 1.5rem;">User Reservations</h2>
    <table class="data-table">
        <thead>
            <tr>
                <th>Reservation ID</th>
                <th>Date</th>
                <th>Time</th>
                <th>Table</th>
                <th>Status</th>
                <th>Actions</th>
            </tr>
        </thead>
        <tbody>
            <%
            for (Reservation reservation : userReservations) {
                // Determine status color
                String statusColor = "#fff";
                if ("confirmed".equals(reservation.getStatus())) {
                    statusColor = "#4CAF50"; // Green for confirmed
                } else if ("pending".equals(reservation.getStatus())) {
                    statusColor = "#FFC107"; // Yellow for pending
                } else if ("cancelled".equals(reservation.getStatus())) {
                    statusColor = "#F44336"; // Red for cancelled
                }
            %>
            <tr>
                <td><%= reservation.getId() %></td>
                <td><%= reservation.getReservationDate() %></td>
                <td><%= reservation.getReservationTime() %></td>
                <td><%= reservation.getTableId() %></td>
                <td><span style="color: <%= statusColor %>;"><%= reservation.getStatus() %></span></td>
                <td>
                    <a href="${pageContext.request.contextPath}/admin/reservations/view?id=<%= reservation.getId() %>" class="action-btn edit-btn">View</a>
                </td>
            </tr>
            <% } %>
        </tbody>
    </table>

    <div class="message warning-message" style="margin-top: 1.5rem;">
        <strong>Note:</strong> This user has active reservations. You must cancel or delete all reservations before deleting this user.
    </div>
</div>
<% } else { %>
<div class="card" style="margin-bottom: 2rem;">
    <h2 style="margin-bottom: 1rem;">User Reservations</h2>
    <p style="text-align: center; padding: 2rem;">This user has no reservations</p>
</div>
<% } %>

<div class="actions" style="display: flex; gap: 1rem; margin-bottom: 2rem;">
    <a href="${pageContext.request.contextPath}/admin/users" class="action-btn edit-btn">Back to List</a>

    <a href="${pageContext.request.contextPath}/admin/users/edit?id=<%= viewUser.getId() %>" class="action-btn edit-btn" style="background-color: #2196F3;">Edit User</a>

    <% if (userReservations == null || userReservations.isEmpty()) { %>
    <form method="post" action="${pageContext.request.contextPath}/admin/users/delete">
        <input type="hidden" name="userId" value="<%= viewUser.getId() %>">
        <button type="submit" class="action-btn delete-btn" style="background-color: #d32f2f;"
                onclick="return confirm('Are you sure you want to delete this user? This action cannot be undone.');">Delete User</button>
    </form>
    <% } else { %>
    <button type="button" class="action-btn delete-btn" style="background-color: #d32f2f; opacity: 0.6; cursor: not-allowed;"
            onclick="alert('Cannot delete user with active reservations. Please cancel or delete all reservations first.')">Delete User</button>
    <% } %>
</div>

<% } %>

<%@ include file="admin-footer.jsp" %>