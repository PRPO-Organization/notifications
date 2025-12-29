# Build stage
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /build

# Copy pom.xml and resolve dependencies first (cache-friendly)
COPY pom.xml .
RUN mvn -q dependency:go-offline

# Copy project sources
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests


# Runtime stage
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app
COPY --from=build /build/target/notification-service-1.0-SNAPSHOT.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java","-jar","app.jar"]
