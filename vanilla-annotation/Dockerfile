# syntax=docker/dockerfile:experimental
FROM openjdk:8-jdk-alpine as build
WORKDIR /workspace/app
ARG HOME=/root
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src
RUN --mount=type=cache,target=${HOME}/.m2 ./mvnw dependency:get -Dartifact=org.springframework.boot.experimental:spring-boot-thin-launcher:1.0.22.RELEASE:jar:exec -Dtransitive=false
RUN --mount=type=cache,target=${HOME}/.m2 ./mvnw dependency:get -Dartifact=org.springframework.boot.experimental:spring-boot-graal-feature:0.5.0.BUILD-SNAPSHOT:jar -Dtransitive=false
RUN --mount=type=cache,target=${HOME}/.m2 ./mvnw install -DskipTests
VOLUME ${HOME}/.m2

FROM oracle/graalvm-ce:19.0.2 as native
WORKDIR /workspace/app
ARG HOME=/root
ARG THINJAR=/root/.m2/repository/org/springframework/boot/experimental/spring-boot-thin-launcher/1.0.22.RELEASE/spring-boot-thin-launcher-1.0.22.RELEASE-exec.jar
COPY --from=build /root/.m2 /root/.m2
COPY --from=build /workspace/app/target/*.jar target/
RUN gu install native-image
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)
RUN native-image -Dio.netty.noUnsafe=true --static --no-server -H:Name=target/demo -H:+ReportExceptionStackTraces --no-fallback --allow-incomplete-classpath --report-unsupported-elements-at-runtime -DremoveUnusedAutoconfig=true -cp target/dependency:`java -jar ${THINJAR} --thin.archive=target/dependency --thin.classpath --thin.profile=graal`:${HOME}/.m2/repository/org/springframework/boot/experimental/spring-boot-graal-feature/0.5.0.BUILD-SNAPSHOT/spring-boot-graal-feature-0.5.0.BUILD-SNAPSHOT.jar com.example.SampleApplication

FROM alpine
WORKDIR /workspace/app
VOLUME /tmp
COPY --from=native /workspace/app/target/demo .
EXPOSE 8080
ENTRYPOINT ["./demo"]