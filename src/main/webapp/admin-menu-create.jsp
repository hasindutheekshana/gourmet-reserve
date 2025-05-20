<%@ include file="admin-header.jsp" %>

<h1 style="color: var(--gold); margin-bottom: 2rem;">Add New Menu Item</h1>

<div class="card" style="margin-bottom: 2rem;">
    <form method="post" action="${pageContext.request.contextPath}/admin/menu/create">
        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 2rem;">
            <div class="form-group">
                <label style="display: block; margin-bottom: 0.5rem; color: var(--gold);">Name *</label>
                <input type="text" name="name" required style="width: 100%; padding: 0.8rem; background: rgba(255, 255, 255, 0.1); border: 1px solid var(--gold); border-radius: 6px; color: var(--text);">
            </div>

            <div class="form-group">
                <label style="display: block; margin-bottom: 0.5rem; color: var(--gold);">Category *</label>
                <select name="category" required style="width: 100%; padding: 0.8rem; background: rgba(255, 255, 255, 0.1); border: 1px solid var(--gold); border-radius: 6px; color: var(--text);">
                    <option value="appetizer">Appetizer</option>
                    <option value="main">Main Course</option>
                    <option value="dessert">Dessert</option>
                    <option value="drink">Drink</option>
                </select>
            </div>

            <div class="form-group">
                <label style="display: block; margin-bottom: 0.5rem; color: var(--gold);">Price ($) *</label>
                <input type="number" name="price" step="0.01" min="0" required style="width: 100%; padding: 0.8rem; background: rgba(255, 255, 255, 0.1); border: 1px solid var(--gold); border-radius: 6px; color: var(--text);">
            </div>

            <div class="form-group">
                <label style="display: block; margin-bottom: 0.5rem; color: var(--gold);">Available</label>
                <div style="display: flex; align-items: center;">
                    <input type="checkbox" name="available" id="available" checked style="margin-right: 0.5rem;">
                    <label for="available">Item is available for ordering</label>
                </div>
            </div>
        </div>

        <div class="form-group" style="margin-top: 1.5rem;">
            <label style="display: block; margin-bottom: 0.5rem; color: var(--gold);">Description</label>
            <textarea name="description" style="width: 100%; padding: 0.8rem; background: rgba(255, 255, 255, 0.1); border: 1px solid var(--gold); border-radius: 6px; color: var(--text); min-height: 100px;"></textarea>
        </div>

        <div class="form-group">
            <label style="display: block; margin-bottom: 0.5rem; color: var(--gold);">Image URL (Optional)</label>
            <input type="url" name="imageUrl" style="width: 100%; padding: 0.8rem; background: rgba(255, 255, 255, 0.1); border: 1px solid var(--gold); border-radius: 6px; color: var(--text);">
            <small style="color: #aaa; margin-top: 0.3rem; display: block;">Enter a URL for an image of this menu item (for future use)</small>
        </div>

        <div style="display: flex; gap: 1rem; margin-top: 2rem;">
            <button type="submit" class="action-btn edit-btn" style="flex: 1; background-color: #4CAF50;">Add Menu Item</button>
            <a href="${pageContext.request.contextPath}/admin/menu" class="action-btn delete-btn" style="flex: 1; text-align: center; text-decoration: none;">Cancel</a>
        </div>
    </form>
</div>

<%@ include file="admin-footer.jsp" %>