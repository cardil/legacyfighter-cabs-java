FROM registry.access.redhat.com/ubi9/openjdk-11 as builder

RUN mkdir -p /home/default/app
WORKDIR /home/default/app
ADD pom.xml /home/default/app/pom.xml
ADD src /home/default/app/src
RUN mvn -V --no-transfer-progress -Dstyle.color=always -DskipTests package

FROM registry.access.redhat.com/ubi9/openjdk-11-runtime

COPY --from=builder /home/default/app/target/cabs-*.jar /home/default/cabs.jar

EXPOSE 8080

ENTRYPOINT ["/usr/bin/java", "-jar", "/home/default/cabs.jar"]
