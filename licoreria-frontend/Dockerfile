# ============ ETAPA 1: BUILD ============
FROM node:22-alpine AS builder

WORKDIR /app

# Aumentar límite de memoria de Node
ENV NODE_OPTIONS="--max-old-space-size=4096"

# Copiar archivos de dependencias
COPY package*.json ./

# Instalar con configuraciones optimizadas
RUN npm ci --legacy-peer-deps --prefer-offline --no-audit

# Copiar código fuente
COPY . .

# Compilar Angular para producción con más memoria
RUN npm run build -- --configuration production

# ============ ETAPA 2: RUNTIME ============
FROM nginx:alpine

# Copiar la configuración personalizada de Nginx
COPY nginx.conf /etc/nginx/nginx.conf

# Copiar los archivos compilados desde la etapa anterior
COPY --from=builder /app/dist/licoreria-frontend/browser /usr/share/nginx/html

# Crear archivo .htaccess para Angular routing
RUN echo '<IfModule mod_rewrite.c>\n  RewriteEngine On\n  RewriteBase /\n  RewriteRule ^index\.html$ - [L]\n  RewriteCond %{REQUEST_FILENAME} !-f\n  RewriteCond %{REQUEST_FILENAME} !-d\n  RewriteRule . /index.html [L]\nIfModule>' > /usr/share/nginx/html/.htaccess

# Exponemos el puerto 80
EXPOSE 80

# Nginx se ejecuta por defecto
CMD ["nginx", "-g", "daemon off;"]

