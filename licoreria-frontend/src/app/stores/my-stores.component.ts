import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

@Component({
  selector: 'app-my-stores',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './my-stores.component.html',
  styleUrls: ['./my-stores.component.css']
})
export class MyStoresComponent implements OnInit {
  stores: any[] = [];

  constructor(private router: Router) {}

  ngOnInit() {
    this.loadStores();
  }

  loadStores() {
    // TODO: Obtener tiendas del backend con HttpClient
    // Por ahora datos de ejemplo
    this.stores = [
      { id: 1, name: 'Tienda Centro', description: 'Ubicada en el centro de la ciudad', manager: 'Admin' },
      { id: 2, name: 'Tienda Sur', description: 'Zona sur de la ciudad', manager: 'Manager1' }
    ];
  }

  goBack() {
    this.router.navigate(['/home']);
  }

  deleteStore(id: number) {
    // TODO: Implementar eliminación en backend
    if (confirm('¿Estás seguro de que quieres eliminar esta tienda?')) {
      this.stores = this.stores.filter(s => s.id !== id);
    }
  }
}
