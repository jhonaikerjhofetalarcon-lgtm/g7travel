import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { G7ApiService, DestinoDto } from '../../core/g7-api.service';

@Component({
  selector: 'app-destinos',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './destinos.html',
  styleUrls: ['./destinos.css'],
})
export class Destinos implements OnInit {
  private readonly api = inject(G7ApiService);

  busqueda = '';
  error: string | null = null;
  successMsg: string | null = null;
  formVisible = false;
  editingId: string | null = null;

  destinos: DestinoDto[] = [];
  fDestino: any = this.empty();

  ngOnInit(): void { this.cargar(); }

  cargar(): void {
    this.api.getDestinos().subscribe({ next: d => this.destinos = d, error: () => {} });
  }

  abrir(): void {
    this.editingId = null;
    this.fDestino = this.empty();
    this.formVisible = true;
    this.error = null;
  }

  cerrar(): void { this.formVisible = false; this.error = null; }

  // Nota: Si no tienes métodos create/update/delete en el backend, coméntalos o ajusta según necesites
  guardar(): void {
    this.error = 'Funcionalidad de crear/editar destinos pendiente en el backend';
    // this.api.createDestino... (agregar cuando lo implementes)
  }

  eliminar(d: DestinoDto): void {
    if (!confirm(`¿Eliminar destino "${d.label}"?`)) return;

    this.flash('Funcionalidad pendiente');
  }

  filtrados(): DestinoDto[] {
    const q = this.busqueda.toLowerCase();
    return q ? this.destinos.filter(d => 
      d.label.toLowerCase().includes(q) || d.title.toLowerCase().includes(q)
    ) : this.destinos;
  }

  private flash(msg: string): void {
    this.successMsg = msg;
    setTimeout(() => (this.successMsg = null), 3000);
  }

  private empty() {
    return { label: '', title: '', desc: '', name: '', bg: '', thumb: '' };
  }
}