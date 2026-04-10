# 🎨 Mockups Visuales - Gestión de Tiendas

## 📱 Perspectiva de Usuario

### 1️⃣ Pantalla: My-Stores (Lista de Tiendas)

```
╔═══════════════════════════════════════════════════════════╗
║                 Mis Tiendas         [← Volver]            ║
╠═══════════════════════════════════════════════════════════╣
║                                                           ║
║  ┌─────────────────┐  ┌─────────────────┐               ║
║  │ Licorería       │  │ Licorería       │               ║
║  │ Centro          │  │ Uptown          │               ║
║  │                 │  │                 │               ║
║  │ Tienda principal│  │ Sucursal zona   │               ║
║  │ en el centro    │  │ norte           │               ║
║  │                 │  │                 │               ║
║  │ 📍 Calle        │  │ 📍 Avenida      │               ║
║  │ Principal 123   │  │ Principal 450   │               ║
║  │                 │  │                 │               ║
║  │ Encargado:      │  │ Encargado:      │               ║
║  │ admin           │  │ admin           │               ║
║  │                 │  │                 │               ║
║  │ [Gestionar →] ◄─╋──── CLICKEABLE    │               ║
║  │ Inventario      │  │                 │               ║
║  └─────────────────┘  └─────────────────┘               ║
║                                                           ║
║                                                           ║
╚═══════════════════════════════════════════════════════════╝

ACCIONES:
- Click en la tarjeta → Navega a /tienda/:id
- Click en botón → Navega a /tienda/:id
- ← Volver → Regresa a Home
```

---

### 2️⃣ Pantalla: Dashboard Tienda (Panel de Gestión)

```
╔═══════════════════════════════════════════════════════════╗
║ [← Volver]    LICORERÍA CENTRO                            ║
║               Tienda principal en el centro               ║
║               📍 Calle Principal 123                      ║
╠═══════════════════════════════════════════════════════════╣
║                  Inventario de LICORERÍA CENTRO            ║
╠═══════════════════════════════════════════════════════════╣
║                                                           ║
║  ┌────────────────────────────────────────────────────┐  ║
║  │ Producto  │ Descripción  │ Stock │Costo│Precio│Acs│  ║
║  ├────────────────────────────────────────────────────┤  ║
║  │ Ron Viejo │ Ron añejo    │ ██50  │ $15 │ $35  │ +- │  ║
║  │           │              │ [Verde]│     │      │ 🗑️ │  ║
║  ├────────────────────────────────────────────────────┤  ║
║  │ Cerveza   │ Pack de 6    │ █████ │ $8  │ $14  │ +- │  ║
║  │ Corona    │ botellas     │ █████ │     │      │ 🗑️ │  ║
║  │           │              │ 120   │     │      │    │  ║
║  │           │              │[Verde]│     │      │    │  ║
║  ├────────────────────────────────────────────────────┤  ║
║  │ Vodka     │ Vodka sueco  │ ██8   │ $20 │ $42  │ +- │  ║
║  │ Absolut   │ 750ml        │ [Rojo]│     │      │ 🗑️ │  ║
║  │           │              │ BAJO  │     │      │    │  ║
║  ├────────────────────────────────────────────────────┤  ║
║  │ Whisky    │ Escocés      │ ████35│ $25 │ $55  │ +- │  ║
║  │ Johnnie   │ premium      │ [Narc]│     │      │ 🗑️ │  ║
║  │ Walker    │              │       │     │      │    │  ║
║  └────────────────────────────────────────────────────┘  ║
║                                                           ║
║  Colores de Stock:                                       ║
║  🟢 ALTO (>50)        🟡 MEDIO (10-50)      🔴 BAJO (<10) ║
║                                                           ║
║  Botones de Acción:                                      ║
║  [+5] = Suma 5 unidades (ENTRADA)                       ║
║  [-5] = Resta 5 unidades (SALIDA)                       ║
║  [🗑️] = Elimina el producto                             ║
║                                                           ║
╚═══════════════════════════════════════════════════════════╝

FLUJO:
1. Carga automática al entrar
2. Lee storeId de la URL (/tienda/1)
3. GET /api/stores/1 → Muestra nombre
4. GET /api/products/store/1 → Tabla
5. Click +5/-5 → PATCH /products/{id}/adjust-stock
6. ← Volver → Regresa a /my-stores
```

---

## 🔄 Flujo de Datos

```
┌─────────────────────────────────────────────────────────────┐
│                    USUARIO FINAL                             │
└─────────────────────────────────────────────────────────────┘
                           │
                           ↓
                    ┌──────────────┐
                    │  Frontend    │
                    │  Angular     │
                    └──────────────┘
                    /my-stores
                    dashboard-tienda
                           │
                           ↓ (HTTP + JWT)
        ┌──────────────────────────────────────────┐
        │      BACKEND - Spring Boot               │
        │                                          │
        │  GET /api/stores/{id}                   │
        │  GET /api/products/store/{id} ⭐ NUEVO  │
        │  PATCH /products/{id}adjust-stock       │
        └──────────────────────────────────────────┘
                           │
                           ↓
        ┌──────────────────────────────────────────┐
        │    BASE DE DATOS - PostgreSQL            │
        │                                          │
        │  stores:                                 │
        │    id=1, name="Centro", manager_id=1    │
        │                                          │
        │  products:                               │
        │    id=1, store_id=1, name="Ron"...      │
        │    ⬆️ Foreign Key - Nueva Relación       │
        │                                          │
        │  transactions:                           │
        │    Registra movimientos automáticos      │
        └──────────────────────────────────────────┘
```

---

## 🎯 Interacciones por Pantalla

### My-Stores (Before & After)

**ANTES (Sin Gestión por Tienda):**
```
┌──────────────┐
│ Tienda 1     │
│ [Eliminar]   │  ← Solo podías eliminar
└──────────────┘
```

**AHORA (Con Gestión por Tienda):**
```
┌──────────────────────┐
│ Tienda 1             │
│                      │
│ [Gestionar →]  ◄─ Clickeable en toda la tarjeta
│ Inventario     
└──────────────────────┘
    │
    └─→ /tienda/1 → Dashboard completo
```

---

### Dashboard (Acciones Detalladas)

```
ACCIÓN: Click en +5
───────────────────
1. User hace click en botón "+5" del producto Ron (ID:1)
2. Frontend ejecuta: adjustStock(1, 5)
3. Envía PATCH a /api/products/1/adjust-stock
4. Body: { delta: 5, transactionType: "ENTRADA", userId: 1 }
5. Backend:
   a) Obtiene producto (stock=50)
   b) Suma delta (50 + 5 = 55)
   c) Guarda en DB
   d) Crea transacción automáticamente
6. Frontend recibe respuesta con stock actualizado
7. Tabla se refresca mostrando stock=55 ✅


ACCIÓN: Click en -5
───────────────────
Igual que +5, pero:
- Button: "-5"
- delta: -5
- transactionType: "SALIDA"
- Resultado: stock baja


ACCIÓN: Click en 🗑️
──────────────────
1. Confirmación: "¿Eliminar este producto?"
2. Si OK:
   - DELETE /api/products/{id}
   - Backend elimina
   - Frontend refresca tabla
3. Producto desaparece de la lista
```

---

## 📊 Indicadores Visuales

### Badge de Stock

```
ALTO (>50)
┌─────────────┐
│ 🟢 75 Units │    ← Verde, abundante
└─────────────┘

MEDIO (10-50)
┌─────────────┐
│ 🟡 30 Units │    ← Amarillo, moderado
└─────────────┘

BAJO (<10)
┌─────────────┐
│ 🔴 5 Units  │    ← Rojo, peligro
└─────────────┘
```

---

## 🔐 Flujo de Autenticación

```
1. Usuario entra a http://localhost:4200
                    ↓
2. No tiene token → Redirige a /login
                    ↓
3. POST /api/auth/login
   Envía: { username, password }
   Recibe: JWT Token
                    ↓
4. localStorage.setItem('token', token)
                    ↓
5. Cada request HTTP incluye:
   Headers: { Authorization: 'Bearer ' + token }
                    ↓
6. ¿Token válido?
   ✅ SÍ → Ejecuta request
   ❌ NO → 401 → Redirige a /login
```

---

## 🎨 Paleta de Colores

```
Primario (Gradiente)
┌─────────────────────────────────┐
│ #667eea (Azul)                 │
│ → #764ba2 (Púrpura)            │
└─────────────────────────────────┘

Secundarios
┌─────────────────────────────────┐
│ ✅ Éxito:  #6bcf7f (Verde)     │
│ ⚠️ Aviso:  #ffd93d (Amarillo)   │
│ ❌ Error:  #ff6b6b (Rojo)       │
└─────────────────────────────────┘

Fondos
┌─────────────────────────────────┐
│ Principal: #ffffff              │
│ Secundario: #f5f5f5             │
│ Fondo: Gradiente                │
└─────────────────────────────────┘
```

---

## 📐 Layout Responivo

### Desktop (>768px)
```
┌─────────────────────────────────┐
│        Header                   │
├─────────────────────────────────┤
│  Tarjeta  │  Tarjeta  │ Tarjeta │
│  Tarjeta  │  Tarjeta  │ Tarjeta │
│────────────────────────────────┘
```

### Tablet (600px-768px)
```
┌───────────────────┐
│      Header       │
├───────────────────┤
│ Tarjeta │ Tarjeta │
│ Tarjeta │ Tarjeta │
│─────────────────┘
```

### Mobile (<600px)
```
┌──────────────┐
│    Header    │
├──────────────┤
│   Tarjeta    │
│   Tarjeta    │
|   Tarjeta    │
└──────────────┘
```

---

## 🔄 Estado de Carga

```
Cuando cargas /tienda/:id:

1. Muestra spinner/loading
2. Hace peticiones en paralelo:
   - GET /stores/1
   - GET /products/store/1
3. Cuando ambas terminen:
   - Desaparece loading
   - Muestra datos
4. Si error:
   - Muestra mensaje
   - Botón para reintentar
```

---

## ✨ Animaciones

```
Hover en Tarjeta:
┌────────────────────────────┐
│ Tienda (normal)            │  → Move up 5px
│────────────────────────────┘     Shadow increases
     ↑ Se mueve hacia arriba
     ↑ Sombra más pronunciada

Click en Botón:
┌──────┐
│ +5   │  → Scale 1.02x
└──────┘     Breve animación

Transición de Página:
/my-stores → / tienda/1
  Fade out  → Fade in (smooth)
```

---

## 📋 Estados Posibles

```
My-Stores:
─────────
✅ Tiendas cargadas → Muestra tarjetas
⏳ Cargando → Spinner
❌ Error → Mensajes de error + botón reintentar
🚫 Sin tiendas → "No hay tiendas, crear una"

Dashboard:
──────────
✅ Datos cargados → Tabla visible
⏳ Cargando → Spinner
❌ Tienda no existe → Mensaje + botón volver
❌ Sin productos → "No hay productos"
```

---

## 🎯 UX Improvements

```
Confirmaciones Explícitas:
- Antes de eliminar producto → Modal "¿Seguro?"
- Errores claros → Mensajes en rojo
- Éxito visible → Toast/Alert "Actualizado"

Feedback Visual:
- Desabled buttons cuando cargando
- Cursor pointer en elementos clickeables
- Color badges para estados
- Tooltips en botones de acción

Accesibilidad:
- Texto alternativo (title) en botones
- Contraste suficiente
- Navegación con Tab
- Labels en formularios
```

---

## 🚀 Transiciones de Rutas

```
Login
  ↓
Home (/home)
  ↓
My-Stores (/my-stores) ←─ CLICK TIENDA
  ↓
Dashboard (/tienda/:id)  ⭐ NUEVA RUTA
  ↓
Volver a My-Stores
  ↓
Logout
```

---

¡Así se verá y funcionará tu aplicación de gestión de tiendas! 🎨✨
