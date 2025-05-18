package com.tablebooknow.service;

import com.tablebooknow.model.payment.Payment;
import com.tablebooknow.model.payment.PaymentCard;
import com.tablebooknow.model.reservation.Reservation;
import com.tablebooknow.model.user.User;
import com.tablebooknow.dao.PaymentCardDAO;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;


public class PaymentGateway {

    private static final String SANDBOX_URL = "https://sandbox.payhere.lk/pay/checkout";
    private static final String PRODUCTION_URL = "https://www.payhere.lk/pay/checkout";

    private static final String MERCHANT_ID = "1221688";
    private static final String MERCHANT_SECRET = "NDEwMjkxMjMxNTMxODkxNzQzNjMyNTI5MjgxMDkzMzgwMjY4MjY0MQ==";
    private static final boolean USE_SANDBOX = true;
    private static final String CURRENCY = "USD";

    private static final Map<String, BigDecimal> TABLE_PRICES = new HashMap<>();
    static {
        TABLE_PRICES.put("family", new BigDecimal("12.00"));
        TABLE_PRICES.put("luxury", new BigDecimal("18.00"));
        TABLE_PRICES.put("regular", new BigDecimal("8.00"));
        TABLE_PRICES.put("couple", new BigDecimal("6.00"));
    }

    public String getCheckoutUrl() {
        return USE_SANDBOX ? SANDBOX_URL : PRODUCTION_URL;
    }

    public BigDecimal calculateAmount(String tableType, int duration) {
        BigDecimal basePrice = TABLE_PRICES.getOrDefault(tableType, new BigDecimal("8.00"));
        return basePrice.multiply(new BigDecimal(duration));
    }

    public Map<String, String> generateFormParameters(
            Payment payment,
            Reservation reservation,
            User user,
            String returnUrl,
            String cancelUrl,
            String notifyUrl,
            String paymentCardId
    ) {
        Map<String, String> params = new HashMap<>();

        params.put("merchant_id", MERCHANT_ID);
        params.put("return_url", returnUrl);
        params.put("cancel_url", cancelUrl);
        params.put("notify_url", notifyUrl);

        String formattedAmount = String.format("%.2f", payment.getAmount());

        params.put("order_id", payment.getId());
        params.put("items", "Table Reservation - " + extractTableTypeFromId(reservation.getTableId()));
        params.put("currency", CURRENCY); // Use USD as currency
        params.put("amount", formattedAmount);

        String firstName = user.getUsername();
        String lastName = "Customer";
        String email = user.getEmail() != null ? user.getEmail() : "customer@example.com";
        String phone = user.getPhone() != null ? user.getPhone() : "0771234567";

        params.put("first_name", firstName);
        params.put("last_name", lastName);
        params.put("email", email);
        params.put("phone", phone);
        params.put("address", "Hotel Address");
        params.put("city", "Colombo");
        params.put("country", "Sri Lanka");

        params.put("custom_1", reservation.getId());
        params.put("custom_2", user.getId());

        if (paymentCardId != null && !paymentCardId.isEmpty()) {
            try {
                PaymentCardDAO paymentCardDAO = new PaymentCardDAO();
                PaymentCard card = paymentCardDAO.findById(paymentCardId);

                if (card != null) {
                    params.put("custom_3", card.getId());

                    params.put("card_holder_name", card.getCardholderName());

                    String lastFour = card.getCardNumber().substring(Math.max(0, card.getCardNumber().length() - 4));
                    params.put("card_no", "************" + lastFour);

                    if (card.getExpiryDate() != null && card.getExpiryDate().contains("/")) {
                        String[] expiryParts = card.getExpiryDate().split("/");
                        if (expiryParts.length == 2) {
                            params.put("card_expiry_month", expiryParts[0]);
                            params.put("card_expiry_year", "20" + expiryParts[1]); // Assuming YY format
                        }
                    }

                    System.out.println("Payment card details added to payment parameters");
                }
            } catch (Exception e) {
                System.err.println("Error retrieving payment card details: " + e.getMessage());
                e.printStackTrace();
            }
        }

        String hash = generateHash(
                MERCHANT_ID,
                params.get("order_id"),
                formattedAmount,
                CURRENCY,
                MERCHANT_SECRET
        );
        params.put("hash", hash);

        System.out.println("PayHere parameters:");
        for (Map.Entry<String, String> entry : params.entrySet()) {
            System.out.println("  " + entry.getKey() + ": " + entry.getValue());
        }

        return params;
    }


    private String extractTableTypeFromId(String tableId) {
        if (tableId == null || tableId.isEmpty()) {
            return "regular";
        }

        char firstChar = tableId.charAt(0);
        switch (firstChar) {
            case 'f':
                return "family";
            case 'l':
                return "luxury";
            case 'r':
                return "regular";
            case 'c':
                return "couple";
            default:
                return "regular";
        }
    }

    private String generateHash(String merchantId, String orderId, String amount, String currency, String merchantSecret) {
        String md5MerchantSecret = md5(merchantSecret).toUpperCase();

        String stringToHash = merchantId + orderId + amount + currency + md5MerchantSecret;

        System.out.println("String to hash: " + stringToHash);

        String hash = md5(stringToHash).toUpperCase();

        System.out.println("Generated hash: " + hash);

        return hash;
    }

    public boolean validateNotification(
            String merchantId,
            String orderId,
            String paymentId,
            String amount,
            String currency,
            String status
    ) {
        if (!merchantId.equals(MERCHANT_ID)) {
            System.out.println("Merchant ID mismatch: " + merchantId + " vs " + MERCHANT_ID);
            return false;
        }

        String md5MerchantSecret = md5(MERCHANT_SECRET).toUpperCase();

        String stringToHash = merchantId + orderId + amount + currency + status + md5MerchantSecret;

        String generatedHash = md5(stringToHash).toUpperCase();
        System.out.println("Generated verification hash: " + generatedHash);


        return true;
    }


    private String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());

            StringBuilder hexString = new StringBuilder();
            for (byte b : messageDigest) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Could not generate MD5 hash", e);
        }
    }
}