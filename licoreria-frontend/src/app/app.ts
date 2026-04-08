import { Component, HostListener, signal } from '@angular/core';
import { NavigationStart, Router, RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, CommonModule],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('licoreria-frontend');
  hasUnsavedChanges = false; // Propiedad para rastrear cambios no guardados

  constructor(private router: Router) {
    this.router.events.subscribe(event => {
      if (event instanceof NavigationStart) {
        const isAuthenticated = !!sessionStorage.getItem('token');
        if (isAuthenticated && (event.url === '/login' || event.url === '/register')) {
          const confirmLeave = confirm('¿Estás seguro? Perderás tu sesión actual. ¿Quieres continuar?');
          if (!confirmLeave) {
            this.router.navigate(['/']);
          }
        }
      }
    });
  }

}