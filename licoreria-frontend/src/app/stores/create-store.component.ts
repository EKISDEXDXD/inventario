import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-create-store',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './create-store.component.html',
  styleUrls: ['./create-store.component.css']
})
export class CreateStoreComponent implements OnInit {
  formData = {
    name: '',
    description: '',
    manager: ''
  };

  users: any[] = [];
  isLoading = false;
  successMessage = '';

  constructor(private router: Router) {}

  ngOnInit() {
    this.loadUsers();
  }

  loadUsers() {
    // TODO: Obtener usuarios del backend con HttpClient
    // Por ahora datos de ejemplo
    this.users = [
      { id: 1, username: 'admin' },
      { id: 2, username: 'manager1' },
      { id: 3, username: 'manager2' }
    ];
  }

  createStore() {
    if (!this.formData.name || !this.formData.description || !this.formData.manager) {
      alert('Por favor completa todos los campos');
      return;
    }

    this.isLoading = true;
    // TODO: Enviar al backend con HttpClient
    console.log('Creando tienda:', this.formData);

    // Simulación de éxito
    setTimeout(() => {
      this.successMessage = '¡Tienda creada exitosamente!';
      this.formData = { name: '', description: '', manager: '' };
      this.isLoading = false;
      
      setTimeout(() => {
        this.router.navigate(['/my-stores']);
      }, 2000);
    }, 1000);
  }

  goBack() {
    this.router.navigate(['/home']);
  }
}
