FROM gradle:jdk19 as builder
WORKDIR /project
COPY src ./src
COPY build.gradle.kts ./build.gradle.kts
COPY settings.gradle.kts ./settings.gradle.kts
RUN gradle clean build

FROM eclipse-temurin as backend
WORKDIR /root
COPY --from=builder /project/build/install/red-sun/ ./app/
WORKDIR /root/app/
ENTRYPOINT ["/root/app/bin/red-sun"]