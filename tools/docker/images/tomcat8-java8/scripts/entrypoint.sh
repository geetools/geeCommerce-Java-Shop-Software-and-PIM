#!/bin/bash

echo "Starting entrypoint.sh ..."

mkdir -p /usr/local/geecommerce-local

cd /usr/local
rsync -rulpt --delete --exclude=projects\/demo\/modules geecommerce-shared/ geecommerce-local/

cd /usr/local/geecommerce-local/tools/scripts

echo "Changed directory ..."
pwd

echo "Calling geec script ..."

./geec.sh add all modules to project demo

cd /usr/local/

# Hack for windows as host-container shared folder is very unstable.
while true; do echo "$(date +"%d.%m.%Y %H:%M:%S"): Synchronizing webapp ..."; rsync -rulpt --delete --exclude=projects\/demo\/modules geecommerce-shared/ geecommerce-local/; echo "$(date +"%d.%m.%Y %H:%M:%S"): Waiting ..."; sleep 5; done > /dev/null 2>&1 &

cd /usr/local/tomcat

echo "Changed directory ..."
pwd

export JPDA_ADDRESS=8000

echo "Calling tomcat script ..."
/usr/local/tomcat/bin/catalina.sh jpda run

