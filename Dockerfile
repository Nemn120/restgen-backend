FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml ./
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]

# ------------------------------------- COMANDOS ----------------------------------------------------------
# Construir la imagen, ATENCION!!! existe un punto al final que se debe incluir
#> docker build -t deployed-aws .

# Crea y arrancar el contenedor a partir de la imagen
#> docker run -d --name deployed-aws-app  -p 8080:8080 deployed-aws

