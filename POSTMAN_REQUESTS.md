# 📬 Ejemplos de Requests para Postman

Utiliza esta guía para crear requests en Postman basados en la nueva arquitectura de tiendas.

---

## 🔧 Configuración Inicial en Postman

### Variables de Entorno
1. En Postman: **Environments** → **Create New Environment** → `Licorería Dev`
2. Agrega estas variables:

| Variable | Initial Value | Current Value |
|----------|--------------|------|
| `baseUrl` | `http://localhost:8081` | |
| `token` | `` | (se llena al hacer login) |
| `storeId` | `1` | (cambiar según la tienda) |
| `productId` | `1` | (cambiar según el producto) |

3. En cada request, usa `{{variable}}` para referenciar

---

## 📝 Requests por Sección

### 🔐 AUTENTICACIÓN

---

#### ✅ 1. Registro de Usuario

```
POST {{baseUrl}}/api/auth/register

Headers:
Content-Type: application/json

Body (JSON):
{
  "username": "nuevo_admin",
  "password": "Mi_Password_Seguro_123"
}

Expected Response (201):
"Usuario registrado correctamente"
```

---

#### ✅ 2. Login

```
POST {{baseUrl}}/api/auth/login

Headers:
Content-Type: application/json

Body (JSON):
{
  "username": "admin",
  "password": "admin"
}

Expected Response (200):
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTcxMzAxNjg3MCwiZXhwIjoxNzEzMTAzMjcwfQ.GbX...

⭐ IMPORTANTE: Copia el token completo y pégalo en tu variable {{token}} en Postman
ó usa Tests para hacerlo automáticamente:

Tests:
pm.environment.set("token", pm.response.text().replace(/"/g, ""));
```

---

### 🏪 TIENDAS

---

#### ✅ 3. Crear Nueva Tienda

```
POST {{baseUrl}}/api/stores

Headers:
Authorization: Bearer {{token}}
Content-Type: application/json

Body (JSON):
{
  "name": "Licorería Uptown",
  "description": "Sucursal en zona norte de la ciudad",
  "address": "Avenida Principal 450, Apt 10"
}

Expected Response (201):
{
  "id": 2,
  "name": "Licorería Uptown",
  "description": "Sucursal en zona norte de la ciudad",
  "address": "Avenida Principal 450, Apt 10",
  "managerUsername": "admin",
  "managerId": 1
}

⭐ Copia el "id" (ejemplo: 2) para usar como {{storeId}} en otros requests
```

---

#### ✅ 4. Obtener Todas las Tiendas

```
GET {{baseUrl}}/api/stores

Headers:
Authorization: Bearer {{token}}

Expected Response (200):
[
  {
    "id": 1,
    "name": "Licorería Centro",
    "description": "Tienda principal",
    "address": "Calle Principal 123",
    "managerId": 1,
    "managerUsername": "admin"
  },
  {
    "id": 2,
    "name": "Licorería Uptown",
    "description": "Sucursal en zona norte",
    "address": "Avenida Principal 450",
    "managerId": 1,
    "managerUsername": "admin"
  }
]
```

---

#### ✅ 5. Obtener Una Tienda Específica

```
GET {{baseUrl}}/api/stores/{{storeId}}

Headers:
Authorization: Bearer {{token}}

Expected Response (200):
{
  "id": 1,
  "name": "Licorería Centro",
  "description": "Tienda principal",
  "address": "Calle Principal 123",
  "managerId": 1,
  "managerUsername": "admin"
}
```

---

### 📦 PRODUCTOS

---

#### ✅ 6. Crear Producto EN una Tienda Específica

```
POST {{baseUrl}}/api/products

Headers:
Authorization: Bearer {{token}}
Content-Type: application/json

Body (JSON):
{
  "name": "Vodka Absolut",
  "description": "Vodka sueco de 750ml",
  "cost": 20.00,
  "price": 42.00,
  "stock": 25,
  "storeId": {{storeId}}
}

⭐ IMPORTANTE: storeId es OBLIGATORIO (antes no era requerido)

Expected Response (201):
{
  "id": 15,
  "name": "Vodka Absolut",
  "description": "Vodka sueco de 750ml",
  "cost": 20.00,
  "price": 42.00,
  "stock": 25,
  "initialStock": 25,
  "store": {
    "id": 1,
    "name": "Licorería Centro"
  }
}
```

---

#### ✅ 7. Obtener TODOS los Productos (de todas las tiendas)

```
GET {{baseUrl}}/api/products

Headers:
Authorization: Bearer {{token}}

⚠️ Nota: Retorna productos de TODAS las tiendas

Expected Response (200):
[
  {
    "id": 1,
    "name": "Ron Viejo",
    "description": "Ron añejo",
    "cost": 15.00,
    "price": 35.00,
    "stock": 50,
    "store": {
      "id": 1,
      "name": "Licorería Centro"
    }
  },
  {
    "id": 15,
    "name": "Vodka Absolut",
    "description": "Vodka sueco",
    "cost": 20.00,
    "price": 42.00,
    "stock": 25,
    "store": {
      "id": 2,
      "name": "Licorería Uptown"
    }
  }
]
```

---

#### ✅ 8. Obtener Productos de una Tienda ESPECÍFICA ⭐ NUEVO

```
GET {{baseUrl}}/api/products/store/{{storeId}}

Headers:
Authorization: Bearer {{token}}

⭐ Esto es la MAGIA - devuelve SOLO productos de esa tienda

Expected Response (200):
[
  {
    "id": 1,
    "name": "Ron Viejo",
    "description": "Ron añejo",
    "cost": 15.00,
    "price": 35.00,
    "stock": 50,
    "initialStock": 50,
    "store": {
      "id": 1,
      "name": "Licorería Centro"
    }
  },
  {
    "id": 3,
    "name": "Cerveza Corona",
    "description": "Pack de 6",
    "cost": 8.00,
    "price": 14.00,
    "stock": 120,
    "initialStock": 120,
    "store": {
      "id": 1,
      "name": "Licorería Centro"
    }
  }
]
```

---

#### ✅ 9. Obtener Un Producto Específico

```
GET {{baseUrl}}/api/products/{{productId}}

Headers:
Authorization: Bearer {{token}}

Expected Response (200):
{
  "id": 1,
  "name": "Ron Viejo",
  "description": "Ron añejo",
  "cost": 15.00,
  "price": 35.00,
  "stock": 50,
  "store": {
    "id": 1,
    "name": "Licorería Centro"
  }
}
```

---

#### ✅ 10. Actualizar Producto

```
PUT {{baseUrl}}/api/products/{{productId}}

Headers:
Authorization: Bearer {{token}}
Content-Type: application/json

Body (JSON):
{
  "name": "Ron Viejo Premium",
  "description": "Ron añejo importado (actualizado)",
  "cost": 18.00,
  "price": 38.00,
  "stock": 50,
  "storeId": {{storeId}}
}

Expected Response (200):
{
  "id": 1,
  "name": "Ron Viejo Premium",
  "description": "Ron añejo importado (actualizado)",
  "cost": 18.00,
  "price": 38.00,
  "stock": 50,
  "store": {
    "id": 1,
    "name": "Licorería Centro"
  }
}
```

---

#### ✅ 11. Ajustar Stock - AGREGAR Unidades

```
PATCH {{baseUrl}}/api/products/{{productId}}/adjust-stock

Headers:
Authorization: Bearer {{token}}
Content-Type: application/json

Body (JSON):
{
  "delta": 10,
  "transactionType": "ENTRADA",
  "userId": 1
}

Significado:
- delta: 10 = Sumar 10 unidades al stock
- transactionType: "ENTRADA" = Es una entrada de inventario
- userId: 1 = El usuario que registra la transacción

Expected Response (200):
{
  "id": 1,
  "name": "Ron Viejo",
  "stock": 60,
  ...
}
```

---

#### ✅ 12. Ajustar Stock - RESTAR Unidades

```
PATCH {{baseUrl}}/api/products/{{productId}}/adjust-stock

Headers:
Authorization: Bearer {{token}}
Content-Type: application/json

Body (JSON):
{
  "delta": -5,
  "transactionType": "SALIDA",
  "userId": 1
}

Significado:
- delta: -5 = Restar 5 unidades al stock
- transactionType: "SALIDA" = Es una salida de inventario
- userId: 1 = El usuario que registra la transacción

Expected Response (200):
{
  "id": 1,
  "name": "Ron Viejo",
  "stock": 55,
  ...
}

⚠️ Error si stock < |delta|:
{
  "message": "Stock insuficiente para el producto: Ron Viejo"
}
```

---

#### ✅ 13. Eliminar Producto

```
DELETE {{baseUrl}}/api/products/{{productId}}

Headers:
Authorization: Bearer {{token}}

Expected Response (204 No Content):
(Sin cuerpo)
```

---

#### ✅ 14. Buscar Productos

```
GET {{baseUrl}}/api/products/search?query=ron

Headers:
Authorization: Bearer {{token}}

Expected Response (200):
[
  {
    "id": 1,
    "name": "Ron Viejo",
    "description": "Ron añejo",
    ...
  },
  {
    "id": 4,
    "name": "Ron Blanco",
    "description": "Ron blanco 750ml",
    ...
  }
]
```

---

#### ✅ 15. Obtener Sugerencias de Búsqueda

```
GET {{baseUrl}}/api/products/search/suggestions?query=ron

Headers:
Authorization: Bearer {{token}}

Expected Response (200):
[
  {
    "id": 1,
    "name": "Ron Viejo",
    ...
  }
]
```

---

### 📝 TRANSACCIONES

---

#### ✅ 16. Obtener Todas las Transacciones

```
GET {{baseUrl}}/api/transactions

Headers:
Authorization: Bearer {{token}}

Expected Response (200):
[
  {
    "id": 1,
    "productId": 1,
    "type": "ENTRADA",
    "quantity": 50,
    "dateTime": "2024-10-01T10:00:00",
    "userId": 1
  },
  {
    "id": 2,
    "productId": 1,
    "type": "SALIDA",
    "quantity": 5,
    "dateTime": "2024-10-02T14:30:00",
    "userId": 1
  }
]
```

---

#### ✅ 17. Crear Transacción Manual

```
POST {{baseUrl}}/api/transactions

Headers:
Authorization: Bearer {{token}}
Content-Type: application/json

Body (JSON):
{
  "productId": 1,
  "type": "ENTRADA",
  "quantity": 20,
  "dateTime": "2024-10-10T09:00:00",
  "userId": 1
}

Expected Response (201):
{
  "id": 10,
  "productId": 1,
  "type": "ENTRADA",
  "quantity": 20,
  "dateTime": "2024-10-10T09:00:00",
  "userId": 1
}
```

---

#### ✅ 18. Ver Historial de un Producto

```
GET {{baseUrl}}/api/transactions/product/{{productId}}

Headers:
Authorization: Bearer {{token}}

Expected Response (200):
[
  {
    "id": 1,
    "productId": 1,
    "type": "ENTRADA",
    "quantity": 50,
    "dateTime": "2024-10-01T10:00:00",
    "userId": 1
  },
  {
    "id": 2,
    "productId": 1,
    "type": "SALIDA",
    "quantity": 5,
    "dateTime": "2024-10-02T14:30:00",
    "userId": 1
  }
]
```

---

#### ✅ 19. Transacciones en Rango de Fechas

```
GET {{baseUrl}}/api/transactions/range?start=2024-10-01T00:00:00&end=2024-10-31T23:59:59

Headers:
Authorization: Bearer {{token}}

Expected Response (200):
[
  {
    "id": 1,
    "productId": 1,
    "type": "ENTRADA",
    "quantity": 50,
    "dateTime": "2024-10-01T10:00:00",
    "userId": 1
  },
  {...}
]
```

---

### 👥 USUARIOS

---

#### ✅ 20. Obtener Todos los Usuarios

```
GET {{baseUrl}}/api/users

Headers:
Authorization: Bearer {{token}}

Expected Response (200):
[
  {
    "id": 1,
    "username": "admin",
    "role": "ADMIN"
  },
  {
    "id": 2,
    "username": "empleado1",
    "role": "USER"
  }
]
```

---

#### ✅ 21. Obtener Datos del Usuario Autenticado

```
GET {{baseUrl}}/api/users/me

Headers:
Authorization: Bearer {{token}}

Expected Response (200):
{
  "id": 1,
  "username": "admin",
  "role": "ADMIN"
}
```

---

#### ✅ 22. Obtener Un Usuario Específico

```
GET {{baseUrl}}/api/users/{{userId}}

Headers:
Authorization: Bearer {{token}}

Expected Response (200):
{
  "id": 1,
  "username": "admin",
  "role": "ADMIN"
}
```

---

#### ✅ 23. Crear Nuevo Usuario

```
POST {{baseUrl}}/api/users

Headers:
Authorization: Bearer {{token}}
Content-Type: application/json

Body (JSON):
{
  "username": "nuevo_empleado",
  "password": "Password_123",
  "role": "USER"
}

Expected Response (201):
{
  "id": 3,
  "username": "nuevo_empleado",
  "role": "USER"
}
```

---

#### ✅ 24. Actualizar Username Propio

```
PUT {{baseUrl}}/api/users/me

Headers:
Authorization: Bearer {{token}}
Content-Type: application/json

Body (JSON):
{
  "username": "admin_nuevo"
}

Expected Response (200):
{
  "id": 1,
  "username": "admin_nuevo",
  "role": "ADMIN"
}
```

---

### 📊 EXPORTAR

---

#### ✅ 25. Descargar Excel

```
GET {{baseUrl}}/api/export/excel

Headers:
Authorization: Bearer {{token}}

Expected Response (200):
- Descarga: inventario-licoreria.xlsx

Contiene 3 hojas:
1. Usuarios (ID, Username, Role)
2. Productos (ID, Nombre, Descripción, Costo, Precio, Stock)
3. Transacciones (ID, Producto, Tipo, Cantidad, Fecha, Usuario)
```

---

## 🎯 Flujo de Prueba Completo Recomendado

1. **Login** → Obtén token
2. **Crear Tienda** → Obtén storeId
3. **Crear Producto** → Usa el storeId en el body
4. **Obtener Productos por Tienda** → Verifica que sea la tienda correcta
5. **Ajustar Stock** → Agrega y resta unidades
6. **Ver Historial** → Verifica las transacciones

---

## 📋 Checklist para tu Testeo

- [ ] Login exitoso
- [ ] Crear 2+ tiendas
- [ ] Crear productos en tienda 1
- [ ] Crear productos en tienda 2
- [ ] GET /products/store/1 devuelve solo productos de tienda 1
- [ ] GET /products/store/2 devuelve solo productos de tienda 2
- [ ] Ajustar stock suma correctamente
- [ ] Ajustar stock resta correctamente
- [ ] Eliminar producto funciona
- [ ] Export Excel incluye todo

¡Listo para testear! 🚀
