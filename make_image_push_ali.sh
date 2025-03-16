cd /home/steven/microservice-uc
git pull
mvn clean package -DskipTests
docker build -t microservice-uc:latest .

#docker login --username=ldw1986hf123@163.com crpi-4p5cpj1jkb2orw5h.cn-hangzhou.personal.cr.aliyuncs.com
#docker push ldw_microservice/microservice-uc:latest

# 拉取新镜像并重启 service-user
#docker-compose pull microservice-uc
cd /home/steven/microservice-lcn

# shellcheck disable=SC2016
echo '$1' 重新打包启动
docker-compose up -d '$1'   #与docker-compose.yml中定义的service对应
tail -500f /home/steven/microservice-uc/logs/myapp-info.log