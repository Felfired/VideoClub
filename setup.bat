@echo off

:: Create a Docker network
docker network create --subnet 192.168.0.0/24 dvd-net

:: Build and create Spark-Java container
docker build -t service:latest -f Dockerfile .

docker create --name spark-java --network dvd-net --ip 192.168.0.10 ^
  -p 8080:8080 ^
  service:latest

echo "Docker containers and network are created but not started. Start by running start.bat.
