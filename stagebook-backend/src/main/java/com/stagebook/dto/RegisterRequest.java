package com.stagebook.dto;

import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
public class RegisterRequest {

    @NotBlank(message = "Le type de compte est obligatoire")
    private String typeCompte; // "ETUDIANT" ou "ENTREPRISE"

    // Champs communs
    @Email(message = "Email invalide")
    @NotBlank(message = "L'email est obligatoire")
    private String email;

    @Size(min = 6, message = "Le mot de passe doit avoir au moins 6 caractères")
    @NotBlank(message = "Le mot de passe est obligatoire")
    private String motDePasse;

    // Étudiant
    private String prenom;
    private String nom;
    private String universite;
    private String filiere;
    private String niveau;

    // Entreprise
    private String nomEntreprise;
    private String secteur;
    private String ville;
    private String siteWeb;
}
