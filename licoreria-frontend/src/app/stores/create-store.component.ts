import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { HttpClient, HttpClientModule, HttpHeaders } from '@angular/common/http';

@Component({
  selector: 'app-create-store',
  standalone: true,
  imports: [CommonModule, FormsModule, HttpClientModule],
  templateUrl: './create-store.component.html',
  styleUrls: ['./create-store.component.css']
})
export class CreateStoreComponent {
  formData = {
    name: '',
    description: '',
    address: ''
  };

  isLoading = false;
  successMessage = '';
  errorMessage = '';

  constructor(private router: Router, private http: HttpClient) {}

  createStore() {
    if (!this.formData.name || !this.formData.description || !this.formData.address) {
      this.errorMessage = 'Por favor completa todos los campos del formulario.';
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';
    this.successMessage = '';

    const token = localStorage.getItem('token');
    const headers = token ? new HttpHeaders({ Authorization: `Bearer ${token}` }) : undefined;
    const payload = {
      name: this.formData.name.trim(),
      description: this.formData.description.trim(),
      address: this.formData.address.trim()
    };

    this.http.post('http://localhost:8081/api/stores', payload, { headers }).subscribe({
      next: () => {
        this.successMessage = '¡Tienda creada exitosamente!';
        this.formData = { name: '', description: '', address: '' };
        this.isLoading = false;
        setTimeout(() => {
          this.router.navigate(['/my-stores']);
        }, 1000);
      },
      error: (error) => {
        console.error('CreateStoreComponent - Error al crear tienda:', error);
        this.errorMessage = error?.error?.message || 'No fue posible crear la tienda. Revisa los datos e intenta de nuevo.';
        this.isLoading = false;
      }
    });
  }

  goBack() {
    this.router.navigate(['/home']);
  }
}
