import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { HttpClient, HttpClientModule, HttpHeaders } from '@angular/common/http';
import { MenuService } from '../core/menu.service';
import { ApiConfigService } from '../auth/api-config.service';

@Component({
  selector: 'app-create-store',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, HttpClientModule],
  templateUrl: './create-store.component.html',
  styleUrls: ['./create-store.component.css']
})
export class CreateStoreComponent implements OnInit {
  storeForm!: FormGroup;
  isLoading = false;
  errorMessage = '';

  get isMenuOpen$() {
    return this.menuService.isMenuOpen$;
  }

  constructor(
    private router: Router,
    private http: HttpClient,
    private fb: FormBuilder,
    private menuService: MenuService,
    private apiConfig: ApiConfigService
  ) {}

  ngOnInit() {
    this.initializeForm();
  }

  toggleMenu() {
    this.menuService.toggleMenu();
  }

  initializeForm() {
    this.storeForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(100)]],
      description: ['', [Validators.required, Validators.minLength(10), Validators.maxLength(500)]],
      address: ['', [Validators.required, Validators.minLength(5), Validators.maxLength(200)]],
      accessPassword: ['', [Validators.required, Validators.minLength(6), Validators.maxLength(50)]]
    });
  }

  get name() {
    return this.storeForm.get('name');
  }

  get description() {
    return this.storeForm.get('description');
  }

  get address() {
    return this.storeForm.get('address');
  }

  get accessPassword() {
    return this.storeForm.get('accessPassword');
  }

  createStore() {
    if (!this.storeForm.valid) {
      this.errorMessage = 'Por favor completa todos los campos correctamente.';
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';

    const token = localStorage.getItem('token');
    const headers = token ? new HttpHeaders({ Authorization: `Bearer ${token}` }) : undefined;

    const apiUrl = this.apiConfig.getApiUrl('/api/stores');
    this.http.post(apiUrl, this.storeForm.value, { headers }).subscribe({
      next: () => {
        this.router.navigate(['/my-stores']);
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
