package com.rapidrent.rapidrent.dto;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String email;
    private String password;
    @JsonProperty("isProvider")
    private boolean isProvider; // Bifa pentru a deveni furnizor
    private boolean gdprConsent; // Bifa obligatorie pentru regulamentul GDPR
}