import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { G7ApiService, TourDto } from '../../core/g7-api.service';

interface TourForm {
  nombre: string;
  descripcion: string;
  destinoId: string;
  precioAdulto: number;
  precioNino: number;
  duracionDias: number;
  cuposTotal: number;
  dificultad: string;
  incluye: string;
  noIncluye: string;
  imagenUrl: string;
}

@Component({
  selector: 'app-tours',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './tours.html',
  styleUrls: ['./tours.css'],
})
export class Tours implements OnInit {
  private readonly api = inject(G7ApiService);

  busqueda = '';
  error: string | null = null;
  successMsg: string | null = null;
  formVisible = false;
  editingId: string | null = null;

  tours: TourDto[] = [];
  destinos: any[] = [];

  fTour: TourForm = this.empty();

  ngOnInit(): void { this.cargar(); }

  cargar(): void {
    this.api.getTours().subscribe({ next: d => this.tours = d, error: () => {} });
    this.api.getDestinos().subscribe({ next: d => this.destinos = d, error: () => {} });
  }

  abrir(): void {
    this.editingId = null;
    this.fTour = this.empty();
    this.formVisible = true;
    this.error = null;
  }

  abrirEditar(t: TourDto): void {
    this.editingId = t.id;
    this.fTour = { ...t } as TourForm;
    this.formVisible = true;
    this.error = null;
  }

  cerrar(): void { this.formVisible = false; this.editingId = null; this.error = null; }

  guardar(): void {
    if (!this.fTour.nombre?.trim()) {
      this.error = 'El nombre del tour es obligatorio.';
      return;
    }
    this.error = null;

    const call = this.editingId
      ? this.api.updateTour(this.editingId, this.fTour)
      : this.api.createTour(this.fTour);

    call.subscribe({
      next: () => { this.cerrar(); this.cargar(); this.flash('✓ Tour guardado'); },
      error: () => { this.error = 'No se pudo guardar el tour.'; },
    });
  }

  eliminar(t: TourDto): void {
    if (!confirm(`¿Eliminar el tour "${t.nombre}"?`)) return;
    this.api.deleteTour(t.id).subscribe({
      next: () => { this.cargar(); this.flash('🗑 Tour eliminado'); },
      error: () => { this.error = 'No se pudo eliminar.'; },
    });
  }

  filtrados(): TourDto[] {
    const q = this.busqueda.toLowerCase();
    return q ? this.tours.filter(t => t.nombre.toLowerCase().includes(q)) : this.tours;
  }

  private flash(msg: string): void {
    this.successMsg = msg;
    setTimeout(() => (this.successMsg = null), 3000);
  }

  private empty(): TourForm {
    return {
      nombre: '', descripcion: '', destinoId: '', precioAdulto: 0, precioNino: 0,
      duracionDias: 1, cuposTotal: 10, dificultad: 'fácil', incluye: '', noIncluye: '', imagenUrl: ''
    };
  }
}