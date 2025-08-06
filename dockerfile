# 1단계: 소스 → 빌드
FROM gradle:7.5.1-jdk17 AS builder
WORKDIR /workspace
COPY . .
RUN gradle clean build --no-daemon

# 2단계: 런타임 이미지
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=builder /workspace/build/libs/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]
