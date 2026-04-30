import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { MenuService } from '../core/menu.service';
import { ApiConfigService } from '../auth/api-config.service';

@Component({
  selector: 'app-my-stores',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './my-stores.component.html',
  styleUrls: ['./my-stores.component.css']
})
export class MyStoresComponent implements OnInit {
  stores: any[] = [];
  openMenuId: number | null = null;

  get isMenuOpen$() {
    return this.menuService.isMenuOpen$;
  }

  constructor(
    private router: Router, 
    private http: HttpClient,
    private cdr: ChangeDetectorRef,
    private menuService: MenuService,
    private apiConfig: ApiConfigService
  ) {}

  ngOnInit() {
    this.loadStores();
  }

  toggleAppMenu() {
    this.menuService.toggleMenu();
  }

  loadStores() {
    const apiUrl = this.apiConfig.getApiUrl('/api/stores');
    const token = localStorage.getItem('token'); 
    
    if (!token) {
      console.error("No se encontró token en localStorage");
      return;
    }

    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    this.http.get<any[]>(apiUrl, { headers }).subscribe({
      next: (data) => {
        console.log('Tiendas cargadas exitosamente:', data);
        this.stores = data; 
        
        // ¡ESTA ES LA MAGIA! Obligamos a Angular a actualizar la vista inmediatamente
        this.cdr.detectChanges(); 
      },
      error: (err) => {
        console.error('Error cargando tiendas:', err);
        if(err.status === 403 || err.status === 401) {
          alert("No tienes permiso o tu sesión expiró. Inicia sesión nuevamente.");
        }
      }
    });
  }

  goBack() {
    this.router.navigate(['/home']);
  }

  toggleMenu(storeId: number) {
    this.openMenuId = this.openMenuId === storeId ? null : storeId;
  }

  closeMenu() {
    this.openMenuId = null;
  }

  manageStore(storeId: number) {
    this.router.navigate(['/tienda', storeId]);
  }

  // Función para eliminar tienda con doble confirmación
  deleteStore(id: number) {
    const store = this.stores.find(s => s.id === id);
    if (!store) return;

    // Primera confirmación
    const firstConfirm = confirm(`¿Estás seguro de que quieres eliminar la tienda "${store.name}"?`);
    if (!firstConfirm) return;

    // Segunda confirmación
    const secondConfirm = confirm(`⚠ ATENCIÓN ⚠\n\nEsta acción no se puede deshacer.\n\n¿Realmente quieres eliminar permanentemente la tienda "${store.name}" y todos sus datos asociados?`);
    if (!secondConfirm) return;

    // Proceder con la eliminación
    const token = localStorage.getItem('token');
    if (!token) {
      alert('Sesión expirada. Inicia sesión nuevamente.');
      return;
    }

    const headers = new HttpHeaders({ 'Authorization': `Bearer ${token}` });
    const apiUrl = this.apiConfig.getApiUrl('/api/stores');

    this.http.delete(`${apiUrl}/${id}`, { headers }).subscribe({
      next: () => {
        this.stores = this.stores.filter(s => s.id !== id);
        this.cdr.detectChanges();
        alert(`La tienda "${store.name}" ha sido eliminada exitosamente.`);
      },
      error: (error) => {
        console.error('Error al eliminar:', error);
        alert('No se pudo eliminar la tienda. Inténtalo de nuevo.');
      }
    });
  }
}