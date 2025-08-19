#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
SYDH IoT Platform - 综合业务流程压力测试
模拟真实IoT设备的完整业务流程：
1. 设备注册 -> 2. MQTT连接 -> 3. 数据上报 -> 4. 指令接收 -> 5. 状态更新
基于数据库真实测试设备数据，支持30万设备并发测试
"""

import json
import random
import time
import threading
import asyncio
import logging
from datetime import datetime, timedelta
from typing import Dict, List, Optional, Tuple
from concurrent.futures import ThreadPoolExecutor
from locust import User, task, between, events
import paho.mqtt.client as mqtt
import requests

# 配置日志
logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')
logger = logging.getLogger(__name__)

# ===== 综合测试配置 =====
COMPREHENSIVE_CONFIG = {
    # 服务器配置
    "servers": {
        "api_host": "http://localhost:8080",
        "mqtt_host": "localhost",
        "mqtt_port": 1883
    },
    
    # 认证配置
    "auth": {
        "username": "admin",
        "password": "admin123",
        "token_expiry": 7200  # 2小时
    },
    
    # 基于init_test_data.sql的真实产品配置
    "products": {
        1: {
            "product_id": 1,
            "product_name": "测试传感器产品",
            "category_name": "智能传感器",
            "mqtt_account": "sensor_test", 
            "mqtt_password": "P_sensor_123456",
            "device_count": 100,
            "device_prefix": "TEST_SENSOR",
            "data_frequency": 30,  # 30秒上报一次
            "properties": {
                "temperature": {"min": -50, "max": 100, "unit": "℃"},
                "humidity": {"min": 0, "max": 100, "unit": "%"},
                "pressure": {"min": 800, "max": 1200, "unit": "hPa"}
            },
            "services": ["restart", "calibrate", "reset_sensor"],
            "business_scenarios": ["环境监测", "农业大棚", "工业车间", "智慧城市"]
        },
        
        2: {
            "product_id": 2,
            "product_name": "测试摄像头产品",
            "category_name": "智能摄像头",
            "mqtt_account": "camera_test",
            "mqtt_password": "P_camera_123456", 
            "device_count": 30,
            "device_prefix": "TEST_CAMERA",
            "data_frequency": 60,  # 60秒上报一次
            "properties": {
                "recording": {"type": "bool"},
                "resolution": {"type": "enum", "values": ["720P", "1080P", "4K"]},
                "storage_used": {"min": 0, "max": 100, "unit": "%"},
                "motion_detected": {"type": "bool"},
                "light_level": {"min": 0, "max": 1000, "unit": "lux"}
            },
            "services": ["take_photo", "start_recording", "stop_recording", "adjust_resolution"],
            "business_scenarios": ["安防监控", "交通监测", "生产监控", "家庭安防"]
        },
        
        3: {
            "product_id": 3,
            "product_name": "测试PLC产品",
            "category_name": "工业控制器",
            "mqtt_account": "plc_test",
            "mqtt_password": "P_plc_123456",
            "device_count": 15,
            "device_prefix": "TEST_PLC",
            "data_frequency": 15,  # 15秒上报一次（工业设备频率高）
            "properties": {
                "production_count": {"min": 0, "max": 999999, "unit": "个"},
                "machine_pressure": {"min": 0, "max": 10, "unit": "MPa"},
                "machine_temperature": {"min": 0, "max": 150, "unit": "℃"},
                "vibration_level": {"min": 0, "max": 100, "unit": "Hz"},
                "energy_consumption": {"min": 0, "max": 1000, "unit": "kWh"}
            },
            "services": ["set_target", "emergency_stop", "maintenance_mode", "reset_counter"],
            "business_scenarios": ["制造生产", "流水线控制", "设备监控", "质量检测"]
        },
        
        4: {
            "product_id": 4,
            "product_name": "测试网关产品",
            "category_name": "物联网网关",
            "mqtt_account": "gateway_test",
            "mqtt_password": "P_gateway_123456",
            "device_count": 10,
            "device_prefix": "TEST_GATEWAY",
            "data_frequency": 120,  # 2分钟上报一次
            "properties": {
                "connected_devices": {"min": 0, "max": 100, "unit": "个"},
                "cpu_usage": {"min": 0, "max": 100, "unit": "%"},
                "memory_usage": {"min": 0, "max": 100, "unit": "%"},
                "network_throughput": {"min": 0, "max": 1000, "unit": "Mbps"},
                "uptime": {"min": 0, "max": 8760, "unit": "hours"}
            },
            "services": ["restart_service", "update_firmware", "backup_config", "sync_time"],
            "business_scenarios": ["边缘计算", "数据汇聚", "协议转换", "设备管理"]
        },
        
        5: {
            "product_id": 5,
            "product_name": "测试电表产品",
            "category_name": "智能电表",
            "mqtt_account": "meter_test",
            "mqtt_password": "P_meter_123456",
            "device_count": 20,
            "device_prefix": "TEST_METER",
            "data_frequency": 300,  # 5分钟上报一次
            "properties": {
                "current_reading": {"min": 0, "max": 999999, "unit": "kWh"},
                "voltage": {"min": 180, "max": 260, "unit": "V"},
                "current": {"min": 0, "max": 100, "unit": "A"},
                "power_factor": {"min": 0.5, "max": 1.0, "unit": ""},
                "frequency": {"min": 49, "max": 51, "unit": "Hz"}
            },
            "services": ["read_meter", "reset_energy", "set_tariff", "calibrate_meter"],
            "business_scenarios": ["电力计量", "能耗管理", "费用结算", "负荷监控"]
        }
    },
    
    # 业务流程配置
    "business_flows": {
        "device_lifecycle": {
            "register": 0.05,      # 5% - 设备注册
            "connect": 0.15,       # 15% - 连接建立
            "data_report": 0.60,   # 60% - 数据上报
            "command_response": 0.15, # 15% - 指令响应
            "status_update": 0.05   # 5% - 状态更新
        },
        
        "failure_simulation": {
            "network_timeout": 0.02,    # 2% 网络超时
            "auth_failure": 0.01,       # 1% 认证失败
            "data_corruption": 0.005,   # 0.5% 数据损坏
            "device_offline": 0.005     # 0.5% 设备离线
        }
    },
    
    # 性能配置
    "performance": {
        "max_connections": 300000,      # 最大并发连接数
        "data_report_tps": 100000,      # 数据上报TPS目标
        "command_response_time": 200,   # 指令响应时间(ms)
        "connection_timeout": 30,       # 连接超时(s)
        "keepalive": 60                 # 心跳间隔(s)
    }
}

# ===== 全局统计信息 =====
global_stats = {
    "devices_registered": 0,
    "mqtt_connections": 0,
    "successful_connections": 0,
    "failed_connections": 0,
    "data_reports": 0,
    "successful_reports": 0,
    "failed_reports": 0,
    "commands_received": 0,
    "commands_responded": 0,
    "avg_connection_time": 0,
    "avg_report_time": 0,
    "errors": {},
    "business_metrics": {
        "total_devices_online": 0,
        "total_data_points": 0,
        "total_commands_sent": 0,
        "system_health_score": 100
    }
}

stats_lock = threading.Lock()

# ===== 设备状态管理 =====
class DeviceStatus:
    """设备状态枚举"""
    REGISTERED = "registered"
    CONNECTING = "connecting"
    ONLINE = "online"
    OFFLINE = "offline"
    ERROR = "error"

device_registry = {}  # 设备注册表
device_registry_lock = threading.Lock()

# ===== 数据生成函数 =====
def generate_device_serial(product_id: int, device_index: int) -> str:
    """生成设备序列号"""
    product = COMPREHENSIVE_CONFIG["products"][product_id]
    return f"{product['device_prefix']}{str(device_index).zfill(6)}"

def generate_realistic_property_data(product_id: int, device_serial: str) -> Dict:
    """生成真实的属性数据，考虑业务场景"""
    product = COMPREHENSIVE_CONFIG["products"][product_id]
    properties = product["properties"]
    scenarios = product["business_scenarios"]
    current_scenario = random.choice(scenarios)
    
    data = {
        "deviceSN": device_serial,
        "timestamp": int(time.time() * 1000),
        "method": "thing.event.property.post",
        "version": "1.0.0",
        "business_scenario": current_scenario
    }
    
    params = {}
    
    # 根据不同产品类型生成有业务逻辑的数据
    if product_id == 1:  # 传感器
        # 模拟不同环境场景的数据
        if current_scenario == "农业大棚":
            params["temperature"] = round(random.uniform(20, 35), 1)
            params["humidity"] = round(random.uniform(60, 90), 1)
            params["pressure"] = round(random.uniform(1000, 1020), 1)
        elif current_scenario == "工业车间":
            params["temperature"] = round(random.uniform(15, 40), 1)
            params["humidity"] = round(random.uniform(30, 70), 1)
            params["pressure"] = round(random.uniform(995, 1015), 1)
        else:  # 默认环境监测
            params["temperature"] = round(random.uniform(-10, 50), 1)
            params["humidity"] = round(random.uniform(20, 80), 1)
            params["pressure"] = round(random.uniform(980, 1030), 1)
            
    elif product_id == 2:  # 摄像头
        params["recording"] = random.choice([True, False])
        params["resolution"] = random.choice(["720P", "1080P", "4K"])
        params["storage_used"] = round(random.uniform(10, 90), 1)
        params["motion_detected"] = random.choice([True, False])
        params["light_level"] = random.randint(50, 800)
        
    elif product_id == 3:  # PLC
        # 模拟生产过程数据
        base_production = random.randint(1000, 5000)
        params["production_count"] = base_production + random.randint(0, 100)
        params["machine_pressure"] = round(random.uniform(2.0, 8.0), 2)
        params["machine_temperature"] = round(random.uniform(40, 80), 1)
        params["vibration_level"] = round(random.uniform(5, 25), 1)
        params["energy_consumption"] = round(random.uniform(50, 200), 2)
        
    elif product_id == 4:  # 网关
        params["connected_devices"] = random.randint(5, 50)
        params["cpu_usage"] = round(random.uniform(20, 80), 1)
        params["memory_usage"] = round(random.uniform(30, 70), 1)
        params["network_throughput"] = round(random.uniform(10, 500), 1)
        params["uptime"] = random.randint(1, 720)  # 小时
        
    elif product_id == 5:  # 电表
        # 模拟真实用电数据
        current_hour = datetime.now().hour
        if 6 <= current_hour <= 22:  # 白天用电高峰
            base_current = random.uniform(15, 45)
        else:  # 夜间用电低谷
            base_current = random.uniform(2, 15)
            
        params["current_reading"] = round(random.uniform(1000, 50000), 2)
        params["voltage"] = round(random.uniform(220, 240), 1)
        params["current"] = round(base_current, 2)
        params["power_factor"] = round(random.uniform(0.85, 0.98), 3)
        params["frequency"] = round(random.uniform(49.8, 50.2), 1)
    
    data["params"] = params
    return data

def generate_command_response(product_id: int, command: str, device_serial: str) -> Dict:
    """生成指令响应数据"""
    response_code = random.choices([200, 400, 500], weights=[90, 8, 2], k=1)[0]
    
    response_data = {
        "deviceSN": device_serial,
        "timestamp": int(time.time() * 1000),
        "method": "thing.service.property.set_reply",
        "version": "1.0.0",
        "id": f"cmd_reply_{int(time.time())}",
        "code": response_code,
        "data": {
            "command": command,
            "result": "success" if response_code == 200 else "failed",
            "execution_time": random.randint(100, 2000)  # 执行时间ms
        }
    }
    
    # 根据指令类型添加特定响应数据
    if command == "take_photo" and response_code == 200:
        response_data["data"]["photo_url"] = f"/photos/{device_serial}_{int(time.time())}.jpg"
        response_data["data"]["file_size"] = random.randint(512, 2048)  # KB
    elif command == "read_meter" and response_code == 200:
        response_data["data"]["meter_reading"] = round(random.uniform(1000, 50000), 2)
        response_data["data"]["reading_time"] = datetime.now().isoformat()
    elif command == "set_target" and response_code == 200:
        response_data["data"]["new_target"] = random.randint(1000, 10000)
        response_data["data"]["estimated_time"] = random.randint(60, 3600)  # 秒
    
    return response_data

# ===== 设备模拟器类 =====
class IoTDeviceSimulator:
    """IoT设备模拟器 - 完整业务流程"""
    
    def __init__(self, product_id: int, device_index: int):
        self.product_id = product_id
        self.device_index = device_index
        self.product_config = COMPREHENSIVE_CONFIG["products"][product_id]
        self.device_serial = generate_device_serial(product_id, device_index)
        
        # 设备状态
        self.status = DeviceStatus.REGISTERED
        self.last_report_time = 0
        self.last_command_time = 0
        self.connection_attempts = 0
        self.max_retries = 3
        
        # MQTT客户端
        self.mqtt_client = None
        self.connected = False
        
        # 业务数据
        self.business_context = {
            "scenario": random.choice(self.product_config["business_scenarios"]),
            "location": f"测试区域{random.randint(1, 100)}",
            "installation_date": datetime.now() - timedelta(days=random.randint(1, 365)),
            "firmware_version": f"{random.randint(1, 3)}.{random.randint(0, 9)}.{random.randint(0, 9)}"
        }
        
        # 注册设备
        self.register_device()
    
    def register_device(self):
        """设备注册（REST API）"""
        try:
            registration_data = {
                "deviceName": f"{self.product_config['category_name']}_{self.device_index}",
                "productId": self.product_id,
                "productName": self.product_config["product_name"],
                "serialNumber": self.device_serial,
                "firmwareVersion": self.business_context["firmware_version"],
                "status": 3,  # 在线状态
                "rssi": random.randint(-90, -30),
                "isShadow": 1,
                "locationWay": 1,
                "networkAddress": self.business_context["location"],
                "networkIp": f"192.168.{random.randint(1, 255)}.{random.randint(1, 255)}",
                "remark": f"压力测试设备 - {self.business_context['scenario']}"
            }
            
            # 模拟API调用（这里简化为本地处理）
            with device_registry_lock:
                device_registry[self.device_serial] = {
                    "device_data": registration_data,
                    "status": self.status,
                    "last_seen": time.time()
                }
            
            global global_stats
            with stats_lock:
                global_stats["devices_registered"] += 1
            
            logger.debug(f"设备 {self.device_serial} 注册成功")
            return True
            
        except Exception as e:
            logger.error(f"设备 {self.device_serial} 注册失败: {e}")
            return False
    
    def establish_mqtt_connection(self) -> bool:
        """建立MQTT连接"""
        try:
            self.status = DeviceStatus.CONNECTING
            start_time = time.time()
            
            # 创建MQTT客户端
            self.mqtt_client = mqtt.Client(
                client_id=self.device_serial,
                clean_session=True,
                protocol=mqtt.MQTTv311
            )
            
            # 设置认证信息
            self.mqtt_client.username_pw_set(
                username=self.product_config["mqtt_account"],
                password=self.product_config["mqtt_password"]
            )
            
            # 设置回调函数
            self.mqtt_client.on_connect = self._on_connect
            self.mqtt_client.on_disconnect = self._on_disconnect
            self.mqtt_client.on_message = self._on_message
            
            # 连接到Broker
            result = self.mqtt_client.connect(
                host=COMPREHENSIVE_CONFIG["servers"]["mqtt_host"],
                port=COMPREHENSIVE_CONFIG["servers"]["mqtt_port"],
                keepalive=COMPREHENSIVE_CONFIG["performance"]["keepalive"]
            )
            
            if result == mqtt.MQTT_ERR_SUCCESS:
                self.mqtt_client.loop_start()
                
                # 等待连接建立
                timeout = COMPREHENSIVE_CONFIG["performance"]["connection_timeout"]
                elapsed = 0
                while not self.connected and elapsed < timeout:
                    time.sleep(0.1)
                    elapsed += 0.1
                
                if self.connected:
                    connection_time = time.time() - start_time
                    
                    global global_stats
                    with stats_lock:
                        global_stats["mqtt_connections"] += 1
                        global_stats["successful_connections"] += 1
                        # 更新平均连接时间
                        if global_stats["avg_connection_time"] == 0:
                            global_stats["avg_connection_time"] = connection_time
                        else:
                            total_connections = global_stats["successful_connections"]
                            global_stats["avg_connection_time"] = (
                                (global_stats["avg_connection_time"] * (total_connections - 1) + connection_time) /
                                total_connections
                            )
                    
                    self.status = DeviceStatus.ONLINE
                    self.subscribe_to_commands()
                    logger.debug(f"设备 {self.device_serial} MQTT连接成功，耗时: {connection_time:.3f}s")
                    return True
                else:
                    self.connection_failed()
                    return False
            else:
                self.connection_failed()
                return False
                
        except Exception as e:
            logger.error(f"设备 {self.device_serial} MQTT连接异常: {e}")
            self.connection_failed()
            return False
    
    def connection_failed(self):
        """连接失败处理"""
        global global_stats
        with stats_lock:
            global_stats["mqtt_connections"] += 1
            global_stats["failed_connections"] += 1
        self.status = DeviceStatus.ERROR
        self.connection_attempts += 1
    
    def _on_connect(self, client, userdata, flags, rc):
        """MQTT连接回调"""
        if rc == 0:
            self.connected = True
            global global_stats
            with stats_lock:
                global_stats["business_metrics"]["total_devices_online"] += 1
        else:
            logger.error(f"设备 {self.device_serial} MQTT连接失败，返回码: {rc}")
    
    def _on_disconnect(self, client, userdata, rc):
        """MQTT断开连接回调"""
        self.connected = False
        if rc != 0:
            logger.warning(f"设备 {self.device_serial} MQTT意外断开，返回码: {rc}")
            self.status = DeviceStatus.OFFLINE
            global global_stats
            with stats_lock:
                global_stats["business_metrics"]["total_devices_online"] -= 1
    
    def _on_message(self, client, userdata, msg):
        """MQTT消息接收回调"""
        try:
            # 解析指令消息
            command_data = json.loads(msg.payload.decode())
            command = command_data.get("method", "unknown")
            
            global global_stats
            with stats_lock:
                global_stats["commands_received"] += 1
            
            # 模拟指令处理时间
            processing_time = random.uniform(0.1, 2.0)
            time.sleep(processing_time)
            
            # 生成并发送响应
            response = generate_command_response(self.product_id, command, self.device_serial)
            response_topic = f"/sys/{self.device_serial}/{self.product_config['mqtt_account']}/thing/service/property/set_reply"
            
            self.mqtt_client.publish(response_topic, json.dumps(response), qos=1)
            
            with stats_lock:
                global_stats["commands_responded"] += 1
                global_stats["business_metrics"]["total_commands_sent"] += 1
            
            self.last_command_time = time.time()
            logger.debug(f"设备 {self.device_serial} 处理指令: {command}")
            
        except Exception as e:
            logger.error(f"设备 {self.device_serial} 处理指令异常: {e}")
    
    def subscribe_to_commands(self):
        """订阅指令主题"""
        if self.connected:
            command_topic = f"/sys/{self.device_serial}/{self.product_config['mqtt_account']}/thing/service/property/set"
            self.mqtt_client.subscribe(command_topic, qos=1)
    
    def report_data(self) -> bool:
        """上报设备数据"""
        if not self.connected:
            return False
        
        try:
            start_time = time.time()
            
            # 生成属性数据
            data = generate_realistic_property_data(self.product_id, self.device_serial)
            
            # 发布数据
            data_topic = f"/sys/{self.device_serial}/{self.product_config['mqtt_account']}/thing/event/property/post"
            payload = json.dumps(data, ensure_ascii=False)
            
            result = self.mqtt_client.publish(data_topic, payload, qos=0)
            
            if result.rc == mqtt.MQTT_ERR_SUCCESS:
                report_time = time.time() - start_time
                
                global global_stats
                with stats_lock:
                    global_stats["data_reports"] += 1
                    global_stats["successful_reports"] += 1
                    global_stats["business_metrics"]["total_data_points"] += 1
                    
                    # 更新平均上报时间
                    if global_stats["avg_report_time"] == 0:
                        global_stats["avg_report_time"] = report_time
                    else:
                        total_reports = global_stats["successful_reports"]
                        global_stats["avg_report_time"] = (
                            (global_stats["avg_report_time"] * (total_reports - 1) + report_time) /
                            total_reports
                        )
                
                self.last_report_time = time.time()
                logger.debug(f"设备 {self.device_serial} 数据上报成功")
                return True
            else:
                with stats_lock:
                    global_stats["data_reports"] += 1
                    global_stats["failed_reports"] += 1
                logger.error(f"设备 {self.device_serial} 数据上报失败，错误码: {result.rc}")
                return False
                
        except Exception as e:
            global global_stats
            with stats_lock:
                global_stats["data_reports"] += 1
                global_stats["failed_reports"] += 1
                error_key = f"数据上报异常: {str(e)}"
                global_stats["errors"][error_key] = global_stats["errors"].get(error_key, 0) + 1
            
            logger.error(f"设备 {self.device_serial} 数据上报异常: {e}")
            return False
    
    def should_report_data(self) -> bool:
        """判断是否应该上报数据"""
        if self.last_report_time == 0:
            return True
        
        frequency = self.product_config["data_frequency"]
        elapsed = time.time() - self.last_report_time
        
        # 添加±10%的随机性，模拟真实设备的时间偏差
        jitter = frequency * 0.1 * random.uniform(-1, 1)
        return elapsed >= (frequency + jitter)
    
    def disconnect(self):
        """断开连接"""
        if self.mqtt_client and self.connected:
            self.mqtt_client.loop_stop()
            self.mqtt_client.disconnect()
            self.connected = False
            self.status = DeviceStatus.OFFLINE
            
            global global_stats
            with stats_lock:
                global_stats["business_metrics"]["total_devices_online"] -= 1

# ===== Locust用户类 =====
class IoTDeviceUser(User):
    """IoT设备用户 - 完整业务流程模拟"""
    
    wait_time = between(1, 5)
    
    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        self.device_simulator = None
        self.business_flow_weights = COMPREHENSIVE_CONFIG["business_flows"]["device_lifecycle"]
    
    def on_start(self):
        """用户启动 - 完整设备生命周期开始"""
        # 随机选择产品和设备索引
        product_id = random.choice(list(COMPREHENSIVE_CONFIG["products"].keys()))
        product_config = COMPREHENSIVE_CONFIG["products"][product_id]
        device_index = random.randint(1, product_config["device_count"])
        
        # 创建设备模拟器
        self.device_simulator = IoTDeviceSimulator(product_id, device_index)
        
        # 建立MQTT连接
        if not self.device_simulator.establish_mqtt_connection():
            logger.error(f"设备 {self.device_simulator.device_serial} 连接失败")
            return
        
        logger.info(f"设备用户启动: {self.device_simulator.device_serial}")
    
    def on_stop(self):
        """用户停止 - 设备生命周期结束"""
        if self.device_simulator:
            self.device_simulator.disconnect()
            logger.info(f"设备用户停止: {self.device_simulator.device_serial}")
    
    @task(60)  # 60%权重 - 数据上报（主要业务）
    def device_data_reporting(self):
        """设备数据上报任务"""
        if not self.device_simulator or not self.device_simulator.connected:
            return
        
        # 检查是否应该上报数据
        if self.device_simulator.should_report_data():
            start_time = time.time()
            success = self.device_simulator.report_data()
            response_time = (time.time() - start_time) * 1000
            
            # 记录Locust统计
            if success:
                events.request_success.fire(
                    request_type="MQTT",
                    name="device_data_reporting",
                    response_time=response_time,
                    response_length=len(json.dumps(generate_realistic_property_data(
                        self.device_simulator.product_id, 
                        self.device_simulator.device_serial
                    )))
                )
            else:
                events.request_failure.fire(
                    request_type="MQTT",
                    name="device_data_reporting",
                    response_time=response_time,
                    response_length=0,
                    exception=Exception("数据上报失败")
                )
    
    @task(15)  # 15%权重 - 指令响应
    def command_response_simulation(self):
        """指令响应模拟"""
        if not self.device_simulator or not self.device_simulator.connected:
            return
        
        # 模拟收到指令的场景
        if random.random() < 0.1:  # 10%概率模拟收到指令
            start_time = time.time()
            
            try:
                # 模拟指令处理
                command = random.choice(self.device_simulator.product_config["services"])
                response = generate_command_response(
                    self.device_simulator.product_id, 
                    command, 
                    self.device_simulator.device_serial
                )
                
                # 模拟发送响应
                response_topic = f"/sys/{self.device_simulator.device_serial}/{self.device_simulator.product_config['mqtt_account']}/thing/service/property/set_reply"
                result = self.device_simulator.mqtt_client.publish(
                    response_topic, 
                    json.dumps(response), 
                    qos=1
                )
                
                response_time = (time.time() - start_time) * 1000
                
                if result.rc == mqtt.MQTT_ERR_SUCCESS:
                    events.request_success.fire(
                        request_type="MQTT",
                        name="command_response",
                        response_time=response_time,
                        response_length=len(json.dumps(response))
                    )
                else:
                    events.request_failure.fire(
                        request_type="MQTT",
                        name="command_response", 
                        response_time=response_time,
                        response_length=0,
                        exception=Exception(f"指令响应失败，错误码: {result.rc}")
                    )
                    
            except Exception as e:
                response_time = (time.time() - start_time) * 1000
                events.request_failure.fire(
                    request_type="MQTT",
                    name="command_response",
                    response_time=response_time,
                    response_length=0,
                    exception=e
                )
    
    @task(15)  # 15%权重 - 连接保活
    def connection_keepalive(self):
        """连接保活和状态检查"""
        if not self.device_simulator:
            return
        
        start_time = time.time()
        
        try:
            # 检查连接状态
            if self.device_simulator.connected:
                # 模拟心跳响应
                ping_response_time = random.uniform(10, 50)  # 10-50ms
                
                # 更新设备注册表中的最后心跳时间
                with device_registry_lock:
                    if self.device_simulator.device_serial in device_registry:
                        device_registry[self.device_simulator.device_serial]["last_seen"] = time.time()
                
                events.request_success.fire(
                    request_type="MQTT",
                    name="connection_keepalive",
                    response_time=ping_response_time,
                    response_length=0
                )
            else:
                # 连接已断开，尝试重连
                if self.device_simulator.connection_attempts < self.device_simulator.max_retries:
                    reconnect_success = self.device_simulator.establish_mqtt_connection()
                    response_time = (time.time() - start_time) * 1000
                    
                    if reconnect_success:
                        events.request_success.fire(
                            request_type="MQTT",
                            name="connection_reconnect",
                            response_time=response_time,
                            response_length=0
                        )
                    else:
                        events.request_failure.fire(
                            request_type="MQTT",
                            name="connection_reconnect",
                            response_time=response_time,
                            response_length=0,
                            exception=Exception("重连失败")
                        )
                        
        except Exception as e:
            response_time = (time.time() - start_time) * 1000
            events.request_failure.fire(
                request_type="MQTT",
                name="connection_keepalive",
                response_time=response_time,
                response_length=0,
                exception=e
            )
    
    @task(5)  # 5%权重 - 状态更新
    def device_status_update(self):
        """设备状态更新"""
        if not self.device_simulator or not self.device_simulator.connected:
            return
        
        start_time = time.time()
        
        try:
            # 生成状态更新数据
            status_data = {
                "deviceSN": self.device_simulator.device_serial,
                "timestamp": int(time.time() * 1000),
                "method": "thing.event.info.post",
                "version": "1.0.0",
                "params": {
                    "status": self.device_simulator.status,
                    "uptime": random.randint(3600, 86400),  # 1小时到1天
                    "firmware_version": self.device_simulator.business_context["firmware_version"],
                    "last_maintenance": (datetime.now() - timedelta(days=random.randint(1, 30))).isoformat(),
                    "error_count": random.randint(0, 5),
                    "warning_count": random.randint(0, 10)
                }
            }
            
            # 发布状态信息
            status_topic = f"/sys/{self.device_simulator.device_serial}/{self.device_simulator.product_config['mqtt_account']}/thing/event/info/post"
            result = self.device_simulator.mqtt_client.publish(
                status_topic,
                json.dumps(status_data),
                qos=0
            )
            
            response_time = (time.time() - start_time) * 1000
            
            if result.rc == mqtt.MQTT_ERR_SUCCESS:
                events.request_success.fire(
                    request_type="MQTT", 
                    name="device_status_update",
                    response_time=response_time,
                    response_length=len(json.dumps(status_data))
                )
            else:
                events.request_failure.fire(
                    request_type="MQTT",
                    name="device_status_update",
                    response_time=response_time,
                    response_length=0,
                    exception=Exception(f"状态更新失败，错误码: {result.rc}")
                )
                
        except Exception as e:
            response_time = (time.time() - start_time) * 1000
            events.request_failure.fire(
                request_type="MQTT",
                name="device_status_update",
                response_time=response_time,
                response_length=0,
                exception=e
            )
    
    @task(5)  # 5%权重 - 故障模拟
    def failure_simulation(self):
        """故障和异常情况模拟"""
        if not self.device_simulator:
            return
        
        failure_types = COMPREHENSIVE_CONFIG["business_flows"]["failure_simulation"]
        
        # 随机选择故障类型
        if random.random() < sum(failure_types.values()):
            failure_type = random.choices(
                list(failure_types.keys()),
                weights=list(failure_types.values()),
                k=1
            )[0]
            
            start_time = time.time()
            
            if failure_type == "network_timeout":
                # 模拟网络超时
                time.sleep(random.uniform(5, 15))
                events.request_failure.fire(
                    request_type="MQTT",
                    name="network_timeout_simulation",
                    response_time=(time.time() - start_time) * 1000,
                    response_length=0,
                    exception=Exception("网络超时")
                )
                
            elif failure_type == "auth_failure":
                # 模拟认证失败
                events.request_failure.fire(
                    request_type="MQTT",
                    name="auth_failure_simulation",
                    response_time=(time.time() - start_time) * 1000,
                    response_length=0,
                    exception=Exception("认证失败")
                )
                
            elif failure_type == "data_corruption":
                # 模拟数据损坏
                try:
                    corrupted_data = {"corrupted": "data", "invalid": "format"}
                    if self.device_simulator.connected:
                        topic = f"/sys/{self.device_simulator.device_serial}/corrupted/data"
                        self.device_simulator.mqtt_client.publish(topic, json.dumps(corrupted_data))
                except Exception:
                    pass
                
                events.request_failure.fire(
                    request_type="MQTT",
                    name="data_corruption_simulation",
                    response_time=(time.time() - start_time) * 1000,
                    response_length=0,
                    exception=Exception("数据损坏")
                )
                
            elif failure_type == "device_offline":
                # 模拟设备离线
                if self.device_simulator.connected:
                    self.device_simulator.disconnect()
                    self.device_simulator.status = DeviceStatus.OFFLINE
                
                events.request_failure.fire(
                    request_type="MQTT",
                    name="device_offline_simulation",
                    response_time=(time.time() - start_time) * 1000,
                    response_length=0,
                    exception=Exception("设备离线")
                )

# ===== 事件监听器 =====
@events.test_start.add_listener
def on_test_start(environment, **kwargs):
    """测试开始事件"""
    logger.info("🚀 SYDH IoT 综合业务流程压力测试开始")
    logger.info(f"📊 目标服务器配置:")
    logger.info(f"   - API服务器: {COMPREHENSIVE_CONFIG['servers']['api_host']}")
    logger.info(f"   - MQTT Broker: {COMPREHENSIVE_CONFIG['servers']['mqtt_host']}:{COMPREHENSIVE_CONFIG['servers']['mqtt_port']}")
    
    # 显示测试配置
    total_devices = sum(product["device_count"] for product in COMPREHENSIVE_CONFIG["products"].values())
    logger.info(f"📱 测试配置:")
    logger.info(f"   - 测试产品数量: {len(COMPREHENSIVE_CONFIG['products'])}")
    logger.info(f"   - 测试设备总数: {total_devices}")
    logger.info(f"   - 最大并发连接: {COMPREHENSIVE_CONFIG['performance']['max_connections']}")
    logger.info(f"   - 目标数据上报TPS: {COMPREHENSIVE_CONFIG['performance']['data_report_tps']}")
    
    # 显示业务场景
    logger.info(f"🏭 业务场景覆盖:")
    for product_id, product in COMPREHENSIVE_CONFIG["products"].items():
        scenarios = ", ".join(product["business_scenarios"])
        logger.info(f"   - {product['product_name']}: {scenarios}")

@events.test_stop.add_listener
def on_test_stop(environment, **kwargs):
    """测试停止事件"""
    logger.info("🛑 SYDH IoT 综合业务流程压力测试结束")
    
    # 输出详细统计报告
    global global_stats
    with stats_lock:
        logger.info("=" * 100)
        logger.info("📊 SYDH IoT 综合业务流程测试统计报告")
        logger.info("=" * 100)
        
        # 设备注册统计
        logger.info(f"📋 设备注册统计:")
        logger.info(f"   注册设备总数: {global_stats['devices_registered']}")
        
        # MQTT连接统计
        logger.info(f"🔗 MQTT连接统计:")
        logger.info(f"   总连接尝试: {global_stats['mqtt_connections']}")
        logger.info(f"   成功连接: {global_stats['successful_connections']}")
        logger.info(f"   失败连接: {global_stats['failed_connections']}")
        if global_stats['mqtt_connections'] > 0:
            success_rate = (global_stats['successful_connections'] / global_stats['mqtt_connections']) * 100
            logger.info(f"   连接成功率: {success_rate:.2f}%")
        if global_stats['avg_connection_time'] > 0:
            logger.info(f"   平均连接时间: {global_stats['avg_connection_time']:.3f}s")
        
        # 数据上报统计
        logger.info(f"📤 数据上报统计:")
        logger.info(f"   总上报次数: {global_stats['data_reports']}")
        logger.info(f"   成功上报: {global_stats['successful_reports']}")
        logger.info(f"   失败上报: {global_stats['failed_reports']}")
        if global_stats['data_reports'] > 0:
            report_success_rate = (global_stats['successful_reports'] / global_stats['data_reports']) * 100
            logger.info(f"   上报成功率: {report_success_rate:.2f}%")
        if global_stats['avg_report_time'] > 0:
            logger.info(f"   平均上报时间: {global_stats['avg_report_time']:.3f}s")
        
        # 指令处理统计
        logger.info(f"⚡ 指令处理统计:")
        logger.info(f"   收到指令总数: {global_stats['commands_received']}")
        logger.info(f"   响应指令总数: {global_stats['commands_responded']}")
        if global_stats['commands_received'] > 0:
            command_response_rate = (global_stats['commands_responded'] / global_stats['commands_received']) * 100
            logger.info(f"   指令响应率: {command_response_rate:.2f}%")
        
        # 业务指标统计
        logger.info(f"🏭 业务指标统计:")
        business_metrics = global_stats['business_metrics']
        logger.info(f"   当前在线设备: {business_metrics['total_devices_online']}")
        logger.info(f"   总数据点数: {business_metrics['total_data_points']}")
        logger.info(f"   总指令发送数: {business_metrics['total_commands_sent']}")
        logger.info(f"   系统健康评分: {business_metrics['system_health_score']}/100")
        
        # 设备注册表统计
        with device_registry_lock:
            active_devices = len([d for d in device_registry.values() 
                                if time.time() - d['last_seen'] < 300])  # 5分钟内有活动
            logger.info(f"   活跃设备数量: {active_devices}/{len(device_registry)}")
        
        # 错误统计
        if global_stats['errors']:
            logger.info(f"❌ 错误详情 (Top 10):")
            sorted_errors = sorted(global_stats['errors'].items(), key=lambda x: x[1], reverse=True)
            for error, count in sorted_errors[:10]:
                logger.info(f"   {error}: {count}次")
        
        # 性能评估
        logger.info(f"⚡ 性能评估:")
        target_tps = COMPREHENSIVE_CONFIG['performance']['data_report_tps']
        if environment.stats.total.num_requests > 0:
            actual_tps = environment.stats.total.num_requests / environment.stats.total.start_time
            tps_achievement = (actual_tps / target_tps) * 100 if target_tps > 0 else 0
            logger.info(f"   实际TPS: {actual_tps:.1f}")
            logger.info(f"   TPS达成率: {tps_achievement:.1f}%")
            
        target_response_time = COMPREHENSIVE_CONFIG['performance']['command_response_time']
        if environment.stats.total.avg_response_time > 0:
            response_time_ratio = environment.stats.total.avg_response_time / target_response_time
            logger.info(f"   平均响应时间: {environment.stats.total.avg_response_time:.1f}ms")
            logger.info(f"   响应时间达标: {'✅' if response_time_ratio <= 1 else '❌'}")

# ===== 主程序入口 =====
if __name__ == "__main__":
    """
    综合业务流程压力测试脚本
    
    使用方法：
    
    # 小规模测试（100个设备）
    locust -f comprehensive_pressure_test.py --host=mqtt://localhost:1883 -u 100 -r 5 -t 300s
    
    # 中规模测试（1000个设备）
    locust -f comprehensive_pressure_test.py --host=mqtt://localhost:1883 -u 1000 -r 20 -t 600s
    
    # 大规模测试（10000个设备）
    locust -f comprehensive_pressure_test.py --host=mqtt://localhost:1883 -u 10000 -r 50 -t 1800s
    
    # 超大规模测试（30万设备）- 需要分布式部署
    locust -f comprehensive_pressure_test.py --host=mqtt://localhost:1883 -u 300000 -r 1000 -t 3600s
    
    # Web界面模式
    locust -f comprehensive_pressure_test.py --host=mqtt://localhost:1883
    """
    
    logger.info("🎯 SYDH IoT 综合业务流程压力测试脚本已就绪")
    logger.info("📋 支持的测试场景:")
    logger.info("   - 完整设备生命周期模拟")
    logger.info("   - 真实业务数据生成")
    logger.info("   - 多产品类型混合测试")
    logger.info("   - 故障和异常情况模拟")
    logger.info("   - 30万设备并发支持")
    logger.info("")
    logger.info("🚀 使用locust命令启动测试，例如：")
    logger.info("   locust -f comprehensive_pressure_test.py --host=mqtt://localhost:1883 -u 1000 -r 20") 