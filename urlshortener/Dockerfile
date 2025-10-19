# Stage de desenvolvimento
FROM eclipse-temurin:21-jdk-alpine AS builder

# Instalar Maven
RUN apk add --no-cache maven

# Configurar Maven para otimização
ENV MAVEN_OPTS="-Xmx1g -XX:+UseG1GC -XX:+UseStringDeduplication"
ENV MAVEN_CONFIG=/root/.m2

WORKDIR /app

# Copiar apenas o pom.xml primeiro para aproveitar cache de dependências
COPY pom.xml .

# Download de dependências com cache otimizado e configurações de performance
RUN mvn dependency:go-offline -B \
    -Dmaven.repo.local=/root/.m2/repository \
    -Dmaven.artifact.threads=4 \
    -Dmaven.wagon.http.retryHandler.count=3 \
    -Dmaven.wagon.http.retryHandler.requestSentEnabled=true

# Copiar código fonte
COPY src src

# Build otimizado com paralelização, skip de testes e configurações de performance
RUN mvn clean package -DskipTests \
    -T 2C \
    -Dmaven.repo.local=/root/.m2/repository \
    -Dmaven.compiler.fork=true \
    -Dmaven.compiler.maxmem=1g \
    -Dmaven.test.skip=true \
    -Dmaven.javadoc.skip=true \
    -Dmaven.source.skip=true

# Stage de produção
FROM eclipse-temurin:21-jre-alpine AS runtime

RUN apk add --no-cache curl

RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup

WORKDIR /app

# Copiar o JAR específico gerado pelo Spring Boot
COPY --from=builder /app/target/urlshortener-*.jar app.jar

RUN mkdir -p /app/logs && \
    chown -R appuser:appgroup /app

USER appuser

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+UseG1GC -XX:+UseStringDeduplication"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
