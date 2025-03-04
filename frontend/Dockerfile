FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests

# Customize JRE
RUN jar xf target/frontend-*.jar

RUN jdeps --ignore-missing-deps -q  \
    --recursive  \
    --multi-release 21  \
    --print-module-deps  \
    --class-path 'BOOT-INF/lib/*'  \
    target/frontend-*.jar > deps.info

RUN jlink \
    --add-modules $(cat deps.info) \
    --strip-debug \
    --compress 2 \
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
COPY --from=builder /app/target/frontend-*.jar /app/frontend.jar

EXPOSE 8090

ENTRYPOINT ["java", "-jar", "/app/frontend.jar"]
