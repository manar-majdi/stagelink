import { Pipe, PipeTransform } from '@angular/core';

@Pipe({ name: 'candidatureFilter', standalone: true })
export class CandidatureFilterPipe implements PipeTransform {
  transform(candidatures: any[], statut: string): any[] {
    if (!candidatures) return [];
    return candidatures.filter(c => c.statut === statut);
  }
}
