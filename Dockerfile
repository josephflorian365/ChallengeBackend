FROM eclipse-temurin:21.0.3_9-jdk
ARG JAR_FILE=build/libs/reto-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app_reto.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app_reto.jar"]