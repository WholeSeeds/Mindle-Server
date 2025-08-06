FROM eclipse-temurin:17-jdk

# 실제 한 개의 JAR 파일 이름으로 지정
ARG JAR_FILE=build/libs/myapp.jar

COPY ${JAR_FILE} /app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
