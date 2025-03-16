cd /home/steven/$1
git pull
mvn clean package -DskipTests
docker build -t $1:latest .

#docker login --username=ldw1986hf123@163.com crpi-4p5cpj1jkb2orw5h.cn-hangzhou.personal.cr.aliyuncs.com
#docker push ldw_microservice/microservice-uc:latest

# 拉取新镜像并重启 service-user
#docker-compose pull microservice-uc
# shellcheck disable=SC2016
echo $1 重新打包启动
docker-compose up -d $1   #与docker-compose.yml中定义的service对应
docker logs -f $1