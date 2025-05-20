<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Reservation Confirmed | Gourmet Reserve</title>
    <link href="https://fonts.googleapis.com/css2?family=Playfair+Display:wght@500&family=Roboto:wght@300;400;500&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/confirmation.css">
    <style>
        /* Additional QR code styling */
        .qr-code-inner {
            width: 150px;
            height: 150px;
            background: #fff;
            display: flex;
            justify-content: center;
            align-items: center;
            font-family: 'Courier New', monospace;
            font-weight: bold;
            font-size: 2rem;
            color: #000;
            margin-bottom: 10px;
            border: 1px solid #ddd;
            position: relative;
            overflow: hidden;
        }

        /* Add this to create a simple fake QR pattern */
        .qr-code-inner::before {
            content: "";
            position: absolute;
            top: 25px;
            left: 25px;
            width: 100px;
            height: 100px;
            background-image:
                linear-gradient(to right, #000 1px, transparent 1px),
                linear-gradient(to bottom, #000 1px, transparent 1px);
            background-size: 10px 10px;
            opacity: 0.2;
        }

        /* Add these for corner squares to simulate QR code */
        .qr-code-inner::after {
            content: "";
            position: absolute;
            width: 30px;
            height: 30px;
            border: 5px solid #000;
            top: 20px;
            left: 20px;
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
        String confirmationMessage = (String) session.getAttribute("confirmationMessage");
        String reservationId = (String) session.getAttribute("reservationId");

        if (confirmationMessage == null) {
            confirmationMessage = "Your reservation has been confirmed!";
        }
    %>

    <div class="confirmation-container">
        <div class="checkmark-circle">
            <div class="checkmark"></div>
        </div>

        <div class="confirmation-header">
            <h1>Reservation Confirmed!</h1>
            <p>Thank you for choosing Gourmet Reserve</p>
        </div>

        <div class="confirmation-details">
            <p class="confirmation-message"><%= confirmationMessage %></p>

            <% if (reservationId != null) { %>
            <div class="reservation-id">
                <span>Reservation ID:</span>
                <strong><%= reservationId %></strong>
            </div>
            <% } %>

            <p class="instruction">A confirmation email has been sent to your registered email address with your reservation details and a QR code for check-in.</p>

            <div class="qr-code-placeholder">
                <div class="qr-code-inner">
                    QR
                    <div class="qr-corner-tr"></div>
                    <div class="qr-corner-bl"></div>
                </div>
                <p>Scan this QR code when you arrive</p>
                <p style="font-size: 0.8rem; color: #777;">Code: <%= reservationId %></p>
            </div>
        </div>

        <div class="action-buttons">
            <a href="${pageContext.request.contextPath}/user/reservations" class="btn btn-secondary">View My Reservations</a>
            <a href="${pageContext.request.contextPath}/" class="btn btn-primary">Return to Home</a>
            <a href="#" class="btn btn-secondary" id="printBtn">Print QR Code</a>
        </div>
    </div>

    <script>
        // Print functionality
        document.addEventListener('DOMContentLoaded', function() {
            document.getElementById('printBtn').addEventListener('click', function(e) {
                e.preventDefault();
                window.print();
            });
        });
    </script>
</body>
</html>