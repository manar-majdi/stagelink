package com.stagebook.controller;

import com.stagebook.entity.Offre;
import com.stagebook.repository.EntrepriseRepository;
import com.stagebook.repository.OffreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/entreprises")
@CrossOrigin(origins = "http://localhost:4200")
public class EntrepriseController {

    @Autowired private EntrepriseRepository entrepriseRepository;
    @Autowired private OffreRepository offreRepository;

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAll() {
        return ResponseEntity.ok(
            entrepriseRepository.findAll().stream().map(e -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id",      e.getId());
                map.put("nom",     e.getNom());
                map.put("secteur", e.getSecteur());
                map.put("ville",   e.getVille());
                map.put("logo",    e.getLogo());
                map.put("verifie", e.isVerifie());
                // Compter les offres par nom d'entreprise
                long count = offreRepository.findByEntrepriseIgnoreCase(e.getNom()).size();
                map.put("nombreOffres", count);
                return map;
            }).collect(Collectors.toList())
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Long id) {
        var e = entrepriseRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Entreprise non trouvée"));
        List<Offre> offres = offreRepository.findByEntrepriseIgnoreCase(e.getNom());
        Map<String, Object> map = new HashMap<>();
        map.put("id",          e.getId());
        map.put("nom",         e.getNom());
        map.put("secteur",     e.getSecteur());
        map.put("ville",       e.getVille());
        map.put("logo",        e.getLogo());
        map.put("verifie",     e.isVerifie());
        map.put("offres",      offres);
        map.put("nombreOffres", offres.size());
        return ResponseEntity.ok(map);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody com.stagebook.entity.Entreprise e) {
        return ResponseEntity.ok(entrepriseRepository.save(e));
    }
}
