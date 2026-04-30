import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MenuService } from '../core/menu.service';
import { ExportModalComponent } from './export-modal.component';
import { ApiConfigService } from '../auth/api-config.service';

@Component({
  selector: 'app-dashboard-tienda',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './dashboard-tienda.component.html',
  styleUrls: ['./dashboard-tienda.component.css']
})
export class DashboardTiendaComponent implements OnInit {
  storeId: number = 0;
  store: any = null;
  products: any[] = [];
  loading: boolean = true;
  activeTab: string = 'inventory'; // Default tab
  
  private apiStoresUrl: string = '';
  private apiProductsUrl: string = '';

  private initializeApiUrls() {
    this.apiStoresUrl = this.apiConfig.getApiUrl('/api/stores');
    this.apiProductsUrl = this.apiConfig.getApiUrl('/api/products');
  }

  get isMenuOpen$() {
    return this.menuService.isMenuOpen$;
  }

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private http: HttpClient,
    private cdr: ChangeDetectorRef,
    private dialog: MatDialog,
    private menuService: MenuService,
    private apiConfig: ApiConfigService
  ) {}

  toggleAppMenu() {
    this.menuService.toggleMenu();
  }

  ngOnInit() {
    this.initializeApiUrls();
    // Capturar el ID de la tienda desde la URL
    this.route.params.subscribe(params => {
      this.storeId = +params['id'];
      this.loadStoreData();
      this.loadStoreProducts();
    });
  }

  loadStoreData() {
    const token = localStorage.getItem('token');
    if (!token) {
      console.error("No se encontró token en localStorage");
      return;
    }

    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    // Verificar si es acceso externo
    const externalStore = sessionStorage.getItem('externalStore');
    console.log('🔍 ExternalStore desde sessionStorage:', externalStore);
    const isExternal = externalStore ? JSON.parse(externalStore).isExternal : false;
    console.log('🔍 ¿Es acceso externo?', isExternal);
    
    // Usar endpoint apropiado
    const endpoint = isExternal 
      ? `${this.apiStoresUrl}/external/${this.storeId}`
      : `${this.apiStoresUrl}/${this.storeId}`;
    console.log('🔍 Endpoint usado:', endpoint);

    this.http.get<any>(endpoint, { headers }).subscribe({
      next: (data) => {
        console.log('✅ Tienda cargada:', data);
        this.store = data;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('❌ Error cargando tienda:', err);
        alert('Error al cargar la tienda');
        this.router.navigate(['/my-stores']);
      }
    });
  }

  loadStoreProducts() {
    const token = localStorage.getItem('token');
    if (!token) {
      console.error("No se encontró token en localStorage");
      return;
    }

    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    // Verificar si es acceso externo
    const externalStore = sessionStorage.getItem('externalStore');
    console.log('🔍 LoadStoreProducts - ExternalStore:', externalStore);
    const isExternal = externalStore ? JSON.parse(externalStore).isExternal : false;
    console.log('🔍 LoadStoreProducts - ¿Es externo?', isExternal);
    
    // Usar endpoint apropiado
    const endpoint = isExternal 
      ? `${this.apiProductsUrl}/store/external/${this.storeId}`
      : `${this.apiProductsUrl}/store/${this.storeId}`;
    console.log('🔍 LoadStoreProducts - Endpoint:', endpoint);
    console.log('🔍 LoadStoreProducts - StoreId:', this.storeId);

    this.http.get<any[]>(endpoint, { headers }).subscribe({
      next: (data) => {
        console.log('✅ Productos cargados:', data);
        this.products = data;
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('❌ Error cargando productos:', err);
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  goBack() {
    this.router.navigate(['/my-stores']);
  }

  navigateTo(section: string) {
    this.router.navigate([section], { relativeTo: this.route });
  }

  adjustStock(productId: number, delta: number) {
    const token = localStorage.getItem('token');
    if (!token) return;

    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });

    const body = {
      delta: delta,
      transactionType: delta > 0 ? 'ENTRADA' : 'SALIDA',
      userId: 1 // Puedes obtener esto del contexto autenticado
    };

    this.http.patch(`${this.apiProductsUrl}/${productId}/adjust-stock`, body, { headers })
      .subscribe({
        next: (data: any) => {
          console.log('Stock ajustado:', data);
          this.loadStoreProducts(); // Recargar productos
          alert('Stock actualizado correctamente');
        },
        error: (err) => {
          console.error('Error al ajustar stock:', err);
          alert('Error al ajustar el stock');
        }
      });
  }

  deleteProduct(productId: number) {
    if (confirm('¿Estás seguro de eliminar este producto?')) {
      const token = localStorage.getItem('token');
      if (!token) return;

      const headers = new HttpHeaders({
        'Authorization': `Bearer ${token}`
      });

      this.http.delete(`${this.apiProductsUrl}/${productId}`, { headers })
        .subscribe({
          next: () => {
            this.products = this.products.filter(p => p.id !== productId);
            this.cdr.detectChanges();
            alert('Producto eliminado correctamente');
          },
          error: (err) => {
            console.error('Error al eliminar producto:', err);
            alert('Error al eliminar el producto');
          }
        });
    }
  }

  exportarReporte() {
    this.dialog.open(ExportModalComponent, {
      width: '900px',
      maxHeight: '90vh',
      data: { storeId: this.storeId },
      disableClose: false
    });
  }
}
