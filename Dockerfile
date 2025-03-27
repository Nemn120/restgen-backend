FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml ./
COPY generator/pom.xml generator/
RUN mvn dependency:go-offline -B
COPY generator/src generator/src
WORKDIR /app/generator
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=build /app/generator/target/*.jar app.jar

EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
