FROM maven:3.6.3-jdk-8-slim as builder

WORKDIR /usr/local/app

COPY ./ ./

RUN mvn \
    -Dmaven.test.skip=true \
    --batch-mode \
    package


FROM gcr.io/distroless/java:8

EXPOSE 8080
WORKDIR /usr/local/app

COPY --from=builder \
    /usr/local/app/target/ims*.jar \
    ./server.jar

COPY ./src/main/resources ./

CMD [ "/usr/local/app/server.jar", "-Djava.security.edg=file:/dev/./urandom", "-Djava.security.egd=file:/dev/./urandom"]
