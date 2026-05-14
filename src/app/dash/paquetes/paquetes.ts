import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { G7ApiService, PaqueteDto, OfertaDto } from '../../core/g7-api.service';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';

@Component({
  selector: 'app-paquetes',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatButtonModule, MatIconModule, MatChipsModule],
  templateUrl: './paquetes.html',
  styleUrl: './paquetes.css'
})
export class Paquetes implements OnInit {
  private api = inject(G7ApiService);

  // Estados reactivos
  paquetesRaw = signal<PaqueteDto[]>([]);
  ofertasRaw = signal<OfertaDto[]>([]);

  // Lógica: Une paquetes con ofertas y calcula precio final
  listaPaquetes = computed(() => {
    return this.paquetesRaw().map(p => {
      // Buscamos si este paquete tiene una oferta activa
      const oferta = this.ofertasRaw().find(o => o.paqueteId === p.id && o.estado);
      
      // Calculamos precio con descuento si existe
      const precioFinal = oferta 
        ? p.presio - (p.presio * (oferta.descuento / 100)) 
        : p.presio;
      
      return { ...p, oferta, precioFinal };
    });
  });

  ngOnInit() {
    this.cargarDatos();
  }

  cargarDatos() {
    this.api.getPaquetes().subscribe(data => this.paquetesRaw.set(data));
    this.api.getOfertasActivas().subscribe(data => this.ofertasRaw.set(data));
  }
}