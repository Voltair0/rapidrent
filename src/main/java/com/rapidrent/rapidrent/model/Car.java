package com.rapidrent.rapidrent.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "cars")
@Data
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id", nullable = false)
    private User provider;

    private String brand;
    private String model;
    private String licensePlate;
    private Double price;

    // Câmpuri folosite de interfața web pentru căutare și afișare.
    // Sunt nullable ca să rămână compatibile cu înregistrările deja existente în baza de date.
    private String location;
    private String category;
    private String transmission;
    private Integer seats;
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    private CarStatus status = CarStatus.PENDING;
}
