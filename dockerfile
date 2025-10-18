FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app

# Copy source code
COPY . .

# Build and run in one stage
RUN ./gradlew build -x test --no-daemon

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "build/libs/*.jar"]