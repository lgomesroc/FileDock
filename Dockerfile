FROM tomcat:11.0-jre21

RUN rm -rf /usr/local/tomcat/webapps/*

COPY target/filedock-0.0.1-SNAPSHOT.war /usr/local/tomcat/webapps/filedock.war

EXPOSE 8080
