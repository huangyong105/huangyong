#!/bin/bash
r=/usr/local/tomcat-8093-sdkscreen
s=./target/
rs=$r/screen

#mvn clean compile war:exploded
mvn compile war:exploded

# 附加资源
# cp -a src/main/WebRoot/*.html src/main/WebRoot/js src/main/WebRoot/page $s/

ssh cd.200 "nohup $r/bin/shutdown.sh"

sleep 2

rsync -v -r --delete $s/ cd.200:$rs/

#ssh cd.200 "[ -d $r/backup_dc ] && cp -r $r/backup_dc/* $rs/WEB-INF/classes/"

ssh cd.200 "nohup $r/bin/startup.sh"