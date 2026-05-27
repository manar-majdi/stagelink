package com.stagebook.service;

import com.stagebook.entity.Offre;
import com.stagebook.repository.OffreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class OffreService {

    @Autowired
    private OffreRepository offreRepository;

    // Toutes les offres
    public List<Offre> getAll() {
        return offreRepository.findAll();
    }

    // Une offre par ID
    public Offre getById(Long id) {
        return offreRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Offre non trouvée avec l'id : " + id));
    }

    // Recherche avancée combinée
    public List<Offre> search(String query, String ville, String domaine, String typeStage) {
        String q     = (query     != null && !query.isEmpty())     ? query     : null;
        String v     = (ville     != null && !ville.isEmpty())     ? ville     : null;
        String d     = (domaine   != null && !domaine.isEmpty())   ? domaine   : null;
        String t     = (typeStage != null && !typeStage.isEmpty()) ? typeStage : null;
        return offreRepository.searchAvanced(q, v, d, t);
    }

    // Offres mises en avant
    public List<Offre> getFeatured() {
        return offreRepository.findByFeaturedTrue();
    }

    // Créer une offre
    public Offre create(Offre offre) {
        return offreRepository.save(offre);
    }

    // Modifier une offre
    public Offre update(Long id, Offre updated) {
        Offre existing = getById(id);
        existing.setTitre(updated.getTitre());
        existing.setEntreprise(updated.getEntreprise());
        existing.setVille(updated.getVille());
        existing.setTypeStage(updated.getTypeStage());
        existing.setDuree(updated.getDuree());
        existing.setSalaire(updated.getSalaire());
        existing.setDomaine(updated.getDomaine());
        existing.setNiveau(updated.getNiveau());
        existing.setDateDebut(updated.getDateDebut());
        existing.setSecteur(updated.getSecteur());
        existing.setDescription(updated.getDescription());
        existing.setMissions(updated.getMissions());
        existing.setCompetences(updated.getCompetences());
        return offreRepository.save(existing);
    }

    // Supprimer une offre
    public void delete(Long id) {
        offreRepository.deleteById(id);
    }
}
