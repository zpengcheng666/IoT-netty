# IoT平台数十万设备负载均衡部署指南

## 📋 概述

本指南详细说明如何将现有的IoT平台从支持50台设备扩展到支持30万台设备的高可用集群架构。

### 🎯 目标架构
- **设备容量**: 30万设备并发连接
- **消息吞吐**: 100万条/秒
- **高可用性**: 99.99%服务可用性
- **故障恢复**: 秒级故障切换

## 🏗️ 架构组件

### 1. 负载均衡层
- **HAProxy**: MQTT/TCP协议负载均衡
- **Nginx**: HTTP/WebSocket七层负载均衡
- **云负载均衡器**: 公网入口（推荐）

### 2. 协议接入层
- **MQTT Broker**: 3实例，每实例10万连接
- **HTTP Server**: 4实例，无状态横向扩展
- **CoAP Server**: 2实例，UDP协议支持

### 3. 消息队列集群
- **Kafka**: 3节点集群，24分区
- **Zookeeper**: 3节点集群

### 4. 缓存集群
- **Redis**: 3主3从集群
- **会话存储**: 分布式存储

### 5. 数据库集群
- **MySQL**: 1主2从读写分离
- **TDengine**: 时序数据存储
- **ProxySQL**: 数据库代理

## 🚀 部署步骤

### 步骤1: 环境准备

#### 服务器规格推荐
```yaml
# 负载均衡器 (2台)
CPU: 8核
内存: 16GB
网络: 10Gbps
磁盘: 100GB SSD

# 应用服务器 (6台)
CPU: 16核
内存: 32GB
网络: 10Gbps
磁盘: 500GB SSD

# 数据库服务器 (5台)
CPU: 24核
内存: 64GB
网络: 10Gbps
磁盘: 1TB NVMe SSD

# Kafka集群 (3台)
CPU: 16核
内存: 32GB
网络: 10Gbps
磁盘: 2TB SSD

# Redis集群 (6台)
CPU: 8核
内存: 32GB
网络: 10Gbps
磁盘: 200GB SSD
```

#### 操作系统优化
```bash
# 内核参数优化
echo "net.core.somaxconn = 65535" >> /etc/sysctl.conf
echo "net.core.netdev_max_backlog = 5000" >> /etc/sysctl.conf
echo "net.ipv4.tcp_max_syn_backlog = 65535" >> /etc/sysctl.conf
echo "net.ipv4.tcp_fin_timeout = 30" >> /etc/sysctl.conf
echo "net.ipv4.tcp_keepalive_time = 1200" >> /etc/sysctl.conf
echo "net.ipv4.tcp_max_tw_buckets = 5000" >> /etc/sysctl.conf
echo "fs.file-max = 1000000" >> /etc/sysctl.conf
sysctl -p

# 文件描述符限制
echo "* soft nofile 1000000" >> /etc/security/limits.conf
echo "* hard nofile 1000000" >> /etc/security/limits.conf

# 创建部署目录
mkdir -p /opt/iot-platform/{kafka,redis,mysql,haproxy,nginx}
```

### 步骤2: Kafka集群部署

```bash
# 1. 部署Kafka集群
cd /opt/iot-platform/kafka
wget https://github.com/your-repo/iot-platform/raw/main/scripts/docker-compose-kafka-cluster.yml
cp docker-compose-kafka-cluster.yml docker-compose.yml

# 2. 配置环境变量
cat > .env << EOF
KAFKA_SERVERS=kafka-1:29092,kafka-2:29093,kafka-3:29094
EOF

# 3. 启动Kafka集群
docker-compose up -d

# 4. 验证集群状态
docker exec iot-kafka-1 kafka-topics --bootstrap-server kafka-1:29092 --list
```

### 步骤3: Redis集群部署

```bash
# 1. 部署Redis集群
cd /opt/iot-platform/redis
wget https://github.com/your-repo/iot-platform/raw/main/scripts/docker-compose-redis-cluster.yml
cp docker-compose-redis-cluster.yml docker-compose.yml

# 2. 配置密码
export REDIS_PASSWORD="your_secure_redis_password"

# 3. 启动Redis集群
docker-compose up -d

# 4. 验证集群状态
docker exec iot-redis-cluster-1 redis-cli -a $REDIS_PASSWORD cluster info
```

### 步骤4: 数据库集群部署

```bash
# 1. 部署MySQL集群
cd /opt/iot-platform/mysql
wget https://github.com/your-repo/iot-platform/raw/main/scripts/database/mysql-cluster.yml
cp mysql-cluster.yml docker-compose.yml

# 2. 配置数据库密码
cat > .env << EOF
MYSQL_ROOT_PASSWORD=your_secure_mysql_password
MYSQL_USER=iot_user
MYSQL_PASSWORD=your_user_password
MYSQL_REPLICATION_PASSWORD=repl_password
EOF

# 3. 启动数据库集群
docker-compose up -d

# 4. 验证主从复制
docker exec iot-mysql-slave1 mysql -u root -p$MYSQL_ROOT_PASSWORD -e "SHOW SLAVE STATUS\G"
```

### 步骤5: 应用服务部署

```bash
# 1. 构建应用镜像
cd /path/to/your/iot-project
docker build -t iot-platform:latest .

# 2. 创建应用配置
cp sydh-admin/src/main/resources/application-cluster.yml application.yml

# 3. 修改配置文件中的连接地址
sed -i 's/localhost/your-cluster-host/g' application.yml

# 4. 启动应用实例
for i in {1..6}; do
  docker run -d \
    --name iot-app-$i \
    -p $((8080+$i)):8080 \
    -v $(pwd)/application.yml:/app/application.yml \
    --network iot-network \
    iot-platform:latest \
    --spring.profiles.active=cluster
done
```

### 步骤6: 负载均衡器部署

#### HAProxy部署
```bash
# 1. 部署HAProxy
cd /opt/iot-platform/haproxy
wget https://github.com/your-repo/iot-platform/raw/main/scripts/load-balancer/haproxy.cfg

# 2. 修改后端服务器地址
sed -i 's/mqtt-broker-1/your-mqtt-host-1/g' haproxy.cfg
sed -i 's/api-server-1/your-api-host-1/g' haproxy.cfg

# 3. 启动HAProxy
docker run -d \
  --name iot-haproxy \
  -p 1883:1883 \
  -p 8090:8090 \
  -p 8404:8404 \
  -v $(pwd)/haproxy.cfg:/usr/local/etc/haproxy/haproxy.cfg \
  haproxy:2.4
```

#### Nginx部署
```bash
# 1. 部署Nginx
cd /opt/iot-platform/nginx
wget https://github.com/your-repo/iot-platform/raw/main/scripts/load-balancer/nginx.conf

# 2. 修改后端服务器地址
sed -i 's/api-server-1/your-api-host-1/g' nginx.conf

# 3. 启动Nginx
docker run -d \
  --name iot-nginx \
  -p 80:80 \
  -p 443:443 \
  -v $(pwd)/nginx.conf:/etc/nginx/nginx.conf \
  nginx:1.21
```

## 📊 性能调优

### JVM参数优化
```bash
# 应用服务器JVM参数
export JAVA_OPTS="-Xms8g -Xmx8g \
  -XX:+UseG1GC \
  -XX:MaxGCPauseMillis=200 \
  -XX:+HeapDumpOnOutOfMemoryError \
  -XX:HeapDumpPath=/var/log/iot \
  -Dserver.tomcat.max-threads=500 \
  -Dserver.tomcat.max-connections=20000"
```

### 操作系统优化
```bash
# TCP连接优化
echo "net.ipv4.tcp_tw_reuse = 1" >> /etc/sysctl.conf
echo "net.ipv4.tcp_tw_recycle = 1" >> /etc/sysctl.conf
echo "net.ipv4.tcp_syncookies = 1" >> /etc/sysctl.conf

# 内存优化
echo "vm.swappiness = 10" >> /etc/sysctl.conf
echo "vm.dirty_ratio = 15" >> /etc/sysctl.conf
echo "vm.dirty_background_ratio = 5" >> /etc/sysctl.conf

sysctl -p
```

## 📈 监控配置

### Prometheus监控
```yaml
# prometheus.yml
global:
  scrape_interval: 15s

scrape_configs:
  - job_name: 'iot-applications'
    static_configs:
      - targets: ['app-1:8080', 'app-2:8080', 'app-3:8080']
    metrics_path: '/actuator/prometheus'

  - job_name: 'kafka'
    static_configs:
      - targets: ['kafka-1:9092', 'kafka-2:9092', 'kafka-3:9092']

  - job_name: 'redis'
    static_configs:
      - targets: ['redis-1:6379', 'redis-2:6379', 'redis-3:6379']

  - job_name: 'mysql'
    static_configs:
      - targets: ['mysql-master:3306', 'mysql-slave1:3306']

  - job_name: 'haproxy'
    static_configs:
      - targets: ['haproxy:8404']
    metrics_path: '/stats;csv'

  - job_name: 'nginx'
    static_configs:
      - targets: ['nginx:80']
    metrics_path: '/nginx_status'
```

### Grafana仪表板
```bash
# 启动监控服务
docker run -d \
  --name prometheus \
  -p 9090:9090 \
  -v $(pwd)/prometheus.yml:/etc/prometheus/prometheus.yml \
  prom/prometheus

docker run -d \
  --name grafana \
  -p 3000:3000 \
  -e GF_SECURITY_ADMIN_PASSWORD=admin \
  grafana/grafana
```

## 🔧 运维管理

### 健康检查脚本
```bash
#!/bin/bash
# health-check.sh

echo "=== IoT Platform Health Check ==="

# 检查Kafka集群
echo "Checking Kafka cluster..."
for broker in kafka-1:9092 kafka-2:9092 kafka-3:9092; do
  if kafkacat -b $broker -L > /dev/null 2>&1; then
    echo "✓ Kafka $broker is healthy"
  else
    echo "✗ Kafka $broker is down"
  fi
done

# 检查Redis集群
echo "Checking Redis cluster..."
for node in redis-1:7000 redis-2:7000 redis-3:7000; do
  if redis-cli -h ${node%:*} -p ${node#*:} ping > /dev/null 2>&1; then
    echo "✓ Redis $node is healthy"
  else
    echo "✗ Redis $node is down"
  fi
done

# 检查MySQL主从
echo "Checking MySQL replication..."
slave_status=$(mysql -h mysql-slave1 -u root -p$MYSQL_ROOT_PASSWORD -e "SHOW SLAVE STATUS\G" 2>/dev/null | grep "Slave_IO_Running\|Slave_SQL_Running")
if echo "$slave_status" | grep -q "Yes"; then
  echo "✓ MySQL replication is healthy"
else
  echo "✗ MySQL replication has issues"
fi

# 检查应用服务
echo "Checking application instances..."
for i in {1..6}; do
  if curl -f http://app-$i:8080/actuator/health > /dev/null 2>&1; then
    echo "✓ App instance $i is healthy"
  else
    echo "✗ App instance $i is down"
  fi
done

echo "Health check completed."
```

### 自动扩容脚本
```bash
#!/bin/bash
# auto-scale.sh

# 获取当前连接数
current_connections=$(curl -s http://haproxy:8404/stats | grep mqtt_backend | awk -F',' '{print $5}')

# 扩容阈值：单实例连接数超过8万
threshold=80000

if [ "$current_connections" -gt "$threshold" ]; then
  echo "High load detected. Scaling up..."
  
  # 启动新的MQTT Broker实例
  docker run -d \
    --name mqtt-broker-4 \
    --network iot-network \
    -p 1884:1883 \
    eclipse-mosquitto:2.0
  
  # 更新HAProxy配置
  echo "server mqtt-4 mqtt-broker-4:1883 check" >> /opt/iot-platform/haproxy/haproxy.cfg
  docker exec iot-haproxy systemctl reload haproxy
  
  echo "Scaling completed."
fi
```

## 🚨 故障处理

### 常见故障及解决方案

#### 1. Kafka消息积压
```bash
# 检查消费者延迟
kafka-consumer-groups --bootstrap-server kafka-1:9092 --describe --all-groups

# 增加消费者实例
docker scale iot-data-processor=6
```

#### 2. Redis内存不足
```bash
# 检查内存使用
redis-cli -h redis-1 info memory

# 调整内存策略
redis-cli -h redis-1 config set maxmemory-policy allkeys-lru
```

#### 3. MySQL主从延迟
```bash
# 检查复制延迟
mysql -h mysql-slave1 -e "SHOW SLAVE STATUS\G" | grep Seconds_Behind_Master

# 重启复制
mysql -h mysql-slave1 -e "STOP SLAVE; START SLAVE;"
```

## 💡 优化建议

### 1. 设备分区策略
- 按地理位置分区
- 按设备类型分区
- 按消息频率分区

### 2. 缓存策略
- 热点数据缓存
- 分级缓存架构
- 缓存预热策略

### 3. 数据库优化
- 分库分表
- 索引优化
- 查询优化

### 4. 网络优化
- 就近接入
- CDN加速
- 专线连接

## 📝 部署检查清单

- [ ] 服务器资源配置符合要求
- [ ] 网络连通性测试通过
- [ ] Kafka集群部署完成
- [ ] Redis集群部署完成
- [ ] MySQL主从复制配置完成
- [ ] 应用服务部署完成
- [ ] 负载均衡器配置完成
- [ ] 监控系统部署完成
- [ ] 健康检查脚本配置完成
- [ ] 日志收集配置完成
- [ ] 备份策略配置完成
- [ ] 安全策略配置完成
- [ ] 压力测试通过
- [ ] 故障切换测试通过

## 🎉 总结

通过以上配置，您的IoT平台将能够支持：

- **30万设备同时在线**
- **100万消息/秒吞吐量**
- **99.99%服务可用性**
- **秒级故障恢复**
- **线性水平扩展能力**

这套架构不仅满足当前需求，还为未来扩展到百万级设备提供了坚实基础。 