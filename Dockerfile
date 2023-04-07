FROM ahawker/jre8
VOLUME /tmp
ENV MYSQL_URL=1 MYSQL_USERNAME=1 MYSQL_PASSWORD=1 \
                REDIS_HOST=1
                REDIS_PORT=1
                REDIS_PASSWORD=1
                NZC_TOKEN=1
                TYN_TOKEN=1
                ZZW_TOKEN=1
                XXY_TOKEN=1
                LZH_TOKEN=1
                DOCKER_USERNAME=1
                DOCKER_PASSWORD=1
                REGISTRY=1
                IMAGE_NAME=1
                IMAGE_TAG=1
ADD target/od-api-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]
