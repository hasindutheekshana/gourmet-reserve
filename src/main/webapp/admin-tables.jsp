<%@ include file="admin-header.jsp" %>
<%@ page import="com.tablebooknow.model.table.Table" %>
<%@ page import="java.util.List" %>

<%
    List<Table> tables = (List<Table>) request.getAttribute("tables");
    Integer tableCount = (Integer) request.getAttribute("tableCount");
    Integer totalTables = (Integer) request.getAttribute("totalTables");
    String searchTerm = (String) request.getAttribute("searchTerm");
    String floorFilter = (String) request.getAttribute("floorFilter");
    String typeFilter = (String) request.getAttribute("typeFilter");
    String floorTitle = (String) request.getAttribute("floorTitle");

    if (tableCount == null) tableCount = 0;
    if (totalTables == null) totalTables = 0;
%>

<h1 style="color: var(--gold); margin-bottom: 2rem;">Table Management</h1>

<div style="display: flex; gap: 1rem; margin-bottom: 2rem;">
    <a href="${pageContext.request.contextPath}/admin/tables/add" class="action-btn edit-btn" style="display: flex; align-items: center; padding: 0.75rem 1.5rem; background-color: #4CAF50;">
        <span style="margin-right: 0.5rem;">➕</span> Add New Table
    </a>
</div>

<div class="card">
    <div style="margin-bottom: 1.5rem;">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1rem;">
            <h2><%= floorTitle != null ? floorTitle : "All Tables" %> (<%= tableCount %>)</h2>
            <div>
                <a href="${pageContext.request.contextPath}/admin/tables" class="action-btn edit-btn" style="padding: 0.5rem 1rem; <%= (floorFilter == null && typeFilter == null) ? "background-color: #4CAF50;" : "" %>">All</a>
                <a href="${pageContext.request.contextPath}/admin/tables/floor?floorNumber=1" class="action-btn edit-btn" style="padding: 0.5rem 1rem; <%= "1".equals(floorFilter) ? "background-color: #FFC107;" : "" %>">Floor 1</a>
                <a href="${pageContext.request.contextPath}/admin/tables/floor?floorNumber=2" class="action-btn edit-btn" style="padding: 0.5rem 1rem; <%= "2".equals(floorFilter) ? "background-color: #2196F3;" : "" %>">Floor 2</a>
            </div>
        </div>
        <div style="display: flex; justify-content: space-between; margin-bottom: 1rem;">
            <div style="display: flex; gap: 0.5rem;">
                <a href="${pageContext.request.contextPath}/admin/tables<%= floorFilter != null ? "/floor?floorNumber=" + floorFilter : "" %>" class="action-btn edit-btn" style="padding: 0.5rem 1rem; <%= typeFilter == null ? "background-color: #4CAF50;" : "" %>">All Types</a>
                <a href="${pageContext.request.contextPath}/admin/tables<%= floorFilter != null ? "/floor?floorNumber=" + floorFilter + "&" : "?" %>type=family" class="action-btn edit-btn" style="padding: 0.5rem 1rem; <%= "family".equals(typeFilter) ? "background-color: #FFC107;" : "" %>">Family</a>
                <a href="${pageContext.request.contextPath}/admin/tables<%= floorFilter != null ? "/floor?floorNumber=" + floorFilter + "&" : "?" %>type=luxury" class="action-btn edit-btn" style="padding: 0.5rem 1rem; <%= "luxury".equals(typeFilter) ? "background-color: #2196F3;" : "" %>">Luxury</a>
                <a href="${pageContext.request.contextPath}/admin/tables<%= floorFilter != null ? "/floor?floorNumber=" + floorFilter + "&" : "?" %>type=regular" class="action-btn edit-btn" style="padding: 0.5rem 1rem; <%= "regular".equals(typeFilter) ? "background-color: #9C27B0;" : "" %>">Regular</a>
                <a href="${pageContext.request.contextPath}/admin/tables<%= floorFilter != null ? "/floor?floorNumber=" + floorFilter + "&" : "?" %>type=couple" class="action-btn edit-btn" style="padding: 0.5rem 1rem; <%= "couple".equals(typeFilter) ? "background-color: #FF5722;" : "" %>">Couple</a>
            </div>
        </div>
        <form method="get" action="${pageContext.request.contextPath}/admin/tables" style="display: flex; gap: 1rem; margin-top: 1rem;">
            <input type="text" name="search" placeholder="Search by table number, type or location..." value="<%= searchTerm != null ? searchTerm : "" %>"
                   style="flex: 1; padding: 0.5rem; border-radius: 5px; background: rgba(255, 255, 255, 0.1); color: var(--text); border: 1px solid rgba(212, 175, 55, 0.3);">
            <% if (floorFilter != null && !floorFilter.isEmpty()) { %>
            <input type="hidden" name="floor" value="<%= floorFilter %>">
            <% } %>
            <% if (typeFilter != null && !typeFilter.isEmpty()) { %>
            <input type="hidden" name="type" value="<%= typeFilter %>">
            <% } %>
            <button type="submit" class="action-btn edit-btn">Search</button>
            <% if (searchTerm != null && !searchTerm.isEmpty()) { %>
            <a href="${pageContext.request.contextPath}/admin/tables<%= floorFilter != null ? "/floor?floorNumber=" + floorFilter : "" %><%= typeFilter != null ? (floorFilter != null ? "&type=" : "?type=") + typeFilter : "" %>" class="action-btn delete-btn" style="padding: 0.5rem 1rem;">Clear</a>
            <% } %>
        </form>
    </div>

    <table class="data-table">
        <thead>
        <tr>
            <th>Table ID</th>
            <th>Number</th>
            <th>Type</th>
            <th>Capacity</th>
            <th>Floor</th>
            <th>Status</th>
            <th>Actions</th>
        </tr>
        </thead>
        <tbody>
        <%
            if (tables != null && !tables.isEmpty()) {
                for (Table table : tables) {
        %>
        <tr>
            <td><%= table.getId() %></td>
            <td><%= table.getTableNumber() %></td>
            <td>
                <%
                    String badgeColor = "";
                    if ("family".equalsIgnoreCase(table.getTableType())) {
                        badgeColor = "#FFC107";
                    } else if ("luxury".equalsIgnoreCase(table.getTableType())) {
                        badgeColor = "#2196F3";
                    } else if ("regular".equalsIgnoreCase(table.getTableType())) {
                        badgeColor = "#9C27B0";
                    } else if ("couple".equalsIgnoreCase(table.getTableType())) {
                        badgeColor = "#FF5722";
                    }
                %>
                <span style="background-color: <%= badgeColor %>; color: white; padding: 3px 8px; border-radius: 12px; font-size: 0.8rem;">
                        <%= table.getTableType() != null ? table.getTableType().toUpperCase() : "UNKNOWN" %>
                    </span>
            </td>
            <td><%= table.getCapacity() %></td>
            <td><%= table.getFloor() %></td>
            <td>
                    <span style="color: <%= table.isActive() ? "#4CAF50" : "#F44336" %>;">
                        <%= table.isActive() ? "Active" : "Inactive" %>
                    </span>
            </td>
            <td>
                <div style="display: flex; gap: 0.5rem;">
                    <a href="${pageContext.request.contextPath}/admin/tables/view?id=<%= table.getId() %>" class="action-btn edit-btn">View</a>
                    <a href="${pageContext.request.contextPath}/admin/tables/edit?id=<%= table.getId() %>" class="action-btn edit-btn" style="background-color: #2196F3;">Edit</a>
                    <form method="post" action="${pageContext.request.contextPath}/admin/tables/delete" onsubmit="return confirmDelete('<%= table.getId() %>');">
                        <input type="hidden" name="tableId" value="<%= table.getId() %>">
                        <button type="submit" class="action-btn delete-btn">Delete</button>
                    </form>
                </div>
            </td>
        </tr>
        <%
            }
        } else {
        %>
        <tr>
            <td colspan="7" style="text-align: center;">
                <% if (searchTerm != null && !searchTerm.isEmpty()) { %>
                No tables found matching "<%= searchTerm %>".
                <% } else if (floorFilter != null && !floorFilter.isEmpty()) { %>
                No tables found on floor <%= floorFilter %>.
                <% } else if (typeFilter != null && !typeFilter.isEmpty()) { %>
                No <%= typeFilter %> tables found.
                <% } else { %>
                No tables found. <a href="${pageContext.request.contextPath}/admin/tables/add" style="color: var(--gold);">Add your first table</a>
                <% } %>
            </td>
        </tr>
        <% } %>
        </tbody>
    </table>
</div>

<div class="card" style="margin-top: 2rem; padding: 1.5rem;">
    <h3 style="margin-bottom: 1rem; color: var(--gold);">Table Management Guide</h3>
    <p style="margin-bottom: 1rem;">Tables are organized by floor and type, with each table having a unique identifier.</p>

    <div style="margin-bottom: 1rem;">
        <h4 style="margin-bottom: 0.5rem; color: var(--gold);">Table Types</h4>
        <ul style="list-style-type: disc; margin-left: 1.5rem;">
            <li><strong>Family Tables:</strong> Large tables for groups, capacity of 6</li>
            <li><strong>Luxury Tables:</strong> Premium tables for special occasions, capacity of 10</li>
            <li><strong>Regular Tables:</strong> Standard tables, capacity of 4</li>
            <li><strong>Couple Tables:</strong> Small tables for couples, capacity of 2</li>
        </ul>
    </div>

    <div style="margin-bottom: 1rem;">
        <h4 style="margin-bottom: 0.5rem; color: var(--gold);">Table ID Format</h4>
        <p>Table IDs follow the format: [type-initial][floor]-[number]</p>
        <p>For example: <code>f1-3</code> means Family table on Floor 1, number 3</p>
    </div>
</div>

<script>
    function confirmDelete(tableId) {
        return confirm('Are you sure you want to delete this table? This action cannot be undone.');
    }
</script>

<%@ include file="admin-footer.jsp" %>