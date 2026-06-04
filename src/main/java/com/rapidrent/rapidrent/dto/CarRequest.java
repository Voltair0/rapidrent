package com.rapidrent.rapidrent.dto;

import lombok.Data;

@Data
public class CarRequest {
    private Long providerId;
    private String brand;
    private String model;
    private String licensePlate;
    private Double price;

    private String location;
    private String category;
    private String transmission;
    private Integer seats;
    private String imageUrl;
}
