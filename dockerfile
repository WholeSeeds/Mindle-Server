# 1단계: Eclipse Temurin 17 환경에서 빌드
FROM eclipse-temurin:17-jdk AS builder

# gradlew 내 JAVA_HOME 검사 비활성화
ENV JAVA_HOME=""

WORKDIR /workspace
COPY . .

RUN chmod +x gradlew \
 && ./gradlew clean build --no-daemon

# 2단계: 런타임만 추출
FROM eclipse-temurin:17-jdk

WORKDIR /app
COPY --from=builder /workspace/build/libs/*.jar app.jar

ENTRYPOINT ["java","-jar","app.jar"]
