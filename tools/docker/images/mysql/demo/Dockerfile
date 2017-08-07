FROM ubuntu:latest

RUN mkdir -p /tmp/mysql/data
COPY ./data/schema.sql /tmp/mysql/data/
COPY ./data/data.sql /tmp/mysql/data/
COPY ./data/schema_jobs_daemon.sql /tmp/mysql/data/
COPY ./data/quartzdesk.sql /tmp/mysql/data/
COPY ./scripts/init.sh /tmp/mysql/
COPY ./scripts/entrypoint.sh /

RUN \
	apt-get update && apt-get install -y debconf-utils net-tools vim less dos2unix && \
	apt-get install -y --no-install-recommends software-properties-common apt-utils && \
	add-apt-repository 'deb http://archive.ubuntu.com/ubuntu trusty universe' && \
	apt-get update && \
	apt install -y mysql-server-5.6 && \
	apt install -y mysql-client-5.6 && \
	cd /tmp/mysql/ && \
	dos2unix /entrypoint.sh && \
	chmod +x /entrypoint.sh && \
	dos2unix init.sh && \
	chmod +x init.sh && \
	./init.sh

RUN bash -c 'debconf-set-selections <<< "mysql-server mysql-server/root_password password changeme"'
RUN bash -c 'debconf-set-selections <<< "mysql-server mysql-server/root_password_again password changeme"'

EXPOSE 3306

ENTRYPOINT ["/entrypoint.sh"]

