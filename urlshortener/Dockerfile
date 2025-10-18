# Stage de desenvolvimento
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .

RUN ./mvnw dependency:go-offline -B

COPY src src

RUN ./mvnw clean package -DskipTests

# Stage de produção
FROM eclipse-temurin:21-jre-alpine AS runtime

RUN apk add --no-cache curl

RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

RUN mkdir -p /app/logs && \
    chown -R appuser:appgroup /app

USER appuser

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+UseG1GC -XX:+UseStringDeduplication"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
