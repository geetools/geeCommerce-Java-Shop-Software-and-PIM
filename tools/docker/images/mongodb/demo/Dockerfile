FROM ubuntu:latest

# Install some useful utils.
RUN apt-get update && apt-get install -y debconf-utils net-tools lsof htop vim less wget telnet dos2unix

# Installation:
# Import MongoDB public GPG key AND create a MongoDB list file
RUN apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv EA312927
RUN apt-get update && apt-get install -y --no-install-recommends software-properties-common
RUN echo "deb http://repo.mongodb.org/apt/ubuntu $(cat /etc/lsb-release | grep DISTRIB_CODENAME | cut -d= -f2)/mongodb-org/3.2 multiverse" | tee /etc/apt/sources.list.d/mongodb-org-3.2.list

# Update apt-get sources AND install MongoDB
RUN apt-get update && apt-get install -y mongodb-org

RUN mkdir -p /tmp/mongodb/data
COPY ./data/gc_system_local.tar.gz /tmp/mongodb/data/
COPY ./data/gc_demo_local.tar.gz /tmp/mongodb/data/
COPY ./data/gc_demo_media_assets_local.tar.gz /tmp/mongodb/data/
COPY ./scripts/init.sh /tmp/mongodb/
COPY ./conf/mongod.conf /tmp/mongodb/

RUN cd /tmp/mongodb/ \
	&& dos2unix init.sh \
	&& chmod +x init.sh \
	&& ./init.sh

EXPOSE 27017

ENTRYPOINT ["/usr/bin/mongod", "--config", "/etc/mongod.conf"]

