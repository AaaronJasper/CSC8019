# ---------- Build Stage ----------
FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline


# ---------- Run Stage ----------
FROM maven:3.9.9-eclipse-temurin-21

WORKDIR /app

COPY --from=builder /root/.m2 /root/.m2

CMD ["mvn", "spring-boot:run"]