<%@ include file="admin-header.jsp" %>
<%@ page import="com.tablebooknow.model.table.Table" %>

<%
    Table table = (Table) request.getAttribute("table");
    boolean editMode = (Boolean) request.getAttribute("editMode");
    String formTitle = editMode ? "Edit Table" : "Add New Table";
    String formAction = editMode ?
            request.getContextPath() + "/admin/tables/update" :
            request.getContextPath() + "/admin/tables/create";
%>

<h1 style="color: var(--gold); margin-bottom: 2rem;"><%= formTitle %></h1>

<div class="card" style="max-width: 800px; margin: 0 auto;">
    <form id="tableForm" method="post" action="<%= formAction %>" onsubmit="return validateForm()">
        <% if (editMode && table != null) { %>
        <input type="hidden" name="tableId" value="<%= table.getId() %>">
        <% } %>

        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1.5rem;">
            <div class="form-group">
                <label for="tableNumber" style="display: block; margin-bottom: 0.5rem; color: var(--gold);">Table Number*</label>
                <input type="text" id="tableNumber" name="tableNumber" value="<%= editMode && table != null ? table.getTableNumber() : "" %>"
                       style="width: 100%; padding: 0.8rem; background: rgba(255, 255, 255, 0.1); border: 1px solid var(--gold); border-radius: 6px; color: var(--text); font-size: 1rem;" required>
                <small style="display: block; margin-top: 0.3rem; color: #aaa;">Example: 1, 2, 3, etc.</small>
            </div>

            <div class="form-group">
                <label for="tableType" style="display: block; margin-bottom: 0.5rem; color: var(--gold);">Table Type*</label>
                <select id="tableType" name="tableType" style="width: 100%; padding: 0.8rem; background: rgba(255, 255, 255, 0.1); border: 1px solid var(--gold); border-radius: 6px; color: var(--text); font-size: 1rem;" required>
                    <option value="">--Select Type--</option>
                    <option value="family" <%= editMode && table != null && "family".equalsIgnoreCase(table.getTableType()) ? "selected" : "" %>>Family (6 seats)</option>
                    <option value="luxury" <%= editMode && table != null && "luxury".equalsIgnoreCase(table.getTableType()) ? "selected" : "" %>>Luxury (10 seats)</option>
                    <option value="regular" <%= editMode && table != null && "regular".equalsIgnoreCase(table.getTableType()) ? "selected" : "" %>>Regular (4 seats)</option>
                    <option value="couple" <%= editMode && table != null && "couple".equalsIgnoreCase(table.getTableType()) ? "selected" : "" %>>Couple (2 seats)</option>
                </select>
            </div>

            <div class="form-group">
                <label for="floor" style="display: block; margin-bottom: 0.5rem; color: var(--gold);">Floor*</label>
                <select id="floor" name="floor" style="width: 100%; padding: 0.8rem; background: rgba(255, 255, 255, 0.1); border: 1px solid var(--gold); border-radius: 6px; color: var(--text); font-size: 1rem;" required>
                    <option value="">--Select Floor--</option>
                    <option value="1" <%= editMode && table != null && table.getFloor() == 1 ? "selected" : "" %>>Floor 1</option>
                    <option value="2" <%= editMode && table != null && table.getFloor() == 2 ? "selected" : "" %>>Floor 2</option>
                </select>
            </div>

            <div class="form-group">
                <label for="capacity" style="display: block; margin-bottom: 0.5rem; color: var(--gold);">Capacity*</label>
                <input type="number" id="capacity" name="capacity" min="1" max="20" value="<%= editMode && table != null ? table.getCapacity() : "" %>"
                       style="width: 100%; padding: 0.8rem; background: rgba(255, 255, 255, 0.1); border: 1px solid var(--gold); border-radius: 6px; color: var(--text); font-size: 1rem;" required>
                <small style="display: block; margin-top: 0.3rem; color: #aaa;">Number of seats</small>
            </div>

            <div class="form-group" style="grid-column: 1 / 3;">
                <label for="locationDescription" style="display: block; margin-bottom: 0.5rem; color: var(--gold);">Location Description</label>
                <textarea id="locationDescription" name="locationDescription" style="width: 100%; padding: 0.8rem; background: rgba(255, 255, 255, 0.1); border: 1px solid var(--gold); border-radius: 6px; color: var(--text); font-size: 1rem; min-height: 100px;"><%= editMode && table != null && table.getLocationDescription() != null ? table.getLocationDescription() : "" %></textarea>
                <small style="display: block; margin-top: 0.3rem; color: #aaa;">Optional descriptive information about the table location</small>
            </div>

            <div class="form-group" style="display: flex; align-items: center;">
                <input type="checkbox" id="isActive" name="isActive" <%= editMode && table != null && table.isActive() ? "checked" : "" %>
                       style="width: 18px; height: 18px; margin-right: 10px;">
                <label for="isActive" style="color: var(--gold); cursor: pointer;">Active</label>
            </div>
        </div>

        <div style="display: flex; gap: 1rem; margin-top: 2rem;">
            <button type="submit" class="action-btn edit-btn" style="flex: 1;">Save Table</button>
            <a href="${pageContext.request.contextPath}/admin/tables" class="action-btn delete-btn" style="flex: 1; text-align: center; text-decoration: none;">Cancel</a>
        </div>

        <div id="form-errors" style="color: #f44336; margin-top: 1rem; display: none;"></div>
    </form>
</div>

<script>
    function validateForm() {
        const tableNumber = document.getElementById('tableNumber').value;
        const tableType = document.getElementById('tableType').value;
        const floor = document.getElementById('floor').value;
        const capacity = document.getElementById('capacity').value;
        const errorsContainer = document.getElementById('form-errors');

        let errors = [];

        if (!tableNumber) {
            errors.push("Table number is required");
        }

        if (!tableType) {
            errors.push("Table type is required");
        }

        if (!floor) {
            errors.push("Floor is required");
        }

        if (!capacity) {
            errors.push("Capacity is required");
        } else if (isNaN(capacity) || parseInt(capacity) < 1) {
            errors.push("Capacity must be a positive number");
        }

        // Display errors if any
        if (errors.length > 0) {
            errorsContainer.innerHTML = errors.join('<br>');
            errorsContainer.style.display = 'block';
            return false;
        }

        // Auto-set capacity based on table type if not explicitly set
        if (tableType && (!capacity || capacity === '')) {
            const capacityInput = document.getElementById('capacity');

            switch(tableType.toLowerCase()) {
                case 'family':
                    capacityInput.value = 6;
                    break;
                case 'luxury':
                    capacityInput.value = 10;
                    break;
                case 'regular':
                    capacityInput.value = 4;
                    break;
                case 'couple':
                    capacityInput.value = 2;
                    break;
                default:
                    capacityInput.value = '';
            }

            capacityInput.setAttribute('data-auto-filled', 'true');
        }

        return true;
    }

    // Auto-update capacity when table type changes
    document.getElementById('tableType').addEventListener('change', function() {
        const capacityInput = document.getElementById('capacity');
        const tableType = this.value;

        // Only auto-fill if the user hasn't entered a custom value or the field is empty
        if (!capacityInput.value || capacityInput.getAttribute('data-auto-filled') === 'true') {
            switch(tableType) {
                case 'family':
                    capacityInput.value = 6;
                    break;
                case 'luxury':
                    capacityInput.value = 10;
                    break;
                case 'regular':
                    capacityInput.value = 4;
                    break;
                case 'couple':
                    capacityInput.value = 2;
                    break;
                default:
                    capacityInput.value = '';
            }

            capacityInput.setAttribute('data-auto-filled', 'true');
        }
    });

    // Mark capacity as user-modified when changed manually
    document.getElementById('capacity').addEventListener('input', function() {
        this.setAttribute('data-auto-filled', 'false');
    });
</script>

<%@ include file="admin-footer.jsp" %>