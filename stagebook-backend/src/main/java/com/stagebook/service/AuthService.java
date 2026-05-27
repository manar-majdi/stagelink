package com.stagebook.service;

import com.stagebook.dto.AuthResponse;
import com.stagebook.dto.LoginRequest;
import com.stagebook.dto.RegisterRequest;
import com.stagebook.entity.Etudiant;
import com.stagebook.entity.Entreprise;
import com.stagebook.repository.EtudiantRepository;
import com.stagebook.repository.EntrepriseRepository;
import com.stagebook.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private EtudiantRepository etudiantRepository;

    @Autowired
    private EntrepriseRepository entrepriseRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    // ── Inscription ──────────────
    public AuthResponse register(RegisterRequest request) {
        if ("ETUDIANT".equalsIgnoreCase(request.getTypeCompte())) {
            if (etudiantRepository.existsByEmail(request.getEmail()))
                throw new RuntimeException("Cet email est déjà utilisé !");

            Etudiant etudiant = new Etudiant();
            etudiant.setPrenom(request.getPrenom());
            etudiant.setNom(request.getNom());
            etudiant.setEmail(request.getEmail());
            etudiant.setMotDePasse(passwordEncoder.encode(request.getMotDePasse()));
            etudiant.setUniversite(request.getUniversite());
            etudiant.setFiliere(request.getFiliere());
            etudiant.setNiveau(request.getNiveau());
            etudiant.setRole("ETUDIANT");

            etudiantRepository.save(etudiant);
            String token = jwtUtils.generateToken(etudiant.getEmail());
            return new AuthResponse(token, etudiant.getPrenom(), etudiant.getNom(),
                                    etudiant.getEmail(), etudiant.getRole());

        } else if ("ENTREPRISE".equalsIgnoreCase(request.getTypeCompte())) {
            if (entrepriseRepository.existsByEmail(request.getEmail()))
                throw new RuntimeException("Cet email est déjà utilisé !");

            Entreprise entreprise = new Entreprise();
            entreprise.setNom(request.getNomEntreprise());
            entreprise.setEmail(request.getEmail());
            entreprise.setMotDePasse(passwordEncoder.encode(request.getMotDePasse()));
            entreprise.setSecteur(request.getSecteur());
            entreprise.setVille(request.getVille());
            entreprise.setSiteWeb(request.getSiteWeb());
            entreprise.setRole("ENTREPRISE");

            entrepriseRepository.save(entreprise);
            String token = jwtUtils.generateToken(entreprise.getEmail());
            return new AuthResponse(token, entreprise.getNom(), null,
                                    entreprise.getEmail(), entreprise.getRole());
        } else {
            throw new RuntimeException("Type de compte invalide !");
        }
    }

    // ── Connexion ──────────────
    public AuthResponse login(LoginRequest request) {
        // Étudiant
        if (etudiantRepository.existsByEmail(request.getEmail())) {
            Etudiant etudiant = etudiantRepository.findByEmail(request.getEmail()).get();
            if (!passwordEncoder.matches(request.getMotDePasse(), etudiant.getMotDePasse()))
                throw new RuntimeException("Email ou mot de passe incorrect");

            String token = jwtUtils.generateToken(etudiant.getEmail());
            return new AuthResponse(token, etudiant.getPrenom(), etudiant.getNom(),
                                    etudiant.getEmail(), etudiant.getRole());
        }

        // Entreprise
        if (entrepriseRepository.existsByEmail(request.getEmail())) {
            Entreprise entreprise = entrepriseRepository.findByEmail(request.getEmail()).get();
            if (!passwordEncoder.matches(request.getMotDePasse(), entreprise.getMotDePasse()))
                throw new RuntimeException("Email ou mot de passe incorrect");

            String token = jwtUtils.generateToken(entreprise.getEmail());
            return new AuthResponse(token, entreprise.getNom(), null,
                                    entreprise.getEmail(), entreprise.getRole());
        }

        throw new RuntimeException("Email ou mot de passe incorrect");
    }
}
