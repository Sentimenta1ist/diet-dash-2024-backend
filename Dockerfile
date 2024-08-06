FROM openjdk:21

COPY target/*SNAPSHOT.jar /opt/service.jar

# Expose here your service ports
EXPOSE 5005

# Management port
EXPOSE 8080

ENTRYPOINT [ "java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005", "-jar", "/opt/service.jar"]