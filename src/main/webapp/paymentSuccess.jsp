<% if (paymentSuccessful) { %>
    <div class="instruction">
        <p>A confirmation email has been sent to your registered email address.</p>
        <p>Please arrive 15 minutes before your reservation time.</p>

        <%-- Add QR code display --%>
        <div style="margin: 20px auto; text-align: center;">
            <p>Scan this QR code when you arrive:</p>
            <div style="position: relative; background-color: #fff; width: 180px; height: 180px; margin: 0 auto; padding: 10px; border: 1px solid #ddd; display: flex; align-items: center; justify-content: center;">
                <span style="font-weight: bold; font-family: monospace; font-size: 24px; z-index: 3;">QR</span>
                <%-- Simulated QR code elements --%>
                <div style="position: absolute; top: 20px; right: 20px; width: 30px; height: 30px; border: 5px solid #000;"></div>
                <div style="position: absolute; top: 20px; left: 20px; width: 30px; height: 30px; border: 5px solid #000;"></div>
                <div style="position: absolute; bottom: 20px; left: 20px; width: 30px; height: 30px; border: 5px solid #000;"></div>
                <div style="position: absolute; top: 25px; left: 25px; width: 130px; height: 130px; background-image: linear-gradient(to right, #000 1px, transparent 1px), linear-gradient(to bottom, #000 1px, transparent 1px); background-size: 10px 10px; opacity: 0.2;"></div>
            </div>
            <p style="font-size: 12px; margin-top: 10px; color: #999;">Reservation ID: <%= reservationId %></p>
        </div>
    </div>
<% } else { %>
    <%-- Existing error section --%>
    <div class="instruction">
        <p>Please try again or contact our support team for assistance.</p>
        <p>Email: support@gourmetreserve.com</p>
        <p>Phone: +1-800-GOURMET</p>
    </div>
<% } %>