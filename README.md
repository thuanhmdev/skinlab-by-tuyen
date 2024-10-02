Lệnh chạy docker: docker compose -p thuanhm up -d

Automated with crontab:
Lệnh: crontab -e

crontab renew ssl, reload nginx, backup database

<!--
0 0 1 * * docker compose -f /skinlab-by-tuyen/docker-compose.yml -p thuanhm up certbot
0 0 1 * * docker compose -f /skinlab-by-tuyen/docker-compose.yml -p thuanhm restart nginx
0 0 * * * sudo docker compose -p thuanhm exec mysqldb /usr/bin/mysqldump -u root --password=121212 blogapp > ~/backupMySQL/mysql_backup.sql
-->

remove container and images docker:

<!--
docker stop $(docker ps -aq)
docker system prune -af
-->
