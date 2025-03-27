FROM jhipster/jhipster:latest

WORKDIR /app

COPY target/space-0.0.1-SNAPSHOT.jar /app/space.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "space.jar"]
