FROM ahawker/jre8
VOLUME /tmp
ADD target/od-api-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]
