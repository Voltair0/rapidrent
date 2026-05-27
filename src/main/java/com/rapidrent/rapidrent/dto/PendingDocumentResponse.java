package com.rapidrent.rapidrent.dto;

import com.rapidrent.rapidrent.model.DocumentStatus;
import com.rapidrent.rapidrent.model.User;

import lombok.Data;

@Data
public class PendingDocumentResponse {
    private Long id;
    private String username;
    private String email;
    private DocumentStatus documentStatus;
    private boolean hasIdCardImage;
    private boolean hasDriverLicenseImage;

    public static PendingDocumentResponse fromUser(User user) {
        PendingDocumentResponse response = new PendingDocumentResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setDocumentStatus(user.getDocumentStatus());
        response.setHasIdCardImage(user.getIdCardImage() != null && user.getIdCardImage().length > 0);
        response.setHasDriverLicenseImage(user.getDriverLicenseImage() != null && user.getDriverLicenseImage().length > 0);
        return response;
    }
}
