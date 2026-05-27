package com.stagebook.controller;

import com.stagebook.entity.Candidature;
import com.stagebook.entity.Etudiant;
import com.stagebook.entity.Offre;
import com.stagebook.repository.CandidatureRepository;
import com.stagebook.repository.EtudiantRepository;
import com.stagebook.repository.OffreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/candidatures")
@CrossOrigin(origins = "http://localhost:4200")
public class CandidatureController {

    @Autowired private CandidatureRepository candidatureRepository;
    @Autowired private EtudiantRepository etudiantRepository;
    @Autowired private OffreRepository offreRepository;

    // ── POST /api/candidatures/postuler ─────────────────────
    // Reçoit : etudiantId, offreId, lettre, cv (fichier)
    @PostMapping(value = "/postuler", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> postuler(
            @RequestParam("etudiantId") Long etudiantId,
            @RequestParam("offreId")    Long offreId,
            @RequestParam("lettre")     String lettre,
            @RequestParam("cv")         MultipartFile cv) {
        try {
            // Vérifier déjà postulé
            if (candidatureRepository.existsByEtudiantIdAndOffreId(etudiantId, offreId)) {
                return ResponseEntity.badRequest().body("Vous avez déjà postulé à cette offre !");
            }

            Etudiant etudiant = etudiantRepository.findById(etudiantId)
                .orElseThrow(() -> new RuntimeException("Étudiant non trouvé"));

            Offre offre = offreRepository.findById(offreId)
                .orElseThrow(() -> new RuntimeException("Offre non trouvée"));

            // Incrémenter le compteur de candidatures
            offre.setNombreCandidatures(offre.getNombreCandidatures() + 1);
            offreRepository.save(offre);

            // Créer la candidature avec le CV
            Candidature c = new Candidature();
            c.setEtudiant(etudiant);
            c.setOffre(offre);
            c.setLettreMOtivation(lettre);
            c.setStatut("EN_ATTENTE");

            // Sauvegarder le CV
            if (!cv.isEmpty()) {
                c.setCvNomFichier(cv.getOriginalFilename());
                c.setCvType(cv.getContentType());
                c.setCvData(cv.getBytes());
            }

            candidatureRepository.save(c);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Candidature envoyée avec succès !");
            response.put("statut", "EN_ATTENTE");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur : " + e.getMessage());
        }
    }

    // ── GET /api/candidatures/etudiant/{id} ─────────────────
    @GetMapping("/etudiant/{etudiantId}")
    public ResponseEntity<List<Candidature>> getMesCandidatures(@PathVariable Long etudiantId) {
        return ResponseEntity.ok(candidatureRepository.findByEtudiantId(etudiantId));
    }

    // ── GET /api/candidatures/offre/{id} ────────────────────
    // Pour l'entreprise : voir toutes les candidatures d'une offre
    @GetMapping("/offre/{offreId}")
    public ResponseEntity<List<Candidature>> getCandidaturesOffre(@PathVariable Long offreId) {
        return ResponseEntity.ok(candidatureRepository.findByOffreId(offreId));
    }

    // ── GET /api/candidatures/{id}/cv ───────────────────────
    // Télécharger le CV d'une candidature
    @GetMapping("/{id}/cv")
    public ResponseEntity<byte[]> telechargerCV(@PathVariable Long id) {
        Candidature c = candidatureRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Candidature non trouvée"));

        if (c.getCvData() == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + c.getCvNomFichier() + "\"")
            .contentType(MediaType.parseMediaType(
                    c.getCvType() != null ? c.getCvType() : "application/octet-stream"))
            .body(c.getCvData());
    }

    // ── PUT /api/candidatures/{id}/statut ───────────────────
    @PutMapping("/{id}/statut")
    public ResponseEntity<Candidature> updateStatut(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        Candidature c = candidatureRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Candidature non trouvée"));
        c.setStatut(body.get("statut"));
        return ResponseEntity.ok(candidatureRepository.save(c));
    }
}
