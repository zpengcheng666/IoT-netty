#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
SYDH IoT Platform - MQTT协议核心接口压力测试
专门测试MQTT连接认证、消息发布、主题订阅等核心功能
基于数据库真实测试设备数据
"""

import json
import random
import time
import threading
import logging
from datetime import datetime, timedelta
from typing import Dict, List, Optional
import paho.mqtt.client as mqtt
from locust import User, task, between, events
from locust.env import Environment

# 配置日志
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# ===== 基于数据库的真实测试设备配置 =====
MQTT_TEST_CONFIG = {
    "broker": {
        "host": "localhost",  # MQTT Broker地址
        "port": 1883,         # MQTT端口
        "keepalive": 60,      # 心跳间隔
        "clean_session": True,
        "protocol": mqtt.MQTTv311
    },
    
    # 基于init_test_data.sql的真实产品配置
    "products": {
        1: {
            "name": "测试传感器产品",
            "account": "sensor_test",
            "password": "P_sensor_123456", 
            "secret": "K_sensor_secret_001",
            "protocol": "MQTT",
            "device_count": 100,
            "device_prefix": "TEST_SENSOR",
            "topics": {
                "property_post": "/sys/{device_sn}/sensor_test/thing/event/property/post",
                "service_reply": "/sys/{device_sn}/sensor_test/thing/service/property/set_reply",
                "info_post": "/sys/{device_sn}/sensor_test/thing/event/info/post"
            },
            "properties": {
                "temperature": {"type": "decimal", "min": -50, "max": 100, "unit": "℃"},
                "humidity": {"type": "decimal", "min": 0, "max": 100, "unit": "%"}, 
                "pressure": {"type": "decimal", "min": 800, "max": 1200, "unit": "hPa"}
            },
            "services": ["restart"]
        },
        
        2: {
            "name": "测试摄像头产品",
            "account": "camera_test", 
            "password": "P_camera_123456",
            "secret": "K_camera_secret_002",
            "protocol": "MQTT",
            "device_count": 30,
            "device_prefix": "TEST_CAMERA",
            "topics": {
                "property_post": "/sys/{device_sn}/camera_test/thing/event/property/post",
                "service_reply": "/sys/{device_sn}/camera_test/thing/service/property/set_reply",
                "info_post": "/sys/{device_sn}/camera_test/thing/event/info/post"
            },
            "properties": {
                "recording": {"type": "bool"},
                "resolution": {"type": "enum", "values": ["720P", "1080P", "4K"]},
                "storage_used": {"type": "decimal", "min": 0, "max": 100, "unit": "%"}
            },
            "services": ["take_photo"]
        },
        
        3: {
            "name": "测试PLC产品",
            "account": "plc_test",
            "password": "P_plc_123456", 
            "secret": "K_plc_secret_003",
            "protocol": "MQTT",
            "device_count": 15,
            "device_prefix": "TEST_PLC",
            "topics": {
                "property_post": "/sys/{device_sn}/plc_test/thing/event/property/post",
                "service_reply": "/sys/{device_sn}/plc_test/thing/service/property/set_reply",
                "info_post": "/sys/{device_sn}/plc_test/thing/event/info/post"
            },
            "properties": {
                "production_count": {"type": "integer", "min": 0, "max": 999999, "unit": "个"},
                "machine_pressure": {"type": "decimal", "min": 0, "max": 10, "unit": "MPa"},
                "machine_temperature": {"type": "decimal", "min": 0, "max": 150, "unit": "℃"}
            },
            "services": ["set_target"]
        },
        
        4: {
            "name": "测试网关产品",
            "account": "gateway_test",
            "password": "P_gateway_123456",
            "secret": "K_gateway_secret_004", 
            "protocol": "MQTT",
            "device_count": 10,
            "device_prefix": "TEST_GATEWAY",
            "topics": {
                "property_post": "/sys/{device_sn}/gateway_test/thing/event/property/post",
                "service_reply": "/sys/{device_sn}/gateway_test/thing/service/property/set_reply",
                "info_post": "/sys/{device_sn}/gateway_test/thing/event/info/post"
            },
            "properties": {
                "connected_devices": {"type": "integer", "min": 0, "max": 100, "unit": "个"},
                "cpu_usage": {"type": "decimal", "min": 0, "max": 100, "unit": "%"},
                "memory_usage": {"type": "decimal", "min": 0, "max": 100, "unit": "%"}
            },
            "services": ["restart_service"]
        },
        
        5: {
            "name": "测试电表产品",
            "account": "meter_test",
            "password": "P_meter_123456",
            "secret": "K_meter_secret_005",
            "protocol": "MQTT", 
            "device_count": 20,
            "device_prefix": "TEST_METER",
            "topics": {
                "property_post": "/sys/{device_sn}/meter_test/thing/event/property/post",
                "service_reply": "/sys/{device_sn}/meter_test/thing/service/property/set_reply",
                "info_post": "/sys/{device_sn}/meter_test/thing/event/info/post"
            },
            "properties": {
                "current_reading": {"type": "decimal", "min": 0, "max": 999999, "unit": "kWh"},
                "voltage": {"type": "decimal", "min": 180, "max": 260, "unit": "V"},
                "current": {"type": "decimal", "min": 0, "max": 100, "unit": "A"}
            },
            "services": ["read_meter"]
        }
    }
}

# ===== 全局统计变量 =====
mqtt_stats = {
    "total_connections": 0,
    "successful_connections": 0, 
    "failed_connections": 0,
    "total_publishes": 0,
    "successful_publishes": 0,
    "failed_publishes": 0,
    "total_subscribes": 0,
    "successful_subscribes": 0,
    "failed_subscribes": 0,
    "connection_errors": {},
    "publish_errors": {},
    "avg_connection_time": 0,
    "avg_publish_time": 0
}

mqtt_stats_lock = threading.Lock()

# ===== 数据生成函数 =====
def generate_device_serial(product_id: int, device_index: int) -> str:
    """根据产品ID和设备索引生成设备序列号"""
    product_config = MQTT_TEST_CONFIG["products"][product_id]
    prefix = product_config["device_prefix"]
    return f"{prefix}{str(device_index).zfill(6)}"

def generate_property_data(product_id: int) -> Dict:
    """根据产品类型生成属性数据"""
    product = MQTT_TEST_CONFIG["products"][product_id]
    properties = product["properties"]
    
    data = {
        "timestamp": int(time.time() * 1000),
        "deviceSN": "",  # 将在使用时填充
        "method": "thing.event.property.post",
        "version": "1.0.0"
    }
    
    params = {}
    for prop_name, prop_config in properties.items():
        if prop_config["type"] == "decimal":
            min_val = prop_config.get("min", 0)
            max_val = prop_config.get("max", 100)
            params[prop_name] = round(random.uniform(min_val, max_val), 2)
        elif prop_config["type"] == "integer":
            min_val = prop_config.get("min", 0)
            max_val = prop_config.get("max", 1000)
            params[prop_name] = random.randint(min_val, max_val)
        elif prop_config["type"] == "bool":
            params[prop_name] = random.choice([True, False])
        elif prop_config["type"] == "enum":
            params[prop_name] = random.choice(prop_config["values"])
    
    data["params"] = params
    return data

def generate_service_reply_data(product_id: int, service_name: str) -> Dict:
    """生成服务调用回复数据"""
    return {
        "id": f"service_reply_{int(time.time())}",
        "code": random.choice([200, 200, 200, 400, 500]),  # 80%成功率
        "data": {
            "service": service_name,
            "result": "success" if random.random() > 0.2 else "failed",
            "timestamp": int(time.time() * 1000)
        },
        "method": "thing.service.property.set_reply",
        "version": "1.0.0"
    }

def generate_info_data(product_id: int) -> Dict:
    """生成设备信息数据"""
    return {
        "timestamp": int(time.time() * 1000),
        "method": "thing.event.info.post",
        "version": "1.0.0",
        "params": {
            "level": random.choice(["INFO", "WARN", "ERROR"]),
            "message": f"Device message {random.randint(1, 1000)}",
            "code": random.randint(1000, 9999)
        }
    }

# ===== MQTT客户端封装类 =====
class MQTTTestClient:
    """MQTT测试客户端封装"""
    
    def __init__(self, product_id: int, device_index: int):
        self.product_id = product_id
        self.device_index = device_index
        self.product_config = MQTT_TEST_CONFIG["products"][product_id]
        self.device_sn = generate_device_serial(product_id, device_index)
        
        # MQTT客户端配置
        self.client = mqtt.Client(
            client_id=self.device_sn,
            clean_session=MQTT_TEST_CONFIG["broker"]["clean_session"],
            protocol=MQTT_TEST_CONFIG["broker"]["protocol"]
        )
        
        # 设置认证信息
        self.client.username_pw_set(
            username=self.product_config["account"],
            password=self.product_config["password"]
        )
        
        # 设置回调函数
        self.client.on_connect = self._on_connect
        self.client.on_disconnect = self._on_disconnect  
        self.client.on_publish = self._on_publish
        self.client.on_subscribe = self._on_subscribe
        self.client.on_message = self._on_message
        
        # 状态跟踪
        self.connected = False
        self.connection_start_time = 0
        self.last_publish_time = 0
        self.publish_results = {}
        
    def _on_connect(self, client, userdata, flags, rc):
        """连接回调"""
        global mqtt_stats
        with mqtt_stats_lock:
            mqtt_stats["total_connections"] += 1
            if rc == 0:
                self.connected = True
                mqtt_stats["successful_connections"] += 1
                connection_time = time.time() - self.connection_start_time
                mqtt_stats["avg_connection_time"] = (
                    (mqtt_stats["avg_connection_time"] * (mqtt_stats["successful_connections"] - 1) + connection_time) /
                    mqtt_stats["successful_connections"]
                )
                logger.debug(f"设备 {self.device_sn} 连接成功，耗时: {connection_time:.3f}s")
            else:
                self.connected = False  
                mqtt_stats["failed_connections"] += 1
                error_desc = f"连接失败，返回码: {rc}"
                mqtt_stats["connection_errors"][error_desc] = mqtt_stats["connection_errors"].get(error_desc, 0) + 1
                logger.error(f"设备 {self.device_sn} {error_desc}")
    
    def _on_disconnect(self, client, userdata, rc):
        """断开连接回调"""
        self.connected = False
        if rc != 0:
            logger.warning(f"设备 {self.device_sn} 意外断开连接，返回码: {rc}")
    
    def _on_publish(self, client, userdata, mid):
        """发布回调"""
        global mqtt_stats
        with mqtt_stats_lock:
            mqtt_stats["successful_publishes"] += 1
            if self.last_publish_time > 0:
                publish_time = time.time() - self.last_publish_time
                mqtt_stats["avg_publish_time"] = (
                    (mqtt_stats["avg_publish_time"] * (mqtt_stats["successful_publishes"] - 1) + publish_time) /
                    mqtt_stats["successful_publishes"]
                )
        logger.debug(f"设备 {self.device_sn} 消息发布成功，MID: {mid}")
    
    def _on_subscribe(self, client, userdata, mid, granted_qos):
        """订阅回调"""
        global mqtt_stats
        with mqtt_stats_lock:
            mqtt_stats["successful_subscribes"] += 1
        logger.debug(f"设备 {self.device_sn} 订阅成功，MID: {mid}, QoS: {granted_qos}")
    
    def _on_message(self, client, userdata, msg):
        """消息接收回调"""
        logger.debug(f"设备 {self.device_sn} 收到消息，主题: {msg.topic}, 负载: {msg.payload.decode()}")
    
    def connect(self) -> bool:
        """连接到MQTT Broker"""
        global mqtt_stats
        try:
            self.connection_start_time = time.time()
            broker_config = MQTT_TEST_CONFIG["broker"]
            
            result = self.client.connect(
                host=broker_config["host"],
                port=broker_config["port"], 
                keepalive=broker_config["keepalive"]
            )
            
            if result == mqtt.MQTT_ERR_SUCCESS:
                self.client.loop_start()
                # 等待连接建立
                timeout = 10  # 10秒超时
                start_time = time.time()
                while not self.connected and (time.time() - start_time) < timeout:
                    time.sleep(0.1)
                
                return self.connected
            else:
                with mqtt_stats_lock:
                    mqtt_stats["total_connections"] += 1
                    mqtt_stats["failed_connections"] += 1
                    error_desc = f"连接失败，错误码: {result}"
                    mqtt_stats["connection_errors"][error_desc] = mqtt_stats["connection_errors"].get(error_desc, 0) + 1
                return False
                
        except Exception as e:
            with mqtt_stats_lock:
                mqtt_stats["total_connections"] += 1
                mqtt_stats["failed_connections"] += 1
                error_desc = f"连接异常: {str(e)}"
                mqtt_stats["connection_errors"][error_desc] = mqtt_stats["connection_errors"].get(error_desc, 0) + 1
            logger.error(f"设备 {self.device_sn} 连接异常: {e}")
            return False
    
    def disconnect(self):
        """断开连接"""
        if self.connected:
            self.client.loop_stop()
            self.client.disconnect()
            self.connected = False
    
    def publish_property_data(self, qos: int = 0) -> bool:
        """发布属性数据"""
        global mqtt_stats
        if not self.connected:
            return False
        
        try:
            # 生成属性数据
            data = generate_property_data(self.product_id)
            data["deviceSN"] = self.device_sn
            
            # 获取发布主题
            topic = self.product_config["topics"]["property_post"].format(device_sn=self.device_sn)
            payload = json.dumps(data, ensure_ascii=False)
            
            # 发布消息
            self.last_publish_time = time.time()
            with mqtt_stats_lock:
                mqtt_stats["total_publishes"] += 1
            
            result = self.client.publish(topic, payload, qos)
            
            if result.rc != mqtt.MQTT_ERR_SUCCESS:
                with mqtt_stats_lock:
                    mqtt_stats["failed_publishes"] += 1
                    error_desc = f"发布失败，错误码: {result.rc}"
                    mqtt_stats["publish_errors"][error_desc] = mqtt_stats["publish_errors"].get(error_desc, 0) + 1
                return False
            
            return True
            
        except Exception as e:
            with mqtt_stats_lock:
                mqtt_stats["total_publishes"] += 1
                mqtt_stats["failed_publishes"] += 1
                error_desc = f"发布异常: {str(e)}"
                mqtt_stats["publish_errors"][error_desc] = mqtt_stats["publish_errors"].get(error_desc, 0) + 1
            logger.error(f"设备 {self.device_sn} 发布属性数据异常: {e}")
            return False
    
    def publish_service_reply(self, service_name: str, qos: int = 0) -> bool:
        """发布服务回复"""
        global mqtt_stats
        if not self.connected:
            return False
        
        try:
            data = generate_service_reply_data(self.product_id, service_name)
            topic = self.product_config["topics"]["service_reply"].format(device_sn=self.device_sn)
            payload = json.dumps(data, ensure_ascii=False)
            
            self.last_publish_time = time.time()
            with mqtt_stats_lock:
                mqtt_stats["total_publishes"] += 1
            
            result = self.client.publish(topic, payload, qos)
            
            if result.rc != mqtt.MQTT_ERR_SUCCESS:
                with mqtt_stats_lock:
                    mqtt_stats["failed_publishes"] += 1
                    error_desc = f"服务回复发布失败，错误码: {result.rc}"
                    mqtt_stats["publish_errors"][error_desc] = mqtt_stats["publish_errors"].get(error_desc, 0) + 1
                return False
            
            return True
            
        except Exception as e:
            with mqtt_stats_lock:
                mqtt_stats["total_publishes"] += 1
                mqtt_stats["failed_publishes"] += 1
                error_desc = f"服务回复发布异常: {str(e)}"
                mqtt_stats["publish_errors"][error_desc] = mqtt_stats["publish_errors"].get(error_desc, 0) + 1
            logger.error(f"设备 {self.device_sn} 发布服务回复异常: {e}")
            return False
    
    def subscribe_service_commands(self) -> bool:
        """订阅服务指令主题"""
        global mqtt_stats
        if not self.connected:
            return False
        
        try:
            # 订阅服务指令主题
            command_topic = f"/sys/{self.device_sn}/{self.product_config['account']}/thing/service/property/set"
            
            with mqtt_stats_lock:
                mqtt_stats["total_subscribes"] += 1
            
            result = self.client.subscribe(command_topic, qos=1)
            
            if result[0] != mqtt.MQTT_ERR_SUCCESS:
                with mqtt_stats_lock:
                    mqtt_stats["failed_subscribes"] = mqtt_stats.get("failed_subscribes", 0) + 1
                return False
            
            return True
            
        except Exception as e:
            with mqtt_stats_lock:
                mqtt_stats["total_subscribes"] += 1
                mqtt_stats["failed_subscribes"] = mqtt_stats.get("failed_subscribes", 0) + 1
            logger.error(f"设备 {self.device_sn} 订阅服务指令异常: {e}")
            return False

# ===== Locust用户类 =====
class MQTTUser(User):
    """MQTT协议压力测试用户"""
    
    wait_time = between(1, 3)  # 消息发送间隔
    
    def on_start(self):
        """用户启动时初始化"""
        # 随机选择产品和设备
        self.product_id = random.choice(list(MQTT_TEST_CONFIG["products"].keys()))
        product_config = MQTT_TEST_CONFIG["products"][self.product_id]
        self.device_index = random.randint(1, product_config["device_count"])
        
        # 创建MQTT客户端
        self.mqtt_client = MQTTTestClient(self.product_id, self.device_index)
        
        # 建立连接
        if not self.mqtt_client.connect():
            logger.error(f"设备 {self.mqtt_client.device_sn} 连接失败")
            return
        
        # 订阅服务指令
        self.mqtt_client.subscribe_service_commands()
        
        logger.info(f"用户启动：产品ID={self.product_id}, 设备={self.mqtt_client.device_sn}")
    
    def on_stop(self):
        """用户停止时清理"""
        if hasattr(self, 'mqtt_client'):
            self.mqtt_client.disconnect()
            logger.info(f"设备 {self.mqtt_client.device_sn} 已断开连接")
    
    @task(70)  # 70%的任务权重 - 属性数据上报（核心业务）
    def publish_property_data(self):
        """发布设备属性数据 - 最高频次"""
        start_time = time.time()
        
        # 随机选择QoS等级
        qos = random.choices([0, 1, 2], weights=[70, 25, 5], k=1)[0]
        
        success = self.mqtt_client.publish_property_data(qos)
        
        end_time = time.time()
        response_time = (end_time - start_time) * 1000  # 转换为毫秒
        
        # 记录Locust统计信息
        if success:
            events.request_success.fire(
                request_type="MQTT",
                name="publish_property_data",
                response_time=response_time,
                response_length=len(json.dumps(generate_property_data(self.product_id)))
            )
        else:
            events.request_failure.fire(
                request_type="MQTT", 
                name="publish_property_data",
                response_time=response_time,
                response_length=0,
                exception=Exception("属性数据发布失败")
            )
    
    @task(20)  # 20%的任务权重 - 服务回复
    def publish_service_reply(self):
        """发布服务调用回复"""
        start_time = time.time()
        
        # 随机选择服务
        product_config = MQTT_TEST_CONFIG["products"][self.product_id]
        service_name = random.choice(product_config["services"])
        
        success = self.mqtt_client.publish_service_reply(service_name)
        
        end_time = time.time()
        response_time = (end_time - start_time) * 1000
        
        if success:
            events.request_success.fire(
                request_type="MQTT",
                name="publish_service_reply", 
                response_time=response_time,
                response_length=len(json.dumps(generate_service_reply_data(self.product_id, service_name)))
            )
        else:
            events.request_failure.fire(
                request_type="MQTT",
                name="publish_service_reply",
                response_time=response_time, 
                response_length=0,
                exception=Exception("服务回复发布失败")
            )
    
    @task(10)  # 10%的任务权重 - 信息事件
    def publish_info_event(self):
        """发布设备信息事件"""
        global mqtt_stats
        start_time = time.time()
        
        try:
            data = generate_info_data(self.product_id)
            topic = self.mqtt_client.product_config["topics"]["info_post"].format(
                device_sn=self.mqtt_client.device_sn
            )
            payload = json.dumps(data, ensure_ascii=False)
            
            with mqtt_stats_lock:
                mqtt_stats["total_publishes"] += 1
            
            result = self.mqtt_client.client.publish(topic, payload, qos=0)
            success = (result.rc == mqtt.MQTT_ERR_SUCCESS)
            
            if not success:
                with mqtt_stats_lock:
                    mqtt_stats["failed_publishes"] += 1
            
        except Exception as e:
            success = False
            with mqtt_stats_lock:
                mqtt_stats["total_publishes"] += 1
                mqtt_stats["failed_publishes"] += 1
        
        end_time = time.time()
        response_time = (end_time - start_time) * 1000
        
        if success:
            events.request_success.fire(
                request_type="MQTT",
                name="publish_info_event",
                response_time=response_time,
                response_length=len(payload) if 'payload' in locals() else 0
            )
        else:
            events.request_failure.fire(
                request_type="MQTT",
                name="publish_info_event", 
                response_time=response_time,
                response_length=0,
                exception=Exception("信息事件发布失败")
            )

# ===== 事件监听器 =====
@events.test_start.add_listener
def on_test_start(environment, **kwargs):
    """测试开始事件"""
    logger.info("🚀 SYDH IoT MQTT协议压力测试开始")
    logger.info(f"📊 目标Broker: {MQTT_TEST_CONFIG['broker']['host']}:{MQTT_TEST_CONFIG['broker']['port']}")
    
    # 显示测试产品统计
    total_devices = sum(product["device_count"] for product in MQTT_TEST_CONFIG["products"].values())
    logger.info(f"📱 测试产品数量: {len(MQTT_TEST_CONFIG['products'])}")
    logger.info(f"📱 测试设备总数: {total_devices}")
    
    for product_id, product in MQTT_TEST_CONFIG["products"].items():
        logger.info(f"   - {product['name']}: {product['device_count']}个设备")

@events.test_stop.add_listener  
def on_test_stop(environment, **kwargs):
    """测试停止事件"""
    logger.info("🛑 SYDH IoT MQTT协议压力测试结束")
    
    # 输出详细统计信息
    global mqtt_stats
    with mqtt_stats_lock:
        logger.info("=" * 80)
        logger.info("📊 MQTT协议测试统计报告")
        logger.info("=" * 80)
        
        # 连接统计
        logger.info(f"🔗 连接统计:")
        logger.info(f"   总连接次数: {mqtt_stats['total_connections']}")
        logger.info(f"   成功连接: {mqtt_stats['successful_connections']}")
        logger.info(f"   失败连接: {mqtt_stats['failed_connections']}")
        if mqtt_stats['total_connections'] > 0:
            success_rate = (mqtt_stats['successful_connections'] / mqtt_stats['total_connections']) * 100
            logger.info(f"   连接成功率: {success_rate:.2f}%")
        if mqtt_stats['avg_connection_time'] > 0:
            logger.info(f"   平均连接时间: {mqtt_stats['avg_connection_time']:.3f}s")
        
        # 发布统计
        logger.info(f"📤 消息发布统计:")
        logger.info(f"   总发布次数: {mqtt_stats['total_publishes']}")
        logger.info(f"   成功发布: {mqtt_stats['successful_publishes']}")
        logger.info(f"   失败发布: {mqtt_stats['failed_publishes']}")
        if mqtt_stats['total_publishes'] > 0:
            publish_success_rate = (mqtt_stats['successful_publishes'] / mqtt_stats['total_publishes']) * 100
            logger.info(f"   发布成功率: {publish_success_rate:.2f}%")
        if mqtt_stats['avg_publish_time'] > 0:
            logger.info(f"   平均发布时间: {mqtt_stats['avg_publish_time']:.3f}s")
        
        # 订阅统计
        logger.info(f"📥 主题订阅统计:")
        logger.info(f"   总订阅次数: {mqtt_stats['total_subscribes']}")
        logger.info(f"   成功订阅: {mqtt_stats['successful_subscribes']}")
        
        # 错误统计
        if mqtt_stats['connection_errors']:
            logger.info(f"❌ 连接错误详情:")
            for error, count in mqtt_stats['connection_errors'].items():
                logger.info(f"   {error}: {count}次")
        
        if mqtt_stats['publish_errors']:
            logger.info(f"❌ 发布错误详情:")
            for error, count in mqtt_stats['publish_errors'].items():
                logger.info(f"   {error}: {count}次")

# ===== 主程序入口 =====
if __name__ == "__main__":
    """
    直接运行此脚本进行单机测试
    或者使用locust命令行：
    
    # 基础测试（100个用户，10秒内启动）
    locust -f mqtt_pressure_test.py --host=mqtt://localhost:1883 -u 100 -r 10 -t 60s
    
    # 大规模测试（1000个用户，60秒内启动，运行10分钟）
    locust -f mqtt_pressure_test.py --host=mqtt://localhost:1883 -u 1000 -r 10 -t 600s
    
    # Web界面测试
    locust -f mqtt_pressure_test.py --host=mqtt://localhost:1883
    """
    
    # 简单的单机测试
    import time
    
    logger.info("开始MQTT单机测试...")
    
    # 创建一个测试客户端
    test_client = MQTTTestClient(product_id=1, device_index=1)
    
    if test_client.connect():
        logger.info("✅ 连接成功")
        
        # 发布几条测试消息
        for i in range(5):
            if test_client.publish_property_data():
                logger.info(f"✅ 属性数据发布成功 #{i+1}")
            else:
                logger.error(f"❌ 属性数据发布失败 #{i+1}")
            time.sleep(1)
        
        test_client.disconnect()
        logger.info("✅ 断开连接")
    else:
        logger.error("❌ 连接失败")
    
    logger.info("单机测试完成") 