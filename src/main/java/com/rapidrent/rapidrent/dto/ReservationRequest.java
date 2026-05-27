package com.rapidrent.rapidrent.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class ReservationRequest {
    private Long clientId;
    private Long carId;
    private LocalDate startDate;
    private LocalDate endDate;
}