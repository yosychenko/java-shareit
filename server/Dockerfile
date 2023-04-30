FROM amazoncorretto:11-alpine-jdk
COPY target/*.jar app.jar
ENTRYPOINT exec java $JAVA_OPTS -jar /app.jar
