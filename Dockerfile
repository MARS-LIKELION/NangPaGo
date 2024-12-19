# 1. OpenJDK 기반의 ARM64 호환 이미지 사용
FROM eclipse-temurin:17-jdk-alpine as builder

# 2. 작업 디렉토리 설정
WORKDIR /app

# 3. Gradle 캐시 활용을 위해 Gradle Wrapper와 build.gradle 파일 복사
COPY gradlew ./
COPY gradle gradle
COPY build.gradle.kts settings.gradle.kts ./

# 4. 종속성 다운로드
RUN ./gradlew dependencies --no-daemon

# 5. 애플리케이션 소스 복사 및 빌드
COPY src src
RUN ./gradlew build --no-daemon

# 6. 실행 환경을 위한 슬림 이미지 사용
FROM eclipse-temurin:17-jre-alpine

# 7. 작업 디렉토리 설정
WORKDIR /app

# 8. 빌드된 파일 복사
COPY --from=builder /app/build/libs/*.jar app.jar

# 9. 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
