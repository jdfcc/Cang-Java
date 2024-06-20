FROM openjdk:11-jre-slim

WORKDIR /app

COPY target/cang-2.0.1.jar app.jar

EXPOSE 9986

CMD ["java","-jar","app.jar"]

