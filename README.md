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
