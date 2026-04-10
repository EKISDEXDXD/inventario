# ⚡ Quick Start - Gestión de Tiendas

## 🏃 Comenzar en 5 minutos

### 1. Inicia Backend
```bash
cd licoreria-backend
mvn spring-boot:run
```
✅ Espera hasta ver: `Application started on http://localhost:8081`

### 2. Inicia Frontend (nueva terminal)
```bash
cd licoreria-frontend
ng serve
```
✅ Espera hasta ver: `Application bundle generation complete`

### 3. Abre la App
```
http://localhost:4200
```

### 4. Login
- Usuario: `admin`
- Contraseña: `admin`

---

## 🎮 Prueba Rápida

### En la Aplicación

1. **Página: Mis Tiendas** (después del login)
   - Ves las tiendas creadas
   - **CLICK en cualquier tienda** ← Esto es lo nuevo
   - Entra a `/tienda/:id`

2. **Página: Dashboard Tienda** ← NUEVA
   - Ver inventario de esa tienda
   - Tabla con productos
   - Botones: **+5** | **-5** | **🗑️**

3. **Prueba botones:**
   - Click **+5** → Stock sube 5
   - Click **-5** → Stock baja 5 (si hay)
   - Click **🗑️** → Confirma y elimina

4. **← Volver** → Regresa a "Mis Tiendas"

---

## 📬 Prueba en Postman

### Obtener Token
```
POST http://localhost:8081/api/auth/login
Body: { "username": "admin", "password": "admin" }

Copia el token de la respuesta
```

### ⭐ El Endpoint Nuevo (Lo más importante)
```
GET http://localhost:8081/api/products/store/1

Headers:
Authorization: Bearer <token_que_copiaste>

Respuesta: SOLO productos de tienda 1 ✅
```

### Crear Producto (AHORA con storeId)
```
POST http://localhost:8081/api/products

Body:
{
  "name": "Cerveza Test",
  "description": "De prueba",
  "cost": 5.00,
  "price": 8.00,
  "stock": 50,
  "storeId": 1
}

⭐ storeId es OBLIGATORIO (nuevo)
```

---

## 📋 Checklist de Verificación

- [ ] Frontend + Backend iniciados sin errores
- [ ] Login funciona
- [ ] "Mis Tiendas" muestra tiendas
- [ ] **Puedo hacer CLICK en una tienda** (antes no era clickeable)
- [ ] Entra a `/tienda/:id` (URL muestra el ID)
- [ ] Tabla de productos se carga
- [ ] Botón +5 suma stock
- [ ] Botón -5 resta stock (con validación)
- [ ] Botón 🗑️ elimina producto
- [ ] Volver regresa a "Mis Tiendas"

---

## 🐛 Si algo no funciona

### Error: "stock_id column not found"
**Solución:** Reinicia la aplicación (Spring crea la columna automáticamente)

### No veo cambios en Frontend
**Solución:** 
```bash
# Limpia caché
ng build --configuration=development --poll=2000
# O simplemente refresca F5 en el navegador
```

### Error 401 en requests
**Solución:** Login nuevamente, el token expiró

### Botones de +5/-5 no funcionan
**Solución:** Abre DevTools (F12) → Console → busca errores

---

## 📚 Documentación Completa

Encontrarás en el proyecto:
- `GUIA_GESTION_TIENDAS.md` - Todo detallado
- `POSTMAN_REQUESTS.md` - 25+ ejemplos
- `RESUMEN_CAMBIOS.md` - Qué cambió
- `MOCKUPS_VISUALES.md` - Cómo se ve

---

## 🎯 Lo Más Importante (Resumen)

**Antes:**
```
My-Stores (lista) → Home
```

**Ahora:**
```
My-Stores (lista, clickeable)
    ↓ Click tienda
Dashboard (/tienda/:id)
    ├─ GET /stores/1
    ├─ GET /products/store/1 ⭐ NUEVO
    └─ Panel gestión
```

---

¡Listo! Testea y disfruta 🚀
