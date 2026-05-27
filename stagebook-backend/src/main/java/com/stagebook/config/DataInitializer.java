package com.stagebook.config;

import com.stagebook.entity.Offre;
import com.stagebook.entity.Entreprise;
import com.stagebook.entity.Etudiant;
import com.stagebook.repository.OffreRepository;
import com.stagebook.repository.EntrepriseRepository;
import com.stagebook.repository.EtudiantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired private OffreRepository offreRepository;
    @Autowired private EntrepriseRepository entrepriseRepository;
    @Autowired private EtudiantRepository etudiantRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (offreRepository.count() > 0) return;

        // ── Entreprises ──────────────────────────────────────
        Entreprise vermeg = creerEntreprise("Vermeg", "Fintech", "Tunis", "💎", true);
        Entreprise ooredoo = creerEntreprise("Ooredoo Tunisie", "Télécom", "Tunis", "📡", true);
        Entreprise biat = creerEntreprise("BIAT", "Banque", "Tunis", "🏦", true);
        Entreprise steg = creerEntreprise("STEG", "Énergie", "Sfax", "⚡", true);
        Entreprise intilaq = creerEntreprise("IntilaQ", "Startup Tech", "Tunis", "🎨", true);
        Entreprise comete = creerEntreprise("COMETE Engineering", "BTP / Ingénierie", "Tunis", "🏗️", false);

        // ── Offres liées aux entreprises ─────────────────────
        creerOffre("Développeur Full-Stack Angular / Spring Boot",
            vermeg, "Tunis, Lac II", "Stage PFE", "6 mois", "500 DT/mois",
            "Informatique", "Licence / Master", "Mars 2026", "💻",
            "Rejoignez notre équipe R&D pour développer des modules Angular et des API Spring Boot.",
            "[\"Développer des fonctionnalités Angular\",\"Créer des API REST avec Spring Boot\",\"Écrire des tests unitaires\",\"Travailler en Agile/Scrum\"]",
            "[\"Angular\",\"Spring Boot\",\"Java\",\"TypeScript\",\"Git\"]",
            true, false, 42);

        creerOffre("Stage Data Analyst / Machine Learning",
            ooredoo, "Tunis, CUN", "Stage PFE", "4 mois", "450 DT/mois",
            "Informatique", "Master / Ingénieur", "Avril 2026", "📡",
            "Analysez les données de trafic réseau et développez des modèles prédictifs.",
            "[\"Nettoyer les données\",\"Construire des modèles ML\",\"Créer des dashboards Power BI\"]",
            "[\"Python\",\"Pandas\",\"Scikit-learn\",\"SQL\",\"Power BI\"]",
            false, false, 28);

        creerOffre("Stage Finance & Analyse de Risques",
            biat, "Tunis, Av. H. Bourguiba", "Stage d'initiation", "3 mois", "350 DT/mois",
            "Finance", "Licence Finance", "Juin 2026", "🏦",
            "Participez à l'analyse des risques de crédit et de marché.",
            "[\"Analyser les dossiers de crédit\",\"Reportings Bâle III\",\"Analyses de sensibilité\"]",
            "[\"Excel\",\"Analyse financière\",\"VBA\",\"IFRS\"]",
            false, false, 65);

        creerOffre("Stage Génie Électrique / Automatisme",
            steg, "Sfax, Zone Industrielle", "Stage ouvrier", "2 mois", "Non rémunéré",
            "Électronique", "Licence Génie Électrique", "Dès maintenant", "⚡",
            "Découvrez les réseaux de distribution électrique.",
            "[\"Observer les interventions terrain\",\"Analyser les plans HTA/BT\",\"Rédiger un rapport\"]",
            "[\"AutoCAD\",\"SCADA\",\"PLC\"]",
            false, true, 18);

        creerOffre("Stage UX/UI Designer — Applications mobiles",
            intilaq, "Tunis, El Ghazala", "Stage PFE", "4 mois", "400 DT/mois",
            "Design", "Licence Design / Info", "Mai 2026", "🎨",
            "Concevez des expériences mobiles intuitives sur iOS et Android.",
            "[\"Mener des recherches utilisateurs\",\"Créer des prototypes Figma\",\"Collaborer avec les devs React Native\"]",
            "[\"Figma\",\"Adobe XD\",\"React Native\",\"Prototypage\"]",
            false, false, 31);

        creerOffre("Stage Génie Civil — Bureau d'Études",
            comete, "Tunis, Montplaisir", "Stage PFE", "4 mois", "300 DT/mois",
            "Génie Civil", "Licence / Master Génie Civil", "Juillet 2026", "🏗️",
            "Participez à la conception et au dimensionnement de structures.",
            "[\"Modéliser sur Robot Structural\",\"Participer aux études de sol\",\"Rédiger des notes de calcul\"]",
            "[\"AutoCAD\",\"Robot Structural\",\"BIM\",\"Revit\"]",
            false, false, 22);

        // ── Étudiant test ────────────────────────────────────
        if (!etudiantRepository.existsByEmail("test@stagebook.tn")) {
            Etudiant e = new Etudiant();
            e.setPrenom("Amira"); e.setNom("Benali");
            e.setEmail("test@stagebook.tn");
            e.setMotDePasse(passwordEncoder.encode("password123"));
            e.setUniversite("ESPRIT"); e.setFiliere("Informatique");
            e.setNiveau("Master 1"); e.setRole("ETUDIANT");
            etudiantRepository.save(e);
        }

        System.out.println("✅ Données insérées avec succès !");
    }

    private Entreprise creerEntreprise(String nom, String secteur, String ville,
                                        String logo, boolean verifie) {
        Entreprise e = new Entreprise();
        e.setNom(nom); e.setSecteur(secteur);
        e.setVille(ville); e.setLogo(logo);
        e.setVerifie(verifie);
        return entrepriseRepository.save(e);
    }

    private void creerOffre(String titre, Entreprise entreprise, String ville,
                             String type, String duree, String salaire,
                             String domaine, String niveau, String dateDebut,
                             String logo, String description, String missions,
                             String competences, boolean featured, boolean urgent,
                             int nbCandidatures) {
        Offre o = new Offre();
        o.setTitre(titre);
        o.setEntreprise(entreprise.getNom());  // nom pour la recherche
        o.setEntrepriseObj(entreprise);         // lien objet pour le comptage
        o.setVille(ville); o.setTypeStage(type);
        o.setDuree(duree); o.setSalaire(salaire);
        o.setDomaine(domaine); o.setNiveau(niveau);
        o.setDateDebut(dateDebut); o.setLogo(logo);
        o.setDescription(description);
        o.setMissions(missions); o.setCompetences(competences);
        o.setFeatured(featured); o.setUrgent(urgent);
        o.setVerifie(true); o.setNombreCandidatures(nbCandidatures);
        offreRepository.save(o);
    }
}
