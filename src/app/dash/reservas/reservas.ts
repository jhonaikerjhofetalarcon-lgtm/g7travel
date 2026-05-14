import { Component, OnInit, inject } from '@angular/core';
import { CommonModule,DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { G7ApiService, ReservaDto } from '../../core/g7-api.service';

@Component({
  selector: 'app-reservas',
  standalone: true,
  imports: [CommonModule, FormsModule, DatePipe],
  templateUrl: './reservas.html',
  styleUrls: ['./reservas.css'],
})
export class Reservas implements OnInit {
  private readonly api = inject(G7ApiService);

  busqueda = '';
  error: string | null = null;
  successMsg: string | null = null;

  reservas: ReservaDto[] = [];

  ngOnInit(): void { this.cargar(); }

  cargar(): void {
    this.api.getReservas().subscribe({ next: d => this.reservas = d, error: () => {} });
  }

  eliminar(r: ReservaDto): void {
    if (!confirm(`¿Eliminar reserva de ${r.nombre} ${r.apellido}?`)) return;
    this.api.deleteReserva(r.id).subscribe({
      next: () => { this.cargar(); this.flash('🗑 Reserva eliminada'); },
      error: () => { this.error = 'No se pudo eliminar.'; },
    });
  }

  filtrados(): ReservaDto[] {
    const q = this.busqueda.toLowerCase();
    return q ? this.reservas.filter(r =>
      r.nombre.toLowerCase().includes(q) ||
      r.apellido.toLowerCase().includes(q) ||
      r.destino.toLowerCase().includes(q)
    ) : this.reservas;
  }

  private flash(msg: string): void {
    this.successMsg = msg;
    setTimeout(() => (this.successMsg = null), 3000);
  }
}