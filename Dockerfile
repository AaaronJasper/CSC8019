# ---------- Dependency Cache Stage ----------
FROM maven:3.9.9-eclipse-temurin-21 AS deps

WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B

# ---------- Run Stage ----------
FROM maven:3.9.9-eclipse-temurin-21

WORKDIR /app

# Pre-populate Maven cache so mvn spring-boot:run doesn't re-download deps
COPY --from=deps /root/.m2 /root/.m2

# Source code is provided via volume mount in docker-compose (for hot reload)
COPY pom.xml .

EXPOSE 8080
CMD ["mvn", "spring-boot:run"]