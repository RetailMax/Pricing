# Stage 1: Build the application using Maven and Java 22
# Usa una imagen base de Maven con Eclipse Temurin JDK 22 para la compilación.
# Esta imagen es adecuada para proyectos Java 22.
FROM maven:3.9.6-eclipse-temurin-22 AS build

# Establece el directorio de trabajo dentro del contenedor para la etapa de construcción
WORKDIR /app

# Copia el archivo pom.xml primero para aprovechar el caché de Docker.
# Si solo cambian los archivos de código fuente, Maven no descargará las dependencias de nuevo.
COPY pom.xml .

# Copia el código fuente de la aplicación
COPY src ./src

# Ejecuta la compilación de Maven.
# El flag -DskipTests se usa para saltar los tests durante la compilación de la imagen,
# lo que acelera la construcción. Puedes removerlo si necesitas que los tests se ejecuten.
RUN mvn clean package -DskipTests

# Stage 2: Create the final lightweight runtime image
# Usa una imagen base de OpenJDK 22, ligera y optimizada para el tiempo de ejecución.
FROM openjdk:22-jdk-slim

# Establece el directorio de trabajo dentro del contenedor para la aplicación final
WORKDIR /app

# Copia el archivo JAR compilado desde la etapa de construcción anterior.
# El asterisco (*) asegura que se copie cualquier JAR que se genere en 'target/'.
# Asume que tu aplicación genera un solo JAR ejecutable.
COPY --from=build /app/target/*.jar app.jar

# Copia el directorio 'wallet' que contiene los archivos de conexión de Oracle.
# Este directorio debe estar en la raíz de tu proyecto local.
COPY ./wallet /app/wallet 

# Establece la variable de entorno TNS_ADMIN.
# Esta variable es utilizada por el controlador JDBC de Oracle para encontrar el wallet.
ENV TNS_ADMIN=/app/wallet

# Expone el puerto en el que la aplicación Spring Boot escuchará.
# Esto es una declaración de qué puerto se usa, no lo publica aún en el host.
EXPOSE 8080

# Define el comando que se ejecutará cuando el contenedor se inicie.
# "-Doracle.net.tns_admin=/app/wallet" le dice a la JVM dónde encontrar los archivos del wallet
# para la conexión a la base de datos Oracle.
ENTRYPOINT ["java", "-Doracle.net.tns_admin=/app/wallet", "-jar", "app.jar"]