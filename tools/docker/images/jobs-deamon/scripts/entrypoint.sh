#!/bin/bash

echo "Starting entrypoint.sh ..."

cd /usr/local/geecommerce/tools/scripts

ls -l

echo "Changed directory ..."
pwd

echo "Calling geec script ..."

#chmod +x geec.sh
#dos2unix geec.sh

#chmod +x run_cron.sh
#dos2unix run_cron.sh

#chmod +x start_demo_cron.sh
#dos2unix start_demo_cron.sh

./geec.sh add all modules to project demo

./start_demo_cron.sh


# tail -f /dev/null
