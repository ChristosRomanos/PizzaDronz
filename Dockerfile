FROM openjdk:17-jdk-slim

# Set the author label
LABEL authors="cmrom"

# Add a volume pointing to /tmp
VOLUME /tmp

# The application's jar file
ARG JAR_FILE=target/PizzaDronz-0.0.1-SNAPSHOT.jar

COPY ${JAR_FILE} app.jar

EXPOSE 8080

# Run the jar file
ENTRYPOINT ["java","-jar","/app.jar"]