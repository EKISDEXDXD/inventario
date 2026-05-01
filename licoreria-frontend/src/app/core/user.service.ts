import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private usernameSubject = new BehaviorSubject<string>('');
  public username$: Observable<string> = this.usernameSubject.asObservable();

  constructor() {
    this.loadUsernameFromToken();
  }

  loadUsernameFromToken(): void {
    const token = localStorage.getItem('token');
    if (token) {
      try {
        const payload = JSON.parse(atob(token.split('.')[1]));
        this.usernameSubject.next(payload.sub || 'Usuario');
      } catch (error) {
        console.error('Error decodificando JWT:', error);
        this.usernameSubject.next('Usuario');
      }
    }
  }

  updateUsername(newUsername: string): void {
    // Actualizar el token en localStorage con el nuevo nombre
    const token = localStorage.getItem('token');
    if (token) {
      try {
        const parts = token.split('.');
        const payload = JSON.parse(atob(parts[1]));
        payload.sub = newUsername;
        
        // Recodificar el payload (nota: esto es solo visual, el servidor valida el token)
        const newPayload = btoa(JSON.stringify(payload));
        const newToken = parts[0] + '.' + newPayload + '.' + parts[2];
        
        localStorage.setItem('token', newToken);
        this.usernameSubject.next(newUsername);
      } catch (error) {
        console.error('Error actualizando JWT:', error);
        this.loadUsernameFromToken();
      }
    }
  }

  getUsername(): Observable<string> {
    return this.username$;
  }

  getCurrentUsername(): string {
    return this.usernameSubject.value;
  }
}
