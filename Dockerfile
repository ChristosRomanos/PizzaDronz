FROM openjdk:18-jdk-slim

# Set the author label
LABEL authors="cmrom"

# Add a volume pointing to /tmp
WORKDIR /app

# The application's jar file


COPY ./target/PizzaDronz-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

# Run the jar file
ENTRYPOINT ["java","-jar","/app/app.jar"]