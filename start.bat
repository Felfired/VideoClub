@echo off

:: Create MySQL container
docker run -d --name mysql --network dvd-net --ip 192.168.0.11 ^
  -p 3306:3306 ^
  -e MYSQL_RANDOM_ROOT_PASSWORD=yes ^
  -e MYSQL_DATABASE=dvd ^
  -e MYSQL_USER=felfired ^
  -e MYSQL_PASSWORD=admin_root ^
  -e MYSQL_HOST=mysql ^
  -e MYSQL_PORT=3306 ^
  -v mysql:/var/lib/mysql ^
  -v %cd%\mysql:/docker-entrypoint-initdb.d ^
  mysql:latest

:: Start the Spark-Java container
docker start spark-java

echo "Docker containers are now starting."