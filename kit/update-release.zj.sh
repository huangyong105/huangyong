#!/bin/bash


s=im-manager/target/im
rs=/cygdrive/f/pub/srv


#mvn clean package -Dmaven.test.skip=true
mvn clean package -e -Dmaven.test.skip=true -Pprod
cp -r $s/ $rs/      
