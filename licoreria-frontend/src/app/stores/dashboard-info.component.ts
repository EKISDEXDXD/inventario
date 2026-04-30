import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { MenuService } from '../core/menu.service';
import { ApiConfigService } from '../auth/api-config.service';

@Component({
  selector: 'app-dashboard-info',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard-info.component.html',
  styleUrls: ['./dashboard-info.component.css']
})
export class DashboardInfoComponent implements OnInit {
  storeId = 0;
  store: any = null;
  products: any[] = [];
  loading = true;

  // Métricas básicas
  totalProducts = 0;
  totalStock = 0;
  lowStock = 0;
  totalValue = 0;
  averagePrice = 0;
  lowPercent = 0;
  mediumPercent = 0;
  highPercent = 0;
  
  // Nuevas métricas
  topProducts: any[] = [];
  productsWithZeroStock: any[] = [];
  productsWithLowStock: any[] = [];
  topSoldProducts: any[] = [];
  bottomSoldProducts: any[] = [];
  
  // Control de secciones desplegables
  expandedSections: { [key: string]: boolean } = {
    stockLevels: true,
    topSold: true,
    bottomSold: true
  };

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
    private menuService: MenuService,
    private apiConfig: ApiConfigService
  ) {}

  toggleAppMenu() {
    this.menuService.toggleMenu();
  }

  ngOnInit() {
    this.initializeApiUrls();
    this.route.params.subscribe(params => {
      this.storeId = +params['id'];
      this.loadStoreData();
      this.loadStoreProducts();
    });
  }

  loadStoreData() {
    const token = localStorage.getItem('token');
    if (!token) {
      console.error('No se encontró token en localStorage');
      return;
    }

    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`
    });

    this.http.get<any>(`${this.apiStoresUrl}/${this.storeId}`, { headers }).subscribe({
      next: data => {
        this.store = data;
        this.cdr.detectChanges();
      },
      error: err => {
        console.error('Error al cargar la tienda:', err);
        alert('No se pudo cargar la tienda');
        this.router.navigate(['/my-stores']);
      }
    });
  }

  loadStoreProducts() {
    const token = localStorage.getItem('token');
    if (!token) {
      console.error('No se encontró token en localStorage');
      this.loading = false;
      return;
    }

    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`
    });

    this.http.get<any[]>(`${this.apiProductsUrl}/store/${this.storeId}`, { headers }).subscribe({
      next: data => {
        this.products = data || [];
        this.computeMetrics();
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: err => {
        console.error('Error cargando productos:', err);
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  computeMetrics() {
    this.totalProducts = this.products.length;
    this.totalStock = this.products.reduce((sum, item) => sum + (item.stock || 0), 0);
    this.totalValue = this.products.reduce((sum, item) => sum + ((item.stock || 0) * (item.price || 0)), 0);
    this.averagePrice = this.products.length > 0
      ? this.products.reduce((sum, item) => sum + (item.price || 0), 0) / this.products.length
      : 0;

    this.lowStock = this.products.filter(item => (item.stock || 0) < 10).length;
    const mediumStock = this.products.filter(item => (item.stock || 0) >= 10 && (item.stock || 0) < 30).length;
    const highStock = this.products.filter(item => (item.stock || 0) >= 30).length;
    const total = this.products.length || 1;
    this.lowPercent = Math.round((this.lowStock / total) * 100);
    this.mediumPercent = Math.round((mediumStock / total) * 100);
    this.highPercent = Math.round((highStock / total) * 100);

    // Productos con stock cero
    this.productsWithZeroStock = this.products.filter(item => (item.stock || 0) === 0);

    // Productos con stock bajo (menor a 10)
    this.productsWithLowStock = this.products.filter(item => (item.stock || 0) > 0 && (item.stock || 0) < 10);

    // Top 5 productos con mayor stock
    this.topProducts = [...this.products]
      .sort((a, b) => (b.stock || 0) - (a.stock || 0))
      .slice(0, 5);

    // Top 5 productos MÁS VENDIDOS (menor stock = más vendidos, se acabaron más rápido)
    this.topSoldProducts = [...this.products]
      .sort((a, b) => (a.stock || 0) - (b.stock || 0))
      .slice(0, 5);

    // Top 5 productos MENOS VENDIDOS (mayor stock = menos vendidos, no bajan de stock)
    this.bottomSoldProducts = [...this.products]
      .sort((a, b) => (b.stock || 0) - (a.stock || 0))
      .slice(0, 5);
  }

  toggleSection(section: string) {
    this.expandedSections[section] = !this.expandedSections[section];
  }

  getOutgoingCount(product: any): number {
    if (!product.movements || !Array.isArray(product.movements)) {
      return 0;
    }
    return product.movements.filter((m: any) => m.type === 'outgoing').length;
  }

  goBack() {
    this.router.navigate(['../'], { relativeTo: this.route });
  }
}
