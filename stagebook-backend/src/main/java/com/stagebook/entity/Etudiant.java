package com.stagebook.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "etudiants")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Etudiant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String prenom;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String motDePasse;

    private String universite;
    private String filiere;
    private String niveau;        // Licence 1, 2, 3 / Master 1, 2
    private String telephone;
    private String ville;

    @Column(length = 500)
    private String bio;

    private String cvUrl;
    private String role = "ETUDIANT"; // ETUDIANT ou ADMIN

    @Column(updatable = false)
    private LocalDateTime dateInscription = LocalDateTime.now();

    @OneToMany(mappedBy = "etudiant", cascade = CascadeType.ALL)
    private List<Candidature> candidatures;
}
