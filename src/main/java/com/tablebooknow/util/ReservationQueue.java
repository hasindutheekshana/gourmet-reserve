package com.tablebooknow.util;

import com.tablebooknow.model.reservation.Reservation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;


public class ReservationQueue {
    private List<Reservation> reservations;
    private Queue<String> pendingQueue;

    public ReservationQueue() {
        this.reservations = new ArrayList<>();
        this.pendingQueue = new LinkedList<>();
    }

    public void clear() {
        this.reservations.clear();
        this.pendingQueue.clear();
    }

    public ReservationQueue(List<Reservation> reservations) {
        this.reservations = new ArrayList<>(reservations);
        this.pendingQueue = new LinkedList<>();

        // Initialize the queue with pending reservation IDs
        for (Reservation reservation : reservations) {
            if ("pending".equals(reservation.getStatus())) {
                pendingQueue.add(reservation.getId());
            }
        }
    }

    public void enqueue(Reservation reservation) {
        reservations.add(reservation);
        if ("pending".equals(reservation.getStatus())) {
            pendingQueue.add(reservation.getId());
        }
    }

    public Reservation dequeue() {
        if (pendingQueue.isEmpty()) {
            return null;
        }

        String nextId = pendingQueue.poll();
        return findReservationById(nextId);
    }

    public boolean isEmpty() {

        return pendingQueue.isEmpty();
    }

    public int size() {

        return pendingQueue.size();
    }

    public List<Reservation> getAllReservations() {

        return new ArrayList<>(reservations);
    }

    public List<Reservation> findPendingReservations() {
        return reservations.stream()
                .filter(r -> "pending".equals(r.getStatus()))
                .collect(Collectors.toList());
    }

    public Reservation peekNextPending() {
        if (pendingQueue.isEmpty()) {
            return null;
        }

        String nextId = pendingQueue.peek();
        return findReservationById(nextId);
    }

    public Reservation processNextReservation() {
        if (pendingQueue.isEmpty()) {
            return null;
        }

        String nextId = pendingQueue.poll();
        Reservation nextReservation = findReservationById(nextId);

        if (nextReservation != null) {
            nextReservation.setStatus("confirmed");
        }

        return nextReservation;
    }

    public boolean removeReservation(String reservationId) {
        boolean removed = false;

        // Remove from main list
        removed = reservations.removeIf(r -> r.getId().equals(reservationId));

        // Remove from pending queue if present
        pendingQueue.remove(reservationId);

        return removed;
    }

    public Reservation findReservationById(String id) {
        return reservations.stream()
                .filter(r -> r.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public List<Reservation> filterByStatus(String status) {
        return reservations.stream()
                .filter(r -> status.equals(r.getStatus()))
                .collect(Collectors.toList());
    }

    public boolean prioritize(String reservationId) {
        // Check if the reservation exists and is pending
        Reservation reservation = findReservationById(reservationId);
        if (reservation == null || !"pending".equals(reservation.getStatus())) {
            return false;
        }

        // Remove from current position in queue if present
        if (pendingQueue.remove(reservationId)) {
            // Add to front of queue
            Queue<String> newQueue = new LinkedList<>();
            newQueue.add(reservationId);
            newQueue.addAll(pendingQueue);
            pendingQueue = newQueue;
            return true;
        }

        return false;
    }

    public ReservationQueue sortByTime() {
        List<Reservation> sortedList = mergeSort(reservations, Comparator
                .comparing(Reservation::getReservationDate)
                .thenComparing(Reservation::getReservationTime));

        return new ReservationQueue(sortedList);
    }

    private <T> List<T> mergeSort(List<T> list, Comparator<T> comparator) {
        if (list.size() <= 1) {
            return list;
        }

        int mid = list.size() / 2;
        List<T> left = mergeSort(list.subList(0, mid), comparator);
        List<T> right = mergeSort(list.subList(mid, list.size()), comparator);

        return merge(left, right, comparator);
    }

    private <T> List<T> merge(List<T> left, List<T> right, Comparator<T> comparator) {
        List<T> result = new ArrayList<>();
        int leftIndex = 0;
        int rightIndex = 0;

        while (leftIndex < left.size() && rightIndex < right.size()) {
            if (comparator.compare(left.get(leftIndex), right.get(rightIndex)) <= 0) {
                result.add(left.get(leftIndex));
                leftIndex++;
            } else {
                result.add(right.get(rightIndex));
                rightIndex++;
            }
        }

        // Add remaining elements
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