# Build stage with Java 21
FROM gradle:8.5-jdk21 AS build
WORKDIR /app

# Copy gradle files first to cache dependencies
COPY build.gradle .
COPY settings.gradle .

# Download dependencies first (this layer will be cached)
RUN gradle dependencies --no-daemon

# Copy source code
COPY src ./src

# Build the application
RUN gradle build -x test --no-daemon --stacktrace

# Runtime stage with Java 21 JRE
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy the built JAR file
COPY --from=build /app/build/libs/*.jar app.jar

# Create a non-root user
RUN addgroup -S spring && adduser -S spring -G spring
USER spring

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]