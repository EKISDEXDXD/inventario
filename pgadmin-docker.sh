#!/bin/bash

echo "=== pgAdmin con Docker para Licorería ==="
echo ""

# Función para verificar si el contenedor está corriendo
container_running() {
    docker ps --format "table {{.Names}}" | grep -q "licoreria-pgadmin"
}

echo "Iniciando pgAdmin con Docker..."
if container_running; then
    echo "pgAdmin ya está corriendo en http://localhost:8080"
else
    docker run -d \
        --name licoreria-pgadmin \
        -p 8080:80 \
        -e PGADMIN_DEFAULT_EMAIL=admin@licoreria.com \
        -e PGADMIN_DEFAULT_PASSWORD=admin123 \
        dpage/pgadmin4:latest
    
    echo "pgAdmin iniciado exitosamente!"
    echo ""
    echo "🌐 Accede en: http://localhost:8080"
    echo "👤 Usuario: admin@licoreria.com"
    echo "🔑 Contraseña: admin123"
fi

echo ""
echo "Para detener pgAdmin: docker stop licoreria-pgadmin"
echo "Para iniciar pgAdmin: docker start licoreria-pgadmin"
echo "Para eliminar: docker rm licoreria-pgadmin"

# Ver estado
docker ps | grep pgadmin

# Detener
docker stop licoreria-pgadmin

# Iniciar
docker start licoreria-pgadmin

# Ver logs
docker logs licoreria-pgadmin



Host: host.docker.internal