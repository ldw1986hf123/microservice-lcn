docker run --name mysql-container -v /home/user/mysql-data:/var/lib/mysql  -e MYSQL_ROOT_PASSWORD=aaaaa888 -p 3306:3306 -d mysql:8.0

--name mysql-container：容器名称，可自定义。
-e MYSQL_ROOT_PASSWORD=rootpassword：设置 MySQL root 用户的密码（必须指定，否则容器启动会失败）。
-p 3306:3306：将容器的 3306 端口映射到主机的 3306 端口。
-d：后台运行容器。
mysql:8.0：使用的镜像和版本。

-v /home/user/mysql-data:/var/lib/mysql : 将宿主机的 /home/user/mysql-data 映射到容器内的 /var/lib/mysql。