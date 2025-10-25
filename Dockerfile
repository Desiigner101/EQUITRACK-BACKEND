FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY equitrack-backend/pom.xml .
COPY equitrack-backend/src ./src
COPY equitrack-backend/.mvn ./.mvn
COPY equitrack-backend/mvnw .
COPY equitrack-backend/mvnw.cmd .
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/equitrack-backend-0.0.1-SNAPSHOT.jar equitrack-v1.0.jar
EXPOSE 4074
ENTRYPOINT ["java", "-jar", "equitrack-v1.0.jar"]