FROM tomcat:8-jre8-alpine

RUN \
  apk update && apk add \
  less \
  wget \
  bash

RUN apk update && apk add dos2unix --update-cache --repository http://dl-3.alpinelinux.org/alpine/edge/testing/ --allow-untrusted

RUN cd /tmp && \
  wget https://repo1.maven.org/maven2/com/quartzdesk/quartzdesk-web/2.8.3/quartzdesk-web-2.8.3.war && \
  mv quartzdesk-web-2.8.3.war quartzdesk-web-2.8.3.zip && \
  mkdir /usr/local/tomcat/webapps/quartzdesk && \
  unzip -d /usr/local/tomcat/webapps/quartzdesk quartzdesk-web-2.8.3.zip && \
  mkdir -p /usr/local/quartzdesk/work && \
  cd /usr/local/tomcat/lib && \
  wget http://repo1.maven.org/maven2/mysql/mysql-connector-java/5.1.42/mysql-connector-java-5.1.42.jar

COPY ./conf/license.key /usr/local/quartzdesk/work/
COPY ./conf/logback.xml /usr/local/quartzdesk/work/
COPY ./conf/quartzdesk.properties /usr/local/quartzdesk/work/
COPY ./conf/context.xml /usr/local/tomcat/webapps/quartzdesk/META-INF/
COPY ./conf/tomcat-users.xml /usr/local/tomcat/conf/

EXPOSE 8090

CMD ["bash"]
CMD ["catalina.sh", "run"]
