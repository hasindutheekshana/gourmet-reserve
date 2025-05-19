<%@ include file="admin-header.jsp" %>
<%@ page import="com.tablebooknow.dao.ReviewDAO" %>
<%@ page import="com.tablebooknow.dao.UserDAO" %>
<%@ page import="com.tablebooknow.dao.ReservationDAO" %>
<%@ page import="com.tablebooknow.model.review.Review" %>
<%@ page import="com.tablebooknow.model.user.User" %>
<%@ page import="com.tablebooknow.model.reservation.Reservation" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Map" %>

<%
    ReviewDAO reviewDAO = new ReviewDAO();
    UserDAO userDAO = new UserDAO();
    ReservationDAO reservationDAO = new ReservationDAO();

    List<Review> allReviews = reviewDAO.findAll();

   allReviews.sort((r1, r2) -> r2.getCreatedAt().compareTo(r1.getCreatedAt()));

    double averageRating = reviewDAO.getAverageRating();

    Map<String, User> userCache = new HashMap<>();
    Map<String, Reservation> reservationCache = new HashMap<>();

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
%>

<h1 style="color: var(--gold); margin-bottom: 2rem;">Customer Reviews</h1>

<div class="card" style="margin-bottom: 2rem;">
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1rem;">
        <h2 style="margin-bottom: 0;">Reviews Overview</h2>
        <div style="display: flex; align-items: center; gap: 1rem;">
            <div style="display: flex; flex-direction: column; align-items: center; background: rgba(0,0,0,0.2); padding: 0.8rem; border-radius: 8px; min-width: 100px;">
                <span style="font-size: 2rem; font-weight: bold; color: var(--gold);"><%= String.format("%.1f", averageRating) %></span>
                <span style="font-size: 0.9rem; opacity: 0.8;">Average</span>
            </div>
            <div style="display: flex; flex-direction: column; align-items: center; background: rgba(0,0,0,0.2); padding: 0.8rem; border-radius: 8px; min-width: 100px;">
                <span style="font-size: 2rem; font-weight: bold; color: var(--gold);"><%= allReviews.size() %></span>
                <span style="font-size: 0.9rem; opacity: 0.8;">Total</span>
            </div>
        </div>
    </div>

    <div style="background: rgba(0,0,0,0.2); padding: 1rem; border-radius: 8px; margin-bottom: 1.5rem;">
        <p><strong>Rating Distribution:</strong></p>
        <div style="display: flex; gap: 1rem; margin-top: 0.8rem;">
            <%
                // Calculate rating distribution
                int[] ratingCounts = new int[5];
                for (Review review : allReviews) {
                    if (review.getRating() >= 1 && review.getRating() <= 5) {
                        ratingCounts[review.getRating() - 1]++;
                    }
                }

                for (int i = 4; i >= 0; i--) {
                    int count = ratingCounts[i];
                    double percentage = allReviews.isEmpty() ? 0 : (count * 100.0 / allReviews.size());
            %>
            <div style="flex: 1; display: flex; flex-direction: column;">
                <div style="display: flex; justify-content: space-between; margin-bottom: 0.3rem;">
                    <span><%= i + 1 %> star</span>
                    <span><%= count %></span>
                </div>
                <div style="background: rgba(255,255,255,0.1); height: 8px; border-radius: 4px; overflow: hidden;">
                    <div style="background: var(--gold); height: 100%; width: <%= percentage %>%;"></div>
                </div>
            </div>
            <% } %>
        </div>
    </div>
</div>

<div class="card">
    <h2 style="margin-bottom: 1.5rem;">All Reviews</h2>

    <table class="data-table">
        <thead>
            <tr>
                <th>ID</th>
                <th>User</th>
                <th>Reservation</th>
                <th>Rating</th>
                <th>Title</th>
                <th>Comment</th>
                <th>Date</th>
                <th>Actions</th>
            </tr>
        </thead>
        <tbody>
            <% for (Review review : allReviews) {
                User user = userCache.get(review.getUserId());
                if (user == null) {
                    try {
                        user = userDAO.findById(review.getUserId());
                        if (user != null) {
                            userCache.put(review.getUserId(), user);
                        }
                    } catch (Exception e) {
                    }
                }

                Reservation reservation = reservationCache.get(review.getReservationId());
                if (reservation == null) {
                    try {
                        reservation = reservationDAO.findById(review.getReservationId());
                        if (reservation != null) {
                            reservationCache.put(review.getReservationId(), reservation);
                        }
                    } catch (Exception e) {
                    }
                }

                String createdAtStr = review.getCreatedAt() != null ?
                    review.getCreatedAt().format(formatter) : "";

                String tableInfo = "";
                if (reservation != null && reservation.getTableId() != null) {
                    char typeChar = reservation.getTableId().charAt(0);
                    String tableType = "Regular";
                    if (typeChar == 'f') tableType = "Family";
                    else if (typeChar == 'l') tableType = "Luxury";
                    else if (typeChar == 'c') tableType = "Couple";
                    tableInfo = tableType + " (" + reservation.getTableId() + ")";
                }

                String comment = review.getComment();
                if (comment != null && comment.length() > 100) {
                    comment = comment.substring(0, 100) + "...";
                }

                String ratingColor = "#4CAF50";
                if (review.getRating() <= 2) {
                    ratingColor = "#F44336";
                } else if (review.getRating() == 3) {
                    ratingColor = "#FFC107";
                }
            %>
            <tr>
                <td><%= review.getId().substring(0, 8) %>...</td>
                <td><%= user != null ? user.getUsername() : "Unknown User" %></td>
                <td>
                    <% if (reservation != null) { %>
                        <%= reservation.getReservationDate() %><br>
                        <span style="font-size: 0.8rem; color: #aaa;"><%= tableInfo %></span>
                    <% } else { %>
                        Unknown
                    <% } %>
                </td>
                <td>
                    <div style="display: flex; color: <%= ratingColor %>;">
                        <% for (int i = 0; i < review.getRating(); i++) { %>
                            <i class="fas fa-star" style="margin-right: 2px;"></i>
                        <% } %>
                    </div>
                </td>
                <td><%= review.getTitle() %></td>
                <td><%= comment %></td>
                <td><%= createdAtStr %></td>
                <td>
                    <button class="action-btn" style="background: rgba(52, 152, 219, 0.8); color: white;"
                            onclick="viewReview('<%= review.getId() %>', '<%= user != null ? user.getUsername() : "Unknown" %>', '<%= review.getRating() %>', '<%= review.getTitle().replace("'", "\\'") %>', '<%= review.getComment().replace("'", "\\'").replace("\n", "\\n") %>')">
                        View
                    </button>
                    <form style="display: inline;" method="post" action="${pageContext.request.contextPath}/admin/reviews/delete"
                          onsubmit="return confirm('Are you sure you want to delete this review? This action cannot be undone.');">
                        <input type="hidden" name="reviewId" value="<%= review.getId() %>">
                        <button type="submit" class="action-btn delete-btn">Delete</button>
                    </form>
                </td>
            </tr>
            <% } %>
        </tbody>
    </table>
</div>

<div id="reviewModal" style="display: none; position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.8); z-index: 1000; justify-content: center; align-items: center;">
    <div style="background: rgba(26, 26, 26, 0.95); border-radius: 15px; width: 90%; max-width: 600px; padding: 2rem; border: 1px solid rgba(212, 175, 55, 0.3);">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.5rem;">
            <h2 id="modalTitle" style="color: var(--gold);"></h2>
            <button onclick="closeModal()" style="background: none; border: none; color: #aaa; font-size: 1.5rem; cursor: pointer;">&times;</button>
        </div>

        <div style="margin-bottom: 1rem;">
            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1rem;">
                <div id="modalUsername" style="font-weight: 500;"></div>
                <div id="modalRating" style="color: var(--gold);"></div>
            </div>
            <div id="modalContent" style="background: rgba(0,0,0,0.2); padding: 1.5rem; border-radius: 8px; white-space: pre-line; max-height: 300px; overflow-y: auto;"></div>
        </div>

        <div style="text-align: right; margin-top: 1.5rem;">
            <button onclick="closeModal()" class="action-btn" style="background: var(--gold); color: var(--dark);">Close</button>
        </div>
    </div>
</div>

<script>
    document.head.innerHTML += '<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.3/css/all.min.css">';

    function viewReview(id, username, rating, title, content) {
        document.getElementById('modalTitle').textContent = title;
        document.getElementById('modalUsername').textContent = username;

        let ratingHTML = '';
        for (let i = 0; i < rating; i++) {
            ratingHTML += '<i class="fas fa-star" style="color: var(--gold); margin-right: 2px;"></i>';
        }
        for (let i = rating; i < 5; i++) {
            ratingHTML += '<i class="far fa-star" style="color: var(--gold); margin-right: 2px;"></i>';
        }
        document.getElementById('modalRating').innerHTML = ratingHTML;

        document.getElementById('modalContent').textContent = content;

        document.getElementById('reviewModal').style.display = 'flex';
    }

    function closeModal() {
        document.getElementById('reviewModal').style.display = 'none';
    }

    document.getElementById('reviewModal').addEventListener('click', function(e) {
        if (e.target === this) {
            closeModal();
        }
    });
</script>

<%@ include file="admin-footer.jsp" %>