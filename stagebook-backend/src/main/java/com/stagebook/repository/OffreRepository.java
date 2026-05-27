package com.stagebook.repository;

import com.stagebook.entity.Offre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OffreRepository extends JpaRepository<Offre, Long> {

    // Recherche par mot-clé dans titre ou entreprise
    @Query("SELECT o FROM Offre o WHERE " +
           "LOWER(o.titre) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(o.entreprise) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(o.domaine) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Offre> searchByQuery(@Param("query") String query);

    // Filtrer par ville
    List<Offre> findByVilleContainingIgnoreCase(String ville);

    // Filtrer par domaine
    List<Offre> findByDomaineIgnoreCase(String domaine);

    // Filtrer par type de stage
    List<Offre> findByTypeStageIgnoreCase(String typeStage);

    // Offres featured
    List<Offre> findByFeaturedTrue();

    // Recherche combinée
    @Query("SELECT o FROM Offre o WHERE " +
           "(:query IS NULL OR LOWER(o.titre) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(o.entreprise) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
           "(:ville IS NULL OR LOWER(o.ville) LIKE LOWER(CONCAT('%', :ville, '%'))) AND " +
           "(:domaine IS NULL OR LOWER(o.domaine) = LOWER(:domaine)) AND " +
           "(:typeStage IS NULL OR LOWER(o.typeStage) = LOWER(:typeStage))")
    List<Offre> searchAvanced(
        @Param("query") String query,
        @Param("ville") String ville,
        @Param("domaine") String domaine,
        @Param("typeStage") String typeStage
    );

    // Par entreprise
    List<Offre> findByEntrepriseIgnoreCase(String entreprise);
}
