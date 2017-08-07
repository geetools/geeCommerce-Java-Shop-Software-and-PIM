@ECHO OFF

mkdir tmp

cd C:/dev/github/geecommerce

#tar cfz tools/docker/images/jobs-deamon/tmp/gc-webapp.tar.gz webapp
#tar cfz tools/docker/images/jobs-deamon/tmp/gc-projects.tar.gz projects --exclude="./projects/*/modules/*" --exclude="./projects/*/target"
#tar cfz tools/docker/images/jobs-deamon/tmp/gc-modules.tar.gz --exclude="*/target/*" --exclude="*/src/*" --exclude="*/src-api/*" modules
#tar cfz tools/docker/images/jobs-deamon/tmp/gc-admin-panel.tar.gz admin-panel --exclude="target"
tar cfz tools/docker/images/jobs-deamon/tmp/gc-scripts.tar.gz tools/scripts tools/cron


cd C:\dev\github\geecommerce\tools\docker\images\jobs-deamon

docker rm gc_demo_jobs_daemon
docker build -t geecommerce/jobs-daemon:latest .

