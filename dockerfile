# 1단계: 소스 빌드해서 jar 파일 생성
FROM eclipse-temurin:17-jdk AS builder
WORKDIR /workspace
COPY . .
RUN chmod +x gradlew \
 && ./gradlew clean build --no-daemon

# 2단계: 런타임만 추출
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=builder /workspace/build/libs/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]
