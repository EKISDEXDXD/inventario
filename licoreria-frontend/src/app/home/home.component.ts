import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent {
  username = '';

  constructor(private router: Router) {
    this.loadUsername();
  }

  loadUsername() {
    const token = localStorage.getItem('token');
    if (token) {
      try {
        const payload = JSON.parse(atob(token.split('.')[1]));
        this.username = payload.sub || 'Usuario';
      } catch (error) {
        console.error('Error decodificando JWT:', error);
        this.username = 'Usuario';
      }
    }
  }

  goToCreateStore() {
    this.router.navigate(['/create-store']);
  }

  goToMyStores() {
    this.router.navigate(['/my-stores']);
  }
}
