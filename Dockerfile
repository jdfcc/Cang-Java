FROM openjdk:8-jre-alpine

WORKDIR /app

COPY target/Cang-1.0.jar app.jar

EXPOSE 9090

CMD ["java","-jar","app.jar"]

