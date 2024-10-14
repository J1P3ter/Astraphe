FROM gradle:8.10.1-jdk17 AS build
ARG FILE_DIRECTORY
WORKDIR /app${FILE_DIRECTORY}
COPY .${FILE_DIRECTORY} /app${FILE_DIRECTORY}
COPY gradlew /app/gradlew
COPY gradle /app/gradle
COPY build.gradle /app/build.gradle
COPY settings.gradle /app/settings.gradle
COPY ./common /app/common
RUN gradle clean bootJar


FROM openjdk:17-jdk-slim
ARG FILE_DIRECTORY
COPY --from=build /app$FILE_DIRECTORY/build/libs/*SNAPSHOT.jar ./app.jar
CMD ["java", "-jar", "app.jar"]
