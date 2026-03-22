FROM gradle:8.5-jdk17 AS build
WORKDIR /home/gradle/project

# 1. Copy Gradle setup files (Note: build.gradle removed from here as it lives in submodules)
COPY gradlew settings.gradle ./
COPY gradle gradle

# 2. Copy module build files
COPY chess-core/build.gradle chess-core/build.gradle
COPY two-player-service/build.gradle two-player-service/build.gradle

# 3. Download dependencies
RUN gradle dependencies --no-daemon

# 4. Copy Source Code
COPY . .

# 5. Build the 2-player service
RUN gradle :two-player-service:bootJar --no-daemon

# 6. Run Stage
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /home/gradle/project/two-player-service/build/libs/*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java","-jar","app.jar"]