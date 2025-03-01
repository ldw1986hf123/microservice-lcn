# 运行环境为JDK 8
FROM openjdk:8
# 暴露端口，根据实际修改
EXPOSE 7001,7001
# 把maven生成的jar包，复制到docker镜像
ADD ./target/*.jar /usr/app/eureka.jar
# 切换镜像中的工作目录
WORKDIR /usr/app
# 运行jar包
ENTRYPOINT ["java", "-jar", "app.jar"]