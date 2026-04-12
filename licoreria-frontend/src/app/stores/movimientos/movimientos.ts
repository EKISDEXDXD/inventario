import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-movimientos',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './movimientos.html',
  styleUrl: './movimientos.css'
})
export class MovimientosComponent implements OnInit {
  storeId: number = 0;
  store: any = null;
  products: any[] = [];
  filteredProducts: any[] = [];
  transactions: any[] = [];
  loading: boolean = true;
  searchTerm: string = '';

  // Form fields
  movement = {
    type: 'ENTRADA',
    productId: 0,
    quantity: 0,
    reason: 'VENTA'
  };

  private apiStoresUrl = 'http://localhost:8081/api/stores';
  private apiProductsUrl = 'http://localhost:8081/api/products';
  private apiTransactionsUrl = 'http://localhost:8081/api/transactions';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private http: HttpClient
  ) {}

  ngOnInit() {
    this.route.parent?.params.subscribe(params => {
      this.storeId = +params['id'];
      this.loadStoreData();
      this.loadStoreProducts();
    });
  }

  loadStoreData() {
    const token = localStorage.getItem('token');
    if (!token) return;

    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    this.http.get<any>(`${this.apiStoresUrl}/${this.storeId}`, { headers }).subscribe({
      next: (data) => {
        this.store = data;
      },
      error: (err) => {
        console.error('Error cargando tienda:', err);
      }
    });
  }

  loadStoreProducts() {
    const token = localStorage.getItem('token');
    if (!token) return;

    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    this.http.get<any[]>(`${this.apiProductsUrl}/store/${this.storeId}`, { headers }).subscribe({
      next: (data) => {
        this.products = data;
        this.filteredProducts = data;
        this.loadTransactions();
      },
      error: (err) => {
        console.error('Error cargando productos:', err);
      }
    });
  }

  loadTransactions() {
    const token = localStorage.getItem('token');
    if (!token) return;

    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    // Para obtener transacciones de la tienda, asumimos que hay un endpoint o filtramos por productos de la tienda
    // Por ahora, cargamos todas y filtramos después
    this.http.get<any[]>(`${this.apiTransactionsUrl}`, { headers }).subscribe({
      next: (data) => {
        // Filtrar transacciones de productos de esta tienda
        const productIds = this.products.map(p => p.id);
        this.transactions = data.filter(t => productIds.includes(t.productId)).sort((a, b) => new Date(b.dateTime).getTime() - new Date(a.dateTime).getTime());
        this.loading = false;
      },
      error: (err) => {
        console.error('Error cargando transacciones:', err);
        this.loading = false;
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

  registerMovement() {
    if (this.movement.productId === 0 || this.movement.quantity <= 0) {
      alert('Por favor selecciona un producto y cantidad válida');
      return;
    }

    const token = localStorage.getItem('token');
    if (!token) return;

    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });

    const transactionData = {
      productId: this.movement.productId,
      type: this.movement.type,
      quantity: this.movement.quantity,
      dateTime: new Date().toISOString(),
      userId: 1
    };

    // Actualización optimista: agregar transacción inmediatamente
    const optimisticTransaction = {
      id: Date.now(),
      ...transactionData,
      dateTime: new Date().toISOString()
    };
    this.transactions.unshift(optimisticTransaction); // Agregar al inicio
    const originalTransactions = [...this.transactions];

    // También actualizar stock optimistamente
    const productIndex = this.products.findIndex(p => p.id === this.movement.productId);
    let originalStock = 0;
    if (productIndex !== -1) {
      originalStock = this.products[productIndex].stock;
      this.products[productIndex].stock += this.movement.type === 'ENTRADA' ? this.movement.quantity : -this.movement.quantity;
    }

    this.http.post(`${this.apiTransactionsUrl}`, transactionData, { headers }).subscribe({
      next: (createdTransaction: any) => {
        // Reemplazar la transacción optimista con la real
        const index = this.transactions.findIndex(t => t.id === optimisticTransaction.id);
        if (index !== -1) {
          this.transactions[index] = createdTransaction;
        }
        this.movement = { type: 'ENTRADA', productId: 0, quantity: 0, reason: 'VENTA' };
        alert('Movimiento registrado correctamente');
      },
      error: (err) => {
        console.error('Error registrando movimiento:', err);
        // Revertir cambios optimistas
        this.transactions = originalTransactions;
        if (productIndex !== -1) {
          this.products[productIndex].stock = originalStock;
        }
        alert('Error al registrar el movimiento. Inténtalo de nuevo.');
      }
    });
  }

  adjustStock() {
    const token = localStorage.getItem('token');
    if (!token) return;

    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });

    const delta = this.movement.type === 'ENTRADA' ? this.movement.quantity : -this.movement.quantity;

    const body = {
      delta: delta,
      transactionType: this.movement.type,
      userId: 1
    };

    this.http.patch(`${this.apiProductsUrl}/${this.movement.productId}/adjust-stock`, body, { headers })
      .subscribe({
        next: () => {
          // Recargar productos para actualizar stock
          this.loadStoreProducts();
        },
        error: (err) => {
          console.error('Error ajustando stock:', err);
        }
      });
  }

  goBack() {
    this.router.navigate(['../'], { relativeTo: this.route });
  }

  getProductName(productId: number): string {
    const product = this.products.find(p => p.id === productId);
    return product ? product.name : 'Producto desconocido';
  }

  private get startOfToday(): Date {
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    return today;
  }

  private padDate(value: number): string {
    return String(value).padStart(2, '0');
  }

  get todayTransactions(): any[] {
    return this.transactions.filter(t => {
      const date = new Date(t.dateTime);
      return date >= this.startOfToday;
    });
  }

  get todayCount(): number {
    return this.todayTransactions.length;
  }

  get entradasCount(): number {
    return this.todayTransactions.filter(t => t.type === 'ENTRADA').length;
  }

  get salidasCount(): number {
    return this.todayTransactions.filter(t => t.type === 'SALIDA').length;
  }

  get todayLabel(): string {
    const today = new Date();
    return `${this.padDate(today.getDate())}/${this.padDate(today.getMonth() + 1)}`;
  }
}
