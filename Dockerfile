
FROM maven:3.9.6-eclipse-temurin-17-alpine AS build

WORKDIR /app


COPY pom.xml .


RUN mvn dependency:go-offline -B


COPY src ./src

RUN mvn clean package -DskipTests


FROM gcr.io/distroless/java17-debian12

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]