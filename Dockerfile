FROM openjdk:21-jdk
VOLUME /tmp
COPY target/banking-0.0.1-SNAPSHOT.jar /app/app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]