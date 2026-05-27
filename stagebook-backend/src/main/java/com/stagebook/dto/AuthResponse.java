package com.stagebook.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String prenom;
    private String nom;
    private String email;
    private String role;
}
