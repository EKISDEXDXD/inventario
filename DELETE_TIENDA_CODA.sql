-- Script para borrar la tienda de Coda desde pgAdmin
-- Tienda: "Tienda de koda y Milo" (ID: 9)
-- Manager: coda (ID: 1)

-- PASO 1: Ver la tienda antes de borrar
SELECT id, name, description, address, manager_id, access_password 
FROM stores 
WHERE id = 9;

-- PASO 2: Borrar la tienda
DELETE FROM stores 
WHERE id = 9;

-- PASO 3: Confirmar que se borró
SELECT id, name, manager_id FROM stores;
