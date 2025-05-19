const container = document.querySelector('.container');
const registerBtn = document.querySelector('.register-btn');
const loginBtn = document.querySelector('.login-btn');

registerBtn.addEventListener('click', () => {
    container.classList.add('active');
});

loginBtn.addEventListener('click', () => {
    container.classList.remove('active');
});

// Check for any URL parameters that might indicate we should show registration form
document.addEventListener('DOMContentLoaded', function() {
    const urlParams = new URLSearchParams(window.location.search);
    if (urlParams.get('register') === 'true') {
        container.classList.add('active');
    }

    // Flash any error messages then fade them out
    const errorMessage = document.querySelector('.error-message');
    if (errorMessage) {
        setTimeout(() => {
            errorMessage.style.opacity = '0';
            setTimeout(() => {
                errorMessage.style.display = 'none';
            }, 500);
        }, 3000);
    }
});