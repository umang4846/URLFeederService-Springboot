FROM openjdk:8
ADD target/URLFeederService.jar URLFeederService.jar
EXPOSE 8085
ENTRYPOINT ["java","-jar","URLFeederService.jar"]