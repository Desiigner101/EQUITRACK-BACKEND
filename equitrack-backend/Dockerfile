FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/equitrack-backend-0.0.1-SNAPSHOT.jar equitrack-v1.0.jar
EXPOSE 4074
ENTRYPOINT ["java", "-jar", "equitrack-v1.0.jar"]