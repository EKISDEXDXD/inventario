# Guía Segura: Cómo Borrar Datos de Usuarios en pgAdmin

## 📋 Índice
1. [Preparación](#preparación)
2. [Identificar Relaciones](#identificar-relaciones)
3. [Métodos de Eliminación](#métodos-de-eliminación)
4. [Procedimiento Paso a Paso](#procedimiento-paso-a-paso)
5. [Verificación y Validación](#verificación-y-validación)
6. [En Caso de Problemas](#en-caso-de-problemas)

---

## 🔒 **Paso 1: Preparación - BACKUP OBLIGATORIO**

### Antes de hacer cualquier cosa, SIEMPRE haz un backup:

**En pgAdmin:**
1. Click derecho en la base de datos → **Backup**
2. **General** → Nombre: `backup_usuarios_[fecha]`
3. **Dump Options** → Elige:
   - ✅ `Only data` (si solo borras datos)
   - ✅ `Include DROP IF EXISTS` (opcional)
4. Click **Backup**

**O desde terminal (línea de comandos):**
```bash
pg_dump -U tu_usuario -d tu_base_datos > backup_$(date +%Y%m%d_%H%M%S).sql
```

---

## 🔍 **Paso 2: Identificar las Relaciones de Datos**

Antes de borrar un usuario, necesitas saber qué otros datos dependen de él:

### En pgAdmin - Visualizar Relaciones:
1. Abre **Databases** → Tu base de datos
2. Expande **Schemas** → **public** → **Tables**
3. Busca las tablas que tienen referencias al usuario:
   - `users` (tabla principal)
   - `products` (puede tener user_id)
   - `stores` (puede tener user_id)
   - `transactions` (puede tener user_id)
   - Cualquier otra tabla con FK a usuarios

### Comando SQL para ver las dependencias:
```sql
SELECT 
    tc.table_name, 
    kcu.column_name, 
    ccu.table_name AS foreign_table_name,
    ccu.column_name AS foreign_column_name 
FROM 
    information_schema.table_constraints AS tc 
    JOIN information_schema.key_column_usage AS kcu
      ON tc.constraint_name = kcu.constraint_name
      AND tc.table_schema = kcu.table_schema
    JOIN information_schema.constraint_column_usage AS ccu
      ON ccu.constraint_name = tc.constraint_name
      AND ccu.table_schema = tc.table_schema
WHERE tc.constraint_type = 'FOREIGN KEY' 
  AND ccu.table_name='users';
```

---

## 🛡️ **Paso 3: Métodos de Eliminación (Elige UNO)**

### **Método 1: DELETE CASCADE (Automático)**
Si las FK ya tienen `ON DELETE CASCADE`, simplemente elimina el usuario:
```sql
DELETE FROM users WHERE id = 123;
```
Esto elimina automáticamente todos los registros relacionados.

### **Método 2: Eliminar en Orden de Dependencias (Manual + Seguro)**
Elimina primero los datos dependientes, luego el usuario:

```sql
BEGIN TRANSACTION;

-- 1. Elimina transacciones del usuario
DELETE FROM transactions WHERE user_id = 123;

-- 2. Elimina productos del usuario
DELETE FROM products WHERE user_id = 123;

-- 3. Elimina tiendas del usuario
DELETE FROM stores WHERE user_id = 123;

-- 4. Finalmente, elimina el usuario
DELETE FROM users WHERE id = 123;

COMMIT;
```

### **Método 3: UPDATE a NULL (Conservar datos)**
Si quieres conservar historial pero desasociar del usuario:

```sql
BEGIN TRANSACTION;

-- Desasocia pero mantiene los registros
UPDATE products SET user_id = NULL WHERE user_id = 123;
UPDATE transactions SET user_id = NULL WHERE user_id = 123;

-- Luego elimina el usuario
DELETE FROM users WHERE id = 123;

COMMIT;
```

### **Método 4: Soft Delete (Marca como inactivo)**
En lugar de eliminar, marca como inactivo:

```sql
UPDATE users 
SET status = 'INACTIVE', 
    deleted_at = NOW() 
WHERE id = 123;
```

---

## 📝 **Paso 4: Procedimiento Paso a Paso en pgAdmin**

### **Opción A: Usando la Interfaz Gráfica**

1. **Abre pgAdmin → Tu Base de Datos**
2. **Schemas → public → Tables → (tabla)**
3. **Click derecho en la tabla → View/Edit Data**
4. **Busca el usuario** a eliminar (filtra por ID o email)
5. **Click en el ícono de papelera** (delete) en esa fila
6. **Confirma la eliminación**

### **Opción B: Usando la Consola SQL (Recomendado)**

1. **Abre Tools → Query Tool** (o Ctrl+Alt+E)
2. **Copia este script:**

```sql
-- 1. BACKUP (verifica que existe)
-- Ejecuta un backup antes de esto en la interfaz

-- 2. Identifica el usuario a eliminar
SELECT * FROM users WHERE email = 'usuario@example.com';

-- 3. Ve qué datos dependen de él
SELECT * FROM products WHERE user_id = 123;
SELECT * FROM transactions WHERE user_id = 123;

-- 4. ELIMINA EN TRANSACCIÓN (copia el ID del usuario)
BEGIN TRANSACTION;

-- Reemplaza 123 con el ID real del usuario
DELETE FROM transactions WHERE user_id = 123;
DELETE FROM products WHERE user_id = 123;
DELETE FROM stores WHERE user_id = 123;
DELETE FROM users WHERE id = 123;

-- Si todo se ve bien, ejecuta COMMIT
-- Si hay error, ejecuta ROLLBACK
COMMIT;

-- 5. VERIFICA que se eliminó
SELECT COUNT(*) FROM users WHERE id = 123;
-- Debería retornar 0
```

3. **Ejecuta línea por línea (F5 o botón Play)**
4. **Revisa resultados antes de hacer COMMIT**
5. **Si algo mal, USA ROLLBACK** (no COMMIT)

---

## ✅ **Paso 5: Verificación y Validación**

Después de eliminar, verifica:

```sql
-- 1. Confirma que el usuario fue eliminado
SELECT COUNT(*) FROM users WHERE id = 123;
-- Debe retornar: 0

-- 2. Verifica que no quedaron datos huérfanos
SELECT * FROM products WHERE user_id = 123;
-- Debe retornar: (sin resultados)

SELECT * FROM transactions WHERE user_id = 123;
-- Debe retornar: (sin resultados)

-- 3. Verifica integridad de la base de datos
SELECT COUNT(*) FROM users;
-- Debería ser uno menos que antes
```

---

## 🚨 **En Caso de Problemas**

### **Error: "violates foreign key constraint"**
Significa que hay datos que dependen del usuario. **Solución:**
1. Ejecuta `ROLLBACK;` si estás en una transacción
2. Primero elimina los datos dependientes
3. Luego elimina el usuario

```sql
ROLLBACK;
-- Luego
DELETE FROM products WHERE user_id = 123;
DELETE FROM users WHERE id = 123;
```

### **Eliminaste accidentalmente datos importantes**
1. **DETÉN TODO INMEDIATAMENTE**
2. Abre pgAdmin
3. **Restore From Backup:**
   - Click derecho en la BD → **Restore**
   - Selecciona el backup que creaste
   - ¡Listo!

### **Quieres deshacer los cambios**

Si estás en una transacción aún abierta:
```sql
ROLLBACK;  -- Deshace todos los cambios
```

---

## 📋 **Checklist de Seguridad**

Antes de eliminar un usuario:

- [ ] ✅ **Backup creado** (confirma que existe)
- [ ] ✅ **Identificaste todas las tablas que lo referencian**
- [ ] ✅ **Verificaste que no se perderán datos críticos**
- [ ] ✅ **Usaste transacción BEGIN...COMMIT**
- [ ] ✅ **Ejecutaste SELECT primero (no DELETE)**
- [ ] ✅ **Revisaste resultados antes de COMMIT**
- [ ] ✅ **Verificaste después que se eliminó correctamente**

---

## 🎯 **Resumen Rápido (Forma Segura)**

```sql
-- 1. Ver usuario
SELECT * FROM users WHERE email = 'ejemplo@mail.com';

-- 2. Ver dependencias
SELECT * FROM products WHERE user_id = 123;
SELECT * FROM transactions WHERE user_id = 123;

-- 3. Eliminar en transacción
BEGIN;
DELETE FROM products WHERE user_id = 123;
DELETE FROM transactions WHERE user_id = 123;
DELETE FROM users WHERE id = 123;
COMMIT;

-- 4. Verificar
SELECT COUNT(*) AS usuario_existe FROM users WHERE id = 123;
```

---

## ⚠️ **Nunca Hagas Esto:**

```sql
-- ❌ NUNCA SIN BACKUP
DELETE FROM users;  -- ¡Borra TODOS los usuarios!

-- ❌ NUNCA SIN REVISAR PRIMERO
DELETE FROM users WHERE email LIKE '%@%';  -- ¡Muy arriesgado!

-- ❌ NUNCA SIN TRANSACCIÓN
DELETE FROM products WHERE user_id = 123;
DELETE FROM users WHERE id = 123;  -- Si falla a mitad, datos inconsistentes
```

---

## 📞 **Ayuda Rápida**

| Problema | Solución |
|----------|----------|
| No sé el ID del usuario | `SELECT id FROM users WHERE email = 'email@ejemplo.com';` |
| Hay datos que no puedo borrar | Usa `UPDATE ... SET user_id = NULL` en lugar de DELETE |
| Necesito borrar múltiples usuarios | Usa `DELETE FROM users WHERE id IN (1, 2, 3);` |
| Quiero ver primero qué se borrará | Usa `SELECT` en lugar de `DELETE` antes de ejecutar |
| Me equivoqué | `ROLLBACK;` (mientras estés en la transacción) |

---

**¡Recuerda: Mejor seguro que sentido!** 🔒
