mvn clean package -Dmaven.test.skip=true -Pprod
rsync  -v -r --delete im-manager/target/im/ live:/usr/local/tomcat-8088-im/webapps/im/
ssh -t live  '/usr/local/tomcat-8088-im/tomcat restart'
