package com.tablebooknow.controller.payment;


import com.tablebooknow.dao.PaymentDAO;
import com.tablebooknow.dao.ReservationDAO;
import com.tablebooknow.dao.UserDAO;
import com.tablebooknow.model.reservation.Reservation;
import com.tablebooknow.service.PaymentGateway;
import com.tablebooknow.util.ReservationQueue;
import java.util.Enumeration;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;



@WebServlet("/payment/*")
public class PaymentServlet extends HttpServlet {
    private PaymentDAO paymentDAO;
    private ReservationDAO reservationDAO;
    private UserDAO userDAO;
    private PaymentGateway paymentGateway;
    private ReservationQueue reservationQueue;

    @Override
    public void init() throws ServletException {
        System.out.println("Initializing PaymentServlet");
        paymentDAO = new PaymentDAO();
        reservationDAO = new ReservationDAO();
        userDAO = new UserDAO();
        paymentGateway = new PaymentGateway();
        reservationQueue = new ReservationQueue();
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
            case "/success":
                handlePaymentSuccess(request, response);
                break;
            case "/cancel":
                handlePaymentCancel(request, response);
                break;
            case "/notify":
                handlePaymentNotification(request, response);
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

        if (pathInfo == null || pathInfo.equals("/")) {
            processPaymentForm(request, response);
            return;
        }

        switch (pathInfo) {
            case "/process":
                processPaymentForm(request, response);
                break;
            case "/notify":
                handlePaymentNotification(request, response);
                break;
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

}
