package com.stagebook.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;

@Entity
@Table(name = "entreprises")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Entreprise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nom;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String motDePasse;

    private String secteur;
    private String ville;
    private String logo;
    private String description;
    private String siteWeb;
    private boolean verifie = false;

    @JsonIgnore
    @OneToMany(mappedBy = "entrepriseObj", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Offre> offres;

    @Transient
    private int nombreOffres = 0;

    private String role = "ENTREPRISE"; // Role par défaut
}
