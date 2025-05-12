package com.tablebooknow.model.reservation;

import com.tablebooknow.model.reservation.Reservation;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ReservationQueue {
    private List<Reservation> queue;

    public ReservationQueue() {
        this.queue = new ArrayList<>();
    }

    public void enqueue(Reservation reservation) {
        queue.add(reservation);
    }

    public Reservation dequeue() {
        if (isEmpty()) {
            return null;
        }
        return queue.remove(0);
    }

    public Reservation peek() {
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

    public List<Reservation> getAllReservations() {
        return new ArrayList<>(queue);
    }

    public void clear() {
        queue.clear();
    }

    public void sortByTime() {
        if (queue.size() <= 1) {
            return;
        }

        List<Reservation> sorted = mergeSort(queue);
        queue.clear();
        queue.addAll(sorted);
    }

    private List<Reservation> mergeSort(List<Reservation> reservations) {
        if (reservations.size() <= 1) {
            return reservations;
        }

        int mid = reservations.size() / 2;
        List<Reservation> left = new ArrayList<>(reservations.subList(0, mid));
        List<Reservation> right = new ArrayList<>(reservations.subList(mid, reservations.size()));

        left = mergeSort(left);
        right = mergeSort(right);

        return merge(left, right);
    }

    private List<Reservation> merge(List<Reservation> left, List<Reservation> right) {
        List<Reservation> result = new ArrayList<>();
        int leftIndex = 0;
        int rightIndex = 0;

        while (leftIndex < left.size() && rightIndex < right.size()) {
            try {
                Reservation leftRes = left.get(leftIndex);
                Reservation rightRes = right.get(rightIndex);

                LocalTime leftTime = LocalTime.parse(leftRes.getReservationTime());
                LocalTime rightTime = LocalTime.parse(rightRes.getReservationTime());

                if (leftTime.isBefore(rightTime) || leftTime.equals(rightTime)) {
                    result.add(leftRes);
                    leftIndex++;
                } else {
                    result.add(rightRes);
                    rightIndex++;
                }
            } catch (Exception e) {
                if (leftIndex < left.size()) {
                    result.add(left.get(leftIndex));
                    leftIndex++;
                }
                if (rightIndex < right.size()) {
                    result.add(right.get(rightIndex));
                    rightIndex++;
                }
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

    public List<Reservation> findByTableAndDate(String tableId, String date) {
        List<Reservation> result = new ArrayList<>();

        for (Reservation reservation : queue) {
            if (reservation.getTableId() != null &&
                    reservation.getTableId().equals(tableId) &&
                    reservation.getReservationDate() != null &&
                    reservation.getReservationDate().equals(date) &&
                    !reservation.getStatus().equals("cancelled")) {

                result.add(reservation);
            }
        }

        return result;
    }

    public boolean isTableAvailable(String tableId, String date, String time, int duration) {
        List<Reservation> tableReservations = findByTableAndDate(tableId, date);

        if (tableReservations.isEmpty()) {
            return true;
        }

        try {
            LocalTime requestedTime = LocalTime.parse(time);
            LocalTime requestedEndTime = requestedTime.plusHours(duration);

            for (Reservation reservation : tableReservations) {
                LocalTime reservationTime = LocalTime.parse(reservation.getReservationTime());
                LocalTime reservationEndTime = reservationTime.plusHours(reservation.getDuration());

                if (requestedTime.isBefore(reservationEndTime) &&
                        reservationTime.isBefore(requestedEndTime)) {
                    return false;
                }
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }
}