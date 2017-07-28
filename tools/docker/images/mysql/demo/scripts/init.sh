#!/bin/bash

systemctl disable mysql

sed -i -e"s/^bind-address\s*=\s*127.0.0.1/bind-address = 0.0.0.0/" /etc/mysql/my.cnf

cat /etc/mysql/my.cnf

/usr/sbin/mysqld --user=mysql &
sleep 10

ps -ef | grep mysql

cat /var/log/mysql/mysql.log

cd /tmp/mysql/data/

/usr/bin/mysqladmin -h 127.0.0.1 -u root -p'changeme' password r00t

/usr/bin/mysql -u root -pr00t < /tmp/mysql/data/schema.sql
/usr/bin/mysql -u root -pr00t < /tmp/mysql/data/data.sql
/usr/bin/mysql -u root -pr00t < /tmp/mysql/data/schema_jobs_daemon.sql
/usr/bin/mysql -u root -pr00t < /tmp/mysql/data/quartzdesk.sql

echo "GRANT ALL PRIVILEGES ON gc_demo_local.* To 'gc_demo'@'%' IDENTIFIED BY 'd3m0'" | mysql -u root -pr00t
echo "GRANT ALL PRIVILEGES ON gc_demo_scheduler_local.* To 'gc_demo'@'%' IDENTIFIED BY 'd3m0'" | mysql -u root -pr00t
echo "GRANT ALL PRIVILEGES ON gc_demo_quartzdesk_local.* To 'gc_demo'@'%' IDENTIFIED BY 'd3m0'" | mysql -u root -pr00t

sleep 3

killall mysqld

sleep 3
