cd /home/steven/microservice-uc
mvn clean package -DskipTests
docker build -t ldw_microservice/microservice-uc:latest .
docker push ldw_microservice/microservice-uc:latest

# 拉取新镜像并重启 service-user
docker-compose pull microservice-uc
docker-compose up -d microservice-uc
