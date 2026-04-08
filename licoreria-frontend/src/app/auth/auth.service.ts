import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { tap, map, catchError } from 'rxjs/operators';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private apiUrl = 'http://localhost:8081/api/auth/login'; // Cambia la URL según tu backend

  constructor(private http: HttpClient) {}

  // Método para hacer login, retorna un observable booleano
  login(username: string, password: string): Observable<boolean> {
    console.log('Enviando login con usuario:', username);
    return this.http.post(this.apiUrl, { username, password }, { responseType: 'text' }).pipe(
      tap(response => {
        console.log('Token recibido del backend:', response);
        // Guarda el token en localStorage si el login es exitoso
        localStorage.setItem('token', response);
        console.log('Token guardado en localStorage');
      }),
      map(response => {
        console.log('Mapeando response a true');
        return true;
      }),
      catchError(error => {
        console.error('Error en login:', error);
        return of(false);
      }) // Si hay error, retorna false
    );
  }
  // Método para hacer registro, retorna un observable booleano
  register(username: string, password: string): Observable<boolean> {
    const apiUrl = 'http://localhost:8081/api/auth/register'; // Cambia la URL según tu backend
    console.log('Enviando registro con usuario:', username);
    return this.http.post(apiUrl, { username, password }, { responseType: 'text' }).pipe(
      tap(response => {
        console.log('Respuesta del backend en registro:', response);
      }),
      map(response => {
        console.log('Mapeando response a true en registro');
        return true;
      }),
      catchError(error => {
        console.error('Error en registro:', error);
        return of(false);
      }) // Si hay error, retorna false
    );
  }
  // Método para cerrar sesión
  logout() {
    localStorage.removeItem('token');
  }

  // Método para verificar si el usuario está autenticado
  isAuthenticated(): boolean {
    return !!localStorage.getItem('token');
  }
}
