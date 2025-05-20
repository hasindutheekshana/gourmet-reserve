<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.tablebooknow.model.user.User" %>

<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>User Profile | Gourmet Reserve</title>
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
            background: var(--dark);
            font-family: 'Roboto', sans-serif;
            background-image:
                linear-gradient(rgba(0,0,0,0.9), rgba(0,0,0,0.9));

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

        .nav-links a:hover {
            color: var(--gold);
        }

        .profile-container {
            width: 90%;
            max-width: 800px;
            margin: 2rem auto;
            background: rgba(26, 26, 26, 0.9);
            border-radius: 15px;
            overflow: hidden;
            border: 1px solid rgba(212, 175, 55, 0.2);
            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.4);
        }

        .profile-header {
            background: linear-gradient(135deg, rgba(128, 0, 32, 0.8), rgba(26, 26, 26, 0.8));
            padding: 2rem;
            text-align: center;
            position: relative;
        }

        .profile-pic {
            width: 120px;
            height: 120px;
            border-radius: 50%;
            border: 3px solid var(--gold);
            margin: 0 auto 1rem;
            display: flex;
            justify-content: center;
            align-items: center;
            font-size: 3rem;
            color: var(--gold);
            background: rgba(255, 255, 255, 0.1);
        }

        .profile-name {
            font-family: 'Playfair Display', serif;
            font-size: 2rem;
            color: var(--gold);
            margin-bottom: 0.5rem;
        }

        .profile-role {
            color: var(--text);
            font-size: 1rem;
            opacity: 0.8;
        }

        .profile-tabs {
            display: flex;
            border-bottom: 1px solid rgba(212, 175, 55, 0.2);
        }

        .profile-tab {
            flex: 1;
            padding: 1rem;
            text-align: center;
            cursor: pointer;
            transition: all 0.3s;
        }

        .profile-tab.active {
            background: rgba(212, 175, 55, 0.1);
            color: var(--gold);
            border-bottom: 2px solid var(--gold);
        }

        .profile-tab:hover {
            background: rgba(212, 175, 55, 0.05);
        }

        .profile-content {
            padding: 2rem;
        }

        .tab-content {
            display: none;
        }

        .tab-content.active {
            display: block;
            animation: fadeIn 0.5s ease-out;
        }

        @keyframes fadeIn {
            from { opacity: 0; transform: translateY(10px); }
            to { opacity: 1; transform: translateY(0); }
        }

        .form-group {
            margin-bottom: 1.5rem;
        }

        .form-label {
            display: block;
            margin-bottom: 0.5rem;
            color: var(--gold);
        }

        .form-input {
            width: 100%;
            padding: 0.8rem;
            background: rgba(255, 255, 255, 0.1);
            border: 1px solid rgba(212, 175, 55, 0.3);
            border-radius: 8px;
            color: var(--text);
            font-size: 1rem;
        }

        .form-input:focus {
            outline: none;
            border-color: var(--gold);
        }

        .submit-btn {
            display: block;
            width: 100%;
            padding: 1rem;
            margin-top: 1.5rem;
            background: linear-gradient(135deg, var(--gold), var(--burgundy));
            border: none;
            border-radius: 8px;
            color: white;
            font-size: 1.1rem;
            font-weight: 500;
            cursor: pointer;
            transition: all 0.3s;
        }

        .submit-btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3);
        }

        .info-item {
            margin-bottom: 1.5rem;
        }

        .info-label {
            font-size: 0.9rem;
            color: var(--gold);
            margin-bottom: 0.3rem;
        }

        .info-value {
            font-size: 1.1rem;
            word-break: break-all;
        }

        .message {
            padding: 1rem;
            margin-bottom: 1.5rem;
            border-radius: 8px;
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

        @media (max-width: 768px) {
            .profile-container {
                width: 95%;
            }

            .profile-pic {
                width: 100px;
                height: 100px;
                font-size: 2.5rem;
            }

            .profile-name {
                font-size: 1.8rem;
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
        User user = (User) request.getAttribute("user");

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String firstLetter = username.substring(0, 1).toUpperCase();

        String successMessage = (String) request.getAttribute("successMessage");
        String errorMessage = (String) request.getAttribute("errorMessage");
    %>

        <nav class="header-nav">
            <a href="${pageContext.request.contextPath}/" class="logo">Gourmet Reserve</a>
            <div class="nav-links">
                <a href="${pageContext.request.contextPath}/reservation/dateSelection">Make Reservation</a>
                <a href="${pageContext.request.contextPath}/user/reservations">My Reservations</a>
                <a href="${pageContext.request.contextPath}/user/profile" style="color: var(--gold);">Profile</a>
                <a href="${pageContext.request.contextPath}/user/logout">Logout</a>
            </div>
        </nav>

    <div class="profile-container">
        <div class="profile-header">
            <div class="profile-pic"><%= firstLetter %></div>
            <h1 class="profile-name"><%= username %></h1>
            <p class="profile-role"><%= user.isAdmin() ? "Administrator" : "Member" %></p>
        </div>

        <div class="profile-tabs">
            <div class="profile-tab active" data-tab="profile-info">Account Information</div>
            <div class="profile-tab" data-tab="profile-edit">Edit Profile</div>
        </div>

        <div class="profile-content">
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

            <div class="tab-content active" id="profile-info">
                <div class="info-item">
                    <div class="info-label">Username</div>
                    <div class="info-value"><%= user.getUsername() %></div>
                </div>

                <div class="info-item">
                    <div class="info-label">Email</div>
                    <div class="info-value"><%= user.getEmail() != null ? user.getEmail() : "Not provided" %></div>
                </div>

                <div class="info-item">
                    <div class="info-label">Phone</div>
                    <div class="info-value"><%= user.getPhone() != null ? user.getPhone() : "Not provided" %></div>
                </div>
            </div>

            <div class="tab-content" id="profile-edit">
                <form method="post" action="${pageContext.request.contextPath}/user/profile">
                    <input type="hidden" name="action" value="update-profile">

                    <div class="form-group">
                        <label class="form-label">Email</label>
                        <input type="email" name="email" class="form-input" value="<%= user.getEmail() != null ? user.getEmail() : "" %>">
                    </div>

                    <div class="form-group">
                        <label class="form-label">Phone</label>
                        <input type="tel" name="phone" class="form-input" value="<%= user.getPhone() != null ? user.getPhone() : "" %>">
                    </div>

                    <div class="form-group">
                        <label class="form-label">Current Password (required to change password)</label>
                        <input type="password" name="currentPassword" class="form-input">
                    </div>

                    <div class="form-group">
                        <label class="form-label">New Password</label>
                        <input type="password" name="newPassword" class="form-input">
                    </div>

                    <div class="form-group">
                        <label class="form-label">Confirm New Password</label>
                        <input type="password" name="confirmPassword" class="form-input">
                    </div>

                    <button type="submit" class="submit-btn">Save Changes</button>
                </form>
            </div>
        </div>
    </div>

    <script>
        document.querySelectorAll('.profile-tab').forEach(tab => {
            tab.addEventListener('click', function() {
                document.querySelectorAll('.profile-tab').forEach(t => {
                    t.classList.remove('active');
                });

                this.classList.add('active');

                document.querySelectorAll('.tab-content').forEach(content => {
                    content.classList.remove('active');
                });

                const tabId = this.getAttribute('data-tab');
                document.getElementById(tabId).classList.add('active');
            });
        });

        setTimeout(() => {
            const messages = document.querySelectorAll('.message');
            messages.forEach(message => {
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