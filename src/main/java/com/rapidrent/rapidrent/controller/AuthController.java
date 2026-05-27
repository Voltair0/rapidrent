package com.rapidrent.rapidrent.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rapidrent.rapidrent.dto.CurrentUserResponse;
import com.rapidrent.rapidrent.dto.LoginRequest;
import com.rapidrent.rapidrent.dto.RegisterRequest;
import com.rapidrent.rapidrent.security.CustomUserDetails;
import com.rapidrent.rapidrent.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        try {
            String message = authService.registerUser(request);
            return ResponseEntity.ok(message);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        try {
            String token = authService.loginUser(request.getEmail(), request.getPassword());
            return ResponseEntity.ok(token);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/me")
    public ResponseEntity<CurrentUserResponse> currentUser(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return ResponseEntity.ok(CurrentUserResponse.fromUser(userDetails.getUser()));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String email) {
        try {
            return ResponseEntity.ok(authService.resetPassword(email));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/change-password/{userId}")
    public ResponseEntity<String> changePassword(
            @PathVariable Long userId,
            @RequestParam String oldPassword,
            @RequestParam String newPassword) {
        try {
            return ResponseEntity.ok(authService.changePassword(userId, oldPassword, newPassword));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
