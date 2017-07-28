FROM nginx:1.13-alpine-perl

RUN \
  apk update && apk add \
  less \
  shadow \
  openssl \
  bash

RUN apk update && apk add dos2unix --update-cache --repository http://dl-3.alpinelinux.org/alpine/edge/testing/ --allow-untrusted

RUN \
  mkdir -p /tmp && \
  mkdir -p /usr/local/geecommerce && \
  mv /etc/nginx/conf.d/default.conf /etc/nginx/conf.d/default.conf.orig && \
  mkdir -p /etc/nginx/certs && \
  openssl req -subj '/CN=localhost/O=CommerceBoard GmbH/C=DE' -new -newkey rsa:2048 -nodes -x509 -keyout /etc/nginx/certs/server.key -out /etc/nginx/certs/server.crt

WORKDIR /tmp

COPY ./tmp/gc-admin-panel.tar.gz .
COPY ./tmp/gc-modules.tar.gz .
COPY ./tmp/gc-projects.tar.gz .
COPY ./tmp/gc-webapp.tar.gz .
COPY ./tmp/gc-scripts.tar.gz .

WORKDIR /usr/local/geecommerce

RUN \
  ls -l /tmp/ && \
  ls -l /usr/local/geecommerce && \
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
  rm -f /usr/local/geecommerce/projects/demo/modules/* && \
  rm -f /usr/local/geecommerce/webapp/src/main/webapp/WEB-INF/lib && \
  ln -s /usr/local/geecommerce/webapp/target/geecommerce-webapp-1.0-SNAPSHOT/WEB-INF/lib/ /usr/local/geecommerce/webapp/src/main/webapp/WEB-INF/lib && \
  ls -l /usr/local/geecommerce/projects/demo/modules/ && \
  ls -l /usr/local/geecommerce

COPY ./conf/geecommerce_proxy.conf /etc/nginx/conf.d/

EXPOSE 80
EXPOSE 443



#COPY ./conf/nginx.conf /etc/nginx/nginx.conf

#ENTRYPOINT ["/usr/bin/mongod", "--config", "/etc/mongod.conf"]

#CMD ["nginx-debug", "-g", "daemon off;"]