import { Component, OnInit, inject } from '@angular/core';
import { CommonModule, SlicePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { G7ApiService, AsientoDto, AsientoCreatePayload, AutoDto } from '../../core/g7-api.service';

@Component({
  selector: 'app-asientos',
  standalone: true,
  imports: [CommonModule, FormsModule, SlicePipe],
  templateUrl: './asientos.html',
  styleUrls: ['./asientos.css'],
})
export class Asientos implements OnInit {
  private readonly api = inject(G7ApiService);

  // Propiedades usadas en el HTML
  busqueda = '';
  error: string | null = null;
  successMsg: string | null = null;
  formVisible = false;

  asientos: AsientoDto[] = [];
  autos: AutoDto[] = [];

  fAsiento: AsientoCreatePayload = this.empty();

  ngOnInit(): void { this.cargar(); }

  cargar(): void {
    (this.api as any).getAsientos?.().subscribe({ 
      next: (d: any) => this.asientos = d, 
      error: () => {} 
    });

    (this.api as any).getAutos?.().subscribe({ 
      next: (d: any) => this.autos = d, 
      error: () => {} 
    });
  }

  abrir(): void {
    this.fAsiento = this.empty();
    this.formVisible = true;
    this.error = null;
  }

  cerrar(): void {
    this.formVisible = false;
    this.error = null;
  }

  guardar(): void {
    if (!this.fAsiento.idAuto || !this.fAsiento.numeroAsiento?.trim()) {
      this.error = 'Vehículo y número de asiento son obligatorios.';
      return;
    }

    this.error = null;

    (this.api as any).createAsiento?.(this.fAsiento).subscribe({
      next: () => {
        this.cerrar();
        this.cargar();
        this.flash('✓ Asiento creado');
      },
      error: () => { this.error = 'No se pudo crear el asiento.'; },
    });
  }

  liberar(s: AsientoDto): void {
    (this.api as any).liberarAsiento?.(s.id).subscribe({
      next: () => { this.cargar(); this.flash('✓ Asiento liberado'); },
      error: () => { this.error = 'No se pudo liberar.'; },
    });
  }

  eliminar(s: AsientoDto): void {
    if (!confirm('¿Eliminar este asiento?')) return;
    (this.api as any).deleteAsiento?.(s.id).subscribe({
      next: () => { this.cargar(); this.flash('🗑 Asiento eliminado'); },
      error: () => { this.error = 'No se pudo eliminar.'; },
    });
  }

  filtrados(): AsientoDto[] {
    const q = this.busqueda.toLowerCase();
    return q ? this.asientos.filter(s => s.numeroAsiento.toLowerCase().includes(q)) : this.asientos;
  }

  placaDeAuto(idAuto: string): string {
    return this.autos.find(a => a.id === idAuto)?.placa ?? '—';
  }

  libres(): number     { return this.asientos.filter(s => s.estado === 'libre').length; }
  reservados(): number { return this.asientos.filter(s => s.estado === 'reservado').length; }
  ocupados(): number   { return this.asientos.filter(s => s.estado === 'ocupado').length; }

  private flash(msg: string): void {
    this.successMsg = msg;
    setTimeout(() => (this.successMsg = null), 3000);
  }

  private empty(): AsientoCreatePayload {
    return { idAuto: '', numeroAsiento: '', estado: 'libre' };
  }
}