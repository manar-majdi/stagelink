package com.stagebook.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "candidatures")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Candidature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "etudiant_id", nullable = false)
    private Etudiant etudiant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "offre_id", nullable = false)
    private Offre offre;

    // Statut : EN_ATTENTE, ACCEPTEE, REFUSEE, EN_COURS
    private String statut = "EN_ATTENTE";

    @Column(length = 3000)
    private String lettreMOtivation;

    // CV stocké en base (nom + données binaires)
    private String cvNomFichier;
    private String cvType;         // application/pdf, etc.

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] cvData;         // contenu du fichier CV

    @Column(updatable = false)
    private LocalDateTime datePostulation = LocalDateTime.now();
}
