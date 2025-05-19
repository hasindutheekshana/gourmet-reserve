<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.tablebooknow.model.reservation.Reservation" %>
<%@ page import="com.tablebooknow.model.user.User" %>
<%@ page import="java.time.LocalDate" %>
<%@ page import="java.time.LocalTime" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Dashboard | Gourmet Reserve</title>
    <link href="https://fonts.googleapis.com/css2?family=Playfair+Display:wght@500&family=Roboto:wght@300;400;500&display=swap" rel="stylesheet">
    <script src="https://unpkg.com/html5-qrcode"></script>
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
            color: var(--text);
        }

        .dashboard-container {
            display: grid;
            grid-template-columns: 250px 1fr;
            min-height: 100vh;
        }

        .sidebar {
            background: rgba(20, 20, 20, 0.95);
            padding: 2rem;
            border-right: 1px solid rgba(212, 175, 55, 0.3);
            position: relative;
            min-height: 100vh;
        }

        .main-content {
            padding: 2rem;
            background: rgba(30, 30, 30, 0.95);
        }

        .nav-item {
            padding: 1rem;
            margin: 0.5rem 0;
            border-radius: 8px;
            cursor: pointer;
            transition: all 0.3s ease;
            color: var(--text);
            text-decoration: none;
            display: block;
        }

        .nav-item:hover {
            background: rgba(212, 175, 55, 0.1);
        }

        .active-section {
            background: rgba(212, 175, 55, 0.2);
            color: var(--gold);
        }

        .card {
            background: rgba(40, 40, 40, 0.6);
            padding: 1.5rem;
            border-radius: 10px;
            margin-bottom: 1.5rem;
            border: 1px solid rgba(212, 175, 55, 0.2);
        }

        .stats-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
            gap: 1rem;
            margin-bottom: 2rem;
        }

        .stat-card {
            background: rgba(30, 30, 30, 0.8);
            border: 1px solid rgba(212, 175, 55, 0.3);
            padding: 1.5rem;
            border-radius: 8px;
            text-align: center;
        }

        .stat-number {
            font-size: 2.5rem;
            font-weight: bold;
            color: var(--gold);
            margin: 0.5rem 0;
        }

        .stat-label {
            color: #ccc;
            font-size: 0.9rem;
        }

        .table-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
            gap: 1rem;
        }

        .table-card {
            padding: 1.5rem;
            border-radius: 8px;
            text-align: center;
            transition: transform 0.3s ease;
        }

        .status-available { background: rgba(76, 175, 80, 0.2); }
        .status-reserved { background: rgba(255, 193, 7, 0.2); }
        .status-occupied { background: rgba(244, 67, 54, 0.2); }

        .qr-scanner {
            width: 100%;
            max-width: 500px;
            margin: 2rem auto;
            border: 2px solid var(--gold);
            border-radius: 15px;
            overflow: hidden;
        }

        .data-table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 1rem;
        }

        .data-table th, .data-table td {
            padding: 1rem;
            text-align: left;
            border-bottom: 1px solid rgba(212, 175, 55, 0.1);
        }

        .action-btn {
            padding: 0.5rem 1rem;
            margin: 0 0.3rem;
            border-radius: 6px;
            border: none;
            cursor: pointer;
            transition: transform 0.3s ease;
        }

        .edit-btn { background: var(--gold); color: var(--dark); }
        .delete-btn { background: #f44336; color: white; }

        .message {
            padding: 1rem;
            margin-bottom: 1rem;
            border-radius: 8px;
        }

        .success-message {
            background: rgba(76, 175, 80, 0.2);
            border: 1px solid rgba(76, 175, 80, 0.5);
        }

        .error-message {
            background: rgba(244, 67, 54, 0.2);
            border: 1px solid rgba(244, 67, 54, 0.5);
        }

        .warning-message {
            background: rgba(255, 193, 7, 0.2);
            border: 1px solid rgba(255, 193, 7, 0.5);
        }

        .logout-btn {
            position: absolute;
            bottom: 2rem;
            left: 2rem;
            padding: 0.8rem 1.5rem;
            background: rgba(244, 67, 54, 0.8);
            color: white;
            border: none;
            border-radius: 6px;
            cursor: pointer;
            transition: all 0.3s ease;
            text-decoration: none;
        }

        .logout-btn:hover {
            background: rgba(244, 67, 54, 1);
            transform: translateY(-2px);
        }

        .badge {
            display: inline-block;
            padding: 0.25rem 0.5rem;
            border-radius: 50%;
            background-color: #f44336;
            color: white;
            font-size: 0.75rem;
            margin-left: 0.5rem;
        }
    </style>
</head>
<body>
    <%
        // Check if admin is logged in
        if (session.getAttribute("adminId") == null) {
            response.sendRedirect(request.getContextPath() + "/admin/login");
            return;
        }

        String adminUsername = (String) session.getAttribute("adminUsername");

        // Success/Error messages
        String successMessage = (String) request.getAttribute("successMessage");
        String errorMessage = (String) request.getAttribute("errorMessage");
        String warningMessage = (String) request.getAttribute("warningMessage");

        // Get current servlet path for highlighting active navigation
        String currentServletPath = request.getServletPath();
    %>

    <div class="dashboard-container">
        <div class="sidebar">
            <h2 style="color: var(--gold); margin-bottom: 2rem;">Admin Panel</h2>
            <p style="margin-bottom: 2rem; color: #ccc;">Welcome, <%= adminUsername %></p>

            <a href="${pageContext.request.contextPath}/admin/dashboard" class="nav-item <%= currentServletPath.contains("admin-dashboard") ? "active-section" : "" %>"> Dashboard</a>
            <a href="${pageContext.request.contextPath}/admin/reservations" class="nav-item <%= currentServletPath.contains("admin-reservations") && !currentServletPath.contains("queue") ? "active-section" : "" %>"> Reservations</a>
            <a href="${pageContext.request.contextPath}/admin/reservations/queue" class="nav-item <%= currentServletPath.contains("admin-reservation-queue") ? "active-section" : "" %>"> Reservation Queue</a>
            <a href="${pageContext.request.contextPath}/admin/reservations/sorted" class="nav-item"> Sorted Reservations</a>
            <a href="${pageContext.request.contextPath}/admin/reviews" class="nav-item <%= currentServletPath.contains("admin-reviews") ? "active-section" : "" %>"> Customer Reviews</a>
            <a href="${pageContext.request.contextPath}/admin/tables" class="nav-item <%= currentServletPath.contains("admin-table") ? "active-section" : "" %>"> Table Management</a>
            <a href="${pageContext.request.contextPath}/admin/users" class="nav-item <%= currentServletPath.contains("admin-users") ? "active-section" : "" %>"> User Management</a>
            <a href="${pageContext.request.contextPath}/admin/menu" class="nav-item <%= currentServletPath.contains("admin-menu") ? "active-section" : "" %>"> Menu Management</a>
            <a href="${pageContext.request.contextPath}/admin/qr" class="nav-item <%= currentServletPath.contains("admin-qr") ? "active-section" : "" %>"> QR Scanner</a>
            <a href="${pageContext.request.contextPath}/admin/logout" class="logout-btn">Logout</a>
        </div>

        <div class="main-content">
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

            <% if (warningMessage != null) { %>
                <div class="message warning-message">
                    <%= warningMessage %>
                </div>
            <% } %>