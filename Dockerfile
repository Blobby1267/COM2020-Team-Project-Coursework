FROM maven:3.9.9-eclipse-temurin-21 AS base
WORKDIR /team-project
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src

# --- TEST STAGE ---
FROM base AS test
CMD ["mvn", "test"]

# --- BUILD STAGE ---
FROM base AS build
RUN mvn clean package -DskipTests

# --- PRODUCTION STAGE ---
FROM eclipse-temurin:21-jre-alpine
WORKDIR /team-project
COPY --from=build /team-project/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]