# ---------- Build Stage ----------
FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /app

# copy project
COPY pom.xml .
COPY src ./src

# build jar
RUN mvn clean package -DskipTests


# ---------- Run Stage ----------
FROM eclipse-temurin:21-jdk

WORKDIR /app

# copy jar from builder
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]