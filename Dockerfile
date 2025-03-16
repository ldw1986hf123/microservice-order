# 基于 OpenJDK 运行环境
FROM openjdk:8-jdk-slim

# 设置工作目录
WORKDIR /app

# 复制项目 jar 文件到容器中
COPY order-app/target/microservice-order.jar microservice-order.jar

# 暴露应用端口（根据你的项目配置修改）
EXPOSE 8002

# 启动命令
ENTRYPOINT ["java", "-jar", "microservice-order.jar"]