package com.stagebook.dto;

import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Data
public class LoginRequest {
    @Email
    @NotBlank
    public String email;

    @NotBlank
    public String motDePasse;
}
