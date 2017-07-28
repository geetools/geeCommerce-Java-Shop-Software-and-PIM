FROM openjdk:8-alpine

RUN \
  apk update && apk add \
  less \
  bash

RUN apk update && apk add dos2unix --update-cache --repository http://dl-3.alpinelinux.org/alpine/edge/testing/ --allow-untrusted

COPY ./scripts/entrypoint.sh /

RUN \
  mkdir -p /tmp && \
  mkdir -p /usr/local/geecommerce && \
  chmod +x /entrypoint.sh && \
  dos2unix /entrypoint.sh && \
  ls -l /

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
  ls -l /usr/local/geecommerce && \
  chmod +x tools/scripts/*


EXPOSE 1099

CMD ["bash"]
CMD ["/entrypoint.sh"]

# CMD ["ls", "-l", "/usr/local/geecommerce"]
