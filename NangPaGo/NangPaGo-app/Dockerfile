# Stage 1: Build React application
FROM node:23-alpine AS frontend-builder
WORKDIR /frontend
COPY frontend/package*.json ./
RUN npm install
COPY frontend/ ./
RUN npm run build

# Stage 2: Build final image with Spring Boot
FROM --platform=linux/amd64 amazoncorretto:17-alpine as builder
WORKDIR /app
COPY *.jar app.jar

# Copy React build files to Spring Boot's static directory
COPY --from=frontend-builder /frontend/dist /app/static

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
