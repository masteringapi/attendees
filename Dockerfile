FROM maven:3.9-eclipse-temurin-17 AS builder
COPY src /usr/src/app/src
COPY pom.xml /usr/src/app
RUN mvn -f /usr/src/app/pom.xml clean package

FROM eclipse-temurin:21
RUN mkdir /opt/app
COPY --from=builder /usr/src/app/target/attendees-0.0.1-SNAPSHOT.jar /opt/app/app.jar
EXPOSE 8080
EXPOSE 9090
ENTRYPOINT [ "java", "-jar", "/opt/app/app.jar" ]