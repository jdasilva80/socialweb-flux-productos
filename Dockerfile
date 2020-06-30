FROM openjdk:8
VOLUME /tmp
ADD ./target/socialweb-flux-productos-0.0.1-SNAPSHOT.jar socialweb-flux-productos.jar
ENTRYPOINT ["java","-jar","/socialweb-flux-productos.jar"]