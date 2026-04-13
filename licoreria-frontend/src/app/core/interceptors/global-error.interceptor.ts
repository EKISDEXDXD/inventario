import { Injectable } from '@angular/core';
import {
  HttpInterceptor,
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpErrorResponse,
} from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { ToastrService } from 'ngx-toastr';

@Injectable()
export class GlobalErrorInterceptor implements HttpInterceptor {
  constructor(private toastr: ToastrService) {}

  intercept(
    request: HttpRequest<any>,
    next: HttpHandler
  ): Observable<HttpEvent<any>> {
    return next.handle(request).pipe(
      catchError((error: HttpErrorResponse) => {
        let errorMessage = 'Error desconocido';

        if (error.error instanceof ErrorEvent) {
          // Error del cliente
          errorMessage = error.error.message;
        } else {
          // Error del servidor
          if (error.error?.message) {
            errorMessage = error.error.message;
          } else {
            switch (error.status) {
              case 400:
                errorMessage = 'Solicitud inválida';
                break;
              case 401:
                errorMessage = 'No autenticado. Por favor inicia sesión.';
                break;
              case 403:
                errorMessage = 'No tienes permiso para acceder a este recurso';
                break;
              case 404:
                errorMessage = 'Recurso no encontrado';
                break;
              case 500:
                errorMessage = 'Error interno del servidor';
                break;
              default:
                errorMessage = `Error: ${error.status} ${error.statusText}`;
            }
          }
        }

        this.toastr.error(errorMessage, 'Error');
        return throwError(() => error);
      })
    );
  }
}
