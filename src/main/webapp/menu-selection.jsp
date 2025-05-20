<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="com.tablebooknow.model.menu.MenuItem" %>
<%@ page import="com.tablebooknow.model.reservation.Reservation" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Select Menu Items | Gourmet Reserve</title>
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

        .container {
            width: 90%;
            max-width: 1200px;
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
            text-align: center;
            margin-bottom: 1.5rem;
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

        .menu-container {
            display: flex;
            flex-wrap: wrap;
            gap: 2rem;
        }

        .menu-category {
            flex: 1;
            min-width: 300px;
        }

        .category-title {
            font-family: 'Playfair Display', serif;
            color: var(--gold);
            margin-bottom: 1rem;
            border-bottom: 1px solid rgba(212, 175, 55, 0.2);
            padding-bottom: 0.5rem;
            text-transform: capitalize;
        }

        .menu-item {
            background: rgba(255, 255, 255, 0.05);
            margin-bottom: 1rem;
            border-radius: 10px;
            overflow: hidden;
            transition: all 0.3s;
            border: 1px solid rgba(212, 175, 55, 0.1);
        }

        .menu-item:hover {
            transform: translateY(-5px);
            border-color: var(--gold);
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3);
        }

        .menu-item-header {
            padding: 1rem;
            display: flex;
            justify-content: space-between;
            align-items: center;
            background: rgba(0, 0, 0, 0.3);
        }

        .menu-item-name {
            font-weight: 500;
            color: var(--gold);
        }

        .menu-item-price {
            color: var(--text);
        }

        .menu-item-body {
            padding: 1rem;
        }

        .menu-item-description {
            margin-bottom: 1rem;
            opacity: 0.8;
            font-size: 0.9rem;
        }

        .menu-item-actions {
            display: flex;
            gap: 0.5rem;
        }

        .quantity-input {
            display: flex;
            align-items: center;
            background: rgba(0, 0, 0, 0.2);
            border-radius: 5px;
            overflow: hidden;
        }

        .quantity-btn {
            width: 30px;
            height: 30px;
            background: rgba(0, 0, 0, 0.3);
            border: none;
            color: var(--text);
            font-size: 1.2rem;
            cursor: pointer;
            transition: all 0.3s;
        }

        .quantity-btn:hover {
            background: rgba(212, 175, 55, 0.2);
        }

        .quantity-value {
            width: 40px;
            text-align: center;
            background: transparent;
            border: none;
            color: var(--text);
            font-size: 0.9rem;
        }

        .add-btn {
            padding: 0.5rem 1rem;
            background: linear-gradient(135deg, var(--gold), var(--burgundy));
            border: none;
            border-radius: 5px;
            color: white;
            cursor: pointer;
            transition: all 0.3s;
            flex: 1;
        }

        .add-btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 3px 10px rgba(0, 0, 0, 0.2);
        }

        .order-summary {
            background: rgba(0, 0, 0, 0.2);
            border-radius: 10px;
            padding: 1.5rem;
            margin-top: 2rem;
            border: 1px solid rgba(212, 175, 55, 0.2);
        }

        .order-title {
            font-family: 'Playfair Display', serif;
            color: var(--gold);
            margin-bottom: 1rem;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .order-title span {
            font-size: 0.9rem;
            opacity: 0.8;
            font-family: 'Roboto', sans-serif;
        }

        .selected-items {
            margin-bottom: 1.5rem;
        }

        .selected-item {
            display: flex;
            justify-content: space-between;
            padding: 0.8rem;
            border-bottom: 1px solid rgba(255, 255, 255, 0.1);
        }

        .selected-item:last-child {
            border-bottom: none;
        }

        .selected-item-info {
            display: flex;
            flex-direction: column;
        }

        .selected-item-name {
            font-weight: 500;
        }

        .selected-item-price {
            font-size: 0.9rem;
            opacity: 0.8;
        }

        .selected-item-actions {
            display: flex;
            align-items: center;
            gap: 1rem;
        }

        .remove-btn {
            padding: 0.3rem 0.6rem;
            background: rgba(231, 76, 60, 0.8);
            border: none;
            border-radius: 5px;
            color: white;
            cursor: pointer;
            font-size: 0.8rem;
            transition: all 0.3s;
        }

        .remove-btn:hover {
            background: #e74c3c;
        }

        .action-buttons {
            display: flex;
            gap: 1rem;
            margin-top: 2rem;
        }

        .primary-btn, .secondary-btn {
            padding: 1rem 1.5rem;
            border-radius: 8px;
            font-weight: 500;
            cursor: pointer;
            text-align: center;
            flex: 1;
            transition: all 0.3s;
            text-decoration: none;
            display: inline-block;
        }

        .primary-btn {
            background: linear-gradient(135deg, var(--gold), var(--burgundy));
            border: none;
            color: white;
        }

        .secondary-btn {
            background: rgba(255, 255, 255, 0.1);
            border: 1px solid rgba(212, 175, 55, 0.3);
            color: var(--text);
        }

        .primary-btn:hover, .secondary-btn:hover {
            transform: translateY(-3px);
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3);
        }

        .loading {
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: rgba(0, 0, 0, 0.8);
            display: none;
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

        /* Responsive Adjustments */
        @media (max-width: 768px) {
            .container {
                width: 95%;
                padding: 1rem;
            }

            .menu-category {
                min-width: 100%;
            }

            .action-buttons {
                flex-direction: column;
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

        Reservation reservation = (Reservation) request.getAttribute("reservation");
        Map<String, List<MenuItem>> itemsByCategory = (Map<String, List<MenuItem>>) request.getAttribute("itemsByCategory");
        Map<MenuItem, Integer> selectedItems = (Map<MenuItem, Integer>) request.getAttribute("selectedItems");

        if (reservation == null) {
            response.sendRedirect(request.getContextPath() + "/user/reservations");
            return;
        }

        String errorMessage = (String) request.getAttribute("errorMessage");
    %>

    <nav class="header-nav">
        <a href="${pageContext.request.contextPath}/" class="logo">Gourmet Reserve</a>
        <div class="nav-links">
            <a href="${pageContext.request.contextPath}/reservation/dateSelection">Make Reservation</a>
            <a href="${pageContext.request.contextPath}/user/reservations" class="active">My Reservations</a>
            <a href="${pageContext.request.contextPath}/user/profile">Profile</a>
            <a href="${pageContext.request.contextPath}/user/logout">Logout</a>
        </div>
    </nav>

    <div class="container">
        <h1 class="page-title">Add Menu Items to Your Reservation</h1>

        <% if (errorMessage != null && !errorMessage.isEmpty()) { %>
            <div class="error-message">
                <%= errorMessage %>
            </div>
        <% } %>

        <div class="reservation-info">
            <p>Reservation for <span><%= reservation.getReservationDate() %></span> at <span><%= reservation.getReservationTime() %></span> • Table <span><%= reservation.getTableId() %></span></p>
            <p style="font-size: 0.9rem; margin-top: 0.5rem; opacity: 0.8;">Select menu items you would like to pre-order for your reservation. You can also skip this step and order at the restaurant.</p>
        </div>

        <div class="menu-container">
            <% if (itemsByCategory != null && !itemsByCategory.isEmpty()) {
                for (Map.Entry<String, List<MenuItem>> entry : itemsByCategory.entrySet()) {
                    String category = entry.getKey();
                    List<MenuItem> items = entry.getValue();

                    if (items == null || items.isEmpty()) {
                        continue;
                    }
            %>
            <div class="menu-category">
                <h2 class="category-title"><%= category %></h2>

                <% for (MenuItem item : items) { %>
                <div class="menu-item">
                    <div class="menu-item-header">
                        <div class="menu-item-name"><%= item.getName() %></div>
                        <div class="menu-item-price">$<%= item.getPrice() %></div>
                    </div>
                    <div class="menu-item-body">
                        <p class="menu-item-description"><%= item.getDescription() != null ? item.getDescription() : "No description available" %></p>
                        <div class="menu-item-actions">
                            <div class="quantity-input">
                                <button class="quantity-btn decrease-btn" data-item-id="<%= item.getId() %>">-</button>
                                <input type="number" class="quantity-value" value="1" min="1" max="10" data-item-id="<%= item.getId() %>">
                                <button class="quantity-btn increase-btn" data-item-id="<%= item.getId() %>">+</button>
                            </div>
                            <button class="add-btn" data-item-id="<%= item.getId() %>" data-item-name="<%= item.getName() %>" data-item-price="<%= item.getPrice() %>">Add to Order</button>
                        </div>
                    </div>
                </div>
                <% } %>
            </div>
            <% } } else { %>
            <p style="text-align: center; padding: 2rem;">No menu items available. Please contact the restaurant staff.</p>
            <% } %>
        </div>

        <div class="order-summary">
            <div class="order-title">
                Your Order <span>Pre-order items to have them ready when you arrive</span>
            </div>

            <div class="selected-items" id="selected-items-container">
                <% if (selectedItems != null && !selectedItems.isEmpty()) {
                    for (Map.Entry<MenuItem, Integer> entry : selectedItems.entrySet()) {
                        MenuItem item = entry.getKey();
                        Integer quantity = entry.getValue();
                %>
                <div class="selected-item" data-item-id="<%= item.getId() %>">
                    <div class="selected-item-info">
                        <div class="selected-item-name"><%= item.getName() %> × <%= quantity %></div>
                        <div class="selected-item-price">$<%= item.getPrice() %> each</div>
                    </div>
                    <div class="selected-item-actions">
                        <button class="remove-btn" data-item-id="<%= item.getId() %>">Remove</button>
                    </div>
                </div>
                <% } } else { %>
                <p style="text-align: center; padding: 1rem; opacity: 0.8;">No items selected yet.</p>
                <% } %>
            </div>

            <div class="action-buttons">
                <form method="post" action="${pageContext.request.contextPath}/reservationMenu/saveSelections" id="save-form">
                    <input type="hidden" name="reservationId" value="<%= reservation.getId() %>">
                    <button type="submit" class="primary-btn">Save Menu Selections</button>
                </form>

                <form method="post" action="${pageContext.request.contextPath}/reservationMenu/skipSelection" id="skip-form">
                    <input type="hidden" name="reservationId" value="<%= reservation.getId() %>">
                    <button type="submit" class="secondary-btn">Skip Menu Selection</button>
                </form>
            </div>
        </div>
    </div>

    <div class="loading" id="loadingOverlay">
        <div class="spinner"></div>
    </div>

    <script>
        document.addEventListener('DOMContentLoaded', function() {
            const reservationId = '<%= reservation.getId() %>';
            const selectedItemsContainer = document.getElementById('selected-items-container');
            const saveForm = document.getElementById('save-form');
            const skipForm = document.getElementById('skip-form');
            const loadingOverlay = document.getElementById('loadingOverlay');

            const selectedItems = {};

            <% if (selectedItems != null && !selectedItems.isEmpty()) {
                for (Map.Entry<MenuItem, Integer> entry : selectedItems.entrySet()) {
                    MenuItem item = entry.getKey();
                    Integer quantity = entry.getValue();
            %>
            selectedItems['<%= item.getId() %>'] = {
                id: '<%= item.getId() %>',
                name: '<%= item.getName() %>',
                price: <%= item.getPrice() %>,
                quantity: <%= quantity %>
            };
            <% } } %>

            document.querySelectorAll('.increase-btn').forEach(btn => {
                btn.addEventListener('click', function() {
                    const itemId = this.dataset.itemId;
                    const input = document.querySelector(`.quantity-value[data-item-id="${itemId}"]`);
                    let value = parseInt(input.value, 10);
                    if (value < 10) {
                        input.value = value + 1;
                    }
                });
            });

            document.querySelectorAll('.decrease-btn').forEach(btn => {
                btn.addEventListener('click', function() {
                    const itemId = this.dataset.itemId;
                    const input = document.querySelector(`.quantity-value[data-item-id="${itemId}"]`);
                    let value = parseInt(input.value, 10);
                    if (value > 1) {
                        input.value = value - 1;
                    }
                });
            });

            document.querySelectorAll('.add-btn').forEach(btn => {
                btn.addEventListener('click', function() {
                    const itemId = this.dataset.itemId;
                    const itemName = this.dataset.itemName;
                    const itemPrice = parseFloat(this.dataset.itemPrice);
                    const quantityInput = document.querySelector(`.quantity-value[data-item-id="${itemId}"]`);
                    const quantity = parseInt(quantityInput.value, 10);

                    addMenuItem(itemId, itemName, itemPrice, quantity);

                    quantityInput.value = 1;
                });
            });

            selectedItemsContainer.addEventListener('click', function(e) {
                if (e.target.classList.contains('remove-btn')) {
                    const itemId = e.target.dataset.itemId;
                    removeMenuItem(itemId);
                }
            });

            saveForm.addEventListener('submit', function(e) {
                if (Object.keys(selectedItems).length === 0) {
                    e.preventDefault();
                    alert('Please select at least one menu item or click "Skip Menu Selection".');
                    return;
                }

                loadingOverlay.style.display = 'flex';
            });

            skipForm.addEventListener('submit', function() {
                loadingOverlay.style.display = 'flex';
            });

            function addMenuItem(itemId, itemName, itemPrice, quantity) {
                if (selectedItems[itemId]) {
                    selectedItems[itemId].quantity += quantity;

                    const existingItem = document.querySelector(`.selected-item[data-item-id="${itemId}"]`);
                    if (existingItem) {
                        const nameElement = existingItem.querySelector('.selected-item-name');
                        nameElement.textContent = `${itemName} × ${selectedItems[itemId].quantity}`;
                    }
                } else {
                    selectedItems[itemId] = {
                        id: itemId,
                        name: itemName,
                        price: itemPrice,
                        quantity: quantity
                    };

                    const emptyMessage = selectedItemsContainer.querySelector('p');
                    if (emptyMessage) {
                        emptyMessage.remove();
                    }

                    const itemElement = document.createElement('div');
                    itemElement.className = 'selected-item';
                    itemElement.dataset.itemId = itemId;
                    itemElement.innerHTML = `
                        <div class="selected-item-info">
                            <div class="selected-item-name">${itemName} × ${quantity}</div>
                            <div class="selected-item-price">$${itemPrice} each</div>
                        </div>
                        <div class="selected-item-actions">
                            <button class="remove-btn" data-item-id="${itemId}">Remove</button>
                        </div>
                    `;
                    selectedItemsContainer.appendChild(itemElement);
                }

                saveMenuItem(itemId, quantity);
            }

            function removeMenuItem(itemId) {
                delete selectedItems[itemId];

                const itemElement = document.querySelector(`.selected-item[data-item-id="${itemId}"]`);
                if (itemElement) {
                    itemElement.remove();
                }

                if (Object.keys(selectedItems).length === 0) {
                    selectedItemsContainer.innerHTML = '<p style="text-align: center; padding: 1rem; opacity: 0.8;">No items selected yet.</p>';
                }

                removeMenuItemFromServer(itemId);
            }

            function saveMenuItem(itemId, quantity) {
                const xhr = new XMLHttpRequest();
                xhr.open('POST', '${pageContext.request.contextPath}/reservationMenu/addItem', true);
                xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
                xhr.onreadystatechange = function() {
                    if (xhr.readyState === 4) {
                        if (xhr.status === 200) {
                            console.log('Menu item added successfully');
                        } else {
                            console.error('Error adding menu item');
                        }
                    }
                };

                const params = `reservationId=${reservationId}&menuItemId=${itemId}&quantity=${quantity}`;
                xhr.send(params);
            }

            function removeMenuItemFromServer(itemId) {
                const xhr = new XMLHttpRequest();
                xhr.open('POST', '${pageContext.request.contextPath}/reservationMenu/removeItem', true);
                xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
                xhr.onreadystatechange = function() {
                    if (xhr.readyState === 4) {
                        if (xhr.status === 200) {
                            console.log('Menu item removed successfully');
                        } else {
                            console.error('Error removing menu item');
                        }
                    }
                };

                const params = `reservationMenuItemId=${itemId}`;
                xhr.send(params);
            }

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
        });
    </script>
</body>
</html>
