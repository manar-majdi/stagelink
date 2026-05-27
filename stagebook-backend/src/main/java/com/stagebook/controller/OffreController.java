package com.stagebook.controller;

import com.stagebook.entity.Offre;
import com.stagebook.service.OffreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/offres")
@CrossOrigin(origins = "http://localhost:4200")
public class OffreController {

    @Autowired
    private OffreService offreService;

    // GET /api/offres → toutes les offres
    @GetMapping
    public ResponseEntity<List<Offre>> getAll() {
        return ResponseEntity.ok(offreService.getAll());
    }

    // GET /api/offres/1 → une offre par ID
    @GetMapping("/{id}")
    public ResponseEntity<Offre> getById(@PathVariable Long id) {
        return ResponseEntity.ok(offreService.getById(id));
    }

    // GET /api/offres/featured → offres mises en avant
    @GetMapping("/featured")
    public ResponseEntity<List<Offre>> getFeatured() {
        return ResponseEntity.ok(offreService.getFeatured());
    }

    // GET /api/offres/search?query=java&ville=tunis&domaine=informatique&typeStage=PFE
    @GetMapping("/search")
    public ResponseEntity<List<Offre>> search(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String ville,
            @RequestParam(required = false) String domaine,
            @RequestParam(required = false) String typeStage) {
        return ResponseEntity.ok(offreService.search(query, ville, domaine, typeStage));
    }

    // POST /api/offres → créer une offre
    @PostMapping
    public ResponseEntity<Offre> create(@RequestBody Offre offre) {
        return ResponseEntity.ok(offreService.create(offre));
    }

    // PUT /api/offres/1 → modifier une offre
    @PutMapping("/{id}")
    public ResponseEntity<Offre> update(@PathVariable Long id, @RequestBody Offre offre) {
        return ResponseEntity.ok(offreService.update(id, offre));
    }

    // DELETE /api/offres/1 → supprimer une offre
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        offreService.delete(id);
        return ResponseEntity.ok("Offre supprimée avec succès");
    }
}
