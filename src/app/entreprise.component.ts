import { Component, Input, Output, EventEmitter, ViewEncapsulation, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { OffreService } from './offre.service';

@Component({
  selector: 'app-entreprise',
  standalone: true,
  imports: [CommonModule, FormsModule, HttpClientModule],
  templateUrl: './entreprise.component.html',
  styleUrl: './entreprise.component.scss',
  encapsulation: ViewEncapsulation.None
})
export class EntrepriseComponent implements OnInit {

  @Input()  entrepriseId: number | null = null;
  @Output() backToHome  = new EventEmitter<void>();
  @Output() openOffer   = new EventEmitter<any>();

  entreprise: any = null;
  offres: any[]   = [];
  loading = true;

  toastMsg = ''; toastVisible = false;
  private toastTimer: any;

  constructor(private offreService: OffreService) {}

  ngOnInit() {
    if (this.entrepriseId) this.charger();
  }

  charger() {
    this.loading = true;
    this.offreService.getEntrepriseById(this.entrepriseId!).subscribe({
      next: (data) => {
        this.entreprise = data;
        this.offres     = data.offres || [];
        this.loading    = false;
      },
      error: () => { this.loading = false; }
    });
  }

  parseJson(str: string): string[] {
    try { return JSON.parse(str || '[]'); } catch { return []; }
  }

  showToast(msg: string) {
    this.toastMsg = msg; this.toastVisible = true;
    clearTimeout(this.toastTimer);
    this.toastTimer = setTimeout(() => this.toastVisible = false, 3000);
  }
}
