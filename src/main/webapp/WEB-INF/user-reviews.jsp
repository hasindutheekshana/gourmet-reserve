<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.tablebooknow.model.review.Review" %>
<%@ page import="com.tablebooknow.model.reservation.Reservation" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Reviews | Gourmet Reserve</title>
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

        .section-title {
            font-size: 1.5rem;
            color: var(--gold);
            margin: 2rem 0 1rem;
            font-family: 'Playfair Display', serif;
            border-bottom: 1px solid rgba(212, 175, 55, 0.2);
            padding-bottom: 0.5rem;
        }

        .review-card {
            margin-bottom: 1.5rem;
            background: rgba(255, 255, 255, 0.05);
            border-radius: 12px;
            overflow: hidden;
            transition: transform 0.3s ease;
            border: 1px solid rgba(212, 175, 55, 0.2);
        }

        .review-card:hover {
            transform: translateY(-3px);
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3);
            border-color: rgba(212, 175, 55, 0.4);
        }

        .review-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 1.2rem;
            background: rgba(0, 0, 0, 0.3);
        }

        .review-title {
            font-size: 1.2rem;
            font-weight: 500;
            color: var(--gold);
        }

        .review-date {
            font-size: 0.8rem;
            color: #aaa;
        }

        .review-body {
            padding: 1.2rem;
        }

        .review-info {
            display: flex;
            justify-content: space-between;
            margin-bottom: 1rem;
        }

        .review-rating {
            color: var(--gold);
        }

        .star {
            color: var(--gold);
            margin-right: 2px;
        }

        .review-reservation {
            color: #aaa;
            font-size: 0.9rem;
        }

        .review-comment {
            margin-top: 1rem;
            line-height: 1.6;
        }

        .review-actions {
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
            text-decoration: none;
            display: inline-block;
        }

        .btn-edit {
            background: rgba(52, 152, 219, 0.8);
            color: white;
        }

        .btn-edit:hover {
            background: #3498db;
            transform: translateY(-2px);
        }

        .btn-delete {
            background: rgba(231, 76, 60, 0.8);
            color: white;
        }

        .btn-delete:hover {
            background: #e74c3c;
            transform: translateY(-2px);
        }

        .btn-add {
            background: linear-gradient(135deg, var(--gold), var(--burgundy));
            color: white;
            padding: 1rem 1.5rem;
            font-weight: 500;
            border-radius: 8px;
            text-decoration: none;
            display: inline-block;
            text-align: center;
            margin-top: 1rem;
            transition: all 0.3s ease;
        }

        .btn-add:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3);
        }

        .no-reviews {
            text-align: center;
            padding: 3rem 1rem;
            background: rgba(0, 0, 0, 0.2);
            border-radius: 12px;
            margin: 2rem 0;
        }

        .no-reviews h3 {
            color: var(--gold);
            margin-bottom: 1rem;
        }

        .no-reviews p {
            margin-bottom: 1.5rem;
            opacity: 0.9;
        }

        .reservations-to-review {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
            gap: 1.5rem;
        }

        .reservation-card {
            background: rgba(255, 255, 255, 0.05);
            border-radius: 10px;
            padding: 1.2rem;
            border: 1px solid rgba(212, 175, 55, 0.2);
            transition: all 0.3s ease;
        }

        .reservation-card:hover {
            transform: translateY(-3px);
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3);
            border-color: rgba(212, 175, 55, 0.4);
        }

        .reservation-info {
            margin-bottom: 1rem;
        }

        .reservation-date {
            color: var(--gold);
            font-weight: 500;
            margin-bottom: 0.5rem;
        }

        .reservation-details {
            font-size: 0.9rem;
            color: #aaa;
        }

        .delete-confirm {
            display: none;
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: rgba(0, 0, 0, 0.8);
            z-index: 1000;
            justify-content: center;
            align-items: center;
        }

        .confirm-box {
            background: rgba(26, 26, 26, 0.95);
            border-radius: 15px;
            width: 90%;
            max-width: 400px;
            padding: 2rem;
            border: 1px solid rgba(212, 175, 55, 0.3);
        }

        .confirm-title {
            font-family: 'Playfair Display', serif;
            color: var(--gold);
            margin-bottom: 1rem;
        }

        .confirm-message {
            margin-bottom: 1.5rem;
            line-height: 1.6;
        }

        .confirm-actions {
            display: flex;
            justify-content: space-between;
            gap: 1rem;
        }

        .btn-cancel {
            background: rgba(255, 255, 255, 0.1);
            color: var(--text);
            flex: 1;
        }

        .btn-confirm {
            background: rgba(231, 76, 60, 0.8);
            color: white;
            flex: 1;
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

            .reservations-to-review {
                grid-template-columns: 1fr;
            }

            .review-actions {
                flex-direction: column;
            }

            .action-btn {
                width: 100%;
                text-align: center;
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

        String successMessage = (String) request.getAttribute("successMessage");
        String errorMessage = (String) request.getAttribute("errorMessage");


        List<Review> userReviews = (List<Review>) request.getAttribute("reviews");


        List<Reservation> completedReservations = (List<Reservation>) request.getAttribute("completedReservations");
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
        <h1 class="page-title">My Reviews & Feedback</h1>

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

        <% if (completedReservations != null && !completedReservations.isEmpty()) { %>
            <h2 class="section-title">Reservations Ready for Review</h2>
            <div class="reservations-to-review">
                <% for (Reservation reservation : completedReservations) {
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
                        <div class="reservation-info">
                            <div class="reservation-date"><%= reservation.getReservationDate() %> at <%= reservation.getReservationTime() %></div>
                            <div class="reservation-details">
                                <div><strong>Table:</strong> <%= tableType %> (<%= reservation.getTableId() %>)</div>
                                <div><strong>Duration:</strong> <%= reservation.getDuration() %> hours</div>
                            </div>
                        </div>
                        <a href="${pageContext.request.contextPath}/reviews/add?reservationId=<%= reservation.getId() %>" class="btn-add">Write a Review</a>
                    </div>
                <% } %>
            </div>
        <% } %>

        <h2 class="section-title">My Past Reviews</h2>

        <% if (userReviews != null && !userReviews.isEmpty()) { %>
            <%
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            for (Review review : userReviews) {
                String createdAtStr = review.getCreatedAt() != null ?
                    review.getCreatedAt().format(formatter) : "";
                String updatedAtStr = review.getUpdatedAt() != null ?
                    review.getUpdatedAt().format(formatter) : "";

                String dateDisplay = createdAtStr;
                if (!createdAtStr.equals(updatedAtStr)) {
                    dateDisplay += " (Updated: " + updatedAtStr + ")";
                }
            %>
                <div class="review-card">
                    <div class="review-header">
                        <div class="review-title"><%= review.getTitle() %></div>
                        <div class="review-date"><%= dateDisplay %></div>
                    </div>
                    <div class="review-body">
                        <div class="review-info">
                            <div class="review-rating">
                                <% for (int i = 0; i < 5; i++) { %>
                                    <% if (i < review.getRating()) { %>
                                        <i class="fas fa-star star"></i>
                                    <% } else { %>
                                        <i class="far fa-star star"></i>
                                    <% } %>
                                <% } %>
                                (<%= review.getRating() %>/5)
                            </div>
                            <div class="review-reservation">
                                Reservation ID: <%= review.getReservationId() %>
                            </div>
                        </div>
                        <div class="review-comment">
                            <%= review.getComment() %>
                        </div>
                        <div class="review-actions">
                            <a href="${pageContext.request.contextPath}/reviews/edit?reviewId=<%= review.getId() %>" class="action-btn btn-edit">Edit Review</a>
                            <a href="#" class="action-btn btn-delete" onclick="confirmDelete('<%= review.getId() %>')">Delete Review</a>
                        </div>
                    </div>
                </div>
            <% } %>
        <% } else { %>
            <div class="no-reviews">
                <h3>No Reviews Found</h3>
                <p>You haven't written any reviews yet.</p>
                <% if (completedReservations != null && !completedReservations.isEmpty()) { %>
                    <p>You have <%= completedReservations.size() %> completed reservations ready to review.</p>
                <% } %>
            </div>
        <% } %>
    </div>

    <div class="delete-confirm" id="deleteConfirm">
        <div class="confirm-box">
            <h2 class="confirm-title">Confirm Deletion</h2>
            <p class="confirm-message">Are you sure you want to delete this review? This action cannot be undone.</p>
            <div class="confirm-actions">
                <button class="action-btn btn-cancel" onclick="cancelDelete()">Cancel</button>
                <form action="${pageContext.request.contextPath}/reviews" method="post" id="deleteForm">
                    <input type="hidden" name="action" value="delete">
                    <input type="hidden" name="reviewId" id="deleteReviewId">
                    <button type="submit" class="action-btn btn-confirm">Delete</button>
                </form>
            </div>
        </div>
    </div>

    <script>
        function confirmDelete(reviewId) {
            document.getElementById('deleteReviewId').value = reviewId;
            document.getElementById('deleteConfirm').style.display = 'flex';
        }

        function cancelDelete() {
            document.getElementById('deleteConfirm').style.display = 'none';
        }

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