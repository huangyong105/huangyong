#!/bin/bash

r=/home/tomcat/apache-tomcat-im
s=im-manager/target/ROOT
rs=$r/webapps/im

ssh cd.200 "nohup $r/bin/shutdown.sh"

sleep 2

#mvn clean package -Dmaven.test.skip=true
mvn compile -Dmaven.test.skip=true
rsync -v -r --delete $s/ cd.200:$rs/



ssh -t cd.200 "nohup $r/bin/startup.sh"
#if [ "$1" = "k" ]; then
#    echo "";
#else
#    ssh -t cd.200 "/etc/init.d/docker_tomcat r im"
#fi       
