@echo off

:: Stop and remove the Spark-Java container
docker stop spark-java
docker rm spark-java

:: Stop and remove the MySQL container
docker stop mysql
docker rm mysql

:: Remove the Docker network
docker network rm dvd-net

echo "Docker containers and network have been stopped and removed."