package com.rapidrent.rapidrent.service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.rapidrent.rapidrent.dto.PendingDocumentResponse;
import com.rapidrent.rapidrent.model.DocumentStatus;
import com.rapidrent.rapidrent.model.User;
import com.rapidrent.rapidrent.repository.UserRepository;

@Service
@Transactional
public class DocumentService {

    @Autowired
    private UserRepository userRepository;

    public String uploadDocuments(Long userId, MultipartFile idCard, MultipartFile driverLicense) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilizatorul nu a fost găsit."));

        user.setIdCardImage(idCard.getBytes());
        user.setDriverLicenseImage(driverLicense.getBytes());
        user.setDocumentStatus(DocumentStatus.PENDING);

        userRepository.save(user);

        return "Documentele au fost încărcate cu succes și sunt în așteptarea validării!";
    }

    public List<PendingDocumentResponse> getPendingDocuments() {
        return userRepository.findByDocumentStatus(DocumentStatus.PENDING)
                .stream()
                .map(PendingDocumentResponse::fromUser)
                .collect(Collectors.toList());
    }

    public String moderateDocuments(Long clientId, String action) {
        User client = userRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Eroare: Clientul nu a fost găsit."));

        if ("APPROVE".equalsIgnoreCase(action)) {
            client.setDocumentStatus(DocumentStatus.VALIDATED);
            userRepository.save(client);
            return "Documentele clientului au fost APROBATE. Acesta poate acum efectua rezervări.";
        } else if ("REJECT".equalsIgnoreCase(action)) {
            client.setDocumentStatus(DocumentStatus.REJECTED);
            userRepository.save(client);
            return "Documentele au fost RESPINSE.";
        } else {
            throw new RuntimeException("Acțiune invalidă. Folosește 'APPROVE' sau 'REJECT'.");
        }
    }
}
