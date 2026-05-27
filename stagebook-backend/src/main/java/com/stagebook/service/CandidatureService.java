package com.stagebook.service;

import com.stagebook.entity.Candidature;
import com.stagebook.entity.Etudiant;
import com.stagebook.entity.Offre;
import com.stagebook.repository.CandidatureRepository;
import com.stagebook.repository.EtudiantRepository;
import com.stagebook.repository.OffreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CandidatureService {

    @Autowired
    private CandidatureRepository candidatureRepository;

    @Autowired
    private EtudiantRepository etudiantRepository;

    @Autowired
    private OffreRepository offreRepository;

    // Postuler à une offre
    public Candidature postuler(Long etudiantId, Long offreId, String lettre) {
        if (candidatureRepository.existsByEtudiantIdAndOffreId(etudiantId, offreId)) {
            throw new RuntimeException("Vous avez déjà postulé à cette offre !");
        }

        Etudiant etudiant = etudiantRepository.findById(etudiantId)
            .orElseThrow(() -> new RuntimeException("Étudiant non trouvé"));

        Offre offre = offreRepository.findById(offreId)
            .orElseThrow(() -> new RuntimeException("Offre non trouvée"));

        // Incrémenter le compteur de candidatures
        offre.setNombreCandidatures(offre.getNombreCandidatures() + 1);
        offreRepository.save(offre);

        Candidature candidature = new Candidature();
        candidature.setEtudiant(etudiant);
        candidature.setOffre(offre);
        candidature.setLettreMOtivation(lettre);
        candidature.setStatut("EN_ATTENTE");

        return candidatureRepository.save(candidature);
    }

    // Mes candidatures
    public List<Candidature> getMesCandidatures(Long etudiantId) {
        return candidatureRepository.findByEtudiantId(etudiantId);
    }

    // Changer le statut (admin)
    public Candidature updateStatut(Long id, String statut) {
        Candidature c = candidatureRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Candidature non trouvée"));
        c.setStatut(statut);
        return candidatureRepository.save(c);
    }
}
