# 빌드 스테이지
FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /workspace

# gradle wrapper 먼저 복사
COPY gradlew gradlew.bat ./
COPY gradle/wrapper/ gradle/wrapper/
RUN chmod +x gradlew && ./gradlew --no-daemon --version

# 빌드 스크립트 → 소스 순서로 복사
COPY settings.gradle* build.gradle* gradle.properties* ./
COPY . .

# 저사양 빌드 옵션
ENV JAVA_TOOL_OPTIONS="-XX:+UseSerialGC -Xmx1g -XX:MaxMetaspaceSize=256m -XX:ActiveProcessorCount=1 -XX:CICompilerCount=1 -Dfile.encoding=UTF-8"
ENV GRADLE_OPTS="-Dorg.gradle.workers.max=1 -Dorg.gradle.parallel=false"

RUN ./gradlew clean build -x test --no-daemon --max-workers=1 --no-parallel

# 최신(non-plain) JAR 하나만 app.jar로 확정
RUN set -eux; \
    JAR_FILE="$(ls -1t build/libs/*.jar | grep -v 'plain' | head -n1)"; \
    test -n "$JAR_FILE"; \
    cp "$JAR_FILE" app.jar

# 런타임 스테이지
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /workspace/app.jar app.jar
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75.0 -Dfile.encoding=UTF-8"
ENTRYPOINT ["java","-jar","/app/app.jar"]
