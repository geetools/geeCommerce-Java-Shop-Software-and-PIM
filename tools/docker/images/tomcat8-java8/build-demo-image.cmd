@ECHO OFF

mkdir tmp

cd C:/dev/github/geecommerce

tar cfz tools/docker/images/tomcat8-java8/tmp/gc-webapp.tar.gz webapp
tar cfz tools/docker/images/tomcat8-java8/tmp/gc-projects.tar.gz projects --exclude="./projects/*/modules/*" --exclude="./projects/*/target"
tar cfz tools/docker/images/tomcat8-java8/tmp/gc-modules.tar.gz --exclude="*/target/*" --exclude="*/src/*" --exclude="*/src-api/*" modules
tar cfz tools/docker/images/tomcat8-java8/tmp/gc-admin-panel.tar.gz admin-panel --exclude="target"
tar cfz tools/docker/images/tomcat8-java8/tmp/gc-scripts.tar.gz tools/scripts


cd C:\dev\github\geecommerce\tools\docker\images\tomcat8-java8

docker rm -f gc_demo_tomcat
rem docker rmi -f geecommerce/tomcat8-java8:1.0

docker build -t geecommerce/tomcat8-java8:1.0 .

