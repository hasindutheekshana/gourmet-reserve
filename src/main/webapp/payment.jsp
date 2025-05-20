<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Payment | Gourmet Reserve</title>
    <link href="https://fonts.googleapis.com/css2?family=Playfair+Display:wght@500&family=Roboto:wght@300;400;500&display=swap" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        :root {
            --gold: #D4AF37;
            --burgundy: #800020;
            --dark: #1a1a1a;
            --text: #e0e0e0;
            --glass: rgba(255, 255, 255, 0.05);
        }

        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            min-height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
            background: var(--dark);
            font-family: 'Roboto', sans-serif;
            background-image:
                linear-gradient(rgba(0,0,0,0.9), rgba(0,0,0,0.9)),
                url('${pageContext.request.contextPath}/assets/img/restaurant-bg.jpg');
            background-size: cover;
            background-position: center;
        }

        .payment-container {
            background: rgba(26, 26, 26, 0.95);
            padding: 3rem;
            border-radius: 20px;
            width: 90%;
            max-width: 600px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.3);
            border: 1px solid rgba(212, 175, 55, 0.2);
            animation: fadeIn 0.5s ease-out;
        }

        @keyframes fadeIn {
            from { opacity: 0; transform: translateY(20px); }
            to { opacity: 1; transform: translateY(0); }
        }

        .header {
            text-align: center;
            margin-bottom: 2.5rem;
        }

        .header h1 {
            font-family: 'Playfair Display', serif;
            color: var(--gold);
            font-size: 2.5rem;
            margin-bottom: 0.5rem;
            letter-spacing: 1px;
        }

        .header p {
            color: var(--text);
            font-size: 1.1rem;
            opacity: 0.9;
        }

        .reservation-summary {
            background: rgba(255, 255, 255, 0.1);
            border-radius: 8px;
            padding: 1.5rem;
            margin-bottom: 1.5rem;
            border: 1px solid rgba(212, 175, 55, 0.3);
        }

        .summary-item {
            margin-bottom: 0.8rem;
            font-size: 1.1rem;
            color: var(--text);
        }

        .summary-item strong {
            color: var(--gold);
            margin-right: 0.5rem;
            display: inline-block;
            width: 120px;
        }

        .payment-options {
            margin-bottom: 2rem;
        }

        .payment-title {
            color: var(--gold);
            margin-bottom: 1rem;
            font-size: 1.2rem;
            font-weight: 500;
        }

        .payment-method {
            display: flex;
            flex-wrap: wrap;
            gap: 1rem;
            margin-bottom: 1.5rem;
        }

        .payment-method-item {
            background: rgba(255, 255, 255, 0.1);
            padding: 1rem;
            border-radius: 8px;
            width: calc(50% - 0.5rem);
            text-align: center;
            cursor: pointer;
            transition: all 0.3s;
            border: 1px solid transparent;
        }

        .payment-method-item:hover {
            background: rgba(255, 255, 255, 0.15);
            transform: translateY(-3px);
        }

        .payment-method-item.selected {
            border-color: var(--gold);
            background: rgba(212, 175, 55, 0.1);
        }

        .payment-method-item img {
            max-height: 40px;
            margin-bottom: 0.5rem;
        }

        .payment-method-item span {
            display: block;
            color: var(--text);
            font-size: 0.9rem;
        }

        .proceed-btn {
            width: 100%;
            padding: 1.2rem;
            background: linear-gradient(135deg, var(--gold), var(--burgundy));
            border: none;
            border-radius: 10px;
            color: white;
            font-size: 1.1rem;
            font-weight: 500;
            cursor: pointer;
            transition: transform 0.3s ease;
            margin-top: 1rem;
        }

        .proceed-btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(212, 175, 55, 0.3);
        }

        .back-link {
            display: block;
            text-align: center;
            margin-top: 1.5rem;
            color: var(--text);
            text-decoration: none;
            font-size: 0.9rem;
            opacity: 0.8;
            transition: opacity 0.3s;
        }

        .back-link:hover {
            opacity: 1;
            color: var(--gold);
        }

        .error-message {
            color: #ff4444;
            margin-top: 1rem;
            text-align: center;
            background: rgba(255, 68, 68, 0.1);
            padding: 1rem;
            border-radius: 8px;
            animation: shake 0.5s ease-in-out;
        }

        @keyframes shake {
            0%, 100% { transform: translateX(0); }
            20%, 60% { transform: translateX(-5px); }
            40%, 80% { transform: translateX(5px); }
        }

        @media (max-width: 768px) {
            .payment-container {
                padding: 2rem;
                width: 95%;
            }

            .header h1 {
                font-size: 2rem;
            }

            .payment-method-item {
                width: 100%;
            }
        }
    </style>
</head>
<body>
    <%
        if (session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String username = (String) session.getAttribute("username");
        String reservationId = (String) session.getAttribute("reservationId");

        if (reservationId == null) {
            reservationId = request.getParameter("reservationId");
        }

        com.tablebooknow.model.reservation.Reservation reservation = null;
        String tableType = request.getAttribute("tableType") != null ?
                          (String) request.getAttribute("tableType") : "Regular";

        if (request.getAttribute("reservation") != null) {
            reservation = (com.tablebooknow.model.reservation.Reservation) request.getAttribute("reservation");
        }
    %>

    <div class="payment-container">
        <div class="header">
            <h1>Secure Payment</h1>
            <p>Complete your table reservation</p>
        </div>

        <% if (reservationId == null) { %>
            <div class="error-message">
                No reservation found. Please make a reservation first.
            </div>
            <a href="${pageContext.request.contextPath}/dateSelection.jsp" class="back-link">Go to Reservations</a>
        <% } else { %>
            <div class="reservation-summary">
                <div class="summary-item">
                    <strong>Reservation ID:</strong> <%= reservationId %>
                </div>
                <% if (reservation != null) { %>
                    <div class="summary-item">
                        <strong>Date:</strong> <%= reservation.getReservationDate() %>
                    </div>
                    <div class="summary-item">
                        <strong>Time:</strong> <%= reservation.getReservationTime() %>
                    </div>
                    <div class="summary-item">
                        <strong>Duration:</strong> <%= reservation.getDuration() %> hours
                    </div>
                    <div class="summary-item">
                        <strong>Table Type:</strong> <%= tableType %> Table
                    </div>
                <% } %>
            </div>

            <form action="${pageContext.request.contextPath}/payment/process" method="post" id="paymentForm">
                <input type="hidden" name="reservationId" value="<%= reservationId %>">

                <div class="payment-options">
                    <h3 class="payment-title">Select Payment Method</h3>
                    <div class="payment-method">
                        <div class="payment-method-item selected" onclick="selectPaymentMethod('payhere')">
                            <img src="https://payhere.lk/payhere-logo-light.png" alt="PayHere Logo">
                            <span>PayHere</span>
                        </div>
                        <div class="payment-method-item" onclick="selectPaymentMethod('card')">
                            <img src="https://cdn-icons-png.flaticon.com/512/179/179457.png" alt="Credit Card">
                            <span>Credit/Debit Card</span>
                        </div>
                    </div>
                    <input type="hidden" name="paymentMethod" id="paymentMethod" value="payhere">
                </div>

                <button type="submit" class="proceed-btn">Proceed to Payment</button>

                <% if (request.getAttribute("errorMessage") != null) { %>
                    <div class="error-message">
                        <%= request.getAttribute("errorMessage") %>
                    </div>
                <% } %>
            </form>

            <a href="${pageContext.request.contextPath}/tableSelection.jsp" class="back-link">Back to Table Selection</a>
        <% } %>
    </div>

    <script>
        function selectPaymentMethod(method) {
            document.getElementById('paymentMethod').value = method;

            const items = document.querySelectorAll('.payment-method-item');
            items.forEach(item => {
                item.classList.remove('selected');
            });

            items.forEach(item => {
                if (item.textContent.toLowerCase().includes(method)) {
                    item.classList.add('selected');
                }
            });
        }
    </script>
</body>
</html>