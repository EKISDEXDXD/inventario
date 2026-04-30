# 🐳 Guía Completa: Dockerizar Nueva Versión y Eliminar Antigua

## 📋 Resumen Rápido

Tienes 3 opciones para dockerizar tu aplicación:

| Opción | Comando | Tiempo | Uso |
|--------|---------|--------|-----|
| **Automático** | `bash docker-manager.sh` | Interactivo | ✅ Recomendado |
| **Manual** | `docker-compose up -d` | 15 min | Si ya compilaste |
| **Paso a Paso** | Comandos individuales | 30 min | Para aprender |

---

## ✅ OPCIÓN 1: AUTOMÁTICO (Recomendado)

### Paso 1: Permisos de Ejecución
```bash
chmod +x /home/ekisde333/proyectos/project-inventario/docker-manager.sh
```

### Paso 2: Ejecutar Script
```bash
cd /home/ekisde333/proyectos/project-inventario
./docker-manager.sh
```

### Paso 3: Seleccionar Opciones
```
═══════════════════════════════════════
  GESTOR DOCKER - LICORERIA v2.0
═══════════════════════════════════════

1️⃣  Construir nueva versión (v2.0)
2️⃣  Iniciar contenedores
3️⃣  Detener contenedores
4️⃣  Limpiar versiones antiguas
5️⃣  Ver logs
6️⃣  Verificar estado
0️⃣  Salir
```

**Flujo recomendado:**
1. Selecciona `1` (Construir v2.0) → Espera 10-15 min
2. Selecciona `2` (Iniciar) → Contenedores arriba
3. Selecciona `6` (Estado) → Verificar todo funciona
4. Selecciona `4` (Limpiar) → Eliminar versiones antiguas

---

## 🔧 OPCIÓN 2: MANUAL (Paso a Paso)

### Paso 1: Construir Backend

```bash
cd /home/ekisde333/proyectos/project-inventario/licoreria-backend
docker build -t licoreria-backend:2.0 .
```

**Esto tardará 5-10 minutos la primera vez.**

Esperas estos pasos:
1. ✓ Descarga Maven + JDK 21
2. ✓ Copia tu código
3. ✓ Compila con `mvn`
4. ✓ Descarga JRE + Alpine
5. ✓ Copia JAR compilado

### Paso 2: Construir Frontend

```bash
cd /home/ekisde333/proyectos/project-inventario/licoreria-frontend
docker build -t licoreria-frontend:2.0 .
```

**Esto tardará 5-10 minutos también.**

Esperas estos pasos:
1. ✓ Descarga Node 22 + Alpine
2. ✓ Instala dependencias npm
3. ✓ Compila Angular production
4. ✓ Descarga Nginx + Alpine
5. ✓ Copia archivos compilados

### Paso 3: Iniciar Contenedores

```bash
cd /home/ekisde333/proyectos/project-inventario
docker-compose down   # Detener antiguos (si existen)
docker-compose up -d  # Iniciar nuevos
```

Esperas estos servicios:
- ✓ **PostgreSQL**: Escucha en puerto 5432
- ✓ **Backend**: Escucha en puerto 8081
- ✓ **Frontend**: Escucha en puerto 80

### Paso 4: Verificar que Funciona

```bash
# Ver contenedores corriendo
docker-compose ps

# Ver logs en tiempo real
docker-compose logs -f

# Probar la aplicación
curl http://localhost      # Frontend
curl http://localhost:8081 # Backend
```

### Paso 5: Limpiar Versiones Antiguas

```bash
# Listar todas las imágenes
docker images

# Eliminar versiones antiguas
docker rmi licoreria-backend:1.0 -f
docker rmi licoreria-frontend:1.0 -f

# Limpiar imágenes intermedias
docker image prune -f
```

---

## 🗑️ ELIMINAR VERSIÓN ANTIGUA

### Opción A: Eliminar Solo Imágenes

```bash
# Ver qué imágenes existen
docker images | grep licoreria

# Eliminar v1.0
docker rmi licoreria-backend:1.0 -f
docker rmi licoreria-frontend:1.0 -f

# Eliminar contenedores asociados (si existen)
docker rm licoreria-backend-v1 -f
docker rm licoreria-frontend-v1 -f
```

### Opción B: Limpiar Todo (¡CUIDADO!)

```bash
# DETENER TODO
docker-compose down

# ELIMINAR IMÁGENES ANTIGUAS
docker rmi licoreria-backend:1.0 -f
docker rmi licoreria-frontend:1.0 -f

# LIMPIAR VOLÚMENES ANTIGUOS (PIERDE DATOS)
docker volume prune -f

# LIMPIAR REDES ANTIGUAS
docker network prune -f
```

---

## 📊 Estructura de Versiones

Después de dockerizar, tendrás:

```
Imágenes (docker images):
├── licoreria-backend:2.0 ✓ Nueva
├── licoreria-frontend:2.0 ✓ Nueva
├── postgres:16-alpine (siempre nueva)
├── maven:3.9 (solo construcción, se puede eliminar)
└── node:22-alpine (solo construcción, se puede eliminar)

Contenedores (docker-compose ps):
├── licoreria-postgres (en ejecución)
├── licoreria-backend (en ejecución)
└── licoreria-frontend (en ejecución)
```

---

## 🌐 Acceder a tu Aplicación

Después de iniciar, accede a:

| Servicio | URL | Puerto |
|----------|-----|--------|
| **Frontend** | http://localhost | 80 |
| **Backend** | http://localhost:8081 | 8081 |
| **DB Postgres** | localhost | 5432 |

---

## 🔍 Solucionar Problemas

### ❌ "docker: command not found"
```bash
# Instalar Docker
sudo apt update
sudo apt install docker.io docker-compose -y

# Agregar tu usuario al grupo docker (sin sudo)
sudo usermod -aG docker $USER
newgrp docker
```

### ❌ "Permission denied while trying to connect to the Docker daemon"
```bash
# Arreglar permisos
sudo usermod -aG docker $USER
sudo systemctl restart docker
# Logout y login nuevamente
```

### ❌ "Port 80 already in use"
```bash
# Ver qué usa el puerto 80
sudo lsof -i :80

# O cambiar puerto en docker-compose.yml
# Cambiar "- '80:80'" a "- '8080:80'"
```

### ❌ "Backend cannot connect to database"
```bash
# Verificar que postgres está corriendo
docker-compose ps

# Ver logs de postgres
docker-compose logs postgres

# Reiniciar todos los servicios
docker-compose down && docker-compose up -d
```

### ❌ "Frontend shows blank page"
```bash
# Ver logs del frontend
docker-compose logs licoreria-frontend

# Verificar que backend es accesible
curl http://localhost:8081/api/health

# Reconstruir si necesario
docker build -t licoreria-frontend:2.0 ./licoreria-frontend
docker-compose restart licoreria-frontend
```

---

## 📈 Monitoreo y Mantenimiento

### Ver uso de recursos
```bash
docker stats

# Ver solo tus contenedores
docker compose stats
```

### Backup de base de datos
```bash
docker exec licoreria-postgres pg_dump -U licoreria_user licoreria_db > backup.sql
```

### Restaurar base de datos
```bash
docker exec -i licoreria-postgres psql -U licoreria_user licoreria_db < backup.sql
```

### Actualizar a nueva versión
```bash
# Construir nueva versión
docker build -t licoreria-backend:3.0 ./licoreria-backend
docker build -t licoreria-frontend:3.0 ./licoreria-frontend

# Actualizar docker-compose.yml con v3.0
# Luego:
docker-compose down
docker-compose up -d

# Eliminar versiones antiguas
docker rmi licoreria-backend:2.0 -f
docker rmi licoreria-frontend:2.0 -f
```

---

## ✅ Checklist de Implementación

- [ ] Docker está instalado (`docker --version`)
- [ ] docker-compose está instalado (`docker-compose --version`)
- [ ] Puedo ejecutar comandos sin `sudo` (usuario en grupo docker)
- [ ] He construido la versión v2.0
- [ ] Contenedores v2.0 están corriendo (`docker-compose ps`)
- [ ] Frontend es accesible en http://localhost
- [ ] Backend es accesible en http://localhost:8081
- [ ] Base de datos funciona
- [ ] He eliminado versiones antiguas (v1.0)
- [ ] Backups están hechos

---

## 🚀 Próximos Pasos

1. **CI/CD**: Configurar GitHub Actions para auto-construir imágenes
2. **Registry**: Subir imágenes a Docker Hub o Container Registry
3. **Kubernetes**: Desplegar en producción con Kubernetes
4. **Health Checks**: Agregar healthchecks más robustos
5. **Logging**: Configurar logging centralizado (ELK Stack, etc)

---

## 📞 Soporte Rápido

**Si algo no funciona:**
1. Ejecuta: `docker-compose logs` para ver todos los logs
2. Ejecuta: `docker-compose ps` para ver estado de contenedores
3. Ejecuta: `docker images | grep licoreria` para ver versiones
4. Reinicia todo: `docker-compose down && docker-compose up -d`

---

**Última actualización**: 29 de Abril, 2026
**Versión**: 2.0
