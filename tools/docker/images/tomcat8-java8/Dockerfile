FROM tomcat:8-jre8-alpine

WORKDIR /tmp

COPY ./tmp/gc-admin-panel.tar.gz .
COPY ./tmp/gc-modules.tar.gz .
COPY ./tmp/gc-projects.tar.gz .
COPY ./tmp/gc-webapp.tar.gz .
COPY ./tmp/gc-scripts.tar.gz .

COPY ./conf/ROOT.xml /usr/local/tomcat/conf/Catalina/localhost/
COPY ./conf/server.template /usr/local/tomcat/conf/
COPY ./scripts/entrypoint.sh /

WORKDIR /usr/local/geecommerce-local

RUN apk add imagemagick=6.9.6.8-r0 --no-cache --repository http://dl-3.alpinelinux.org/alpine/v3.3/main/


RUN \
  apk update && apk add dos2unix --update-cache --repository http://dl-3.alpinelinux.org/alpine/edge/community/ --allow-untrusted && \
  apk update && apk add rsync less vim bash gettext && \
  mkdir -p /usr/local/tomcat/conf/Catalina/localhost && \
  mkdir -p /usr/local/geecommerce-local && \
  ls -l /tmp/ && \
  ls -l /usr/local/geecommerce-local && \
  tar xfz /tmp/gc-admin-panel.tar.gz && \
  tar xfz /tmp/gc-modules.tar.gz && \
  tar xfz /tmp/gc-projects.tar.gz && \
  tar xfz /tmp/gc-webapp.tar.gz && \
  tar xfz /tmp/gc-scripts.tar.gz && \
  rm /tmp/gc-admin-panel.tar.gz && \
  rm /tmp/gc-modules.tar.gz && \
  rm /tmp/gc-projects.tar.gz && \
  rm /tmp/gc-webapp.tar.gz && \
  rm /tmp/gc-scripts.tar.gz && \
  rm -f /usr/local/geecommerce-local/projects/demo/modules/* && \
  rm -f /usr/local/geecommerce-local/webapp/src/main/webapp/WEB-INF/lib && \
  ln -s /usr/local/geecommerce-local/webapp/target/geecommerce-webapp-1.0-SNAPSHOT/WEB-INF/lib/ /usr/local/geecommerce-local/webapp/src/main/webapp/WEB-INF/lib && \
  ls -l /usr/local/geecommerce-local/projects/demo/modules/ && \
  ls -l /usr/local/geecommerce-local && \
  ls -l /usr/local/tomcat/conf/Catalina/localhost/ && \
  chmod +x /entrypoint.sh && \
  dos2unix /entrypoint.sh && \
  chmod +x tools/scripts/* && \
  ls -l /

CMD ["bash"]
CMD ["/entrypoint.sh"]

