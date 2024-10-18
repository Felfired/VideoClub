FROM maven:3.6.1-jdk-8 as maven_builder
WORKDIR /dvd
ADD pom.xml .
RUN ["mvn", "dependency:resolve"]
RUN ["mvn", "verify"]

ADD src ./src
RUN mvn clean package

FROM openjdk:8
COPY --from=maven_builder /dvd/target/dvd-0.0.1-jar-with-dependencies.jar .
CMD ["java", "-jar", "dvd-0.0.1-jar-with-dependencies.jar"]