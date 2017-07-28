#!/bin/bash

dos2unix /tmp/mongodb/mongod.conf
mv /tmp/mongodb/mongod.conf /etc/mongod.conf

cat /etc/mongod.conf

mongod --config /etc/mongod.conf --fork

ps -ef | grep mongo

cat /var/log/mongodb/mongod.log

cd /tmp/mongodb/data/

mongo admin --port 27017 --eval "db.createUser({user:'root', pwd:'r00t', roles:[{role:'root',db:'admin'}]});"
mongo admin --port 27017 -u root -p r00t --eval "db.createUser({user:'gc_demo', pwd:'d3m0', roles : [{ role:'readWrite', db:'gc_system_local'}, { role:'readWrite', db:'gc_demo_local'}, { role:'readWrite', db:'gc_demo_media_assets_local'}]});"

tar xvfz gc_system_local.tar.gz
tar xvfz gc_demo_local.tar.gz
tar xvfz gc_demo_media_assets_local.tar.gz

mongorestore --drop --port 27017 -u root -p r00t --authenticationDatabase admin -d gc_system_local /tmp/mongodb/data/gc_system_local
mongorestore --drop --port 27017 -u root -p r00t --authenticationDatabase admin -d gc_demo_local /tmp/mongodb/data/gc_demo_local
mongorestore --drop --port 27017 -u root -p r00t --authenticationDatabase admin -d gc_demo_media_assets_local /tmp/mongodb/data/gc_demo_media_assets_local

mongo admin -u root -p r00t --quiet --eval "printjson(db.adminCommand('listDatabases'))"

mongod --config /etc/mongod.conf --shutdown
