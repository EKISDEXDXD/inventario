# ============ ETAPA 1: BUILD ============
# Usamos Maven 3.9 con JDK 21 para compilar
FROM maven:3.9-eclipse-temurin-21 AS builder

# Establecemos el directorio de trabajo
WORKDIR /app

# Copiamos los archivos de configuración de Maven
COPY pom.xml .

# Copiamos el código fuente
COPY src ./src

# Compilamos el proyecto (genera el JAR)
RUN mvn clean package -DskipTests

# ============ ETAPA 2: RUNTIME ============
# Usamos JDK 21 (más ligero que el Maven anterior)
FROM eclipse-temurin:21-jre-alpine

# Establecemos el directorio de trabajo
WORKDIR /app

# Copiamos el JAR compilado desde la etapa anterior
COPY --from=builder /app/target/*.jar app.jar

# Exponemos el puerto 8081
EXPOSE 8081

# Variable de entorno para la BD
ENV SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/licoreria_db
ENV SPRING_DATASOURCE_USERNAME=licoreria_user
ENV SPRING_DATASOURCE_PASSWORD=licoreria_password
ENV SPRING_JPA_HIBERNATE_DDL_AUTO=update

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
