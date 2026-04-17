# 📋 Guía de Implementación - Costos Administrativos

## ✅ Implementación Completada

Se ha implementado exitosamente un nuevo módulo de **Costos Administrativos** en el sistema de inventario de la licorería.

---

## 📦 Cambios Realizados

### Backend (Java/Spring Boot)

#### Archivos Creados:
1. **Modelo**: `AdministrativeCost.java`
   - Campos: id, name, cost, description, store
   - Relación: ManyToOne con Store
   - Anotaciones JPA para persistencia

2. **DTO**: `AdministrativeCostDTO.java`
   - Validaciones de campos obligatorios
   - Validaciones de valores positivos

3. **Repositorio**: `AdministrativeCostRepository.java`
   - Método: `findByStoreId(storeId)` - obtiene costos por tienda

4. **Servicio**: `AdministrativeCostService.java`
   - CRUD completo (Create, Read, Update, Delete)
   - Validación de permisos por usuario
   - Métodos:
     - `findAll()` - obtiene todos los costos
     - `findAllByUsername()` - obtiene costos del usuario
     - `findById()` - obtiene un costo específico
     - `findByStoreId()` - obtiene costos de una tienda
     - `create()` - crea nuevo costo
     - `update()` - actualiza costo existente
     - `delete()` - elimina costo

5. **Controlador**: `AdministrativeCostController.java`
   - Endpoints REST:
     - `GET /api/administrative-costs` - lista todos
     - `GET /api/administrative-costs/{id}` - obtiene uno
     - `GET /api/administrative-costs/store/{storeId}` - lista por tienda
     - `POST /api/administrative-costs` - crea nuevo
     - `PUT /api/administrative-costs/{id}` - actualiza
     - `DELETE /api/administrative-costs/{id}` - elimina

**Ubicación**: `/licoreria-backend/src/main/java/com/inventario/licoreria/modules/administrative_costs/`

---

### Frontend (Angular)

#### Actualizaciones en `inventario.ts`:
- Propiedades nuevas:
  - `administrativeCosts: any[]` - lista de costos
  - `showCreateAdminCostForm: boolean` - mostrar/ocultar formulario
  - `editingAdminCostId: number | null` - ID del costo en edición
  - `loadingAdminCosts: boolean` - estado de carga
  - `newAdminCost = {}` - objeto del formulario
  - `apiAdminCostsUrl` - URL del API

- Métodos nuevos:
  - `loadAdministrativeCosts()` - carga costos de la tienda
  - `toggleCreateAdminCostForm()` - muestra/oculta formulario
  - `startEditAdminCost(cost)` - inicia edición
  - `cancelEditAdminCost()` - cancela edición
  - `saveAdminCost()` - guarda (crea o actualiza)
  - `deleteAdminCost(costId)` - elimina con doble confirmación

#### Actualizaciones en `inventario.html`:
- Nueva sección "Costos Administrativos" al final
- **Lado izquierdo**: Lista de costos con:
  - Nombre, descripción, monto
  - Botones de editar y eliminar
  - Estado de carga
  - Mensaje cuando no hay costos
  
- **Lado derecho**: Formulario dinámico para:
  - Crear nuevo costo
  - Editar costo existente
  - Campos: nombre, costo, descripción (textarea)
  - Botones: Guardar/Actualizar y Cancelar (al editar)

#### Actualizaciones en `inventario.css`:
- Estilos para lista de costos administrativos
- Estilos para items de costo
- Estilos para botones de acción
- Estilos responsive para móvil
- Animaciones y transiciones
- Paleta de colores consistente con el resto de la app

---

## 🎯 Funcionalidades Implementadas

### Crear Costo Administrativo
1. Al lado derecho, rellenar: nombre, monto, descripción
2. Hacer click en "Guardar Costo"
3. El costo aparece inmediatamente en la lista del lado izquierdo

### Listar Costos Administrativos
- Se cargan automáticamente al abrir la página
- Muestran: nombre, descripción, monto
- Ordenados alfabéticamente por nombre
- Scroll si hay muchos costos

### Editar Costo Administrativo
1. Hacer click en el botón "Editar" (lápiz) en un costo
2. El formulario se rellena con los datos del costo
3. El título cambia a "Editar Costo"
4. Cambiar los valores deseados
5. Hacer click en "Actualizar Costo"
6. También hay botón "Cancelar" para descartar cambios

### Eliminar Costo Administrativo
1. Hacer click en el botón "Eliminar" (basura) en un costo
2. Aparece primera confirmación: "¿Estás seguro de eliminar?"
3. Si confirma, aparece segunda confirmación: "¿Desea continuar?"
4. Solo después de ambas confirmaciones se elimina

### Características de Seguridad
- Solo el gerente de la tienda puede ver/editar/eliminar sus costos
- Validación de permisos en el backend
- Los cambios a un costo NO afectan movimientos históricos
- Cada usuario solo ve sus propios costos

---

## 🔌 Endpoints API

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/administrative-costs` | Lista todos los costos del usuario |
| GET | `/api/administrative-costs/{id}` | Obtiene un costo específico |
| GET | `/api/administrative-costs/store/{storeId}` | Lista costos de una tienda |
| POST | `/api/administrative-costs` | Crea nuevo costo |
| PUT | `/api/administrative-costs/{id}` | Actualiza costo existente |
| DELETE | `/api/administrative-costs/{id}` | Elimina un costo |

### Ejemplo de Payload - Crear/Actualizar:
```json
{
  "name": "Alquiler",
  "cost": 500.00,
  "description": "Alquiler del local principal",
  "storeId": 1
}
```

---

## 🗄️ Base de Datos

Se crea automáticamente la tabla `administrative_cost` con:
- `id` (Long, PK, Auto-increment)
- `name` (String, 255)
- `cost` (BigDecimal)
- `description` (String, 500)
- `store_id` (Long, FK → store)

---

## 📝 Próximos Pasos (En Movimientos)

Para completar el flujo de costos administrativos, se debe:

1. **Crear módulo de Movimientos de Costos Administrativos**
   - Tabla similar a transacciones de productos
   - Campos: id, administrative_cost_id, fecha, monto_pagado, tipo (PAGO/AJUSTE)

2. **Agregar UI en Movimientos**
   - Sección aparte para movimientos administrativos
   - Formulario para registrar pagos
   - Tabla con historial de pagos

3. **Lógica en Movimientos**
   - Registrar que se pagó un costo administrativo
   - Mostrar próximos pagos vencidos
   - Alertas de costos próximos a vencer

---

## ✅ Validación

El código ha sido compilado y validado:
- ✅ Backend: `mvn clean compile` - SUCCESS
- ✅ Frontend: `ng build --configuration=development` - SUCCESS
- ✅ Todos los tipos de datos correctos
- ✅ Sin errores de compilación

---

## 🚀 Instrucciones de Uso

### 1. En el Backend
Los archivos están listos para la base de datos:
```bash
mvn clean package
java -jar target/licoreria-0.0.1-SNAPSHOT.jar
```

La tabla se crea automáticamente al iniciar (si usas Hibernate con `update`).

### 2. En el Frontend
Se accede desde:
```
http://localhost:4200/tienda/{id}
```

Y aparece la sección "Costos Administrativos" al final de la página.

### 3. Pruebas en Postman
```
# Crear costo
POST http://localhost:8081/api/administrative-costs
Headers: Authorization: Bearer {token}
Body: {
  "name": "Luz",
  "cost": 200,
  "description": "Factura de energía eléctrica",
  "storeId": 1
}

# Listar costos de tienda
GET http://localhost:8081/api/administrative-costs/store/1
Headers: Authorization: Bearer {token}

# Actualizar
PUT http://localhost:8081/api/administrative-costs/1
Headers: Authorization: Bearer {token}
Body: {
  "name": "Luz (Julio)",
  "cost": 250,
  "description": "Factura actualizada",
  "storeId": 1
}

# Eliminar
DELETE http://localhost:8081/api/administrative-costs/1
Headers: Authorization: Bearer {token}
```

---

## 📊 Características Destacadas

✨ **Lista Reactiva**: Se actualiza en tiempo real
✨ **Edición Inline**: Cambios aparecen inmediatamente
✨ **Doble Confirmación de Eliminación**: Mayor seguridad
✨ **Validaciones Completas**: Tanto en frontend como backend
✨ **Diseño Responsive**: Funciona en móvil, tablet y desktop
✨ **Tema Oscuro**: Compatible con modo oscuro del sistema
✨ **Permisos**: Solo el dueño puede modificar sus costos
✨ **Historial Independiente**: Cambios no afectan movimientos pasados

---

## 💡 Notas Importantes

1. Los costos administrativos son **templates** que definen qué gastos debe pagar la tienda
2. Los **movimientos** serán registros de cuándo se pagó cada costo
3. Cambiar un costo administrativo solo afecta futuros movimientos
4. Cada tienda tiene sus propios costos (no comparten con otras tiendas)
5. Los permisos se validan tanto en frontend como backend (seguridad en capas)

---

**Implementación completada el**: 2026-04-16
**Estado**: ✅ LISTO PARA USAR
