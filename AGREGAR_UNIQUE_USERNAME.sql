-- Script para agregar constraint UNIQUE en la columna username
-- Ejecutar INMEDIATAMENTE en pgAdmin → Query Tool

BEGIN;

-- 1. Verificar si el constraint ya existe
SELECT constraint_name 
FROM information_schema.table_constraints 
WHERE table_name = 'users' AND constraint_name = 'users_username_unique';

-- 2. Si no existe, agregarlo
ALTER TABLE users 
ADD CONSTRAINT users_username_unique UNIQUE (username);

-- 3. Verificar que se agregó
SELECT constraint_name, constraint_type
FROM information_schema.table_constraints
WHERE table_name = 'users' AND constraint_type = 'UNIQUE';

COMMIT;
