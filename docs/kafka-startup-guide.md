# Kafka启动和连接状态检查指南

## 概述

本指南说明如何启动Kafka服务并查看IoT应用的连接状态提示信息。

## 启动方式

### 方式一：快速启动脚本 (推荐)

运行快速启动脚本，选择适合的方式：

```bash
scripts/quick-start-kafka.bat
```

脚本会检查Kafka状态并提供三种启动方案：
- **Docker方式** (推荐，无需手动安装)
- **手动安装** (传统方式)
- **队列模式** (无需Kafka)

### 方式二：Docker启动 (推荐)

如果已安装Docker Desktop：

```bash
# 启动Kafka集群
docker-compose -f scripts/docker-compose-kafka.yml up -d

# 查看状态
docker-compose -f scripts/docker-compose-kafka.yml ps

# 停止服务
docker-compose -f scripts/docker-compose-kafka.yml down
```

### 方式三：手动安装

1. **下载Kafka**
   ```bash
   # 下载并解压
   https://downloads.apache.org/kafka/2.8.0/kafka_2.13-2.8.0.tgz
   # 解压到 D:\kafka_2.13-2.8.0
   ```

2. **运行启动脚本**
   ```bash
   scripts/start-kafka.bat
   ```

## 应用启动日志

### Kafka模式启动成功

当Kafka服务可用且配置为Kafka模式时，你会看到：

```
🔍 正在检查Kafka连接状态...
📡 Kafka服务器: localhost:9092
✅ Kafka连接成功！
🎯 消息模式: kafka (Kafka模式)
📊 发现 5 个Topic
🚀 Kafka消息分发系统已就绪

📋 创建Topic: iot-device-data (分区: 6, 副本: 1)
📋 创建Topic: iot-device-status (分区: 6, 副本: 1)  
📋 创建Topic: iot-device-commands (分区: 6, 副本: 1)
📋 创建Topic: iot-device-events (分区: 6, 副本: 1)
📋 创建Topic: iot-device-ota (分区: 6, 副本: 1)
🎯 IoT Kafka Topics配置完成

🎧 初始化Kafka消费者配置
   📡 服务器: localhost:9092
   👥 消费者组: iot-device-consumer
   🔀 并发数: 3
🏭 创建Kafka消费者工厂
⚙️  Kafka消费者配置完成
🎧 Kafka监听器容器工厂配置完成
✅ Kafka消费者系统已就绪
```

### 队列模式启动

当使用队列模式或Kafka不可用时，你会看到：

```
🔄 消息模式: queue (队列模式)
💡 提示: 设置 iot.message.mode=kafka 以启用Kafka模式
```

### Kafka连接失败

当Kafka服务不可用时，你会看到：

```
🔍 正在检查Kafka连接状态...
📡 Kafka服务器: localhost:9092
❌ Kafka连接失败: Connection refused
⚠️  将使用队列模式作为后备方案
💡 解决方案:
   1. 启动Kafka服务: scripts/start-kafka.bat
   2. 或设置 iot.message.mode=queue 使用队列模式
   3. 检查配置: spring.kafka.bootstrap-servers=localhost:9092
```

## 配置说明

### 启用Kafka模式

在 `application-dev.yml` 中设置：

```yaml
iot:
  message:
    mode: kafka      # 切换到Kafka模式
  kafka:
    enabled: true    # 启用Kafka组件
```

### 保持队列模式

```yaml
iot:
  message:
    mode: queue      # 使用队列模式
  kafka:
    enabled: false   # 禁用Kafka组件
```

## 验证连接

### 检查Kafka服务状态

```bash
# Windows
netstat -an | findstr 9092

# 应该看到 LISTENING 状态
TCP    0.0.0.0:9092           0.0.0.0:0              LISTENING
```

### 使用Kafka UI (如果启用)

访问管理界面：http://localhost:8080

### 命令行验证

```bash
# 列出Topics
docker exec -it iot-kafka kafka-topics --list --bootstrap-server localhost:9092

# 查看Topic详情
docker exec -it iot-kafka kafka-topics --describe --topic iot-device-data --bootstrap-server localhost:9092
```

## 切换模式

### 运行时切换到Kafka

1. 启动Kafka服务
2. 修改配置：`iot.message.mode=kafka`
3. 重启应用

### 运行时切换到队列

1. 修改配置：`iot.message.mode=queue`
2. 重启应用 (可选：停止Kafka服务)

## 故障排除

### Kafka连接超时

**问题**: `org.springframework.kafka.KafkaException: Timed out waiting to get existing topics`

**解决方案**:
1. 检查Kafka服务是否启动
2. 检查端口9092是否被占用
3. 运行 `scripts/quick-start-kafka.bat`

### Docker启动失败

**问题**: Docker命令失败

**解决方案**:
1. 确认Docker Desktop已安装并运行
2. 检查端口冲突 (8080, 9092, 2181)
3. 使用手动安装方式

### Topic创建失败

**问题**: Topic自动创建失败

**解决方案**:
1. 确认Kafka集群正常运行
2. 检查权限配置
3. 手动创建Topic

### 关键指标

启动后可以通过日志观察：
- 📊 Topic数量
- 🔀 消费者并发数  
- 👥 消费者组状态
- 📡 连接状态

