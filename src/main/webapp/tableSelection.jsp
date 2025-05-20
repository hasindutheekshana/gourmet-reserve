<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="com.tablebooknow.model.table.Table" %>
<%@ page import="com.tablebooknow.dao.TableDAO" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Table Selection | Gourmet Reserve</title>
    <link href="https://fonts.googleapis.com/css2?family=Playfair+Display:wght@500&family=Roboto:wght@300;400;500&display=swap" rel="stylesheet">
    <style>
        :root {
            --gold: #D4AF37;
            --burgundy: #800020;
            --dark-bg: #1a1a1a;
            --table-color: #4a4a4a;
            --reserved-color: #8B0000;
            --available-color: #336633;
            --hover-color: #3498db;
            --text: #e0e0e0;
        }

        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            min-height: 100vh;
            background: var(--dark-bg);
            font-family: 'Roboto', sans-serif;
            color: var(--text);
            background-image:
                linear-gradient(rgba(0,0,0,0.9), rgba(0,0,0,0.9)),
                url('${pageContext.request.contextPath}/assets/img/restaurant-bg.jpg');
            background-size: cover;
            background-position: center;
            padding-top: 80px;
            display: flex;
            flex-direction: column;
            align-items: center;
        }

        .header-nav {
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            padding: 1.5rem 5%;
            background: rgba(26, 26, 26, 0.95);
            backdrop-filter: blur(10px);
            z-index: 1000;
            display: flex;
            justify-content: space-between;
            align-items: center;
            border-bottom: 1px solid rgba(212, 175, 55, 0.3);
        }

        .logo {
            font-family: 'Playfair Display', serif;
            font-size: 1.8rem;
            color: var(--gold);
            text-decoration: none;
        }

        .nav-links {
            display: flex;
            gap: 2rem;
        }

        .nav-links a {
            color: var(--text);
            text-decoration: none;
            font-family: 'Roboto', sans-serif;
            font-weight: 400;
            transition: color 0.3s ease;
        }

        .nav-links a:hover,
        .nav-links a.active {
            color: var(--gold);
        }

        .container {
            width: 90%;
            max-width: 1200px;
            margin: 1rem auto;
            padding: 2rem;
            background: rgba(26, 26, 26, 0.9);
            border-radius: 15px;
            border: 1px solid rgba(212, 175, 55, 0.2);
            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.4);
        }

        .page-title {
            font-family: 'Playfair Display', serif;
            color: var(--gold);
            margin-bottom: 1.5rem;
            text-align: center;
        }

        .reservation-info {
            background: rgba(0, 0, 0, 0.2);
            padding: 1rem;
            border-radius: 10px;
            margin-bottom: 1.5rem;
            text-align: center;
        }

        .reservation-info span {
            font-weight: bold;
            color: var(--gold);
        }

        .floor-tabs {
            display: flex;
            gap: 1rem;
            margin-bottom: 1.5rem;
            justify-content: center;
        }

        .floor-tab {
            padding: 0.8rem 2rem;
            background: rgba(255, 255, 255, 0.1);
            border: 1px solid rgba(212, 175, 55, 0.3);
            border-radius: 8px;
            cursor: pointer;
            transition: all 0.3s;
        }

        .floor-tab:hover,
        .floor-tab.active {
            background: rgba(212, 175, 55, 0.2);
            transform: translateY(-2px);
        }

        .floor-tab.active {
            border-color: var(--gold);
            color: var(--gold);
        }

        .tables-container {
            display: none;
        }

        .tables-container.active {
            display: block;
            animation: fadeIn 0.5s ease-out;
        }

        .tables-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
            gap: 1.5rem;
        }

        .table-card {
            background: var(--table-color);
            border-radius: 12px;
            overflow: hidden;
            transition: all 0.3s;
            position: relative;
            border: 2px solid transparent;
        }

        .table-card:not(.reserved):hover {
            transform: translateY(-5px);
            box-shadow: 0 8px 25px rgba(0, 0, 0, 0.3);
            border-color: var(--gold);
        }

        .table-card.reserved {
            background: var(--reserved-color);
            opacity: 0.8;
            cursor: not-allowed;
        }

        .table-header {
            background: rgba(0, 0, 0, 0.3);
            padding: 1rem;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .table-name {
            font-size: 1.2rem;
            font-weight: 500;
        }

        .table-status {
            padding: 0.3rem 0.8rem;
            border-radius: 20px;
            font-size: 0.8rem;
            font-weight: 500;
        }

        .status-available {
            background: rgba(46, 204, 113, 0.2);
            color: #2ecc71;
            border: 1px solid rgba(46, 204, 113, 0.5);
        }

        .status-reserved {
            background: rgba(231, 76, 60, 0.2);
            color: #e74c3c;
            border: 1px solid rgba(231, 76, 60, 0.5);
        }

        .table-body {
            padding: 1.2rem;
        }

        .table-visualization {
            margin: 1rem auto;
            width: 180px;
            height: 120px;
            position: relative;
            display: flex;
            justify-content: center;
            align-items: center;
        }

        .table-shape {
            width: 120px;
            height: 80px;
            background: #5a5a5a;
            border-radius: 8px;
            display: flex;
            justify-content: center;
            align-items: center;
            color: white;
            font-weight: bold;
        }

        .table-shape.family {
            width: 140px;
            height: 90px;
        }

        .table-shape.luxury {
            width: 160px;
            height: 100px;
        }

        .table-shape.couple {
            width: 80px;
            height: 80px;
            border-radius: 50%;
        }

        .chair {
            width: 20px;
            height: 20px;
            background: #333;
            border-radius: 50%;
            position: absolute;
        }

        .table-info {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 0.8rem;
            margin: 1.5rem 0;
        }

        .info-item {
            background: rgba(0, 0, 0, 0.2);
            padding: 0.8rem;
            border-radius: 8px;
            text-align: center;
        }

        .info-label {
            font-size: 0.8rem;
            color: #bbb;
            margin-bottom: 0.3rem;
        }

        .info-value {
            font-size: 1rem;
            font-weight: 500;
        }

        .book-btn {
            width: 100%;
            padding: 1rem;
            background: linear-gradient(135deg, var(--gold), var(--burgundy));
            border: none;
            border-radius: 8px;
            color: white;
            font-weight: 500;
            cursor: pointer;
            transition: all 0.3s;
            margin-top: 1rem;
        }

        .book-btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3);
        }

        .unavailable-message {
            width: 100%;
            padding: 1rem;
            background: rgba(231, 76, 60, 0.2);
            border: 1px solid rgba(231, 76, 60, 0.3);
            color: #e74c3c;
            text-align: center;
            border-radius: 8px;
            margin-top: 1rem;
        }

        /* Modal Styles */
        .modal-overlay {
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: rgba(0, 0, 0, 0.8);
            backdrop-filter: blur(5px);
            z-index: 1000;
            display: none;
            justify-content: center;
            align-items: center;
        }

        .modal {
            background: rgba(26, 26, 26, 0.95);
            border-radius: 15px;
            width: 90%;
            max-width: 500px;
            padding: 2rem;
            border: 1px solid rgba(212, 175, 55, 0.3);
            animation: modalAppear 0.3s forwards;
        }

        @keyframes modalAppear {
            from {
                opacity: 0;
                transform: scale(0.95);
            }
            to {
                opacity: 1;
                transform: scale(1);
            }
        }

        .modal-header {
            text-align: center;
            margin-bottom: 1.5rem;
            position: relative;
        }

        .modal-title {
            font-family: 'Playfair Display', serif;
            color: var(--gold);
            font-size: 1.8rem;
            margin-bottom: 0.5rem;
        }

        .close-modal {
            position: absolute;
            top: 0;
            right: 0;
            background: none;
            border: none;
            color: #aaa;
            font-size: 1.5rem;
            cursor: pointer;
            transition: color 0.3s;
        }

        .close-modal:hover {
            color: var(--gold);
        }

        .reservation-summary {
            background: rgba(0, 0, 0, 0.2);
            padding: 1.2rem;
            border-radius: 10px;
            margin-bottom: 1.5rem;
        }

        .summary-item {
            display: flex;
            justify-content: space-between;
            padding: 0.5rem 0;
            border-bottom: 1px solid rgba(255, 255, 255, 0.1);
        }

        .summary-item:last-child {
            border-bottom: none;
        }

        .summary-label {
            color: #bbb;
        }

        .summary-value {
            font-weight: 500;
            color: var(--gold);
        }

        .form-group {
            margin-bottom: 1.5rem;
        }

        .form-label {
            display: block;
            color: var(--gold);
            margin-bottom: 0.5rem;
        }

        .form-textarea {
            width: 100%;
            padding: 1rem;
            background: rgba(255, 255, 255, 0.1);
            border: 1px solid rgba(212, 175, 55, 0.3);
            border-radius: 8px;
            color: white;
            resize: vertical;
            min-height: 100px;
        }

        .confirm-btn {
            width: 100%;
            padding: 1rem;
            background: linear-gradient(135deg, var(--gold), var(--burgundy));
            border: none;
            border-radius: 8px;
            color: white;
            font-weight: 500;
            cursor: pointer;
            transition: all 0.3s;
        }

        .confirm-btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3);
        }

        /* Error Message */
        .error-message {
            background: rgba(231, 76, 60, 0.2);
            border: 1px solid rgba(231, 76, 60, 0.5);
            color: #e74c3c;
            padding: 1rem;
            border-radius: 8px;
            margin-bottom: 1.5rem;
            text-align: center;
        }

        .loading {
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: rgba(0, 0, 0, 0.8);
            display: flex;
            justify-content: center;
            align-items: center;
            z-index: 2000;
        }

        .spinner {
            width: 50px;
            height: 50px;
            border: 5px solid rgba(255, 255, 255, 0.3);
            border-radius: 50%;
            border-top-color: var(--gold);
            animation: spin 1s ease-in-out infinite;
        }

        @keyframes spin {
            to {
                transform: rotate(360deg);
            }
        }

        @keyframes fadeIn {
            from {
                opacity: 0;
                transform: translateY(20px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }

        /* Responsive Adjustments */
        @media (max-width: 768px) {
            .tables-grid {
                grid-template-columns: 1fr;
            }

            .container {
                width: 95%;
                padding: 1rem;
            }
        }
    </style>
</head>
<body>
    <%
        // Check if user is logged in
        if (session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        // Get data from previous page (date selection)
        String reservationDate = (String) session.getAttribute("reservationDate");
        String reservationTime = (String) session.getAttribute("reservationTime");
        String bookingType = (String) session.getAttribute("bookingType");
        String reservationDuration = (String) session.getAttribute("reservationDuration");

        // Default duration is 2 hours for normal booking
        if (reservationDuration == null && "normal".equals(bookingType)) {
            reservationDuration = "2";
        } else if (reservationDuration == null) {
            reservationDuration = "2"; // Default fallback
        }

        if (reservationDate == null || reservationTime == null) {
            response.sendRedirect(request.getContextPath() + "/dateSelection.jsp");
            return;
        }

        String username = (String) session.getAttribute("username");

        // Get reserved tables from request attribute
        List<String> reservedTables = (List<String>) request.getAttribute("reservedTables");
        if (reservedTables == null) {
            reservedTables = new ArrayList<>();
        }

        // Get all tables from request attribute
        List<Table> allTables = (List<Table>) request.getAttribute("allTables");
        if (allTables == null) {
            allTables = new ArrayList<>();
        }

        // Group tables by floor for easier navigation
        Map<Integer, List<Table>> tablesByFloor = new HashMap<>();

        for (Table table : allTables) {
            int floor = table.getFloor();
            if (!tablesByFloor.containsKey(floor)) {
                tablesByFloor.put(floor, new ArrayList<>());
            }
            tablesByFloor.get(floor).add(table);
        }

        // Get any error message
        String errorMessage = (String) request.getAttribute("errorMessage");
    %>

    <!-- Header Navigation -->
    <nav class="header-nav">
        <a href="${pageContext.request.contextPath}/" class="logo">Gourmet Reserve</a>
        <div class="nav-links">
            <a href="${pageContext.request.contextPath}/reservation/dateSelection" class="active">Reservations</a>
            <a href="${pageContext.request.contextPath}/user/reservations">My Reservations</a>
            <a href="${pageContext.request.contextPath}/user/profile">Profile</a>
            <a href="${pageContext.request.contextPath}/user/logout">Logout</a>
        </div>
    </nav>

    <div class="container">
        <h1 class="page-title">Select a Table</h1>

        <!-- Error message display if any -->
        <% if (errorMessage != null && !errorMessage.isEmpty()) { %>
            <div class="error-message">
                <%= errorMessage %>
            </div>
        <% } %>

        <div class="reservation-info">
            <p>Reservation for <span><%= reservationDate %></span> at <span><%= reservationTime %></span></p>
            <p>Duration: <span><%= reservationDuration %> hours</span> • Booking Type: <span><%= bookingType %></span></p>
        </div>

        <div class="floor-tabs">
            <%
            // Create tab for each floor that has tables
            for (Integer floor : tablesByFloor.keySet()) {
            %>
                <div class="floor-tab <%= floor == 1 ? "active" : "" %>" data-floor="<%= floor %>">
                    Floor <%= floor %>
                </div>
            <% } %>
        </div>

        <!-- Tables Container for each floor -->
        <%
        for (Map.Entry<Integer, List<Table>> entry : tablesByFloor.entrySet()) {
            Integer floor = entry.getKey();
            List<Table> floorTables = entry.getValue();
        %>
            <div id="floor-<%= floor %>" class="tables-container <%= floor == 1 ? "active" : "" %>">
                <div class="tables-grid">
                    <%
                    for (Table table : floorTables) {
                        boolean isReserved = reservedTables.contains(table.getId());
                        String tableType = table.getTableType();
                    %>
                        <div class="table-card <%= isReserved ? "reserved" : "" %>">
                            <div class="table-header">
                                <div class="table-name"><%= table.getDisplayName() %></div>
                                <div class="table-status <%= isReserved ? "status-reserved" : "status-available" %>">
                                    <%= isReserved ? "Reserved" : "Available" %>
                                </div>
                            </div>

                            <div class="table-body">
                                <div class="table-visualization">
                                    <%
                                    // Add chairs based on table type
                                    if (tableType.equals("family")) {
                                        // 6 chairs for family table
                                        %>
                                        <div class="chair" style="top: 10px; left: 40px;"></div>
                                        <div class="chair" style="top: 10px; left: 100px;"></div>
                                        <div class="chair" style="top: 50px; left: 20px;"></div>
                                        <div class="chair" style="top: 50px; right: 20px;"></div>
                                        <div class="chair" style="bottom: 10px; left: 40px;"></div>
                                        <div class="chair" style="bottom: 10px; left: 100px;"></div>
                                        <div class="table-shape family"><%= table.getCapacity() %> seats</div>
                                    <%
                                    } else if (tableType.equals("luxury")) {
                                        // 10 chairs for luxury table
                                        %>
                                        <div class="chair" style="top: 10px; left: 30px;"></div>
                                        <div class="chair" style="top: 10px; left: 70px;"></div>
                                        <div class="chair" style="top: 10px; right: 30px;"></div>
                                        <div class="chair" style="left: 10px; top: 40px;"></div>
                                        <div class="chair" style="right: 10px; top: 40px;"></div>
                                        <div class="chair" style="left: 10px; bottom: 40px;"></div>
                                        <div class="chair" style="right: 10px; bottom: 40px;"></div>
                                        <div class="chair" style="bottom: 10px; left: 30px;"></div>
                                        <div class="chair" style="bottom: 10px; left: 70px;"></div>
                                        <div class="chair" style="bottom: 10px; right: 30px;"></div>
                                        <div class="table-shape luxury"><%= table.getCapacity() %> seats</div>
                                    <%
                                    } else if (tableType.equals("couple")) {
                                        // 2 chairs for couple table
                                        %>
                                        <div class="chair" style="top: 10px; left: 80px;"></div>
                                        <div class="chair" style="bottom: 10px; left: 80px;"></div>
                                        <div class="table-shape couple"><%= table.getCapacity() %> seats</div>
                                    <%
                                    } else {
                                        // 4 chairs for regular table
                                        %>
                                        <div class="chair" style="top: 10px; left: 60px;"></div>
                                        <div class="chair" style="right: 20px; top: 50px;"></div>
                                        <div class="chair" style="bottom: 10px; left: 60px;"></div>
                                        <div class="chair" style="left: 20px; top: 50px;"></div>
                                        <div class="table-shape"><%= table.getCapacity() %> seats</div>
                                    <%
                                    }
                                    %>
                                </div>

                                <div class="table-info">
                                    <div class="info-item">
                                        <div class="info-label">Type</div>
                                        <div class="info-value"><%= tableType.substring(0, 1).toUpperCase() + tableType.substring(1) %></div>
                                    </div>
                                    <div class="info-item">
                                        <div class="info-label">Capacity</div>
                                        <div class="info-value"><%= table.getCapacity() %> people</div>
                                    </div>
                                    <div class="info-item">
                                        <div class="info-label">Floor</div>
                                        <div class="info-value"><%= table.getFloor() %></div>
                                    </div>
                                    <div class="info-item">
                                        <div class="info-label">Table ID</div>
                                        <div class="info-value"><%= table.getId() %></div>
                                    </div>
                                </div>

                                <% if (!isReserved) { %>
                                    <button class="book-btn" data-table-id="<%= table.getId() %>"
                                            data-table-type="<%= tableType %>"
                                            data-table-capacity="<%= table.getCapacity() %>"
                                            data-table-number="<%= table.getTableNumber() %>"
                                            data-table-floor="<%= table.getFloor() %>">
                                        Book This Table
                                    </button>
                                <% } else { %>
                                    <div class="unavailable-message">This table is currently reserved</div>
                                <% } %>
                            </div>
                        </div>
                    <% } %>
                </div>
            </div>
        <% } %>
    </div>

    <!-- Reservation Confirmation Modal -->
    <div class="modal-overlay" id="reservationModal">
        <div class="modal">
            <div class="modal-header">
                <h2 class="modal-title">Confirm Your Reservation</h2>
                <button class="close-modal">&times;</button>
            </div>

            <form id="reservationForm" action="${pageContext.request.contextPath}/reservation/confirmReservation" method="post">
                <input type="hidden" name="tableId" id="tableId">

                <div class="reservation-summary">
                    <div class="summary-item">
                        <div class="summary-label">Date</div>
                        <div class="summary-value"><%= reservationDate %></div>
                    </div>
                    <div class="summary-item">
                        <div class="summary-label">Time</div>
                        <div class="summary-value"><%= reservationTime %></div>
                    </div>
                    <div class="summary-item">
                        <div class="summary-label">Duration</div>
                        <div class="summary-value"><%= reservationDuration %> hours</div>
                    </div>
                    <div class="summary-item">
                        <div class="summary-label">Table</div>
                        <div class="summary-value" id="tableName"></div>
                    </div>
                    <div class="summary-item">
                        <div class="summary-label">Capacity</div>
                        <div class="summary-value" id="tableCapacity"></div>
                    </div>
                    <div class="summary-item">
                        <div class="summary-label">Booking Type</div>
                        <div class="summary-value"><%= bookingType %></div>
                    </div>
                </div>

                <div class="form-group">
                    <label class="form-label">Special Requests</label>
                    <textarea name="specialRequests" class="form-textarea" placeholder="Any dietary requirements, seating preferences, or special occasions?"></textarea>
                </div>

                <button type="submit" class="confirm-btn">Confirm Reservation</button>
            </form>
        </div>
    </div>

    <!-- Loading Overlay -->
    <div class="loading" id="loadingOverlay" style="display: none;">
        <div class="spinner"></div>
    </div>

    <script>
        // Floor tab switching
        document.querySelectorAll('.floor-tab').forEach(tab => {
            tab.addEventListener('click', function() {
                // Get floor number
                const floor = this.dataset.floor;

                // Update active tab
                document.querySelectorAll('.floor-tab').forEach(t => t.classList.remove('active'));
                this.classList.add('active');

                // Update active tables container
                document.querySelectorAll('.tables-container').forEach(container => {
                    container.classList.remove('active');
                });
                document.getElementById('floor-' + floor).classList.add('active');
            });
        });

        // Book table functionality
        document.querySelectorAll('.book-btn').forEach(button => {
            button.addEventListener('click', function() {
                const tableId = this.dataset.tableId;
                const tableType = this.dataset.tableType;
                const tableCapacity = this.dataset.tableCapacity;
                const tableNumber = this.dataset.tableNumber;
                const tableFloor = this.dataset.tableFloor;

                // Populate modal
                document.getElementById('tableId').value = tableId;
                document.getElementById('tableName').textContent = tableType.charAt(0).toUpperCase() + tableType.slice(1) + ' Table ' + tableNumber + ' (Floor ' + tableFloor + ')';
                document.getElementById('tableCapacity').textContent = tableCapacity + ' people';

                // Show modal
                document.getElementById('reservationModal').style.display = 'flex';
            });
        });

        // Close modal functionality
        document.querySelector('.close-modal').addEventListener('click', function() {
            document.getElementById('reservationModal').style.display = 'none';
        });

        // Close modal when clicking outside
        document.getElementById('reservationModal').addEventListener('click', function(e) {
            if (e.target === this) {
                this.style.display = 'none';
            }
        });

        // Form submission with loading overlay
        document.getElementById('reservationForm').addEventListener('submit', function() {
            document.getElementById('loadingOverlay').style.display = 'flex';
        });

        // Auto hide error messages
        setTimeout(() => {
            const errorMessage = document.querySelector('.error-message');
            if (errorMessage) {
                errorMessage.style.opacity = '0';
                errorMessage.style.transition = 'opacity 0.5s';
                setTimeout(() => {
                    errorMessage.style.display = 'none';
                }, 500);
            }
        }, 5000);

        // Check for reservation data
        if (!<%= reservationDate != null && reservationTime != null %>) {
            alert('Missing reservation information. Please go back to select date and time.');
            window.location.href = '${pageContext.request.contextPath}/reservation/dateSelection';
        }

        // Check if we have tables for the selected floors
        <% if (tablesByFloor.isEmpty()) { %>
            alert('No tables found. Please try again later or contact our staff.');
        <% } %>
    </script>
</body>
</html>