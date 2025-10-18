FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
RUN apk add --no-cache bash
COPY . .
RUN chmod +x gradlew && ./gradlew build -x test --no-daemon
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "build/libs/*.jar"]
