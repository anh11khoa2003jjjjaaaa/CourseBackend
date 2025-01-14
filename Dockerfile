# Build Stage
FROM maven:3-openjdk-17 AS build

WORKDIR /app

# Copy toàn bộ mã nguồn vào container
COPY . .

# Build ứng dụng và tạo file .war
RUN mvn clean package -DskipTests

# Run Stage
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy file .war từ build stage
COPY --from=build /app/target/SellingCourese-0.0.1-SNAPSHOT.war app.war

# Expose port
EXPOSE 8080

# Run ứng dụng
ENTRYPOINT ["java", "-jar", "app.war"]
