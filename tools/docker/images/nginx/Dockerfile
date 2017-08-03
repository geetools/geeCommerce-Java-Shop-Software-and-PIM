FROM nginx:1.13-alpine-perl

RUN \
  apk update && apk add \
  less \
  shadow \
  openssl \
  bash

RUN apk update && apk add dos2unix --update-cache --repository http://dl-3.alpinelinux.org/alpine/edge/testing/ --allow-untrusted

RUN mv /etc/nginx/conf.d/default.conf /etc/nginx/conf.d/default.conf.orig && \
  mkdir -p /etc/nginx/certs && \
  openssl req -subj '/CN=localhost/O=CommerceBoard GmbH/C=DE' -new -newkey rsa:2048 -nodes -x509 -keyout /etc/nginx/certs/server.key -out /etc/nginx/certs/server.crt

COPY ./conf/geecommerce_proxy.conf /etc/nginx/conf.d/

EXPOSE 80
EXPOSE 443



#COPY ./conf/nginx.conf /etc/nginx/nginx.conf

#ENTRYPOINT ["/usr/bin/mongod", "--config", "/etc/mongod.conf"]

#CMD ["nginx-debug", "-g", "daemon off;"]