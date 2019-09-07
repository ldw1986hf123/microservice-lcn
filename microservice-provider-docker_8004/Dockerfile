FROM openjdk:8-jdk-alpine
VOLUME /tmp
ADD microservice-provider-docker_8004.jar app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]