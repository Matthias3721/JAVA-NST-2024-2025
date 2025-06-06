FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY target/medmanagement-0.0.1-SNAPSHOT.jar medmanagement.jar
ENTRYPOINT ["java","-jar","medmanagement.jar"]
