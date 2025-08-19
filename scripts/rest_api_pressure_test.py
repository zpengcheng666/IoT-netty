#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
SYDH IoT Platform - REST API业务流程压力测试
专门测试设备管理、数据上报、指令下发等核心REST API接口
基于数据库真实测试设备数据
"""

import json
import random
import time
import hashlib
from datetime import datetime, timedelta
from typing import Dict, List, Optional
from locust import HttpUser, task, between, events
import logging

# 配置日志
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# ===== 基于数据库的真实测试配置 =====
REST_API_CONFIG = {
    # 认证配置
    "auth": {
        "username": "admin",
        "password": "admin123",
        "token_refresh_threshold": 300,  # 5分钟前刷新token
        "max_retry": 3,
        # 预设有效token（如果提供，将优先使用）
        "preset_token": "eyJhbGciOiJIUzUxMiJ9.eyJsb2dpbl91c2VyX2tleSI6IjkwYjdiNGNkLTBlOGUtNDVlOS04ODE2LTJkMTJkMDNhMTNhNyJ9.4Fx7ZADlLzrt7qh8dP87dVMBUBkRqyfpE2NMcdVZlkA4yiYiUfuOrJMK3ibCjwaxU3-NnPY4vsu409AebAszVg",
        "preset_token_expires": None  # None表示不过期，或设置具体过期时间戳
    },
    
    # 基于init_test_data.sql的真实产品配置
    "products": {
        1: {
            "product_id": 1,
            "product_name": "测试传感器产品",
            "protocol_code": "MQTT",
            "category_id": 101,
            "category_name": "智能传感器",
            "account": "sensor_test",
            "auth_password": "P_sensor_123456",
            "secret": "K_sensor_secret_001",
            "device_count": 100,
            "device_prefix": "TEST_SENSOR",
            "properties": {
                "temperature": {"type": "decimal", "min": -50, "max": 100, "unit": "℃"},
                "humidity": {"type": "decimal", "min": 0, "max": 100, "unit": "%"},
                "pressure": {"type": "decimal", "min": 800, "max": 1200, "unit": "hPa"}
            },
            "services": ["restart"]
        },
        
        2: {
            "product_id": 2,
            "product_name": "测试摄像头产品", 
            "protocol_code": "MQTT",
            "category_id": 102,
            "category_name": "智能摄像头",
            "account": "camera_test",
            "auth_password": "P_camera_123456",
            "secret": "K_camera_secret_002",
            "device_count": 30,
            "device_prefix": "TEST_CAMERA",
            "properties": {
                "recording": {"type": "bool"},
                "resolution": {"type": "enum", "values": ["720P", "1080P", "4K"]},
                "storage_used": {"type": "decimal", "min": 0, "max": 100, "unit": "%"}
            },
            "services": ["take_photo"]
        },
        
        3: {
            "product_id": 3,
            "product_name": "测试PLC产品",
            "protocol_code": "MQTT", 
            "category_id": 103,
            "category_name": "工业控制器",
            "account": "plc_test",
            "auth_password": "P_plc_123456",
            "secret": "K_plc_secret_003",
            "device_count": 15,
            "device_prefix": "TEST_PLC",
            "properties": {
                "production_count": {"type": "integer", "min": 0, "max": 999999, "unit": "个"},
                "machine_pressure": {"type": "decimal", "min": 0, "max": 10, "unit": "MPa"},
                "machine_temperature": {"type": "decimal", "min": 0, "max": 150, "unit": "℃"}
            },
            "services": ["set_target"]
        },
        
        4: {
            "product_id": 4,
            "product_name": "测试网关产品",
            "protocol_code": "MQTT",
            "category_id": 104, 
            "category_name": "物联网网关",
            "account": "gateway_test",
            "auth_password": "P_gateway_123456",
            "secret": "K_gateway_secret_004",
            "device_count": 10,
            "device_prefix": "TEST_GATEWAY",
            "properties": {
                "connected_devices": {"type": "integer", "min": 0, "max": 100, "unit": "个"},
                "cpu_usage": {"type": "decimal", "min": 0, "max": 100, "unit": "%"},
                "memory_usage": {"type": "decimal", "min": 0, "max": 100, "unit": "%"}
            },
            "services": ["restart_service"]
        },
        
        5: {
            "product_id": 5,
            "product_name": "测试电表产品",
            "protocol_code": "MQTT",
            "category_id": 105,
            "category_name": "智能电表", 
            "account": "meter_test",
            "auth_password": "P_meter_123456",
            "secret": "K_meter_secret_005",
            "device_count": 20,
            "device_prefix": "TEST_METER",
            "properties": {
                "current_reading": {"type": "decimal", "min": 0, "max": 999999, "unit": "kWh"},
                "voltage": {"type": "decimal", "min": 180, "max": 260, "unit": "V"},
                "current": {"type": "decimal", "min": 0, "max": 100, "unit": "A"}
            },
            "services": ["read_meter"]
        }
    },
    
    # API端点配置
    "endpoints": {
        "login": "/login",
        "device_list": "/iot/device/list", 
        "device_detail": "/iot/device/{device_id}",
        "device_add": "/iot/device",
        "device_status": "/iot/device/runningStatus",
        "device_reset": "/iot/device/reset/{serial_number}",
        "device_by_serial": "/iot/device/getDeviceBySerialNumber/{serial_number}",
        "device_statistic": "/iot/device/statistic",
        "runtime_invoke": "/iot/runtime/service/invoke",
        "runtime_reply": "/iot/runtime/service/invokeReply", 
        "message_post": "/iot/message/post",
        "message_encode": "/iot/message/encode",
        "message_decode": "/iot/message/decode",
        "product_list": "/iot/product/list",
        "category_list": "/iot/category/list",
        "health_check": "/actuator/health"
    },
    
    # 请求配置
    "request": {
        "timeout": 30,
        "connect_timeout": 15,
        "headers": {
            "Content-Type": "application/json",
            "User-Agent": "SYDH-IoT-API-Test/3.0",
            "Accept": "application/json"
        }
    }
}

# ===== 真实测试设备数据 =====
TEST_DEVICES = {
    # 传感器设备 (100个)
    1: [f"TEST_SENSOR{str(i).zfill(6)}" for i in range(1, 101)],
    # 摄像头设备 (30个)  
    2: [f"TEST_CAMERA{str(i).zfill(6)}" for i in range(1, 31)],
    # PLC设备 (15个)
    3: [f"TEST_PLC{str(i).zfill(6)}" for i in range(1, 16)],
    # 网关设备 (10个)
    4: [f"TEST_GATEWAY{str(i).zfill(6)}" for i in range(1, 11)],
    # 电表设备 (20个)
    5: [f"TEST_METER{str(i).zfill(6)}" for i in range(1, 21)]
}

# ===== 全局状态管理 =====
global_state = {
    "shared_token": None,
    "token_expires_at": 0,
    "login_attempts": 0,
    "successful_logins": 0,
    "real_devices_cache": {},
    "products_cache": {},
    "api_errors": {},
    "health_status": "unknown"
}

# ===== 统计信息 =====
api_stats = {
    "total_requests": 0,
    "successful_requests": 0,
    "failed_requests": 0,
    "auth_failures": 0,
    "devices_tested": set(),
    "products_tested": set(),
    "connection_failures": 0,
    "business_logic_failures": 0,
    "avg_response_time": 0
}

# ===== 数据生成函数 =====
def get_real_device_serial(product_id: int) -> str:
    """获取真实的设备序列号"""
    devices = TEST_DEVICES.get(product_id, [])
    if devices:
        return random.choice(devices)
    return f"TEST_DEVICE{random.randint(100000, 999999)}"

def generate_device_property_data(product_id: int) -> Dict:
    """根据产品类型生成属性数据"""
    product = REST_API_CONFIG["products"][product_id]
    properties = product["properties"]
    
    data = {}
    for prop_name, prop_config in properties.items():
        if prop_config["type"] == "decimal":
            min_val = prop_config.get("min", 0)
            max_val = prop_config.get("max", 100)
            data[prop_name] = round(random.uniform(min_val, max_val), 2)
        elif prop_config["type"] == "integer":
            min_val = prop_config.get("min", 0)
            max_val = prop_config.get("max", 1000)
            data[prop_name] = random.randint(min_val, max_val)
        elif prop_config["type"] == "bool":
            data[prop_name] = random.choice([True, False])
        elif prop_config["type"] == "enum":
            data[prop_name] = random.choice(prop_config["values"])
    
    return data

def generate_service_invoke_data(product_id: int, device_serial: str) -> Dict:
    """生成服务调用数据"""
    product = REST_API_CONFIG["products"][product_id]
    service_name = random.choice(product["services"])
    
    # 生成服务参数
    service_params = {}
    if service_name == "restart":
        service_params = {"delay": random.randint(0, 60)}
    elif service_name == "take_photo":
        service_params = {
            "quality": random.choice(["720P", "1080P", "4K"]),
            "duration": random.randint(1, 10)
        }
    elif service_name == "set_target":
        service_params = {"target": random.randint(100, 5000)}
    elif service_name == "restart_service":
        service_params = {"service": random.choice(["data_collection", "monitoring", "all"])}
    elif service_name == "read_meter":
        service_params = {"type": random.choice(["current_month", "last_month", "current_year"])}
    
    return {
        "productId": product_id,
        "deviceNumber": device_serial,
        "service": "property",
        "method": service_name,
        "params": service_params
    }

def generate_runtime_invoke_data(product_id: int, device_serial: str) -> Dict:
    """生成运行时数据上报格式"""
    property_data = generate_device_property_data(product_id)
    
    return {
        "productId": product_id,
        "deviceNumber": device_serial,
        "service": "property",
        "method": "post",
        "data": {
            **property_data,
            "timestamp": datetime.now().isoformat(),
            "deviceStatus": "online",
            "signalStrength": random.randint(-90, -30),
            "batteryLevel": random.randint(20, 100) if product_id in [1, 5] else None
        }
    }

def generate_message_post_data(device_serial: str) -> Dict:
    """生成消息发布数据"""
    return {
        "deviceNumber": device_serial,
        "type": random.choice(["info", "warning", "error", "debug"]),
        "content": {
            "level": random.choice(["INFO", "WARN", "ERROR"]),
            "message": f"Device message {random.randint(1, 1000)} - {datetime.now().strftime('%H:%M:%S')}",
            "timestamp": datetime.now().isoformat(),
            "module": random.choice(["sensor", "communication", "power", "system"]),
            "code": random.randint(1000, 9999)
        }
    }

def generate_device_add_data(product_id: int) -> Dict:
    """生成设备添加数据"""
    product = REST_API_CONFIG["products"][product_id]
    device_index = random.randint(1000, 9999)
    
    return {
        "deviceName": f"压测{product['category_name']}设备{device_index}",
        "productId": product_id,
        "productName": product["product_name"],
        "serialNumber": f"{product['device_prefix']}{device_index}",
        "firmwareVersion": f"{random.randint(1, 3)}.{random.randint(0, 9)}.{random.randint(0, 9)}",
        "status": 3,  # 在线状态
        "rssi": random.randint(-90, -30),
        "isShadow": 1,
        "locationWay": 1,
        "networkAddress": "压力测试环境",
        "networkIp": f"192.168.{random.randint(1, 255)}.{random.randint(1, 255)}",
        "remark": "压力测试设备，自动创建"
    }

# ===== Locust用户类 =====
class RestAPIUser(HttpUser):
    """REST API压力测试用户"""
    
    wait_time = between(1, 3)
    
    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        self.token = None
        self.device_cache = {}
        
    def on_start(self):
        """用户启动时初始化"""
        logger.info("启动REST API测试用户")
        
        # 执行登录
        if not self.perform_login():
            logger.error("登录失败，无法继续测试")
            return
        
        # 缓存系统数据
        self.cache_system_data()
        
        logger.info("REST API用户初始化完成")
    
    def _verify_token(self, token: str) -> bool:
        """验证token是否有效"""
        try:
            test_headers = REST_API_CONFIG["request"]["headers"].copy()
            test_headers["Authorization"] = f"Bearer {token}"
            
            # 使用健康检查接口验证token
            with self.client.get(
                REST_API_CONFIG["endpoints"]["health_check"],
                headers=test_headers,
                timeout=10,
                catch_response=True,
                name="token_verification"
            ) as response:
                if response.status_code == 200:
                    logger.info("Token验证成功")
                    response.success()
                    return True
                elif response.status_code == 401:
                    logger.warning("Token已失效")
                    response.failure("Token验证失败: 401 Unauthorized")
                    return False
                else:
                    # 如果健康检查接口不可用，尝试用产品列表接口验证
                    logger.info("健康检查接口不可用，尝试使用产品列表接口验证token")
                    return self._verify_token_with_product_list(token)
                    
        except Exception as e:
            logger.warning(f"Token验证异常: {str(e)}，尝试备用验证方法")
            return self._verify_token_with_product_list(token)
    
    def _verify_token_with_product_list(self, token: str) -> bool:
        """使用产品列表接口验证token"""
        try:
            test_headers = REST_API_CONFIG["request"]["headers"].copy()
            test_headers["Authorization"] = f"Bearer {token}"
            
            with self.client.get(
                REST_API_CONFIG["endpoints"]["product_list"],
                headers=test_headers,
                timeout=10,
                catch_response=True,
                name="token_verification_backup"
            ) as response:
                if response.status_code == 200:
                    try:
                        result = response.json()
                        if result.get("code") == 200:
                            logger.info("Token验证成功（通过产品列表接口）")
                            response.success()
                            return True
                        else:
                            logger.warning(f"Token验证失败: {result.get('msg', '未知错误')}")
                            response.failure(f"Token验证失败: {result.get('msg')}")
                            return False
                    except json.JSONDecodeError:
                        logger.warning("Token验证响应格式错误")
                        response.failure("Token验证响应格式错误")
                        return False
                else:
                    logger.warning(f"Token验证HTTP失败: {response.status_code}")
                    response.failure(f"Token验证HTTP失败: {response.status_code}")
                    return False
                    
        except Exception as e:
            logger.error(f"Token验证异常: {str(e)}")
            return False
    
    def perform_login(self) -> bool:
        """执行用户登录"""
        global global_state, api_stats
        
        # 1. 优先尝试使用预设token
        preset_token = REST_API_CONFIG["auth"].get("preset_token")
        if preset_token:
            logger.info("尝试使用预设token进行认证")
            if self._verify_token(preset_token):
                self.token = preset_token
                global_state["shared_token"] = preset_token
                global_state["token_expires_at"] = time.time() + 7200  # 2小时过期
                global_state["successful_logins"] += 1
                logger.info("预设token验证成功")
                return True
            else:
                logger.warning("预设token验证失败，尝试动态登录")
        
        # 2. 尝试使用共享token
        if (global_state["shared_token"] and 
            time.time() < global_state["token_expires_at"] - REST_API_CONFIG["auth"]["token_refresh_threshold"]):
            self.token = global_state["shared_token"]
            logger.info("使用共享token")
            return True
        
        # 3. 执行动态登录
        logger.info("执行动态登录")
        login_data = {
            "username": REST_API_CONFIG["auth"]["username"],
            "password": REST_API_CONFIG["auth"]["password"]
        }
        
        try:
            global_state["login_attempts"] += 1
            
            logger.info(f"尝试登录: {REST_API_CONFIG['endpoints']['login']}")
            logger.info(f"登录参数: {{'username': '{login_data['username']}', 'password': '***'}}")
            
            with self.client.post(
                REST_API_CONFIG["endpoints"]["login"],
                json=login_data,
                headers=REST_API_CONFIG["request"]["headers"],
                timeout=REST_API_CONFIG["request"]["timeout"],
                catch_response=True,
                name="login"
            ) as response:
                api_stats["total_requests"] += 1
                
                logger.info(f"登录响应状态码: {response.status_code}")
                if hasattr(response, 'text'):
                    logger.info(f"登录响应内容: {response.text[:500]}...")  # 只显示前500字符
                
                if response.status_code == 200:
                    try:
                        result = response.json()
                        logger.info(f"登录响应解析: code={result.get('code')}, msg={result.get('msg')}")
                        
                        if result.get("code") == 200:
                            token = result.get("token")
                            if token:
                                self.token = token
                                global_state["shared_token"] = self.token
                                global_state["token_expires_at"] = time.time() + 7200  # 2小时过期
                                global_state["successful_logins"] += 1
                                api_stats["successful_requests"] += 1
                                response.success()
                                logger.info("动态登录成功")
                                return True
                            else:
                                api_stats["failed_requests"] += 1
                                api_stats["auth_failures"] += 1
                                response.failure("登录成功但未返回token")
                                logger.error("登录成功但未返回token")
                                return False
                        else:
                            api_stats["failed_requests"] += 1
                            api_stats["auth_failures"] += 1
                            error_msg = result.get('msg', '未知错误')
                            response.failure(f"登录业务失败: {error_msg}")
                            logger.error(f"登录业务失败: {error_msg}")
                            return False
                    except json.JSONDecodeError as e:
                        api_stats["failed_requests"] += 1
                        api_stats["auth_failures"] += 1
                        response.failure("登录响应格式错误")
                        logger.error(f"登录响应JSON解析失败: {e}")
                        return False
                else:
                    api_stats["failed_requests"] += 1
                    api_stats["auth_failures"] += 1
                    response.failure(f"登录HTTP失败: {response.status_code}")
                    logger.error(f"登录HTTP失败: {response.status_code}")
                    return False
                    
        except Exception as e:
            api_stats["total_requests"] += 1
            api_stats["failed_requests"] += 1
            api_stats["connection_failures"] += 1
            logger.error(f"登录异常: {e}")
            return False
    
    def cache_system_data(self):
        """缓存系统数据"""
        try:
            # 获取产品列表
            with self.client.get(
                REST_API_CONFIG["endpoints"]["product_list"],
                headers=self._get_auth_headers(),
                timeout=REST_API_CONFIG["request"]["timeout"],
                catch_response=True,
                name="cache_product_list"
            ) as response:
                if response.status_code == 200:
                    result = response.json()
                    if result.get("code") == 200:
                        global_state["products_cache"] = result.get("data", {})
                        response.success()
                
            # 获取部分设备列表用于后续测试
            with self.client.get(
                REST_API_CONFIG["endpoints"]["device_list"] + "?pageNum=1&pageSize=50",
                headers=self._get_auth_headers(),
                timeout=REST_API_CONFIG["request"]["timeout"],
                catch_response=True,
                name="cache_device_list"
            ) as response:
                if response.status_code == 200:
                    result = response.json()
                    if result.get("code") == 200:
                        rows = result.get("rows", [])
                        for device in rows:
                            device_id = device.get("deviceId")
                            if device_id:
                                self.device_cache[device_id] = device
                        response.success()
                        
        except Exception as e:
            logger.warning(f"缓存系统数据异常: {e}")
    
    def _get_auth_headers(self) -> Dict:
        """获取包含认证信息的请求头"""
        headers = REST_API_CONFIG["request"]["headers"].copy()
        if self.token:
            headers["Authorization"] = f"Bearer {self.token}"
        return headers
    
    def _record_error(self, error_type: str, detail: str):
        """记录错误信息"""
        global api_stats
        error_key = f"{error_type}: {detail}"
        api_stats["api_errors"][error_key] = api_stats["api_errors"].get(error_key, 0) + 1
    
    @task(40)  # 40%权重 - 数据上报（最高频业务）
    def post_device_runtime_data(self):
        """设备运行时数据上报 - 核心业务"""
        if not self.token:
            return
        
        global api_stats
        
        # 选择真实设备
        product_id = random.choice(list(REST_API_CONFIG["products"].keys()))
        device_serial = get_real_device_serial(product_id)
        api_stats["devices_tested"].add(device_serial)
        api_stats["products_tested"].add(product_id)
        
        # 生成运行时数据
        runtime_data = generate_runtime_invoke_data(product_id, device_serial)
        
        try:
            with self.client.post(
                REST_API_CONFIG["endpoints"]["runtime_invoke"],
                json=runtime_data,
                headers=self._get_auth_headers(),
                timeout=REST_API_CONFIG["request"]["timeout"],
                catch_response=True,
                name="runtime_invoke"
            ) as response:
                api_stats["total_requests"] += 1
                
                if response.status_code == 200:
                    try:
                        result = response.json()
                        if result.get("code") == 200:
                            api_stats["successful_requests"] += 1
                            response.success()
                        else:
                            api_stats["failed_requests"] += 1
                            api_stats["business_logic_failures"] += 1
                            error_msg = result.get("msg", "数据上报失败")
                            response.failure(f"数据上报业务失败: {error_msg}")
                            self._record_error("数据上报业务失败", error_msg)
                    except json.JSONDecodeError:
                        api_stats["failed_requests"] += 1
                        api_stats["business_logic_failures"] += 1
                        response.failure("数据上报响应格式错误")
                        self._record_error("数据上报响应格式错误", "JSON解析失败")
                else:
                    api_stats["failed_requests"] += 1
                    api_stats["connection_failures"] += 1
                    response.failure(f"数据上报HTTP失败: {response.status_code}")
                    self._record_error("数据上报HTTP失败", str(response.status_code))
                    
        except Exception as e:
            api_stats["total_requests"] += 1
            api_stats["failed_requests"] += 1
            api_stats["connection_failures"] += 1
            logger.error(f"数据上报异常: {str(e)}")
            self._record_error("数据上报异常", str(e))
    
    @task(25)  # 25%权重 - 设备状态查询（高频查询）
    def query_device_status(self):
        """查询设备运行状态"""
        if not self.token:
            return
        
        global api_stats
        
        # 选择真实设备
        if self.device_cache:
            device_info = random.choice(list(self.device_cache.values()))
            device_id = device_info.get("deviceId")
        else:
            device_id = random.randint(1, 1000)  # 模拟设备ID
        
        try:
            with self.client.get(
                REST_API_CONFIG["endpoints"]["device_status"] + f"?deviceId={device_id}",
                headers=self._get_auth_headers(),
                timeout=REST_API_CONFIG["request"]["timeout"],
                catch_response=True,
                name="device_status_query"
            ) as response:
                api_stats["total_requests"] += 1
                
                if response.status_code == 200:
                    try:
                        result = response.json()
                        if result.get("code") == 200:
                            api_stats["successful_requests"] += 1
                            response.success()
                        else:
                            api_stats["failed_requests"] += 1
                            api_stats["business_logic_failures"] += 1
                            response.failure(f"设备状态查询失败: {result.get('msg', '未知错误')}")
                    except json.JSONDecodeError:
                        api_stats["failed_requests"] += 1
                        api_stats["business_logic_failures"] += 1
                        response.failure("设备状态查询响应格式错误")
                else:
                    api_stats["failed_requests"] += 1
                    api_stats["connection_failures"] += 1
                    response.failure(f"设备状态查询HTTP失败: {response.status_code}")
                    
        except Exception as e:
            api_stats["total_requests"] += 1
            api_stats["failed_requests"] += 1
            api_stats["connection_failures"] += 1
            logger.error(f"设备状态查询异常: {str(e)}")
    
    @task(20)  # 20%权重 - 服务调用回复（指令下发）
    def invoke_device_service_reply(self):
        """设备服务调用回复"""
        if not self.token:
            return
        
        global api_stats
        
        # 选择真实设备
        product_id = random.choice(list(REST_API_CONFIG["products"].keys()))
        device_serial = get_real_device_serial(product_id)
        
        # 生成服务调用数据
        service_data = generate_service_invoke_data(product_id, device_serial)
        
        try:
            with self.client.post(
                REST_API_CONFIG["endpoints"]["runtime_reply"],
                json=service_data,
                headers=self._get_auth_headers(),
                timeout=REST_API_CONFIG["request"]["timeout"],
                catch_response=True,
                name="service_invoke_reply"
            ) as response:
                api_stats["total_requests"] += 1
                
                if response.status_code == 200:
                    try:
                        result = response.json()
                        if result.get("code") == 200:
                            api_stats["successful_requests"] += 1
                            response.success()
                        else:
                            api_stats["failed_requests"] += 1
                            api_stats["business_logic_failures"] += 1
                            response.failure(f"服务调用失败: {result.get('msg', '未知错误')}")
                    except json.JSONDecodeError:
                        api_stats["failed_requests"] += 1
                        api_stats["business_logic_failures"] += 1
                        response.failure("服务调用响应格式错误")
                else:
                    api_stats["failed_requests"] += 1
                    api_stats["connection_failures"] += 1
                    response.failure(f"服务调用HTTP失败: {response.status_code}")
                    
        except Exception as e:
            api_stats["total_requests"] += 1
            api_stats["failed_requests"] += 1
            api_stats["connection_failures"] += 1
            logger.error(f"服务调用异常: {str(e)}")
    
    @task(10)  # 10%权重 - 设备列表查询
    def query_device_list(self):
        """查询设备列表"""
        if not self.token:
            return
        
        global api_stats
        
        # 随机分页参数
        page_num = random.randint(1, 5)
        page_size = random.choice([10, 20, 50])
        product_id = random.choice([None] + list(REST_API_CONFIG["products"].keys()))
        
        params = {
            "pageNum": page_num,
            "pageSize": page_size
        }
        if product_id:
            params["productId"] = product_id
        
        query_string = "&".join([f"{k}={v}" for k, v in params.items()])
        
        try:
            with self.client.get(
                REST_API_CONFIG["endpoints"]["device_list"] + f"?{query_string}",
                headers=self._get_auth_headers(),
                timeout=REST_API_CONFIG["request"]["timeout"],
                catch_response=True,
                name="device_list_query"
            ) as response:
                api_stats["total_requests"] += 1
                
                if response.status_code == 200:
                    try:
                        result = response.json()
                        if result.get("code") == 200:
                            api_stats["successful_requests"] += 1
                            response.success()
                        else:
                            api_stats["failed_requests"] += 1
                            api_stats["business_logic_failures"] += 1
                            response.failure(f"设备列表查询失败: {result.get('msg', '未知错误')}")
                    except json.JSONDecodeError:
                        api_stats["failed_requests"] += 1
                        api_stats["business_logic_failures"] += 1
                        response.failure("设备列表查询响应格式错误")
                else:
                    api_stats["failed_requests"] += 1
                    api_stats["connection_failures"] += 1
                    response.failure(f"设备列表查询HTTP失败: {response.status_code}")
                    
        except Exception as e:
            api_stats["total_requests"] += 1
            api_stats["failed_requests"] += 1
            api_stats["connection_failures"] += 1
            logger.error(f"设备列表查询异常: {str(e)}")
    
    @task(8)  # 8%权重 - 设备详情查询
    def query_device_detail(self):
        """查询设备详情"""
        if not self.token:
            return
        
        global api_stats
        
        # 选择真实设备
        device_serial = get_real_device_serial(random.choice(list(REST_API_CONFIG["products"].keys())))
        
        try:
            with self.client.get(
                REST_API_CONFIG["endpoints"]["device_by_serial"].format(serial_number=device_serial),
                headers=self._get_auth_headers(),
                timeout=REST_API_CONFIG["request"]["timeout"],
                catch_response=True,
                name="device_detail_query"
            ) as response:
                api_stats["total_requests"] += 1
                
                if response.status_code == 200:
                    try:
                        result = response.json()
                        if result.get("code") == 200:
                            api_stats["successful_requests"] += 1
                            response.success()
                        else:
                            api_stats["failed_requests"] += 1
                            api_stats["business_logic_failures"] += 1
                            response.failure(f"设备详情查询失败: {result.get('msg', '未知错误')}")
                    except json.JSONDecodeError:
                        api_stats["failed_requests"] += 1
                        api_stats["business_logic_failures"] += 1
                        response.failure("设备详情查询响应格式错误")
                else:
                    api_stats["failed_requests"] += 1
                    api_stats["connection_failures"] += 1
                    response.failure(f"设备详情查询HTTP失败: {response.status_code}")
                    
        except Exception as e:
            api_stats["total_requests"] += 1
            api_stats["failed_requests"] += 1
            api_stats["connection_failures"] += 1
            logger.error(f"设备详情查询异常: {str(e)}")
    
    @task(5)  # 5%权重 - 消息发布
    def post_device_message(self):
        """设备消息发布"""
        if not self.token:
            return
        
        global api_stats
        
        # 选择真实设备
        device_serial = get_real_device_serial(random.choice(list(REST_API_CONFIG["products"].keys())))
        
        # 生成消息数据
        message_data = generate_message_post_data(device_serial)
        
        try:
            with self.client.post(
                REST_API_CONFIG["endpoints"]["message_post"],
                json=message_data,
                headers=self._get_auth_headers(),
                timeout=REST_API_CONFIG["request"]["timeout"],
                catch_response=True,
                name="message_post"
            ) as response:
                api_stats["total_requests"] += 1
                
                if response.status_code == 200:
                    try:
                        result = response.json()
                        if result.get("code") == 200:
                            api_stats["successful_requests"] += 1
                            response.success()
                        else:
                            api_stats["failed_requests"] += 1
                            api_stats["business_logic_failures"] += 1
                            response.failure(f"消息发布失败: {result.get('msg', '未知错误')}")
                    except json.JSONDecodeError:
                        api_stats["failed_requests"] += 1
                        api_stats["business_logic_failures"] += 1
                        response.failure("消息发布响应格式错误")
                else:
                    api_stats["failed_requests"] += 1
                    api_stats["connection_failures"] += 1
                    response.failure(f"消息发布HTTP失败: {response.status_code}")
                    
        except Exception as e:
            api_stats["total_requests"] += 1
            api_stats["failed_requests"] += 1
            api_stats["connection_failures"] += 1
            logger.error(f"消息发布异常: {str(e)}")
    
    @task(3)  # 3%权重 - 设备统计查询
    def query_device_statistics(self):
        """查询设备统计信息"""
        if not self.token:
            return
        
        global api_stats
        
        try:
            with self.client.get(
                REST_API_CONFIG["endpoints"]["device_statistic"],
                headers=self._get_auth_headers(),
                timeout=REST_API_CONFIG["request"]["timeout"],
                catch_response=True,
                name="device_statistics"
            ) as response:
                api_stats["total_requests"] += 1
                
                if response.status_code == 200:
                    try:
                        result = response.json()
                        if result.get("code") == 200:
                            api_stats["successful_requests"] += 1
                            response.success()
                        else:
                            api_stats["failed_requests"] += 1
                            api_stats["business_logic_failures"] += 1
                            response.failure(f"统计查询失败: {result.get('msg', '未知错误')}")
                    except json.JSONDecodeError:
                        api_stats["failed_requests"] += 1
                        api_stats["business_logic_failures"] += 1
                        response.failure("统计查询响应格式错误")
                else:
                    api_stats["failed_requests"] += 1
                    api_stats["connection_failures"] += 1
                    response.failure(f"统计查询HTTP失败: {response.status_code}")
                    
        except Exception as e:
            api_stats["total_requests"] += 1
            api_stats["failed_requests"] += 1
            api_stats["connection_failures"] += 1
            logger.error(f"统计查询异常: {str(e)}")

# ===== 事件监听器 =====
@events.test_start.add_listener
def on_test_start(environment, **kwargs):
    """测试开始事件"""
    logger.info("🚀 SYDH IoT REST API业务流程压力测试开始")
    logger.info(f"📊 目标服务器: {environment.host}")
    
    # 显示认证配置
    preset_token = REST_API_CONFIG["auth"].get("preset_token")
    if preset_token:
        logger.info(f"🔑 认证方式: 优先使用预设Token")
        logger.info(f"   预设Token: {preset_token[:20]}...{preset_token[-20:]}")
    else:
        logger.info(f"🔑 认证方式: 用户名密码登录")
        logger.info(f"   用户名: {REST_API_CONFIG['auth']['username']}")
    
    # 显示测试配置统计
    total_devices = sum(len(devices) for devices in TEST_DEVICES.values())
    logger.info(f"📱 测试产品数量: {len(REST_API_CONFIG['products'])}")
    logger.info(f"📱 测试设备总数: {total_devices}")
    
    for product_id, product in REST_API_CONFIG["products"].items():
        device_count = len(TEST_DEVICES.get(product_id, []))
        logger.info(f"   - {product['product_name']}: {device_count}个设备")

@events.test_stop.add_listener
def on_test_stop(environment, **kwargs):
    """测试停止事件"""
    logger.info("🛑 SYDH IoT REST API业务流程压力测试结束")
    
    # 输出详细统计信息
    global api_stats
    logger.info("=" * 80)
    logger.info("📊 REST API测试统计报告")
    logger.info("=" * 80)
    
    # 请求统计
    logger.info(f"📡 请求统计:")
    logger.info(f"   总请求次数: {api_stats['total_requests']}")
    logger.info(f"   成功请求: {api_stats['successful_requests']}")
    logger.info(f"   失败请求: {api_stats['failed_requests']}")
    if api_stats['total_requests'] > 0:
        success_rate = (api_stats['successful_requests'] / api_stats['total_requests']) * 100
        logger.info(f"   请求成功率: {success_rate:.2f}%")
    
    # 认证统计
    logger.info(f"🔐 认证统计:")
    logger.info(f"   登录尝试次数: {global_state['login_attempts']}")
    logger.info(f"   登录成功次数: {global_state['successful_logins']}")
    logger.info(f"   认证失败次数: {api_stats['auth_failures']}")
    
    # 业务统计
    logger.info(f"📱 业务统计:")
    logger.info(f"   测试设备数量: {len(api_stats['devices_tested'])}")
    logger.info(f"   测试产品数量: {len(api_stats['products_tested'])}")
    logger.info(f"   连接失败次数: {api_stats['connection_failures']}")
    logger.info(f"   业务逻辑失败次数: {api_stats['business_logic_failures']}")
    
    # 错误详情
    if api_stats['api_errors']:
        logger.info(f"❌ 错误详情 (Top 10):")
        sorted_errors = sorted(api_stats['api_errors'].items(), key=lambda x: x[1], reverse=True)
        for error, count in sorted_errors[:10]:
            logger.info(f"   {error}: {count}次")

# ===== 主程序入口 =====
if __name__ == "__main__":
    """
    直接运行此脚本进行单机测试
    或者使用locust命令行：
    
    # 基础测试（100个用户，10秒内启动）
    locust -f rest_api_pressure_test.py --host=http://localhost:8080 -u 100 -r 10 -t 60s
    
    # 大规模测试（1000个用户，60秒内启动，运行10分钟）
    locust -f rest_api_pressure_test.py --host=http://localhost:8080 -u 1000 -r 10 -t 600s
    
    # Web界面测试
    locust -f rest_api_pressure_test.py --host=http://localhost:8080
    
    # 认证说明：
    # 1. 如果有有效token，请在REST_API_CONFIG["auth"]["preset_token"]中配置
    # 2. 如果token失效，脚本会自动尝试用户名密码登录
    # 3. 如果需要修改登录信息，请更新REST_API_CONFIG["auth"]中的配置
    """
    
    logger.info("REST API压力测试脚本已就绪")
    logger.info("使用locust命令启动测试，例如：")
    logger.info("locust -f rest_api_pressure_test.py --host=http://localhost:8080")
    
    # 显示当前认证配置
    preset_token = REST_API_CONFIG["auth"].get("preset_token")
    if preset_token:
        logger.info(f"🔑 当前配置: 使用预设Token ({preset_token[:20]}...)")
    else:
        logger.info(f"🔑 当前配置: 用户名密码登录 ({REST_API_CONFIG['auth']['username']})")
    
    logger.info("💡 如需修改认证信息，请编辑脚本中的REST_API_CONFIG['auth']配置") 