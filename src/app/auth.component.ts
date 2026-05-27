import { Component, Input, Output, EventEmitter, ViewEncapsulation, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { OffreService } from './offre.service';

@Component({
  selector: 'app-auth',
  standalone: true,
  imports: [CommonModule, FormsModule, HttpClientModule],
  templateUrl: './auth.component.html',
  styleUrls: ['./auth.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class AuthComponent implements OnInit {

  @Input() initialMode: 'login' | 'register' = 'login';
  @Output() backToHome = new EventEmitter<void>();

  mode: 'login' | 'register' = 'login';

  loginData    = { email: '', motDePasse: '' };
  registerData = {
    typeCompte: '',
    prenom: '', nom: '', email: '',
    motDePasse: '', confirmMotDePasse: '',
    universite: '', filiere: '', niveau: '',
    nomEntreprise: '', secteur: ''
  };

  loading = false;
  errorMsg = '';
  successMsg = '';
  showPassword = false;
  showConfirmPassword = false;

  constructor(private offreService: OffreService) {}

  ngOnInit() { this.mode = this.initialMode; }

  goBack() { this.backToHome.emit(); }

  switchMode(m: 'login' | 'register') {
    this.mode = m;
    this.errorMsg = '';
    this.successMsg = '';
  }

  togglePassword()        { this.showPassword = !this.showPassword; }
  toggleConfirmPassword() { this.showConfirmPassword = !this.showConfirmPassword; }

  onLogin() {
    this.errorMsg = '';
    if (!this.loginData.email || !this.loginData.motDePasse) {
      this.errorMsg = 'Veuillez remplir tous les champs.';
      return;
    }
    this.loading = true;
    this.offreService.login(this.loginData.email, this.loginData.motDePasse).subscribe({
      next: (res) => {
        this.loading = false;
        try {
          localStorage.setItem('token', res.token);
          localStorage.setItem('user', JSON.stringify({
            prenom: res.prenom, nom: res.nom,
            email: res.email, role: res.role
          }));
        } catch (e) {}
        this.successMsg = `🎉 Bienvenue ${res.prenom} !`;
        setTimeout(() => this.backToHome.emit(), 1200);
      },
      error: (err) => {
        this.loading = false;
        this.errorMsg = err.error || 'Email ou mot de passe incorrect.';
      }
    });
  }

  onRegister() {
    this.errorMsg = '';
    if (!this.registerData.typeCompte) {
      this.errorMsg = 'Veuillez sélectionner votre type de compte.';
      return;
    }
    if (this.registerData.typeCompte === 'etudiant') {
      if (!this.registerData.prenom || !this.registerData.nom || !this.registerData.email ||
          !this.registerData.motDePasse) {
        this.errorMsg = 'Veuillez remplir tous les champs étudiants obligatoires.';
        return;
      }
    } else if (this.registerData.typeCompte === 'entreprise') {
      if (!this.registerData.nomEntreprise || !this.registerData.email || !this.registerData.motDePasse) {
        this.errorMsg = 'Veuillez remplir tous les champs entreprise obligatoires.';
        return;
      }
    }

    if (this.registerData.motDePasse !== this.registerData.confirmMotDePasse) {
      this.errorMsg = 'Les mots de passe ne correspondent pas.';
      return;
    }
    if (this.registerData.motDePasse.length < 6) {
      this.errorMsg = 'Le mot de passe doit avoir au moins 6 caractères.';
      return;
    }

    this.loading = true;
    this.offreService.register(this.registerData).subscribe({
      next: (res) => {
        this.loading = false;
        try {
          localStorage.setItem('token', res.token);
          localStorage.setItem('user', JSON.stringify({
            prenom: res.prenom, nom: res.nom,
            email: res.email, role: res.role
          }));
        } catch (e) {}
        this.successMsg = `🎉 Compte créé ! Bienvenue ${res.prenom || res.nomEntreprise} !`;
        setTimeout(() => this.backToHome.emit(), 1200);
      },
      error: (err) => {
        this.loading = false;
        this.errorMsg = err.error || 'Une erreur est survenue.';
      }
    });
  }
}
