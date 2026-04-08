import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { AuthService } from './auth.service';

@Injectable({ providedIn: 'root' })
export class AuthGuard implements CanActivate {
  constructor(private authService: AuthService, private router: Router) {}

  // Método que determina si la ruta puede ser activada
  canActivate(): boolean {
    const isAuthenticated = this.authService.isAuthenticated();
    console.log('AuthGuard - ¿Está autenticado?:', isAuthenticated);
    console.log('AuthGuard - Token en localStorage:', localStorage.getItem('token'));
    
    if (isAuthenticated) {
      console.log('AuthGuard - Permitiendo acceso');
      return true; // Permite el acceso si está autenticado
    } else {
      console.log('AuthGuard - Redirigiendo al login');
      this.router.navigate(['/login']); // Redirige al login si no está autenticado
      return false;
    }
  }
}
