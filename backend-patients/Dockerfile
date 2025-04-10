FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests

RUN jar xf target/backend-patients-*.jar

RUN jdeps --ignore-missing-deps -q \
    --recursive \
    --multi-release 21 \
    --print-module-deps \
    --class-path 'BOOT-INF/lib/*' \
    target/backend-patients-*.jar > deps.info

RUN jlink \
    --add-modules $(cat deps.info) \
    --strip-debug \
    --compress zip-6 \
    --no-header-files \
    --no-man-pages \
    --output /custom-jre

FROM debian:trixie-slim

RUN apt update && \
    apt install -y --no-install-recommends ca-certificates curl && \
    rm -rf /var/lib/apt/lists/* && \
    groupadd -r prevendia && \
    useradd -r -g prevendia -s /bin/false prevendia

ENV JAVA_HOME /opt/java/jdk21
ENV PATH $JAVA_HOME/bin:$PATH

COPY --from=builder /custom-jre $JAVA_HOME
COPY --from=builder /app/target/backend-patients-*.jar /app/backend-patients.jar

WORKDIR /app
RUN chown -R prevendia:prevendia /app

EXPOSE 8081

USER prevendia

ENTRYPOINT ["java", "-jar", "/app/backend-patients.jar"]
