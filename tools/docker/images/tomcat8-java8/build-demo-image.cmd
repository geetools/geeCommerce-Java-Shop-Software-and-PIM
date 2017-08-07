@ECHO OFF

mkdir tmp

cd C:/dev/github/geecommerce

rm -f tools/docker/images/tomcat8-java8/tmp/gc-webapp.tar.gz
rm -f tools/docker/images/tomcat8-java8/tmp/gc-modules.tar.gz

tar cfz tools/docker/images/tomcat8-java8/tmp/gc-webapp.tar.gz webapp
rem tar cfz tools/docker/images/tomcat8-java8/tmp/gc-projects.tar.gz projects --exclude="./projects/*/modules/*" --exclude="./projects/*/target"
tar cfz tools/docker/images/tomcat8-java8/tmp/gc-modules.tar.gz --exclude="*/target/*" --exclude="*/src/*" --exclude="*/src-api/*" modules
rem tar cfz tools/docker/images/tomcat8-java8/tmp/gc-admin-panel.tar.gz admin-panel --exclude="target"
rem tar cfz tools/docker/images/tomcat8-java8/tmp/gc-scripts.tar.gz tools/scripts


cd C:\dev\github\geecommerce\tools\docker\images\tomcat8-java8

docker rm -f gc_demo_tomcat


docker build -t geecommerce/tomcat8-java8:latest .
docker tag geecommerce/tomcat8-java8:latest dockerhub.geetools.net:5000/geecommerce/tomcat8-java8:latest
