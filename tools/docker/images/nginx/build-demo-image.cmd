@ECHO OFF

mkdir tmp

cd C:/dev/github/geecommerce

tar cfz tools/docker/images/nginx/tmp/gc-webapp.tar.gz webapp
tar cfz tools/docker/images/nginx/tmp/gc-projects.tar.gz projects --exclude="./projects/*/modules/*" --exclude="./projects/*/target"
tar cfz tools/docker/images/nginx/tmp/gc-modules.tar.gz --exclude="*/target/*" --exclude="*/src/*" --exclude="*/src-api/*" modules
tar cfz tools/docker/images/nginx/tmp/gc-admin-panel.tar.gz admin-panel --exclude="target"
tar cfz tools/docker/images/nginx/tmp/gc-scripts.tar.gz tools/scripts


cd C:\dev\github\geecommerce\tools\docker\images\nginx

docker rm -f gc_demo_nginx
rem docker rmi -f geecommerce/demo-nginx:1.0

docker build -t geecommerce/demo-nginx:1.0 .

