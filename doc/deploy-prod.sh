mvn clean package -Dmaven.test.skip=true -Pprod
rsync uuc-manager/target/ROOT.war root@uuc-prod:/home/tomcat/tomcat/webapps
ssh -t -p 32222 root@uuc-prod  '/etc/init.d/tomcat restart'
