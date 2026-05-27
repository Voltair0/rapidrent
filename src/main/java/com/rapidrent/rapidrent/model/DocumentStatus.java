package com.rapidrent.rapidrent.model;

public enum DocumentStatus {
    NONE,       // Încă nu a încărcat nimic
    PENDING,    // În așteptare validare
    VALIDATED,  // Acte aprobate de admin
    REJECTED    // Acte respinse
}