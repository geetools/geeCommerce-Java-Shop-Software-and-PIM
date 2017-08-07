#!/bin/bash

# Store the current directory in a local variable.
CURRENT_DIR=$( cd $(dirname $0) ; pwd -P )
mkdir -p $CURRENT_DIR/log
mkdir -p $CURRENT_DIR/../../projects/demo/log/_demostore

while true; do
  case "$1" in
    -jvm | --java-home ) java_home_path=$2; shift 2 ;;
    -- ) shift; break ;;
    * ) break ;;
  esac
done

if [ -n "$java_home_path" ]; then
  export JAVA_HOME $java_home_path
fi



URL_PREFIX='demo.geecommerce.local${p}'

WEBAPP_PATH=$( cd $CURRENT_DIR/../../webapp/src/main/webapp; pwd -P )
LOG_PATH=$( cd $CURRENT_DIR/../../projects/demo/log/_demostore; pwd -P )
LOG_CONFIG=$( cd $CURRENT_DIR/../cron/conf; pwd -P )/log4j2.xml
JVM_ARGS="-Xms512m -Xmx512m"

echo "JAVA_HOME: $java_home_path"
echo "CURRENT_DIR: $CURRENT_DIR"
echo "WEBAPP_PATH: $WEBAPP_PATH"
echo "LOG_PATH: $LOG_PATH"
echo "URL_PREFIX: $URL_PREFIX"

$CURRENT_DIR/run_cron.sh \
-j "$java_home_path" \
-ja "$JVM_ARGS" \
-up $URL_PREFIX \
-w $WEBAPP_PATH \
-l $LOG_PATH \
-L $LOG_CONFIG