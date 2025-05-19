<%@ include file="admin-header.jsp" %>

<h1 style="color: var(--gold); margin-bottom: 2rem;">Reservation Management</h1>

<div style="display: flex; gap: 1rem; margin-bottom: 2rem;">
    <a href="${pageContext.request.contextPath}/admin/reservations/queue" class="action-btn edit-btn" style="display: flex; align-items: center; padding: 0.75rem 1.5rem;">
        <span style="margin-right: 0.5rem;"></span> Manage Reservation Queue
    </a>
    <a href="${pageContext.request.contextPath}/admin/reservations/sorted" class="action-btn edit-btn" style="display: flex; align-items: center; padding: 0.75rem 1.5rem;">
        <span style="margin-right: 0.5rem;"></span> View Reservations Sorted by Time
    </a>
</div>

<div class="card">
    <div style="margin-bottom: 1rem;">
        <h3>Filter Reservations</h3>
        <form method="get" action="${pageContext.request.contextPath}/admin/reservations" style="display: flex; gap: 1rem; margin-top: 1rem;">
            <select name="status" style="padding: 0.5rem; border-radius: 5px; background: rgba(255, 255, 255, 0.1); color: var(--text); border: 1px solid rgba(212, 175, 55, 0.3);">
                <option value="">All Statuses</option>
                <option value="confirmed" <%= "confirmed".equals(request.getAttribute("statusFilter")) ? "selected" : "" %>>Confirmed</option>
                <option value="pending" <%= "pending".equals(request.getAttribute("statusFilter")) ? "selected" : "" %>>Pending</option>
                <option value="cancelled" <%= "cancelled".equals(request.getAttribute("statusFilter")) ? "selected" : "" %>>Cancelled</option>
            </select>
            <input type="date" name="date" value="<%= request.getAttribute("dateFilter") != null ? request.getAttribute("dateFilter") : "" %>" style="padding: 0.5rem; border-radius: 5px; background: rgba(255, 255, 255, 0.1); color: var(--text); border: 1px solid rgba(212, 175, 55, 0.3);">
            <button type="submit" class="action-btn edit-btn">Filter</button>
            <button type="button" class="action-btn edit-btn" onclick="location.href='${pageContext.request.contextPath}/admin/reservations'">Reset</button>
        </form>
    </div>

    <% if ("true".equals(request.getAttribute("sortedByTime"))) { %>
    <div class="message success-message" style="margin-top: 1rem;">
        Reservations are currently sorted by date and time
    </div>
    <% } %>
    <table class="data-table">
        <thead>
            <tr>
                <th>Reservation ID</th>
                <th>User ID</th>
                <th>Date</th>
                <th>Time</th>
                <th>Table</th>
                <th>Status</th>
                <th>Actions</th>
            </tr>
        </thead>
        <tbody>
            <%
            List<Reservation> allReservations = (List<Reservation>) request.getAttribute("reservations");
            if (allReservations != null && !allReservations.isEmpty()) {
                for (Reservation reservation : allReservations) {
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
                <td><%= reservation.getUserId() %></td>
                <td><%= reservation.getReservationDate() %></td>
                <td><%= reservation.getReservationTime() %></td>
                <td><%= reservation.getTableId() %></td>
                <td><span style="color: <%= statusColor %>;"><%= reservation.getStatus() %></span></td>
                <td>
                    <button class="action-btn edit-btn" onclick="location.href='${pageContext.request.contextPath}/admin/reservations/view?id=<%= reservation.getId() %>'">View</button>

                    <button class="action-btn edit-btn" style="background-color: #2196F3;" onclick="location.href='${pageContext.request.contextPath}/admin/reservations/edit?id=<%= reservation.getId() %>'">Edit</button>

                    <% if (!"cancelled".equals(reservation.getStatus())) { %>
                    <form style="display: inline;" method="post" action="${pageContext.request.contextPath}/admin/reservations/cancel">
                        <input type="hidden" name="reservationId" value="<%= reservation.getId() %>">
                        <button type="submit" class="action-btn delete-btn" onclick="return confirm('Are you sure you want to cancel this reservation?')">Cancel</button>
                    </form>
                    <% } %>

                    <form style="display: inline;" method="post" action="${pageContext.request.contextPath}/admin/reservations/delete">
                        <input type="hidden" name="reservationId" value="<%= reservation.getId() %>">
                        <button type="submit" class="action-btn delete-btn" style="background-color: #d32f2f;" onclick="return confirm('Are you sure you want to permanently delete this reservation? This action cannot be undone.')">Delete</button>
                    </form>
                </td>
            </tr>
            <%
                }
            } else {
            %>
            <tr>
                <td colspan="7" style="text-align: center;">No reservations found</td>
            </tr>
            <% } %>
        </tbody>
    </table>
</div>

<%@ include file="admin-footer.jsp" %>