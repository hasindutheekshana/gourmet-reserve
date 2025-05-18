<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Select Dining Time | Gourmet Reserve</title>
    <link href="https://fonts.googleapis.com/css2?family=Playfair+Display:wght@500&family=Roboto:wght@300;400;500&display=swap" rel="stylesheet">
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
            flex-direction: column;
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

        .datetime-container {
            background: rgba(26, 26, 26, 0.95);
            padding: 3rem;
            border-radius: 20px;
            width: 90%;
            max-width: 500px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.3);
            border: 1px solid rgba(212, 175, 55, 0.2);
            animation: fadeIn 0.5s ease-out;
            margin: 2rem auto;
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

        .datetime-form {
            display: grid;
            gap: 2rem;
        }

        .input-group {
            position: relative;
        }

        .input-label {
            display: block;
            margin-bottom: 1rem;
            color: var(--gold);
            font-weight: 500;
            font-size: 1.1rem;
        }

        .datetime-input {
            width: 100%;
            padding: 1.2rem;
            background: rgba(255, 255, 255, 0.08);
            border: 2px solid rgba(212, 175, 55, 0.3);
            border-radius: 10px;
            color: var(--text);
            font-size: 1rem;
            transition: all 0.3s ease;
        }

        .datetime-input:focus {
            outline: none;
            border-color: var(--gold);
            box-shadow: 0 0 15px rgba(212, 175, 55, 0.2);
        }

        .datetime-input:hover {
            transform: translateY(-2px);
        }

        /* Custom Calendar Icon */
        input[type="date"]::-webkit-calendar-picker-indicator,
        input[type="time"]::-webkit-calendar-picker-indicator {
            filter: invert(1);
            padding: 5px;
            cursor: pointer;
            transition: transform 0.2s ease;
        }

        input[type="date"]::-webkit-calendar-picker-indicator:hover,
        input[type="time"]::-webkit-calendar-picker-indicator:hover {
            transform: scale(1.1);
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

        .error-message {
            color: #ff4444;
            margin-top: 1rem;
            text-align: center;
            animation: shake 0.5s ease-in-out;
            background: rgba(255, 68, 68, 0.1);
            padding: 1rem;
            border-radius: 8px;
            border: 1px solid rgba(255, 68, 68, 0.3);
        }

        @keyframes shake {
            0%, 100% { transform: translateX(0); }
            20%, 60% { transform: translateX(-5px); }
            40%, 80% { transform: translateX(5px); }
        }

        /* Booking Type Styles */
        .booking-type-container {
            margin-bottom: 0;
        }

        .booking-type-options {
            display: flex;
            flex-direction: column;
            gap: 0.8rem;
            background: rgba(255, 255, 255, 0.08);
            border: 2px solid rgba(212, 175, 55, 0.3);
            border-radius: 10px;
            padding: 1rem;
        }

        .booking-option {
            display: flex;
            align-items: center;
            color: var(--text);
        }

        .booking-option input[type="radio"] {
            margin-right: 10px;
            width: 18px;
            height: 18px;
            accent-color: var(--gold);
        }

        .booking-option label {
            font-size: 1rem;
            cursor: pointer;
        }

        /* Business Hours Info */
        .business-hours {
            background: rgba(255, 255, 255, 0.05);
            border-radius: 10px;
            padding: 1rem;
            margin-top: 2rem;
            border: 1px solid rgba(212, 175, 55, 0.2);
        }

        .business-hours h3 {
            color: var(--gold);
            font-size: 1rem;
            margin-bottom: 0.5rem;
            font-weight: 500;
        }

        .hours-item {
            display: flex;
            justify-content: space-between;
            margin-bottom: 0.3rem;
        }

        .day {
            color: #bbb;
        }

        .time {
            color: var(--text);
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
            display: none;
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

        @media (max-width: 768px) {
            .datetime-container {
                padding: 2rem;
            }

            .header h1 {
                font-size: 2rem;
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

        // Get any previously selected values
        String selectedDate = (String) session.getAttribute("reservationDate");
        String selectedTime = (String) session.getAttribute("reservationTime");
        String selectedType = (String) session.getAttribute("bookingType");
        String selectedDuration = (String) session.getAttribute("reservationDuration");

        if (selectedType == null) {
            selectedType = "normal";
        }
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

    <div class="datetime-container">
        <div class="header">
            <h1>Select Your Time</h1>
            <p>Welcome, <%= username %>! Choose your preferred dining date and time</p>
        </div>

        <form class="datetime-form" id="datetimeForm" action="${pageContext.request.contextPath}/reservation/createReservation" method="post">
            <div class="input-group">
                <label class="input-label">Dining Date</label>
                <input type="date" class="datetime-input" id="reservationDate" name="reservationDate" required
                       min="<%= java.time.LocalDate.now() %>"
                       max="<%= java.time.LocalDate.now().plusMonths(2) %>"
                       value="<%= selectedDate != null ? selectedDate : "" %>">
            </div>

            <div class="input-group">
                <label class="input-label">Dining Time</label>
                <input type="time" class="datetime-input" id="reservationTime" name="reservationTime" required
                       min="10:00" max="22:00"
                       value="<%= selectedTime != null ? selectedTime : "" %>">
                <small style="display: block; margin-top: 0.5rem; color: #bbb;">Restaurant hours: 10:00 AM - 10:00 PM</small>
            </div>

            <div class="booking-type-container">
                <label class="input-label">Booking Type</label>
                <div class="booking-type-options">
                    <div class="booking-option">
                        <input type="radio" id="normalBooking" name="bookingType" value="normal"
                               <%= "normal".equals(selectedType) ? "checked" : "" %>>
                        <label for="normalBooking">Normal Booking (2 hours)</label>
                    </div>
                    <div class="booking-option">
                        <input type="radio" id="specialBooking" name="bookingType" value="special"
                               <%= "special".equals(selectedType) ? "checked" : "" %>>
                        <label for="specialBooking">Special Booking</label>
                    </div>
                </div>
            </div>

            <div class="input-group" id="durationContainer" style="display: <%= "special".equals(selectedType) ? "block" : "none" %>;">
                <label class="input-label">Duration (hours)</label>
                <select class="datetime-input" id="reservationDuration" name="reservationDuration">
                    <option value="3" <%= "3".equals(selectedDuration) ? "selected" : "" %>>3 hours</option>
                    <option value="4" <%= "4".equals(selectedDuration) ? "selected" : "" %>>4 hours</option>
                    <option value="5" <%= "5".equals(selectedDuration) ? "selected" : "" %>>5 hours</option>
                    <option value="6" <%= "6".equals(selectedDuration) ? "selected" : "" %>>6 hours</option>
                </select>
            </div>

            <button type="submit" class="proceed-btn" id="submitBtn">Find Available Tables</button>

            <%
                String errorMessage = (String) request.getAttribute("errorMessage");
                if (errorMessage != null) {
            %>
            <div class="error-message">
                <%= errorMessage %>
            </div>
            <% } %>

            <div class="business-hours">
                <h3>Restaurant Business Hours</h3>
                <div class="hours-item">
                    <div class="day">Monday - Thursday:</div>
                    <div class="time">10:00 AM - 10:00 PM</div>
                </div>
                <div class="hours-item">
                    <div class="day">Friday - Saturday:</div>
                    <div class="time">10:00 AM - 11:00 PM</div>
                </div>
                <div class="hours-item">
                    <div class="day">Sunday:</div>
                    <div class="time">11:00 AM - 9:00 PM</div>
                </div>
            </div>
        </form>
    </div>

    <!-- Loading overlay -->
    <div class="loading" id="loadingOverlay">
        <div class="spinner"></div>
    </div>

    <script>
        // Toggle duration field based on booking type
        document.querySelectorAll('input[name="bookingType"]').forEach(radio => {
            radio.addEventListener('change', function() {
                const durationContainer = document.getElementById('durationContainer');
                if (this.value === 'special') {
                    durationContainer.style.display = 'block';
                } else {
                    durationContainer.style.display = 'none';
                }
            });
        });

        // Form validation and submission
        document.getElementById('datetimeForm').addEventListener('submit', function(e) {
            e.preventDefault();

            const date = document.getElementById('reservationDate').value;
            const time = document.getElementById('reservationTime').value;

            if (!date || !time) {
                showError('Please select both date and time');
                return;
            }

            // Check if date is valid
            const selectedDate = new Date(date);
            const today = new Date();
            today.setHours(0, 0, 0, 0);

            if (selectedDate < today) {
                showError('Please select a future date');
                return;
            }

            // Check if time is valid (10:00 AM - 10:00 PM)
            const [hours, minutes] = time.split(':').map(num => parseInt(num));

            if (hours < 10 || (hours === 22 && minutes > 0) || hours > 22) {
                showError('Please select a time between 10:00 AM and 10:00 PM');
                return;
            }

            // If today, check if time is in the past
            if (selectedDate.toDateString() === today.toDateString()) {
                const now = new Date();
                const selectedTime = new Date();
                selectedTime.setHours(hours, minutes, 0, 0);

                if (selectedTime < now) {
                    showError('Please select a future time');
                    return;
                }
            }

            // Check closing time based on duration
            const bookingType = document.querySelector('input[name="bookingType"]:checked').value;
            let duration = 2;

            if (bookingType === 'special') {
                duration = parseInt(document.getElementById('reservationDuration').value);
            }

            // Calculate end time
            const endHours = hours + duration;

            if (endHours > 22) {
                showError('Your booking would end after our closing time (10:00 PM). Please select an earlier time or reduce duration.');
                return;
            }

            // All checks passed, show loading and submit form
            document.getElementById('loadingOverlay').style.display = 'flex';
            this.submit();
        });

        function showError(message) {
            let errorElement = document.querySelector('.error-message');

            if (!errorElement) {
                errorElement = document.createElement('div');
                errorElement.className = 'error-message';
                document.getElementById('datetimeForm').appendChild(errorElement);
            }

            errorElement.textContent = message;
            errorElement.style.display = 'block';

            // Auto-hide error after 5 seconds
            setTimeout(() => {
                errorElement.style.opacity = '0';
                errorElement.style.transition = 'opacity 0.5s';
                setTimeout(() => {
                    errorElement.style.display = 'none';
                    errorElement.style.opacity = '1';
                }, 500);
            }, 5000);
        }

        // Set default date to today if not already selected
        window.addEventListener('load', function() {
            if (!document.getElementById('reservationDate').value) {
                const today = new Date().toISOString().split('T')[0];
                document.getElementById('reservationDate').value = today;
            }

            // Set default time to current time (rounded to nearest hour) if not already selected
            if (!document.getElementById('reservationTime').value) {
                const now = new Date();
                now.setMinutes(0, 0, 0); // Round to nearest hour
                now.setHours(now.getHours() + 1); // Set to next hour

                // Format time as HH:MM
                let hours = now.getHours().toString().padStart(2, '0');
                let minutes = now.getMinutes().toString().padStart(2, '0');

                // Ensure time is within restaurant hours (10:00 - 22:00)
                if (hours < 10) hours = '10';
                if (hours > 22) hours = '20';

                document.getElementById('reservationTime').value = `${hours}:${minutes}`;
            }

            // Auto-hide any error message
            const errorMessage = document.querySelector('.error-message');
            if (errorMessage) {
                setTimeout(() => {
                    errorMessage.style.opacity = '0';
                    errorMessage.style.transition = 'opacity 0.5s';
                    setTimeout(() => {
                        errorMessage.style.display = 'none';
                    }, 500);
                }, 5000);
            }
        });
    </script>
</body>
</html>