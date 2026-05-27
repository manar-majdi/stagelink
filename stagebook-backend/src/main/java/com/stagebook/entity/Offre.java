package com.stagebook.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "offres")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Offre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titre;

    @Column(nullable = false)
    private String entreprise;

    @Column(nullable = false)
    private String ville;

    @Column(nullable = false)
    private String typeStage; // PFE, Initiation, Ouvrier, Alternance

    private String duree;
    private String salaire;
    private String domaine;
    private String niveau;       // Licence, Master, Ingénieur
    private String dateDebut;
    private String secteur;
    private String logo;         // emoji ou URL image

    @Column(length = 2000)
    private String description;

    @Column(length = 3000)
    private String missions;     // JSON string

    @Column(length = 1000)
    private String competences;  // JSON string

    private boolean featured = false;
    private boolean urgent = false;
    private boolean verifie = false;
    private int nombreCandidatures = 0;

    @Column(updatable = false)
    private LocalDateTime datePublication = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entreprise_id")
    private Entreprise entrepriseObj;
}
