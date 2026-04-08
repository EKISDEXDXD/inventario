import { CommonModule } from "@angular/common";
import { Component } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { AuthService } from "./auth.service";
import { Router } from "@angular/router";
import { error } from "console";
import { HasUnsavedChanges } from "../common/without-unsaved-changes-guard.spec";




@Component({
  selector: 'app-register',
  standalone: true, // Indica que este componente es standalone
  imports: [CommonModule, FormsModule], // Importa módulos necesarios para *ngIf y [(ngModel)]
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent implements HasUnsavedChanges {
  username = '';
  password = '';
  confirmPassword = '';
  error = '';

  hasUnsavedChanges(): boolean {
    return this.username !== '' || this.password !== '' || this.confirmPassword !== '';
  }

  constructor(private authService: AuthService, private router: Router) {}

  // Método que se llama al enviar el formulario de registro
  register() {
    this.error = '';

    if (!this.username || !this.password || !this.confirmPassword) {
      this.error = 'Por favor completa todos los campos';
      return;
    }

    if (this.password !== this.confirmPassword) {
      this.error = 'Las contraseñas no coinciden';
      return;
    }

    this.authService.register(this.username, this.password).subscribe({
      next: (success) => {
        console.log('Register result:', success);
        if (success) {
          console.log('Navegando al login...');
          this.router.navigate(['/login']);
        } else {
          this.error = 'Error en el registro';
        }
      },
      error: (err) => {
        console.error('Error en registro:', err);
        this.error = 'Error en el servidor';
      }
    });
  }
}
