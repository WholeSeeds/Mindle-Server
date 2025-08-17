# 1단계: 빌드 스테이지
FROM eclipse-temurin:17-jdk AS builder
ENV JAVA_HOME="" \
    GRADLE_OPTS="-Dorg.gradle.jvmargs=-Xmx1536m"

WORKDIR /workspace
COPY . .
RUN chmod +x gradlew \
 && ./gradlew clean build --no-daemon --max-workers=8

# 2단계: 런타임 스테이지
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=builder /workspace/build/libs/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]
