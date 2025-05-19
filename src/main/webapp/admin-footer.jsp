</div> <!-- Close main-content div -->
    </div> <!-- Close dashboard-container div -->

    <script>
        // Auto-hide alerts after 5 seconds
        setTimeout(() => {
            try {
                const messages = document.querySelectorAll('.message');
                if (messages && messages.length > 0) {
                    messages.forEach(msg => {
                        if (msg) {
                            msg.style.opacity = '0';
                            setTimeout(() => {
                                if (msg) {
                                    msg.style.display = 'none';
                                }
                            }, 500);
                        }
                    });
                }
            } catch (error) {
                console.error("Error hiding alerts:", error);
            }
        }, 5000);
    </script>
</body>
</html>