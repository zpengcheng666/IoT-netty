import json
import random
import time
import os
import hashlib
from datetime import datetime, timedelta
from locust import HttpUser, task, between, events
import logging

# 配置日志
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# ===== 修复后的REST API测试配置 =====
API_CONFIG = {
    # 认证配置
    "auth": {
        "username": os.getenv("IOT_USERNAME", "admin"),
        "password": os.getenv("IOT_PASSWORD", "admin123"),
        "token_refresh_threshold": 300,  # 5分钟前刷新token
        "max_retry": 3
    },
    
    # 真实产品配置（与数据库保持一致）
    "products": {
        1: {"name": "测试传感器产品", "type": "sensor", "auth_account": "sensor_test"},
        2: {"name": "测试摄像头产品", "type": "camera", "auth_account": "camera_test"},
        3: {"name": "测试PLC产品", "type": "plc", "auth_account": "plc_test"},
        4: {"name": "测试网关产品", "type": "gateway", "auth_account": "gateway_test"},
        5: {"name": "测试电表产品", "type": "meter", "auth_account": "meter_test"}
    },
    
    # API端点配置
    "endpoints": {
        "login": "/login",
        "device_list": "/iot/device/list",
        "device_detail": "/iot/device/{device_id}",
        "device_status": "/iot/device/status/{device_id}",
        "product_list": "/iot/product/list",
        "message_post": "/iot/message/post",
        "runtime_invoke": "/iot/runtime/service/invoke",
        "runtime_reply": "/iot/runtime/service/invokeReply",
        "device_log": "/iot/device/log/list",
        "category_list": "/iot/category/list",
        # 新增健康检查端点
        "health_check": "/actuator/health"
    },
    
    # 请求配置
    "request": {
        "timeout": 30,
        "connect_timeout": 15,
        "headers": {
            "Content-Type": "application/json",
            "User-Agent": "SYDH-IoT-LoadTest/3.1",
            "Accept": "application/json"
        }
    }
}

# ===== 真实设备数据池（与数据库和MQTT测试保持一致）=====
REAL_DEVICES = {
    # 传感器设备
    1: [f"TEST_SENSOR{str(i).zfill(6)}" for i in range(1, 101)],
    # 摄像头设备  
    2: [f"TEST_CAMERA{str(i).zfill(6)}" for i in range(1, 51)],
    # PLC设备
    3: [f"TEST_PLC{str(i).zfill(6)}" for i in range(1, 31)],
    # 网关设备
    4: [f"TEST_GATEWAY{str(i).zfill(6)}" for i in range(1, 21)],
    # 电表设备
    5: [f"TEST_METER{str(i).zfill(6)}" for i in range(1, 51)]
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
    "business_logic_failures": 0
}

# ===== 健康检查和连接诊断 =====

def check_server_health(host: str) -> dict:
    """检查服务器健康状态"""
    import requests
    
    health_info = {
        "server_reachable": False,
        "api_responsive": False,
        "estimated_latency": 0,
        "recommendations": []
    }
    
    try:
        # 检查基础连接
        start_time = time.time()
        response = requests.get(
            f"{host}/actuator/health", 
            timeout=10,
            headers={"Accept": "application/json"}
        )
        latency = (time.time() - start_time) * 1000
        
        health_info["server_reachable"] = True
        health_info["estimated_latency"] = round(latency, 2)
        
        if response.status_code == 200:
            health_info["api_responsive"] = True
            global_state["health_status"] = "healthy"
        else:
            health_info["recommendations"].append(f"健康检查返回状态码: {response.status_code}")
            global_state["health_status"] = "degraded"
            
    except requests.exceptions.ConnectTimeout:
        health_info["recommendations"].append("连接超时，请检查服务器地址和端口")
        global_state["health_status"] = "unhealthy"
    except requests.exceptions.ConnectionError:
        health_info["recommendations"].append("无法连接到服务器，请检查网络和服务状态")
        global_state["health_status"] = "unhealthy"
    except Exception as e:
        health_info["recommendations"].append(f"健康检查异常: {str(e)}")
        global_state["health_status"] = "unhealthy"
        
    return health_info

# ===== 真实数据生成函数 =====

def generate_real_api_device_data(product_id: int) -> dict:
    """根据产品类型生成真实的API设备数据"""
    base_data = {
        "timestamp": datetime.now().isoformat(),
        "deviceStatus": "online",
        "signalStrength": random.randint(-90, -30)
    }
    
    if product_id == 1:  # 传感器
        return {
            **base_data,
            "temperature": round(random.uniform(15.0, 35.0), 1),
            "humidity": round(random.uniform(30.0, 80.0), 1),
            "pressure": round(random.uniform(1000.0, 1100.0), 2),
            "airQuality": random.choice(["good", "moderate", "poor"])
        }
    elif product_id == 2:  # 摄像头
        return {
            **base_data,
            "recording": random.choice([True, False]),
            "motionDetected": random.choice([True, False]),
            "resolution": random.choice(["720P", "1080P", "4K"]),
            "storageUsed": round(random.uniform(10.0, 90.0), 1)
        }
    elif product_id == 3:  # PLC
        return {
            **base_data,
            "productionCount": random.randint(100, 10000),
            "machinePressure": round(random.uniform(1.0, 5.0), 2),
            "temperature": round(random.uniform(40.0, 80.0), 1),
            "status": random.choice(["running", "idle", "maintenance"])
        }
    elif product_id == 4:  # 网关
        return {
            **base_data,
            "connectedDevices": random.randint(5, 50),
            "cpuUsage": round(random.uniform(10.0, 80.0), 1),
            "memoryUsage": round(random.uniform(20.0, 90.0), 1),
            "networkStatus": random.choice(["online", "unstable"])
        }
    elif product_id == 5:  # 电表
        return {
            **base_data,
            "currentReading": round(random.uniform(1000.0, 50000.0), 2),
            "voltage": round(random.uniform(220.0, 240.0), 1),
            "current": round(random.uniform(1.0, 50.0), 2),
            "powerFactor": round(random.uniform(0.8, 1.0), 3)
        }
    
    return base_data

def get_real_device_for_api(product_id: int) -> str:
    """获取用于API测试的真实设备序列号"""
    if product_id in REAL_DEVICES and REAL_DEVICES[product_id]:
        device_serial = random.choice(REAL_DEVICES[product_id])
        api_stats["devices_tested"].add(device_serial)
        return device_serial
    else:
        logger.warning(f"产品ID {product_id} 没有可用的测试设备，使用回退设备")
        return f"FALLBACK_API_DEV_{random.randint(1000, 9999)}"

def format_api_message(data: dict, device_serial: str, product_id: int) -> dict:
    """格式化API消息，确保符合系统规范"""
    return {
        "deviceSerialNumber": device_serial,
        "productId": product_id,
        "messageId": f"api_{int(time.time() * 1000)}_{random.randint(1000, 9999)}",
        "timestamp": datetime.now().isoformat(),
        "topicName": "property/post",  # 添加主题名称
        "message": data  # 将数据放在message字段中
    }

# ===== 重试装饰器 =====

def retry_on_failure(max_retries=3, delay=1):
    """API请求重试装饰器"""
    def decorator(func):
        def wrapper(self, *args, **kwargs):
            last_exception = None
            for attempt in range(max_retries):
                try:
                    return func(self, *args, **kwargs)
                except Exception as e:
                    last_exception = e
                    if attempt < max_retries - 1:
                        logger.debug(f"请求失败，第{attempt + 1}次重试: {str(e)}")
                        time.sleep(delay * (attempt + 1))
                        continue
                    break
            raise last_exception
        return wrapper
    return decorator

# ===== Locust事件监听 =====

@events.test_start.add_listener
def on_test_start(environment, **kwargs):
    """测试开始事件"""
    global api_stats, global_state
    
    logger.info("🚀 SYDH IoT REST API压力测试开始 v3.1 - 修复版")
    logger.info(f"📊 目标服务器: {environment.host}")
    logger.info(f"📊 目标用户数: {getattr(environment.parsed_options, 'num_users', 'N/A')}")
    logger.info(f"⚙️  认证账号: {API_CONFIG['auth']['username']}")
    
    # 执行服务器健康检查
    if environment.host:
        logger.info("🔍 正在检查服务器健康状态...")
        health_info = check_server_health(environment.host)
        
        if health_info["api_responsive"]:
            logger.info(f"✅ 服务器健康检查通过 - 延迟: {health_info['estimated_latency']}ms")
        else:
            logger.warning("❌ 服务器健康检查失败")
            for rec in health_info["recommendations"]:
                logger.warning(f"   💡 {rec}")
    
    # 显示真实数据统计
    total_devices = sum(len(devices) for devices in REAL_DEVICES.values())
    total_products = len(API_CONFIG["products"])
    logger.info(f"📱 可用测试设备: {total_devices} 个")
    logger.info(f"🏷️  可用测试产品: {total_products} 个")
    
    for product_id, product_info in API_CONFIG["products"].items():
        device_count = len(REAL_DEVICES.get(product_id, []))
        logger.info(f"   - {product_info['name']}: {device_count} 个设备")

@events.test_stop.add_listener
def on_test_stop(environment, **kwargs):
    """测试结束事件"""
    global api_stats, global_state
    
    logger.info("📈 === REST API修复版测试报告 ===")
    logger.info(f"🔗 认证统计:")
    logger.info(f"   登录尝试: {global_state['login_attempts']}")
    logger.info(f"   成功登录: {global_state['successful_logins']}")
    logger.info(f"   认证失败: {api_stats['auth_failures']}")
    
    logger.info(f"📡 请求统计:")
    logger.info(f"   总请求数: {api_stats['total_requests']}")
    logger.info(f"   成功请求: {api_stats['successful_requests']}")
    logger.info(f"   失败请求: {api_stats['failed_requests']}")
    logger.info(f"   连接失败: {api_stats['connection_failures']}")
    logger.info(f"   业务失败: {api_stats['business_logic_failures']}")
    
    logger.info(f"📱 数据使用:")
    logger.info(f"   测试设备数: {len(api_stats['devices_tested'])}")
    logger.info(f"   测试产品数: {len(api_stats['products_tested'])}")
    
    logger.info(f"🏥 服务状态: {global_state['health_status']}")
    
    if global_state["api_errors"]:
        logger.info(f"❌ 主要错误类型:")
        for error, count in list(global_state["api_errors"].items())[:10]:  # 只显示前10个
            logger.info(f"   {error}: {count} 次")

# ===== 修复后的HTTP用户类 =====

class FixedIoTUser(HttpUser):
    """修复后的IoT平台HTTP用户"""
    
    wait_time = between(2, 5)
    
    def on_start(self):
        """用户开始时的初始化"""
        self.token = None
        self.user_info = None
        self.device_cache = {}
        self.product_cache = {}
        
        # 执行登录
        self.perform_login()
        
        # 缓存产品和设备信息
        if self.token:
            self.cache_system_data()
        else:
            logger.error("登录失败，无法缓存系统数据")
    
    def perform_login(self):
        """执行用户登录 - 修复版"""
        global global_state, api_stats
        
        # 检查是否可以使用共享token
        current_time = time.time()
        if (global_state["shared_token"] and 
            current_time < global_state["token_expires_at"] - API_CONFIG["auth"]["token_refresh_threshold"]):
            self.token = global_state["shared_token"]
            logger.debug("使用共享Token进行认证")
            return
        
        global_state["login_attempts"] += 1
        
        # 修正登录数据格式
        login_data = {
            "username": API_CONFIG["auth"]["username"],
            "password": API_CONFIG["auth"]["password"],
            "captcha": "",  # 测试环境通常不需要验证码
            "uuid": "",
            "rememberMe": False
        }
        
        try:
            with self.client.post(
                API_CONFIG["endpoints"]["login"],
                json=login_data,
                headers=API_CONFIG["request"]["headers"],
                timeout=API_CONFIG["request"]["timeout"],
                catch_response=True
            ) as response:
                api_stats["total_requests"] += 1
                
                if response.status_code == 200:
                    try:
                        result = response.json()
                        if result.get("code") == 200 and "token" in result:
                            self.token = result["token"]
                            self.user_info = result.get("user", {})
                            
                            # 更新共享token
                            global_state["shared_token"] = self.token
                            global_state["token_expires_at"] = current_time + 3600  # 1小时有效期
                            global_state["successful_logins"] += 1
                            
                            api_stats["successful_requests"] += 1
                            response.success()
                            logger.debug(f"✅ 用户登录成功: {API_CONFIG['auth']['username']}")
                        else:
                            api_stats["auth_failures"] += 1
                            api_stats["failed_requests"] += 1
                            api_stats["business_logic_failures"] += 1
                            error_msg = result.get("msg", "登录失败")
                            response.failure(f"登录业务失败: {error_msg}")
                            self._record_error("登录业务失败", error_msg)
                            logger.error(f"登录业务失败: {error_msg}")
                    except json.JSONDecodeError as e:
                        api_stats["failed_requests"] += 1
                        api_stats["business_logic_failures"] += 1
                        response.failure("登录响应格式错误")
                        self._record_error("登录响应格式错误", "JSON解析失败")
                        logger.error(f"登录响应JSON解析失败: {str(e)}")
                else:
                    api_stats["failed_requests"] += 1
                    api_stats["connection_failures"] += 1
                    response.failure(f"登录HTTP失败: {response.status_code}")
                    self._record_error("登录HTTP失败", str(response.status_code))
                    logger.error(f"登录HTTP失败: {response.status_code}")
                    
        except Exception as e:
            api_stats["failed_requests"] += 1
            api_stats["connection_failures"] += 1
            logger.error(f"❌ 登录异常: {str(e)}")
            self._record_error("登录异常", str(e))
    
    def cache_system_data(self):
        """缓存系统数据"""
        if not self.token:
            logger.warning("没有有效Token，无法缓存系统数据")
            return
            
        # 缓存产品列表
        try:
            self.get_product_list()
        except Exception as e:
            logger.error(f"缓存产品列表失败: {str(e)}")
        
        # 缓存部分设备信息
        try:
            self.get_device_list(page_size=10)
        except Exception as e:
            logger.error(f"缓存设备列表失败: {str(e)}")
    
    def _record_error(self, error_type: str, detail: str):
        """记录错误信息"""
        error_key = f"{error_type}: {detail}"
        global_state["api_errors"][error_key] = global_state["api_errors"].get(error_key, 0) + 1
    
    def _get_auth_headers(self) -> dict:
        """获取认证头"""
        headers = API_CONFIG["request"]["headers"].copy()
        if self.token:
            headers["Authorization"] = f"Bearer {self.token}"
        return headers
    
    @retry_on_failure(max_retries=2)
    def get_product_list(self):
        """获取产品列表"""
        global api_stats
        
        params = {
            "pageNum": 1,
            "pageSize": 10,
            "productName": "",
            "status": 2  # 已发布的产品
        }
        
        try:
            with self.client.get(
                API_CONFIG["endpoints"]["product_list"],
                params=params,
                headers=self._get_auth_headers(),
                timeout=API_CONFIG["request"]["timeout"],
                catch_response=True,
                name="product_list"
            ) as response:
                api_stats["total_requests"] += 1
                
                if response.status_code == 200:
                    try:
                        result = response.json()
                        if result.get("code") == 200:
                            products = result.get("rows", [])
                            for product in products:
                                product_id = product.get("productId")
                                if product_id:
                                    self.product_cache[product_id] = product
                                    api_stats["products_tested"].add(product_id)
                            
                            api_stats["successful_requests"] += 1
                            response.success()
                            logger.debug(f"成功获取 {len(products)} 个产品")
                        else:
                            api_stats["failed_requests"] += 1
                            api_stats["business_logic_failures"] += 1
                            error_msg = result.get("msg", "获取产品列表失败")
                            response.failure(f"产品列表业务失败: {error_msg}")
                            self._record_error("产品列表业务失败", error_msg)
                    except json.JSONDecodeError:
                        api_stats["failed_requests"] += 1
                        api_stats["business_logic_failures"] += 1
                        response.failure("产品列表响应格式错误")
                        self._record_error("产品列表响应格式错误", "JSON解析失败")
                else:
                    api_stats["failed_requests"] += 1
                    api_stats["connection_failures"] += 1
                    response.failure(f"产品列表HTTP失败: {response.status_code}")
                    self._record_error("产品列表HTTP失败", str(response.status_code))
                    
        except Exception as e:
            api_stats["failed_requests"] += 1
            api_stats["connection_failures"] += 1
            logger.error(f"❌ 获取产品列表异常: {str(e)}")
            self._record_error("产品列表异常", str(e))
    
    @retry_on_failure(max_retries=2)
    def get_device_list(self, page_size=10):
        """获取设备列表"""
        global api_stats
        
        params = {
            "pageNum": 1,
            "pageSize": page_size,
            "deviceName": "",
            "status": 3  # 在线设备
        }
        
        try:
            with self.client.get(
                API_CONFIG["endpoints"]["device_list"],
                params=params,
                headers=self._get_auth_headers(),
                timeout=API_CONFIG["request"]["timeout"],
                catch_response=True,
                name="device_list"
            ) as response:
                api_stats["total_requests"] += 1
                
                if response.status_code == 200:
                    try:
                        result = response.json()
                        if result.get("code") == 200:
                            devices = result.get("rows", [])
                            for device in devices:
                                device_serial = device.get("serialNumber")
                                if device_serial:
                                    self.device_cache[device_serial] = device
                                    api_stats["devices_tested"].add(device_serial)
                            
                            api_stats["successful_requests"] += 1
                            response.success()
                            logger.debug(f"成功获取 {len(devices)} 个设备")
                            return devices
                        else:
                            api_stats["failed_requests"] += 1
                            api_stats["business_logic_failures"] += 1
                            error_msg = result.get("msg", "获取设备列表失败")
                            response.failure(f"设备列表业务失败: {error_msg}")
                            self._record_error("设备列表业务失败", error_msg)
                    except json.JSONDecodeError:
                        api_stats["failed_requests"] += 1
                        api_stats["business_logic_failures"] += 1
                        response.failure("设备列表响应格式错误")
                        self._record_error("设备列表响应格式错误", "JSON解析失败")
                else:
                    api_stats["failed_requests"] += 1
                    api_stats["connection_failures"] += 1
                    response.failure(f"设备列表HTTP失败: {response.status_code}")
                    self._record_error("设备列表HTTP失败", str(response.status_code))
                    
        except Exception as e:
            api_stats["failed_requests"] += 1
            api_stats["connection_failures"] += 1
            logger.error(f"❌ 获取设备列表异常: {str(e)}")
            self._record_error("设备列表异常", str(e))
        
        return []
    
    @task(40)  # 权重40 - 数据上报（主要业务）
    def post_device_message(self):
        """设备数据上报 - 修复版"""
        if not self.token:
            logger.debug("没有有效Token，跳过数据上报")
            return
        
        global api_stats
        
        # 选择真实设备和产品
        product_id = random.choice(list(API_CONFIG["products"].keys()))
        device_serial = get_real_device_for_api(product_id)
        
        # 生成真实数据
        device_data = generate_real_api_device_data(product_id)
        
        # 修正消息格式，符合系统期望
        message = format_api_message(device_data, device_serial, product_id)
        
        try:
            with self.client.post(
                API_CONFIG["endpoints"]["message_post"],
                json=message,
                headers=self._get_auth_headers(),
                timeout=API_CONFIG["request"]["timeout"],
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
                            error_msg = result.get("msg", "数据上报失败")
                            response.failure(f"消息处理业务失败: {error_msg}")
                            self._record_error("消息处理业务失败", error_msg)
                    except json.JSONDecodeError:
                        api_stats["failed_requests"] += 1
                        api_stats["business_logic_failures"] += 1
                        response.failure("消息处理响应格式错误")
                        self._record_error("消息处理响应格式错误", "JSON解析失败")
                else:
                    api_stats["failed_requests"] += 1
                    api_stats["connection_failures"] += 1
                    response.failure(f"消息处理HTTP失败: {response.status_code}")
                    self._record_error("消息处理HTTP失败", str(response.status_code))
                    
        except Exception as e:
            api_stats["failed_requests"] += 1
            api_stats["connection_failures"] += 1
            logger.error(f"❌ 数据上报异常: {str(e)}")
            self._record_error("消息处理异常", str(e))
    
    @task(20)  # 权重20 - 设备控制
    def invoke_device_service(self):
        """设备服务调用（控制指令）- 修复版"""
        if not self.token:
            return
        
        global api_stats
        
        # 选择真实设备
        product_id = random.choice(list(API_CONFIG["products"].keys()))
        device_serial = get_real_device_for_api(product_id)
        
        # 生成控制指令
        control_commands = {
            1: {"command": "set_threshold", "temperature": 25, "humidity": 60},
            2: {"command": "start_recording", "duration": 3600},
            3: {"command": "set_production_target", "target": 1000},
            4: {"command": "restart_service", "service": "data_collection"},
            5: {"command": "read_meter", "type": "current_month"}
        }
        
        command_data = control_commands.get(product_id, {"command": "status_check"})
        
        # 修正服务调用数据格式
        invoke_data = {
            "deviceSerialNumber": device_serial,
            "productId": product_id,
            "serviceId": f"control_{random.randint(1000, 9999)}",
            "identifier": list(command_data.keys())[0],  # 使用第一个命令作为标识符
            "params": command_data
        }
        
        try:
            with self.client.post(
                API_CONFIG["endpoints"]["runtime_invoke"],
                json=invoke_data,
                headers=self._get_auth_headers(),
                timeout=API_CONFIG["request"]["timeout"],
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
                            error_msg = result.get("msg", "设备控制失败")
                            response.failure(f"设备控制业务失败: {error_msg}")
                            self._record_error("设备控制业务失败", error_msg)
                    except json.JSONDecodeError:
                        api_stats["failed_requests"] += 1
                        api_stats["business_logic_failures"] += 1
                        response.failure("设备控制响应格式错误")
                        self._record_error("设备控制响应格式错误", "JSON解析失败")
                else:
                    api_stats["failed_requests"] += 1
                    api_stats["connection_failures"] += 1
                    response.failure(f"设备控制HTTP失败: {response.status_code}")
                    self._record_error("设备控制HTTP失败", str(response.status_code))
                    
        except Exception as e:
            api_stats["failed_requests"] += 1
            api_stats["connection_failures"] += 1
            logger.error(f"❌ 设备控制异常: {str(e)}")
            self._record_error("设备控制异常", str(e))
    
    @task(15)  # 权重15 - 设备状态查询
    def query_device_status(self):
        """查询设备状态 - 修复版"""
        if not self.token:
            return
        
        global api_stats
        
        # 如果有缓存的设备，优先使用
        if self.device_cache:
            device_info = random.choice(list(self.device_cache.values()))
            device_id = device_info.get("deviceId")
        else:
            # 使用真实设备ID（这里需要根据实际情况调整）
            device_id = random.randint(1, 1000)  # 模拟设备ID
        
        if not device_id:
            return
        
        try:
            url = API_CONFIG["endpoints"]["device_status"].format(device_id=device_id)
            with self.client.get(
                url,
                headers=self._get_auth_headers(),
                timeout=API_CONFIG["request"]["timeout"],
                catch_response=True,
                name="device_status"
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
                            error_msg = result.get("msg", "设备状态查询失败")
                            response.failure(f"设备状态业务失败: {error_msg}")
                            self._record_error("设备状态业务失败", error_msg)
                    except json.JSONDecodeError:
                        api_stats["failed_requests"] += 1
                        api_stats["business_logic_failures"] += 1
                        response.failure("设备状态响应格式错误")
                        self._record_error("设备状态响应格式错误", "JSON解析失败")
                else:
                    api_stats["failed_requests"] += 1
                    if response.status_code == 404:
                        api_stats["business_logic_failures"] += 1
                        response.failure(f"设备不存在: {device_id}")
                        self._record_error("设备不存在", str(device_id))
                    else:
                        api_stats["connection_failures"] += 1
                        response.failure(f"设备状态HTTP失败: {response.status_code}")
                        self._record_error("设备状态HTTP失败", str(response.status_code))
                    
        except Exception as e:
            api_stats["failed_requests"] += 1
            api_stats["connection_failures"] += 1
            logger.error(f"❌ 设备状态查询异常: {str(e)}")
            self._record_error("设备状态异常", str(e))
    
    @task(5)  # 权重5 - 获取设备日志
    def get_device_logs(self):
        """获取设备日志 - 修复版"""
        if not self.token:
            return
        
        global api_stats
        
        # 选择真实设备
        product_id = random.choice(list(API_CONFIG["products"].keys()))
        device_serial = get_real_device_for_api(product_id)
        
        params = {
            "pageNum": 1,
            "pageSize": 10,
            "serialNumber": device_serial,
            "logType": random.choice([1, 2, 3]),  # 1-属性，2-事件，3-服务
            "beginTime": (datetime.now() - timedelta(hours=1)).strftime("%Y-%m-%d %H:%M:%S"),
            "endTime": datetime.now().strftime("%Y-%m-%d %H:%M:%S")
        }
        
        try:
            with self.client.get(
                API_CONFIG["endpoints"]["device_log"],
                params=params,
                headers=self._get_auth_headers(),
                timeout=API_CONFIG["request"]["timeout"],
                catch_response=True,
                name="device_log"
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
                            error_msg = result.get("msg", "设备日志查询失败")
                            response.failure(f"设备日志业务失败: {error_msg}")
                            self._record_error("设备日志业务失败", error_msg)
                    except json.JSONDecodeError:
                        api_stats["failed_requests"] += 1
                        api_stats["business_logic_failures"] += 1
                        response.failure("设备日志响应格式错误")
                        self._record_error("设备日志响应格式错误", "JSON解析失败")
                else:
                    api_stats["failed_requests"] += 1
                    api_stats["connection_failures"] += 1
                    response.failure(f"设备日志HTTP失败: {response.status_code}")
                    self._record_error("设备日志HTTP失败", str(response.status_code))
                    
        except Exception as e:
            api_stats["failed_requests"] += 1
            api_stats["connection_failures"] += 1
            logger.error(f"❌ 设备日志查询异常: {str(e)}")
            self._record_error("设备日志异常", str(e))

if __name__ == "__main__":
    import os
    os.system("locust -f locustfile.py")


