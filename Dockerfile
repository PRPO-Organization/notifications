FROM eclipse-temurin:17-jre-jammy

WORKDIR /app
COPY target/notification-service-1.0-SNAPSHOT.jar app.jar

EXPOSE 8081

# Environment variables
ENV DB_URL=jdbc:postgresql://notification-db:5432/notificationdb
ENV DB_USER=admin
ENV DB_PASSWORD=pass

ENTRYPOINT ["java","-jar","app.jar"]