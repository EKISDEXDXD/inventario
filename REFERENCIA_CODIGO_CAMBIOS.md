# 📝 REFERENCIA RÁPIDA DE CAMBIOS DE CÓDIGO

## 1. BCRYPT - StoreService.java

### Antes
```java
@Service
public class StoreService {
    private final StoreRepository storeRepository;
    private final UserService userService;

    public StoreService(StoreRepository storeRepository, UserService userService) {
        this.storeRepository = storeRepository;
        this.userService = userService;
    }
```

### Después
```java
@Service
public class StoreService {
    private final StoreRepository storeRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public StoreService(StoreRepository storeRepository, UserService userService) {
        this.storeRepository = storeRepository;
        this.userService = userService;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }
```

### Crear Tienda - Antes
```java
Store store = new Store();
store.setName(dto.getName());
store.setDescription(dto.getDescription());
store.setAddress(dto.getAddress());
store.setAccessPassword(dto.getAccessPassword()); // ❌ TEXTO PLANO
store.setManager(manager);
```

### Crear Tienda - Después
```java
Store store = new Store();
store.setName(dto.getName());
store.setDescription(dto.getDescription());
store.setAddress(dto.getAddress());
store.setAccessPassword(passwordEncoder.encode(dto.getAccessPassword())); // ✅ BCRYPT
store.setManager(manager);
```

### Acceso Externo - Antes
```java
public StoreResponseDTO accessExternal(String storeName, String password) {
    Store store = storeRepository.findByName(storeName);
    if (store == null || !password.equals(store.getAccessPassword())) { // ❌ sin encripción
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, 
            "Tienda no encontrada o contraseña incorrecta");
    }
    StoreResponseDTO dto = convertToResponseDTO(store);
    dto.setExternal(true);
    return dto;
}
```

### Acceso Externo - Después
```java
public StoreResponseDTO accessExternal(String storeName, String password) {
    Store store = storeRepository.findByName(storeName);
    if (store == null || !passwordEncoder.matches(password, store.getAccessPassword())) { // ✅ bcrypt
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, 
            "Tienda no encontrada o contraseña incorrecta");
    }
    StoreResponseDTO dto = convertToResponseDTO(store);
    dto.setExternal(true);
    return dto;
}
```

---

## 2. VALIDACIÓN DE PERMISOS - StoreService.java

### Nuevo Método
```java
public void validateUserAccess(Store store, String username) {
    User user = userService.findByUsername(username);
    if (user == null || !store.getManager().getId().equals(user.getId())) {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, 
            "No tienes permiso para acceder a esta tienda");
    }
}
```

### findById - Antes
```java
public StoreResponseDTO findById(Long id) {
    return convertToResponseDTO(findStoreById(id));
}
```

### findById - Después
```java
public StoreResponseDTO findById(Long id, String username) {
    Store store = findStoreById(id);
    validateUserAccess(store, username);
    return convertToResponseDTO(store);
}
```

---

## 3. VALIDACIÓN EN CONTROLLER - StoreController.java

### GET por ID - Antes
```java
@GetMapping("/{id}")
public ResponseEntity<StoreResponseDTO> getById(@PathVariable Long id) {
    return ResponseEntity.ok(storeService.findById(id)); // ❌ Sin validación
}
```

### GET por ID - Después
```java
@GetMapping("/{id}")
public ResponseEntity<StoreResponseDTO> getById(
    @PathVariable Long id, 
    Authentication authentication) {
    return ResponseEntity.ok(storeService.findById(id, authentication.getName())); // ✅ Con validación
}
```

---

## 4. GLOBAL EXCEPTION HANDLER - Backend

### Archivo: GlobalExceptionHandler.java
```java
package com.inventario.licoreria.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatusException(
        ResponseStatusException ex) {
        ErrorResponse error = ErrorResponse.builder()
            .message(ex.getReason())
            .status(ex.getStatusCode().value())
            .timestamp(LocalDateTime.now())
            .error(ex.getStatusCode().toString())
            .build();
        return new ResponseEntity<>(error, ex.getStatusCode());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
        MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ErrorResponse error = ErrorResponse.builder()
            .message("Errores de validación")
            .status(HttpStatus.BAD_REQUEST.value())
            .timestamp(LocalDateTime.now())
            .error("VALIDATION_ERROR")
            .validationErrors(errors)
            .build();
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
        ErrorResponse error = ErrorResponse.builder()
            .message("Error interno del servidor")
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .timestamp(LocalDateTime.now())
            .error("INTERNAL_SERVER_ERROR")
            .build();
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
```

### Archivo: ErrorResponse.java
```java
package com.inventario.licoreria.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponse {
    private String message;
    private int status;
    private String error;
    private LocalDateTime timestamp;
    private Map<String, String> validationErrors;
}
```

---

## 5. ERROR INTERCEPTOR - Frontend (Angular)

### Archivo: global-error.interceptor.ts
```typescript
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
          errorMessage = error.error.message;
        } else {
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
```

---

## 6. APP CONFIG - Registrar Interceptores

### Archivo: app.config.ts - Antes
```typescript
import { ApplicationConfig, provideBrowserGlobalErrorListeners } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';

import { routes } from './app.routes';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideRouter(routes),
    provideHttpClient()
  ]
};
```

### Archivo: app.config.ts - Después
```typescript
import { ApplicationConfig, provideBrowserGlobalErrorListeners } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, HTTP_INTERCEPTORS, withXsrfConfiguration } from '@angular/common/http';
import { provideToastr } from 'ngx-toastr';
import { provideAnimations } from '@angular/platform-browser/animations';

import { routes } from './app.routes';
import { GlobalErrorInterceptor } from './core/interceptors/global-error.interceptor';
import { AuthInterceptor } from './auth/auth.interceptor';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideAnimations(),
    provideToastr(),
    provideRouter(routes),
    provideHttpClient(
      withXsrfConfiguration({
        cookieName: 'XSRF-TOKEN',
        headerName: 'X-XSRF-TOKEN'
      })
    ),
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true },
    { provide: HTTP_INTERCEPTORS, useClass: GlobalErrorInterceptor, multi: true }
  ]
};
```

---

## 7. CREATE STORE - VALIDACIONES REACTIVAS

### Antes (ngModel/FormsModule)
```typescript
export class CreateStoreComponent {
  formData = {
    name: '',
    description: '',
    address: '',
    accessPassword: ''
  };
```

### Después (FormBuilder/ReactiveFormsModule)
```typescript
import { Component, OnInit } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';

export class CreateStoreComponent implements OnInit {
  storeForm!: FormGroup;

  constructor(private fb: FormBuilder, ...) {}

  ngOnInit() {
    this.storeForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(100)]],
      description: ['', [Validators.required, Validators.minLength(10), Validators.maxLength(500)]],
      address: ['', [Validators.required, Validators.minLength(5), Validators.maxLength(200)]],
      accessPassword: ['', [Validators.required, Validators.minLength(6), Validators.maxLength(50)]]
    });
  }

  createStore() {
    if (!this.storeForm.valid) {
      this.errorMessage = 'Por favor completa todos los campos correctamente.';
      return;
    }
    this.http.post('http://localhost:8081/api/stores', this.storeForm.value, { headers }).subscribe(...)
  }
}
```

### Template HTML - Forma de Input (Antes)
```html
<input 
  type="text" 
  id="name" 
  name="name"
  [(ngModel)]="formData.name" 
  placeholder="Ej: Tienda Centro"
  class="form-input"
  required/>
```

### Template HTML - Forma de Input (Después)
```html
<input 
  type="text" 
  id="name" 
  formControlName="name"
  placeholder="Ej: Tienda Centro"
  class="form-input"
  [class.input-error]="name?.invalid && name?.touched"/>

<div class="validation-messages" *ngIf="name?.invalid && name?.touched">
  <small class="error-text" *ngIf="name?.errors?.['required']">
    <i class="bx bx-x"></i> El nombre es requerido
  </small>
  <small class="error-text" *ngIf="name?.errors?.['minlength']">
    <i class="bx bx-x"></i> Mínimo 3 caracteres
  </small>
</div>
```

### Template HTML - Botón (Antes)
```html
<button type="submit" class="submit-btn" [disabled]="isLoading">
  <span *ngIf="!isLoading">Crear Tienda</span>
  <span *ngIf="isLoading" class="loading">Creando...</span>
</button>
```

### Template HTML - Botón (Después)
```html
<button type="submit" class="submit-btn" [disabled]="isLoading || storeForm.invalid">
  <span *ngIf="!isLoading">Crear Tienda</span>
  <span *ngIf="isLoading" class="loading">Creando...</span>
</button>
```

---

## 8. CSS - ESTILOS DE VALIDACIÓN

```css
/* Input con error */
.input-error {
  border-color: #dc2626 !important;
  background-color: #fef2f2 !important;
}

/* Mensajes de validación */
.validation-messages {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
  margin-top: 0.5rem;
}

.error-text {
  font-size: 0.8rem;
  color: #dc2626;
  display: flex;
  align-items: center;
  gap: 0.4rem;
}
```

---

## 📋 Resumen de Cambios

| Componente | Archivo | Tipo | Descripción |
|-----------|---------|------|-----------|
| Backend Security | StoreService.java | MODIFY | DDR Bcrypt Password Encoder |
| Backend Security | StoreService.java | ADD | validateUserAccess() method |
| Backend Security | StoreController.java | MODIFY | Agregrar validacion a GET /{id} |
| Backend Error | GlobalExceptionHandler.java | CREATE | Manejo centralizado de excepciones |
| Backend Error | ErrorResponse.java | CREATE | DTO de respuesta de error |
| Frontend Error | global-error.interceptor.ts | CREATE | Interception HTTP errors |
| Frontend Config | app.config.ts | MODIFY | Registrar interceptors |
| Frontend Form | create-store.component.ts | MODIFY | Validaciones reactivas |
| Frontend Form | create-store.component.html | MODIFY | Mostrar mensajes de error |
| Frontend Form | create-store.component.css | MODIFY | Estilos error states |
| Frontend Deps | package.json | MODIFY | Agregar ngx-toastr, animations |
| Frontend HTML | index.html | MODIFY | Link toastr styles |

---

## 🔗 Importes Necesarios

### Java Backend
```java
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.ResponseStatusException;
import org.springframework.http.HttpStatus;
```

### Angular Frontend
```typescript
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { HttpInterceptor, HTTP_INTERCEPTORS } from '@angular/common/http';
import { ToastrService } from 'ngx-toastr';
import { provideAnimations } from '@angular/platform-browser/animations';
import { provideToastr } from 'ngx-toastr';
```

---

## ✅ Verificación Final

Ejecutar estos comandos para verificar:

```bash
# Backend compile
cd licoreria-backend
mvn clean compile
# SUCCESS

# Frontend build
cd ../licoreria-frontend
npm run build
# SUCCESS - Output location: dist/licoreria-frontend
```

