# syntax=docker/dockerfile:1

FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /workspace

COPY . .

RUN mvn -ntp package -Pprod -DskipTests


FROM eclipse-temurin:21-jre AS runtime

WORKDIR /app

RUN useradd --system --uid 10001 --create-home detallsublim

COPY --from=builder \
    /workspace/target/detall-sublim-0.0.1-SNAPSHOT.jar \
    /app/app.jar

COPY docker-entrypoint.sh /usr/local/bin/docker-entrypoint.sh

RUN sed -i 's/\r$//' /usr/local/bin/docker-entrypoint.sh && \
    chmod +x /usr/local/bin/docker-entrypoint.sh && \
    mkdir -p /data/historias && \
    chown -R detallsublim:detallsublim /app /data/historias

USER detallsublim

EXPOSE 8080

ENV SPRING_PROFILES_ACTIVE=prod
ENV APPLICATION_STORAGE_HISTORIAS_LOCATION=/data/historias

ENTRYPOINT ["/usr/local/bin/docker-entrypoint.sh"]

CMD ["java", "-jar", "/app/app.jar"]