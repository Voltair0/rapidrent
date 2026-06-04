package com.rapidrent.rapidrent.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.rapidrent.rapidrent.service.DocumentService;

@RestController
@RequestMapping("/api/client/documents")
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @PostMapping("/upload/{userId}")
    public ResponseEntity<String> uploadDocs(
            @PathVariable Long userId,
            @RequestParam("idCard") MultipartFile idCard,
            @RequestParam("driverLicense") MultipartFile driverLicense) {
        try {
            String message = documentService.uploadDocuments(userId, idCard, driverLicense);
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Eroare la încărcare: " + e.getMessage());
        }
    }
}