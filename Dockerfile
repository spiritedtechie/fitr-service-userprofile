FROM java:8

# Add SSL keystores
ADD ssl/keystore/fitr-keystore.jks /opt/services/fitr-keystore.jks
ADD ssl/truststore/fitr-truststore.jks /opt/services/fitr-truststore.jks

# Add application config
ADD config-docker.yml /opt/services/config-docker.yml

# Add application JAR
ADD build/libs/fitr-service-userprofile-1.0-SNAPSHOT-standalone.jar /opt/services/fitr-service-userprofile.jar

EXPOSE 8443

WORKDIR /opt/services
CMD ["java", "-Xms128m", "-Xmx1500m", "-Dfile.encoding=UTF-8", "-Djavax.net.ssl.trustStore=fitr-truststore.jks", "-Djavax.net.ssl.trustStorePassword=example", "-jar", "fitr-service-userprofile.jar", "server", "config-docker.yml"]