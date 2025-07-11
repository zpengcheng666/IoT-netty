# sydh物联网平台10万台设备压力测试方案

## 1. 系统配置优化

### 1.1 数据库连接池优化
```yaml
# application-prod.yml
spring:
  datasource:
    dynamic:
      druid:
        initial-size: 50
        min-idle: 50
        max-active: 200  # 从20调整为200
        max-wait: 60000
        timeBetweenEvictionRunsMillis: 60000
        minEvictableIdleTimeMillis: 300000
        maxEvictableIdleTimeMillis: 900000
```

### 1.2 Redis连接池优化
```yaml
spring:
  redis:
    lettuce:
      pool:
        min-idle: 10    # 从0调整为10
        max-idle: 50    # 从8调整为50
        max-active: 100 # 从8调整为100
        max-wait: 3000ms
```

### 1.3 Netty服务器配置优化
```java
// 修改NettyConfig中的线程配置
public NettyConfig(...) {
    int processors = NettyRuntime.availableProcessors();
    this.workerCore = workerGroup > 0 ? workerGroup : processors * 4; // 增加worker线程
    this.businessCore = businessGroup > 0 ? businessGroup : processors * 2; // 增加业务线程
    // ...
}
```

### 1.4 JVM参数优化
```bash
-Xms4g -Xmx8g
-XX:+UseG1GC
-XX:MaxGCPauseMillis=200
-XX:+UnlockExperimentalVMOptions
-XX:+UseJVMCICompiler
-XX:ParallelGCThreads=8
-XX:ConcGCThreads=8
-Dnetty.allocator.type=pooled
-Dio.netty.leakDetection.level=simple
```

## 2. 压力测试环境准备

### 2.1 测试环境配置
- **服务器配置**：16核CPU，32GB内存，SSD存储
- **网络环境**：千兆网络，低延迟
- **操作系统优化**：
  ```bash
  # 增加文件描述符限制
  echo "* soft nofile 1000000" >> /etc/security/limits.conf
  echo "* hard nofile 1000000" >> /etc/security/limits.conf
  
  # 网络参数优化
  echo "net.core.somaxconn = 65535" >> /etc/sysctl.conf
  echo "net.ipv4.tcp_max_syn_backlog = 65535" >> /etc/sysctl.conf
  echo "net.core.netdev_max_backlog = 5000" >> /etc/sysctl.conf
  sysctl -p
  ```

### 2.2 EMQX配置优化
```bash
# emqx.conf
listeners.tcp.default.max_connections = 100000
listeners.tcp.default.max_conn_rate = 1000
listeners.tcp.default.acceptors = 64
listeners.tcp.default.tcp_options.backlog = 1024
listeners.tcp.default.tcp_options.send_timeout = 15s
listeners.tcp.default.tcp_options.nodelay = true

# 内存和性能优化
node.max_ports = 1000000
node.process_limit = 2048000
```

## 3. 测试策略

### 3.1 分阶段测试
1. **连接稳定性测试**：1000 → 10000 → 50000 → 100000设备
2. **数据吞吐量测试**：不同消息频率下的性能表现
3. **混合场景测试**：连接+数据上报+下发指令

### 3.2 测试指标
- **连接指标**：并发连接数、连接成功率、连接时延
- **吞吐量指标**：消息发送速率、接收速率、处理延迟
- **系统指标**：CPU、内存、磁盘、网络使用率
- **应用指标**：JVM GC、数据库连接池、缓存命中率

### 3.3 性能目标
- **并发连接**：支持100,000并发连接
- **消息吞吐**：10,000 msg/s
- **响应时延**：95%请求<100ms，99%请求<500ms
- **系统资源**：CPU<80%，内存<80%

## 4. 压力测试执行

### 4.1 MQTT协议测试
使用 `scripts/mqtt-pressure-test.sh` 脚本执行测试

### 4.2 HTTP协议测试  
使用 `scripts/http-coap-pressure-test.sh` 脚本执行测试

### 4.3 设备模拟器测试
使用 `scripts/DeviceSimulator.java` 进行Java客户端模拟

## 5. 监控和告警

### 5.1 系统监控
使用 `scripts/system-monitor.sh` 实时监控系统状态

### 5.2 应用监控
- JVM监控：堆内存、GC频率、线程数
- 数据库监控：连接数、慢查询、锁等待
- 缓存监控：命中率、内存使用、连接数

### 5.3 网络监控
- 连接数监控
- 带宽使用监控
- 数据包丢失率

## 6. 故障处理

### 6.1 常见问题
1. **连接超时**：检查网络配置和连接池设置
2. **内存不足**：调整JVM参数和优化对象创建
3. **数据库连接超限**：优化连接池配置
4. **消息堆积**：检查消息消费能力

### 6.2 应急预案
1. 自动限流机制
2. 降级服务策略
3. 集群扩容方案

## 7. 测试报告模板

### 7.1 性能测试结果
| 指标 | 目标值 | 实际值 | 达标情况 |
|------|--------|--------|----------|
| 并发连接数 | 100,000 | | |
| 消息吞吐量 | 10,000 msg/s | | |
| 平均响应时间 | <100ms | | |
| CPU使用率 | <80% | | |
| 内存使用率 | <80% | | |

### 7.2 稳定性测试结果
- 测试时长：
- 连接成功率：
- 消息丢失率：
- 系统崩溃次数：

### 7.3 优化建议
基于测试结果提出的系统优化建议

## 8. 持续优化

### 8.1 性能调优
- 根据测试结果持续调优配置参数
- 优化代码热点和瓶颈
- 数据库查询优化

### 8.2 架构优化
- 考虑集群部署
- 负载均衡策略
- 缓存策略优化

通过以上方案，可以系统性地对sydh平台进行10万台设备的压力测试，确保系统在大规模设备接入时的稳定性和性能。 


使用方法
# 1. 系统配置优化（参考指导文档）
cat docs/pressure-test-guide.md

# 2. 启动系统监控
chmod +x scripts/system-monitor.sh
./scripts/system-monitor.sh 5  # 5秒间隔

# 3. 运行MQTT压力测试
chmod +x scripts/mqtt-pressure-test.sh  
./scripts/mqtt-pressure-test.sh 177.7.0.12 1883 sydh sydh

# 4. 运行HTTP/CoAP测试
chmod +x scripts/http-coap-pressure-test.sh
./scripts/http-coap-pressure-test.sh 177.7.0.13 8081

# 5. 运行Java设备模拟器
javac -cp "path/to/paho-mqtt-client-1.2.5.jar:path/to/fastjson2.jar" scripts/DeviceSimulator.java
java -cp ".:path/to/libs/*" -Ddevice.count=100000 DeviceSimulator