<%@ include file="admin-header.jsp" %>

<h1 style="color: var(--gold); margin-bottom: 2rem;">Reservation Queue Management</h1>

<%
    List<Reservation> pendingReservations = (List<Reservation>) request.getAttribute("pendingReservations");
    Reservation nextPending = (Reservation) request.getAttribute("nextPending");
    String sorted = (String) request.getAttribute("sorted");
    boolean isSorted = "true".equals(sorted);
%>

<div class="card" style="margin-bottom: 2rem;">
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.5rem;">
        <h2>Queue Controls</h2>
        <div style="display: flex; gap: 1rem;">
            <a href="${pageContext.request.contextPath}/admin/reservations/queue?sort=time" class="action-btn edit-btn"
               style="<%= isSorted ? "background-color: #4CAF50;" : "" %>">
                <i class="fa fa-sort-amount-asc" aria-hidden="true"></i> Sort by Time
            </a>
            <form method="post" action="${pageContext.request.contextPath}/admin/reservations/queue/refresh">
                <button type="submit" class="action-btn edit-btn"><i class="fa fa-refresh" aria-hidden="true"></i> Refresh Queue</button>
            </form>
        </div>
    </div>

    <div style="background: rgba(41, 128, 185, 0.1); border: 1px solid rgba(41, 128, 185, 0.3); padding: 1rem; border-radius: 8px; margin-bottom: 1.5rem;">
        <p><strong>Queue Management Information:</strong></p>
        <p>This system implements a priority queue for reservation management. Reservations are processed in order of arrival, but you can prioritize specific reservations when needed.</p>
        <p>Queue uses Merge Sort algorithm to efficiently sort reservations by date and time when the "Sort by Time" button is clicked.</p>
    </div>

    <% if (nextPending != null) { %>
    <div style="margin-bottom: 2rem; padding: 1.5rem; background: rgba(0, 0, 0, 0.2); border-radius: 10px;">
        <h3 style="color: var(--gold); margin-bottom: 1rem;">Next in Queue</h3>
        <div style="display: grid; grid-template-columns: repeat(auto-fill, minmax(200px, 1fr)); gap: 1rem;">
            <div>
                <p><strong>Reservation ID:</strong> <%= nextPending.getId() %></p>
                <p><strong>Date:</strong> <%= nextPending.getReservationDate() %></p>
                <p><strong>Time:</strong> <%= nextPending.getReservationTime() %></p>
            </div>
            <div>
                <p><strong>Table:</strong> <%= nextPending.getTableId() %></p>
                <p><strong>Duration:</strong> <%= nextPending.getDuration() %> hours</p>
                <p><strong>Type:</strong> <%= nextPending.getBookingType() %></p>
            </div>
            <div>
                <p><strong>User ID:</strong> <%= nextPending.getUserId() %></p>
                <p><strong>Created:</strong> <%= nextPending.getCreatedAt() %></p>
                <% if (nextPending.getSpecialRequests() != null && !nextPending.getSpecialRequests().isEmpty()) { %>
                <p><strong>Notes:</strong> <%= nextPending.getSpecialRequests() %></p>
                <% } %>
            </div>
        </div>
        <div style="margin-top: 1rem;">
            <form method="post" action="${pageContext.request.contextPath}/admin/reservations/queue/process" style="display: inline-block; margin-right: 1rem;">
                <button type="submit" class="action-btn edit-btn" style="background-color: #4CAF50;">Process & Confirm</button>
            </form>
            <a href="${pageContext.request.contextPath}/admin/reservations/view?id=<%= nextPending.getId() %>" class="action-btn edit-btn">View Details</a>
        </div>
    </div>
    <% } else { %>
    <div style="text-align: center; padding: 2rem; background: rgba(0, 0, 0, 0.2); border-radius: 10px; margin-bottom: 2rem;">
        <p>No pending reservations in the queue</p>
    </div>
    <% } %>
</div>

<div class="card">
    <h2 style="margin-bottom: 1.5rem;">Pending Reservations Queue</h2>

    <% if (pendingReservations != null && !pendingReservations.isEmpty()) { %>
    <div class="queue-metrics" style="display: flex; gap: 1rem; margin-bottom: 1.5rem;">
        <div style="flex: 1; padding: 1rem; background: rgba(231, 76, 60, 0.1); border-radius: 8px; text-align: center;">
            <div style="font-size: 2rem; font-weight: bold; color: #e74c3c;"><%= pendingReservations.size() %></div>
            <div>Total in Queue</div>
        </div>

        <div style="flex: 1; padding: 1rem; background: rgba(46, 204, 113, 0.1); border-radius: 8px; text-align: center;">
            <div style="font-size: 2rem; font-weight: bold; color: #2ecc71;"><%= isSorted ? "Yes" : "No" %></div>
            <div>Queue Sorted</div>
        </div>

        <div style="flex: 1; padding: 1rem; background: rgba(241, 196, 15, 0.1); border-radius: 8px; text-align: center;">
            <div style="font-size: 2rem; font-weight: bold; color: #f1c40f;"><%= pendingReservations.size() > 0 ? "~" + (pendingReservations.size() * 2) + " min" : "0 min" %></div>
            <div>Est. Processing Time</div>
        </div>
    </div>

    <table class="data-table">
        <thead>
            <tr>
                <th>Queue Position</th>
                <th>Reservation ID</th>
                <th>Date</th>
                <th>Time</th>
                <th>Table</th>
                <th>User ID</th>
                <th>Created</th>
                <th>Actions</th>
            </tr>
        </thead>
        <tbody>
            <%
            int position = 1;
            for (Reservation reservation : pendingReservations) {
            %>
            <tr>
                <td><span style="background-color: <%= position == 1 ? "#4CAF50" : "#607D8B" %>; color: white; padding: 2px 8px; border-radius: 12px; font-size: 0.8rem;"><%= position++ %></span></td>
                <td><%= reservation.getId() %></td>
                <td><%= reservation.getReservationDate() %></td>
                <td><%= reservation.getReservationTime() %></td>
                <td><%= reservation.getTableId() %></td>
                <td><%= reservation.getUserId() %></td>
                <td><%= reservation.getCreatedAt().substring(0, 16).replace('T', ' ') %></td>
                <td>
                    <div style="display: flex; gap: 0.5rem; justify-content: center;">
                        <a href="${pageContext.request.contextPath}/admin/reservations/view?id=<%= reservation.getId() %>" class="action-btn edit-btn" title="View Details"><i class="fa fa-eye"></i></a>

                        <form method="post" action="${pageContext.request.contextPath}/admin/reservations/queue/prioritize">
                            <input type="hidden" name="reservationId" value="<%= reservation.getId() %>">
                            <button type="submit" class="action-btn edit-btn" style="background-color: #FFC107;" title="Move to Front of Queue"><i class="fa fa-arrow-up"></i></button>
                        </form>

                        <form method="post" action="${pageContext.request.contextPath}/admin/reservations/cancel">
                            <input type="hidden" name="reservationId" value="<%= reservation.getId() %>">
                            <button type="submit" class="action-btn delete-btn" onclick="return confirm('Are you sure you want to cancel this reservation?')" title="Cancel Reservation"><i class="fa fa-ban"></i></button>
                        </form>
                    </div>
                </td>
            </tr>
            <% } %>
        </tbody>
    </table>
    <% } else { %>
    <div style="text-align: center; padding: 2rem;">
        <p>No pending reservations found</p>
    </div>
    <% } %>
</div>

<script>
    // Add font awesome for icons
    document.head.innerHTML += '<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">';
</script>

<%@ include file="admin-footer.jsp" %>