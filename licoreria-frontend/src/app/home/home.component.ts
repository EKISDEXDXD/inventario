import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, ActivatedRoute } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { MenuService } from '../core/menu.service';
import { ExternalStoreService } from '../core/external-store.service';
import { ApiConfigService } from '../auth/api-config.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
  username = '';
  showExternalModal = false;
  externalStoreName = '';
  externalPassword = '';
  loadingExternal = false;

  get isMenuOpen$() {
    return this.menuService.isMenuOpen$;
  }

  constructor(private router: Router, private http: HttpClient, private menuService: MenuService, private activatedRoute: ActivatedRoute, private externalStoreService: ExternalStoreService, private apiConfig: ApiConfigService) {
    this.loadUsername();
  }

  ngOnInit() {
    // Escucha cambios en los query params para abrir el modal
    this.activatedRoute.queryParams.subscribe(params => {
      if (params['openExternal'] === 'true') {
        this.openExternalModal();
      }
    });

    // Escucha el evento del servicio para abrir el modal (funciona incluso si ya estás en home)
    this.externalStoreService.openExternalModal$.subscribe(() => {
      this.openExternalModal();
    });
  }

  toggleMenu() {
    this.menuService.toggleMenu();
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

    const apiUrl = this.apiConfig.getApiUrl('/api/stores');
    this.http.post<any>(`${apiUrl}/external-access`, body, { headers }).subscribe({
      next: (store) => {
        console.log('✅ Tienda externa obtenida:', store);
        // Guardar que es acceso externo
        const externalData = {
          id: store.id,
          name: store.name,
          isExternal: true
        };
        console.log('💾 Guardando en sessionStorage:', externalData);
        sessionStorage.setItem('externalStore', JSON.stringify(externalData));
        console.log('✅ SessionStorage guardado. Valor:', sessionStorage.getItem('externalStore'));
        // Navegar a la tienda externa
        this.router.navigate(['/tienda', store.id]);
        this.closeExternalModal();
      },
      error: (err) => {
        console.error('❌ Error accediendo a tienda externa:', err);
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
