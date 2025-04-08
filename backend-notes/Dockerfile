FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests

# Customize JRE
RUN jar xf target/backend-notes-*.jar

RUN jdeps --ignore-missing-deps -q  \
    --recursive  \
    --multi-release 21  \
    --print-module-deps  \
    --class-path 'BOOT-INF/lib/*'  \
    target/backend-notes-*.jar > deps.info

RUN jlink \
    --add-modules $(cat deps.info) \
    --strip-debug \
    --compress zip-6 \
    --no-header-files \
    --no-man-pages \
    --output /custom-jre

# Create image
FROM debian:bookworm-slim

RUN apt-get update && \
    apt-get install -y --no-install-recommends ca-certificates && \
    rm -rf /var/lib/apt/lists/*

ENV JAVA_HOME /opt/java/jdk21
ENV PATH $JAVA_HOME/bin:$PATH

COPY --from=builder /custom-jre $JAVA_HOME
COPY --from=builder /app/target/backend-notes-*.jar /app/backend-notes.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "/app/backend-notes.jar"]
