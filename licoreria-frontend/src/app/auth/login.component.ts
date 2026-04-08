import { Component, Host, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from './auth.service';
import { Router } from '@angular/router';
import { HasUnsavedChanges } from '../common/without-unsaved-changes-guard.spec';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  username = '';
  password = '';
  error = '';

  @HostListener('window:beforeunload', ['$event'])
  onBeforeUnLoad(e: BeforeUnloadEvent) {}
  constructor(private authService: AuthService, private router: Router) {}

  // Método que se llama al enviar el formulario de login
  login() {
    // Validar que los campos no estén vacíos
    if (!this.username || !this.password) {
      this.error = 'Por favor completa todos los campos';
      return;
    }

    this.authService.login(this.username, this.password).subscribe({
      next: (success) => {
        console.log('Login result:', success);
        console.log('Token en localStorage:', localStorage.getItem('token'));
        if (success) {
          console.log('Navegando al home...');
          this.router.navigate(['/']);
        } else {
          this.error = 'Usuario o contraseña incorrectos';
        }
      },
      error: (err) => {
        console.error('Error en login:', err);
        this.error = 'Error en el servidor';
      }

      
    });
  }

  goToRegister() {
  this.router.navigate(['/register']);
}


}
