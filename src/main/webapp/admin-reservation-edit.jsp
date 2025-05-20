<%@ include file="admin-header.jsp" %>

<%
    Reservation reservation = (Reservation) request.getAttribute("reservation");

    if (reservation == null) {
        // Handle case where reservation is not found
%>
    <div class="message error-message">
        Reservation not found. It may have been deleted or the ID is invalid.
    </div>
    <p><a href="${pageContext.request.contextPath}/admin/reservations" class="action-btn edit-btn">Back to Reservations</a></p>
<%
    } else {
%>

<h1 style="color: var(--gold); margin-bottom: 2rem;">Edit Reservation</h1>

<div class="card" style="margin-bottom: 2rem;">
    <form method="post" action="${pageContext.request.contextPath}/admin/reservations/update">
        <input type="hidden" name="reservationId" value="<%= reservation.getId() %>">

        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 2rem;">
            <div class="form-group">
                <label style="display: block; margin-bottom: 0.5rem; color: var(--gold);">Reservation Date</label>
                <input type="date" name="reservationDate" value="<%= reservation.getReservationDate() %>" style="width: 100%; padding: 0.8rem; background: rgba(255, 255, 255, 0.1); border: 1px solid var(--gold); border-radius: 6px; color: var(--text);">
            </div>

            <div class="form-group">
                <label style="display: block; margin-bottom: 0.5rem; color: var(--gold);">Reservation Time</label>
                <input type="time" name="reservationTime" value="<%= reservation.getReservationTime() %>" style="width: 100%; padding: 0.8rem; background: rgba(255, 255, 255, 0.1); border: 1px solid var(--gold); border-radius: 6px; color: var(--text);">
            </div>

            <div class="form-group">
                <label style="display: block; margin-bottom: 0.5rem; color: var(--gold);">Duration (hours)</label>
                <select name="duration" style="width: 100%; padding: 0.8rem; background: rgba(255, 255, 255, 0.1); border: 1px solid var(--gold); border-radius: 6px; color: var(--text);">
                    <% for (int i = 1; i <= 6; i++) { %>
                    <option value="<%= i %>" <%= (reservation.getDuration() == i) ? "selected" : "" %>><%= i %> hour<%= (i > 1) ? "s" : "" %></option>
                    <% } %>
                </select>
            </div>

            <div class="form-group">
                <label style="display: block; margin-bottom: 0.5rem; color: var(--gold);">Table ID</label>
                <input type="text" name="tableId" value="<%= reservation.getTableId() %>" style="width: 100%; padding: 0.8rem; background: rgba(255, 255, 255, 0.1); border: 1px solid var(--gold); border-radius: 6px; color: var(--text);">
            </div>

            <div class="form-group">
                <label style="display: block; margin-bottom: 0.5rem; color: var(--gold);">Status</label>
                <select name="status" style="width: 100%; padding: 0.8rem; background: rgba(255, 255, 255, 0.1); border: 1px solid var(--gold); border-radius: 6px; color: var(--text);">
                    <option value="pending" <%= "pending".equals(reservation.getStatus()) ? "selected" : "" %>>Pending</option>
                    <option value="confirmed" <%= "confirmed".equals(reservation.getStatus()) ? "selected" : "" %>>Confirmed</option>
                    <option value="cancelled" <%= "cancelled".equals(reservation.getStatus()) ? "selected" : "" %>>Cancelled</option>
                    <option value="completed" <%= "completed".equals(reservation.getStatus()) ? "selected" : "" %>>Completed</option>
                </select>
            </div>

            <div class="form-group">
                <label style="display: block; margin-bottom: 0.5rem; color: var(--gold);">Booking Type</label>
                <select name="bookingType" style="width: 100%; padding: 0.8rem; background: rgba(255, 255, 255, 0.1); border: 1px solid var(--gold); border-radius: 6px; color: var(--text);">
                    <option value="normal" <%= "normal".equals(reservation.getBookingType()) ? "selected" : "" %>>Normal</option>
                    <option value="special" <%= "special".equals(reservation.getBookingType()) ? "selected" : "" %>>Special</option>
                </select>
            </div>
        </div>

        <div class="form-group" style="margin-top: 1.5rem;">
            <label style="display: block; margin-bottom: 0.5rem; color: var(--gold);">Special Requests</label>
            <textarea name="specialRequests" style="width: 100%; padding: 0.8rem; background: rgba(255, 255, 255, 0.1); border: 1px solid var(--gold); border-radius: 6px; color: var(--text); min-height: 100px;"><%= reservation.getSpecialRequests() != null ? reservation.getSpecialRequests() : "" %></textarea>
        </div>

        <div style="display: flex; gap: 1rem; margin-top: 2rem;">
            <button type="submit" class="action-btn edit-btn" style="flex: 1;">Save Changes</button>
            <a href="${pageContext.request.contextPath}/admin/reservations/view?id=<%= reservation.getId() %>" class="action-btn delete-btn" style="flex: 1; text-align: center; text-decoration: none;">Cancel</a>
        </div>
    </form>
</div>

<% } %>

<%@ include file="admin-footer.jsp" %>