package com.rapidrent.rapidrent.service;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rapidrent.rapidrent.dto.RegisterRequest;
import com.rapidrent.rapidrent.model.DocumentStatus;
import com.rapidrent.rapidrent.model.Role;
import com.rapidrent.rapidrent.model.User;
import com.rapidrent.rapidrent.repository.UserRepository;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private com.rapidrent.rapidrent.security.JwtUtil jwtUtil;
    
    @Autowired
    private com.rapidrent.rapidrent.security.CustomUserDetailsService userDetailsService;

    public String registerUser(RegisterRequest request) {
        if (!request.isGdprConsent()) {
            throw new RuntimeException("Trebuie să accepți termenii GDPR pentru a crea un cont.");
        }

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Acest email este deja folosit.");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        
        user.setGdprConsented(request.isGdprConsent());
        user.setDocumentStatus(DocumentStatus.NONE);

        if (request.isProvider()) {
            user.setRole(Role.ROLE_FURNIZOR);
        } else {
            user.setRole(Role.ROLE_CLIENT);
        }

        userRepository.save(user);

        return "Utilizatorul a fost înregistrat cu succes! Te rugăm să îți confirmi email-ul.";
    }

    @Transactional
    public String loginUser(String email, String rawPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Eroare: Email-ul nu a fost găsit.")); 

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new RuntimeException("Eroare: Parola este incorectă.");
        }

        org.springframework.security.core.userdetails.UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        return jwtUtil.generateToken(userDetails);
    }
    public String resetPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Eroare: Nu există niciun cont cu acest email."));

        String newRandomPassword = UUID.randomUUID().toString().substring(0, 8);
        
        user.setPassword(passwordEncoder.encode(newRandomPassword));
        userRepository.save(user);

        return "Parola a fost resetată cu succes. Noua ta parolă este: " + newRandomPassword + " (Te rugăm să o schimbi la următoarea logare!)";
    }

    public String changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Eroare: Utilizatorul nu a fost găsit."));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("Eroare: Parola veche este incorectă.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return "Parola a fost schimbată cu succes!";
    }
}