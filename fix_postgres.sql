-- Script SQL para configurar PostgreSQL
DO $$
BEGIN
   IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'licoreria_user') THEN
      CREATE USER licoreria_user WITH PASSWORD 'licoreria_pass';
      RAISE NOTICE 'Usuario licoreria_user creado';
   ELSE
      ALTER USER licoreria_user PASSWORD 'licoreria_pass';
      RAISE NOTICE 'Contraseña de licoreria_user actualizada';
   END IF;
END
$$;

-- Crear base de datos si no existe
SELECT 'CREATE DATABASE licoreria_db OWNER licoreria_user ENCODING ''UTF8'''
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'licoreria_db')\gexec

-- Otorgar permisos
GRANT ALL PRIVILEGES ON DATABASE licoreria_db TO licoreria_user;
ALTER USER licoreria_user CREATEDB;
