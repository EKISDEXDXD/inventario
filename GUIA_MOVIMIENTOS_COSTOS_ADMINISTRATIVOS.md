# 📋 Implementación - Movimientos de Costos Administrativos

## ✅ Implementación Completada

Se ha implementado exitosamente el módulo de **Movimientos de Costos Administrativos** que permite registrar los pagos de los gastos operativos de la tienda.

---

## 📦 Cambios Realizados

### Backend (Java/Spring Boot)

#### Archivos Creados:
1. **Modelo**: `AdministrativeCostMovement.java`
   - Campos: id, administrativeCost (FK), type, amountPaid, dateTime, user (FK)
   - Relaciones: ManyToOne con AdministrativeCost y User
   - Tabla: `administrative_cost_movement`

2. **DTO**: `AdministrativeCostMovementDTO.java`
   - Validaciones de campos obligatorios
   - Validaciones de montos positivos

3. **Repositorio**: `AdministrativeCostMovementRepository.java`
   - Métodos de búsqueda:
     - `findByCostId(costId)` - movimientos de un costo
     - `findByStoreId(storeId)` - movimientos de una tienda
   - Ordenados por fecha descencente

4. **Servicio**: `AdministrativeCostMovementService.java`
   - CRUD completo con validación de permisos
   - Solo el gerente de la tienda puede ver/editar/eliminar
   - Métodos:
     - `findAll()` / `findAllByUsername()` / `findById()`
     - `findByStoreId()` / `findByCostId()`
     - `create()` / `update()` / `delete()`

5. **Controlador**: `AdministrativeCostMovementController.java`
   - 6 Endpoints REST:
     - `GET /api/administrative-cost-movements` - lista todos
     - `GET /api/administrative-cost-movements/{id}` - obtiene uno
     - `GET /api/administrative-cost-movements/store/{storeId}` - lista por tienda
     - `GET /api/administrative-cost-movements/cost/{costId}` - lista por costo
     - `POST /api/administrative-cost-movements` - crea nuevo
     - `PUT /api/administrative-cost-movements/{id}` - actualiza
     - `DELETE /api/administrative-cost-movements/{id}` - elimina

**Ubicación**: `/licoreria-backend/src/main/java/com/inventario/licoreria/modules/administrative_costs/`

---

### Frontend (Angular)

#### Actualizaciones en `movimientos.ts`:
- Propiedades nuevas:
  - `administrativeCosts: any[]` - lista de costos disponibles
  - `adminCostMovements: any[]` - lista de movimientos/pagos
  - `showAdminCostMovementForm: boolean` - mostrar/ocultar formulario
  - `adminCostMovement = {}` - objeto del formulario
  - `userName: string` - nombre del usuario actual
  - `apiAdminCostsUrl` y `apiAdminCostMovementsUrl` - URLs del API

- Métodos nuevos:
  - `loadAdministrativeCosts()` - carga costos de la tienda
  - `loadAdministrativeCostMovements()` - carga movimientos/pagos
  - `toggleAdminCostMovementForm()` - muestra/oculta formulario
  - `createAdminCostMovement()` - registra nuevo movimiento
  - `deleteAdminCostMovement(movementId)` - elimina movimiento
  - `getAdminCostName(costId)` - obtiene nombre del costo

#### Actualizaciones en `movimientos.html`:
- Nueva sección "Movimientos de Costos Administrativos" al final
- **Lado izquierdo**: Lista de movimientos/pagos con:
  - Nombre del costo administrativo
  - Tipo de movimiento (badge con color)
  - Fecha y hora del movimiento
  - Monto pagado y usuario que hizo el pago
  - Botón para eliminar movimiento
  - Estado de carga y mensaje cuando no hay movimientos
  
- **Lado derecho**: Formulario dinámico para:
  - Crear nuevo movimiento/pago
  - Selector de costo administrativo (con monto mostrado)
  - Selector de tipo de movimiento (PAGO, AJUSTE, DEVOLUCIÓN)
  - Campo de monto pagado (puede ser parcial o completo)
  - Selector de fecha/hora del pago
  - Botones: Cancelar y Registrar Pago

#### Actualizaciones en `movimientos.css`:
- Estilos para lista de movimientos administrativos
- Estilos para ítems de movimiento con diseño en tarjeta
- Estilos para badges de tipo de movimiento con colores:
  - Verde (PAGO)
  - Naranja (AJUSTE)
  - Azul (DEVOLUCIÓN)
- Estilos responsive para móvil
- Animaciones y transiciones suaves

---

## 🎯 Funcionalidades Implementadas

### Registrar Pago de Costo Administrativo
1. En la sección de movimientos, seleccionar "Nuevo Pago"
2. Elegir el costo administrativo (lista dinámica con montos)
3. Seleccionar tipo de movimiento (PAGO, AJUSTE, DEVOLUCIÓN)
4. Ingresar monto pagado (puede ser parcial)
5. Seleccionar fecha y hora del pago
6. Hacer click en "Registrar Pago"
7. El movimiento aparece inmediatamente en la lista del lado izquierdo

### Listar Movimientos Administrativos
- Se cargan automáticamente al abrir la página
- Ordenados por fecha descendente (más recientes primero)
- Muestran:
  - Nombre del costo asociado
  - Tipo de movimiento con badge colorido
  - Fecha y hora exacta del pago
  - Monto pagado y usuario que lo registró
- Scroll automático si hay muchos movimientos

### Eliminar Movimiento Administrativo
1. Hacer click en el botón "Eliminar" (basura) en un movimiento
2. Aparece confirmación
3. Si confirma, se elimina el movimiento
4. La lista se actualiza automáticamente

### Características de Seguridad
- Solo el gerente de la tienda puede ver movimientos de sus tiendas
- Validación de permisos en backend
- El usuario se extrae automáticamente del JWT token
- Los movimientos se pueden eliminar pero no modificar (crear nuevo o eliminar)

---

## 🔌 Endpoints API

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/administrative-cost-movements` | Lista todos los movimientos del usuario |
| GET | `/api/administrative-cost-movements/{id}` | Obtiene un movimiento específico |
| GET | `/api/administrative-cost-movements/store/{storeId}` | Lista movimientos de una tienda |
| GET | `/api/administrative-cost-movements/cost/{costId}` | Lista movimientos de un costo |
| POST | `/api/administrative-cost-movements` | Crea nuevo movimiento |
| PUT | `/api/administrative-cost-movements/{id}` | Actualiza movimiento existente |
| DELETE | `/api/administrative-cost-movements/{id}` | Elimina un movimiento |

### Ejemplo de Payload - Crear Movimiento:
```json
{
  "administrativeCostId": 1,
  "type": "PAGO",
  "amountPaid": 500.00,
  "dateTime": "2026-04-16T10:30:00",
  "userId": 1
}
```

---

## 🗄️ Base de Datos

Se crea automáticamente la tabla `administrative_cost_movement` con:
- `id` (Long, PK, Auto-increment)
- `administrative_cost_id` (Long, FK → administrative_cost)
- `type` (String) - PAGO, AJUSTE, DEVOLUCIÓN
- `amount_paid` (BigDecimal)
- `date_time` (LocalDateTime)
- `user_id` (Long, FK → user)

---

## 📝 Integraciones Implementadas

✅ **Cargar automáticamente** costos administrativos al abrir movimientos
✅ **Cargar automáticamente** movimientos al abrir la página
✅ **Extrae username** del JWT token automáticamente
✅ **Validaciones completas** en frontend y backend
✅ **Mensajes de error** amigables al usuario
✅ **Estados de carga** visuales
✅ **Lista dinámica** de costos en el selector
✅ **Formato de fecha** consistente con el resto del sistema
✅ **Badges coloridos** para tipos de movimiento

---

## ✅ Validación

El código ha sido compilado y validado:
- ✅ Backend: `mvn clean compile` - **52 archivos** compilados sin errores
- ✅ Frontend: `ng build --configuration=development` - **2.77 MB** sin errores
- ✅ Todos los tipos de datos correctos
- ✅ Relaciones de FK correctas
- ✅ Sin errores de Angular/TypeScript

---

## 🚀 Cómo Funciona

### Flujo Completo

1. **Usuario abre la sección de Movimientos**
   - Se cargan automáticamente: costos administrativos y movimientos anteriores

2. **Usuario hace click en "Nuevo Pago"**
   - Se muestra el formulario de registro

3. **Usuario llena el formulario**
   - Selecciona costo administrativo (ej: "Alquiler - $500")
   - Selecciona tipo (ej: "PAGO")
   - Ingresa monto pagado (ej: "500" - puede ser parcial)
   - Selecciona fecha/hora (ej: "16/04/2026 10:30")
   - El usuario se toma automáticamente del token

4. **Usuario hace click en "Registrar Pago"**
   - Frontend valida que todos los campos estén completos
   - Se envía al backend vía POST
   - Backend valida permisos y datos
   - Se guarda en BD
   - El movimiento aparece en la lista

5. **Lista se actualiza automáticamente**
   - Nuevo pago aparece al inicio
   - Ordenado por fecha descendente
   - Muestra todos los detalles

---

## 💡 Ejemplo de Uso Real

```
Usuario: Koda
Tienda: Licorería Centro

Acción: Registrar pago de alquiler
- Costo Administrativo: Alquiler ($500)
- Tipo de Movimiento: PAGO
- Monto Pagado: $500
- Fecha: 16/04/2026 10:30
- Usuario: Koda (automático del token)

Resultado:
- Movimiento creado en BD
- Aparece en lista de movimientos administrativos
- Fecha: 16/04/2026 10:30
- Monto: $500
- Usuario: Koda
- Tipo: PAGO (badge verde)
```

---

## 🔄 Diferencia con Costos Administrativos

| Aspecto | Costos Administrativos | Movimientos de Costos |
|--------|----------------------|----------------------|
| **Propósito** | Definir qué gastos tiene la tienda | Registrar que se pagó un gasto |
| **Ubicación** | Sección Inventario > Costos Administrativos | Sección Movimientos |
| **Datos** | Nombre, monto fijo, descripción | Costo vinculado, monto pagado, fecha, usuario |
| **CRUD** | Create, Read, Update, Delete | Create, Read, Delete (no update) |
| **Cardinalidad** | Menos items (costos configurados) | Más items (histórico de pagos) |
| **Ejemplo** | "Alquiler - $500" | "Pago de Alquiler - $500 - 16/04/2026" |

---

## 📊 Relación de Tablas

```
┌─────────────────────────────┐
│    administrative_cost      │
├──────────────┬──────────────┤
│ id (PK)      │              │
│ name         │              │
│ cost         │              │
│ description  │              │
│ store_id (FK)│──────┐       │
└──────────────┴──────┤       │
                      │       │
                      │   ┌───┴────────────────────────┐
                      │   │    administrative_cost_    │
                      │   │    movement                │
           ┌──────────┤   ├──────────────┬─────────────┤
           │          │   │ id (PK)      │             │
           │          └───┼─ admin_cost_ │             │
           │              │   id (FK)    │             │
           │              │ type         │             │
           │              │ amount_paid  │             │
           │              │ date_time    │             │
           │              │ user_id (FK) ├─────┐       │
           │              └──────────────┴─────┤       │
           │                                    │       │
           │          ┌──────────────┐      ┌───┴────┐
           │          │    store     │      │  user  │
           │          └──────────────┘      └────────┘
           │
           └─── store_id (FK)
```

---

**Implementación completada el**: 2026-04-16
**Estado**: ✅ LISTO PARA USAR
**Compilaciones**: ✅ Backend y Frontend sin errores
