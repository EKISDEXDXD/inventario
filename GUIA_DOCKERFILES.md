# 🐳 Guía Completa: Construir y Usar los Dockerfiles

## 📋 Estructura de Archivos Actual

```
project-inventario/
├── licoreria-backend/
│   ├── Dockerfile          ← Recién creado
│   ├── .dockerignore       ← Recién creado
│   ├── pom.xml
│   ├── src/
│   └── target/             (se genera al compilar)
│
├── licoreria-frontend/
│   ├── Dockerfile          ← Recién creado
│   ├── .dockerignore       ← Recién creado
│   ├── nginx.conf          ← Recién creado
│   ├── package.json
│   └── src/
```

---

## 🛠️ PASO 1: Instalar Docker

### En Linux (WSL en Windows):
```bash
# Actualizar repositorios
sudo apt update

# Instalar Docker
sudo apt install docker.io -y

# Instalar Docker Compose
sudo apt install docker-compose -y

# Verificar instalación
docker --version
docker-compose --version
```

### En Windows/Mac:
- Descargar [Docker Desktop](https://www.docker.com/products/docker-desktop)
- Instalar y reiniciar

---

## 🏗️ PASO 2: Construir la imagen del Backend

Navega a la carpeta del backend:

```bash
cd /home/ekisde333/proyectos/project-inventario/licoreria-backend
```

Construye la imagen:

```bash
docker build -t licoreria-backend:1.0 .
```

**Explicación:**
- `docker build`: comando para construir imagen
- `-t licoreria-backend:1.0`: etiqueta (nombre:versión)
- `.`: busca Dockerfile en la carpeta actual

**¿Qué pasa?**
1. Docker lee el Dockerfile
2. Descarga la imagen `maven:3.9`
3. Copia tu código
4. Compila con `mvn`
5. Descarga `eclipse-temurin:21-jre-alpine`
6. Copia el JAR
7. Crea la imagen final

**Esto tarda ~5-10 minutos la primera vez** (después es más rápido porque cachea)

---

## 🎨 PASO 3: Construir la imagen del Frontend

Navega a la carpeta del frontend:

```bash
cd /home/ekisde333/proyectos/project-inventario/licoreria-frontend
```

Construye la imagen:

```bash
docker build -t licoreria-frontend:1.0 .
```

**¿Qué pasa?**
1. Docker descarga `node:20-alpine`
2. Copia `package.json`
3. Instala dependencias con `npm`
4. Compila Angular
5. Descarga `nginx:alpine`
6. Copia archivos compilados
7. Configura Nginx
8. Crea imagen final

**Esto tarda ~8-15 minutos** (Node tarda más)

---

## ✅ PASO 4: Verificar que las imágenes se crearon

```bash
docker images
```

Deberías ver:
```
REPOSITORY             TAG       IMAGE ID       SIZE
licoreria-frontend     1.0       abc123def456   50MB
licoreria-backend      1.0       xyz789def456   250MB
```

---

## 🧪 PASO 5: Testear localmente (SIN Docker Compose aún)

### Testear el Backend:

```bash
# Ejecutar contenedor
docker run -p 8081:8081 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/licoreria_db \
  -e SPRING_DATASOURCE_USERNAME=licoreria_user \
  -e SPRING_DATASOURCE_PASSWORD=licoreria_password \
  licoreria-backend:1.0
```

**Explicación:**
- `-p 8081:8081`: mapea puerto (tu máquina:contenedor)
- `-e`: variables de entorno
- `host.docker.internal`: referencia a tu máquina host

**Para Postgres en tu máquina**, usa `host.docker.internal` en lugar de `localhost`

### Testear el Frontend:

```bash
# En otra terminal
docker run -p 80:80 licoreria-frontend:1.0
```

Luego abre: http://localhost

---

## 📊 PASO 6: Ver logs del contenedor

```bash
# Backend
docker logs -f <container_id_backend>

# Frontend
docker logs -f <container_id_frontend>

# Obtener container_id
docker ps
```

---

## 🗑️ PASO 7: Limpiar

```bash
# Detener contenedor
docker stop <container_id>

# Eliminar contenedor
docker rm <container_id>

# Ver tamaño de imágenes
docker images

# Limpiar imágenes no usadas
docker image prune
```

---

## 📝 Próximo Paso: Docker Compose

Una vez que las imágenes funcionen, crearemos `docker-compose.yml` que:
- Levanta Backend + Frontend + PostgreSQL
- Con UN solo comando: `docker-compose up`

---

## ❓ Errores comunes y soluciones

### Error: "Cannot find Maven executable"
→ Asegúrate que tengas `pom.xml` en `licoreria-backend/`

### Error: "npm: command not found"
→ La imagen `node:20-alpine` no se descargó bien, intenta nuevamente

### Error: "Connection refused"
→ El Backend no está corriendo, usa el comando `docker run` primero

### Error: "Port already in use"
→ Algo ya corre en 8081 o 80
```bash
# Encuentra qué corre en el puerto
lsof -i :8081
# Detén ese proceso
```

---

## 📦 Ver contenido de la imagen

```bash
# Explorar archivo dentro de contenedor
docker run -it licoreria-backend:1.0 /bin/sh

# Dentro del contenedor
ls -la /app
cat /app/app.jar
```

---

¡Listo! Sigue estos pasos exactos y avísame si tienes errores.
