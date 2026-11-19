FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY . .
RUN sed -i 's/\r$//' gradlew && chmod +x gradlew && ./gradlew bootJar -x test --no-daemon

FROM eclipse-temurin:21-jre
COPY --from=build /app/build/libs/*-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]