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
# 5. Permisos Usuario
  a)Permisos directorios tomcat
  # chmod -R g+w /opt/tomcat/conf
  # chmod -R g+w /opt/tomcat/logs
  # chmod -R g+w /opt/tomcat/temp
  # chmod -R g+w /opt/tomcat/webapps
  # chmod -R g+w /opt/tomcat/work
  b)“sticky-bit” de grupo (para que los nuevos ficheros/directorios creados por cualquier usuario pertenezcan al grupo “tomcat”)
  # chmod -R g+s /opt/tomcat/conf
  # chmod -R g+s /opt/tomcat/logs
  # chmod -R g+s /opt/tomcat/temp
  # chmod -R g+s /opt/tomcat/webapps
  # chmod -R g+s /opt/tomcat/work
  c) Establecer que los archivos creados sean visto por los usuarios del grupo Tomcat
  #umask 002
  
  d) Referencia
  # https://rubensa.wordpress.com/2013/02/12/setting-tomcat-for-shared-use/
# 6. Tomcat LOG
sudo cat $CATALINA_HOME/logs/catalina.out
# 7. Deploy 
  a) cd /usr/local/tomcat9/webapps
  b) sudo curl -v -X PUT -u admin:password -T /home/slr/slr_files/tomcat/slr.war 'http://localhost:8081/manager/text/deploy?path=/slr&update=true'
# 8.Undeploy
  wget http://admin:password@localhost:8081/manager/text/undeploy?path=/usr/local/tomcat9/webapps/slr.war
