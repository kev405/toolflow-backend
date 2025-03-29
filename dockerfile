# Etapa 1: Compilar la aplicación usando Maven con Java 17
FROM maven:3.8.6-openjdk-18-slim AS build

# Establecer el directorio de trabajo
WORKDIR /app

# Copiar el archivo pom.xml y descargar las dependencias
COPY pom.xml .
RUN mvn dependency:go-offline

# Copiar el código fuente de la aplicación
COPY src ./src

# Empaquetar la aplicación, omitiendo los tests (ajusta si deseas ejecutarlos)
RUN mvn clean package -DskipTests

# Etapa 2: Crear la imagen final usando OpenJDK 17
FROM openjdk:18-slim

# Establecer el directorio de trabajo
WORKDIR /app

# Copiar el JAR generado en la etapa anterior (ajusta el nombre del JAR si es necesario)
COPY --from=build /app/target/toolflow-api.jar .

# Exponer el puerto en el que se ejecutará la API (por defecto Spring Boot usa el 8080)
EXPOSE 9009

# Comando para ejecutar la aplicación
CMD ["java", "-jar", "toolflow-api.jar"]

