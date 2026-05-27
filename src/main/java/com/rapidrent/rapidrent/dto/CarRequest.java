package com.rapidrent.rapidrent.dto;

import lombok.Data;

@Data
public class CarRequest {
    private Long providerId; // Momentan primim ID-ul direct pentru a putea testa ușor
    private String brand;
    private String model;
    private String licensePlate;
    private Double price;

    // Date opționale pentru afișarea în frontend și filtrare.
    private String location;
    private String category;
    private String transmission;
    private Integer seats;
    private String imageUrl;
}
