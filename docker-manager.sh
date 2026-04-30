#!/bin/bash

# ============================================
# SCRIPT: Dockerizar Nueva Versión
# ============================================
# Este script construye, inicia y gestiona los contenedores Docker

set -e  # Salir si hay algún error

PROJECT_DIR="/home/ekisde333/proyectos/project-inventario"
BACKEND_DIR="$PROJECT_DIR/licoreria-backend"
FRONTEND_DIR="$PROJECT_DIR/licoreria-frontend"

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# ============================================
# FUNCIONES AUXILIARES
# ============================================

print_header() {
    echo -e "\n${BLUE}========================================${NC}"
    echo -e "${BLUE}$1${NC}"
    echo -e "${BLUE}========================================${NC}\n"
}

print_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠ $1${NC}"
}

print_error() {
    echo -e "${RED}✗ $1${NC}"
}

# ============================================
# OPCIÓN 1: CONSTRUCCIÓN COMPLETA
# ============================================

build_new_version() {
    print_header "CONSTRUCCIÓN: Nueva Versión (v2.0)"
    
    print_warning "Esta operación tardará 10-15 minutos la primera vez"
    
    # Construir Backend
    print_header "Compilando BACKEND..."
    cd "$BACKEND_DIR"
    docker build -t licoreria-backend:2.0 .
    print_success "Backend compilado: licoreria-backend:2.0"
    
    # Construir Frontend
    print_header "Compilando FRONTEND..."
    cd "$FRONTEND_DIR"
    docker build -t licoreria-frontend:2.0 .
    print_success "Frontend compilado: licoreria-frontend:2.0"
    
    print_header "CONSTRUCCIÓN COMPLETADA ✓"
}

# ============================================
# OPCIÓN 2: INICIAR CONTENEDORES
# ============================================

start_containers() {
    print_header "INICIANDO CONTENEDORES..."
    
    cd "$PROJECT_DIR"
    
    print_warning "Detener contenedores antiguos..."
    docker-compose down 2>/dev/null || true
    
    print_warning "Iniciando docker-compose..."
    docker-compose up -d
    
    sleep 3
    
    print_header "ESTADO DE CONTENEDORES"
    docker-compose ps
    
    print_success "Contenedores iniciados"
    print_header "URLs DE ACCESO"
    echo -e "  ${BLUE}Frontend:${NC}  http://localhost"
    echo -e "  ${BLUE}Backend:${NC}   http://localhost:8081"
    echo -e "  ${BLUE}Database:${NC}  localhost:5432"
}

# ============================================
# OPCIÓN 3: DETENER CONTENEDORES
# ============================================

stop_containers() {
    print_header "DETENIENDO CONTENEDORES..."
    
    cd "$PROJECT_DIR"
    docker-compose down
    
    print_success "Contenedores detenidos"
}

# ============================================
# OPCIÓN 4: LIMPIAR VERSIONES ANTIGUAS
# ============================================

cleanup_old_versions() {
    print_header "LIMPIEZA: Eliminando Versiones Antiguas"
    
    # Detener contenedores
    print_warning "Deteniendo contenedores..."
    docker-compose down 2>/dev/null || true
    
    # Listar imágenes antiguas
    echo -e "\n${BLUE}Imágenes encontradas:${NC}"
    docker images | grep licoreria || echo "Ninguna imagen de licoreria"
    
    # Eliminar imágenes v1.0 si existen
    print_warning "Eliminando versión antigua (v1.0)..."
    docker rmi licoreria-backend:1.0 -f 2>/dev/null || true
    docker rmi licoreria-frontend:1.0 -f 2>/dev/null || true
    
    # Limpiar imágenes colgadas
    print_warning "Limpiando imágenes intermedias..."
    docker image prune -f --filter "label!=keep"
    
    # Mostrar espacio liberado
    print_header "LIMPIEZA COMPLETADA"
    echo -e "${BLUE}Imágenes restantes:${NC}"
    docker images | grep licoreria || echo "Ninguna imagen de licoreria"
}

# ============================================
# OPCIÓN 5: VER LOGS
# ============================================

view_logs() {
    print_header "LOGS DE CONTENEDORES"
    
    read -p "¿Qué servicio deseas ver? (backend/frontend/postgres/todos): " service
    
    cd "$PROJECT_DIR"
    
    case $service in
        backend)
            docker-compose logs -f licoreria-backend
            ;;
        frontend)
            docker-compose logs -f licoreria-frontend
            ;;
        postgres)
            docker-compose logs -f postgres
            ;;
        todos)
            docker-compose logs -f
            ;;
        *)
            print_error "Opción no válida"
            ;;
    esac
}

# ============================================
# OPCIÓN 6: VERIFICAR ESTADO
# ============================================

check_status() {
    print_header "ESTADO GENERAL"
    
    echo -e "${BLUE}CONTENEDORES:${NC}"
    docker-compose ps 2>/dev/null || echo "No hay contenedores corriendo"
    
    echo -e "\n${BLUE}IMÁGENES:${NC}"
    docker images | grep licoreria || echo "No hay imágenes de licoreria"
    
    echo -e "\n${BLUE}ESPACIOS EN DISCO:${NC}"
    docker system df | head -5
    
    print_success "Estado verificado"
}

# ============================================
# MENÚ PRINCIPAL
# ============================================

show_menu() {
    echo -e "\n${BLUE}╔════════════════════════════════════════╗${NC}"
    echo -e "${BLUE}║  GESTOR DOCKER - LICORERIA v2.0       ║${NC}"
    echo -e "${BLUE}╚════════════════════════════════════════╝${NC}\n"
    
    echo "1️⃣  Construir nueva versión (v2.0)"
    echo "2️⃣  Iniciar contenedores"
    echo "3️⃣  Detener contenedores"
    echo "4️⃣  Limpiar versiones antiguas"
    echo "5️⃣  Ver logs"
    echo "6️⃣  Verificar estado"
    echo "0️⃣  Salir"
    echo ""
}

# ============================================
# SCRIPT PRINCIPAL
# ============================================

main() {
    # Verificar que Docker está instalado
    if ! command -v docker &> /dev/null; then
        print_error "Docker no está instalado"
        exit 1
    fi
    
    # Menú interactivo
    while true; do
        show_menu
        read -p "Selecciona una opción: " option
        
        case $option in
            1)
                build_new_version
                ;;
            2)
                start_containers
                ;;
            3)
                stop_containers
                ;;
            4)
                cleanup_old_versions
                ;;
            5)
                view_logs
                ;;
            6)
                check_status
                ;;
            0)
                print_success "¡Hasta luego!"
                exit 0
                ;;
            *)
                print_error "Opción no válida"
                ;;
        esac
    done
}

# Ejecutar script
main "$@"
