# Build stage
FROM gradle:8.5-jdk21-alpine AS build
WORKDIR /app

# Copy source code
COPY . .

# Build the application
RUN gradle build -x test --no-daemon

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy the specific JAR file
COPY --from=build /app/build/libs/*.jar app.jar

# Create a non-root user
RUN addgroup -S spring && adduser -S spring -G spring
USER spring

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]