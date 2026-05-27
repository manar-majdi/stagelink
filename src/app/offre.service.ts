import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class OffreService {

  private apiUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  // ── Offres ───────────────────────────────────────────────
  getOffres(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/offres`);
  }

  search(query: string, ville: string, domaine: string, typeStage: string): Observable<any[]> {
    let params = new HttpParams();
    if (query)     params = params.set('query', query);
    if (ville)     params = params.set('ville', ville);
    if (domaine)   params = params.set('domaine', domaine);
    if (typeStage) params = params.set('typeStage', typeStage);
    return this.http.get<any[]>(`${this.apiUrl}/offres/search`, { params });
  }

  // ── Entreprises ──────────────────────────────────────────
  getEntreprises(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/entreprises`);
  }

  getEntrepriseById(id: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/entreprises/${id}`);
  }

  // ── Auth ─────────────────────────────────────────────────
  login(email: string, motDePasse: string): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/auth/login`, { email, motDePasse });
  }

  register(data: any): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/auth/register`, data);
  }

  // ── Candidatures ─────────────────────────────────────────

  // Postuler avec CV (multipart/form-data)
  postulerAvecCV(
    etudiantId: number,
    offreId: number,
    lettre: string,
    cvFile: File
  ): Observable<any> {
    const formData = new FormData();
    formData.append('etudiantId', etudiantId.toString());
    formData.append('offreId', offreId.toString());
    formData.append('lettre', lettre);
    formData.append('cv', cvFile, cvFile.name);

    const token = this.getToken();
    return this.http.post<any>(
      `${this.apiUrl}/candidatures/postuler`,
      formData,
      { headers: { Authorization: `Bearer ${token}` } }
    );
  }

  getMesCandidatures(etudiantId: number): Observable<any[]> {
    const token = this.getToken();
    return this.http.get<any[]>(
      `${this.apiUrl}/candidatures/etudiant/${etudiantId}`,
      { headers: { Authorization: `Bearer ${token}` } }
    );
  }

  // Télécharger le CV d'une candidature
  telechargerCV(candidatureId: number): string {
    return `${this.apiUrl}/candidatures/${candidatureId}/cv`;
  }

  private getToken(): string {
    try { return localStorage.getItem('token') || ''; } catch { return ''; }
  }
}
