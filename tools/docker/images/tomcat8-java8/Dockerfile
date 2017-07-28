FROM tomcat:8.0-jre8

RUN \
  apt-get update && apt-get install -y --force-yes \
  imagemagick \
  dos2unix \
  less

RUN mkdir -p /usr/local/tomcat/conf/Catalina/localhost && \
  mkdir -p /usr/local/geecommerce

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

COPY ./conf/ROOT.xml /usr/local/tomcat/conf/Catalina/localhost/
COPY ./scripts/entrypoint.sh /

RUN chmod +x /entrypoint.sh && \
  dos2unix /entrypoint.sh && \
  chmod +x tools/scripts/* && \
  ls -l /

EXPOSE 8080
EXPOSE 8000

CMD ["bash"]
CMD ["/entrypoint.sh"]
