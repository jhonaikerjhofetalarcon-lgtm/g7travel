import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { G7ApiService, UserDto } from '../../core/g7-api.service';

interface User {
  nombre: string; email: string; telefono: string;
  rol: 'admin' | 'conductor'; password: string;
}

@Component({
  selector: 'app-users',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './users.html',
  styleUrls: ['./users.css'],
})
export class Users implements OnInit {
  private readonly api = inject(G7ApiService);

  busqueda   = '';
  error: string | null = null;
  successMsg: string | null = null;
  formVisible = false;
  editingId: string | null = null;
  usuarios: UserDto[] = [];
  fUsuario: User = this.empty();

  ngOnInit(): void { this.cargar(); }

  cargar(): void {
    this.api.getUsers().subscribe({ next: d => this.usuarios = d, error: () => {} });
  }

  abrir(): void {
    this.editingId = null; this.fUsuario = this.empty();
    this.formVisible = true; this.error = null;
  }

  abrirEditar(u: UserDto): void {
    this.editingId = u.id;
    this.fUsuario  = { nombre: u.nombre, email: u.email, telefono: u.telefono, rol: u.rol as any, password: '' };
    this.formVisible = true; this.error = null;
  }

  cerrar(): void { this.formVisible = false; this.editingId = null; this.error = null; }

  guardar(): void {
    const { nombre, email, telefono, rol, password } = this.fUsuario;
    if (!nombre.trim() || !email.trim() || !telefono.trim()) { this.error = 'Nombre, email y teléfono son obligatorios.'; return; }
    if (!this.editingId && !password.trim()) { this.error = 'La contraseña es obligatoria al crear.'; return; }
    if (password.trim() && password.length < 6) { this.error = 'Contraseña mínimo 6 caracteres.'; return; }
    this.error = null;

    if (this.editingId) {
      const body: any = { nombre, email, telefono, rol };
      if (password.trim()) body.password = password;
      this.api.updateUser(this.editingId, body).subscribe({
        next: () => { this.cerrar(); this.cargar(); this.flash('✓ Usuario actualizado'); },
        error: (e: any) => { this.error = e?.error?.message ?? 'Error al actualizar.'; },
      });
    } else {
      this.api.createUser({ nombre, email, telefono, rol, password }).subscribe({
        next: () => { this.cerrar(); this.cargar(); this.flash('✓ Usuario creado'); },
        error: (e: any) => { this.error = e?.error?.message ?? 'Error al crear (¿email duplicado?).'; },
      });
    }
  }

  eliminar(u: UserDto): void {
    if (!confirm(`¿Eliminar a "${u.nombre}"?`)) return;
    this.api.deleteUser(u.id).subscribe({
      next: () => { this.cargar(); this.flash('🗑 Usuario eliminado'); },
      error: () => { this.error = 'No se pudo eliminar.'; },
    });
  }

  filtrados(): UserDto[] {
    const q = this.busqueda.toLowerCase();
    return q ? this.usuarios.filter(u =>
      u.nombre.toLowerCase().includes(q) || u.email.toLowerCase().includes(q)) : this.usuarios;
  }

  totalAdmins():      number { return this.usuarios.filter(u => u.rol === 'admin').length; }
  totalConductores(): number { return this.usuarios.filter(u => u.rol === 'conductor').length; }

  avatarColor(nombre: string): string {
    const p = ['#c0392b','#2980b9','#27ae60','#8e44ad','#d35400','#16a085'];
    return p[nombre?.charCodeAt(0) % p.length] ?? '#888';
  }

  private flash(msg: string): void {
    this.successMsg = msg;
    setTimeout(() => (this.successMsg = null), 3000);
  }

  private empty(): User {
    return { nombre: '', email: '', telefono: '', rol: 'conductor', password: '' };
  }
}