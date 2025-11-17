# Etapa 1: Build com Maven
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

COPY . .

# Se você usa mvnw, troque "mvn" por "./mvnw"
RUN /.mvnw clean package -DskipTests

# Etapa 2: Runtime com JDK leve
FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

# copia o JAR gerado na etapa de build
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
