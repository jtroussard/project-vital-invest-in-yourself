# Stage 1: Build
FROM maven:3.9.9-eclipse-temurin-21-jammy AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests -B

# Stage 2: Run
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
RUN addgroup --system spring && adduser --system spring --ingroup spring
USER spring:spring
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080

# Run with the prod profile by default (overridable)
ENV SPRING_PROFILES_ACTIVE=prod
ENTRYPOINT ["java", "-Djava.net.preferIPv4Stack=true", "-Dsun.net.inetaddr.ttl=60", "-jar", "app.jar"]