import { Component, Input, Output, EventEmitter, ViewEncapsulation, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { OffreService } from './offre.service';
import { CandidatureFilterPipe } from './candidature-filter.pipe';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, FormsModule, HttpClientModule, CandidatureFilterPipe],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.scss',
  encapsulation: ViewEncapsulation.None
})
export class ProfileComponent implements OnInit {

  @Input() user: any = null;
  @Output() backToHome = new EventEmitter<void>();
  @Output() logout     = new EventEmitter<void>();

  activeTab: 'profil' | 'candidatures' | 'favoris' = 'profil';

  editMode    = false;
  profileData: any = {};
  saving      = false;
  saveSuccess = false;

  candidatures: any[]    = [];
  loadingCandidatures    = false;

  postulModal   = false;
  postulOffre: any = null;
  lettre        = '';
  postulLoading = false;
  postulSuccess = false;
  postulError   = '';

  favoris: any[] = [];

  toastMsg     = '';
  toastVisible = false;
  private toastTimer: any;

  constructor(private offreService: OffreService) {}

  ngOnInit() {
    if (this.user) {
      this.profileData = { ...this.user };
      this.chargerCandidatures();
    }
    try {
      const f = localStorage.getItem('favoris');
      if (f) this.favoris = JSON.parse(f);
    } catch (e) {}
  }

  // ── Tabs ─────────────────────────────────────────────────
  setTab(tab: 'profil' | 'candidatures' | 'favoris') {
    this.activeTab = tab;
    if (tab === 'candidatures') this.chargerCandidatures();
  }

  // ── Complétion profil ────────────────────────────────────
  getCompletion(): number {
    if (!this.user) return 0;
    const fields = ['prenom','nom','email','telephone','ville','universite','filiere','niveau','bio'];
    const filled  = fields.filter(f => this.user[f] && this.user[f].toString().trim() !== '').length;
    return Math.round((filled / fields.length) * 100);
  }

  getMissingFields(): string {
    if (!this.user) return '';
    const labels: any = {
      telephone:'votre téléphone', ville:'votre ville',
      universite:"votre université", filiere:'votre filière',
      niveau:'votre niveau', bio:'une bio'
    };
    const missing = Object.keys(labels).filter(f => !this.user[f] || this.user[f].trim() === '');
    return missing.slice(0,2).map(f => labels[f]).join(' et ');
  }

  // ── Profil ───────────────────────────────────────────────
  startEdit()  { this.profileData = { ...this.user }; this.editMode = true; }
  cancelEdit() { this.editMode = false; }

  saveProfile() {
    this.saving = true;
    setTimeout(() => {
      this.user = { ...this.profileData };
      try { localStorage.setItem('user', JSON.stringify(this.user)); } catch (e) {}
      this.saving      = false;
      this.editMode    = false;
      this.saveSuccess = true;
      this.showToast('✅ Profil mis à jour !');
      setTimeout(() => this.saveSuccess = false, 3000);
    }, 800);
  }

  getUserInitials(): string {
    if (!this.user) return '?';
    return ((this.user.prenom||'?')[0] + (this.user.nom||'?')[0]).toUpperCase();
  }

  // ── Candidatures ─────────────────────────────────────────
  chargerCandidatures() {
    if (!this.user?.id) {
      // Données fictives pour démo
      this.candidatures = [
        {
          id:1,
          offre:{ titre:'Développeur Full-Stack Angular/Spring Boot', entreprise:'Vermeg', ville:'Tunis', logo:'💻' },
          statut:'EN_ATTENTE',
          datePostulation: new Date().toISOString()
        },
        {
          id:2,
          offre:{ titre:'Stage Data Analyst / Machine Learning', entreprise:'Ooredoo', ville:'Tunis', logo:'📡' },
          statut:'ACCEPTEE',
          datePostulation: new Date(Date.now()-86400000*3).toISOString()
        },
        {
          id:3,
          offre:{ titre:'Stage Finance & Analyse de Risques', entreprise:'BIAT', ville:'Tunis', logo:'🏦' },
          statut:'EN_COURS',
          datePostulation: new Date(Date.now()-86400000*7).toISOString()
        }
      ];
      return;
    }
    this.loadingCandidatures = true;
    this.offreService.getMesCandidatures(this.user.id).subscribe({
      next:  (data) => { this.candidatures = data; this.loadingCandidatures = false; },
      error: ()     => { this.loadingCandidatures = false; }
    });
  }

  getStatutLabel(statut: string): string {
    const m: any = { EN_ATTENTE:'⏳ En attente', ACCEPTEE:'✅ Acceptée', REFUSEE:'❌ Refusée', EN_COURS:'🔄 En cours' };
    return m[statut] || statut;
  }

  getStatutClass(statut: string): string {
    const m: any = { EN_ATTENTE:'statut-attente', ACCEPTEE:'statut-acceptee', REFUSEE:'statut-refusee', EN_COURS:'statut-encours' };
    return m[statut] || '';
  }

  formatDate(d: string): string {
    if (!d) return '';
    return new Date(d).toLocaleDateString('fr-FR', { day:'2-digit', month:'short', year:'numeric' });
  }

  // ── Postuler ─────────────────────────────────────────────
  ouvrirPostul(offre: any) {
    this.postulOffre  = offre;
    this.lettre       = `Bonjour,\n\nJe me permets de vous adresser ma candidature pour le poste de ${offre.titre}.\n\nJe suis étudiant(e) en ${this.user?.niveau || '...'} à ${this.user?.universite || '...'}, spécialisé(e) en ${this.user?.filiere || '...'}.\n\nJe suis particulièrement motivé(e) par ce stage car...\n\nDans l'attente d'un retour de votre part,\nCordialement,\n${this.user?.prenom} ${this.user?.nom}`;
    this.postulSuccess = false;
    this.postulError   = '';
    this.postulModal   = true;
  }

  fermerPostul() { this.postulModal = false; }

  postuler() {
    if (!this.lettre.trim()) { this.postulError = 'Veuillez écrire une lettre de motivation.'; return; }
    this.postulLoading = true;
    this.postulError   = '';

    if (!this.user?.id) {
      // Simuler envoi si pas d'API
      setTimeout(() => {
        this.postulLoading = false;
        this.postulSuccess = true;
        this.showToast('🎉 Candidature envoyée !');
        setTimeout(() => { this.postulModal = false; this.chargerCandidatures(); }, 1500);
      }, 1000);
      return;
    }

    this.offreService.postulerAvecCV(
      this.user.id,
      this.postulOffre.id,
      this.lettre,
      new File([], 'cv.pdf') // CV géré depuis app.ts
    ).subscribe({
      next: () => {
        this.postulLoading = false;
        this.postulSuccess = true;
        this.showToast('🎉 Candidature envoyée !');
        setTimeout(() => { this.postulModal = false; this.chargerCandidatures(); }, 1500);
      },
      error: (err: any) => {
        this.postulLoading = false;
        this.postulError = err.error || 'Erreur lors de la candidature.';
      }
    });
  }

  // ── Favoris ───────────────────────────────────────────────
  retirerFavori(i: number) {
    this.favoris.splice(i, 1);
    try { localStorage.setItem('favoris', JSON.stringify(this.favoris)); } catch (e) {}
    this.showToast('Favori retiré');
  }

  showToast(msg: string) {
    this.toastMsg     = msg;
    this.toastVisible = true;
    clearTimeout(this.toastTimer);
    this.toastTimer = setTimeout(() => this.toastVisible = false, 3000);
  }

  onLogout() { this.logout.emit(); }
  goBack()   { this.backToHome.emit(); }
}
