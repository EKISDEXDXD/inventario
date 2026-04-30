#!/bin/bash

# ============================================
# COMANDOS RÁPIDOS: Docker Manager
# ============================================
# Copiar y pegar estos comandos en la terminal

# 📦 CONSTRUIR NUEVA VERSIÓN
echo "=== CONSTRUIR NUEVA VERSIÓN v2.0 ==="
cd /home/ekisde333/proyectos/project-inventario/licoreria-backend && docker build -t licoreria-backend:2.0 .
cd /home/ekisde333/proyectos/project-inventario/licoreria-frontend && docker build -t licoreria-frontend:2.0 .

# 🚀 INICIAR CONTENEDORES
echo "=== INICIAR CONTENEDORES ==="
cd /home/ekisde333/proyectos/project-inventario
docker-compose down 2>/dev/null
docker-compose up -d

# ✓ VERIFICAR ESTADO
echo "=== ESTADO DE CONTENEDORES ==="
docker-compose ps

# 📋 VER LOGS EN TIEMPO REAL
echo "=== LOGS (Presiona Ctrl+C para salir) ==="
docker-compose logs -f

# 🗑️ ELIMINAR VERSIONES ANTIGUAS
echo "=== ELIMINAR v1.0 ==="
docker rmi licoreria-backend:1.0 -f 2>/dev/null
docker rmi licoreria-frontend:1.0 -f 2>/dev/null
docker image prune -f

# 🔍 VER IMÁGENES ACTUALES
echo "=== IMÁGENES DISPONIBLES ==="
docker images | grep licoreria

# 🛑 DETENER CONTENEDORES
echo "=== DETENER TODO ==="
docker-compose down

# 💾 BACKUP BASE DE DATOS
echo "=== HACER BACKUP BD ==="
docker exec licoreria-postgres pg_dump -U licoreria_user licoreria_db > /home/ekisde333/proyectos/project-inventario/backup_$(date +%Y%m%d_%H%M%S).sql

# 🧹 LIMPIAR ESPACIO (elimina imágenes sin usar)
echo "=== LIMPIAR ESPACIO ==="
docker system prune -a -f

# 📊 VER USO DE RECURSOS
echo "=== USO DE RECURSOS ==="
docker stats

# 🔧 RECONSTRUIR UNA IMAGEN
echo "=== RECONSTRUIR BACKEND ==="
cd /home/ekisde333/proyectos/project-inventario/licoreria-backend && docker build --no-cache -t licoreria-backend:2.0 .

echo "=== RECONSTRUIR FRONTEND ==="
cd /home/ekisde333/proyectos/project-inventario/licoreria-frontend && docker build --no-cache -t licoreria-frontend:2.0 .
