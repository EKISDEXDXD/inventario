import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class ApiConfigService {
  private apiBaseUrl: string;

  constructor() {
    this.apiBaseUrl = this.getApiBaseUrl();
  }

  private getApiBaseUrl(): string {
    const currentUrl = window.location.hostname;
    const currentPort = window.location.port;
    const isNgrok = currentUrl.includes('ngrok');
    const isLocalhost = currentUrl === 'localhost' || currentUrl === '127.0.0.1';

    if (isNgrok) {
      // Si accedemos a través de ngrok, usar la misma URL base para el backend
      return `${window.location.protocol}//${window.location.host}`;
    } else if (isLocalhost) {
      // Si es localhost, usar el puerto 8081 para el backend
      return `http://localhost:8081`;
    } else {
      // Para otros casos (IP pública, dominio, etc.), usar el puerto 8081
      return `http://${currentUrl}:8081`;
    }
  }

  getApiUrl(endpoint: string): string {
    return `${this.apiBaseUrl}${endpoint}`;
  }

  getBaseUrl(): string {
    return this.apiBaseUrl;
  }
}
