#!/bin/bash

echo "Starting entrypoint.sh ..."

cd /usr/local/geecommerce/tools/scripts

echo "Changed directory ..."
pwd

echo "Calling geec script ..."

./geec.sh add all modules to project demo

cd /usr/local/tomcat

echo "Changed directory ..."
pwd

export JPDA_ADDRESS=8000


echo "Calling tomcat script ..."
/usr/local/tomcat/bin/catalina.sh jpda run
