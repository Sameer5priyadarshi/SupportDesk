# BUILD STAGE 

FROM eclipse-temurin:17-jdk-alpine AS builder 

# Set working directory inside the container

WORKDIR /app 

# Copy Maven or Gradle build files first (for caching dependencies) 
COPY pom.xml mvnw ./ 
COPY .mvn .mvn 
RUN ./mvnw dependency:go-offline 

# Copy the entire project source 
COPY src src 

# Build the application JAR 
RUN ./mvnw clean package -DskipTests 

# RUN STAGE 

FROM eclipse-temurin:17-jdk-alpine 

# Set working directory for the runtime container 
WORKDIR /app 

# Copy only the built JAR from the builder stage 
COPY --from=builder /app/target/*.jar app.jar 

# Expose the application port 
EXPOSE 8080 

# Start the application 
ENTRYPOINT ["java", "-jar", "/app/app.jar"]