package tablebooknow.model.payment;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

public class PaymentCard implements Serializable {
    private String id;
    private String userId;
    private String cardholderName;
    private String cardNumber;
    private String expiryDate;
    private String cvv;
    private String cardType;
    private boolean defaultCard;


    public PaymentCard() {
        this.id = UUID.randomUUID().toString();
        this.defaultCard = false;
    }

    public PaymentCard(String id, String userId, String cardholderName, String cardNumber,
                       String expiryDate, String cvv, String cardType, boolean defaultCard,
                       LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.cardholderName = cardholderName;
        this.cardNumber = cardNumber;
        this.expiryDate = expiryDate;
        this.cvv = cvv;
        this.cardType = cardType;
        this.defaultCard = defaultCard;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCardholderName() {
        return cardholderName;
    }

    public void setCardholderName(String cardholderName) {
        this.cardholderName = cardholderName;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public boolean isDefaultCard() {
        return defaultCard;
    }

    public void setDefaultCard(boolean defaultCard) {
        this.defaultCard = defaultCard;
    }


    public String getLast4Digits() {
        if (cardNumber == null || cardNumber.length() < 4) {
            return "****";
        }
        return cardNumber.substring(cardNumber.length() - 4);
    }

    public String getMaskedCardNumber() {
        if (cardNumber == null || cardNumber.isEmpty()) {
            return "****************";
        }

        String last4 = getLast4Digits();
        StringBuilder masked = new StringBuilder();
        for (int i = 0; i < cardNumber.length() - 4; i++) {
            masked.append("*");
        }
        masked.append(last4);

        StringBuilder formatted = new StringBuilder();
        for (int i = 0; i < masked.length(); i++) {
            if (i > 0 && i % 4 == 0) {
                formatted.append(" ");
            }
            formatted.append(masked.charAt(i));
        }

        return formatted.toString();
    }

    public String toCsvString() {
        return String.format("%s,%s,%s,%s,%s,%s,%s,%b,%s,%s",
                id,
                userId != null ? userId : "",
                cardholderName != null ? cardholderName.replace(",", ";;") : "",
                cardNumber != null ? cardNumber : "",
                expiryDate != null ? expiryDate : "",
                cvv != null ? cvv : "",
                cardType != null ? cardType : "",
                defaultCard);
    }

    public static PaymentCard fromCsvString(String csvLine) {
        String[] parts = csvLine.split(",");
        if (parts.length < 10) {
            throw new IllegalArgumentException("Invalid CSV format for PaymentCard");
        }

        LocalDateTime createdDateTime = null;
        LocalDateTime updatedDateTime = null;
        try {
            if (!parts[8].isEmpty()) {
                createdDateTime = LocalDateTime.parse(parts[8]);
            }
            if (!parts[9].isEmpty()) {
                updatedDateTime = LocalDateTime.parse(parts[9]);
            }
        } catch (Exception e) {
            LocalDateTime now = LocalDateTime.now();
            createdDateTime = now;
            updatedDateTime = now;
        }

        String cardholderName = parts[2].replace(";;", ",");

        boolean isDefault = Boolean.parseBoolean(parts[7]);

        return new PaymentCard(
                parts[0],
                parts[1],
                cardholderName,
                parts[3],
                parts[4],
                parts[5],
                parts[6],
                isDefault,
                createdDateTime,
                updatedDateTime
        );
    }

    @Override
    public String toString() {
        return "PaymentCard{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", cardholderName='" + cardholderName + '\'' +
                ", cardNumber='" + getMaskedCardNumber() + '\'' +
                ", expiryDate='" + expiryDate + '\'' +
                ", cardType='" + cardType + '\'' +
                ", defaultCard=" + defaultCard +
                '}';
    }
}
