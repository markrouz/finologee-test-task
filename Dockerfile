FROM openjdk:17-alpine

ENV TZ Europe/Brussels

EXPOSE 8080

WORKDIR /app
COPY target/@project.build.finalName@.jar /app/@project.build.finalName@.jar
CMD ["java", "-jar", "@project.build.finalName@.jar"]