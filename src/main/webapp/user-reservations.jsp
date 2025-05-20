<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.tablebooknow.model.reservation.Reservation" %>
<%@ page import="java.time.LocalDate" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Reservations | Gourmet Reserve</title>
    <link href="https://fonts.googleapis.com/css2?family=Playfair+Display:wght@500&family=Roboto:wght@300;400;500&display=swap" rel="stylesheet">
    <style>
        :root {
            --gold: #D4AF37;
            --burgundy: #800020;
            --dark: #1a1a1a;
            --text: #e0e0e0;
        }

        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            min-height: 100vh;
            background: var(--dark);
            font-family: 'Roboto', sans-serif;
            background-image:
                linear-gradient(rgba(0,0,0,0.9), rgba(0,0,0,0.9)),
                url('${pageContext.request.contextPath}/assets/img/restaurant-bg.jpg');
            background-size: cover;
            background-position: center;
            color: var(--text);
            padding-top: 80px;
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

        .main-container {
            width: 90%;
            max-width: 1000px;
            margin: 2rem auto;
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

        .message {
            padding: 1rem;
            margin-bottom: 1.5rem;
            border-radius: 8px;
            text-align: center;
        }

        .success-message {
            background: rgba(46, 204, 113, 0.2);
            border: 1px solid rgba(46, 204, 113, 0.5);
            color: #2ecc71;
        }

        .error-message {
            background: rgba(231, 76, 60, 0.2);
            border: 1px solid rgba(231, 76, 60, 0.5);
            color: #e74c3c;
        }

        .reservation-card {
            margin-bottom: 1.5rem;
            background: rgba(255, 255, 255, 0.05);
            border-radius: 12px;
            overflow: hidden;
            transition: transform 0.3s ease;
            border: 1px solid rgba(212, 175, 55, 0.2);
        }

        .reservation-card:hover {
            transform: translateY(-3px);
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3);
            border-color: rgba(212, 175, 55, 0.4);
        }

        .reservation-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 1.2rem;
            background: rgba(0, 0, 0, 0.3);
        }

        .reservation-date {
            font-size: 1.2rem;
            font-weight: 500;
            color: var(--gold);
        }

        .reservation-status {
            padding: 0.5rem 1rem;
            border-radius: 20px;
            font-size: 0.8rem;
            font-weight: 500;
        }

        .status-confirmed {
            background: rgba(46, 204, 113, 0.2);
            color: #2ecc71;
            border: 1px solid rgba(46, 204, 113, 0.5);
        }

        .status-pending {
            background: rgba(241, 196, 15, 0.2);
            color: #f1c40f;
            border: 1px solid rgba(241, 196, 15, 0.5);
        }

        .status-cancelled {
            background: rgba(231, 76, 60, 0.2);
            color: #e74c3c;
            border: 1px solid rgba(231, 76, 60, 0.5);
        }

        .status-completed {
            background: rgba(52, 152, 219, 0.2);
            color: #3498db;
            border: 1px solid rgba(52, 152, 219, 0.5);
        }

        .reservation-body {
            padding: 1.2rem;
        }

        .reservation-info {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 1.5rem;
            margin-bottom: 1.5rem;
        }

        .info-item {
            padding: 0.8rem;
            background: rgba(0, 0, 0, 0.2);
            border-radius: 8px;
        }

        .info-label {
            color: var(--gold);
            font-size: 0.9rem;
            margin-bottom: 0.3rem;
            opacity: 0.9;
        }

        .info-value {
            font-size: 1.1rem;
        }

        .special-requests {
            margin-top: 1rem;
            padding: 1rem;
            background: rgba(0, 0, 0, 0.2);
            border-radius: 8px;
        }

        .special-requests h4 {
            color: var(--gold);
            margin-bottom: 0.5rem;
            font-size: 0.9rem;
        }

        .reservation-actions {
            display: flex;
            justify-content: flex-end;
            gap: 1rem;
            margin-top: 1.5rem;
        }

        .action-btn {
            padding: 0.7rem 1.2rem;
            border: none;
            border-radius: 8px;
            cursor: pointer;
            font-weight: 500;
            transition: all 0.3s ease;
        }

        .btn-cancel {
            background: rgba(231, 76, 60, 0.8);
            color: white;
        }

        .btn-cancel:hover {
            background: #e74c3c;
            transform: translateY(-2px);
        }

        .btn-qr {
            background: rgba(52, 152, 219, 0.8);
            color: white;
        }

        .btn-qr:hover {
            background: #3498db;
            transform: translateY(-2px);
        }

        .no-reservations {
            text-align: center;
            padding: 3rem 1rem;
            background: rgba(0, 0, 0, 0.2);
            border-radius: 12px;
            margin: 2rem 0;
        }

        .no-reservations h3 {
            color: var(--gold);
            margin-bottom: 1rem;
        }

        .no-reservations p {
            margin-bottom: 1.5rem;
            opacity: 0.9;
        }

        .make-reservation-btn {
            display: inline-block;
            padding: 0.8rem 1.5rem;
            background: linear-gradient(135deg, var(--gold), var(--burgundy));
            border: none;
            border-radius: 8px;
            color: white;
            text-decoration: none;
            font-weight: 500;
            transition: all 0.3s;
        }

        .make-reservation-btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3);
        }

        /* QR Code Modal Styles */
        .modal-overlay {
            display: none;
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: rgba(0, 0, 0, 0.8);
            z-index: 1100;
            justify-content: center;
            align-items: center;
        }

        .modal-content {
            background: rgba(26, 26, 26, 0.95);
            padding: 2rem;
            border-radius: 15px;
            width: 90%;
            max-width: 400px;
            border: 1px solid rgba(212, 175, 55, 0.3);
            text-align: center;
        }

        .modal-title {
            color: var(--gold);
            margin-bottom: 1.5rem;
            font-family: 'Playfair Display', serif;
        }

        .qr-container {
            background: white;
            width: 200px;
            height: 200px;
            margin: 0 auto 1.5rem;
            display: flex;
            justify-content: center;
            align-items: center;
            border-radius: 5px;
            position: relative;
        }

        .fake-qr {
            position: relative;
            width: 100%;
            height: 100%;
            display: flex;
            justify-content: center;
            align-items: center;
            font-family: monospace;
            font-weight: bold;
            font-size: 24px;
            color: #000;
        }

        .fake-qr::before {
            content: "";
            position: absolute;
            top: 25px;
            left: 25px;
            width: 150px;
            height: 150px;
            background-image: linear-gradient(to right, #000 1px, transparent 1px), linear-gradient(to bottom, #000 1px, transparent 1px);
            background-size: 10px 10px;
            opacity: 0.2;
        }

        .fake-qr::after {
            content: "";
            position: absolute;
            width: 30px;
            height: 30px;
            border: 5px solid #000;
            top: 20px;
            left: 20px;
        }

        .btn-review {
            background: rgba(46, 204, 113, 0.8);
            color: white;
        }

        .btn-review:hover {
            background: #2ecc71;
            transform: translateY(-2px);
        }

        .qr-corner-tr, .qr-corner-bl {
            position: absolute;
            width: 30px;
            height: 30px;
            border: 5px solid #000;
        }

        .qr-corner-tr {
            top: 20px;
            right: 20px;
        }

        .qr-corner-bl {
            bottom: 20px;
            left: 20px;
        }

        .close-modal {
            margin-top: 1rem;
            padding: 0.7rem 1.5rem;
            background: rgba(231, 76, 60, 0.8);
            color: white;
            border: none;
            border-radius: 8px;
            cursor: pointer;
            font-weight: 500;
            transition: all 0.3s;
        }

        .close-modal:hover {
            background: #e74c3c;
            transform: translateY(-2px);
        }

        /* ANIMATION FOR PAGE LOAD */
        @keyframes fadeIn {
            from { opacity: 0; transform: translateY(20px); }
            to { opacity: 1; transform: translateY(0); }
        }

        .animated {
            animation: fadeIn 0.6s ease-out forwards;
        }

        /* Responsive adjustments */
        @media (max-width: 768px) {
            .main-container {
                width: 95%;
                padding: 1.5rem;
            }

            .reservation-info {
                grid-template-columns: 1fr;
                gap: 1rem;
            }

            .reservation-actions {
                flex-direction: column;
            }

            .action-btn {
                width: 100%;
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

        String username = (String) session.getAttribute("username");

        // Get messages if any
        String successMessage = (String) request.getAttribute("successMessage");
        String errorMessage = (String) request.getAttribute("errorMessage");

        // Get list of user reservations
        List<Reservation> userReservations = (List<Reservation>) request.getAttribute("userReservations");
    %>

    <!-- Header Navigation -->
    <nav class="header-nav">
        <a href="${pageContext.request.contextPath}/" class="logo">Gourmet Reserve</a>
        <div class="nav-links">
            <a href="${pageContext.request.contextPath}/reservation/dateSelection">Make Reservation</a>
            <a href="${pageContext.request.contextPath}/user/reservations" class="active">My Reservations</a>
            <a href="${pageContext.request.contextPath}/user/profile">Profile</a>
            <a href="${pageContext.request.contextPath}/user/logout">Logout</a>
        </div>
    </nav>

    <!-- Main Content -->
    <div class="main-container animated">
        <h1 class="page-title">My Reservations</h1>

        <!-- Success/Error Messages -->
        <% if (successMessage != null) { %>
        <div class="message success-message">
            <%= successMessage %>
        </div>
        <% } %>

        <% if (errorMessage != null) { %>
        <div class="message error-message">
            <%= errorMessage %>
        </div>
        <% } %>

        <!-- Reservation List -->
        <% if (userReservations != null && !userReservations.isEmpty()) { %>
            <% for (Reservation reservation : userReservations) {
                String status = reservation.getStatus();
                String statusClass = "";

                if ("confirmed".equalsIgnoreCase(status)) {
                    statusClass = "status-confirmed";
                } else if ("pending".equalsIgnoreCase(status)) {
                    statusClass = "status-pending";
                } else if ("cancelled".equalsIgnoreCase(status)) {
                    statusClass = "status-cancelled";
                } else if ("completed".equalsIgnoreCase(status)) {
                    statusClass = "status-completed";
                }

                // Determine table type from table ID
                String tableId = reservation.getTableId();
                String tableType = "Regular";
                if (tableId != null && !tableId.isEmpty()) {
                    char typeChar = tableId.charAt(0);
                    if (typeChar == 'f') tableType = "Family";
                    else if (typeChar == 'l') tableType = "Luxury";
                    else if (typeChar == 'c') tableType = "Couple";
                    else if (typeChar == 'r') tableType = "Regular";
                }
            %>
            <div class="reservation-card">
                <div class="reservation-header">
                    <div class="reservation-date">
                        <%= reservation.getReservationDate() %> at <%= reservation.getReservationTime() %>
                    </div>
                    <div class="reservation-status <%= statusClass %>">
                        <%= status.toUpperCase() %>
                    </div>
                </div>
                <div class="reservation-body">
                    <div class="reservation-info">
                        <div class="info-item">
                            <div class="info-label">Reservation ID</div>
                            <div class="info-value"><%= reservation.getId() %></div>
                        </div>
                        <div class="info-item">
                            <div class="info-label">Table</div>
                            <div class="info-value"><%= tableType %> (<%= reservation.getTableId() %>)</div>
                        </div>
                        <div class="info-item">
                            <div class="info-label">Duration</div>
                            <div class="info-value"><%= reservation.getDuration() %> hours</div>
                        </div>
                        <div class="info-item">
                            <div class="info-label">Booking Type</div>
                            <div class="info-value"><%= reservation.getBookingType() %></div>
                        </div>
                    </div>

                    <% if (reservation.getSpecialRequests() != null && !reservation.getSpecialRequests().isEmpty()) { %>
                    <div class="special-requests">
                        <h4>Special Requests</h4>
                        <p><%= reservation.getSpecialRequests() %></p>
                    </div>
                    <% } %>

                    <div class="reservation-actions">
                        <% if ("confirmed".equalsIgnoreCase(status)) { %>
                            <button class="action-btn btn-qr" onclick="showQRCode('<%= reservation.getId() %>')">Show QR Code</button>
                            <form action="${pageContext.request.contextPath}/user/reservations" method="post"
                                  onsubmit="return confirm('Are you sure you want to cancel this reservation?');">
                                <input type="hidden" name="action" value="cancel">
                                <input type="hidden" name="reservationId" value="<%= reservation.getId() %>">
                                <button type="submit" class="action-btn btn-cancel">Cancel Reservation</button>
                            </form>
                        <% } else if ("pending".equalsIgnoreCase(status)) { %>
                            <form action="${pageContext.request.contextPath}/user/reservations" method="post"
                                  onsubmit="return confirm('Are you sure you want to cancel this reservation?');">
                                <input type="hidden" name="action" value="cancel">
                                <input type="hidden" name="reservationId" value="<%= reservation.getId() %>">
                                <button type="submit" class="action-btn btn-cancel">Cancel Reservation</button>
                            </form>
                        <% } else if ("completed".equalsIgnoreCase(status)) { %>
                            <a href="${pageContext.request.contextPath}/reviews/add?reservationId=<%= reservation.getId() %>" class="action-btn btn-review">Write Review</a>
                        <% } %>
                    </div>
                </div>
            </div>
            <% } %>
        <% } else { %>
            <div class="no-reservations">
                <h3>No Reservations Found</h3>
                <p>You don't have any reservations yet.</p>
                <a href="${pageContext.request.contextPath}/reservation/dateSelection" class="make-reservation-btn">Make a Reservation</a>
            </div>
        <% } %>
    </div>

    <!-- QR Code Modal -->
    <div class="modal-overlay" id="qrModal">
        <div class="modal-content">
            <h2 class="modal-title">Reservation QR Code</h2>
            <div class="qr-container">
                <div class="fake-qr">
                    QR
                    <div class="qr-corner-tr"></div>
                    <div class="qr-corner-bl"></div>
                </div>
            </div>
            <p>Present this QR code when you arrive at the restaurant</p>
            <p style="font-size: 0.8rem; color: #777; margin-top: 5px;">Reservation ID: <span id="modalReservationId"></span></p>
            <button class="close-modal" onclick="hideQRCode()">Close</button>
        </div>
    </div>

    <script>
        // QR Code modal functions
        function showQRCode(reservationId) {
            document.getElementById('modalReservationId').textContent = reservationId;
            document.getElementById('qrModal').style.display = 'flex';
        }

        function hideQRCode() {
            document.getElementById('qrModal').style.display = 'none';
        }

        // Auto-hide messages after 5 seconds
        setTimeout(() => {
            document.querySelectorAll('.message').forEach(message => {
                message.style.opacity = '0';
                message.style.transition = 'opacity 0.5s';
                setTimeout(() => {
                    message.style.display = 'none';
                }, 500);
            });
        }, 5000);
    </script>
</body>
</html>