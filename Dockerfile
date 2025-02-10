# Stage 1: Build the application
FROM maven:3.9.4 AS build

# Set the working directory in the container
WORKDIR /app

# Copy the Maven project files
COPY pom.xml .
COPY src ./src

# Run Maven to build the application
RUN mvn clean package

# Stage 2: Package the application
FROM openjdk:21

# Copy the JAR file from the build stage
COPY --from=build /app/target/*SNAPSHOT.jar /opt/service.jar

# Copy the keystore file into the container
COPY /etc/letsencrypt/live/api.diet-dash.com/keystore.p12 /app/keystore.p12

# Expose the port your app will run on (e.g., 8443 for HTTPS)
EXPOSE 8443

# Set the entry point
ENTRYPOINT ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005", "-jar", "/opt/service.jar"]