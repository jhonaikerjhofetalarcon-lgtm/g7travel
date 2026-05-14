import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { G7ApiService, OfertaDto } from '../../core/g7-api.service';

// Angular Material
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatCardModule } from '@angular/material/card';
import { MatDividerModule } from '@angular/material/divider';
import { MatTooltipModule } from '@angular/material/tooltip';

@Component({
  selector: 'app-ofertas',
  standalone: true,
  imports: [
    CommonModule,
    DatePipe,
    MatButtonModule,
    MatIconModule,
    MatSlideToggleModule,
    MatCardModule,
    MatDividerModule,
    MatTooltipModule
  ],
  templateUrl: './ofertas.html',
  styleUrl: './ofertas.css'
})
export class Ofertas implements OnInit {
  private api = inject(G7ApiService);
  
  // Estado reactivo para las ofertas
  ofertas = signal<OfertaDto[]>([]);

  ngOnInit() {
    this.cargarOfertas();
  }

  cargarOfertas() {
    this.api.getOfertas().subscribe({
      next: (data) => {
        this.ofertas.set(data || []);
      },
      error: (err) => {
        console.error('Error al cargar ofertas:', err);
      }
    });
  }

  toggleEstado(oferta: OfertaDto) {
    // Cambiamos el estado localmente para feedback inmediato
    const nuevoEstado = !oferta.estado;
    
    // Llamada al backend para persistir el cambio
    // Asumiendo que tu API tiene un método update o patch
    (this.api as any).updateOferta?.(oferta.id, { estado: nuevoEstado }).subscribe({
      next: () => {
        console.log(`Oferta ${oferta.titulo} actualizada a: ${nuevoEstado}`);
        this.cargarOfertas(); // Recargamos para asegurar sincronización
      },
      error: (err: any) => {
        console.error('Error al actualizar estado:', err);
      }
    });
  }

  eliminarOferta(id: string) {
    if (confirm('¿Estás seguro de que deseas eliminar esta promoción?')) {
      (this.api as any).deleteOferta?.(id).subscribe(() => {
        this.cargarOfertas();
      });
    }
  }
}