import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { G7ApiService, AutoDto } from '../../core/g7-api.service';



@Component({
  selector: 'app-autos',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './autos.html',
  styleUrls: ['./autos.css'],
})
export class Autos implements OnInit {
  private readonly api = inject(G7ApiService);

  busqueda = '';
  error: string | null = null;
  successMsg: string | null = null;
  formVisible = false;
  editingId: string | null = null;

  autos: AutoDto[] = [];
  fAuto: AutoDto = this.empty();

  ngOnInit(): void { this.cargar(); }

  cargar(): void {
    (this.api as any).getAutos?.().subscribe({ next: (d: any) => this.autos = d, error: () => {} });
  }

  abrir(): void {
    this.editingId = null;
    this.fAuto = this.empty();
    this.formVisible = true;
    this.error = null;
  }

  abrirEditar(a: AutoDto): void {
    this.editingId = a.id;
    this.fAuto = { ...a };
    this.formVisible = true;
    this.error = null;
  }

  cerrar(): void { this.formVisible = false; this.editingId = null; this.error = null; }

  guardar(): void {
    if (!this.fAuto.placa?.trim() || !this.fAuto.marca?.trim()) {
      this.error = 'Placa y marca son obligatorios.';
      return;
    }
    this.error = null;

    const call = this.editingId
      ? (this.api as any).updateAuto?.(this.editingId, this.fAuto)
      : (this.api as any).createAuto?.(this.fAuto);

    call?.subscribe?.({
      next: () => { this.cerrar(); this.cargar(); this.flash('✓ Vehículo guardado'); },
      error: () => { this.error = 'No se pudo guardar el vehículo.'; },
    });
  }

  eliminar(a: AutoDto): void {
    if (!confirm(`¿Eliminar el vehículo ${a.placa}?`)) return;
    (this.api as any).deleteAuto?.(a.id).subscribe({
      next: () => { this.cargar(); this.flash('🗑 Vehículo eliminado'); },
      error: () => { this.error = 'No se pudo eliminar.'; },
    });
  }

  filtrados(): AutoDto[] {
    const q = this.busqueda.toLowerCase();
    return q ? this.autos.filter(a =>
      a.placa.toLowerCase().includes(q) ||
      a.marca.toLowerCase().includes(q) ||
      a.modelo.toLowerCase().includes(q)
    ) : this.autos;
  }

  private flash(msg: string): void {
    this.successMsg = msg;
    setTimeout(() => (this.successMsg = null), 3000);
  }

  private empty(): AutoDto {
    return {
      id: '',
      placa: '',
      marca: '',
      modelo: '',
      color: '',
      anioFabrica: new Date().getFullYear(),
      cantidadAsiento: 12,
      tipo: 'minivan',
      conductor: '',
      estado: 'activo'
    };
  }
}