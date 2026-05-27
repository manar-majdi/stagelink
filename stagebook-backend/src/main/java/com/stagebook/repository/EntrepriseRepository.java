package com.stagebook.repository;

import com.stagebook.entity.Entreprise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface EntrepriseRepository extends JpaRepository<Entreprise, Long> {

    // Recherche par nom (insensible à la casse)
    Optional<Entreprise> findByNomIgnoreCase(String nom);

    // Toutes les entreprises vérifiées
    List<Entreprise> findByVerifieTrue();

    // Vérifie si une entreprise existe par email
    boolean existsByEmail(String email);

    // Récupère une entreprise par email
    Optional<Entreprise> findByEmail(String email);

    // Compte les offres par entreprise
    @Query("SELECT COUNT(o) FROM Offre o WHERE o.entrepriseObj.nom = :nom")
    long countOffresByEntreprise(String nom);
}
