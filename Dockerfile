# syntax=docker/dockerfile:1

FROM eclipse-temurin:21-jdk AS build
WORKDIR /workspace

COPY gradlew ./
COPY gradle ./gradle
COPY build.gradle.kts settings.gradle.kts ./
RUN chmod +x gradlew

# 소스 변경 시에도 의존성 레이어 캐시를 재사용
RUN --mount=type=cache,target=/root/.gradle ./gradlew dependencies --no-daemon || true

COPY src ./src
RUN --mount=type=cache,target=/root/.gradle ./gradlew bootJar --no-daemon -x test \
    && cp "$(ls build/libs/*.jar | grep -v plain | head -1)" /workspace/app.jar

FROM eclipse-temurin:21-jre-alpine AS runtime
RUN addgroup -g 1001 -S calto && adduser -S calto -u 1001 -G calto
WORKDIR /app
COPY --from=build --chown=calto:calto /workspace/app.jar app.jar
USER 1001
EXPOSE 8080
HEALTHCHECK --interval=15s --timeout=5s --start-period=60s --retries=5 \
    CMD wget -qO- http://localhost:8080/health || exit 1
ENTRYPOINT ["java", "-jar", "app.jar"]
