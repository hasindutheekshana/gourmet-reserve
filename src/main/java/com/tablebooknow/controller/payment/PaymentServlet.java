package com.tablebooknow.controller.payment;


import com.tablebooknow.dao.PaymentDAO;
import com.tablebooknow.model.payment.Payment;

import java.util.Enumeration;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;


@WebServlet("/payment/*")
public class PaymentServlet extends HttpServlet {
    private PaymentDAO paymentDAO;


    @Override
    public void init() throws ServletException {
        System.out.println("Initializing PaymentServlet");
        paymentDAO = new PaymentDAO();

    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        System.out.println("GET request to payment: " + pathInfo);

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            System.out.println("User not logged in, redirecting to login page");
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        if (pathInfo == null || pathInfo.equals("/")) {
            response.sendRedirect(request.getContextPath() + "/paymentcard/dashboard");
            return;
        }

        switch (pathInfo) {
            case "/initiate":
                initiatePayment(request, response);
                break;
            default:
                System.out.println("Unknown path: " + pathInfo);
                response.sendRedirect(request.getContextPath() + "/paymentcard/dashboard");
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        System.out.println("POST request to payment: " + pathInfo);

        System.out.println("Request parameters:");
        Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            String[] paramValues = request.getParameterValues(paramName);
            for (String value : paramValues) {
                System.out.println("  " + paramName + " = " + value);
            }
        }

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            System.out.println("User not logged in, redirecting to login page");
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        switch (pathInfo) {
            default:
                System.out.println("Unknown path: " + pathInfo);
                response.sendRedirect(request.getContextPath() + "/paymentcard/dashboard");
                break;
        }
    }

    private void initiatePayment(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("Initiating payment");

        HttpSession session = request.getSession();
        String userId = (String) session.getAttribute("userId");

        System.out.println("All session attributes:");
        Enumeration<String> attributeNames = session.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            String name = attributeNames.nextElement();
            System.out.println("  " + name + ": " + session.getAttribute(name));
        }

        String reservationId = request.getParameter("reservationId");
        if (reservationId == null) {
            reservationId = (String) session.getAttribute("reservationId");
            System.out.println("Retrieved reservationId from session: " + reservationId);
        } else {
            System.out.println("Retrieved reservationId from request parameters: " + reservationId);
        }

        if (reservationId == null) {
            System.out.println("No reservation ID found, redirecting to date selection");
            response.sendRedirect(request.getContextPath() + "/reservation/dateSelection");
            return;
        }

        session.setAttribute("reservationId", reservationId);
        System.out.println("Stored reservationId in session: " + reservationId);

        try {
            Reservation reservation = reservationDAO.findById(reservationId);
            if (reservation != null) {
                System.out.println("Found reservation: " + reservation);
                request.setAttribute("reservation", reservation);

                if (reservation.getTableId() != null) {
                    String tableId = reservation.getTableId();
                    char typeChar = tableId.charAt(0);
                    String tableType = "Regular";
                    if (typeChar == 'f') tableType = "Family";
                    else if (typeChar == 'l') tableType = "Luxury";
                    else if (typeChar == 'c') tableType = "Couple";
                    request.setAttribute("tableType", tableType);
                }
            } else {
                System.out.println("Reservation not found for ID: " + reservationId);
            }
        } catch (Exception e) {
            System.err.println("Error loading reservation: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("Redirecting to payment dashboard");
        response.sendRedirect(request.getContextPath() + "/paymentcard/dashboard");
    }

    private void handlePaymentSuccess(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("Payment success callback received");

        HttpSession session = request.getSession();
        String paymentId = (String) session.getAttribute("paymentId");
        String reservationId = (String) session.getAttribute("reservationId");
        String userId = (String) session.getAttribute("userId");

        String status = request.getParameter("status_code");
        String paymentGatewayId = request.getParameter("payment_id");
        String orderId = request.getParameter("order_id");
        String simulatePayment = request.getParameter("simulatePayment");

        System.out.println("Payment status: " + status);
        System.out.println("PayHere payment ID: " + paymentGatewayId);
        System.out.println("Order ID: " + orderId);
        System.out.println("Simulation mode: " + simulatePayment);

        System.out.println("All payment success parameters:");
        Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            String[] values = request.getParameterValues(paramName);
            for (String value : values) {
                System.out.println("  " + paramName + ": " + value);
            }
        }

        boolean isSimulation = "true".equals(simulatePayment);

        if (orderId == null && paymentId != null) {
            orderId = paymentId;
        }

        boolean isValid = false;
        String confirmationMessage = "";
        Payment payment = null;
        Reservation reservation = null;
        User user = null;
        String qrCodeBase64 = null;

        try {
            if (isSimulation) {
                if (reservationId != null) {
                    reservation = reservationDAO.findById(reservationId);
                    if (reservation != null) {
                        userId = (String) session.getAttribute("userId");
                        user = userDAO.findById(userId);

                        if (user != null) {
                            String tableId = reservation.getTableId();
                            String tableType = "regular";
                            if (tableId != null && !tableId.isEmpty()) {
                                char typeChar = tableId.charAt(0);
                                if (typeChar == 'f') tableType = "family";
                                else if (typeChar == 'l') tableType = "luxury";
                                else if (typeChar == 'c') tableType = "couple";
                                else if (typeChar == 'r') tableType = "regular";
                            }

                            BigDecimal amount = paymentGateway.calculateAmount(tableType, reservation.getDuration());

                            payment = new Payment();
                            payment.setUserId(userId);
                            payment.setReservationId(reservationId);
                            payment.setAmount(amount);
                            payment.setCurrency("USD");
                            payment.setStatus("COMPLETED");
                            payment.setTransactionId("SIM-" + System.currentTimeMillis());
                            payment.setPaymentGateway("Development Simulation");
                            payment.setCompletedAt(LocalDateTime.now());

                            String cardType = (String) session.getAttribute("cardType");
                            if (cardType != null) {
                                payment.setPaymentMethod("Card - " + cardType.toUpperCase());
                            } else {
                                payment.setPaymentMethod("Simulated Payment");
                            }

                            paymentDAO.create(payment);
                            paymentId = payment.getId();
                            session.setAttribute("paymentId", paymentId);

                            reservation.setStatus("confirmed");
                            reservationDAO.update(reservation);

                            reservationQueue.enqueue(reservation);

                            isValid = true;
                            confirmationMessage = "Payment successful! Your table reservation is now confirmed.";
                        }
                    }
                }
            } else if (orderId != null) {
                payment = null;

                try {
                    payment = paymentDAO.findById(orderId);
                } catch (Exception e) {
                    System.err.println("Error finding payment by ID: " + e.getMessage());
                }

                if (payment == null && reservationId != null) {
                    try {
                        List<Payment> payments = paymentDAO.findByReservationId(reservationId);
                        if (!payments.isEmpty()) {
                            payment = payments.get(0);
                        }
                    } catch (Exception e) {
                        System.err.println("Error finding payment by reservation: " + e.getMessage());
                    }
                }

                if (payment != null) {
                    payment.setStatus("COMPLETED");
                    if (paymentGatewayId != null) {
                        payment.setTransactionId(paymentGatewayId);
                    }
                    payment.setCompletedAt(LocalDateTime.now());

                    paymentDAO.update(payment);

                    session.setAttribute("paymentId", payment.getId());

                    userId = payment.getUserId();
                    if (userId != null) {
                        user = userDAO.findById(userId);
                    }

                    reservation = reservationDAO.findById(payment.getReservationId());
                    if (reservation != null) {
                        reservation.setStatus("confirmed");
                        reservationDAO.update(reservation);

                        if (session.getAttribute("reservationId") == null) {
                            session.setAttribute("reservationId", reservation.getId());
                        }


                        reservationQueue.enqueue(reservation);

                        isValid = true;
                        confirmationMessage = "Payment successful! Your table reservation is now confirmed.";
                    }
                }
            }

            if (isValid && payment != null && reservation != null && user != null) {
                try {
                    System.out.println("Generating QR code for reservation: " + reservationId);

                    String qrContent = QRCodeGenerator.createQRCodeContent(
                            reservation.getId(),
                            payment.getId(),
                            user.getId());

                    qrCodeBase64 = QRCodeGenerator.createQRCodeBase64(qrContent, 250, 250);

                    session.setAttribute("qrCodeBase64", qrCodeBase64);

                    confirmationMessage += " Please keep your reservation QR code for check-in.";

                    try {
                        System.out.println("Attempting to send confirmation email to user: " + user.getEmail());
                        boolean emailSent = EmailService.sendConfirmationEmail(user, reservation, payment);
                        System.out.println("Email sending result: " + (emailSent ? "SUCCESS" : "FAILED"));

                        if (emailSent) {
                            confirmationMessage += " A confirmation email has been sent to your email address.";
                        } else {
                            System.err.println("Failed to send confirmation email to: " + user.getEmail());
                            confirmationMessage += " We were unable to send a confirmation email. Please contact support if needed.";
                        }
                    } catch (Exception emailEx) {
                        System.err.println("Exception sending confirmation email: " + emailEx.getMessage());
                        emailEx.printStackTrace();
                        confirmationMessage += " We encountered an issue sending your confirmation email.";
                    }
                } catch (Exception e) {
                    System.err.println("Error generating QR code: " + e.getMessage());
                    e.printStackTrace();
                    confirmationMessage += " Please keep your reservation ID for check-in.";
                }
            }
        } catch (Exception e) {
            System.err.println("Error processing payment success: " + e.getMessage());
            e.printStackTrace();
            confirmationMessage = "There was an issue processing your payment. Please contact support.";
        }

        if (isValid) {
            session.setAttribute("confirmationMessage", confirmationMessage);

            if (reservation != null) {
                session.setAttribute("tableId", reservation.getTableId());
                session.setAttribute("reservationDate", reservation.getReservationDate());
                session.setAttribute("reservationTime", reservation.getReservationTime());
            }

            if (qrCodeBase64 != null) {
                session.setAttribute("qrCodeBase64", qrCodeBase64);
            }

            request.setAttribute("paymentSuccessful", true);

            response.sendRedirect(request.getContextPath() + "/reservationConfirmation.jsp");
        } else {
            request.setAttribute("errorMessage", "We couldn't verify your payment. Please contact support.");
            request.setAttribute("paymentSuccessful", false);
            request.getRequestDispatcher("/paymentSuccess.jsp").forward(request, response);
        }
    }

}
