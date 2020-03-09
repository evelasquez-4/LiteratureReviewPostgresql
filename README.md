# LiteratureReviewPostgresql

# CMDS

# 1. Connect server 
  ssh -p 923 slr@192.80.24.34
# 2. Upload files
  scp -P 923 slr@192.80.24.34:/home/slr/slr_files
# 3. Start Tomcat Server
 sudo $CATALINA_HOME/bin/catalina.sh [start|stop|...]
# 4. BACKUP DB
pg_dump --format=p --schema-only --host=localhost --port=5432 --username=postgres --dbname=dbslr > /home/slr/slr_files/postgreSQL/DB07032020.sql
# 5. Tomcat LOG
sudo cat $CATALINA_HOME/logs/catalina.out
# 6. Deploy 
  a) cd /usr/local/tomcat9/webapps
  b) sudo curl -v -X PUT -u admin:password -T /home/slr/slr_files/tomcat/slr.war 'http://localhost:8081/manager/text/deploy?path=/slr&update=true'
# 7.Undeploy
  wget http://admin:password@localhost:8081/manager/text/undeploy?path=/usr/local/tomcat9/webapps/slr.war
