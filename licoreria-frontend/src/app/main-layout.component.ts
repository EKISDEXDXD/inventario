import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule, RouterOutlet } from '@angular/router';
import { AuthService } from './auth/auth.service';
import { HasUnsavedChanges } from './common/without-unsaved-changes-guard.spec';

@Component({
  selector: 'app-main-layout',
  standalone: true,
  imports: [CommonModule, RouterModule, RouterOutlet],
  templateUrl: './main-layout.component.html',
  styleUrls: ['./main-layout.component.css']
})
export class MainLayoutComponent implements HasUnsavedChanges {
  username = '';
  isMenuOpen = false;
  isDarkMode = false;

  hasUnsavedChanges(): boolean {
    return false;
  }

  constructor(private authService: AuthService, private router: Router) {
    console.log('MainLayoutComponent - Inicializando...');
    this.loadUsername();
  }

  loadUsername() {
    console.log('MainLayoutComponent - Cargando username...');
    const token = localStorage.getItem('token');
    console.log('MainLayoutComponent - Token encontrado:', token ? 'SÍ' : 'NO');
    if (token) {
      try {
        const payload = JSON.parse(atob(token.split('.')[1]));
        console.log('MainLayoutComponent - JWT payload:', payload);
        this.username = payload.sub || 'Usuario';
      } catch (error) {
        console.error('MainLayoutComponent - Error decodificando JWT:', error);
        this.username = 'Usuario';
      }
    }
  }

  toggleMenu() {
    this.isMenuOpen = !this.isMenuOpen;
  }

  toggleDarkMode() {
    this.isDarkMode = !this.isDarkMode;
  }

  goToHome() {
    this.isMenuOpen = false;
    this.router.navigate(['/home']);
  }

  goToMyStores() {
    this.isMenuOpen = false;
    this.router.navigate(['/my-stores']);
  }

  goToCreateStore() {
    this.isMenuOpen = false;
    this.router.navigate(['/create-store']);
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}