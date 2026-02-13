FROM maven:3.9.9-eclipse-temurin-21 AS build 
WORKDIR /team-project
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src

RUN mvn test

RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /team-project

COPY --from=build /team-project/target/*.jar app.jar

EXPOSE 8080
 
ENTRYPOINT ["java", "-jar", "app.jar"]