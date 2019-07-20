FROM openjdk:8-jre-alpine

ENV SPRING_OUTPUT_ANSI_ENABLED=ALWAYS \
    SLEEP=0
ENV JAVA_OPTS="-Xmx512m -Xmx256m"
# add directly the war
ADD *.jar /app.jar
RUN sh -c 'touch /app.jar'
VOLUME /tmp
EXPOSE 8085
CMD echo "The application will start in ${SLEEP}s..." && \
    sleep ${SLEEP} && \
    java  -Xmx512m -Xms256m -Djava.security.egd=file:/dev/./urandom -jar /app.jar
