import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { HttpClient, HttpHeaders } from '@angular/common/http';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent {
  username = '';
  showExternalModal = false;
  externalStoreName = '';
  externalPassword = '';
  loadingExternal = false;
  private apiUrl = 'http://localhost:8081/api/stores';

  constructor(private router: Router, private http: HttpClient) {
    this.loadUsername();
  }

  loadUsername() {
    const token = localStorage.getItem('token');
    if (token) {
      try {
        const payload = JSON.parse(atob(token.split('.')[1]));
        this.username = payload.sub || 'Usuario';
      } catch (error) {
        console.error('Error decodificando JWT:', error);
        this.username = 'Usuario';
      }
    }
  }

  goToCreateStore() {
    this.router.navigate(['/create-store']);
  }

  goToMyStores() {
    this.router.navigate(['/my-stores']);
  }

  openExternalModal() {
    this.showExternalModal = true;
    this.externalStoreName = '';
    this.externalPassword = '';
  }

  closeExternalModal() {
    this.showExternalModal = false;
    this.loadingExternal = false;
  }

  accessExternalStore() {
    if (!this.externalStoreName.trim() || !this.externalPassword.trim()) {
      alert('Por favor, ingresa el nombre de la tienda y la contraseña.');
      return;
    }

    this.loadingExternal = true;
    const token = localStorage.getItem('token');
    if (!token) {
      alert('Sesión expirada. Inicia sesión nuevamente.');
      this.closeExternalModal();
      return;
    }

    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });

    const body = {
      storeName: this.externalStoreName.trim(),
      password: this.externalPassword.trim()
    };

    this.http.post<any>(`${this.apiUrl}/external-access`, body, { headers }).subscribe({
      next: (store) => {
        // Navegar a la tienda externa
        this.router.navigate(['/tienda', store.id]);
        this.closeExternalModal();
      },
      error: (err) => {
        console.error('Error accediendo a tienda externa:', err);
        if (err.status === 404) {
          alert('Tienda no encontrada o contraseña incorrecta.');
        } else if (err.status === 403) {
          alert('No tienes permiso para acceder a esta tienda.');
        } else {
          alert('Error al acceder a la tienda externa. Inténtalo de nuevo.');
        }
        this.loadingExternal = false;
      }
    });
  }
}
