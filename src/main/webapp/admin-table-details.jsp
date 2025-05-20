<%@ include file="admin-header.jsp" %>
<%@ page import="com.tablebooknow.model.table.Table" %>

<%
    Table viewTable = (Table) request.getAttribute("table");
    List<Reservation> tableReservations = (List<Reservation>) request.getAttribute("tableReservations");

    if (viewTable == null) {
        // Handle case where table is not found
%>
<div class="message error-message">
    Table not found. It may have been deleted or the ID is invalid.
</div>
<p><a href="${pageContext.request.contextPath}/admin/tables" class="action-btn edit-btn">Back to Tables</a></p>
<%
} else {
    // Determine badge color based on table type
    String badgeColor = "";
    if ("family".equalsIgnoreCase(viewTable.getTableType())) {
        badgeColor = "#FFC107";
    } else if ("luxury".equalsIgnoreCase(viewTable.getTableType())) {
        badgeColor = "#2196F3";
    } else if ("regular".equalsIgnoreCase(viewTable.getTableType())) {
        badgeColor = "#9C27B0";
    } else if ("couple".equalsIgnoreCase(viewTable.getTableType())) {
        badgeColor = "#FF5722";
    }
%>

<h1 style="color: var(--gold); margin-bottom: 2rem;">Table Details</h1>

<div class="card" style="margin-bottom: 2rem;">
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 2rem;">
        <h2><%= viewTable.getDisplayName() %></h2>
        <span class="table-status" style="font-size: 1.1rem; padding: 0.5rem 1rem; border-radius: 5px; background: <%= viewTable.isActive() ? "rgba(46, 204, 113, 0.2)" : "rgba(231, 76, 60, 0.2)" %>; color: <%= viewTable.isActive() ? "#2ecc71" : "#e74c3c" %>;">
            <%= viewTable.isActive() ? "ACTIVE" : "INACTIVE" %>
        </span>
    </div>

    <div class="details-grid" style="display: grid; grid-template-columns: 1fr 1fr; gap: 2rem; margin-bottom: 2rem;">
        <div>
            <p><strong>Table ID:</strong> <%= viewTable.getId() %></p>
            <p><strong>Table Number:</strong> <%= viewTable.getTableNumber() %></p>
            <p><strong>Type:</strong> <span style="background-color: <%= badgeColor %>; color: white; padding: 3px 8px; border-radius: 12px; font-size: 0.9rem;"><%= viewTable.getTableType() != null ? viewTable.getTableType().toUpperCase() : "UNKNOWN" %></span></p>
        </div>
        <div>
            <p><strong>Floor:</strong> <%= viewTable.getFloor() %></p>
            <p><strong>Capacity:</strong> <%= viewTable.getCapacity() %> seats</p>
            <p><strong>Status:</strong> <span style="color: <%= viewTable.isActive() ? "#2ecc71" : "#e74c3c" %>;"><%= viewTable.isActive() ? "Active" : "Inactive" %></span></p>
        </div>
    </div>

    <% if (viewTable.getLocationDescription() != null && !viewTable.getLocationDescription().isEmpty()) { %>
    <div class="location-description" style="margin-bottom: 2rem;">
        <h3 style="margin-bottom: 0.5rem; color: var(--gold);">Location Description</h3>
        <div style="background: rgba(0,0,0,0.2); padding: 1rem; border-radius: 5px;">
            <%= viewTable.getLocationDescription() %>
        </div>
    </div>
    <% } %>

    <div class="table-visualization" style="margin-bottom: 2rem; display: flex; justify-content: center;">
        <div style="position: relative; width: 200px; height: 200px; background: rgba(0,0,0,0.2); border-radius: 8px; display: flex; justify-content: center; align-items: center;">
            <%
                String tableColor = "#4a4a4a";
                String tableShape = "width: 120px; height: 90px;";

                if ("luxury".equalsIgnoreCase(viewTable.getTableType())) {
                    tableColor = "#6a6a6a";
                    tableShape = "width: 150px; height: 120px;";
                } else if ("couple".equalsIgnoreCase(viewTable.getTableType())) {
                    tableColor = "#505050";
                    tableShape = "width: 80px; height: 80px; border-radius: 50%;";
                }

                // Generate chairs based on table type and capacity
                int capacity = viewTable.getCapacity();
            %>

            <div style="position: relative;">
                <div style="<%= tableShape %> background: <%= tableColor %>; border-radius: 8px; display: flex; justify-content: center; align-items: center; color: white; font-weight: bold;">
                    <%= viewTable.getTableNumber() %>
                </div>

                <%
                    // Create chair positions based on capacity
                    for (int i = 0; i < capacity; i++) {
                        double angle = (2 * Math.PI / capacity) * i;
                        int chairX = (int) (100 + 80 * Math.cos(angle) - 10); // 10 is half chair size
                        int chairY = (int) (100 + 80 * Math.sin(angle) - 10);
                %>
                <div style="position: absolute; width: 20px; height: 20px; background: #333; border-radius: 50%; left: <%= chairX %>px; top: <%= chairY %>px;"></div>
                <% } %>
            </div>
        </div>
    </div>
</div>

<% if (tableReservations != null && !tableReservations.isEmpty()) { %>
<div class="card" style="margin-bottom: 2rem;">
    <h2 style="margin-bottom: 1.5rem; color: var(--gold);">Table Reservations</h2>
    <table class="data-table">
        <thead>
        <tr>
            <th>Reservation ID</th>
            <th>Date</th>
            <th>Time</th>
            <th>Duration</th>
            <th>Status</th>
            <th>Actions</th>
        </tr>
        </thead>
        <tbody>
        <%
            for (Reservation reservation : tableReservations) {
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
            <td><%= reservation.getDuration() %> hours</td>
            <td><span style="color: <%= statusColor %>;"><%= reservation.getStatus() %></span></td>
            <td>
                <a href="${pageContext.request.contextPath}/admin/reservations/view?id=<%= reservation.getId() %>" class="action-btn edit-btn">View</a>
            </td>
        </tr>
        <% } %>
        </tbody>
    </table>

    <div class="message warning-message" style="margin-top: 1.5rem;">
        <strong>Note:</strong> This table has active reservations. You must cancel or delete all reservations before deleting this table.
    </div>
</div>
<% } else { %>
<div class="card" style="margin-bottom: 2rem;">
    <h2 style="margin-bottom: 1rem; color: var(--gold);">Table Reservations</h2>
    <p style="text-align: center; padding: 2rem;">This table has no reservations.</p>
</div>
<% } %>

<div class="actions" style="display: flex; gap: 1rem; margin-bottom: 2rem;">
    <a href="${pageContext.request.contextPath}/admin/tables" class="action-btn edit-btn">Back to List</a>

    <a href="${pageContext.request.contextPath}/admin/tables/edit?id=<%= viewTable.getId() %>" class="action-btn edit-btn" style="background-color: #2196F3;">Edit Table</a>

    <% if (tableReservations == null || tableReservations.isEmpty()) { %>
    <form method="post" action="${pageContext.request.contextPath}/admin/tables/delete">
        <input type="hidden" name="tableId" value="<%= viewTable.getId() %>">
        <button type="submit" class="action-btn delete-btn" style="background-color: #d32f2f;"
                onclick="return confirm('Are you sure you want to delete this table? This action cannot be undone.');">Delete Table</button>
    </form>
    <% } else { %>
    <button type="button" class="action-btn delete-btn" style="background-color: #d32f2f; opacity: 0.6; cursor: not-allowed;"
            onclick="alert('Cannot delete table with active reservations. Please cancel or delete all reservations first.')">Delete Table</button>
    <% } %>
</div>

<% } %>

<%@ include file="admin-footer.jsp" %>
