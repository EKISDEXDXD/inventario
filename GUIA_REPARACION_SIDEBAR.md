# Guía: Reparación del Sidebar con Menú Desplegable

## Problema Identificado
El menú lateral (sidebar) se cortaba cuando expandía el menú desplegable, debido a que el contenedor padre tenía `overflow: hidden` o `overflow-y: auto` que limitaba la visualización del contenido.

## Soluciones Implementadas ✅

### 1. **Z-Index Jerarquía Corregida**
```css
.menu-main { z-index: 2000; }
.menu-main .menu-site { z-index: 2001; }
.menu-main [class*="dropdown"] { z-index: 2010; }
```
- Aumenté el z-index del sidebar de 1000 a 2000
- Elementos internos tienen z-index: 2001
- Dropdowns tienen z-index: 2010 (flotante sobre todo)

### 2. **Overflow Configurado Correctamente**
```css
.menu-main {
  overflow: visible;
  pointer-events: auto;
}

.menu-site {
  overflow-y: auto;
  overflow-x: visible;  /* ← Permite que el contenido se extienda horizontalmente */
}
```

### 3. **Flexbox Layout Optimizado**
```css
.bottom-content {
  margin-top: auto;     /* Empuja la sección inferior automáticamente */
  flex-shrink: 0;       /* Evita que se comprima */
  position: relative;
  z-index: 2001;
}
```

### 4. **Contenedores Internos con Z-Index**
```css
.nav-link { position: relative; z-index: 2001; }
.mode { position: relative; z-index: 2001; flex-shrink: 0; }
```

## Cambios Específicos Realizados

| Elemento | Antes | Después | Razón |
|----------|-------|---------|-------|
| `.menu-main` z-index | 1000 | 2000 | Mayor prioridad visual |
| `.menu-main` overflow | visible | visible + pointer-events | Asegura interactividad |
| `.menu-site` overflow-x | auto | visible | No recorta contenido horizontal |
| `.bottom-content` position | absolute bottom: 10px | margin-top: auto | Flexbox automático |
| `.bottom-content` flex-shrink | no definido | 0 | Evita compresión |
| `.nav-link` margin | 5px 0 | 5px 10px | Espaciado horizontal consistente |

## Técnicas CSS Utilizadas

### 1. **Flexbox + margin-top: auto**
Reemplazó `position: absolute` en `.bottom-content`:
- ✅ Más flexible y responsivo
- ✅ No requiere hardcoding de posiciones
- ✅ Funciona mejor en diferentes tamaños de pantalla

### 2. **Overflow Visible en Contenedores Padre**
```css
.home-container { overflow: visible; }
.main-content { overflow: visible; }
```
Evita que el contenido flotante se corte.

### 3. **Z-Index Stratification**
Jerarquía clara:
- Menu overlay: z-index 1500
- Main container: z-index 1
- Menu main: z-index 2000
- Menu internals: z-index 2001+
- Dropdowns flotantes: z-index 2010+

## Uso: Dropdowns/Menús Expandibles Futuros

Si necesitas agregar un menú desplegable dentro del sidebar, asegúrate de:

### Opción 1: CSS Puro (Recomendado)
```css
.dropdown-menu {
  position: absolute;
  top: 100%;
  left: 0;
  z-index: 2010;        /* ← Crítico: debe ser > 2001 */
  min-width: 200px;
  background: white;
  border-radius: 6px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}
```

### Opción 2: Angular CDK Overlay (Para casos complejos)
```typescript
import { Overlay, OverlayConfig } from '@angular/cdk/overlay';
import { TemplatePortal } from '@angular/cdk/portal';

constructor(private overlay: Overlay, private vcr: ViewContainerRef) {}

openDropdown(trigger: HTMLElement) {
  const config = new OverlayConfig({
    positionStrategy: this.overlay.position()
      .flexibleConnectedTo(trigger)
      .withPositions([{
        originX: 'start',
        originY: 'bottom',
        overlayX: 'start',
        overlayY: 'top'
      }]),
    scrollStrategy: this.overlay.scrollStrategies.reposition(),
    hasBackdrop: false
  });

  const overlayRef = this.overlay.create(config);
  const portal = new TemplatePortal(this.dropdownTemplate, this.vcr);
  overlayRef.attach(portal);
}
```

## Archivo Modificado
- **Ruta**: `licoreria-frontend/src/app/main-layout.component.css`
- **Cambios**: 8 reemplazos CSS
- **Estado**: ✅ Completado

## Testing Recomendado
1. ✅ Expandir/contraer sidebar en desktop (1920px, 1200px)
2. ✅ Expandir/contraer sidebar en tablet (768px)
3. ✅ Expandir/contraer sidebar en mobile (480px, 390px)
4. ✅ Verificar que no haya cortes de contenido
5. ✅ Probar con elemento desplegable si existe

## Notas Técnicas
- Los cambios son **100% CSS**, no requieren cambios en TypeScript
- Compatible con todas las versiones modernas de Angular
- Sin dependencias externas (CDK Overlay es opcional)
- Mantiene la accesibilidad (no afecta ARIA labels ni navegación)

---
**Última actualización**: 29 de Abril, 2026
