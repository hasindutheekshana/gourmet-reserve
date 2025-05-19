<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.tablebooknow.model.review.Review" %>
<%@ page import="com.tablebooknow.model.reservation.Reservation" %>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${param.editMode ? 'Edit Review' : 'Add Review'} | Gourmet Reserve</title>
    <link href="https://fonts.googleapis.com/css2?family=Playfair+Display:wght@500&family=Roboto:wght@300;400;500&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.3/css/all.min.css">
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
            max-width: 800px;
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

        .error-message {
            background: rgba(231, 76, 60, 0.2);
            border: 1px solid rgba(231, 76, 60, 0.5);
            color: #e74c3c;
        }

        .reservation-summary {
            background: rgba(0, 0, 0, 0.2);
            padding: 1.5rem;
            border-radius: 12px;
            margin-bottom: 2rem;
        }

        .summary-title {
            font-family: 'Playfair Display', serif;
            color: var(--gold);
            margin-bottom: 1rem;
            font-size: 1.2rem;
        }

        .summary-item {
            display: flex;
            margin-bottom: 0.5rem;
            border-bottom: 1px solid rgba(255, 255, 255, 0.1);
            padding-bottom: 0.5rem;
        }

        .summary-item:last-child {
            border-bottom: none;
            margin-bottom: 0;
            padding-bottom: 0;
        }

        .summary-label {
            width: 120px;
            color: #aaa;
        }

        .summary-value {
            flex: 1;
        }

        .form-group {
            margin-bottom: 1.5rem;
        }

        .form-label {
            display: block;
            margin-bottom: 0.8rem;
            color: var(--gold);
            font-weight: 500;
        }

        .form-input {
            width: 100%;
            padding: 1rem;
            background: rgba(255, 255, 255, 0.08);
            border: 1px solid rgba(212, 175, 55, 0.3);
            border-radius: 8px;
            color: var(--text);
            font-size: 1rem;
        }

        .form-input:focus {
            outline: none;
            border-color: var(--gold);
            box-shadow: 0 0 10px rgba(212, 175, 55, 0.2);
        }

        .form-textarea {
            width: 100%;
            padding: 1rem;
            background: rgba(255, 255, 255, 0.08);
            border: 1px solid rgba(212, 175, 55, 0.3);
            border-radius: 8px;
            color: var(--text);
            font-size: 1rem;
            min-height: 150px;
            resize: vertical;
        }

        .form-textarea:focus {
            outline: none;
            border-color: var(--gold);
            box-shadow: 0 0 10px rgba(212, 175, 55, 0.2);
        }

        .rating-group {
            display: flex;
            flex-direction: row-reverse;
            justify-content: flex-end;
        }

        .rating-label {
            color: #aaa;
            font-size: 1.5rem;
            padding: 0 0.2rem;
            cursor: pointer;
            transition: all 0.2s ease;
        }

        .rating-label:hover,
        .rating-label:hover ~ .rating-label,
        .rating-input:checked ~ .rating-label {
            color: var(--gold);
        }

        .rating-input {
            display: none;
        }

        .form-actions {
            display: flex;
            gap: 1rem;
            margin-top: 2rem;
        }

        .btn {
            padding: 1rem;
            border-radius: 8px;
            font-weight: 500;
            transition: all 0.3s ease;
            border: none;
            cursor: pointer;
            flex: 1;
            text-decoration: none;
            text-align: center;
        }

        .btn-primary {
            background: linear-gradient(135deg, var(--gold), var(--burgundy));
            color: white;
        }

        .btn-primary:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3);
        }

        .btn-secondary {
            background: rgba(255, 255, 255, 0.1);
            color: var(--text);
            border: 1px solid rgba(212, 175, 55, 0.3);
        }

        .btn-secondary:hover {
            background: rgba(255, 255, 255, 0.15);
        }

        @keyframes fadeIn {
            from { opacity: 0; transform: translateY(20px); }
            to { opacity: 1; transform: translateY(0); }
        }

        .animated {
            animation: fadeIn 0.6s ease-out forwards;
        }

        @media (max-width: 768px) {
            .main-container {
                width: 95%;
                padding: 1.5rem;
            }

            .form-actions {
                flex-direction: column;
            }

            .rating-label {
                font-size: 1.8rem;
                padding: 0 0.4rem;
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

        String errorMessage = (String) request.getAttribute("errorMessage");

        boolean editMode = request.getAttribute("editMode") != null;

        Reservation reservation = (Reservation) request.getAttribute("reservation");

        Review review = (Review) request.getAttribute("review");

        if (reservation == null) {
            response.sendRedirect(request.getContextPath() + "/reviews/list");
            return;
        }

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

    <nav class="header-nav">
        <a href="${pageContext.request.contextPath}/" class="logo">Gourmet Reserve</a>
        <div class="nav-links">
            <a href="${pageContext.request.contextPath}/reservation/dateSelection">Make Reservation</a>
            <a href="${pageContext.request.contextPath}/user/reservations">My Reservations</a>
            <a href="${pageContext.request.contextPath}/reviews/list" class="active">My Reviews</a>
            <a href="${pageContext.request.contextPath}/user/profile">Profile</a>
            <a href="${pageContext.request.contextPath}/user/logout">Logout</a>
        </div>
    </nav>

    <div class="main-container animated">
        <h1 class="page-title"><%= editMode ? "Edit Review" : "Write a Review" %></h1>

        <% if (errorMessage != null) { %>
            <div class="message error-message">
                <%= errorMessage %>
            </div>
        <% } %>

        <div class="reservation-summary">
            <h3 class="summary-title">Reservation Details</h3>
            <div class="summary-item">
                <div class="summary-label">Date & Time:</div>
                <div class="summary-value"><%= reservation.getReservationDate() %> at <%= reservation.getReservationTime() %></div>
            </div>
            <div class="summary-item">
                <div class="summary-label">Table:</div>
                <div class="summary-value"><%= tableType %> (<%= reservation.getTableId() %>)</div>
            </div>
            <div class="summary-item">
                <div class="summary-label">Duration:</div>
                <div class="summary-value"><%= reservation.getDuration() %> hours</div>
            </div>
            <div class="summary-item">
                <div class="summary-label">Reservation ID:</div>
                <div class="summary-value"><%= reservation.getId() %></div>
            </div>
        </div>

        <form action="${pageContext.request.contextPath}/reviews" method="post">
            <input type="hidden" name="action" value="<%= editMode ? "update" : "create" %>">
            <input type="hidden" name="reservationId" value="<%= reservation.getId() %>">
            <% if (editMode) { %>
                <input type="hidden" name="reviewId" value="<%= review.getId() %>">
            <% } %>

            <div class="form-group">
                <label class="form-label">Rating:</label>
                <div class="rating-group">
                    <% for (int i = 5; i >= 1; i--) { %>
                        <input type="radio" name="rating" value="<%= i %>" class="rating-input" id="rating-<%= i %>"
                               <%= editMode && review.getRating() == i ? "checked" : i == 5 && !editMode ? "checked" : "" %>>
                        <label for="rating-<%= i %>" class="rating-label">
                            <i class="fas fa-star"></i>
                        </label>
                    <% } %>
                </div>
            </div>

            <div class="form-group">
                <label for="title" class="form-label">Review Title:</label>
                <input type="text" id="title" name="title" class="form-input" placeholder="Summarize your experience" required
                       value="<%= editMode ? review.getTitle() : "" %>">
            </div>

            <div class="form-group">
                <label for="comment" class="form-label">Your Review:</label>
                <textarea id="comment" name="comment" class="form-textarea" placeholder="Share the details of your dining experience" required><%= editMode ? review.getComment() : "" %></textarea>
            </div>

            <div class="form-actions">
                <a href="${pageContext.request.contextPath}/reviews/list" class="btn btn-secondary">Cancel</a>
                <button type="submit" class="btn btn-primary"><%= editMode ? "Update Review" : "Submit Review" %></button>
            </div>
        </form>
    </div>

    <script>
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