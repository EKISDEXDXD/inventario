import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../auth/auth.service';
import { HasUnsavedChanges } from '../common/without-unsaved-changes-guard.spec';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements HasUnsavedChanges{
  username = '';
  hasUnsavedChanges(): boolean {
    return false;
  }

  constructor(private authService: AuthService, private router: Router) {
    console.log('HomeComponent - Inicializando...');
    this.loadUsername();
  }

  loadUsername() {
    console.log('HomeComponent - Cargando username...');
    const token = localStorage.getItem('token');
    console.log('HomeComponent - Token encontrado:', token ? 'SÍ' : 'NO');
    if (token) {
      // Decodificar el JWT para obtener el username
      try {
        const payload = JSON.parse(atob(token.split('.')[1]));
        console.log('HomeComponent - JWT payload:', payload);
        this.username = payload.sub || 'Usuario';
      } catch (error) {
        console.error('HomeComponent - Error decodificando JWT:', error);
        this.username = 'Usuario';
      }
    }
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
