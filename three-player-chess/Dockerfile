FROM gradle:8.5-jdk17 AS build
WORKDIR /home/gradle/project

COPY gradlew settings.gradle ./
COPY gradle gradle
COPY chess-core/build.gradle chess-core/build.gradle
COPY three-player-service/build.gradle three-player-service/build.gradle

RUN gradle dependencies --no-daemon

COPY chess-core chess-core
COPY three-player-service three-player-service

RUN gradle :three-player-service:bootJar --no-daemon

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /home/gradle/project/three-player-service/build/libs/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","app.jar"]