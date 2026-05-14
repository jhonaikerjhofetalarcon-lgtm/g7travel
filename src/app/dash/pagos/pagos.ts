import { Component, OnInit, inject } from '@angular/core';
import { CommonModule, DatePipe, DecimalPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { G7ApiService, PagoDto } from '../../core/g7-api.service';

@Component({
  selector: 'app-pagos',
  standalone: true,
  imports: [CommonModule, FormsModule, DatePipe, DecimalPipe],
  templateUrl: './pagos.html',
  styleUrls: ['./pagos.css'],
})
export class Pagos implements OnInit {
  private readonly api = inject(G7ApiService);

  busqueda = '';
  error: string | null = null;
  successMsg: string | null = null;

  pagos: PagoDto[] = [];

  ngOnInit(): void { this.cargar(); }

  cargar(): void {
    this.api.getPagos().subscribe({ 
      next: d => this.pagos = d, 
      error: () => {} 
    });
  }

  confirmar(p: PagoDto): void {
    this.api.confirmarPago(p.id).subscribe({
      next: () => { this.cargar(); this.flash('✓ Pago confirmado'); },
      error: () => { this.error = 'No se pudo confirmar el pago.'; },
    });
  }

  filtrados(): PagoDto[] {
    const q = this.busqueda.toLowerCase();
    return q ? this.pagos.filter(p => 
      (p.referencia?.toLowerCase().includes(q)) || 
      p.estado.toLowerCase().includes(q)
    ) : this.pagos;
  }

  totalMontoPagos(): number {
    return this.pagos.reduce((sum, p) => sum + (p.monto || 0), 0);
  }

  pagosCompletados(): number {
    return this.pagos.filter(p => p.estado === 'completado').length;
  }

  pagosPendientes(): number {
    return this.pagos.filter(p => p.estado === 'pendiente').length;
  }

  private flash(msg: string): void {
    this.successMsg = msg;
    setTimeout(() => (this.successMsg = null), 3000);
  }
}