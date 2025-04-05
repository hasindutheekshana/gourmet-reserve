package tablebooknow.model.payment;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class Payment implements Serializable {
    private String id;
    private String reservationId;
    private String userId;
    private BigDecimal amount;
    private String currency;
    private String status;
    private String paymentMethod;
    private String transactionId;
    private String paymentGateway;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;


    public Payment() {
        this.id = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
        this.status = "PENDING";
        this.currency = "LKR";
    }


    public Payment(String id, String reservationId, String userId, BigDecimal amount,
                   String currency, String status, String paymentMethod,
                   String transactionId, String paymentGateway,
                   LocalDateTime createdAt, LocalDateTime completedAt) {
        this.id = id;
        this.reservationId = reservationId;
        this.userId = userId;
        this.amount = amount;
        this.currency = currency;
        this.status = status;
        this.paymentMethod = paymentMethod;
        this.transactionId = transactionId;
        this.paymentGateway = paymentGateway;
        this.createdAt = createdAt;
        this.completedAt = completedAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReservationId() {
        return reservationId;
    }

    public void setReservationId(String reservationId) {
        this.reservationId = reservationId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getPaymentGateway() {
        return paymentGateway;
    }

    public void setPaymentGateway(String paymentGateway) {
        this.paymentGateway = paymentGateway;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }


    public String toCsvString() {
        return String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s",
                id,
                reservationId != null ? reservationId : "",
                userId != null ? userId : "",
                amount != null ? amount.toString() : "",
                currency != null ? currency : "",
                status != null ? status : "",
                paymentMethod != null ? paymentMethod : "",
                transactionId != null ? transactionId : "",
                paymentGateway != null ? paymentGateway : "",
                createdAt != null ? createdAt.toString() : "",
                completedAt != null ? completedAt.toString() : "");
    }


    public static Payment fromCsvString(String csvLine) {
        try {
            String[] parts = csvLine.split(",");
            if (parts.length < 10) {
                throw new IllegalArgumentException("Invalid CSV format for Payment: expected at least 10 fields, got " + parts.length);
            }

            Payment payment = new Payment();

            payment.setId(parts[0].trim());

            if (parts.length > 1 && !parts[1].trim().isEmpty()) {
                payment.setReservationId(parts[1].trim());
            }

            if (parts.length > 2 && !parts[2].trim().isEmpty()) {
                payment.setUserId(parts[2].trim());
            }

            if (parts.length > 3 && !parts[3].trim().isEmpty()) {
                try {
                    payment.setAmount(new BigDecimal(parts[3].trim()));
                } catch (NumberFormatException e) {
                    System.err.println("Error parsing amount: " + parts[3]);
                    payment.setAmount(BigDecimal.ZERO);
                }
            }

            if (parts.length > 4 && !parts[4].trim().isEmpty()) {
                payment.setCurrency(parts[4].trim());
            }

            if (parts.length > 5 && !parts[5].trim().isEmpty()) {
                payment.setStatus(parts[5].trim());
            }

            if (parts.length > 6 && !parts[6].trim().isEmpty()) {
                payment.setPaymentMethod(parts[6].trim());
            }

            if (parts.length > 7 && !parts[7].trim().isEmpty()) {
                payment.setTransactionId(parts[7].trim());
            }

            if (parts.length > 8 && !parts[8].trim().isEmpty()) {
                payment.setPaymentGateway(parts[8].trim());
            }

            if (parts.length > 9 && !parts[9].trim().isEmpty()) {
                try {
                    payment.setCreatedAt(LocalDateTime.parse(parts[9].trim()));
                } catch (Exception e) {
                    System.err.println("Error parsing createdAt date: " + parts[9]);
                    payment.setCreatedAt(LocalDateTime.now());
                }
            }

            if (parts.length > 10 && !parts[10].trim().isEmpty()) {
                try {
                    payment.setCompletedAt(LocalDateTime.parse(parts[10].trim()));
                } catch (Exception e) {
                    System.err.println("Error parsing completedAt date: " + parts[10]);
                }
            }

            return payment;
        } catch (Exception e) {
            throw new IllegalArgumentException("Error parsing payment CSV: " + e.getMessage(), e);
        }
    }

    @Override
    public String toString() {
        return "Payment{" +
                "id='" + id + '\'' +
                ", reservationId='" + reservationId + '\'' +
                ", userId='" + userId + '\'' +
                ", amount=" + amount +
                ", currency='" + currency + '\'' +
                ", status='" + status + '\'' +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", transactionId='" + transactionId + '\'' +
                ", paymentGateway='" + paymentGateway + '\'' +
                ", createdAt=" + createdAt +
                ", completedAt=" + completedAt +
                '}';
    }
}
