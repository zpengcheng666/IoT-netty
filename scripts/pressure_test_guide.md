# SYDH IoT Platform 压力测试指南

## 📋 概述

基于你的数据库测试数据，我为SYDH IoT平台生成了三套针对性的压力测试脚本，覆盖MQTT协议核心接口和REST API业务流程的全面测试。

## 🎯 测试脚本概览0

### 1. **MQTT协议核心接口压力测试** (`mqtt_pressure_test.py`)
- **目标**: 专门测试MQTT Broker的核心功能
- **覆盖接口**: 连接认证、消息发布、主题订阅、心跳保活
- **设备规模**: 支持30万并发MQTT连接
- **适用场景**: MQTT协议性能极限测试

### 2. **REST API业务流程压力测试** (`rest_api_pressure_test.py`)  
- **目标**: 测试HTTP API接口的业务处理能力
- **覆盖接口**: 设备管理、数据上报、指令下发、状态查询
- **请求模式**: 基于真实业务权重分配
- **适用场景**: API网关和业务服务压力测试

### 3. **综合业务流程压力测试** (`comprehensive_pressure_test.py`)
- **目标**: 模拟完整的设备生命周期和业务流程
- **测试流程**: 设备注册→MQTT连接→数据上报→指令响应→状态更新
- **业务场景**: 覆盖5种产品类型的真实业务场景
- **适用场景**: 端到端系统性能验证

## 🏗️ 基于真实数据的测试配置

### 产品和设备配置
基于 `init_test_data.sql` 中的真实测试数据：

| 产品ID | 产品名称 | 设备前缀 | 设备数量 | MQTT账号 | 业务场景 |
|--------|----------|----------|----------|----------|----------|
| 1 | 测试传感器产品 | TEST_SENSOR | 100个 | sensor_test | 环境监测、农业大棚、工业车间 |
| 2 | 测试摄像头产品 | TEST_CAMERA | 30个 | camera_test | 安防监控、交通监测、生产监控 |
| 3 | 测试PLC产品 | TEST_PLC | 15个 | plc_test | 制造生产、流水线控制、设备监控 |
| 4 | 测试网关产品 | TEST_GATEWAY | 10个 | gateway_test | 边缘计算、数据汇聚、协议转换 |
| 5 | 测试电表产品 | TEST_METER | 20个 | meter_test | 电力计量、能耗管理、费用结算 |

### 物模型数据格式
每种产品类型都有对应的物模型定义，生成符合实际业务逻辑的测试数据：

```json
// 传感器数据示例
{
  "deviceSN": "TEST_SENSOR000001",
  "timestamp": 1640995200000,
  "method": "thing.event.property.post",
  "params": {
    "temperature": 25.6,
    "humidity": 65.2,
    "pressure": 1013.25
  }
}

// 摄像头数据示例  
{
  "deviceSN": "TEST_CAMERA000001",
  "timestamp": 1640995200000,
  "method": "thing.event.property.post",
  "params": {
    "recording": true,
    "resolution": "1080P",
    "storage_used": 78.5,
    "motion_detected": false
  }
}
```

## 🚀 快速开始

### 环境准备

1. **安装依赖**
```bash
pip install locust paho-mqtt requests
```

2. **数据库初始化**
```bash
# 执行测试数据初始化脚本
mysql -u root -p your_database < scripts/init_test_data.sql
```

3. **配置服务器地址**
修改脚本中的服务器配置：
```python
# MQTT服务器配置
"broker": {
    "host": "your-mqtt-broker-host",  # 改为你的MQTT Broker地址
    "port": 1883
}

# API服务器配置  
"servers": {
    "api_host": "http://your-api-host:8080"  # 改为你的API服务器地址
}
```

### 测试执行

#### 1. MQTT协议压力测试

```bash
# 基础测试（100个MQTT连接）
locust -f mqtt_pressure_test.py --host=mqtt://localhost:1883 -u 100 -r 10 -t 300s

# 中等规模测试（1000个MQTT连接）
locust -f mqtt_pressure_test.py --host=mqtt://localhost:1883 -u 1000 -r 50 -t 600s

# 大规模测试（10000个MQTT连接）
locust -f mqtt_pressure_test.py --host=mqtt://localhost:1883 -u 10000 -r 100 -t 1800s

# Web界面模式
locust -f mqtt_pressure_test.py --host=mqtt://localhost:1883
```

#### 2. REST API压力测试

```bash
# 基础API测试（100个并发用户）
locust -f rest_api_pressure_test.py --host=http://localhost:8080 -u 100 -r 10 -t 300s

# 数据上报高频测试（500个并发用户）
locust -f rest_api_pressure_test.py --host=http://localhost:8080 -u 500 -r 25 -t 600s

# 混合业务测试（1000个并发用户）
locust -f rest_api_pressure_test.py --host=http://localhost:8080 -u 1000 -r 50 -t 1800s
```

#### 3. 综合业务流程测试

```bash
# 小规模综合测试（100个设备完整流程）
locust -f comprehensive_pressure_test.py --host=mqtt://localhost:1883 -u 100 -r 5 -t 600s

# 中规模综合测试（1000个设备）
locust -f comprehensive_pressure_test.py --host=mqtt://localhost:1883 -u 1000 -r 20 -t 1800s

# 大规模综合测试（10000个设备）
locust -f comprehensive_pressure_test.py --host=mqtt://localhost:1883 -u 10000 -r 50 -t 3600s
```

## 📊 测试场景设计

### 1. 核心接口优先级测试

根据前面的分析，按优先级设计测试场景：

#### **🔥 最高优先级场景**
```bash
# MQTT连接认证极限测试
locust -f mqtt_pressure_test.py -u 50000 -r 1000 -t 1800s --host=mqtt://localhost:1883

# 数据上报API高频测试  
locust -f rest_api_pressure_test.py -u 2000 -r 100 -t 1800s --host=http://localhost:8080
```

#### **⚡ 高优先级场景**
```bash
# 混合协议综合测试
locust -f comprehensive_pressure_test.py -u 5000 -r 200 -t 3600s --host=mqtt://localhost:1883

# 指令下发响应测试
locust -f mqtt_pressure_test.py -u 1000 -r 50 -t 1200s --host=mqtt://localhost:1883
```

### 2. 业务流程测试场景

#### **环境监测场景**（传感器设备为主）
```bash
# 重点测试传感器设备的高频数据上报
locust -f comprehensive_pressure_test.py -u 2000 -r 100 -t 3600s --host=mqtt://localhost:1883
```

#### **工业控制场景**（PLC设备为主）  
```bash
# 重点测试PLC设备的实时控制指令
locust -f mqtt_pressure_test.py -u 500 -r 25 -t 1800s --host=mqtt://localhost:1883
```

#### **安防监控场景**（摄像头设备为主）
```bash
# 重点测试摄像头设备的大数据传输
locust -f comprehensive_pressure_test.py -u 1000 -r 30 -t 2400s --host=mqtt://localhost:1883
```

### 3. 故障恢复测试场景

#### **网络闪断测试**
```bash
# 模拟网络不稳定情况下的设备重连
locust -f comprehensive_pressure_test.py -u 1000 -r 100 -t 1800s --host=mqtt://localhost:1883
```

#### **服务重启测试**
```bash
# 测试服务重启后的设备自动重连能力
# 1. 启动测试
locust -f mqtt_pressure_test.py -u 5000 -r 200 -t 3600s --host=mqtt://localhost:1883

# 2. 在测试过程中重启MQTT Broker服务
# 3. 观察设备重连恢复情况
```

## 📈 性能指标监控

### 关键性能指标 (KPI)

#### **MQTT协议指标**
- **连接成功率**: >99.9%
- **平均连接时间**: <100ms
- **消息吞吐量**: >100万条/秒
- **消息丢失率**: <0.1%
- **心跳响应时间**: <50ms

#### **REST API指标**
- **API成功率**: >99.5%
- **平均响应时间**: <200ms
- **数据上报TPS**: >50,000
- **指令下发延迟**: <500ms
- **系统吞吐量**: >10,000 QPS

#### **业务流程指标**
- **端到端延迟**: <1秒
- **设备在线率**: >98%
- **指令响应率**: >95%
- **数据完整性**: >99.9%

### 实时监控命令

#### **系统资源监控**
```bash
# CPU和内存监控
top -p $(pidof java)

# 网络连接监控
netstat -an | grep :1883 | wc -l

# JVM堆内存监控
jstat -gc -t $(pidof java) 5s
```

#### **MQTT Broker监控**
```bash
# 查看MQTT连接数
mosquitto_sub -h localhost -t '$SYS/broker/clients/connected'

# 查看消息吞吐量
mosquitto_sub -h localhost -t '$SYS/broker/messages/received'
```

#### **数据库监控**
```sql
-- 查看活跃连接数
SHOW PROCESSLIST;

-- 查看慢查询
SHOW VARIABLES LIKE 'slow_query_log';

-- 查看设备在线统计
SELECT status, COUNT(*) FROM iot_device GROUP BY status;
```

## 🔧 高级配置

### 分布式测试部署

#### **Master节点配置**
```bash
# 启动Master节点
locust -f comprehensive_pressure_test.py --master --host=mqtt://your-broker-host:1883
```

#### **Worker节点配置**
```bash
# 启动Worker节点（可启动多个）
locust -f comprehensive_pressure_test.py --worker --master-host=master-ip
```

#### **30万设备并发测试**
```bash
# Master节点
locust -f comprehensive_pressure_test.py --master --host=mqtt://broker-cluster:1883

# Worker节点1-10（每个节点3万连接）
for i in {1..10}; do
  locust -f comprehensive_pressure_test.py --worker --master-host=master-ip -u 30000 &
done
```

### 自定义测试参数

#### **修改设备数据频率**
```python
# 在脚本中修改数据上报频率
"data_frequency": 30,  # 30秒上报一次（可调整）
```

#### **调整业务权重**
```python
# 修改测试任务权重
@task(70)  # 数据上报占70%
def publish_property_data(self):
    pass

@task(20)  # 指令响应占20%  
def publish_service_reply(self):
    pass
```

#### **设置故障模拟比例**
```python
# 调整故障模拟概率
"failure_simulation": {
    "network_timeout": 0.02,    # 2% 网络超时
    "auth_failure": 0.01,       # 1% 认证失败
    "data_corruption": 0.005,   # 0.5% 数据损坏
}
```

## 🎯 测试策略建议

### 阶段性测试计划

#### **阶段一：单接口基准测试**（1-2天）
1. **MQTT连接测试**: 找出单节点最大连接数
2. **数据上报测试**: 确定消息吞吐量上限
3. **API响应测试**: 测试REST接口响应能力

```bash
# 示例测试序列
locust -f mqtt_pressure_test.py -u 1000 -r 50 -t 600s --host=mqtt://localhost:1883
locust -f rest_api_pressure_test.py -u 500 -r 25 -t 600s --host=http://localhost:8080
```

#### **阶段二：业务场景测试**（3-5天）
1. **真实业务流程**: 完整设备生命周期测试
2. **混合负载测试**: 多种设备类型同时在线
3. **峰值负载测试**: 模拟业务高峰期

```bash
# 业务场景测试序列
locust -f comprehensive_pressure_test.py -u 2000 -r 100 -t 1800s --host=mqtt://localhost:1883
locust -f comprehensive_pressure_test.py -u 5000 -r 200 -t 3600s --host=mqtt://localhost:1883
locust -f comprehensive_pressure_test.py -u 10000 -r 500 -t 7200s --host=mqtt://localhost:1883
```

#### **阶段三：极限压力测试**（3-5天）
1. **30万设备并发**: 分布式部署测试
2. **故障恢复测试**: 异常情况处理能力
3. **长期稳定性测试**: 24小时持续运行

```bash
# 极限测试（需要分布式部署）
locust -f comprehensive_pressure_test.py --master -u 300000 -r 5000 -t 86400s
```

### 性能优化建议

#### **MQTT Broker优化**
1. **连接池配置**: 调整最大连接数限制
2. **消息队列**: 优化消息缓存和持久化
3. **负载均衡**: 部署MQTT集群和HAProxy

#### **API服务优化**  
1. **数据库连接池**: 增加连接池大小
2. **缓存策略**: Redis缓存热点数据
3. **异步处理**: 数据上报接口改为异步处理

#### **系统级优化**
1. **操作系统参数**: 调整文件描述符限制
2. **JVM参数**: 优化堆内存和GC配置
3. **网络优化**: 调整TCP参数和网络缓冲区

## 📋 测试报告模板

### 测试结果汇总
```
测试时间: 2024-01-01 10:00:00 - 11:00:00
测试场景: 综合业务流程压力测试
并发设备: 10,000个
测试时长: 3600秒

📊 核心指标:
- MQTT连接成功率: 99.8%
- 数据上报成功率: 99.5%
- 平均响应时间: 156ms
- 峰值TPS: 85,000

🔍 详细统计:
- 总连接尝试: 10,000次
- 成功连接: 9,980次
- 总数据上报: 1,200,000次
- 成功上报: 1,194,000次
- 指令响应率: 98.5%

⚠️ 发现问题:
1. 高并发时偶现连接超时
2. 数据上报峰值时延迟增加
3. 内存使用率在高峰期达到85%

💡 优化建议:
1. 增加MQTT Broker连接池大小
2. 优化数据上报接口的异步处理
3. 调整JVM堆内存配置
```

## 🔚 总结

这三套压力测试脚本基于你的真实数据库配置，提供了完整的IoT平台性能测试解决方案：

1. **`mqtt_pressure_test.py`**: 专注MQTT协议核心功能的极限测试
2. **`rest_api_pressure_test.py`**: 覆盖HTTP API业务流程的全面测试  
3. **`comprehensive_pressure_test.py`**: 模拟真实业务场景的端到端测试

通过这套测试体系，可以全面验证SYDH IoT平台在30万设备并发场景下的性能表现，确保系统的稳定性和可靠性。 