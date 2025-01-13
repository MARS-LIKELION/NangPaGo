# Build JAR locally before building the Docker image
# Dockerfile
FROM --platform=linux/amd64 amazoncorretto:17-alpine as builder

# Set the working directory
WORKDIR /app

# Copy the Spring Boot application JAR
COPY build/libs/*.jar app.jar

# Runtime image
FROM amazoncorretto:17-alpine

# Set the working directory
WORKDIR /app

# Copy the JAR file from the builder stage
COPY --from=builder /app/app.jar app.jar

# Expose the port for the Spring Boot application
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
