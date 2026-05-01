import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '@environments/environment';

@Injectable({
  providedIn: 'root'
})
export class SettingsService {
  private apiUrl = `${environment.apiUrl}/api/users`;

  constructor(private http: HttpClient) {}

  updateUsername(newUsername: string): Observable<any> {
    return this.http.put(`${this.apiUrl}/username`, { username: newUsername });
  }

  updatePassword(oldPassword: string, newPassword: string): Observable<any> {
    return this.http.put(`${this.apiUrl}/password`, { 
      oldPassword, 
      newPassword 
    });
  }

  getCurrentUser(): Observable<any> {
    return this.http.get(`${this.apiUrl}/profile`);
  }
}
