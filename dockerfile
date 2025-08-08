# 빌드 스테이지
FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /workspace

# 1) wrapper 먼저 복사 + 실행 확인
COPY gradlew gradlew.bat ./
COPY gradle/wrapper/ gradle/wrapper/
RUN chmod +x gradlew && ./gradlew --no-daemon --version

# 2) 빌드 스크립트 → 소스 순서로 복사 후 빌드
COPY settings.gradle* build.gradle* gradle.properties* ./
COPY . .
RUN ./gradlew clean build -x test --no-daemon --max-workers=1 --no-parallel

# 런타임 스테이지
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /workspace/build/libs/*.jar app.jar
ENTRYPOINT ["java","-jar","/app/app.jar"]
