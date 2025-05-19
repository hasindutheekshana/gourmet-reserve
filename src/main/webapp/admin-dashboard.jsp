<%@ include file="admin-header.jsp" %>

<%
    // Get attributes specific to dashboard
    List<Reservation> upcomingReservations = (List<Reservation>) request.getAttribute("upcomingReservations");

        // Stats
        Integer totalUsers = (Integer) request.getAttribute("totalUsers");
        Integer totalReservations = (Integer) request.getAttribute("totalReservations");
        Integer pendingReservations = (Integer) request.getAttribute("pendingReservations");
        Integer confirmedReservations = (Integer) request.getAttribute("confirmedReservations");

        // Table stats
        Integer totalTables = (Integer) request.getAttribute("totalTables");
        Integer familyTables = (Integer) request.getAttribute("familyTables");
        Integer luxuryTables = (Integer) request.getAttribute("luxuryTables");
        Integer regularTables = (Integer) request.getAttribute("regularTables");
        Integer coupleTables = (Integer) request.getAttribute("coupleTables");

        // Format values or set defaults if null
        if (totalUsers == null) totalUsers = 0;
        if (totalReservations == null) totalReservations = 0;
        if (pendingReservations == null) pendingReservations = 0;
        if (confirmedReservations == null) confirmedReservations = 0;
        if (totalTables == null) totalTables = 0;
        if (familyTables == null) familyTables = 0;
        if (luxuryTables == null) luxuryTables = 0;
        if (regularTables == null) regularTables = 0;
        if (coupleTables == null) coupleTables = 0;
%>

<h1 style="color: var(--gold); margin-bottom: 2rem;">Dashboard Overview</h1>

<!-- Quick Access Buttons -->
<!-- Quick Access Buttons -->
<div style="display: flex; flex-wrap: wrap; gap: 1rem; margin-bottom: 2rem;">
    <a href="${pageContext.request.contextPath}/admin/reservations/queue" class="action-btn edit-btn" style="display: flex; align-items: center; justify-content: center; padding: 1rem; background-color: #4CAF50;">
        <span style="margin-right: 0.5rem;"></span> Reservation Queue
    </a>
    <a href="${pageContext.request.contextPath}/admin/reservations/sorted" class="action-btn edit-btn" style="display: flex; align-items: center; justify-content: center; padding: 1rem;">
        <span style="margin-right: 0.5rem;"></span> Sorted Reservations
    </a>
    <a href="${pageContext.request.contextPath}/admin/users" class="action-btn edit-btn" style="display: flex; align-items: center; justify-content: center; padding: 1rem;">
        <span style="margin-right: 0.5rem;"></span> Manage Users
    </a>
    <a href="${pageContext.request.contextPath}/admin/reviews" class="action-btn edit-btn" style="display: flex; align-items: center; justify-content: center; padding: 1rem; background-color: #9C27B0;">
        <span style="margin-right: 0.5rem;"></span> Manage Reviews
    </a>
    <a href="${pageContext.request.contextPath}/admin/tables" class="action-btn edit-btn" style="display: flex; align-items: center; justify-content: center; padding: 1rem; background-color: #FFC107;">
        <span style="margin-right: 0.5rem;"></span> Manage Tables
    </a>
    <a href="${pageContext.request.contextPath}/admin/qr" class="action-btn edit-btn" style="display: flex; align-items: center; justify-content: center; padding: 1rem;">
        <span style="margin-right: 0.5rem;"></span> QR Scanner
    </a>
</div>
<!-- Statistics Cards -->
<div class="stats-grid">
    <div class="stat-card">
        <div class="stat-number"><%= totalUsers %></div>
        <div class="stat-label">Total Users</div>
    </div>
    <div class="stat-card">
        <div class="stat-number"><%= totalReservations %></div>
        <div class="stat-label">Total Reservations</div>
    </div>
    <div class="stat-card">
        <div class="stat-number"><%= pendingReservations %></div>
        <div class="stat-label">Pending Reservations</div>
    </div>
    <div class="stat-card">
        <div class="stat-number"><%= confirmedReservations %></div>
        <div class="stat-label">Confirmed Reservations</div>
    </div>
    <div class="stat-card">
        <div class="stat-number"><%= totalTables %></div>
        <div class="stat-label">Total Tables</div>
    </div>
    <div class="stat-card">
        <div class="stat-number"><%= familyTables %></div>
        <div class="stat-label">Family Tables</div>
    </div>
</div>

<!-- Table Type Distribution Card -->
<div class="card" style="margin-bottom: 2rem;">
    <h3 style="margin-bottom: 1rem; color: var(--gold);">Table Distribution by Type</h3>
    <div style="display: flex; gap: 1rem; margin-top: 1rem;">
        <div style="flex: <%= familyTables %>; background-color: #FFC107; height: 30px; display: flex; justify-content: center; align-items: center; color: #333; font-weight: bold; min-width: 40px; border-radius: 4px;">
            <%= familyTables %>
        </div>
        <div style="flex: <%= luxuryTables %>; background-color: #2196F3; height: 30px; display: flex; justify-content: center; align-items: center; color: white; font-weight: bold; min-width: 40px; border-radius: 4px;">
            <%= luxuryTables %>
        </div>
        <div style="flex: <%= regularTables %>; background-color: #9C27B0; height: 30px; display: flex; justify-content: center; align-items: center; color: white; font-weight: bold; min-width: 40px; border-radius: 4px;">
            <%= regularTables %>
        </div>
        <div style="flex: <%= coupleTables %>; background-color: #FF5722; height: 30px; display: flex; justify-content: center; align-items: center; color: white; font-weight: bold; min-width: 40px; border-radius: 4px;">
            <%= coupleTables %>
        </div>
    </div>
    <div style="display: flex; gap: 1rem; margin-top: 0.5rem; justify-content: space-between;">
        <div style="display: flex; align-items: center;">
            <div style="width: 12px; height: 12px; background-color: #FFC107; margin-right: 5px; border-radius: 2px;"></div>
            <span>Family</span>
        </div>
        <div style="display: flex; align-items: center;">
            <div style="width: 12px; height: 12px; background-color: #2196F3; margin-right: 5px; border-radius: 2px;"></div>
            <span>Luxury</span>
        </div>
        <div style="display: flex; align-items: center;">
            <div style="width: 12px; height: 12px; background-color: #9C27B0; margin-right: 5px; border-radius: 2px;"></div>
            <span>Regular</span>
        </div>
        <div style="display: flex; align-items: center;">
            <div style="width: 12px; height: 12px; background-color: #FF5722; margin-right: 5px; border-radius: 2px;"></div>
            <span>Couple</span>
        </div>
    </div>
</div>

<!-- Upcoming Reservations -->
<div class="card">
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1rem;">
        <h2 style="color: var(--gold);">Today's Reservations</h2>
        <a href="${pageContext.request.contextPath}/admin/reservations" class="action-btn edit-btn" style="margin-bottom: 0;">View All Reservations</a>
    </div>
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
            if (upcomingReservations != null && !upcomingReservations.isEmpty()) {
                for (Reservation reservation : upcomingReservations) {
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
                    <button class="action-btn edit-btn" onclick="location.href='${pageContext.request.contextPath}/admin/reservations/view?id=<%= reservation.getId() %>'">View</button>

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
                <td colspan="6" style="text-align: center;">No upcoming reservations found</td>
            </tr>
            <% } %>
        </tbody>
    </table>
</div>

<!-- Recent Activities Section -->
<div class="card">
    <h2 style="margin-bottom: 1rem; color: var(--gold);">System Status</h2>
    <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(300px, 1fr)); gap: 1rem;">
        <div style="padding: 1.5rem; background: rgba(255, 255, 255, 0.05); border-radius: 10px; text-align: center;">
            <h3 style="margin-bottom: 1rem;">Server Status</h3>
            <p style="font-size: 1.2rem; color: #4CAF50;">Running</p>
            <p style="font-size: 0.9rem; margin-top: 0.5rem;">Last checked: <%= new java.util.Date() %></p>
        </div>
        <div style="padding: 1.5rem; background: rgba(255, 255, 255, 0.05); border-radius: 10px; text-align: center;">
            <h3 style="margin-bottom: 1rem;">Database Status</h3>
            <p style="font-size: 1.2rem; color: #4CAF50;">Connected</p>
            <p style="font-size: 0.9rem; margin-top: 0.5rem;">File-based storage is active</p>
        </div>
    </div>
</div>

<%@ include file="admin-footer.jsp" %>