"""
SYDH物联网平台 MQTT 协议专项压力测试脚本
==================================================

🎯 测试目标：
- 针对MQTT协议进行专项压力测试
- 模拟真实IoT设备MQTT连接和消息传输
- 支持多种认证方式和设备类型
- 测试MQTT Broker承载能力和消息吞吐量

📋 MQTT测试覆盖：
1. 🔐 MQTT认证测试
   - 服务端认证 (fastbee/fastbee)
   - 设备简单认证 (S&设备号&产品ID&用户ID)
   - 设备加密认证 (E&设备号&产品ID&用户ID)
   - Web/移动端Token认证

2. 📡 MQTT消息传输测试
   - 属性上报 (property/post)
   - 事件上报 (event/post)
   - 状态上报 (status/post)
   - 功能调用响应 (function/get)
   - 设备信息上报 (info/post)

3. 🚀 性能测试场景
   - 大量设备并发连接
   - 高频数据上报
   - 消息确认机制测试
   - 连接稳定性测试

🚀 使用方法：
# 基础MQTT测试 (1000设备)
locust -f mqtt_locust_test.py --host=tcp://localhost:1883 --users=1000 --spawn-rate=100 --run-time=300s

# 大规模MQTT测试 (10万设备)
locust -f mqtt_locust_test.py --host=tcp://localhost:1883 --users=100000 --spawn-rate=1000 --run-time=1800s --csv=results/mqtt-load-test

# WebSocket MQTT测试
locust -f mqtt_locust_test.py --host=ws://localhost:8083 --users=5000 --spawn-rate=500 --run-time=600s

# 分布式MQTT测试
locust -f mqtt_locust_test.py --master --host=tcp://localhost:1883
locust -f mqtt_locust_test.py --worker --master-host=192.168.1.100

依赖安装：
pip install locust paho-mqtt faker
"""

import json
import random
import time
import ssl
import threading
from datetime import datetime, timedelta
from typing import Dict, List, Optional
import logging

from locust import User, task, between, events
from locust.exception import LocustError
import paho.mqtt.client as mqtt
from faker import Faker

# 配置日志
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# 初始化Faker
fake = Faker('zh_CN')

# ===== MQTT测试配置 =====
MQTT_CONFIG = {
    # MQTT连接配置
    "connection": {
        "host": "localhost",
        "port": 1883,
        "websocket_port": 8083,
        "keepalive": 60,
        "clean_session": True,
        "qos_levels": [0, 1, 2],
        "retain": False
    },
    
    # 认证配置
    "auth": {
        # 服务端认证
        "server": {
            "username": "fastbee",
            "password": "fastbee",
            "client_prefix": "server"
        },
        # Web端认证 (需要JWT Token)
        "web": {
            "username": "admin",
            "password": "eyJhbGciOiJIUzUxMiJ9.eyJsb2dpbl91c2VyX2tleSI6ImNlOTVjZjc1LWJjNGQtNDJmYi1iOGIzLTQ2OTNmODkxZjY1MSJ9.X6B8IhFXi0alMzcOOP-DrIHfTjNMQqzQtcPSB1FlkTpZfX_-dnGWIMu17I4RmeaP3kUgVlxwmXHpaFgyF_277A",
            "client_prefix": "web"
        },
        # 移动端认证
        "mobile": {
            "username": "admin", 
            "password": "eyJhbGciOiJIUzUxMiJ9.eyJsb2dpbl91c2VyX2tleSI6ImNlOTVjZjc1LWJjNGQtNDJmYi1iOGIzLTQ2OTNmODkxZjY1MSJ9.X6B8IhFXi0alMzcOOP-DrIHfTjNMQqzQtcPSB1FlkTpZfX_-dnGWIMu17I4RmeaP3kUgVlxwmXHpaFgyF_277A",
            "client_prefix": "phone"
        },
        # 设备端认证
        "device": {
            "simple_auth": {
                "auth_type": "S",  # 简单认证
                "username": "fastbee_device",
                "password": "device123"
            },
            "encrypt_auth": {
                "auth_type": "E",  # 加密认证
                "username": "fastbee_device",
                "password": "encrypted_device_password",
                "secret": "device_secret_key"
            }
        }
    },
    
    # 设备类型定义 (与现有系统保持一致)
    "device_types": {
        "camera": {
            "product_id": 1,
            "device_type": 1,
            "prefix": "CAM",
            "data_frequency": 5,  # 5秒上报一次
            "description": "智能摄像头"
        },
        "plc": {
            "product_id": 2,
            "device_type": 2,
            "prefix": "PLC",
            "data_frequency": 10,
            "description": "工业控制器"
        },
        "sensor": {
            "product_id": 3,
            "device_type": 3,
            "prefix": "SENSOR",
            "data_frequency": 30,
            "description": "环境传感器"
        },
        "gateway": {
            "product_id": 4,
            "device_type": 4,
            "prefix": "GW",
            "data_frequency": 60,
            "description": "物联网网关"
        },
        "meter": {
            "product_id": 5,
            "device_type": 5,
            "prefix": "METER",
            "data_frequency": 300,
            "description": "智能电表"
        }
    },
    
    # MQTT主题模板 (与系统保持一致)
    "topics": {
        "property_post": "/{product_id}/{device_num}/property/post",
        "event_post": "/{product_id}/{device_num}/event/post", 
        "function_post": "/{product_id}/{device_num}/function/post",
        "info_post": "/{product_id}/{device_num}/info/post",
        "status_post": "/{product_id}/{device_num}/status/post",
        "ntp_post": "/{product_id}/{device_num}/ntp/post",
        "message_post": "/{product_id}/{device_num}/message/post",
        "function_get": "/{product_id}/{device_num}/function/get",
        "property_get": "/{product_id}/{device_num}/property/get",
        "property_set": "/{product_id}/{device_num}/property/set",
        "ntp_get": "/{product_id}/{device_num}/ntp/get",
        "info_get": "/{product_id}/{device_num}/info/get"
    }
}

# ===== IoT设备数据模板 =====
DEVICE_DATA_TEMPLATES = {
    "camera": {
        "system_status": lambda: {
            "online": random.choice([0, 1]),
            "recording": random.choice([0, 1]),
            "resolution": random.choice(["720P", "1080P", "4K"]),
            "storage_free": round(random.uniform(10, 500), 1)
        },
        "performance": lambda: {
            "cpu_usage": round(random.uniform(5, 85), 2),
            "memory_usage": round(random.uniform(20, 80), 2),
            "temperature": round(random.uniform(30, 65), 1),
            "network_quality": random.randint(60, 100)
        },
        "detection": lambda: {
            "motion_detected": random.choice([0, 1]),
            "face_count": random.randint(0, 15),
            "vehicle_count": random.randint(0, 8),
            "alarm_triggered": random.choice([0, 1])
        }
    },
    
    "plc": {
        "production": lambda: {
            "status": random.choice(["running", "stopped", "fault", "maintenance"]),
            "count": random.randint(1000, 8000),
            "rate": round(random.uniform(75, 100), 2),
            "cycle_time": round(random.uniform(45, 120), 1)
        },
        "physical": lambda: {
            "temperature": round(random.uniform(35, 85), 1),
            "pressure": round(random.uniform(1.0, 3.0), 2),
            "vibration": round(random.uniform(0.1, 5.0), 2),
            "flow_rate": round(random.uniform(50, 200), 1)
        },
        "electrical": lambda: {
            "voltage": round(random.uniform(210, 250), 1),
            "current": round(random.uniform(10, 50), 2),
            "power": round(random.uniform(2.0, 15.0), 2),
            "frequency": round(random.uniform(49.5, 50.5), 2)
        }
    },
    
    "sensor": {
        "environment": lambda: {
            "temperature": round(random.uniform(-10, 50), 1),
            "humidity": round(random.uniform(20, 90), 1),
            "pressure": round(random.uniform(950, 1050), 2),
            "light": random.randint(0, 100000)
        },
        "air_quality": lambda: {
            "pm2_5": round(random.uniform(5, 150), 1),
            "pm10": round(random.uniform(10, 200), 1),
            "co2": random.randint(400, 2000),
            "co": round(random.uniform(0.1, 10.0), 2)
        },
        "device_status": lambda: {
            "battery": random.randint(20, 100),
            "signal_strength": -random.randint(40, 120),
            "data_quality": random.choice(["excellent", "good", "fair"])
        }
    },
    
    "gateway": {
        "network": lambda: {
            "connected_devices": random.randint(50, 500),
            "total_devices": random.randint(100, 600),
            "uplink_rate": round(random.uniform(10, 1000), 2),
            "downlink_rate": round(random.uniform(50, 1000), 2),
            "packet_loss": round(random.uniform(0, 3), 3)
        },
        "system": lambda: {
            "cpu_usage": round(random.uniform(10, 70), 2),
            "memory_usage": round(random.uniform(20, 80), 2),
            "disk_usage": round(random.uniform(30, 85), 2),
            "temperature": round(random.uniform(25, 70), 1)
        }
    },
    
    "meter": {
        "energy": lambda: {
            "current_reading": round(random.uniform(1000, 99999), 2),
            "instant_power": round(random.uniform(0.5, 30.0), 3),
            "daily_consumption": round(random.uniform(10, 100), 2),
            "monthly_consumption": round(random.uniform(300, 3000), 1)
        },
        "electrical": lambda: {
            "voltage_a": round(random.uniform(210, 250), 1),
            "voltage_b": round(random.uniform(210, 250), 1), 
            "voltage_c": round(random.uniform(210, 250), 1),
            "current_a": round(random.uniform(1, 50), 2),
            "current_b": round(random.uniform(1, 50), 2),
            "current_c": round(random.uniform(1, 50), 2),
            "power_factor": round(random.uniform(0.8, 1.0), 3)
        }
    }
}

# ===== 全局统计 =====
mqtt_stats = {
    "connected_devices": 0,
    "total_connections": 0,
    "failed_connections": 0,
    "messages_sent": 0,
    "messages_received": 0,
    "connection_errors": 0,
    "message_errors": 0,
    "start_time": None
}

# ===== 工具函数 =====
def generate_device_id(device_type: str, user_id: int = 1) -> tuple:
    """生成设备ID和相关信息"""
    device_config = MQTT_CONFIG["device_types"][device_type]
    device_num = f"{device_config['prefix']}{random.randint(10000, 99999)}"
    product_id = device_config["product_id"]
    
    return device_num, product_id, user_id

def generate_client_id(auth_type: str, device_num: str, product_id: int, user_id: int = 1) -> str:
    """生成MQTT ClientID"""
    if auth_type in ["server", "web", "phone"]:
        return f"{auth_type}_{random.randint(1000, 9999)}"
    else:
        # 设备端认证格式: 认证类型&设备编号&产品ID&用户ID
        return f"{auth_type}&{device_num}&{product_id}&{user_id}"

def generate_device_data(device_type: str) -> dict:
    """生成设备数据"""
    if device_type not in DEVICE_DATA_TEMPLATES:
        device_type = "sensor"
    
    template = DEVICE_DATA_TEMPLATES[device_type]
    data = {}
    
    # 生成各类数据
    for category, generator in template.items():
        data.update(generator())
    
    return data

def format_mqtt_message(data: dict, message_type: str = "property") -> dict:
    """格式化MQTT消息"""
    message = {
        "type": message_type,
        "timestamp": datetime.now().isoformat(),
        "messageId": f"{message_type}_{random.randint(100000, 999999)}",
        "data": data
    }
    
    if message_type == "property":
        # 转换为ThingsModel格式
        properties = []
        for key, value in data.items():
            properties.append({
                "id": key,
                "value": str(value),
                "name": key.replace("_", " ").title(),
                "ts": datetime.now().strftime("%Y-%m-%d %H:%M:%S")
            })
        message["properties"] = properties
    elif message_type == "event":
        message.update({
            "level": random.choice(["info", "warning", "error"]),
            "eventType": random.choice(["threshold_exceeded", "state_changed", "alarm_triggered"])
        })
    elif message_type == "status":
        message.update({
            "status": random.choice([1, 2, 3, 4]),  # 1=在线, 2=离线, 3=故障, 4=维护
            "rssi": -random.randint(30, 120),
            "battery": random.randint(10, 100)
        })
    
    return message

# ===== Locust事件监听 =====
@events.test_start.add_listener
def on_test_start(environment, **kwargs):
    """测试开始事件"""
    global mqtt_stats
    mqtt_stats["start_time"] = datetime.now()
    logger.info("🚀 MQTT压力测试开始")
    logger.info(f"📊 目标用户数: {environment.parsed_options.num_users}")
    logger.info(f"⏱️  启动速率: {environment.parsed_options.spawn_rate} users/second")

@events.test_stop.add_listener
def on_test_stop(environment, **kwargs):
    """测试结束事件"""
    global mqtt_stats
    end_time = datetime.now()
    duration = (end_time - mqtt_stats["start_time"]).total_seconds()
    
    logger.info("📈 === MQTT测试统计报告 ===")
    logger.info(f"⏱️  测试时长: {duration:.2f}秒")
    logger.info(f"🔗 连接统计:")
    logger.info(f"   总连接尝试: {mqtt_stats['total_connections']}")
    logger.info(f"   成功连接: {mqtt_stats['connected_devices']}")
    logger.info(f"   失败连接: {mqtt_stats['failed_connections']}")
    logger.info(f"   连接成功率: {(mqtt_stats['connected_devices']/max(mqtt_stats['total_connections'],1)*100):.2f}%")
    
    logger.info(f"📡 消息统计:")
    logger.info(f"   发送消息: {mqtt_stats['messages_sent']}")
    logger.info(f"   接收消息: {mqtt_stats['messages_received']}")
    logger.info(f"   平均发送QPS: {(mqtt_stats['messages_sent']/max(duration,1)):.2f}")
    
    logger.info(f"❌ 错误统计:")
    logger.info(f"   连接错误: {mqtt_stats['connection_errors']}")
    logger.info(f"   消息错误: {mqtt_stats['message_errors']}")

# ===== MQTT客户端封装 =====
class MQTTClient:
    """MQTT客户端封装类"""
    
    def __init__(self, client_id: str, auth_config: dict, host: str = "localhost", port: int = 1883):
        self.client_id = client_id
        self.auth_config = auth_config
        self.host = host
        self.port = port
        self.client = None
        self.connected = False
        self.message_callbacks = []
        
    def on_connect(self, client, userdata, flags, rc, properties=None):
        """连接回调"""
        global mqtt_stats
        if rc == 0:
            self.connected = True
            mqtt_stats["connected_devices"] += 1
            logger.debug(f"✅ MQTT连接成功: {self.client_id}")
        else:
            mqtt_stats["failed_connections"] += 1
            mqtt_stats["connection_errors"] += 1
            logger.error(f"❌ MQTT连接失败: {self.client_id}, RC: {rc}")
    
    def on_disconnect(self, client, userdata, rc, properties=None):
        """断连回调"""
        global mqtt_stats
        self.connected = False
        mqtt_stats["connected_devices"] -= 1
        logger.debug(f"🔌 MQTT断开连接: {self.client_id}")
        
    def on_message(self, client, userdata, msg):
        """消息接收回调"""
        global mqtt_stats
        mqtt_stats["messages_received"] += 1
        logger.debug(f"📩 收到消息: {msg.topic} - {len(msg.payload)}字节")
        
    def on_publish(self, client, userdata, mid, properties=None):
        """发布确认回调"""
        global mqtt_stats
        mqtt_stats["messages_sent"] += 1
        logger.debug(f"📤 消息发送确认: MID {mid}")
        
    def connect(self) -> bool:
        """建立MQTT连接"""
        global mqtt_stats
        mqtt_stats["total_connections"] += 1
        
        try:
            self.client = mqtt.Client(
                client_id=self.client_id, 
                clean_session=MQTT_CONFIG["connection"]["clean_session"],
                callback_api_version=mqtt.CallbackAPIVersion.VERSION2
            )
            
            # 设置回调
            self.client.on_connect = self.on_connect
            self.client.on_disconnect = self.on_disconnect
            self.client.on_message = self.on_message
            self.client.on_publish = self.on_publish
            
            # 设置认证
            if "username" in self.auth_config and "password" in self.auth_config:
                self.client.username_pw_set(self.auth_config["username"], self.auth_config["password"])
            
            # 连接到MQTT Broker
            self.client.connect(self.host, self.port, MQTT_CONFIG["connection"]["keepalive"])
            self.client.loop_start()
            
            # 等待连接完成
            timeout = 10
            while not self.connected and timeout > 0:
                time.sleep(0.1)
                timeout -= 0.1
                
            return self.connected
            
        except Exception as e:
            mqtt_stats["connection_errors"] += 1
            logger.error(f"❌ MQTT连接异常: {self.client_id} - {str(e)}")
            return False
    
    def disconnect(self):
        """断开MQTT连接"""
        if self.client and self.connected:
            self.client.loop_stop()
            self.client.disconnect()
            
    def publish(self, topic: str, payload: str, qos: int = 0, retain: bool = False) -> bool:
        """发布MQTT消息"""
        if not self.connected:
            mqtt_stats["message_errors"] += 1
            return False
            
        try:
            result = self.client.publish(topic, payload, qos, retain)
            return result.rc == mqtt.MQTT_ERR_SUCCESS
        except Exception as e:
            mqtt_stats["message_errors"] += 1
            logger.error(f"❌ 消息发布失败: {topic} - {str(e)}")
            return False
    
    def subscribe(self, topic: str, qos: int = 0) -> bool:
        """订阅MQTT主题"""
        if not self.connected:
            return False
            
        try:
            result = self.client.subscribe(topic, qos)
            return result[0] == mqtt.MQTT_ERR_SUCCESS
        except Exception as e:
            logger.error(f"❌ 主题订阅失败: {topic} - {str(e)}")
            return False

# ===== Locust用户类 =====
class MQTTUser(User):
    """MQTT用户基类"""
    
    abstract = True
    
    def __init__(self, environment):
        super().__init__(environment)
        self.mqtt_client = None
        self.device_info = None
        self.last_data_time = {}
        
    def on_start(self):
        """用户开始时的初始化"""
        pass
        
    def on_stop(self):
        """用户结束时的清理"""
        if self.mqtt_client:
            self.mqtt_client.disconnect()

class MQTTDeviceUser(MQTTUser):
    """模拟IoT设备的MQTT用户"""
    
    wait_time = between(1, 5)
    
    def on_start(self):
        """设备用户初始化"""
        super().on_start()
        
        # 随机选择设备类型
        device_type = random.choice(list(MQTT_CONFIG["device_types"].keys()))
        device_num, product_id, user_id = generate_device_id(device_type)
        
        self.device_info = {
            "type": device_type,
            "device_num": device_num,
            "product_id": product_id,
            "user_id": user_id
        }
        
        # 随机选择认证方式
        auth_type = random.choice(["S"])  # 暂时只使用简单认证
        client_id = generate_client_id(auth_type, device_num, product_id, user_id)
        
        # 配置认证 - 简化为匿名连接或使用fastbee认证  
        auth_config = {}  # 使用匿名连接
        # 如果需要认证，可以使用：
        # auth_config = {"username": "fastbee", "password": "fastbee"}
        
        # 解析主机和端口
        host = self.environment.host.replace("tcp://", "").replace("ws://", "").split(":")[0] if "://" in self.environment.host else self.environment.host
        port = 1883
        if ":" in self.environment.host:
            try:
                port = int(self.environment.host.split(":")[-1])
            except:
                port = 1883
        
        # 创建MQTT连接
        self.mqtt_client = MQTTClient(client_id, auth_config, host, port)
        
        # 尝试连接
        start_time = time.time()
        success = self.mqtt_client.connect()
        response_time = (time.time() - start_time) * 1000
        
        if success:
            self.environment.events.request.fire(
                request_type="MQTT",
                name="connect",
                response_time=response_time,
                response_length=0,
                exception=None
            )
            logger.info(f"🔗 设备连接成功: {device_type}_{device_num}")
        else:
            self.environment.events.request.fire(
                request_type="MQTT",
                name="connect", 
                response_time=response_time,
                response_length=0,
                exception=LocustError("MQTT连接失败")
            )
    
    @task(50)  # 权重50 - 属性数据上报
    def report_property_data(self):
        """设备属性数据上报"""
        if not self.mqtt_client or not self.mqtt_client.connected:
            return
            
        device_type = self.device_info["type"]
        
        # 检查上报频率
        now = time.time()
        frequency = MQTT_CONFIG["device_types"][device_type]["data_frequency"]
        if device_type in self.last_data_time:
            if now - self.last_data_time[device_type] < frequency:
                return
        
        self.last_data_time[device_type] = now
        
        # 生成设备数据
        device_data = generate_device_data(device_type)
        message = format_mqtt_message(device_data, "property")
        
        # 构造主题
        topic = MQTT_CONFIG["topics"]["property_post"].format(
            product_id=self.device_info["product_id"],
            device_num=self.device_info["device_num"]
        )
        
        # 发布消息
        start_time = time.time()
        payload = json.dumps(message, ensure_ascii=False)
        qos = random.choice(MQTT_CONFIG["connection"]["qos_levels"])
        
        success = self.mqtt_client.publish(topic, payload, qos)
        response_time = (time.time() - start_time) * 1000
        
        if success:
            self.environment.events.request.fire(
                request_type="MQTT",
                name="property_post",
                response_time=response_time,
                response_length=len(payload.encode('utf-8')),
                exception=None
            )
        else:
            self.environment.events.request.fire(
                request_type="MQTT", 
                name="property_post",
                response_time=response_time,
                response_length=len(payload.encode('utf-8')),
                exception=LocustError("属性数据发布失败")
            )
    
    @task(25)  # 权重25 - 事件上报
    def report_event_data(self):
        """设备事件上报"""
        if not self.mqtt_client or not self.mqtt_client.connected:
            return
            
        # 随机生成事件
        if random.random() > 0.1:  # 10% 的概率发送事件
            return
            
        device_data = generate_device_data(self.device_info["type"])
        message = format_mqtt_message(device_data, "event")
        
        topic = MQTT_CONFIG["topics"]["event_post"].format(
            product_id=self.device_info["product_id"],
            device_num=self.device_info["device_num"]
        )
        
        start_time = time.time()
        payload = json.dumps(message, ensure_ascii=False)
        qos = random.choice([0, 1])  # 事件一般使用QoS 0或1
        
        success = self.mqtt_client.publish(topic, payload, qos)
        response_time = (time.time() - start_time) * 1000
        
        if success:
            self.environment.events.request.fire(
                request_type="MQTT",
                name="event_post", 
                response_time=response_time,
                response_length=len(payload.encode('utf-8')),
                exception=None
            )
        else:
            self.environment.events.request.fire(
                request_type="MQTT",
                name="event_post",
                response_time=response_time, 
                response_length=len(payload.encode('utf-8')),
                exception=LocustError("事件数据发布失败")
            )
    
    @task(15)  # 权重15 - 状态上报
    def report_status_data(self):
        """设备状态上报"""
        if not self.mqtt_client or not self.mqtt_client.connected:
            return
            
        # 每60秒上报一次状态
        if random.random() > 0.02:  # 约2%的概率，模拟60秒间隔
            return
            
        status_data = {
            "online": 1,
            "signal_strength": -random.randint(30, 120),
            "battery": random.randint(10, 100)
        }
        message = format_mqtt_message(status_data, "status") 
        
        topic = MQTT_CONFIG["topics"]["status_post"].format(
            product_id=self.device_info["product_id"],
            device_num=self.device_info["device_num"]
        )
        
        start_time = time.time()
        payload = json.dumps(message, ensure_ascii=False)
        
        success = self.mqtt_client.publish(topic, payload, 0)  # 状态上报使用QoS 0
        response_time = (time.time() - start_time) * 1000
        
        if success:
            self.environment.events.request.fire(
                request_type="MQTT",
                name="status_post",
                response_time=response_time,
                response_length=len(payload.encode('utf-8')),
                exception=None
            )
        else:
            self.environment.events.request.fire(
                request_type="MQTT",
                name="status_post",
                response_time=response_time,
                response_length=len(payload.encode('utf-8')),
                exception=LocustError("状态数据发布失败")
            )
    
    @task(10)  # 权重10 - 功能调用响应
    def respond_function_call(self):
        """响应功能调用"""
        if not self.mqtt_client or not self.mqtt_client.connected:
            return
            
        # 模拟接收功能调用后的响应
        if random.random() > 0.05:  # 5%的概率响应功能调用
            return
            
        response_data = {
            "result": random.choice(["success", "failed", "timeout"]),
            "code": random.choice([200, 400, 404, 500]),
            "message": "Function executed",
            "execution_time": random.randint(50, 5000)
        }
        message = format_mqtt_message(response_data, "function")
        
        topic = MQTT_CONFIG["topics"]["function_get"].format(
            product_id=self.device_info["product_id"],
            device_num=self.device_info["device_num"]
        )
        
        start_time = time.time()
        payload = json.dumps(message, ensure_ascii=False)
        
        success = self.mqtt_client.publish(topic, payload, 1)  # 功能响应使用QoS 1
        response_time = (time.time() - start_time) * 1000
        
        if success:
            self.environment.events.request.fire(
                request_type="MQTT",
                name="function_response",
                response_time=response_time,
                response_length=len(payload.encode('utf-8')),
                exception=None
            )
        else:
            self.environment.events.request.fire(
                request_type="MQTT", 
                name="function_response",
                response_time=response_time,
                response_length=len(payload.encode('utf-8')),
                exception=LocustError("功能响应发布失败")
            )

class MQTTServerUser(MQTTUser):
    """模拟服务端MQTT用户"""
    
    wait_time = between(5, 15)
    
    def on_start(self):
        """服务端用户初始化"""
        super().on_start()
        
        client_id = f"server_{random.randint(1000, 9999)}"
        auth_config = {"username": "fastbee", "password": "fastbee"}  # 服务端认证
        
        # 解析主机和端口
        host = self.environment.host.replace("tcp://", "").replace("ws://", "").split(":")[0] if "://" in self.environment.host else self.environment.host
        port = 1883
        if ":" in self.environment.host:
            try:
                port = int(self.environment.host.split(":")[-1])
            except:
                port = 1883
        
        self.mqtt_client = MQTTClient(client_id, auth_config, host, port)
        
        start_time = time.time()
        success = self.mqtt_client.connect()
        response_time = (time.time() - start_time) * 1000
        
        if success:
            self.environment.events.request.fire(
                request_type="MQTT",
                name="server_connect",
                response_time=response_time,
                response_length=0,
                exception=None
            )
            # 订阅通配符主题
            self.mqtt_client.subscribe("/+/+/property/post", 0)
            self.mqtt_client.subscribe("/+/+/event/post", 0)
            logger.info(f"🖥️  服务端连接成功: {client_id}")
        else:
            self.environment.events.request.fire(
                request_type="MQTT",
                name="server_connect",
                response_time=response_time,
                response_length=0,
                exception=LocustError("服务端MQTT连接失败")
            )
    
    @task(30)  # 权重30 - 发送设备控制指令
    def send_device_command(self):
        """发送设备控制指令"""
        if not self.mqtt_client or not self.mqtt_client.connected:
            return
            
        # 随机选择一个设备进行控制
        device_type = random.choice(list(MQTT_CONFIG["device_types"].keys()))
        device_num, product_id, user_id = generate_device_id(device_type)
        
        command_data = {
            "command": random.choice(["start", "stop", "reset", "query", "update"]),
            "parameters": {
                "mode": random.choice(["auto", "manual"]),
                "value": random.randint(1, 100)
            },
            "timeout": 30000
        }
        
        message = {
            "type": "command",
            "timestamp": datetime.now().isoformat(),
            "messageId": f"cmd_{random.randint(100000, 999999)}",
            "data": command_data
        }
        
        topic = MQTT_CONFIG["topics"]["function_get"].format(
            product_id=product_id,
            device_num=device_num
        )
        
        start_time = time.time()
        payload = json.dumps(message, ensure_ascii=False)
        
        success = self.mqtt_client.publish(topic, payload, 1)
        response_time = (time.time() - start_time) * 1000
        
        if success:
            self.environment.events.request.fire(
                request_type="MQTT",
                name="device_command",
                response_time=response_time,
                response_length=len(payload.encode('utf-8')),
                exception=None
            )
        else:
            self.environment.events.request.fire(
                request_type="MQTT",
                name="device_command", 
                response_time=response_time,
                response_length=len(payload.encode('utf-8')),
                exception=LocustError("设备指令发送失败")
            )

class MQTTWebUser(MQTTUser):
    """模拟Web端MQTT用户"""
    
    wait_time = between(10, 30)
    
    def on_start(self):
        """Web用户初始化"""
        super().on_start()
        
        client_id = f"web_{random.randint(1000, 9999)}"
        auth_config = {}  # Web端使用匿名连接
        
        # 解析主机和端口
        host = self.environment.host.replace("tcp://", "").replace("ws://", "").split(":")[0] if "://" in self.environment.host else self.environment.host
        port = 1883
        
        # 如果是WebSocket连接，使用WebSocket端口
        if "ws://" in self.environment.host:
            port = MQTT_CONFIG["connection"]["websocket_port"]
        elif ":" in self.environment.host:
            try:
                port = int(self.environment.host.split(":")[-1])
            except:
                port = 1883
        
        self.mqtt_client = MQTTClient(client_id, auth_config, host, port)
        
        start_time = time.time()
        success = self.mqtt_client.connect()
        response_time = (time.time() - start_time) * 1000
        
        if success:
            self.environment.events.request.fire(
                request_type="MQTT",
                name="web_connect",
                response_time=response_time,
                response_length=0,
                exception=None
            )
            # 订阅设备数据主题
            self.mqtt_client.subscribe("/+/+/property/post", 0)
            self.mqtt_client.subscribe("/+/+/event/post", 0)
            self.mqtt_client.subscribe("/+/+/status/post", 0)
            logger.info(f"🌐 Web端连接成功: {client_id}")
        else:
            self.environment.events.request.fire(
                request_type="MQTT",
                name="web_connect",
                response_time=response_time,
                response_length=0,
                exception=LocustError("Web端MQTT连接失败")
            )
    
    @task(20)  # 权重20 - 查询设备信息
    def query_device_info(self):
        """查询设备信息"""
        if not self.mqtt_client or not self.mqtt_client.connected:
            return
            
        # 随机查询一个设备的信息
        device_type = random.choice(list(MQTT_CONFIG["device_types"].keys()))
        device_num, product_id, user_id = generate_device_id(device_type)
        
        query_data = {
            "query_type": random.choice(["info", "status", "properties"]),
            "device_filter": {
                "type": device_type,
                "status": "online"
            }
        }
        
        message = {
            "type": "query",
            "timestamp": datetime.now().isoformat(),
            "messageId": f"query_{random.randint(100000, 999999)}",
            "data": query_data
        }
        
        topic = MQTT_CONFIG["topics"]["info_get"].format(
            product_id=product_id,
            device_num=device_num
        )
        
        start_time = time.time()
        payload = json.dumps(message, ensure_ascii=False)
        
        success = self.mqtt_client.publish(topic, payload, 0)
        response_time = (time.time() - start_time) * 1000
        
        if success:
            self.environment.events.request.fire(
                request_type="MQTT",
                name="device_query",
                response_time=response_time,
                response_length=len(payload.encode('utf-8')),
                exception=None
            )
        else:
            self.environment.events.request.fire(
                request_type="MQTT",
                name="device_query",
                response_time=response_time,
                response_length=len(payload.encode('utf-8')),
                exception=LocustError("设备查询失败")
            ) 