package com.tablebooknow.util;

import com.tablebooknow.model.payment.Payment;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class PaymentQueue {
    private List<Payment> queue;

    public void enqueue(Payment payment) {
        queue.add(payment);
    }

    public PaymentQueue() {
        this.queue = new ArrayList<>();
    }


    public Payment dequeue() {
        if (isEmpty()) {
            return null;
        }
        return queue.remove(0);
    }


    public Payment peek() {
        if (isEmpty()) {
            return null;
        }
        return queue.get(0);
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public int size() {
        return queue.size();
    }

    public List<Payment> getAllPayments() {
        return new ArrayList<>(queue);
    }

    public void clear() {
        queue.clear();
    }

    public List<Payment> getPendingPayments() {
        List<Payment> pendingPayments = new ArrayList<>();

        for (Payment payment : queue) {
            if ("PENDING".equals(payment.getStatus())) {
                pendingPayments.add(payment);
            }
        }

        return pendingPayments;
    }

    public List<Payment> getCompletedPayments() {
        List<Payment> completedPayments = new ArrayList<>();

        for (Payment payment : queue) {
            if ("COMPLETED".equals(payment.getStatus())) {
                completedPayments.add(payment);
            }
        }

        return completedPayments;
    }

    public List<Payment> findByReservationId(String reservationId) {
        List<Payment> result = new ArrayList<>();

        for (Payment payment : queue) {
            if (payment.getReservationId() != null &&
                    payment.getReservationId().equals(reservationId)) {
                result.add(payment);
            }
        }

        return result;
    }

    public List<Payment> findByUserId(String userId) {
        List<Payment> result = new ArrayList<>();

        for (Payment payment : queue) {
            if (payment.getUserId() != null &&
                    payment.getUserId().equals(userId)) {
                result.add(payment);
            }
        }

        return result;
    }

    public void sortByDate() {
        Collections.sort(queue, Comparator.comparing(Payment::getCreatedAt));
    }

    public int processPayments() {
        List<Payment> pendingPayments = getPendingPayments();

        if (pendingPayments.isEmpty()) {
            return 0;
        }

        List<Payment> sortedPayments = mergeSort(pendingPayments);

        int processedCount = 0;

        for (Payment payment : sortedPayments) {
            try {

                System.out.println("Processing payment: " + payment.getId());

                payment.setStatus("PROCESSING");

                boolean success = true;

                if (success) {
                    payment.setStatus("COMPLETED");
                    payment.setCompletedAt(LocalDateTime.now());
                    processedCount++;
                } else {
                    payment.setStatus("FAILED");
                }

                updatePaymentInQueue(payment);

            } catch (Exception e) {
                System.err.println("Error processing payment " + payment.getId() + ": " + e.getMessage());
                payment.setStatus("FAILED");
                updatePaymentInQueue(payment);
            }
        }

        return processedCount;
    }


    private void updatePaymentInQueue(Payment payment) {
        for (int i = 0; i < queue.size(); i++) {
            if (queue.get(i).getId().equals(payment.getId())) {
                queue.set(i, payment);
                break;
            }
        }
    }

    private List<Payment> mergeSort(List<Payment> payments) {
        if (payments.size() <= 1) {
            return payments;
        }

        int mid = payments.size() / 2;
        List<Payment> left = new ArrayList<>(payments.subList(0, mid));
        List<Payment> right = new ArrayList<>(payments.subList(mid, payments.size()));

        left = mergeSort(left);
        right = mergeSort(right);

        return merge(left, right);
    }

    private List<Payment> merge(List<Payment> left, List<Payment> right) {
        List<Payment> result = new ArrayList<>();
        int leftIndex = 0;
        int rightIndex = 0;

        while (leftIndex < left.size() && rightIndex < right.size()) {
            LocalDateTime leftDate = left.get(leftIndex).getCreatedAt();
            LocalDateTime rightDate = right.get(rightIndex).getCreatedAt();

            if (leftDate == null || (rightDate != null && leftDate.isAfter(rightDate))) {
                result.add(right.get(rightIndex));
                rightIndex++;
            } else {
                result.add(left.get(leftIndex));
                leftIndex++;
            }
        }

        while (leftIndex < left.size()) {
            result.add(left.get(leftIndex));
            leftIndex++;
        }

        while (rightIndex < right.size()) {
            result.add(right.get(rightIndex));
            rightIndex++;
        }

        return result;
    }
}