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
        .qr-code {
            width: 200px;
            height: 200px;
            background: #fff;
            display: flex;
            justify-content: center;
            align-items: center;
            font-family: 'Courier New', monospace;
            font-weight: bold;
            font-size: 2rem;
            color: #000;
            margin: 0 auto 10px;
            border: 1px solid #ddd;
            position: relative;
            overflow: hidden;
        }

        /* Real QR code image styling */
        .qr-code img {
            width: 100%;
            height: 100%;
            object-fit: contain;
        }

        /* Fallback QR styling when no real QR is available */
        .qr-code::before {
            content: "";
            position: absolute;
            top: 25px;
            left: 25px;
            width: 150px;
            height: 150px;
            background-image:
                linear-gradient(to right, #000 1px, transparent 1px),
                linear-gradient(to bottom, #000 1px, transparent 1px);
            background-size: 10px 10px;
            opacity: 0.2;
            z-index: 1;
        }

        .qr-code::after {
            content: "";
            position: absolute;
            width: 30px;
            height: 30px;
            border: 5px solid #000;
            top: 20px;
            left: 20px;
            z-index: 2;
        }

        .qr-corner-tr, .qr-corner-bl {
            position: absolute;
            width: 30px;
            height: 30px;
            border: 5px solid #000;
            z-index: 2;
        }

        .qr-corner-tr {
            top: 20px;
            right: 20px;
        }

        .qr-corner-bl {
            bottom: 20px;
            left: 20px;
        }

        /* Hide fallback styling when real QR is present */
        .qr-code.real-qr::before,
        .qr-code.real-qr::after,
        .qr-code.real-qr .qr-corner-tr,
        .qr-code.real-qr .qr-corner-bl {
            display: none;
        }

        .reservation-details {
            background: rgba(255, 255, 255, 0.05);
            border-radius: 10px;
            padding: 20px;
            margin: 20px 0;
        }

        .detail-item {
            display: flex;
            justify-content: space-between;
            margin-bottom: 10px;
            padding-bottom: 10px;
            border-bottom: 1px solid rgba(255, 255, 255, 0.1);
        }

        .detail-item:last-child {
            border-bottom: none;
            margin-bottom: 0;
            padding-bottom: 0;
        }

        .detail-label {
            color: var(--gold);
            font-weight: 500;
        }

        .detail-value {
            color: white;
        }

        /* Print styling */
        @media print {
            body {
                background: white;
            }
            .confirmation-container {
                box-shadow: none;
                border: none;
                background: white;
                color: black;
            }
            .action-buttons {
                display: none;
            }
            .checkmark-circle, .checkmark {
                display: none;
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
        String confirmationMessage = (String) session.getAttribute("confirmationMessage");
        String reservationId = (String) session.getAttribute("reservationId");
        String reservationDate = (String) session.getAttribute("reservationDate");
        String reservationTime = (String) session.getAttribute("reservationTime");
        String tableId = (String) session.getAttribute("tableId");
        String qrCodeBase64 = (String) session.getAttribute("qrCodeBase64");
        String tableType = "Regular";

        // Extract table type from table ID if available
        if (tableId != null && !tableId.isEmpty()) {
            char typeChar = tableId.charAt(0);
            if (typeChar == 'f') tableType = "Family";
            else if (typeChar == 'l') tableType = "Luxury";
            else if (typeChar == 'c') tableType = "Couple";
            else if (typeChar == 'r') tableType = "Regular";
        }

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

            <div class="reservation-id">
                <span>Reservation ID:</span>
                <strong><%= reservationId %></strong>
            </div>

            <div class="reservation-details">
                <div class="detail-item">
                    <span class="detail-label">Date:</span>
                    <span class="detail-value"><%= reservationDate != null ? reservationDate : "Not specified" %></span>
                </div>
                <div class="detail-item">
                    <span class="detail-label">Time:</span>
                    <span class="detail-value"><%= reservationTime != null ? reservationTime : "Not specified" %></span>
                </div>
                <div class="detail-item">
                    <span class="detail-label">Table Type:</span>
                    <span class="detail-value"><%= tableType %></span>
                </div>
                <div class="detail-item">
                    <span class="detail-label">Table ID:</span>
                    <span class="detail-value"><%= tableId != null ? tableId : "Not assigned" %></span>
                </div>
            </div>

            <p class="instruction">Please arrive 15 minutes before your reservation time. A confirmation email has been sent to your registered email address.</p>

            <div class="qr-code-container">
                <% if (qrCodeBase64 != null && !qrCodeBase64.isEmpty()) { %>
                    <div class="qr-code real-qr">
                        <img src="<%= qrCodeBase64 %>" alt="Reservation QR Code">
                    </div>
                <% } else { %>
                    <div class="qr-code">
                        QR
                        <div class="qr-corner-tr"></div>
                        <div class="qr-corner-bl"></div>
                    </div>
                <% } %>
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