package com.tablebooknow.controller.payment;

import com.tablebooknow.dao.PaymentCardDAO;
import com.tablebooknow.model.payment.PaymentCard;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;


@WebServlet("/paymentcard/*")
public class PaymentCardServlet extends HttpServlet {
    private PaymentCardDAO paymentCardDAO;

    @Override
    public void init() throws ServletException {
        System.out.println("Initializing PaymentCardServlet");
        paymentCardDAO = new PaymentCardDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        System.out.println("GET request to paymentcard: " + pathInfo);

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            System.out.println("User not logged in, redirecting to login page");
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String userId = (String) session.getAttribute("userId");

        if (pathInfo == null || pathInfo.equals("/")) {
            try {
                List<PaymentCard> cards = paymentCardDAO.findByUserId(userId);
                request.setAttribute("paymentCards", cards);
                request.getRequestDispatcher("/paymentDashboard.jsp").forward(request, response);
            } catch (Exception e) {
                System.err.println("Error retrieving payment cards: " + e.getMessage());
                e.printStackTrace();
                request.setAttribute("errorMessage", "Error retrieving payment cards: " + e.getMessage());
                request.getRequestDispatcher("/paymentDashboard.jsp").forward(request, response);
            }
            return;
        }

        if (pathInfo.equals("/dashboard")) {
            String reservationId = request.getParameter("reservationId");
            if (reservationId != null) {
                session.setAttribute("reservationId", reservationId);
            } else {
                reservationId = (String) session.getAttribute("reservationId");
            }

            if (reservationId == null) {
                response.sendRedirect(request.getContextPath() + "/reservation/dateSelection");
                return;
            }

            try {
                List<PaymentCard> cards = paymentCardDAO.findByUserId(userId);
                request.setAttribute("paymentCards", cards);
                request.getRequestDispatcher("/paymentDashboard.jsp").forward(request, response);
            } catch (Exception e) {
                System.err.println("Error retrieving payment cards for dashboard: " + e.getMessage());
                e.printStackTrace();
                request.setAttribute("errorMessage", "Error retrieving payment cards: " + e.getMessage());
                request.getRequestDispatcher("/paymentDashboard.jsp").forward(request, response);
            }
            return;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        System.out.println("POST request to paymentcard: " + pathInfo);

        System.out.println("All parameters received:");
        request.getParameterMap().forEach((key, values) -> {
            System.out.println(key + ": " + String.join(", ", values));
        });

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            System.out.println("User not logged in, redirecting to login page");
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String userId = (String) session.getAttribute("userId");

        if (pathInfo != null) {
            if (pathInfo.equals("/setdefault")) {
                System.out.println("Processing set default request with cardId: " + request.getParameter("cardId"));
                setDefaultCard(request, response, userId);
                return;
            }
        }

        String action = request.getParameter("action");
        if (action != null) {
            if ("setdefault".equals(action)) {
                setDefaultCard(request, response, userId);
                return;
            }
        }

    }

    private void setDefaultCard(HttpServletRequest request, HttpServletResponse response, String userId) throws ServletException, IOException {
        try {
            String cardId = request.getParameter("cardId");
            System.out.println("Setting default card: " + cardId);

            if (cardId == null || cardId.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Card ID is required");
                return;
            }

            PaymentCard card = paymentCardDAO.findById(cardId);

            if (card == null || !card.getUserId().equals(userId)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Card not found or access denied");
                return;
            }

            List<PaymentCard> existingCards = paymentCardDAO.findByUserId(userId);
            for (PaymentCard existingCard : existingCards) {
                if (existingCard.isDefaultCard()) {
                    existingCard.setDefaultCard(false);
                    paymentCardDAO.update(existingCard);
                }
            }

            card.setDefaultCard(true);
            paymentCardDAO.update(card);

            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("Default payment method updated");

        } catch (Exception e) {
            System.err.println("Error setting default payment card: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error setting default payment card: " + e.getMessage());
        }
    }
}
