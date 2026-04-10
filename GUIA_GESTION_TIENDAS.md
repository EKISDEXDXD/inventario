# 📦 Guía de Gestión de Tiendas - Sistema de Inventario Licorería

## 🎯 Arquitectura Implementada

Tu aplicación ahora funciona con **un modelo de arquitectura SaaS (Software as a Service)**, donde cada tienda es un contenedor independiente con su propio inventario.

### Flujo General:

```
1. Usuario inicia sesión
   ↓
2. Va a "Mis Tiendas"
   ↓
3. Selecciona una tienda (hace click)
   ↓
4. Entra al Dashboard de esa tienda (URL: tienda/:id)
   ↓
5. Gestiona el inventario específico de esa tienda
```

---

## 🗄️ Cambios en Base de Datos

### Relación: Product → Store

Ahora **cada producto pertenece a una tienda**:

```sql
ALTER TABLE product ADD COLUMN store_id BIGINT NOT NULL;
ALTER TABLE product ADD CONSTRAINT fk_product_store FOREIGN KEY (store_id) REFERENCES stores(id);
```

**Esto significa:**
- ✅ Productos están asociados a una tienda específica
- ✅ GET `/api/products/store/{storeId}` devuelve SOLO los productos de esa tienda
- ✅ Al crear un producto, debes especificar a qué tienda pertenece

---

## 🌐 Endpoints API (Backend)

### **1. Autenticación**

#### POST `/api/auth/login`
```json
{
  "username": "tu_usuario",
  "password": "tu_contraseña"
}
```
**Respuesta:** Token JWT

---

#### POST `/api/auth/register`
```json
{
  "username": "nuevo_usuario",
  "password": "contraseña_segura"
}
```

---

### **2. Tiendas**

#### GET `/api/stores`
Lista todas las tiendas del usuario autenticado.

#### GET `/api/stores/{id}`
Obtiene una tienda específica.

**Respuesta:**
```json
{
  "id": 1,
  "name": "Licorería Centro",
  "description": "Tienda principal en el centro",
  "address": "Calle Principal 123",
  "managerUsername": "admin"
}
```

#### POST `/api/stores`
Crea una nueva tienda.

**Body:**
```json
{
  "name": "Nueva Licorería",
  "description": "Descripción de la tienda",
  "address": "Dirección de la tienda"
}
```

---

### **3. Productos (ACTUALIZADO)**

#### GET `/api/products`
Lista todos los productos (de TODAS las tiendas).

#### GET `/api/products/{id}`
Obtiene un producto específico.

#### GET `/api/products/store/{storeId}` ⭐ **NUEVO**
Obtiene SOLO los productos de una tienda específica.

**Ejemplo:** `GET /api/products/store/1`

**Respuesta:**
```json
[
  {
    "id": 1,
    "name": "Ron Viejo",
    "description": "Ron añejo importado",
    "cost": 15.00,
    "price": 35.00,
    "stock": 50,
    "store": {
      "id": 1,
      "name": "Licorería Centro"
    }
  }
]
```

#### POST `/api/products` ⭐ **ACTUALIZADO**
Crea un producto en una tienda específica.

**Body (AHORA INCLUYE storeId):**
```json
{
  "name": "Cerveza Importada",
  "description": "Pack de 6 botellas",
  "cost": 8.50,
  "price": 12.00,
  "stock": 100,
  "storeId": 1
}
```

#### PUT `/api/products/{id}` ⭐ **ACTUALIZADO**
Actualiza un producto (incluye storeId).

**Body:**
```json
{
  "name": "Cerveza Importada Premium",
  "description": "Pack de 6 botellas premium",
  "cost": 9.50,
  "price": 15.00,
  "stock": 100,
  "storeId": 1
}
```

#### PATCH `/api/products/{id}/adjust-stock`
Ajusta el stock de un producto (suma/resta unidades).

**Body:**
```json
{
  "delta": 10,
  "transactionType": "ENTRADA",
  "userId": 1
}
```

- `delta`: Número positivo (suma) o negativo (resta)
- `transactionType`: "ENTRADA" o "SALIDA"

**Ejemplo:** Si quieres RESTAR 5 unidades:
```json
{
  "delta": -5,
  "transactionType": "SALIDA",
  "userId": 1
}
```

#### DELETE `/api/products/{id}`
Elimina un producto.

---

### **4. Transacciones de Inventario**

#### GET `/api/transactions`
Lista todas las transacciones de stock.

#### GET `/api/transactions/{id}`
Obtiene una transacción específica.

#### POST `/api/transactions`
Crea una transacción manualmente.

**Body:**
```json
{
  "productId": 1,
  "type": "ENTRADA",
  "quantity": 50,
  "dateTime": "2024-10-01T10:00:00",
  "userId": 1
}
```

#### GET `/api/transactions/product/{productId}`
Obtiene el historial de transacciones de un producto.

#### GET `/api/transactions/range?start=2024-01-01T00:00:00&end=2024-12-31T23:59:59`
Obtiene transacciones en un rango de fechas.

#### DELETE `/api/transactions/{id}`
Elimina una transacción.

---

### **5. Usuarios**

#### GET `/api/users`
Lista todos los usuarios.

#### GET `/api/users/me`
Obtiene el usuario autenticado actualmente.

#### GET `/api/users/{id}`
Obtiene un usuario específico.

#### POST `/api/users`
Crea un nuevo usuario (solo ADMIN).

**Body:**
```json
{
  "username": "nuevo_empleado",
  "password": "contraseña_segura",
  "role": "USER"
}
```

#### PUT `/api/users/me`
Actualiza el username del usuario autenticado.

**Body:**
```json
{
  "username": "nuevo_username"
}
```

---

### **6. Exportar Datos**

#### GET `/api/export/excel`
Descarga un archivo Excel con todos los usuarios, productos y transacciones.

---

## 🎨 Frontend - Rutas Disponibles

| Ruta | Componente | Descripción |
|------|-----------|-----------|
| `/login` | LoginComponent | Inicio de sesión |
| `/register` | RegisterComponent | Registro de usuario |
| `/home` | HomeComponent | Página de inicio (protegida) |
| `/my-stores` | MyStoresComponent | Lista de tiendas del usuario |
| `/create-store` | CreateStoreComponent | Crear nueva tienda |
| `/tienda/:id` | **DashboardTiendaComponent** ⭐ | **Panel de gestión de tienda** |

---

## 💡 Cómo Usar en Postman

### Setup Inicial

1. **Abre Postman** y crea una nueva colección llamada "Licorería API"

2. **Variable de Entorno:**
   - Nombre: `token`
   - Valor: (se llenará dinámicamente)
   - Base URL: `http://localhost:8081`

### Flujo de Prueba Completo

#### 1️⃣ Login
```
POST http://localhost:8081/api/auth/login

Body (JSON):
{
  "username": "admin",
  "password": "admin"
}

Response: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."

Copia ese token y guárdalo en la variable {{token}}
```

#### 2️⃣ Get Tiendas
```
GET http://localhost:8081/api/stores

Headers:
Authorization: Bearer {{token}}
```

#### 3️⃣ Obtén el ID de una tienda (digamos que es 1)

#### 4️⃣ Crear un Producto EN esa Tienda
```
POST http://localhost:8081/api/products

Headers:
Authorization: Bearer {{token}}
Content-Type: application/json

Body:
{
  "name": "Whisky Johnnie Walker",
  "description": "Whisky escocés premium",
  "cost": 25.00,
  "price": 45.00,
  "stock": 30,
  "storeId": 1
}
```

#### 5️⃣ Obtener SOLO los Productos de la Tienda 1
```
GET http://localhost:8081/api/products/store/1

Headers:
Authorization: Bearer {{token}}
```

#### 6️⃣ Ajustar Stock de un Producto
```
PATCH http://localhost:8081/api/products/1/adjust-stock

Headers:
Authorization: Bearer {{token}}
Content-Type: application/json

Body:
{
  "delta": 5,
  "transactionType": "ENTRADA",
  "userId": 1
}
```

---

## 📱 Frontend - Flujo de Usuario

### Dashboard de Tienda (`/tienda/:id`)

El nuevo componente **DashboardTiendaComponent** muestra:

✅ **Información de la Tienda**
- Nombre
- Descripción
- Dirección
- Encargado

✅ **Inventario en Tabla**
| Producto | Descripción | Stock | Costo | Precio | Acciones |
|----------|-------------|-------|-------|--------|----------|
| Ron Viejo | ... | 50 | $15 | $35 | +5, -5, 🗑️ |

✅ **Botones de Acción**
- **+5**: Agrega 5 unidades al stock
- **-5**: Resta 5 unidades al stock
- **🗑️**: Elimina el producto

---

## 🔑 Puntos Clave de la Arquitectura

### 1. **Foreign Key en Base de Datos**
```sql
-- Cada producto sabe a qué tienda pertenece
Product.store_id → Store.id
```

### 2. **Endpoint Filtrado**
```
GET /api/products/store/{id}
Devuelve SOLO los productos WHERE store_id = {id}
```

### 3. **Rutas Dinámicas en Angular**
```typescript
{ path: 'tienda/:id', component: DashboardTiendaComponent }
```

### 4. **Lectura de Parámetros**
```typescript
constructor(private route: ActivatedRoute) {}

ngOnInit() {
  this.route.params.subscribe(params => {
    this.storeId = +params['id'];
    // Cargar datos de esa tienda
  });
}
```

---

## 🚀 Siguientes Pasos

### Mejoras Opcionales:

1. **Crear Productos desde el Dashboard**
   - Agregar formulario para crear productos dentro del dashboard

2. **Editar Productos**
   - Agregar botón para editar detalles del producto

3. **Historial de Transacciones**
   - Mostrar el historial de movimientos de cada producto

4. **Reportes por Tienda**
   - Generar reportes de ventas/inventario por tienda

5. **Multi-usuario**
   - Cada empleado solo ve sus tiendas asignadas

6. **Búsqueda en Inventario**
   - Buscar productos dentro del inventario de la tienda

---

## ⚠️ Validaciones

### Al crear un producto, recuerda:
- ✅ El `storeId` es **OBLIGATORIO**
- ✅ `cost` y `price` deben ser **positivos**
- ✅ `stock` debe ser **≥ 0**
- ✅ `name` y `description` no pueden estar **vacíos**

### Error Si No Incluyes storeId:
```json
{
  "timestamp": "2026-04-10T...",
  "status": 400,
  "error": "El ID de la tienda es obligatorio"
}
```

---

## 📊 Diagrama de Flujo

```
┌─────────────────────────────────────────┐
│      USUARIO INICIA SESIÓN              │
│   POST /api/auth/login → JWT Token      │
└──────────────┬──────────────────────────┘
               │
               ↓
        ┌──────────────┐
        │  MY-STORES   │
        │  GET /stores │
        └──────┬───────┘
               │
        ┌──────┴──────────────────────────┐
        │   Click en una Tienda (ID:1)    │
        └───────────┬──────────────────────┘
                    │
                    ↓
        ┌─────────────────────────┐
        │  DASHBOARD TIENDA (:id) │
        │  Ruta: /tienda/1        │
        └───────────┬─────────────┘
                    │
        ┌───────────┴────────────────┐
        │ Carga Datos:              │
        │ • GET /stores/1           │
        │ • GET /products/store/1   │
        └───────────┬────────────────┘
                    │
                    ↓
        ┌─────────────────────────┐
        │  TABLA INVENTARIO       │
        │  Muestra Productos      │
        │  +5, -5, Eliminar       │
        └─────────────────────────┘
```

---

## 💬 Preguntas Frecuentes

**P: ¿Puedo ver productos de varias tiendas a la vez?**
R: No directamente desde el dashboard. Solo ves una tienda a la vez. Para ver TODAS, llama a `GET /api/products`.

**P: ¿Qué pasa si elimino una tienda?**
R: Los productos asociados también deben eliminarse (Cascade Delete debe estar configurado en la DB).

**P: ¿Puedo transferir un producto de una tienda a otra?**
R: Actualmente no. Tendrías que eliminar y recrear. Mejora futura: endpoint PATCH para actualizar `storeId`.

**P: ¿Los datos están cifrados?**
R: El comunicación está protegida por JWT (token). El token expira en 24 horas.

---

¡Tu aplicación está lista para gestionar múltiples tiendas con inventarios independientes! 🎉
