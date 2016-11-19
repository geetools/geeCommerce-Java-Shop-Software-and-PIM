#!/bin/bash

usage() { echo "Usage: $0 [-m <merchantId>] [-s <storeId>] Optional: [-w <path to webpp>] [-M <path to Merchant.properties>] [-S <path to System.properties>] [-j <java home>] [-l <log dir>] [-L <log configuration file>]" 1>&2; exit 1; }

while true; do
  case "$1" in
    -j | --java-home ) java_home_path=$2; shift 2 ;;
    -ja | --java-args ) java_args=$2; shift 2 ;;
    -l | --log-path ) log_path=$2; shift 2 ;;
    -L | --log4j-conf-path ) log4j_config_file="$2"; shift 2 ;;
    -m | --merchant-id ) merchant_id=$2; shift 2 ;;
    -M | --merchant-properties-path ) merchant_properties_path=$2; shift 2 ;;
    -s | --store-id ) store_id=$2; shift 2 ;;
    -v | --view-id ) view_id=$2; shift 2 ;;
   -up | --url-prefix ) url_prefix=$2; shift 2 ;;
    -S | --system-properties-path ) system_properties_path=$2; shift 2 ;;
    -E | --environment-properties-path ) environment_properties_path=$2; shift 2 ;;
    -C | --cache-properties-path ) cache_properties_path=$2; shift 2 ;;
    -Q | --quartz-properties-path ) quartz_properties_path=$2; shift 2 ;;
    -w | --webapp-path ) webapp_path=$2; shift 2 ;;
    -p | --jmx-port ) jmx_port=$2; shift 2 ;;
    -ll | --locale-lang ) locale_lang=$2; shift 2 ;;
    -lc | --locale-country ) locale_country=$2; shift 2 ;;
    -run | --run-task ) run=$2; shift 2 ;;
    -- ) shift; break ;;
    * ) break ;;
  esac
done



echo "merchant_id: $merchant_id"
echo "url_prefix: $url_prefix"
echo "run: $run"

# --------------------------------------------------------
# Evaluate parameters.
# --------------------------------------------------------

# Make sure that at least the merchant_id or the url_prefix is set.
if [ -z "$merchant_id" ] && [ -z "$url_prefix" ]; then
  usage
fi


# Store the current directory in a local variable.
current_dir=$( cd $(dirname $0) ; pwd -P )

# Now we can store the current directory in to the base_dir variable.
base_dir=$( cd $current_dir/../../ ; pwd -P )

#
# Log directory.
#
log_dir=$base_dir/cron/log
log_conf_file=$base_dir/cron/conf/log4j2.xml

# Overwrite default value if one exists.
if [ ! -z "$log_path" -a "$log_path" != " " ]; then
	log_dir=$log_path
fi

mkdir -p $log_dir

# Overwrite default value if one exists.
if [ ! -z "$log4j_config_file" -a "$log4j_config_file" != " " ]; then
	log_conf_file=$log4j_config_file
fi

#
# System properties.
#
if [ -z "$system_properties_path" ]; then
        system_properties_path=$webapp_path/WEB-INF/conf/System.properties
fi

#
# Environment properties.
#
if [ -z "$environment_properties_path" ]; then
        environment_properties_path=$webapp_path/WEB-INF/conf/Environment.properties
fi

#
# Cache properties.
#
if [ -z "$cache_properties_path" ]; then
        cache_properties_path=$webapp_path/WEB-INF/conf/Cache.properties
fi

#
# Quartz properties.
#
if [ -z "$quartz_properties_path" ]; then
        quartz_properties_path=$webapp_path/WEB-INF/classes/quartz.properties
fi



#
# JMX port.
#
if [ -z "$jmx_port" ]; then
        jmx_port=1099
fi


#
# Java home.
#

# Find java home path.
if [ ! -z "$java_home_path" -a "$java_home_path" != " " ]; then
	JAVA_HOME=$java_home_path
	export JAVA_HOME
fi

if [ -z "$JAVA_HOME" ]; then
   JAVACMD=$(which java)
else
    if [ -f $JAVA_HOME/bin/java.exe ]; then
      JAVACMD=$JAVA_HOME/bin/java.exe
    else
      JAVACMD=$JAVA_HOME/bin/java
    fi
fi

echo "JAVACMD: $JAVACMD"


export PATH="$JAVA_HOME/bin:$PATH"


# Java args.
if [ -z "$java_args" ]; then
    java_args="-Xms2048m -Xmx2048m"
fi


#
# Classpath.
#

classes_path=$webapp_path/WEB-INF/classes
lib_path="$webapp_path/WEB-INF/lib/*"

echo "JAVA_HOME: $JAVA_HOME"
echo "java_args: $java_args"
echo "classpath: $classes_path:$lib_path"
echo "current_dir: $current_dir"
echo "base_dir: $base_dir"
echo "log_dir: $log_dir"

echo "merchant_id: $merchant_id"
echo "store_id: $store_id"
echo "view_id: $view_id"
echo "locale_lang: $locale_lang"
echo "locale_country: $locale_country"
echo "url_prefix: $url_prefix"
echo "webapp_path: $webapp_path"
echo "system_properties_path: $system_properties_path"
echo "merchant_properties_path: $merchant_properties_path"
echo "quartz_properties_path: $quartz_properties_path"


VIEW_ARG=""
if [ -n "$view_id" ]; then
  VIEW_ARG="-Dview.id=$view_id"
fi

LANG_ARG=""
if [ -n "$locale_lang" ]; then
  LANG_ARG="-Dlang.code=$locale_lang"
fi

COUNTRY_ARG=""
if [ -n "$locale_country" ]; then
  COUNTRY_ARG="-Dcountry.code=$locale_country"
fi

echo "args: $VIEW_ARG $LANG_ARG $COUNTRY_ARG $url_prefix"



$JAVACMD \
-cp "$classes_path:$lib_path" \
-Dinjector.providers.enabled=true \
-Dmerchant.id="$merchant_id" \
-Dstore.id="$store_id" \
$VIEW_ARG \
$LANG_ARG \
$COUNTRY_ARG \
-Dreqctx.urlprefix="$url_prefix" \
-Dbase.path=$base_dir \
-Dlog.path=$log_dir \
-Dlog4j.configurationFile=$log4j_config_file \
-Dsystem.properties.path=$system_properties_path \
-Dmerchant.properties.path=$merchant_properties_path \
-Denv.properties.path=$environment_properties_path \
-Dcache.properties.path=$cache_properties_path \
-Dorg.quartz.properties=$quartz_properties_path \
$java_args \
-XX:OnOutOfMemoryError="kill -9 %p" -XX:+HeapDumpOnOutOfMemoryError \
-Dorg.quartz.scheduler.jmx.export=true \
-Dorg.quartz.scheduler.jmx.objectName=com.geecommerce.core.cron.TaskRunner:type=geeCommerce-Scheduler \
-Dcom.sun.management.jmxremote \
-Dcom.sun.management.jmxremote.port=$jmx_port \
-Dcom.sun.management.jmxremote.authenticate=false \
-Dcom.sun.management.jmxremote.ssl=false \
-Dcom.sun.xml.ws.transport.http.client.HttpTransportPipe.dump=true \
-Drun="$run" \
com.geecommerce.core.cron.TaskRunner

