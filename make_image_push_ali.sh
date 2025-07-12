PROJECT_DIR="/home/ldw/code"

# 脚本遇到任何错误就退出
set -e
echo '先更新lcn自己'
git pull

echo '更新对应的项目'  $1

cd "$PROJECT_DIR"/$1
git pull
mvn clean package -DskipTests
docker build -t $1:latest .

#docker login --username=ldw1986hf123@163.com crpi-4p5cpj1jkb2orw5h.cn-hangzhou.personal.cr.aliyuncs.com
#docker push ldw_microservice/microservice-uc:latest

# 拉取新镜像并重启 service-user
#docker-compose pull microservice-uc
cd "$PROJECT_DIR"/microservice-lcn

# shellcheck disable=SC2016
echo $1 重新打包启动
docker-compose up -d $1   #与docker-compose.yml中定义的service对应
docker logs -f $1