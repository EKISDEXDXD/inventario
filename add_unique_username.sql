-- Script para agregar constraint UNIQUE en username
-- Ejecutar en pgAdmin → Query Tool

-- 1. Verificar duplicados actuales
SELECT username, COUNT(*) as cantidad
FROM users
GROUP BY username
HAVING COUNT(*) > 1;

-- 2. Si hay duplicados, limpiar (mantiene el primero, borra los demás)
DELETE FROM users 
WHERE id NOT IN (
    SELECT MIN(id) FROM users GROUP BY username
)
AND username IN (
    SELECT username FROM users GROUP BY username HAVING COUNT(*) > 1
);

-- 3. Agregar constraint UNIQUE a username
ALTER TABLE users 
ADD CONSTRAINT users_username_unique UNIQUE (username);

-- 4. Verificar que se agregó correctamente
SELECT constraint_name, constraint_type
FROM information_schema.table_constraints
WHERE table_name = 'users' AND constraint_type = 'UNIQUE';

-- 5. Probar que funciona (esto debería fallar si hay duplicados)
-- SELECT * FROM users WHERE username = 'admin';
