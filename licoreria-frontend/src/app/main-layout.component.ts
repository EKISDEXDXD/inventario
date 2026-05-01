import { Component, HostListener, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule, RouterOutlet } from '@angular/router';
import { AuthService } from './auth/auth.service';
import { MenuService } from './core/menu.service';
import { UserService } from './core/user.service';
import { ExternalStoreService } from './core/external-store.service';
import { HasUnsavedChanges } from './common/without-unsaved-changes-guard.spec';

@Component({
  selector: 'app-main-layout',
  standalone: true,
  imports: [CommonModule, RouterModule, RouterOutlet],
  templateUrl: './main-layout.component.html',
  styleUrls: ['./main-layout.component.css']
})
export class MainLayoutComponent implements HasUnsavedChanges, OnInit {
  username = '';
  isDarkMode = false;
  isMobileView = false;

  get isMenuOpen$() {
    return this.menuService.isMenuOpen$;
  }

  hasUnsavedChanges(): boolean {
    return false;
  }

  constructor(private authService: AuthService, private router: Router, private menuService: MenuService, private userService: UserService, private externalStoreService: ExternalStoreService) {
    console.log('MainLayoutComponent - Inicializando...');
    this.loadUsername();
    this.checkWindowSize();
  }

  ngOnInit() {
    this.menuService.closeMenu();
    // Suscribirse a los cambios de nombre de usuario
    this.userService.getUsername().subscribe((newUsername) => {
      if (newUsername) {
        this.username = newUsername;
      }
    });
  }

  @HostListener('window:resize', ['$event'])
  onResize(event: any) {
    this.checkWindowSize();
  }

  checkWindowSize() {
    this.isMobileView = window.innerWidth <= 480;
  }

  loadUsername() {
    console.log('MainLayoutComponent - Cargando username...');
    const currentUsername = this.userService.getCurrentUsername();
    if (currentUsername) {
      this.username = currentUsername;
    } else {
      // Si no hay username en el servicio, cargar desde el token
      const token = localStorage.getItem('token');
      console.log('MainLayoutComponent - Token encontrado:', token ? 'SÍ' : 'NO');
      if (token) {
        try {
          const payload = JSON.parse(atob(token.split('.')[1]));
          console.log('MainLayoutComponent - JWT payload:', payload);
          this.username = payload.sub || 'Usuario';
        } catch (error) {
          console.error('MainLayoutComponent - Error decodificando JWT:', error);
          this.username = 'Usuario';
        }
      }
    }
  }

  toggleMenu() {
    this.menuService.toggleMenu();
  }

  closeMenuOnMobile() {
    if (this.isMobileView) {
      this.menuService.closeMenu();
    }
  }

  toggleDarkMode() {
    this.isDarkMode = !this.isDarkMode;
  }

  goToHome() {
    this.menuService.closeMenu();
    this.router.navigate(['/home']);
  }

  goToMyStores() {
    this.menuService.closeMenu();
    this.router.navigate(['/my-stores']);
  }

  goToCreateStore() {
    this.menuService.closeMenu();
    this.router.navigate(['/create-store']);
  }

  goToExternalStores() {
    this.menuService.closeMenu();
    // Navega a home
    this.router.navigate(['/home']);
    // Emite evento para abrir el modal (funciona incluso si ya estás en home)
    setTimeout(() => this.externalStoreService.triggerOpenExternalModal(), 100);
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}