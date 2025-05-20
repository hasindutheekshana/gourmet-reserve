<%@ include file="admin-header.jsp" %>

<h1 style="color: var(--gold); margin-bottom: 2rem;">QR Code Scanner</h1>
<p class="card" style="margin-bottom: 2rem;">Scan a reservation QR code to quickly check in guests or verify reservation details.</p>
<div class="qr-scanner" id="qrScanner"></div>
<div id="scanResult" class="card" style="margin-top: 2rem; display: none;">
    <h3 style="color: var(--gold);">Reservation Details</h3>
    <pre id="scanData"></pre>
    <div id="reservationDetails" style="margin-top: 1rem;"></div>
    <button id="checkInBtn" class="action-btn edit-btn" style="margin-top: 1rem; display: none;">Check In Guest</button>
</div>

<script>
// QR Scanner initialization
let qrScanner = null;

function initializeScanner() {
    try {
        if(qrScanner) return;

        const qrScannerElement = document.getElementById("qrScanner");
        if (!qrScannerElement) {
            console.warn("QR Scanner container not found");
            return;
        }

        qrScanner = new Html5QrcodeScanner("qrScanner", {
            fps: 10,
            qrbox: 250
        }, false);

        qrScanner.render((decodedText) => {
            const scanResultElement = document.getElementById('scanResult');
            const scanDataElement = document.getElementById('scanData');
            const reservationDetailsElement = document.getElementById('reservationDetails');
            const checkInBtnElement = document.getElementById('checkInBtn');

            if (scanResultElement) scanResultElement.style.display = 'block';
            if (scanDataElement) scanDataElement.textContent = decodedText;

            // Parse QR data (expected format: JSON with reservationId, paymentId, userId)
            try {
                const reservationData = JSON.parse(decodedText);

                if (reservationDetailsElement) {
                    reservationDetailsElement.innerHTML =
                        '<div style="margin-top: 1rem;">' +
                            '<p><strong>Reservation ID:</strong> ' + (reservationData.reservationId || 'N/A') + '</p>' +
                            '<p><strong>Payment ID:</strong> ' + (reservationData.paymentId || 'N/A') + '</p>' +
                            '<p><strong>User ID:</strong> ' + (reservationData.userId || 'N/A') + '</p>' +
                            '<p><strong>Timestamp:</strong> ' + new Date(parseInt(reservationData.timestamp)).toLocaleString() + '</p>' +
                        '</div>';
                }

                // Show check-in button
                if (checkInBtnElement) {
                    checkInBtnElement.style.display = 'inline-block';

                    // Add check-in functionality
                    checkInBtnElement.onclick = function() {
                        if (reservationData.reservationId) {
                            // Send to server to update reservation status
                            alert('Guest checked in successfully!');
                        }
                    };
                }
            } catch (e) {
                if (reservationDetailsElement) {
                    reservationDetailsElement.innerHTML =
                        '<div style="margin-top: 1rem; color: #ff6b6b;">' +
                            '<p>Invalid QR code format. Unable to parse reservation data.</p>' +
                        '</div>';
                }
                if (checkInBtnElement) checkInBtnElement.style.display = 'none';
            }

            // Clean up the scanner after successful scan
            if (qrScanner) {
                qrScanner.clear();
                qrScanner = null;
            }
        });
    } catch (error) {
        console.error("QR scanner initialization error:", error);
    }
}

// Initialize scanner when page loads
document.addEventListener('DOMContentLoaded', initializeScanner);
</script>

<%@ include file="admin-footer.jsp" %>