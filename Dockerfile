# Step 1: Build the application
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
# Copy the pom.xml and source code
COPY pom.xml .
COPY src ./src
# Build the JAR and skip tests to go faster on Render
RUN mvn clean package -DskipTests

# Step 2: Run the application
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
# Copy the JAR from the build step
COPY --from=build /app/target/*.jar app.jar
# Expose the port (Render uses 8080 by default for Spring Boot)
EXPOSE 8080
# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
