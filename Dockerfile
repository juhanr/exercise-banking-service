FROM openjdk:17
VOLUME /tmp
ADD build/libs/* banking-service.jar
ENTRYPOINT ["java", "-jar", "banking-service.jar"]
EXPOSE 8080

