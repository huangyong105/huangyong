mvn clean package -Dmaven.test.skip=true -Pdev
rsync uuc-manager/target/ROOT.war root@uuc.huit.tech:/home/tomcat/tomcat/webapps/
ssh -t -p 22 root@uuc.huit.tech  '/etc/init.d/tomcat restart'
