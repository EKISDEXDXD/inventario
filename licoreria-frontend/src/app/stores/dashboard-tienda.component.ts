import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { FormsModule } from '@angular/forms';

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
  
  private apiStoresUrl = 'http://localhost:8081/api/stores';
  private apiProductsUrl = 'http://localhost:8081/api/products';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private http: HttpClient,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
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

    this.http.get<any>(`${this.apiStoresUrl}/${this.storeId}`, { headers }).subscribe({
      next: (data) => {
        console.log('Tienda cargada:', data);
        this.store = data;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error cargando tienda:', err);
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

    this.http.get<any[]>(`${this.apiProductsUrl}/store/${this.storeId}`, { headers }).subscribe({
      next: (data) => {
        console.log('Productos de la tienda:', data);
        this.products = data;
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error cargando productos:', err);
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  goBack() {
    this.router.navigate(['/my-stores']);
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
}
