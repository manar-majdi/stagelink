import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { OffreService } from './offre.service';
import { AuthComponent } from './auth.component';
import { ProfileComponent } from './profile.component';

interface FilterOption {
  label: string;
  count?: number;
  checked: boolean;
}

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule, HttpClientModule, AuthComponent, ProfileComponent],
  templateUrl: './app.html',
  styleUrl: './app.scss',
  encapsulation: ViewEncapsulation.None
})
export class App implements OnInit {

  currentPage: 'home' | 'auth' | 'profile' = 'home';
  authMode: 'login' | 'register' = 'login';
  currentUser: any = null;
  avatarOpen = false;

  searchQuery = '';
  searchCompany = '';
  searchCity = '';

  modalOpen = false;
  selectedJob: any = null;
  toastMsg = '';
  toastVisible = false;
  loading = false;
  errorConnection = false;
  private toastTimer: any;
  private retryTimer: any;

  // ── Modal Postuler ───────────────────────────────────────
  postulModal   = false;
  postulOffre: any = null;
  postulStep    = 1;
  postulLoading = false;
  postulSuccess = false;
  postulError   = '';
  lettre        = '';
  cvFile: File | null = null;
  cvFileName = '';
  cvFileSize = '';
  cvError    = '';
  docFile: File | null = null;

  // ── Filtres ─────────────────────────────────────────────
  activeChip = 'Tous';
  chips = [
    'Tous','💻 Informatique','📊 Finance','🏗️ Génie Civil',
    '📣 Marketing','⚡ Électronique','🔬 Sciences',
    '🎨 Design','⚖️ Droit','🏥 Santé','🤖 IA / Data'
  ];

  typeFilters: FilterOption[] = [
    { label: 'Stage PFE',          count: 0, checked: false },
    { label: "Stage d'initiation", count: 0, checked: false },
    { label: 'Stage ouvrier',      count: 0, checked: false },
    { label: 'Alternance',         count: 0, checked: false },
  ];

  cityFilters: FilterOption[] = [
    { label: 'Tunis',    count: 0, checked: false },
    { label: 'Sfax',     count: 0, checked: false },
    { label: 'Sousse',   count: 0, checked: false },
    { label: 'Monastir', count: 0, checked: false },
    { label: 'Bizerte',  count: 0, checked: false },
  ];

  durationFilters: FilterOption[] = [
    { label: '1 mois',      checked: false },
    { label: '2–3 mois',    checked: false },
    { label: '4–6 mois',    checked: false },
    { label: '6 mois et +', checked: false },
  ];

  sectorFilters: FilterOption[] = [
    { label: 'Informatique / IT', count: 0, checked: false },
    { label: 'Banque / Finance',  count: 0, checked: false },
    { label: 'Industrie',         count: 0, checked: false },
    { label: 'Télécom',           count: 0, checked: false },
    { label: 'Énergie',           count: 0, checked: false },
  ];

  verifiedOnly: FilterOption = { label: '✅ Vérifiée seulement', checked: false };

  allJobs: any[] = [];
  jobs:    any[] = [];
  companies: any[] = [];
  savedJobs: Set<number> = new Set();
  activePage = 1;

  constructor(private offreService: OffreService) {}

  ngOnInit() {
    // Toujours déconnecté au démarrage
    try {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
    } catch (e) {}
    this.currentUser = null;
    this.chargerOffres();
    this.chargerEntreprises();
  }

  // ── Navigation ──────────────────────────────────────────
  goToLogin()    { this.authMode = 'login';    this.currentPage = 'auth'; }
  goToRegister() { this.authMode = 'register'; this.currentPage = 'auth'; }
  goToProfile()  { this.currentPage = 'profile'; this.avatarOpen = false; }

  goToHome() {
    try {
      const u = localStorage.getItem('user');
      if (u) this.currentUser = JSON.parse(u);
    } catch (e) {}
    this.currentPage = 'home';
    if (this.allJobs.length === 0) this.chargerOffres();
    if (this.companies.length === 0) this.chargerEntreprises();
  }

  doLogout() {
    try { localStorage.removeItem('token'); localStorage.removeItem('user'); } catch (e) {}
    this.currentUser = null;
    this.currentPage = 'home';
    this.avatarOpen = false;
    this.showToast('👋 Déconnecté avec succès');
  }

  getUserInitials(): string {
    if (!this.currentUser) return '?';
    return ((this.currentUser.prenom||'?')[0] + (this.currentUser.nom||'?')[0]).toUpperCase();
  }

  // ── Données ─────────────────────────────────────────────
  chargerOffres(tentative = 1) {
    this.loading = true; this.errorConnection = false;
    this.offreService.getOffres().subscribe({
      next: (data) => {
        this.allJobs = data;
        this.calculerCompteurs(data);
        this.appliquerFiltres();
        this.loading = false;
      },
      error: () => {
        if (tentative < 5) this.retryTimer = setTimeout(() => this.chargerOffres(tentative+1), 2000);
        else { this.loading = false; this.errorConnection = true; }
      }
    });
  }

  // Calcule les vrais compteurs depuis les données BDD
  calculerCompteurs(data: any[]) {
    // Type de stage
    this.typeFilters.forEach(f => {
      f.count = data.filter(j =>
        j.typeStage?.toLowerCase().includes(f.label.toLowerCase())
      ).length;
    });

    // Ville
    this.cityFilters.forEach(f => {
      f.count = data.filter(j =>
        j.ville?.toLowerCase().includes(f.label.toLowerCase())
      ).length;
    });

    // Secteur
    this.sectorFilters.forEach(f => {
      const s = f.label.toLowerCase().split('/')[0].trim();
      f.count = data.filter(j =>
        j.secteur?.toLowerCase().includes(s) ||
        j.domaine?.toLowerCase().includes(s)
      ).length;
    });
  }

  chargerEntreprises(tentative = 1) {
    this.offreService.getEntreprises().subscribe({
      next:  (d) => { this.companies = d; },
      error: ()  => { if (tentative < 5) setTimeout(() => this.chargerEntreprises(tentative+1), 2000); }
    });
  }

  // ── Filtrage ─────────────────────────────────────────────
  appliquerFiltres() {
    let r = [...this.allJobs];
    if (this.activeChip !== 'Tous') {
      const d = this.activeChip.replace(/\p{Emoji}/gu,'').trim().toLowerCase();
      r = r.filter(j => j.domaine?.toLowerCase().includes(d));
    }
    const q = this.searchQuery.toLowerCase().trim();
    if (q) r = r.filter(j =>
      j.titre?.toLowerCase().includes(q) ||
      j.entreprise?.toLowerCase().includes(q) ||
      j.domaine?.toLowerCase().includes(q) ||
      (j.competences && j.competences.toLowerCase().includes(q))
    );
    const cs = this.searchCity.toLowerCase().trim();
    if (cs) r = r.filter(j => j.ville?.toLowerCase().includes(cs));
    const vc = this.cityFilters.filter(f=>f.checked).map(f=>f.label.toLowerCase());
    if (vc.length) r = r.filter(j => vc.some(v => j.ville?.toLowerCase().includes(v)));
    const tc = this.typeFilters.filter(f=>f.checked).map(f=>f.label.toLowerCase());
    if (tc.length) r = r.filter(j => tc.some(t => j.typeStage?.toLowerCase().includes(t)));
    const dc = this.durationFilters.filter(f=>f.checked).map(f=>f.label);
    if (dc.length) r = r.filter(j => {
      const dur = j.duree?.toLowerCase()||'';
      return dc.some(d => {
        if (d==='1 mois')      return dur.includes('1 mois');
        if (d==='2–3 mois')    return dur.includes('2')||dur.includes('3');
        if (d==='4–6 mois')    return ['4','5','6'].some(n=>dur.includes(n+' mois'));
        if (d==='6 mois et +') return parseInt(dur)>=6;
        return false;
      });
    });
    const sc = this.sectorFilters.filter(f=>f.checked).map(f=>f.label.toLowerCase());
    if (sc.length) r = r.filter(j => sc.some(s => j.secteur?.toLowerCase().includes(s.split('/')[0].trim())));
    if (this.verifiedOnly.checked) r = r.filter(j => j.verifie===true);
    this.jobs = r; this.activePage = 1;
  }

  doSearch() { this.appliquerFiltres(); this.showToast(`🔍 ${this.jobs.length} offre(s)`); }
  setChip(c: string) { this.activeChip = c; this.appliquerFiltres(); }
  toggleFilter(f: FilterOption) { f.checked = !f.checked; this.appliquerFiltres(); }

  filterByEntreprise(nom: string) {
    this.searchQuery = this.searchQuery === nom ? '' : nom;
    this.appliquerFiltres();
  }

  resetFilters() {
    [...this.typeFilters,...this.cityFilters,...this.durationFilters,...this.sectorFilters].forEach(f=>f.checked=false);
    this.verifiedOnly.checked = false;
    this.activeChip = 'Tous';
    this.searchQuery = ''; this.searchCity = ''; this.searchCompany = '';
    this.jobs = [...this.allJobs]; this.activePage = 1;
    this.showToast('🔄 Filtres réinitialisés');
  }

  // ── Modal Détail ─────────────────────────────────────────
  openJobModal(job: any) { this.selectedJob = job; this.modalOpen = true; }
  closeModal() { this.modalOpen = false; }
  closeModalOverlay(e: MouseEvent) {
    if ((e.target as HTMLElement).classList.contains('modal-overlay')) this.modalOpen = false;
  }

  // ── Modal Postuler ───────────────────────────────────────
  postulDepuisModal() {
    if (!this.currentUser) { this.closeModal(); this.goToLogin(); return; }
    this.ouvrirPostulerDirect(this.selectedJob);
  }

  ouvrirPostuler(job: any, e: MouseEvent) {
    e.stopPropagation();
    if (!this.currentUser) { this.goToLogin(); return; }
    this.ouvrirPostulerDirect(job);
  }

  ouvrirPostulerDirect(job: any) {
    this.postulOffre   = job;
    this.postulStep    = 1;
    this.postulSuccess = false;
    this.postulError   = '';
    this.lettre        = '';
    this.cvFile        = null;
    this.cvFileName    = '';
    this.cvFileSize    = '';
    this.cvError       = '';
    this.docFile       = null;
    this.postulModal   = true;
    this.modalOpen     = false;
  }

  closePostulOverlay(e: MouseEvent) {
    if ((e.target as HTMLElement).classList.contains('modal-overlay'))
      this.postulModal = false;
  }

  nextStep() {
    if (this.postulStep === 2 && !this.cvFile) {
      this.cvError = 'Veuillez ajouter votre CV avant de continuer.';
      return;
    }
    this.cvError = '';
    this.postulStep++;
  }

  // ── CV Upload ────────────────────────────────────────────
  onCvSelect(event: any) {
    const file = event.target.files[0];
    if (file) this.setCvFile(file);
  }

  onCvDrop(event: DragEvent) {
    event.preventDefault();
    const file = event.dataTransfer?.files[0];
    if (file) this.setCvFile(file);
  }

  setCvFile(file: File) {
    const maxSize = 5 * 1024 * 1024;
    const allowed = ['application/pdf','application/msword',
                     'application/vnd.openxmlformats-officedocument.wordprocessingml.document'];
    if (!allowed.includes(file.type)) {
      this.cvError = 'Format non supporté. Utilisez PDF, DOC ou DOCX.';
      return;
    }
    if (file.size > maxSize) {
      this.cvError = 'Fichier trop lourd. Maximum 5 MB.';
      return;
    }
    this.cvFile     = file;
    this.cvFileName = file.name;
    this.cvFileSize = (file.size / 1024).toFixed(0) + ' KB';
    this.cvError    = '';
  }

  removeCV() { this.cvFile = null; this.cvFileName = ''; this.cvFileSize = ''; }

  onDocSelect(event: any) { this.docFile = event.target.files[0]; }

  // ── Lettre ───────────────────────────────────────────────
  genererLettreAuto(job?: any): string {
    const j = job || this.postulOffre;
    if (!j || !this.currentUser) return '';
    return `Madame, Monsieur,

Je me permets de vous adresser ma candidature pour le poste de ${j.titre} au sein de votre entreprise ${j.entreprise}.

Actuellement étudiant(e) en ${this.currentUser.niveau || 'formation'} à ${this.currentUser.universite || 'mon établissement'}, spécialisé(e) en ${this.currentUser.filiere || 'ma filière'}, je suis particulièrement motivé(e) par cette opportunité de stage.

Votre entreprise m'attire notamment pour son expertise dans le secteur ${j.secteur || j.domaine}. Ce stage de ${j.duree} me permettrait de mettre en pratique mes compétences et d'acquérir une expérience professionnelle enrichissante.

Je reste disponible pour tout entretien à votre convenance.

Veuillez agréer, Madame, Monsieur, l'expression de mes salutations distinguées.

${this.currentUser.prenom} ${this.currentUser.nom}
${this.currentUser.email}`;
  }

  genererLettre() {
    this.lettre = this.genererLettreAuto();
    this.showToast('✍️ Lettre générée automatiquement');
  }

  viderLettre() { this.lettre = ''; }

  // ── Envoi ─────────────────────────────────────────────────
  envoyerCandidature() {
    if (!this.cvFile)            { this.postulError = 'Veuillez ajouter votre CV.'; return; }
    if (this.lettre.length < 50) { this.postulError = 'Votre lettre est trop courte.'; return; }

    this.postulLoading = true;
    this.postulError   = '';

    // Envoi réel avec CV vers Spring Boot
    if (this.currentUser?.id && this.postulOffre?.id) {
      this.offreService.postulerAvecCV(
        this.currentUser.id,
        this.postulOffre.id,
        this.lettre,
        this.cvFile
      ).subscribe({
        next: () => {
          this.postulLoading = false;
          this.postulSuccess = true;
          this.showToast('🎉 Candidature envoyée avec succès !');
        },
        error: (err) => {
          this.postulLoading = false;
          this.postulError = err.error || 'Erreur lors de l\'envoi.';
        }
      });
    } else {
      // Mode démo (sans ID utilisateur)
      setTimeout(() => {
        this.postulLoading = false;
        this.postulSuccess = true;
        this.showToast('🎉 Candidature envoyée avec succès !');
      }, 1500);
    }
  }

  // ── Favoris ──────────────────────────────────────────────
  toggleSave(i: number, e: MouseEvent) {
    e.stopPropagation();
    if (!this.currentUser) { this.goToLogin(); return; }
    if (this.savedJobs.has(i)) { this.savedJobs.delete(i); this.showToast('Retiré des favoris'); }
    else { this.savedJobs.add(i); this.showToast('🔖 Stage sauvegardé'); }
  }

  isSaved(i: number) { return this.savedJobs.has(i); }
  setPage(p: number) { this.activePage = p; }

  showToast(msg: string) {
    this.toastMsg = msg; this.toastVisible = true;
    clearTimeout(this.toastTimer);
    this.toastTimer = setTimeout(() => this.toastVisible = false, 3000);
  }

  parseJson(str: string): string[] {
    try { return JSON.parse(str||'[]'); } catch { return []; }
  }
}
