package com.rapidrent.rapidrent.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String username; // Adaugă această linie
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(name = "is_gdpr_consented", nullable = false)
    private boolean isGdprConsented;

    @Enumerated(EnumType.STRING)
    @Column(name = "document_status", nullable = false)
    private DocumentStatus documentStatus = DocumentStatus.NONE;

    @Lob // Spune bazei de date să rezerve spațiu pentru fișiere mari (Large Object)
    private byte[] idCardImage; // Poza de la buletin

    @Lob
    private byte[] driverLicenseImage; // Poza de la permis
}