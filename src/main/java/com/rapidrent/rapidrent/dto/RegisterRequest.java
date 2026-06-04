package com.rapidrent.rapidrent.dto;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String email;
    private String password;
    @JsonProperty("isProvider")
    private boolean isProvider;
    private boolean gdprConsent;
}