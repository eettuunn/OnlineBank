FROM adoptopenjdk/openjdk11:latest AS build

# Установка Maven
RUN apt-get update && \
    apt-get install -y maven

WORKDIR /app
COPY . .
RUN mvn clean package

FROM adoptopenjdk/openjdk11:latest
ARG JAR_FILE=target/*.jar
COPY --from=build /app/${JAR_FILE} application.jar
ENTRYPOINT ["java", "-jar", "application.jar"]
