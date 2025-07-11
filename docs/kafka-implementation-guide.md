# Kafka消息分发实施指南

## 概述

本指南详细说明如何在IoT平台中实施Kafka消息分发，支持50台设备的高性能消息处理。通过重新设计模块架构，避免了循环依赖问题。

## 架构设计

### 模块职责划分

#### sydh-mq模块
- Kafka Topic配置和自动创建
- Kafka生产者实现
- 消息发送逻辑

#### sydh-iot-data模块  
- Kafka消费者实现
- 设备消息处理逻辑
- 业务逻辑集成

## 前置条件

### 1. 环境要求
- Java 8+
- Kafka 2.8+
- Zookeeper 3.6+
- Spring Boot 2.7+

### 2. Kafka环境搭建

#### 下载安装Kafka
```bash
# 下载Kafka
wget https://downloads.apache.org/kafka/2.8.0/kafka_2.13-2.8.0.tgz
tar -xzf kafka_2.13-2.8.0.tgz
cd kafka_2.13-2.8.0
```

#### 启动Kafka服务
```bash
# 启动Zookeeper
bin/zookeeper-server-start.sh config/zookeeper.properties

# 启动Kafka
bin/kafka-server-start.sh config/server.properties
```

## 实施步骤

### 第一步：确认模块结构

确认以下文件已正确创建：

#### sydh-mq模块
- `KafkaTopicConfig.java` - Topic配置
- `KafkaMessageProducer.java` - 生产者
- `MessageProducer.java` - 原有生产者接口

#### sydh-iot-data模块
- `KafkaConsumerConfig.java` - 消费者配置
- `kafka/KafkaDeviceDataConsumer.java` - 数据消费者
- `kafka/KafkaDeviceStatusConsumer.java` - 状态消费者
- `kafka/KafkaDeviceCommandConsumer.java` - 指令消费者
- `kafka/KafkaDeviceEventConsumer.java` - 事件消费者
- `kafka/KafkaDeviceOtaConsumer.java` - OTA消费者

### 第二步：配置修改

#### 1. 修改配置文件
在 `application-dev.yml` 中设置：
```yaml
iot:
  message:
    mode: kafka  # 切换到Kafka模式
```

#### 2. 验证Topic创建
```bash
# 查看Topic列表
bin/kafka-topics.sh --list --bootstrap-server localhost:9092

# 查看Topic详情
bin/kafka-topics.sh --describe --topic iot-device-data --bootstrap-server localhost:9092
```

### 第三步：解决循环依赖

#### 原有循环依赖问题
```
[sydh-modbus-tcp,sydh-iot-data,mqtt-broker,iot-server-core]
```

#### 解决方案
1. **消费者迁移**: 将所有Kafka消费者从`sydh-mq`移动到`sydh-iot-data`
2. **单向依赖**: 确保`sydh-iot-data`依赖`sydh-mq`，而不是反向
3. **职责分离**: 生产者和Topic配置保留在`sydh-mq`，消费者放在`sydh-iot-data`

### 第四步：应用部署

#### 1. 编译项目
```bash
mvn clean package -DskipTests
```

#### 2. 启动应用
```bash
java -jar sydh-admin/target/sydh-admin.jar
```

#### 3. 检查日志
```bash
tail -f logs/spring.log | grep -i kafka
```

### 第五步：功能验证

#### 1. 设备连接测试
使用MQTT客户端连接并发送测试消息：
```bash
# 安装mosquitto客户端
sudo apt-get install mosquitto-clients

# 发送测试消息
mosquitto_pub -h localhost -p 1883 -t "DEVICE001/property/post" -m '{"temperature":25.5}'
```

#### 2. 消息分区验证
```bash
# 查看消息分布
bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic iot-device-data --from-beginning --property print.partition=true --property print.key=true
```

#### 3. 性能测试
```bash
# 生产者性能测试
bin/kafka-producer-perf-test.sh --topic iot-device-data --num-records 10000 --record-size 1024 --throughput 1000 --producer-props bootstrap.servers=localhost:9092

# 消费者性能测试  
bin/kafka-consumer-perf-test.sh --topic iot-device-data --messages 10000 --bootstrap-server localhost:9092
```

## 消息流程

### 1. 生产者流程
```
设备消息 → MQTT/HTTP服务器 → KafkaMessageProducer → Kafka Topic
```

### 2. 消费者流程  
```
Kafka Topic → KafkaDeviceXxxConsumer → 业务处理逻辑
```

### 3. Topic路由
- **设备数据**: `iot-device-data`
- **设备状态**: `iot-device-status`  
- **设备指令**: `iot-device-commands`
- **设备事件**: `iot-device-events`
- **OTA升级**: `iot-device-ota`

## 监控配置

### 1. 应用指标
访问指标端点：
```
http://localhost:8080/actuator/prometheus
```

### 2. 关键指标监控
- `kafka_producer_record_send_total` - 发送消息总数
- `kafka_consumer_records_consumed_total` - 消费消息总数
- `kafka_consumer_lag` - 消费延迟
- `kafka_producer_batch_size_avg` - 平均批次大小

### 3. 告警规则示例
```yaml
groups:
- name: kafka-alerts
  rules:
  - alert: KafkaConsumerLag
    expr: kafka_consumer_lag > 1000
    for: 5m
    labels:
      severity: warning
    annotations:
      summary: "Kafka消费延迟过高"
      
  - alert: KafkaProducerErrors
    expr: rate(kafka_producer_errors_total[5m]) > 0.1
    for: 2m
    labels:
      severity: critical
    annotations:
      summary: "Kafka生产者错误率过高"
```

## 运维管理

### 1. 日常运维命令

#### Topic管理
```bash
# 创建Topic
bin/kafka-topics.sh --create --topic new-topic --partitions 6 --replication-factor 1 --bootstrap-server localhost:9092

# 修改分区数
bin/kafka-topics.sh --alter --topic iot-device-data --partitions 12 --bootstrap-server localhost:9092

# 删除Topic
bin/kafka-topics.sh --delete --topic old-topic --bootstrap-server localhost:9092
```

#### 消费者组管理
```bash
# 查看消费者组
bin/kafka-consumer-groups.sh --list --bootstrap-server localhost:9092

# 查看消费者组详情
bin/kafka-consumer-groups.sh --describe --group device-data-processor --bootstrap-server localhost:9092

# 重置偏移量
bin/kafka-consumer-groups.sh --reset-offsets --group device-data-processor --topic iot-device-data --to-earliest --execute --bootstrap-server localhost:9092
```

### 2. 性能调优

#### 生产者优化
```yaml
spring:
  kafka:
    producer:
      batch-size: 32768      # 增加批次大小
      linger-ms: 10          # 增加延迟时间
      compression-type: lz4   # 使用LZ4压缩
      buffer-memory: 67108864 # 增加缓冲区
```

#### 消费者优化
```yaml
spring:
  kafka:
    consumer:
      max-poll-records: 500   # 增加单次拉取记录数
      fetch-min-size: 4096    # 增加最小拉取大小
    listener:
      concurrency: 6          # 增加并发数
```

### 3. 故障处理

#### 常见问题及解决方案

1. **循环依赖错误**
   - 确认消费者在sydh-iot-data模块中
   - 确认sydh-mq没有依赖其他业务模块
   - 检查pom.xml依赖配置

2. **消费延迟过高**
   - 增加消费者并发数
   - 优化业务处理逻辑
   - 检查网络和磁盘性能

3. **分区数据倾斜**
   - 检查消息Key分布
   - 重新设计分区策略
   - 考虑增加分区数

4. **消息丢失**
   - 检查acks配置
   - 确认重试机制
   - 验证消费者提交策略

## 扩展方案

### 1. 生产环境配置
```yaml
iot:
  kafka:
    partitions: 12           # 增加分区数
    replication-factor: 3    # 增加副本数

spring:
  kafka:
    producer:
      acks: all             # 等待所有副本确认
      retries: 10           # 增加重试次数
    consumer:
      isolation-level: read_committed  # 事务支持
```

### 2. 集群扩展
- 添加Kafka节点
- 分区重新分布
- 消费者组水平扩展

### 3. 高可用配置
- 多数据中心部署
- 跨区域复制
- 自动故障转移

## 回滚方案

### 紧急回滚到队列模式
```yaml
iot:
  message:
    mode: queue  # 切换回队列模式
```

### 数据迁移
1. 停止新消息写入
2. 消费完Kafka中剩余消息  
3. 切换配置重启应用
4. 验证功能正常

## 架构优势

### 1. 解决循环依赖
- 清晰的模块职责划分
- 单向依赖关系
- 易于维护和扩展

### 2. 高性能
- 分区并行处理
- 批量消息处理
- 异步消息传递

### 3. 可靠性
- 消息持久化
- 手动提交确保不丢失
- 故障重试机制

## 性能基准

### 预期性能指标
- **吞吐量**: 10,000 msg/s
- **延迟**: P99 < 100ms
- **可用性**: 99.9%
- **数据丢失**: 0

### 容量规划
- **50台设备**: 6分区足够
- **100台设备**: 建议12分区  
- **500台设备**: 建议24分区

### 存储估算
- **消息大小**: 平均1KB
- **保留时间**: 7天
- **存储需求**: 约60GB/天（50台设备） 