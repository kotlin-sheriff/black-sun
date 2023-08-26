FROM gradle:jdk19 as builder
WORKDIR /project
COPY src ./src
COPY build.gradle.kts ./build.gradle.kts
COPY settings.gradle.kts ./settings.gradle.kts
RUN gradle clean build

FROM docker as java-dind
COPY --from=eclipse-temurin:20-jre-alpine /opt/java/openjdk/ /opt/java/openjdk/

FROM eclipse-temurin as backend
WORKDIR /root
COPY --from=builder /project/build/install/black-sun/ ./app/
WORKDIR /root/app/
ENTRYPOINT ["/root/app/bin/black-sun"]