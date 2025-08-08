# 1단계: 빌드 (alpine 사용)
FROM eclipse-temurin:17-jdk-alpine AS builder

# 불필요: JAVA_HOME 빈값 설정 제거
# ENV JAVA_HOME=""

# JVM/Gradle 자원 최소화
ENV JAVA_TOOL_OPTIONS="-XX:+UseSerialGC -Xmx1g -XX:MaxMetaspaceSize=256m -XX:ActiveProcessorCount=1 -XX:CICompilerCount=1 -Dfile.encoding=UTF-8"
ENV GRADLE_OPTS="-Dorg.gradle.workers.max=1 -Dorg.gradle.parallel=false"

WORKDIR /workspace
# wrapper 먼저 복사해 캐시 활용
COPY gradlew gradle/ ./
RUN chmod +x gradlew
# 빌드 스크립트만 먼저 복사 (의존성 캐시)
COPY settings.gradle* build.gradle* gradle.properties* ./
RUN ./gradlew --no-daemon --version

# 소스 마지막에 복사
COPY . .

# 테스트는 일단 제외(메모리 절약)
RUN ./gradlew clean build -x test --no-daemon --max-workers=1 --no-parallel

# 2단계: 런타임
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /workspace/build/libs/*.jar app.jar
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75.0 -Dfile.encoding=UTF-8"
ENTRYPOINT ["java","-jar","/app/app.jar"]
