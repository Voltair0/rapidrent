package com.rapidrent.rapidrent.dto;

import com.rapidrent.rapidrent.model.DocumentStatus;
import com.rapidrent.rapidrent.model.Role;
import com.rapidrent.rapidrent.model.User;

import lombok.Data;

@Data
public class CurrentUserResponse {
    private Long id;
    private String username;
    private String email;
    private Role role;
    private DocumentStatus documentStatus;

    public static CurrentUserResponse fromUser(User user) {
        CurrentUserResponse response = new CurrentUserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        response.setDocumentStatus(user.getDocumentStatus());
        return response;
    }
}
