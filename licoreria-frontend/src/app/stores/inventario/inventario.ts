import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { ChangeDetectorRef } from '@angular/core';
import { ApiConfigService } from '../../auth/api-config.service';

@Component({
  selector: 'app-inventario',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './inventario.html',
  styleUrl: './inventario.css'
})
export class InventarioComponent implements OnInit {
  storeId: number = 0;
  store: any = null;
  products: any[] = [];
  filteredProducts: any[] = [];
  loading: boolean = true;
  searchTerm: string = '';
  showCreateForm: boolean = false;

  // Administrative Costs properties
  administrativeCosts: any[] = [];
  showCreateAdminCostForm: boolean = false;
  editingAdminCostId: number | null = null;
  loadingAdminCosts: boolean = false;

  newAdminCost = {
    name: '',
    cost: 0,
    description: ''
  };

  lowStockThreshold = 50;
  normalStockThreshold = 50;
  showThresholdConfig = false;
  thresholdForm = {
    lowStockThreshold: 50,
    normalStockThreshold: 50
  };

  // Collapsible state variables
  showProductsList: boolean = this.loadCollapsibleState('showProductsList', true);
  showAdminCostsList: boolean = this.loadCollapsibleState('showAdminCostsList', true);

  // Form fields
  newProduct = {
    name: '',
    description: '',
    cost: 0,
    price: 0,
    stock: 0
  };

  private apiStoresUrl: string = '';
  private apiProductsUrl: string = '';
  private apiAdminCostsUrl: string = '';

  private initializeApiUrls() {
    this.apiStoresUrl = this.apiConfig.getApiUrl('/api/stores');
    this.apiProductsUrl = this.apiConfig.getApiUrl('/api/products');
    this.apiAdminCostsUrl = this.apiConfig.getApiUrl('/api/administrative-costs');
  }

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private http: HttpClient,
    private cdr: ChangeDetectorRef,
    private apiConfig: ApiConfigService
  ) {}

  ngOnInit() {
    this.initializeApiUrls();
    console.log('InventarioComponent ngOnInit - iniciando');
    this.route.params.subscribe(params => {
      console.log('InventarioComponent route.params:', params);
      const id = +params['id'];
      if (id && id !== this.storeId) {
        console.log('Cargando inventario para storeId:', id);
        this.storeId = id;
        this.loadStoreData();
        this.loadStoreProducts();
        this.loadAdministrativeCosts();
      } else if (!this.storeId) {
        console.log('storeId no definido, intentando obtener de snapshot');
        const snapshotId = this.getStoreIdFromRoute(this.route);
        if (snapshotId) {
          console.log('storeId desde snapshot:', snapshotId);
          this.storeId = snapshotId;
          this.loadStoreData();
          this.loadStoreProducts();
          this.loadAdministrativeCosts();
        }
      }
    });
  }

  private tryLoadStoreData() {
    const initialStoreId = this.getStoreIdFromRoute(this.route);
    if (initialStoreId) {
      this.storeId = initialStoreId;
      this.loadStoreData();
      this.loadStoreProducts();
      this.loadAdministrativeCosts();
    }
  }

  private watchStoreIdChanges() {
    let current: ActivatedRoute | null = this.route;
    while (current) {
      current.params.subscribe(params => {
        const nextStoreId = +params['id'];
        if (nextStoreId && nextStoreId !== this.storeId) {
          this.storeId = nextStoreId;
          this.loadStoreData();
          this.loadStoreProducts();
          this.loadAdministrativeCosts();
        }
      });
      current = current.parent;
    }
  }

  private getStoreIdFromRoute(route: ActivatedRoute): number {
    let current: ActivatedRoute | null = route;
    while (current) {
      const id = current.snapshot.params['id'];
      if (id) {
        return +id;
      }
      current = current.parent;
    }
    return 0;
  }

  loadStoreData() {
    const token = localStorage.getItem('token');
    console.log('loadStoreData - token:', !!token);
    if (!token) return;

    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    console.log('loadStoreData - haciendo GET a:', `${this.apiStoresUrl}/${this.storeId}`);
    this.http.get<any>(`${this.apiStoresUrl}/${this.storeId}`, { headers }).subscribe({
      next: (data) => {
        console.log('loadStoreData - SUCCESS:', data);
        this.store = data;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('loadStoreData - ERROR:', err);
      }
    });
  }

  loadStoreProducts() {
    const token = localStorage.getItem('token');
    console.log('loadStoreProducts - token:', !!token);
    if (!token) return;

    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    console.log('loadStoreProducts - haciendo GET a:', `${this.apiProductsUrl}/store/${this.storeId}`);
    this.http.get<any[]>(`${this.apiProductsUrl}/store/${this.storeId}`, { headers }).subscribe({
      next: (data) => {
        console.log('loadStoreProducts - SUCCESS, cantidad de productos:', data?.length);
        this.products = data;
        this.filteredProducts = data;
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('loadStoreProducts - ERROR:', err);
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  onSearch() {
    if (this.searchTerm.trim() === '') {
      this.filteredProducts = this.products;
    } else {
      this.filteredProducts = this.products.filter(product =>
        product.name.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        product.description.toLowerCase().includes(this.searchTerm.toLowerCase())
      );
    }
  }

  toggleCreateForm() {
    this.showCreateForm = !this.showCreateForm;
  }

  createProduct() {
    if (!this.newProduct.name || !this.newProduct.description || this.newProduct.cost <= 0 || this.newProduct.price <= 0) {
      alert('Por favor completa todos los campos correctamente');
      return;
    }

    const token = localStorage.getItem('token');
    if (!token) return;

    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });

    const productData = {
      ...this.newProduct,
      storeId: this.storeId
    };

    // Actualización optimista: agregar inmediatamente a la lista
    const optimisticProduct = {
      id: Date.now(), // ID temporal
      ...productData,
      store: this.store
    };
    this.products.unshift(optimisticProduct); // Agregar al inicio
    this.filteredProducts = [...this.products]; // Actualizar filtered
    this.showCreateForm = false; // Ocultar formulario
    const originalProducts = [...this.products]; // Backup por si falla

    this.http.post(`${this.apiProductsUrl}`, productData, { headers }).subscribe({
      next: (createdProduct: any) => {
        // Reemplazar el producto optimista con el real
        const index = this.products.findIndex(p => p.id === optimisticProduct.id);
        if (index !== -1) {
          this.products[index] = createdProduct;
          this.filteredProducts = [...this.products];
        }
        this.newProduct = { name: '', description: '', cost: 0, price: 0, stock: 0 };
        alert('Producto creado correctamente');
      },
      error: (err) => {
        console.error('Error creando producto:', err);
        // Revertir cambios optimistas
        this.products = originalProducts;
        this.filteredProducts = [...this.products];
        this.showCreateForm = true; // Mostrar formulario de nuevo
        alert('Error al crear el producto. Inténtalo de nuevo.');
      }
    });
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
      userId: 1
    };

    this.http.patch(`${this.apiProductsUrl}/${productId}/adjust-stock`, body, { headers })
      .subscribe({
        next: () => {
          this.loadStoreProducts();
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
            this.loadStoreProducts();
            alert('Producto eliminado correctamente');
          },
          error: (err) => {
            console.error('Error al eliminar producto:', err);
            alert('Error al eliminar el producto');
          }
        });
    }
  }

  goBack() {
    this.router.navigate(['../'], { relativeTo: this.route });
  }

  toggleThresholdConfig() {
    this.showThresholdConfig = !this.showThresholdConfig;
    if (this.showThresholdConfig) {
      this.thresholdForm = {
        lowStockThreshold: this.lowStockThreshold,
        normalStockThreshold: this.normalStockThreshold
      };
    }
  }

  saveThresholdConfig() {
    if (this.thresholdForm.lowStockThreshold <= 0 || this.thresholdForm.normalStockThreshold <= 0) {
      alert('Los umbrales deben ser mayores que cero.');
      return;
    }
    if (this.thresholdForm.lowStockThreshold >= this.thresholdForm.normalStockThreshold) {
      alert('El nivel de stock bajo debe ser menor al nivel de stock normal.');
      return;
    }

    this.lowStockThreshold = this.thresholdForm.lowStockThreshold;
    this.normalStockThreshold = this.thresholdForm.normalStockThreshold;
    this.showThresholdConfig = false;
  }

  toggleProductsList() {
    this.showProductsList = !this.showProductsList;
    this.saveCollapsibleState('showProductsList', this.showProductsList);
  }

  toggleAdminCostsList() {
    this.showAdminCostsList = !this.showAdminCostsList;
    this.saveCollapsibleState('showAdminCostsList', this.showAdminCostsList);
  }

  private loadCollapsibleState(key: string, defaultValue: boolean): boolean {
    const saved = localStorage.getItem(`inventario_${key}`);
    return saved !== null ? JSON.parse(saved) : defaultValue;
  }

  private saveCollapsibleState(key: string, value: boolean): void {
    localStorage.setItem(`inventario_${key}`, JSON.stringify(value));
  }

  get totalProducts() {
    return this.products.length;
  }

  getLowStockProducts() {
    return this.products.filter(p => p.stock > 0 && p.stock < this.lowStockThreshold);
  }

  getOutOfStockProducts() {
    return this.products.filter(p => p.stock === 0);
  }

  getNormalStockProducts() {
    return this.products.filter(p => p.stock > this.normalStockThreshold);
  }

  // Administrative Costs Methods
  loadAdministrativeCosts() {
    const token = localStorage.getItem('token');
    console.log('loadAdministrativeCosts - token:', !!token);
    if (!token) return;

    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    this.loadingAdminCosts = true;
    console.log('loadAdministrativeCosts - haciendo GET a:', `${this.apiAdminCostsUrl}/store/${this.storeId}`);
    this.http.get<any[]>(`${this.apiAdminCostsUrl}/store/${this.storeId}`, { headers }).subscribe({
      next: (data) => {
        console.log('loadAdministrativeCosts - SUCCESS, cantidad de costos:', data?.length);
        this.administrativeCosts = data;
        this.loadingAdminCosts = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('loadAdministrativeCosts - ERROR:', err);
        this.loadingAdminCosts = false;
        this.cdr.detectChanges();
      }
    });
  }

  toggleCreateAdminCostForm() {
    this.showCreateAdminCostForm = !this.showCreateAdminCostForm;
    this.editingAdminCostId = null;
    if (!this.showCreateAdminCostForm) {
      this.newAdminCost = { name: '', cost: 0, description: '' };
    }
  }

  startEditAdminCost(cost: any) {
    this.editingAdminCostId = cost.id;
    this.newAdminCost = {
      name: cost.name,
      cost: cost.cost,
      description: cost.description
    };
    this.showCreateAdminCostForm = true;
  }

  cancelEditAdminCost() {
    this.editingAdminCostId = null;
    this.newAdminCost = { name: '', cost: 0, description: '' };
    this.showCreateAdminCostForm = false;
  }

  saveAdminCost() {
    if (!this.newAdminCost.name || !this.newAdminCost.description || this.newAdminCost.cost <= 0) {
      alert('Por favor completa todos los campos correctamente');
      return;
    }

    const token = localStorage.getItem('token');
    if (!token) return;

    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });

    const adminCostData = {
      ...this.newAdminCost,
      storeId: this.storeId
    };

    console.log('saveAdminCost -> adminCostData:', adminCostData, 'editingAdminCostId:', this.editingAdminCostId);

    if (this.editingAdminCostId) {
      // Update existing cost
      this.http.put(`${this.apiAdminCostsUrl}/${this.editingAdminCostId}`, adminCostData, { headers }).subscribe({
        next: (updatedCost: any) => {
          console.log('updateAdminCost -> updatedCost:', updatedCost);
          this.administrativeCosts = this.administrativeCosts.map(cost =>
            cost.id === this.editingAdminCostId ? updatedCost : cost
          );
          this.cancelEditAdminCost();
          this.showAdminCostsList = true;
          this.cdr.detectChanges();
          alert('Costo administrativo actualizado correctamente');
        },
        error: (err) => {
          console.error('Error actualizando costo administrativo:', err);
          const message = err?.error?.message || 'Error al actualizar el costo administrativo';
          alert(message);
        }
      });
    } else {
      // Create new cost
      this.http.post(`${this.apiAdminCostsUrl}`, adminCostData, { headers }).subscribe({
        next: (createdCost: any) => {
          console.log('createAdminCost -> createdCost:', createdCost);
          this.administrativeCosts = [createdCost, ...this.administrativeCosts];
          this.newAdminCost = { name: '', cost: 0, description: '' };
          this.showCreateAdminCostForm = false;
          this.showAdminCostsList = true;
          this.cdr.detectChanges();
          alert('Costo administrativo creado correctamente');
        },
        error: (err) => {
          console.error('Error creando costo administrativo:', err);
          const message = err?.error?.message || 'Error al crear el costo administrativo';
          alert(message);
        }
      });
    }
  }

  deleteAdminCost(costId: number) {
    if (confirm('¿Estás seguro de eliminar este costo administrativo? Esta acción no se puede deshacer.')) {
      const confirmDelete = confirm('Esta es su última advertencia. ¿Desea continuar con la eliminación?');
      if (!confirmDelete) return;

      const token = localStorage.getItem('token');
      if (!token) return;

      const headers = new HttpHeaders({
        'Authorization': `Bearer ${token}`
      });

      this.http.delete(`${this.apiAdminCostsUrl}/${costId}`, { headers }).subscribe({
        next: () => {
          this.loadAdministrativeCosts();
          alert('Costo administrativo eliminado correctamente');
        },
        error: (err) => {
          console.error('Error al eliminar costo administrativo:', err);
          alert('Error al eliminar el costo administrativo');
        }
      });
    }
  }
}
