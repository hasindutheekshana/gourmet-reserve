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

    private void addNewCard(HttpServletRequest request, HttpServletResponse response, String userId) throws ServletException, IOException {
        try {
            String cardholderName = request.getParameter("cardholderName");
            String cardNumber = request.getParameter("cardNumber").replace(" ", "");
            String expiryDate = request.getParameter("expiryDate");
            String cvv = request.getParameter("cvv");
            String cardType = request.getParameter("cardType");
            boolean makeDefault = "true".equals(request.getParameter("makeDefault"));

            if (cardholderName == null || cardNumber == null || expiryDate == null || cvv == null || cardType == null ||
                    cardholderName.isEmpty() || cardNumber.isEmpty() || expiryDate.isEmpty() || cvv.isEmpty() || cardType.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("All fields are required");
                return;
            }

            PaymentCard card = new PaymentCard();
            card.setUserId(userId);
            card.setCardholderName(cardholderName);
            card.setCardNumber(cardNumber);
            card.setExpiryDate(expiryDate);
            card.setCvv(cvv);
            card.setCardType(cardType);

            if (makeDefault) {
                List<PaymentCard> existingCards = paymentCardDAO.findByUserId(userId);
                for (PaymentCard existingCard : existingCards) {
                    if (existingCard.isDefaultCard()) {
                        existingCard.setDefaultCard(false);
                        paymentCardDAO.update(existingCard);
                    }
                }
                card.setDefaultCard(true);
            } else {
                if (paymentCardDAO.findByUserId(userId).isEmpty()) {
                    card.setDefaultCard(true);
                } else {
                    card.setDefaultCard(false);
                }
            }

            paymentCardDAO.create(card);

            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("Card added successfully");

        } catch (Exception e) {
            System.err.println("Error adding payment card: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error adding payment card: " + e.getMessage());
        }
    }

    private void updateCard(HttpServletRequest request, HttpServletResponse response, String userId) throws ServletException, IOException {
        try {
            String cardId = request.getParameter("cardId");
            String cardholderName = request.getParameter("cardholderName");
            String expiryDate = request.getParameter("expiryDate");
            String cvv = request.getParameter("cvv");
            String cardType = request.getParameter("cardType");
            String cardNumber = request.getParameter("cardNumber");
            boolean makeDefault = "true".equals(request.getParameter("makeDefault"));

            System.out.println("Updating card ID: " + cardId);
            System.out.println("Make default: " + makeDefault);

            PaymentCard card = paymentCardDAO.findById(cardId);

            if (card == null || !card.getUserId().equals(userId)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Card not found or access denied");
                return;
            }

            if (cardholderName != null && !cardholderName.isEmpty()) {
                card.setCardholderName(cardholderName);
            }

            if (expiryDate != null && !expiryDate.isEmpty()) {
                card.setExpiryDate(expiryDate);
            }

            if (cvv != null && !cvv.isEmpty()) {
                card.setCvv(cvv);
            }

            if (cardType != null && !cardType.isEmpty()) {
                card.setCardType(cardType);
            }

            if (cardNumber != null && !cardNumber.isEmpty()) {
                card.setCardNumber(cardNumber.replace(" ", ""));
            }

            if (makeDefault && !card.isDefaultCard()) {
                List<PaymentCard> existingCards = paymentCardDAO.findByUserId(userId);
                for (PaymentCard existingCard : existingCards) {
                    if (existingCard.isDefaultCard() && !existingCard.getId().equals(card.getId())) {
                        existingCard.setDefaultCard(false);
                        paymentCardDAO.update(existingCard);
                    }
                }
                card.setDefaultCard(true);
            }

            paymentCardDAO.update(card);

            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("Card updated successfully");

        } catch (Exception e) {
            System.err.println("Error updating payment card: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error updating payment card: " + e.getMessage());
        }
    }
}
