cd /home/steven/microservice-uc
mvn clean package -DskipTests
docker build -t ldw_microservice/microservice-uc:latest .

docker login --username=ldw1986hf123@163.com crpi-4p5cpj1jkb2orw5h.cn-hangzhou.personal.cr.aliyuncs.com
docker push ldw_microservice/microservice-uc:latest

# 拉取新镜像并重启 service-user
docker-compose pull microservice-uc
docker-compose up -d microservice-uc
