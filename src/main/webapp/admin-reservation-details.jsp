<%@ include file="admin-header.jsp" %>

<%
    Reservation reservation = (Reservation) request.getAttribute("reservation");
    User user = (User) request.getAttribute("user");

    if (reservation == null) {
        // Handle case where reservation is not found
%>
    <div class="message error-message">
        Reservation not found. It may have been deleted or the ID is invalid.
    </div>
    <p><a href="${pageContext.request.contextPath}/admin/reservations" class="action-btn edit-btn">Back to Reservations</a></p>
<%
    } else {
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

<h1 style="color: var(--gold); margin-bottom: 2rem;">Reservation Details</h1>

<div class="card" style="margin-bottom: 2rem;">
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 2rem;">
        <h2>Reservation Information</h2>
        <span class="reservation-status" style="font-size: 1.1rem; color: <%= statusColor %>; padding: 0.5rem 1rem; border-radius: 5px; background: rgba(0,0,0,0.2);">
            <%= reservation.getStatus().toUpperCase() %>
        </span>
    </div>

    <div class="details-grid" style="display: grid; grid-template-columns: 1fr 1fr; gap: 2rem; margin-bottom: 2rem;">
        <div>
            <p><strong>Reservation ID:</strong> <%= reservation.getId() %></p>
            <p><strong>Date:</strong> <%= reservation.getReservationDate() %></p>
            <p><strong>Time:</strong> <%= reservation.getReservationTime() %></p>
            <p><strong>Duration:</strong> <%= reservation.getDuration() %> hours</p>
            <p><strong>Table ID:</strong> <%= reservation.getTableId() %></p>
        </div>
        <div>
            <p><strong>Booking Type:</strong> <%= reservation.getBookingType() %></p>
            <p><strong>Created At:</strong> <%= reservation.getCreatedAt() %></p>
            <p><strong>Status:</strong> <span style="color: <%= statusColor %>;"><%= reservation.getStatus() %></span></p>
        </div>
    </div>

    <div class="special-requests" style="margin-bottom: 2rem;">
        <h3 style="margin-bottom: 0.5rem;">Special Requests</h3>
        <div style="background: rgba(0,0,0,0.2); padding: 1rem; border-radius: 5px;">
            <%= reservation.getSpecialRequests() != null && !reservation.getSpecialRequests().isEmpty() ? reservation.getSpecialRequests() : "No special requests" %>
        </div>
    </div>
</div>

<% if (user != null) { %>
<div class="card" style="margin-bottom: 2rem;">
    <h2 style="margin-bottom: 1.5rem;">Customer Information</h2>
    <div class="details-grid" style="display: grid; grid-template-columns: 1fr 1fr; gap: 2rem;">
        <div>
            <p><strong>User ID:</strong> <%= user.getId() %></p>
            <p><strong>Username:</strong> <%= user.getUsername() %></p>
        </div>
        <div>
            <p><strong>Email:</strong> <%= user.getEmail() != null ? user.getEmail() : "N/A" %></p>
            <p><strong>Phone:</strong> <%= user.getPhone() != null ? user.getPhone() : "N/A" %></p>
        </div>
    </div>
</div>
<% } else { %>
<div class="card" style="margin-bottom: 2rem;">
    <h2 style="margin-bottom: 1rem;">Customer Information</h2>
    <p>User information not available.</p>
</div>
<% } %>

<div class="actions" style="display: flex; gap: 1rem; margin-bottom: 2rem;">
    <a href="${pageContext.request.contextPath}/admin/reservations" class="action-btn edit-btn">Back to List</a>

    <a href="${pageContext.request.contextPath}/admin/reservations/edit?id=<%= reservation.getId() %>" class="action-btn edit-btn" style="background-color: #2196F3;">Edit Reservation</a>

    <% if (!"cancelled".equals(reservation.getStatus())) { %>
    <form method="post" action="${pageContext.request.contextPath}/admin/reservations/cancel">
        <input type="hidden" name="reservationId" value="<%= reservation.getId() %>">
        <button type="submit" class="action-btn delete-btn" onclick="return confirm('Are you sure you want to cancel this reservation?')">Cancel Reservation</button>
    </form>
    <% } %>

    <form method="post" action="${pageContext.request.contextPath}/admin/reservations/delete">
        <input type="hidden" name="reservationId" value="<%= reservation.getId() %>">
        <button type="submit" class="action-btn delete-btn" style="background-color: #d32f2f;" onclick="return confirm('Are you sure you want to permanently delete this reservation? This action cannot be undone.')">Delete Permanently</button>
    </form>
</div>

<% } %>

<%@ include file="admin-footer.jsp" %>