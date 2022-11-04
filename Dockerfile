FROM maven:3.5.4-jdk-8-alpine
COPY ./pom.xml ./pom.xml
COPY ./src ./src
RUN mvn dependency:go-offline -B
RUN mvn package
EXPOSE 8080
CMD java -jar target/dependency/webapp-runner.jar target/*.war