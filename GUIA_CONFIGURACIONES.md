# 📋 Configuración de Usuario - Resumen de Implementación

## ✅ Frontend - Angular

### Componentes Creados:
- **SettingsComponent** - Componente principal para gestionar configuraciones
- **SettingsService** - Servicio que comunica con el backend

### Ubicación de Archivos:
```
licoreria-frontend/src/app/settings/
├── settings.component.ts      (Lógica del componente)
├── settings.component.html    (Interfaz de usuario)
├── settings.component.css     (Estilos)
└── settings.service.ts        (Servicio HTTP)
```

### Características:
✨ Cambiar nombre de usuario
✨ Cambiar contraseña (con validación de contraseña actual)
✨ Validación de formularios
✨ Mensajes de éxito/error
✨ Soporte para modo oscuro
✨ Responsive en mobile

### Ruta de Acceso:
`http://localhost:4200/settings`

---

## ✅ Backend - Java Spring Boot

### Archivos Modificados:
- `UserController.java` - Agregados 3 nuevos endpoints
- `UserService.java` - Agregado método `updatePassword()`

### Archivos Creados:
- `UpdatePasswordDTO.java` - DTO para actualizar contraseña

### Nuevos Endpoints:
```
PUT /api/users/username      - Actualizar nombre de usuario
PUT /api/users/password      - Actualizar contraseña
GET /api/users/profile       - Obtener perfil del usuario actual
```

### Autenticación:
✅ Todos los endpoints están protegidos con autenticación
✅ Requieren un token JWT válido

---

## 🔑 Endpoints del Backend

### 1. Actualizar Nombre de Usuario
```
PUT /api/users/username
Content-Type: application/json
Authorization: Bearer <JWT_TOKEN>

{
  "username": "nuevo_nombre"
}

Response (200 OK):
{
  "id": 1,
  "username": "nuevo_nombre",
  "role": "USER"
}
```

### 2. Actualizar Contraseña
```
PUT /api/users/password
Content-Type: application/json
Authorization: Bearer <JWT_TOKEN>

{
  "oldPassword": "contraseña_actual",
  "newPassword": "nueva_contraseña"
}

Response (200 OK):
{
  "id": 1,
  "username": "usuario",
  "role": "USER"
}
```

### 3. Obtener Perfil
```
GET /api/users/profile
Authorization: Bearer <JWT_TOKEN>

Response (200 OK):
{
  "id": 1,
  "username": "usuario",
  "role": "USER"
}
```

---

## 📱 Interfaz de Usuario

### Menú Lateral:
- La opción "Configuraciones" está disponible en la sección inferior del menú
- Icono: ⚙️ (bx bx-cog)
- Posición: Encima de "Cerrar Sesión"

### Página de Configuraciones:
```
┌─────────────────────────────────┐
│ Configuraciones                 │
│ Gestiona tu perfil y seguridad  │
└─────────────────────────────────┘

┌─ PERFIL DE USUARIO ─────────────┐
│ Nombre de usuario actual: juan  │
│ [Cambiar nombre de usuario]     │
└─────────────────────────────────┘

┌─ SEGURIDAD ─────────────────────┐
│ Cambia tu contraseña...         │
│ [Cambiar contraseña]            │
└─────────────────────────────────┘
```

---

## 🔒 Validaciones

### Frontend:
- ✓ Nombre de usuario: mínimo 3 caracteres
- ✓ Nueva contraseña: mínimo 6 caracteres
- ✓ Las contraseñas deben coincidir
- ✓ Todos los campos son requeridos

### Backend:
- ✓ Validación de JWT token
- ✓ Verificación de contraseña actual (para cambio de contraseña)
- ✓ Verificación de disponibilidad de nuevo nombre de usuario
- ✓ Encriptación bcrypt de contraseñas

---

## ⚙️ Cómo Usar

### 1. Acceder a Configuraciones:
   1. Haz clic en el icono de menú (hamburguer)
   2. Desplázate al final del menú
   3. Haz clic en "Configuraciones"

### 2. Cambiar Nombre de Usuario:
   1. Haz clic en "Cambiar nombre de usuario"
   2. Ingresa el nuevo nombre (mín. 3 caracteres)
   3. Haz clic en "Actualizar"

### 3. Cambiar Contraseña:
   1. Haz clic en "Cambiar contraseña"
   2. Ingresa tu contraseña actual
   3. Ingresa la nueva contraseña (mín. 6 caracteres)
   4. Confirma la nueva contraseña
   5. Haz clic en "Actualizar"

---

## 🚀 Próximas Mejoras (Opcional)

- [ ] Autenticación de dos factores (2FA)
- [ ] Historial de cambios de contraseña
- [ ] Sesiones activas
- [ ] Recuperación de contraseña por correo
- [ ] Validación de contraseña fuerte
- [ ] Auditoría de cambios de seguridad

---

**Última actualización:** 30 de Abril, 2026
