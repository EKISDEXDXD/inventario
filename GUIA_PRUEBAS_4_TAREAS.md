# 🧪 GUÍA DE PRUEBAS - 4 TAREAS CRÍTICAS

## Preparación del Ambiente

### Terminal 1 - Backend
```bash
cd /home/ekisde333/proyectos/project-inventario/licoreria-backend
mvn spring-boot:run
```
✓ Esperar hasta ver: `Started Application in X.XXX seconds`

### Terminal 2 - Frontend
```bash
cd /home/ekisde333/proyectos/project-inventario/licoreria-frontend
npm start
```
✓ Abrir navegador en: `http://localhost:4200`

---

## ✅ Prueba 1: Validaciones de Formulario (Frontend)

### Paso 1: Ir a Crear Tienda
1. Inicia sesión en la aplicación
2. Navega a "Crear Nueva Tienda"

### Paso 2: Probar Campos Vacíos
1. Haz clic en campo "Nombre" y luego en otro campo
   - ❌ Debe mostrar: "El nombre es requerido"
2. Haz clic en campo "Descripción"  
   - ❌ Debe mostrar: "La descripción es requerida"

### Paso 3: Probar Validaciones de Longitud
1. Ingresa en "Nombre": "AB" (2 caracteres)
2. Haz clic en otro campo
   - ❌ Debe mostrar: "Mínimo 3 caracteres"
   
3. Ingresa en "Descripción": "Corta" (5 caracteres)
4. Haz clic en otro campo
   - ❌ Debe mostrar: "Mínimo 10 caracteres"

### Paso 4: Probar Botón Deshabilitado
1. Todos los campos vacíos → Botón "Crear" DESHABILITADO (gris)
2. Llena todos correctamente → Botón "Crear" HABILITADO (azul)

**Datos de Prueba Válidos:**
- Nombre: `Tienda Test 2024`
- Descripción: `Esta es una tienda de prueba para validar el sistema`
- Dirección: `Jr. Ejemplo 123, Lima`
- Contraseña: `password123`

---

## ✅ Prueba 2: Encriptación de Contraseña (Backend)

### Paso 1: Crear Tienda con Contraseña
```
Nombre: Tienda Encriptada
Descripción: Tienda para probar encriptación
Dirección: Av. Test 456
Contraseña: micontraseña123
```
✓ Tienda creada exitosamente

### Paso 2: Verificar en Base de Datos
```bash
# En terminal 3, conectar a PostgreSQL:
psql -U username -d inventario_db

# Listar tiendas:
SELECT id, name, access_password FROM stores WHERE name='Tienda Encriptada';
```

**Resultado Esperado:**
```
 id │        name         │           access_password
────┼─────────────────────┼────────────────────────────────
  5 │ Tienda Encriptada   │ $2a$10$xyz...xyz (hash bcrypt)
```

⚠️ La columna `access_password` debe mostrar un hash bcrypt (comienza con $2a$)
❌ NO debe mostrar "micontraseña123" en texto plano

---

## ✅ Prueba 3: Validación de Permisos (Backend)

### Configuración: 2 Usuarios
1. **Usuario A (tu usuario actual)** - Ya logueado
2. **Usuario B** - Crear nueva cuenta

### Paso 1: Usuario A Crea Tienda
1. Inicia sesión como Usuario A
2. Crea tienda: "Tienda de Usuario A"
3. Anota el ID (verlo en URL: `/stores/123`)

### Paso 2: Usuario B Intenta Acceder
1. Abre pestaña privada/incógnito
2. Inicia sesión como Usuario B
3. Abre desarrollador (F12) → Network
4. Intenta acceder a tienda de A directamente:
   ```
   http://localhost:4200/stores/123
   ```

**Resultado Esperado:**
- ❌ Pantalla en blanco o error
- 📋 En Network → Ver request a `/api/stores/123`
- 🔴 Respuesta HTTP 403 (Forbidden)
- 📝 Mensaje: "No tienes permiso para acceder a esta tienda"

### Paso 3: Usuario A Accede Su Tienda
1. Vuelve a pestaña de Usuario A
2. Navega a "Mis Tiendas"
3. Haz clic en su tienda
   - ✅ Acceso permitido
   - 📊 Muestra datos de la tienda

---

## ✅ Prueba 4: Acceso Externo con Contraseña Encriptada

### Paso 1: Home - Modal de Acceso Externo
1. Inicia sesión como Usuario A
2. Navega a Home (no a Mis Tiendas)
3. Busca botón "Acceder como Externo" o similar
4. Abre modal de acceso externo

### Paso 2: Acceso Exitoso
1. Ingresa nombre de tienda: "Tienda Encriptada"
2. Ingresa contraseña: "micontraseña123"
3. Haz clic "Acceder"
   - ✅ Acceso permitido
   - 📊 Muestra inventario de la tienda

### Paso 3: Acceso Fallido
1. Abre modal nuevamente
2. Ingresa nombre: "Tienda Encriptada"
3. Ingresa contraseña: "contraseñaincorrecta"
4. Haz clic "Acceder"
   - 🔴 Toast rojo: "Tienda no encontrada o contraseña incorrecta"
   - ❌ Acceso denegado

---

## ✅ Prueba 5: Manejo Global de Errores

### Prueba 5A: Error 404
1. Abre desarrollador (F12) → Console
2. Ejecuta:
   ```javascript
   fetch('http://localhost:8081/api/tiendas/99999', {
     headers: { 'Authorization': 'Bearer ' + localStorage.getItem('token') }
   })
   ```
3. **Resultado:** Toast rojo con "Recurso no encontrado"

### Prueba 5B: Error 403 (Permiso Denegado)
1. Intenta acceder a tienda que no es tuya
2. **Resultado:** Toast rojo con "No tienes permiso para acceder a este recurso"

### Prueba 5C: Error 500
1. Para simular, puedes hacer una request malformada
2. **Resultado:** Toast rojo con "Error interno del servidor"

### Prueba 5D: Validación Fallida
1. Intenta crear tienda sin datos
2. **Resultado:** Toast rojo con error de validación

---

## 📊 Checklist de Pruebas

### Validaciones Frontend
- [ ] Campo vacío muestra error requerido
- [ ] Campo corto muestra error de mínimo
- [ ] Campo largo muestra error de máximo
- [ ] Botón deshabilitado cuando hay errores
- [ ] Botón habilitado cuando todo es válido
- [ ] Mensajes desaparecen al corregir errores

### Encriptación Backend
- [ ] Contraseña en BD es hash bcrypt ($2a$...)
- [ ] Contraseña original NO es visible en BD
- [ ] Validación de contraseña funciona correctamente
- [ ] Acceso externo con contraseña correcta funciona
- [ ] Acceso externo con contraseña incorrecta falla

### Validación de Permisos
- [ ] Usuario A puede ver su tienda
- [ ] Usuario A NO puede ver tienda de Usuario B
- [ ] Respuesta 403 cuando no hay permiso
- [ ] Acceso externo respeta contraseña cifrada
- [ ] GET, POST, DELETE validados

### Manejo de Errores
- [ ] Error 404 muestra toast "Recurso no encontrado"
- [ ] Error 403 muestra toast "No tienes permiso"
- [ ] Error 500 muestra toast "Error interno"
- [ ] Error de validación muestra toast específico
- [ ] Toast desaparece después de 5 segundos

---

## 🐛 Troubleshooting

### El frontend no compila
```bash
cd licoreria-frontend
rm -rf node_modules package-lock.json
npm install --legacy-peer-deps
npm run build
```

### Backend no inicia
```bash
cd licoreria-backend
mvn clean install
mvn spring-boot:run
```

### Contraseña anterior en BD no está encriptada
- Es normal, se encriptará en el siguiente login/creación
- Ejecuta: `UPDATE stores SET access_password = '' WHERE name = 'tienda_vieja'`
- Recrear la tienda para encriptar nueva contraseña

### Toast no aparece
- Verificar que ngx-toastr está instalado
- Ver console para errors: F12 → Console
- Refresh página: Ctrl+F5

### Validaciones no funcionan
- Verificar que create-store.component.ts usa FormBuilder
- Ver console para errores de TypeScript
- Rebuild frontend: `npm run build`

---

## 📈 Resultados Esperados

**Después de todas las pruebas:**
- ✅ 5/5 Validaciones frontend funcionando
- ✅ 5/5 Encriptación backend verificada
- ✅ 5/5 Permisos validados correctamente
- ✅ 5/5 Errores manejados globalmente
- 🟢 **Sistema listo para producción**

