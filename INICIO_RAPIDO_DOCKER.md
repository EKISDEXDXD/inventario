# ⚡ PASOS RÁPIDOS: Dockerizar + Eliminar Antigua (5 min)

## 🎯 Opción Más Rápida (Recomendada)

### Paso 1: Dar permisos y ejecutar script
```bash
chmod +x /home/ekisde333/proyectos/project-inventario/docker-manager.sh
/home/ekisde333/proyectos/project-inventario/docker-manager.sh
```

### Paso 2: Selecciona en el menú
```
1  ← Construir (espera 15 min)
2  ← Iniciar
6  ← Verificar
4  ← Limpiar antiguas
0  ← Salir
```

**¡Listo! Tu app está dockerizada en 20 minutos.**

---

## ⏱️ Si Prefieres Hacerlo en Terminal (Copy-Paste)

### 1️⃣ Construir Backend
```bash
cd /home/ekisde333/proyectos/project-inventario/licoreria-backend
docker build -t licoreria-backend:2.0 .
```
⏳ *Espera 5-10 minutos*

### 2️⃣ Construir Frontend  
```bash
cd /home/ekisde333/proyectos/project-inventario/licoreria-frontend
docker build -t licoreria-frontend:2.0 .
```
⏳ *Espera 5-10 minutos*

### 3️⃣ Iniciar Contenedores
```bash
cd /home/ekisde333/proyectos/project-inventario
docker-compose down
docker-compose up -d
```
⏳ *Espera 30 segundos*

### 4️⃣ Verificar que Funciona
```bash
docker-compose ps
```
Debería ver 3 contenedores: `postgres`, `backend`, `frontend` — **STATUS: running**

### 5️⃣ Probar en el Navegador
- Frontend: http://localhost
- Backend: http://localhost:8081

### 6️⃣ Eliminar Versión Antigua (si existe)
```bash
docker rmi licoreria-backend:1.0 -f 2>/dev/null
docker rmi licoreria-frontend:1.0 -f 2>/dev/null
docker image prune -f
```

---

## ✅ ¿Qué Significan los Cambios?

| Concepto | Antes | Ahora |
|----------|-------|-------|
| **Ejecución** | En tu máquina local | En contenedores Docker |
| **Versión** | v1.0 (antigua) | v2.0 (nueva) |
| **Base de Datos** | Local/Externa | PostgreSQL en Docker |
| **Dependencias** | Debes instalar Node, Maven, etc | Todo en Docker |
| **Portabilidad** | Solo en tu máquina | Funciona en cualquier máquina |
| **Escalabilidad** | Difícil | Fácil (Kubernetes, etc) |

---

## 📋 Checklist Final

- [ ] Docker instalado: `docker --version`
- [ ] Images construidas: `docker images | grep licoreria`
- [ ] Contenedores corriendo: `docker-compose ps`
- [ ] Frontend accesible: http://localhost
- [ ] Backend accesible: http://localhost:8081
- [ ] Base de datos funciona
- [ ] Versión antigua eliminada
- [ ] Backups hechos

---

## 🆘 Si Algo Falla

```bash
# 1. Ver logs de error
docker-compose logs licoreria-backend
docker-compose logs licoreria-frontend
docker-compose logs postgres

# 2. Reiniciar todo
docker-compose down && docker-compose up -d

# 3. Ver si hay puertos en conflicto
sudo lsof -i :80
sudo lsof -i :8081
sudo lsof -i :5432

# 4. Limpiar y reconstruir
docker system prune -a
docker build -t licoreria-backend:2.0 ./licoreria-backend
docker build -t licoreria-frontend:2.0 ./licoreria-frontend
docker-compose up -d
```

---

**Tiempo total estimado: 20-30 minutos**  
**Complejidad: ⭐ Baja**  
**Riesgo: 🟢 Bajo (no elimina datos si tienes backup)**
