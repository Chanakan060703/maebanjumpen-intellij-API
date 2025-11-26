# Stage 1: Build
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copy Maven files first for better caching
COPY pom.xml ./
COPY mvnw ./
COPY .mvn ./.mvn

# Download dependencies (this layer will be cached if dependencies don't change)
RUN mvn dependency:go-offline -B || true

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Create directories for uploads and qr_codes
RUN mkdir -p /app/uploads/verify_photos /app/uploads/progression_images /app/qr_codes

# Copy the built jar from build stage
COPY --from=build /app/target/*.jar app.jar

# Expose port
EXPOSE 8088

# Set JVM options for better container performance
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
