FROM openjdk:11.0.6-jre-slim
COPY ./target/EmailParser-jar-with-dependencies.jar /tmp
WORKDIR /tmp
ENTRYPOINT ["java","-jar","EmailParser-jar-with-dependencies.jar"]