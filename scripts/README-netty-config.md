# Netty配置优化方案 - 简化版

## 📋 概述

这是一个**简化版**的Netty配置优化方案，直接集成到现有的`application.yml`和`application-dev.yml`配置文件中，无需额外的配置文件，避免资源浪费。

## 🎯 设计理念

- ✅ **无侵入性**：不破坏现有配置结构
- ✅ **简单实用**：通过配置参数控制，无需重启
- ✅ **自适应调整**：根据设备规模自动优化
- ✅ **实时监控**：提供REST API查看配置状态

## 🔧 配置方式

### 1. 基础配置（application.yml）

```yaml
server:
  # 设备规模配置
  device:
    scale: small             # 设备规模: small(0-1万), medium(1万-10万), large(10万+)
    expected-count: 5000     # 预期设备数量
    
  # 自适应配置
  adaptive:
    enabled: true            # 启用自适应配置调整
    monitor-interval: 300    # 监控间隔(秒)
    auto-scale: true         # 自动扩缩容建议
```

### 2. 环境配置（application-dev.yml）

```yaml
server:
  device:
    scale: small             # 开发环境使用小规模配置
    expected-count: 1000     # 开发环境预期1000台设备
  
  # Netty线程配置 - 开发环境优化
  netty:
    mqtt:
      worker-threads: 8      # MQTT Worker线程数
      business-threads: 4    # MQTT 业务线程数
      max-connections: 10000 # MQTT最大连接数
      queue-size: 2000       # 队列大小
    tcp:
      worker-threads: 6      # TCP Worker线程数
      business-threads: 3    # TCP 业务线程数  
      max-connections: 5000  # TCP最大连接数
      queue-size: 1500       # 队列大小
```

## 📊 配置规模对比

| 规模 | 设备数量 | MQTT线程 | TCP线程 | 内存需求 | 适用场景 |
|------|---------|----------|---------|----------|----------|
| **small** | 0-1万 | 8W+4B | 6W+3B | 2GB | 开发、测试、小企业 |
| **medium** | 1万-10万 | 32W+16B | 24W+12B | 4GB | 中型企业、区域项目 |
| **large** | 10万+ | 64W+32B | 48W+24B | 8GB+ | 大型企业、城市级 |

> W=Worker线程，B=Business线程

## 🏗️ 架构设计

项目采用**极简架构**，只有两个核心类：

- **`NettyConfigProperties`**: 读取配置文件，提供配置属性
- **`NettyConfigFactory`**: 根据配置创建NettyConfig，提供工厂方法  
- **`NettyConfigController`**: 提供REST API监控接口

```
application.yml -> NettyConfigProperties -> NettyConfigFactory -> NettyConfig
                                                     ↓
                                          NettyConfigController (监控API)
```

## 🚀 使用方法

### 1. 启动时自动配置

系统启动时会根据`device.scale`配置自动调整线程数：

```bash
# 启动日志示例
2025-07-09 10:00:00 INFO - 根据设备规模(small)自动调整了Netty配置
2025-07-09 10:00:00 INFO - === Netty配置摘要 ===
2025-07-09 10:00:00 INFO - 设备规模: small (预期1000台)
2025-07-09 10:00:00 INFO - MQTT: Worker=8, Business=4, MaxConn=10000
2025-07-09 10:00:00 INFO - TCP: Worker=6, Business=3, MaxConn=5000
```

### 2. 运行时调整

#### 通过API调整规模：
```bash
# 调整为中规模
curl -X POST "http://localhost:8080/monitor/netty/adjust-scale?scale=medium"

# 更新设备数量
curl -X POST "http://localhost:8080/monitor/netty/update-device-count?deviceCount=25000"
```

#### 查看当前配置：
```bash
# 获取配置摘要
curl "http://localhost:8080/monitor/netty/config"

# 获取优化建议
curl "http://localhost:8080/monitor/netty/suggestion?protocol=mqtt&actualConnections=5000"
```

### 3. 配置文件调整

如果设备数量发生变化，只需修改配置文件：

```yaml
# 生产环境 - application-prod.yml
server:
  device:
    scale: medium           # 调整为中规模
    expected-count: 50000   # 预期5万设备
  netty:
    mqtt:
      worker-threads: 32    # 手动指定线程数（可选）
      business-threads: 16
      max-connections: 150000
```

## 📈 监控和优化

### 1. 查看资源使用情况

```bash
curl "http://localhost:8080/monitor/netty/resource-usage"
```

返回示例：
```json
{
  "code": 200,
  "data": {
    "memory": {
      "used": "512 MB",
      "max": "2048 MB", 
      "utilization": "25.0%"
    },
    "threads": {
      "count": 45,
      "processors": 8
    },
    "currentConfig": {
      "scale": "small",
      "expectedDevices": 1000,
      "adaptiveEnabled": true
    }
  }
}
```

### 2. 获取优化建议

```bash
curl "http://localhost:8080/monitor/netty/suggestion?protocol=mqtt&actualConnections=2000"
```

返回示例：
```json
{
  "code": 200,
  "data": {
    "protocol": "MQTT",
    "actualConnections": 2000,
    "utilization": "20.0%",
    "suggestion": "MQTT协议连接利用率较低(20.0%)，可考虑缩容节约资源",
    "status": "LOW"
  }
}
```

## ⚡ 快速切换指南

### 开发环境 → 测试环境
```yaml
# application-test.yml
server:
  device:
    scale: small
    expected-count: 3000
```

### 测试环境 → 生产环境
```yaml
# application-prod.yml  
server:
  device:
    scale: medium        # 或 large
    expected-count: 50000
```

### 紧急扩容
```bash
# 快速调整为大规模配置
curl -X POST "http://localhost:8080/monitor/netty/adjust-scale?scale=large"
```

## 🔍 故障排查

### 1. 配置未生效
检查配置文件：
```bash
curl "http://localhost:8080/monitor/netty/config" | jq '.data.deviceScale'
```

### 2. 资源利用率异常
```bash
# 检查各协议利用率
curl "http://localhost:8080/monitor/netty/suggestion?protocol=mqtt&actualConnections=实际连接数"
curl "http://localhost:8080/monitor/netty/suggestion?protocol=tcp&actualConnections=实际连接数"
```

### 3. 性能问题
```bash
# 查看资源使用情况
curl "http://localhost:8080/monitor/netty/resource-usage"
```

## 💡 最佳实践

### 1. 渐进式优化
```
开始：small规模 → 监控性能 → 按需调整到medium → 最终调整到large
```

### 2. 配置建议
- **开发环境**：始终使用`small`配置
- **测试环境**：根据测试规模选择`small`或`medium`
- **生产环境**：根据实际设备数量选择`medium`或`large`

### 3. 监控频率
- **开发环境**：10分钟检查一次
- **生产环境**：5分钟检查一次
- **高峰期**：1分钟检查一次

## 🎉 总结

这个简化方案的优势：

✅ **零学习成本**：基于现有配置文件  
✅ **零运维负担**：自动调整，无需手动配置  
✅ **零资源浪费**：按需分配，实时监控  
✅ **零停机切换**：运行时调整，无需重启  

**适用场景**：适合中小型IoT项目，设备数量在1万-50万之间的场景。 