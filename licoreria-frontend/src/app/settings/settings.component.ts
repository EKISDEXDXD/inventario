import { Component, OnInit, NgZone } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { SettingsService } from './settings.service';
import { AuthService } from '../auth/auth.service';
import { UserService } from '../core/user.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-settings',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './settings.component.html',
  styleUrls: ['./settings.component.css']
})
export class SettingsComponent implements OnInit {
  usernameForm!: FormGroup;
  passwordForm!: FormGroup;
  
  currentUsername = '';
  successMessage = '';
  errorMessage = '';
  
  showUsernameForm = false;
  showPasswordForm = false;
  
  loadingUsername = false;
  loadingPassword = false;

  constructor(
    private fb: FormBuilder,
    private settingsService: SettingsService,
    private authService: AuthService,
    private userService: UserService,
    private router: Router,
    private ngZone: NgZone
  ) {
    this.initializeForms();
  }

  ngOnInit() {
    this.loadCurrentUsername();
  }

  initializeForms() {
    this.usernameForm = this.fb.group({
      newUsername: ['', [Validators.required, Validators.minLength(3)]]
    });

    this.passwordForm = this.fb.group({
      oldPassword: ['', [Validators.required]],
      newPassword: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', [Validators.required]]
    }, { validators: this.passwordMatchValidator });
  }

  loadCurrentUsername() {
    const token = localStorage.getItem('token');
    if (token) {
      try {
        const payload = JSON.parse(atob(token.split('.')[1]));
        this.currentUsername = payload.sub || 'Usuario';
      } catch (error) {
        console.error('Error decodificando JWT:', error);
      }
    }
  }

  passwordMatchValidator(group: FormGroup) {
    const password = group.get('newPassword')?.value;
    const confirmPassword = group.get('confirmPassword')?.value;
    
    if (password && confirmPassword && password !== confirmPassword) {
      group.get('confirmPassword')?.setErrors({ passwordMismatch: true });
      return { passwordMismatch: true };
    }
    return null;
  }

  onUpdateUsername() {
    if (this.usernameForm.valid) {
      this.loadingUsername = true;
      this.successMessage = '';
      this.errorMessage = '';

      const newUsername = this.usernameForm.get('newUsername')?.value;

      this.settingsService.updateUsername(newUsername).subscribe({
        next: (response) => {
          this.ngZone.run(() => {
            this.loadingUsername = false;
            this.successMessage = 'Nombre de usuario actualizado correctamente.';
            this.currentUsername = newUsername;
            // Notificar al UserService para actualizar el nombre en el sidebar
            this.userService.updateUsername(newUsername);
            this.usernameForm.reset();
            this.showUsernameForm = false;
            
            setTimeout(() => this.successMessage = '', 5000);
          });
        },
        error: (error) => {
          this.ngZone.run(() => {
            this.loadingUsername = false;
            this.errorMessage = error.error?.message || 'Error al actualizar el nombre de usuario.';
            setTimeout(() => this.errorMessage = '', 5000);
          });
        }
      });
    }
  }

  onUpdatePassword() {
    if (this.passwordForm.valid) {
      this.loadingPassword = true;
      this.successMessage = '';
      this.errorMessage = '';

      const oldPassword = this.passwordForm.get('oldPassword')?.value;
      const newPassword = this.passwordForm.get('newPassword')?.value;

      this.settingsService.updatePassword(oldPassword, newPassword).subscribe({
        next: (response) => {
          this.ngZone.run(() => {
            this.loadingPassword = false;
            this.successMessage = 'Contraseña actualizada correctamente.';
            this.passwordForm.reset();
            this.showPasswordForm = false;
            
            setTimeout(() => this.successMessage = '', 5000);
          });
        },
        error: (error) => {
          this.ngZone.run(() => {
            this.loadingPassword = false;
            this.errorMessage = error.error?.message || 'Error al actualizar la contraseña. Verifica tu contraseña actual.';
            setTimeout(() => this.errorMessage = '', 5000);
          });
        }
      });
    }
  }

  toggleUsernameForm() {
    this.showUsernameForm = !this.showUsernameForm;
    if (!this.showUsernameForm) {
      this.usernameForm.reset();
    }
  }

  togglePasswordForm() {
    this.showPasswordForm = !this.showPasswordForm;
    if (!this.showPasswordForm) {
      this.passwordForm.reset();
    }
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
