FROM docker.io/openjdk:11
COPY app.jar /
EXPOSE 8100
ENTRYPOINT ["java","-jar","/app.jar"]