<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gourmet Reserve - Login | Register</title>
    <link href="https://fonts.googleapis.com/css2?family=Playfair+Display:wght@500&family=Roboto:wght@300;400;500&display=swap" rel="stylesheet">
    <link href='https://unpkg.com/boxicons@2.1.4/css/boxicons.min.css' rel='stylesheet'>
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
            font-family: 'Roboto', sans-serif;
        }

        body {
            min-height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
            background: var(--dark);
            background-image:
                radial-gradient(circle at 10% 20%, rgba(196, 30, 58, 0.1) 0%, transparent 50%),
                radial-gradient(circle at 90% 80%, rgba(255, 215, 0, 0.1) 0%, transparent 50%),
                linear-gradient(rgba(0,0,0,0.8), rgba(0,0,0,0.8)),
                url('${pageContext.request.contextPath}/assets/img/restaurant-bg.jpg');
            background-size: cover;
            background-position: center;
        }

        .auth-container {
            background: rgba(30, 30, 30, 0.95);
            border-radius: 20px;
            border: 1px solid var(--gold);
            width: 90%;
            max-width: 1000px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.3);
            display: flex;
            overflow: hidden;
            height: 600px;
            position: relative;
        }

        .auth-image {
            width: 50%;
            background-image:
                linear-gradient(rgba(0,0,0,0.6), rgba(0,0,0,0.6)),
                url('https://images.unsplash.com/photo-1519708227418-c8fd9a32b7a2?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxzZWFyY2h8Mnx8cmVzdGF1cmFudHxlbnwwfHwwfHw%3D&w=1000&q=80');
            background-size: cover;
            background-position: center;
            display: flex;
            flex-direction: column;
            justify-content: center;
            align-items: center;
            color: var(--text);
            text-align: center;
            padding: 3rem;
            position: relative;
        }

        .auth-image::before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: linear-gradient(45deg, rgba(212, 175, 55, 0.2), rgba(128, 0, 32, 0.2));
        }

        .auth-image-content {
            position: relative;
            z-index: 2;
        }

        .auth-image h2 {
            font-family: 'Playfair Display', serif;
            color: var(--gold);
            font-size: 2.5rem;
            margin-bottom: 1.5rem;
        }

        .auth-image p {
            margin-bottom: 2rem;
            line-height: 1.6;
        }

        .auth-image .btn {
            background: transparent;
            border: 2px solid var(--gold);
            color: var(--gold);
            padding: 0.8rem 2rem;
            font-weight: 500;
            border-radius: 30px;
            cursor: pointer;
            transition: all 0.3s ease;
            font-size: 1rem;
        }

        .auth-image .btn:hover {
            background: var(--gold);
            color: var(--dark);
        }

        .auth-forms {
            width: 50%;
            padding: 3rem;
            overflow-y: auto;
        }

        .auth-forms h1 {
            font-family: 'Playfair Display', serif;
            color: var(--gold);
            font-size: 2.2rem;
            margin-bottom: 0.5rem;
            text-align: center;
        }

        .auth-forms p {
            color: var(--text);
            text-align: center;
            margin-bottom: 2rem;
            opacity: 0.8;
        }

        .form-container {
            transition: all 0.3s ease;
        }

        .login-form, .register-form {
            display: none;
        }

        .active-form {
            display: block;
            animation: fadeIn 0.5s ease forwards;
        }

        @keyframes fadeIn {
            from { opacity: 0; transform: translateY(20px); }
            to { opacity: 1; transform: translateY(0); }
        }

        .form-group {
            margin-bottom: 1.5rem;
            position: relative;
        }

        .form-group i {
            position: absolute;
            left: 15px;
            top: 50%;
            transform: translateY(-50%);
            color: var(--gold);
            font-size: 1.2rem;
        }

        .form-input {
            width: 100%;
            padding: 1rem 1rem 1rem 2.5rem;
            background: rgba(255, 255, 255, 0.1);
            border: 1px solid var(--gold);
            border-radius: 6px;
            color: var(--text);
            font-size: 1rem;
        }

        .form-input:focus {
            outline: none;
            box-shadow: 0 0 10px rgba(212, 175, 55, 0.3);
        }

        .form-input::placeholder {
            color: rgba(224, 224, 224, 0.6);
        }

        .form-footer {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 1.5rem;
        }

        .form-footer a {
            color: var(--text);
            text-decoration: none;
            font-size: 0.9rem;
            opacity: 0.8;
            transition: all 0.3s ease;
        }

        .form-footer a:hover {
            color: var(--gold);
            opacity: 1;
        }

        .form-footer label {
            color: var(--text);
            font-size: 0.9rem;
            opacity: 0.8;
            display: flex;
            align-items: center;
            cursor: pointer;
        }

        .form-footer input[type="checkbox"] {
            margin-right: 5px;
            accent-color: var(--gold);
        }

        .submit-btn {
            width: 100%;
            padding: 1rem;
            background: var(--gold);
            border: none;
            border-radius: 6px;
            color: var(--dark);
            font-weight: 600;
            cursor: pointer;
            transition: transform 0.3s ease;
            margin-bottom: 1.5rem;
            font-size: 1rem;
        }

        .submit-btn:hover {
            transform: translateY(-3px);
            box-shadow: 0 5px 15px rgba(212, 175, 55, 0.3);
        }

        .social-login {
            text-align: center;
        }

        .social-login p {
            margin-bottom: 1rem;
            color: var(--text);
            font-size: 0.9rem;
            position: relative;
        }

        .social-login p::before,
        .social-login p::after {
            content: '';
            position: absolute;
            top: 50%;
            width: 30%;
            height: 1px;
            background: rgba(224, 224, 224, 0.3);
        }

        .social-login p::before {
            left: 0;
        }

        .social-login p::after {
            right: 0;
        }

        .social-icons {
            display: flex;
            justify-content: center;
            gap: 15px;
        }

        .social-icon {
            width: 40px;
            height: 40px;
            border-radius: 50%;
            background: rgba(255, 255, 255, 0.1);
            display: flex;
            justify-content: center;
            align-items: center;
            font-size: 1.2rem;
            color: var(--text);
            transition: all 0.3s ease;
            border: 1px solid rgba(212, 175, 55, 0.3);
        }

        .social-icon:hover {
            background: var(--gold);
            color: var(--dark);
        }

        .toggle-form {
            text-align: center;
            margin-top: 2rem;
        }

        .toggle-form p {
            color: var(--text);
            font-size: 0.9rem;
            margin-bottom: 0.5rem;
        }

        .toggle-btn {
            background: transparent;
            border: none;
            color: var(--gold);
            font-weight: 500;
            cursor: pointer;
            font-size: 0.95rem;
        }

        .toggle-btn:hover {
            text-decoration: underline;
        }

        .error-message {
            background: rgba(255, 0, 0, 0.1);
            color: #ff4444;
            padding: 0.8rem;
            border-radius: 6px;
            margin-bottom: 1.5rem;
            text-align: center;
            font-size: 0.9rem;
            animation: fadeIn 0.3s ease;
        }

        @media (max-width: 768px) {
            .auth-container {
                flex-direction: column;
                height: auto;
                max-width: 500px;
            }

            .auth-image, .auth-forms {
                width: 100%;
            }

            .auth-image {
                padding: 2rem;
                min-height: 250px;
            }

            .auth-forms {
                padding: 2rem;
            }
        }
    </style>
</head>
<body>
    <div class="auth-container">
        <div class="auth-image">
            <div class="auth-image-content">
                <h2>Gourmet Reserve</h2>
                <p>Experience fine dining at its best. Reserve your table and indulge in culinary excellence.</p>
                <button id="switch-form-btn" class="btn">Create Account</button>
            </div>
        </div>

        <div class="auth-forms">
            <div class="form-container">
                <div class="login-form active-form">
                    <h1>Welcome Back</h1>
                    <p>Please sign in to continue your gourmet journey</p>

                    <%
                        String errorMessage = (String) request.getAttribute("errorMessage");
                        if (errorMessage != null) {
                    %>
                    <div class="error-message">
                        <%= errorMessage %>
                    </div>
                    <% } %>

                    <form action="${pageContext.request.contextPath}/user/login" method="post">
                        <div class="form-group">
                            <i class='bx bxs-user'></i>
                            <input type="text" name="username" class="form-input" placeholder="Username" required>
                        </div>

                        <div class="form-group">
                            <i class='bx bxs-lock-alt'></i>
                            <input type="password" name="password" class="form-input" placeholder="Password" required>
                        </div>

                        <div class="form-footer">
                            <label>
                                <input type="checkbox" name="remember"> Remember me
                            </label>
                            <a href="#">Forgot password?</a>
                        </div>

                        <button type="submit" class="submit-btn">Sign In</button>
                    </form>

                    <div class="social-login">
                        <p>Or sign in with</p>
                        <div class="social-icons">
                            <a href="#" class="social-icon"><i class='bx bxl-google'></i></a>
                            <a href="#" class="social-icon"><i class='bx bxl-facebook'></i></a>
                            <a href="#" class="social-icon"><i class='bx bxl-apple'></i></a>
                        </div>
                    </div>

                    <div class="toggle-form">
                        <p>Don't have an account?</p>
                        <button class="toggle-btn" id="go-to-register">Create Account</button>
                    </div>
                </div>

                <div class="register-form">
                    <h1>Create Account</h1>
                    <p>Join us to experience culinary excellence</p>

                    <form action="${pageContext.request.contextPath}/user/register" method="post">
                        <div class="form-group">
                            <i class='bx bxs-user'></i>
                            <input type="text" name="username" class="form-input" placeholder="Username" required>
                        </div>

                        <div class="form-group">
                            <i class='bx bxs-envelope'></i>
                            <input type="email" name="email" class="form-input" placeholder="Email" required>
                        </div>

                        <div class="form-group">
                            <i class='bx bxs-lock-alt'></i>
                            <input type="password" name="password" class="form-input" placeholder="Password" required>
                        </div>

                        <div class="form-group">
                            <i class='bx bxs-phone'></i>
                            <input type="text" name="phone" class="form-input" placeholder="Phone Number">
                        </div>

                        <button type="submit" class="submit-btn">Create Account</button>
                    </form>

                    <div class="social-login">
                        <p>Or sign up with</p>
                        <div class="social-icons">
                            <a href="#" class="social-icon"><i class='bx bxl-google'></i></a>
                            <a href="#" class="social-icon"><i class='bx bxl-facebook'></i></a>
                            <a href="#" class="social-icon"><i class='bx bxl-apple'></i></a>
                        </div>
                    </div>

                    <div class="toggle-form">
                        <p>Already have an account?</p>
                        <button class="toggle-btn" id="go-to-login">Sign In</button>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script>
        document.addEventListener('DOMContentLoaded', () => {
            const loginForm = document.querySelector('.login-form');
            const registerForm = document.querySelector('.register-form');
            const switchFormBtn = document.getElementById('switch-form-btn');
            const goToRegister = document.getElementById('go-to-register');
            const goToLogin = document.getElementById('go-to-login');

            // Check if register parameter is in URL
            const urlParams = new URLSearchParams(window.location.search);
            if (urlParams.get('register') === 'true') {
                switchToRegister();
            }

            function switchToRegister() {
                loginForm.classList.remove('active-form');
                registerForm.classList.add('active-form');
                switchFormBtn.textContent = 'Sign In';
            }

            function switchToLogin() {
                registerForm.classList.remove('active-form');
                loginForm.classList.add('active-form');
                switchFormBtn.textContent = 'Create Account';
            }

            switchFormBtn.addEventListener('click', () => {
                if (loginForm.classList.contains('active-form')) {
                    switchToRegister();
                } else {
                    switchToLogin();
                }
            });

            goToRegister.addEventListener('click', switchToRegister);
            goToLogin.addEventListener('click', switchToLogin);
        });
    </script>
</body>
</html>