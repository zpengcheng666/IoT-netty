# 物联网平台

## 📋 项目简介

一套全开源的物联网平台，基于若依框架深度定制开发，专为物联网应用场景设计。平台提供设备管理、数据采集、协议解析、规则引擎、数据可视化等完整的物联网解决方案，支持百万级设备并发接入。

### ✨ 核心特性

- 🌐 **多协议支持**：MQTT、CoAP、HTTP、TCP、UDP、Modbus TCP等多种协议
- 🔧 **设备管理**：设备生命周期管理、分组管理、批量操作
- 📊 **数据采集**：实时数据采集、历史数据存储、数据清洗
- ⚡ **规则引擎**：LiteFlow规则引擎、Groovy脚本、可视化规则配置
- 📈 **数据可视化**：GoView大屏、图表展示、实时监控
- 🔒 **安全认证**：JWT令牌、权限管理、设备认证
- 🏗️ **微服务架构**：模块化设计、易于扩展、云原生部署

## 🏗️ 系统架构

```
┌─────────────────────────────────────────────────────────────┐
│                        Web管理后台                          │
├─────────────────────────────────────────────────────────────┤
│                        开放API接口                          │
├─────────────────────────────────────────────────────────────┤
│        规则引擎        │        数据处理        │ 通知服务  │
├─────────────────────────────────────────────────────────────┤
│       协议解析层       │       数据存储层       │ 缓存层    │
├─────────────────────────────────────────────────────────────┤
│  MQTT  │  CoAP  │  HTTP  │  TCP  │  UDP  │  Modbus TCP     │
└─────────────────────────────────────────────────────────────┘
```

## 🚀 技术栈

### 后端技术
- **框架**: Spring Boot 2.5.14、Spring Security、Spring Data JPA
- **数据库**: MySQL、PostgreSQL、Oracle、SQL Server、达梦
- **时序数据库**: TDengine、InfluxDB、IoTDB
- **缓存**: Redis、Redisson
- **消息队列**: RocketMQ、MQTT Broker
- **规则引擎**: LiteFlow 2.13.2
- **协议支持**: Eclipse Paho MQTT、CoAP、Modbus
- **工具库**: Hutool、MyBatis Plus、FastJSON2

### 前端技术
- **框架**: Vue 3.x、Element Plus
- **构建工具**: Vite
- **可视化**: GoView、ECharts
- **状态管理**: Vuex/Pinia

### 基础设施
- **消息中间件**: EMQX、RocketMQ
- **监控**: Prometheus、Grafana
- **容器**: Docker、Docker Compose
- **文档**: Swagger 3.0

## 📦 模块结构

```
sydh-*
├── sydh-admin           # 管理后台模块
├── sydh-service         # 业务服务层
│   ├── sydh-iot-service     # IoT业务服务
│   └── sydh-system-service  # 系统服务
├── sydh-server          # 协议服务层
│   ├── mqtt-broker             # MQTT代理服务
│   ├── coap-server             # CoAP服务
│   ├── http-server             # HTTP服务
│   └── sip-server              # SIP服务
├── sydh-protocol        # 协议解析层
│   ├── sydh-protocol-base    # 协议基础
│   └── sydh-protocol-collect # 协议采集
├── sydh-plugs          # 插件模块
│   ├── sydh-quartz          # 定时任务
│   ├── sydh-generator       # 代码生成器
│   ├── sydh-oss             # 对象存储
│   ├── sydh-oauth           # OAuth认证
│   ├── sydh-ruleEngine      # 规则引擎
│   └── sydh-modbus-tcp      # Modbus TCP
├── sydh-common          # 公共模块
├── sydh-framework       # 核心框架
├── sydh-iot-data        # IoT数据处理
├── sydh-mq              # 消息队列
├── sydh-notify          # 通知服务
├── sydh-open-api        # 开放API
└── sydh-record          # 录像服务
```

## 🛠️ 功能特性

### 📱 设备管理
- **设备注册**: 批量导入、自动注册、设备认证
- **设备分组**: 按地区、类型、用途灵活分组管理
- **设备监控**: 实时状态监控、在线率统计、告警管理
- **固件升级**: OTA升级、版本管理、升级进度跟踪

### 📡 协议接入
- **MQTT协议**: 支持QoS 0/1/2、保留消息、遗嘱消息
- **CoAP协议**: 轻量级物联网协议、适用于资源受限设备
- **HTTP协议**: RESTful API接入、简单易用
- **TCP/UDP**: 自定义协议、灵活配置
- **Modbus TCP**: 工业协议支持、寄存器读写

### 🔄 数据处理
- **实时采集**: 毫秒级数据采集、高并发处理
- **数据存储**: 关系型数据库 + 时序数据库
- **数据清洗**: 数据验证、格式转换、异常过滤
- **数据转发**: 支持HTTP、MQTT、WebSocket等方式

### ⚙️ 规则引擎
- **可视化配置**: 拖拽式规则配置、无需编码
- **脚本支持**: Groovy脚本、Java Bean、Spring组件
- **触发条件**: 设备状态、数据阈值、定时触发
- **执行动作**: 设备控制、消息推送、数据转发

### 📊 数据可视化
- **实时监控**: 设备状态、数据趋势、告警信息
- **图表展示**: 折线图、柱状图、饼图、仪表盘
- **大屏展示**: GoView大屏、自定义布局
- **报表导出**: Excel、PDF格式导出

## 🚦 快速开始

### 环境要求
- JDK 1.8+
- MySQL 5.7+ / PostgreSQL 10+
- Redis 5.0+
- Maven 3.6+
- Node.js 14+


### 打包部署
需要将application.yml文件修改一下：
active: dev # 环境配置，dev=开发环境，prod=生产环境

### netty分层配置
1. 分层配置策略
   根据实际设备规模选择合适的配置：
   配置级别	设备数量	配置文件	特点
   小规模	0-1万	application-small-scale.yml	🔋 资源节约型
   中规模	1万-10万	application-medium-scale.yml	⚖️ 性能平衡型
   大规模	10万-50万+	application-performance.yml	🚀 性能优先型


### 安装步骤

1. **克隆项目**
```bash
git clone http://e13a4837c1b5/ITMP/iot.git
```

2. **数据库初始化**
```sql
-- 创建数据库
CREATE DATABASE sydh DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 导入表结构和数据
mysql -u root -p sydh < sql/sydh-v2.7.0.sql
```

3. **修改配置**
```yaml
# sydh-admin/src/main/resources/application-dev.yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/sydh
    username: your_username
    password: your_password
  redis:
    host: localhost
    port: 6379
```

4. **启动应用**
```bash
# 编译项目
mvn clean package

# 启动应用 (Windows)
sydh.bat

# 启动应用 (Linux/Mac)
chmod +x sydh.sh
./sydh.sh start
```

5. **访问系统**
- 后台管理: http://localhost:8080
- 默认账号: admin/admin123
- API文档: http://localhost:8080/swagger-ui/

### Docker 部署

```bash
# 使用 Docker Compose 一键部署
docker-compose up -d

# 或使用 Docker 镜像
docker run -p 8080:8080 sydh/sydh:latest
```

## 📖 使用文档

### 设备接入示例

#### MQTT 设备接入
```java
// 设备连接
MqttConnectOptions options = new MqttConnectOptions();
options.setUserName("deviceId");
options.setPassword("deviceSecret");
MqttClient client = new MqttClient("tcp://localhost:1883", "clientId");
client.connect(options);

// 数据上报
String topic = "/productId/deviceId/property/post";
String payload = "[{\"id\":\"temperature\",\"value\":25.6}]";
client.publish(topic, payload.getBytes(), 1, false);
```

#### HTTP 设备接入
```bash
# 设备认证
curl -X POST http://localhost:8080/api/device/auth \
  -H "Content-Type: application/json" \
  -d '{"deviceId":"D001","deviceSecret":"secret123"}'

# 数据上报
curl -X POST http://localhost:8080/api/device/property \
  -H "Authorization: Bearer TOKEN" \
  -H "Content-Type: application/json" \
  -d '[{"id":"temperature","value":25.6}]'
```

### 压力测试

支持10万台设备并发接入测试：

```bash
# 系统监控
./scripts/system-monitor.sh 5

# MQTT压力测试
./scripts/mqtt-pressure-test.sh 127.0.0.1 1883 sydh sydh

# HTTP压力测试
./scripts/http-coap-pressure-test.sh 127.0.0.1 8080
```

详细测试指南请查看: [压力测试文档](docs/pressure-test-guide.md)

## 🔧 环境配置

### Java 9+ 兼容性

如果使用Java 9+版本，请使用兼容性启动脚本：

```bash
# Windows
start-java9-compatible.bat

# Linux/Mac  
./start-java9-compatible.sh
```

或在IDE中添加JVM参数：
```
--add-opens java.base/sun.reflect.annotation=ALL-UNNAMED
--add-opens java.base/java.lang=ALL-UNNAMED
--add-opens java.base/java.util=ALL-UNNAMED
```

详细配置请查看: [Java9+配置指南](IDE-JAVA9-CONFIG.md)

## 📊 性能指标

- **并发连接**: 支持10万+ MQTT并发连接
- **消息吞吐**: 10万+ 消息/秒
- **响应延迟**: 95% < 100ms, 99% < 500ms
- **设备规模**: 支持百万级设备管理
- **数据存储**: PB级时序数据存储


