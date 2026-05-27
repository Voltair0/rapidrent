package com.rapidrent.rapidrent.dto;

import java.time.LocalDate;

import com.rapidrent.rapidrent.model.Reservation;

import lombok.Data;

@Data
public class ReservationSummaryResponse {
    private Long id;
    private Long carId;
    private String carName;
    private String location;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double totalPrice;
    private String status;

    public static ReservationSummaryResponse fromReservation(Reservation reservation) {
        ReservationSummaryResponse response = new ReservationSummaryResponse();
        response.setId(reservation.getId());
        response.setCarId(reservation.getCar().getId());
        response.setCarName(reservation.getCar().getBrand() + " " + reservation.getCar().getModel());
        response.setLocation(reservation.getCar().getLocation());
        response.setStartDate(reservation.getStartDate());
        response.setEndDate(reservation.getEndDate());
        response.setTotalPrice(reservation.getTotalPrice());
        response.setStatus(reservation.getStatus());
        return response;
    }
}
