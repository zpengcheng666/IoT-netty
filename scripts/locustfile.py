"""
SYDH物联网平台Locust压力测试脚本 v2.0
==================================================

🎯 测试目标：
- 支持50万+设备并发连接
- 模拟真实IoT设备数据上报场景  
- 覆盖核心REST API接口和MQTT协议
- 提供多种设备类型和数据模式

📋 接口覆盖范围：
1. 🔥 核心数据传输接口 (专注测试)
   - POST /iot/runtime/service/invoke        设备数据上报 ⭐⭐⭐⭐⭐ (权重78%)
   - POST /iot/runtime/service/invokeReply   设备指令下发 ⭐⭐⭐⭐⭐ (权重22%)
   - POST /iot/message/post                  设备消息处理 ⭐⭐⭐⭐ (权重10%)

注意: 本版本专注于IoT设备核心REST API测试，MQTT协议测试已单独实现

🚀 使用方法：
# 基础测试 (1000设备)
locust -f locustfile.py --host=http://localhost:8080 --users=1000 --spawn-rate=100 --run-time=300s

# 大规模测试 (10万设备)  
locust -f locustfile.py --host=http://localhost:8080 --users=100000 --spawn-rate=1000 --run-time=1800s --csv=results/load-test

# 超大规模测试 (50万设备)
locust -f locustfile.py --host=http://localhost:8080 --users=500000 --spawn-rate=5000 --run-time=3600s --csv=results/stress-test

# 分布式测试
locust -f locustfile.py --master --host=http://localhost:8080
locust -f locustfile.py --worker --master-host=192.168.1.100

📊 测试数据规模：
- 设备类型: 6种 (摄像头、PLC、传感器、网关、采集器、电表)
- 数据点: 每设备5-15个属性
- 消息频率: 1-30秒/次 (专注高频数据传输)
- 数据大小: 500B-5KB/消息  
- 总数据量: 最高可达15GB/小时 (纯REST API数据传输测试)

认证信息：
- 用户名: admin  
- 密码: admin123
- Token: 自动获取或使用预设token
"""

import json
import random
import time
import math
from datetime import datetime, timedelta
from locust import HttpUser, task, between, events
from locust.runners import MasterRunner
import logging

# 配置日志
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# ===== 核心测试配置 =====
IOT_TEST_CONFIG = {
    # 基础配置
    "max_devices": 100000,
    "api_timeout": 30,
    "data_interval_min": 1,     # 最小上报间隔(秒)
    "data_interval_max": 30,    # 最大上报间隔(秒)
    
    # 认证配置
    "auth": {
        "username": "admin",
        "password": "admin123", 
        "token": "eyJhbGciOiJIUzUxMiJ9.eyJsb2dpbl91c2VyX2tleSI6ImNlOTVjZjc1LWJjNGQtNDJmYi1iOGIzLTQ2OTNmODkxZjY1MSJ9.X6B8IhFXi0alMzcOOP-DrIHfTjNMQqzQtcPSB1FlkTpZfX_-dnGWIMu17I4RmeaP3kUgVlxwmXHpaFgyF_277A"
    },
    
    # 设备类型定义
    "device_types": {
        "camera": {
            "productId": 1,
            "deviceType": 1, 
            "prefix": "CAM",
            "description": "智能摄像头",
            "data_frequency": "high"  # 高频上报
        },
        "plc": {
            "productId": 2,
            "deviceType": 2,
            "prefix": "PLC", 
            "description": "工业控制器",
            "data_frequency": "high"
        },
        "sensor": {
            "productId": 3,
            "deviceType": 3,
            "prefix": "SENSOR",
            "description": "环境传感器", 
            "data_frequency": "medium"
        },
        "gateway": {
            "productId": 4,
            "deviceType": 4,
            "prefix": "GW",
            "description": "物联网网关",
            "data_frequency": "medium"
        },
        "collector": {
            "productId": 5,
            "deviceType": 5,
            "prefix": "COL",
            "description": "数据采集器",
            "data_frequency": "low"
        },
        "meter": {
            "productId": 6,
            "deviceType": 6,
            "prefix": "METER",
            "description": "智能电表",
            "data_frequency": "low"
        }
    },
    

    
    # 消息类型配置
    "message_types": {
        "alarm": {
            "levels": ["info", "warning", "error", "critical"],
            "codes": ["TEMP_HIGH", "DISK_FULL", "NETWORK_ERROR", "SENSOR_FAULT", "POWER_FAIL", "MEMORY_LEAK", "CPU_OVERLOAD"]
        },
        "status": {
            "states": ["online", "offline", "maintenance", "error", "standby", "upgrading"]
        },
        "event": {
            "types": ["threshold_exceeded", "state_changed", "alarm_triggered", "heartbeat", "ota_update", "config_changed"]
        }
    }
}

# ===== 真实IoT设备数据模板 =====
DEVICE_DATA_TEMPLATES = {
    "camera": {
        # 系统状态数据
        "online_status": {"type": "bool", "generator": lambda: random.choice([0, 1])},
        "recording_status": {"type": "bool", "generator": lambda: random.choice([0, 1])},
        "video_quality": {"type": "enum", "generator": lambda: random.choice(["720P", "1080P", "4K", "8K"])},
        
        # 性能监控数据  
        "cpu_usage": {"type": "decimal", "unit": "%", "generator": lambda: round(random.uniform(5, 95), 2)},
        "memory_usage": {"type": "decimal", "unit": "%", "generator": lambda: round(random.uniform(20, 85), 2)},
        "disk_usage": {"type": "decimal", "unit": "%", "generator": lambda: round(random.uniform(15, 90), 2)},
        "temperature": {"type": "decimal", "unit": "°C", "generator": lambda: round(random.uniform(30, 70), 1)},
        
        # 网络状态数据
        "network_quality": {"type": "integer", "unit": "%", "generator": lambda: random.randint(60, 100)},
        "bandwidth_usage": {"type": "decimal", "unit": "Mbps", "generator": lambda: round(random.uniform(0.5, 15.0), 2)},
        "packet_loss": {"type": "decimal", "unit": "%", "generator": lambda: round(random.uniform(0, 5), 3)},
        
        # 功能状态数据
        "motion_detected": {"type": "bool", "generator": lambda: random.choice([0, 1])},
        "face_count": {"type": "integer", "generator": lambda: random.randint(0, 20)},
        "storage_remaining": {"type": "decimal", "unit": "GB", "generator": lambda: round(random.uniform(5, 500), 1)},
        
        # 设备信息
        "firmware_version": {"type": "string", "generator": lambda: f"V{random.randint(1,4)}.{random.randint(0,9)}.{random.randint(0,99)}"},
        "uptime": {"type": "integer", "unit": "seconds", "generator": lambda: random.randint(3600, 8640000)}
    },
    
    "plc": {
        # 生产状态数据
        "production_status": {"type": "enum", "generator": lambda: random.choice(["stopped", "running", "fault", "maintenance", "standby"])},
        "production_count": {"type": "integer", "unit": "pieces", "generator": lambda: random.randint(800, 6000)},
        "production_rate": {"type": "decimal", "unit": "%", "generator": lambda: round(random.uniform(75, 100), 2)},
        "cycle_time": {"type": "decimal", "unit": "seconds", "generator": lambda: round(random.uniform(30, 120), 1)},
        
        # 物理参数监控
        "temperature": {"type": "decimal", "unit": "°C", "generator": lambda: round(random.uniform(35, 90), 1)},
        "pressure": {"type": "decimal", "unit": "MPa", "generator": lambda: round(random.uniform(0.8, 3.0), 2)},
        "vibration": {"type": "decimal", "unit": "mm/s", "generator": lambda: round(random.uniform(0.05, 8.0), 2)},
        "flow_rate": {"type": "decimal", "unit": "L/min", "generator": lambda: round(random.uniform(40, 250), 1)},
        
        # 电气参数
        "voltage": {"type": "decimal", "unit": "V", "generator": lambda: round(random.uniform(210, 250), 1)},
        "current": {"type": "decimal", "unit": "A", "generator": lambda: round(random.uniform(8, 60), 2)},
        "power": {"type": "decimal", "unit": "kW", "generator": lambda: round(random.uniform(1.5, 12.0), 2)},
        "frequency": {"type": "decimal", "unit": "Hz", "generator": lambda: round(random.uniform(49.5, 50.5), 2)},
        
        # 数字量状态
        "pump_status": {"type": "array", "generator": lambda: [random.randint(0, 1) for _ in range(8)]},
        "valve_status": {"type": "array", "generator": lambda: [random.randint(0, 1) for _ in range(6)]},
        "alarm_bits": {"type": "array", "generator": lambda: [random.randint(0, 1) for _ in range(16)]},
        
        # 故障信息
        "alarm_code": {"type": "integer", "generator": lambda: random.randint(0, 99)},
        "error_count": {"type": "integer", "generator": lambda: random.randint(0, 15)}
    },
    
    "sensor": {
        # 环境监测数据
        "temperature": {"type": "decimal", "unit": "°C", "generator": lambda: round(random.uniform(-20, 60), 1)},
        "humidity": {"type": "decimal", "unit": "%", "generator": lambda: round(random.uniform(15, 98), 1)},
        "pressure": {"type": "decimal", "unit": "hPa", "generator": lambda: round(random.uniform(900, 1100), 2)},
        "light_intensity": {"type": "integer", "unit": "lux", "generator": lambda: random.randint(0, 120000)},
        "noise_level": {"type": "decimal", "unit": "dB", "generator": lambda: round(random.uniform(25, 130), 1)},
        "wind_speed": {"type": "decimal", "unit": "m/s", "generator": lambda: round(random.uniform(0, 25), 1)},
        
        # 空气质量监测
        "pm2_5": {"type": "decimal", "unit": "μg/m³", "generator": lambda: round(random.uniform(2, 200), 1)},
        "pm10": {"type": "decimal", "unit": "μg/m³", "generator": lambda: round(random.uniform(5, 300), 1)},
        "co2": {"type": "integer", "unit": "ppm", "generator": lambda: random.randint(350, 5000)},
        "co": {"type": "decimal", "unit": "ppm", "generator": lambda: round(random.uniform(0.1, 50.0), 2)},
        "no2": {"type": "decimal", "unit": "ppm", "generator": lambda: round(random.uniform(0.01, 0.5), 3)},
        "so2": {"type": "decimal", "unit": "ppm", "generator": lambda: round(random.uniform(0.01, 0.3), 3)},
        
        # 设备自身状态
        "battery_level": {"type": "integer", "unit": "%", "generator": lambda: random.randint(5, 100)},
        "signal_strength": {"type": "integer", "unit": "dBm", "generator": lambda: -random.randint(30, 120)},
        "data_quality": {"type": "enum", "generator": lambda: random.choice(["excellent", "good", "fair", "poor"])},
        
        # 地理位置信息
        "latitude": {"type": "decimal", "generator": lambda: round(random.uniform(39.5, 40.5), 6)},
        "longitude": {"type": "decimal", "generator": lambda: round(random.uniform(116.0, 117.0), 6)},
        "altitude": {"type": "decimal", "unit": "m", "generator": lambda: round(random.uniform(5, 200), 1)}
    },
    
    "gateway": {
        # 连接状态监控
        "online_devices": {"type": "integer", "generator": lambda: random.randint(30, 800)},
        "total_devices": {"type": "integer", "generator": lambda: random.randint(100, 1000)},
        "connection_rate": {"type": "decimal", "unit": "%", "generator": lambda: round(random.uniform(80, 99.5), 2)},
        "active_connections": {"type": "integer", "generator": lambda: random.randint(50, 600)},
        
        # 系统资源监控
        "cpu_usage": {"type": "decimal", "unit": "%", "generator": lambda: round(random.uniform(5, 85), 2)},
        "memory_usage": {"type": "decimal", "unit": "%", "generator": lambda: round(random.uniform(15, 90), 2)},
        "disk_usage": {"type": "decimal", "unit": "%", "generator": lambda: round(random.uniform(25, 80), 2)},
        "temperature": {"type": "decimal", "unit": "°C", "generator": lambda: round(random.uniform(20, 75), 1)},
        
        # 网络性能监控
        "uplink_bandwidth": {"type": "decimal", "unit": "Mbps", "generator": lambda: round(random.uniform(10, 1000), 2)},
        "downlink_bandwidth": {"type": "decimal", "unit": "Mbps", "generator": lambda: round(random.uniform(50, 1000), 2)},
        "packet_loss": {"type": "decimal", "unit": "%", "generator": lambda: round(random.uniform(0, 8), 3)},
        "latency": {"type": "integer", "unit": "ms", "generator": lambda: random.randint(1, 150)},
        "throughput": {"type": "decimal", "unit": "Mbps", "generator": lambda: round(random.uniform(50, 900), 1)},
        
        # 数据传输统计
        "data_received": {"type": "integer", "unit": "packets", "generator": lambda: random.randint(5000, 200000)},
        "data_sent": {"type": "integer", "unit": "packets", "generator": lambda: random.randint(4000, 180000)},
        "error_rate": {"type": "decimal", "unit": "%", "generator": lambda: round(random.uniform(0, 3), 3)},
        "retransmission_rate": {"type": "decimal", "unit": "%", "generator": lambda: round(random.uniform(0, 5), 2)},
        
        # 系统信息
        "firmware_version": {"type": "string", "generator": lambda: f"GW-{random.randint(2,5)}.{random.randint(0,9)}.{random.randint(0,99)}"},
        "uptime": {"type": "integer", "unit": "seconds", "generator": lambda: random.randint(86400, 31536000)}
    },
    
    "collector": {
        # 数据采集状态
        "collection_status": {"type": "enum", "generator": lambda: random.choice(["active", "idle", "error", "maintenance"])},
        "data_count": {"type": "integer", "unit": "records", "generator": lambda: random.randint(500, 10000)},
        "collection_rate": {"type": "decimal", "unit": "records/min", "generator": lambda: round(random.uniform(10, 500), 1)},
        "success_rate": {"type": "decimal", "unit": "%", "generator": lambda: round(random.uniform(85, 99.9), 2)},
        
        # 存储状态
        "storage_usage": {"type": "decimal", "unit": "%", "generator": lambda: round(random.uniform(30, 95), 1)},
        "storage_capacity": {"type": "decimal", "unit": "GB", "generator": lambda: round(random.uniform(100, 2000), 1)},
        "data_compression": {"type": "decimal", "unit": "%", "generator": lambda: round(random.uniform(60, 90), 1)},
        
        # 同步状态
        "last_sync_time": {"type": "string", "generator": lambda: (datetime.now() - timedelta(minutes=random.randint(1, 60))).strftime("%Y-%m-%d %H:%M:%S")},
        "sync_success_rate": {"type": "decimal", "unit": "%", "generator": lambda: round(random.uniform(90, 100), 2)},
        "pending_records": {"type": "integer", "generator": lambda: random.randint(0, 1000)},
        
        # 性能指标
        "cpu_usage": {"type": "decimal", "unit": "%", "generator": lambda: round(random.uniform(10, 70), 2)},
        "memory_usage": {"type": "decimal", "unit": "%", "generator": lambda: round(random.uniform(20, 80), 2)},
        "error_count": {"type": "integer", "generator": lambda: random.randint(0, 10)},
        "timeout_count": {"type": "integer", "generator": lambda: random.randint(0, 5)}
    },
    
    "meter": {
        # 电能计量数据
        "current_reading": {"type": "decimal", "unit": "kWh", "generator": lambda: round(random.uniform(500, 99999), 2)},
        "instantaneous_power": {"type": "decimal", "unit": "kW", "generator": lambda: round(random.uniform(0.1, 50.0), 3)},
        "daily_consumption": {"type": "decimal", "unit": "kWh", "generator": lambda: round(random.uniform(5, 200), 2)},
        "monthly_consumption": {"type": "decimal", "unit": "kWh", "generator": lambda: round(random.uniform(150, 6000), 1)},
        
        # 电气参数
        "voltage_a": {"type": "decimal", "unit": "V", "generator": lambda: round(random.uniform(210, 250), 1)},
        "voltage_b": {"type": "decimal", "unit": "V", "generator": lambda: round(random.uniform(210, 250), 1)},
        "voltage_c": {"type": "decimal", "unit": "V", "generator": lambda: round(random.uniform(210, 250), 1)},
        "current_a": {"type": "decimal", "unit": "A", "generator": lambda: round(random.uniform(0.5, 80), 2)},
        "current_b": {"type": "decimal", "unit": "A", "generator": lambda: round(random.uniform(0.5, 80), 2)},
        "current_c": {"type": "decimal", "unit": "A", "generator": lambda: round(random.uniform(0.5, 80), 2)},
        "power_factor": {"type": "decimal", "generator": lambda: round(random.uniform(0.7, 1.0), 3)},
        "frequency": {"type": "decimal", "unit": "Hz", "generator": lambda: round(random.uniform(49.5, 50.5), 2)},
        
        # 费率和计费
        "tariff_rate": {"type": "decimal", "unit": "yuan/kWh", "generator": lambda: round(random.uniform(0.3, 1.5), 4)},
        "peak_demand": {"type": "decimal", "unit": "kW", "generator": lambda: round(random.uniform(5, 100), 2)},
        "demand_time": {"type": "string", "generator": lambda: (datetime.now() - timedelta(hours=random.randint(1, 24))).strftime("%Y-%m-%d %H:%M:%S")},
        
        # 设备状态
        "meter_status": {"type": "enum", "generator": lambda: random.choice(["normal", "warning", "error", "maintenance"])},
        "communication_status": {"type": "bool", "generator": lambda: random.choice([0, 1])},
        "temperature": {"type": "decimal", "unit": "°C", "generator": lambda: round(random.uniform(15, 60), 1)},
        "signal_strength": {"type": "integer", "unit": "dBm", "generator": lambda: -random.randint(40, 100)}
    }
}

# 全局变量
device_pool = []
test_statistics = {
    "total_devices": 0,
    "total_messages": 0,
    "data_volume_mb": 0,
    "start_time": None
}

# ===== 数据生成函数 =====

def generate_realistic_device_data(device_type, num_properties=None):
    """生成真实的IoT设备数据"""
    if device_type not in DEVICE_DATA_TEMPLATES:
        device_type = "sensor"
    
    template = DEVICE_DATA_TEMPLATES[device_type]
    
    # 随机选择属性数量
    if num_properties is None:
        num_properties = random.randint(5, min(12, len(template)))
    
    selected_props = random.sample(list(template.keys()), min(num_properties, len(template)))
    
    data_items = []
    for prop_name in selected_props:
        prop_config = template[prop_name]
        
        # 生成数据值
        value = prop_config["generator"]()
        
        # 构造ThingsModelSimpleItem格式
        item = {
            "id": prop_name,
            "value": json.dumps(value) if isinstance(value, (list, dict)) else str(value),
            "name": prop_name.replace("_", " ").title(),
            "ts": datetime.now().strftime("%Y-%m-%d %H:%M:%S"),
            "remark": f"{device_type} {prop_name} measurement"
        }
        data_items.append(item)
    
    return data_items

def generate_device_command_data(device_type):
    """生成设备指令数据"""
    commands = {
        "camera": {
            "start_recording": {"quality": "1080P", "duration": 3600, "format": "mp4"},
            "stop_recording": {},
            "capture_snapshot": {"format": "jpg", "quality": 90},
            "set_quality": {"quality": random.choice(["720P", "1080P", "4K"])},
            "pan_tilt": {"pan": random.randint(-180, 180), "tilt": random.randint(-90, 90)},
            "zoom": {"level": random.randint(1, 10)},
            "night_vision": {"enabled": random.choice([True, False])},
            "motion_detection": {"enabled": random.choice([True, False]), "sensitivity": random.randint(1, 10)}
        },
        "plc": {
            "start_production": {"mode": "auto", "target_count": random.randint(1000, 5000)},
            "stop_production": {"reason": random.choice(["maintenance", "emergency", "end_shift"])},
            "reset_counter": {"counter_type": random.choice(["production", "error", "all"])},
            "set_parameters": {
                "pressure": round(random.uniform(1.0, 2.5), 2),
                "flow_rate": random.randint(100, 200),
                "temperature": random.randint(60, 80)
            },
            "emergency_stop": {},
            "calibrate_sensor": {"sensor_type": random.choice(["temperature", "pressure", "flow"])},
            "update_recipe": {"recipe_id": random.randint(1, 20)}
        },
        "sensor": {
            "calibrate": {"reference_value": random.uniform(20, 30), "sensor_type": "temperature"},
            "set_interval": {"interval": random.choice([30, 60, 300, 600])},
            "reset_device": {"preserve_config": True},
            "sleep_mode": {"duration": random.randint(3600, 86400)},
            "set_threshold": {
                "parameter": random.choice(["temperature", "humidity", "pressure"]),
                "min_value": random.uniform(0, 50),
                "max_value": random.uniform(50, 100)
            },
            "location_update": {
                "latitude": round(random.uniform(39.5, 40.5), 6),
                "longitude": round(random.uniform(116.0, 117.0), 6)
            }
        },
        "gateway": {
            "reboot": {"delay": random.randint(5, 30)},
            "update_firmware": {"version": f"2.{random.randint(0,9)}.{random.randint(0,99)}", "auto_reboot": True},
            "sync_time": {"ntp_server": random.choice(["ntp.aliyun.com", "time.windows.com"])},
            "reset_network": {"preserve_config": random.choice([True, False])},
            "add_device": {"device_id": f"DEV_{random.randint(1000, 9999)}", "device_type": random.choice(["sensor", "meter"])},
            "remove_device": {"device_id": f"DEV_{random.randint(1000, 9999)}"},
            "backup_config": {"include_logs": random.choice([True, False])}
        },
        "collector": {
            "start_collect": {"data_source": random.choice(["modbus", "opcua", "http"])},
            "stop_collect": {"graceful": True},
            "sync_data": {"force": random.choice([True, False])},
            "clear_cache": {"confirm": True},
            "export_data": {"format": random.choice(["csv", "json", "xml"]), "date_range": "7d"},
            "set_schedule": {"cron": "0 */6 * * *", "enabled": True}
        },
        "meter": {
            "read_meter": {"register_type": random.choice(["energy", "demand", "voltage"])},
            "reset_billing": {"confirm": True},
            "set_tariff": {"rate": round(random.uniform(0.5, 1.2), 4)},
            "calibrate": {"parameter": random.choice(["voltage", "current", "power"])},
            "freeze_reading": {"freeze_time": datetime.now().strftime("%Y-%m-%d %H:%M:%S")},
            "demand_reset": {"reset_time": "monthly"}
        }
    }
    
    device_commands = commands.get(device_type, commands["sensor"])
    command_name = random.choice(list(device_commands.keys()))
    command_params = device_commands[command_name]
    
    return command_name, command_params



def generate_complex_device_message():
    """生成复杂设备消息（告警、事件等）"""
    message_type = random.choice(list(IOT_TEST_CONFIG["message_types"].keys()))
    
    if message_type == "alarm":
        config = IOT_TEST_CONFIG["message_types"]["alarm"]
        alarm_code = random.choice(config["codes"])
        return {
            "messageType": "alarm",
            "level": random.choice(config["levels"]),
            "code": alarm_code,
            "content": f"设备告警: {alarm_code}",
            "timestamp": datetime.now().isoformat(),
            "context": {
                "location": f"Zone_{random.randint(1, 20)}",
                "operator": f"User_{random.randint(1, 100)}",
                "severity": random.randint(1, 10),
                "acknowledged": random.choice([True, False])
            },
            "data": generate_realistic_device_data(random.choice(list(IOT_TEST_CONFIG["device_types"].keys())), 3),
            "remediation": {
                "suggested_action": random.choice(["check_sensors", "restart_device", "contact_support", "schedule_maintenance"]),
                "urgency": random.choice(["low", "medium", "high", "critical"])
            }
        }
    elif message_type == "status":
        config = IOT_TEST_CONFIG["message_types"]["status"]
        return {
            "messageType": "status",
            "currentState": random.choice(config["states"]),
            "previousState": random.choice(config["states"]),
            "stateChangeTime": datetime.now().isoformat(),
            "duration": random.randint(60, 86400),
            "reason": random.choice(["user_command", "system_auto", "error_recovery", "scheduled_maintenance", "power_cycle"]),
            "metadata": {
                "change_count": random.randint(1, 50),
                "stability_score": round(random.uniform(0.7, 1.0), 3),
                "next_maintenance": (datetime.now() + timedelta(days=random.randint(1, 30))).strftime("%Y-%m-%d")
            }
        }
    else:  # event
        config = IOT_TEST_CONFIG["message_types"]["event"]
        return {
            "messageType": "event",
            "eventType": random.choice(config["types"]),
            "trigger": random.choice(["manual", "automatic", "scheduled", "threshold", "external"]),
            "severity": random.choice(["low", "medium", "high", "critical"]),
            "timestamp": datetime.now().isoformat(),
            "context": {
                "userId": random.randint(1, 1000),
                "source": random.choice(["device", "user", "system", "external", "scheduler"]),
                "sessionId": f"sess_{random.randint(100000, 999999)}",
                "correlation_id": f"corr_{random.randint(1000000, 9999999)}"
            },
            "payload": {
                "event_data": generate_realistic_device_data(random.choice(list(IOT_TEST_CONFIG["device_types"].keys())), 5),
                "metrics": {
                    "processing_time": random.randint(10, 500),
                    "retry_count": random.randint(0, 3),
                    "success": random.choice([True, False])
                }
            }
        }

def generate_device_info(device_id=None, device_type=None):
    """生成设备注册信息"""
    if device_id is None:
        device_id = random.randint(1, IOT_TEST_CONFIG["max_devices"])
    
    if device_type is None:
        device_type = random.choice(list(IOT_TEST_CONFIG["device_types"].keys()))
    
    type_config = IOT_TEST_CONFIG["device_types"][device_type]
    
    return {
        "deviceName": f"{type_config['prefix']}-{device_id:06d}",
        "productId": type_config["productId"],
        "userId": 1,
        "serialNumber": f"{type_config['prefix']}-{device_id:010d}",
        "firmwareVersion": f"V{random.randint(1,5)}.{random.randint(0,9)}.{random.randint(0,99)}",
        "status": random.choice([1, 2, 3]),  # 1=在线, 2=离线, 3=维护
        "deviceType": type_config["deviceType"],
        "networkType": random.randint(1, 4),  # 1=WiFi, 2=Ethernet, 3=4G, 4=5G
        "networkIp": f"192.168.{random.randint(1,255)}.{random.randint(1,255)}",
        "isEnable": 1,
        "coordinate": f"{random.uniform(116.0, 117.0):.6f},{random.uniform(39.5, 40.5):.6f}",
        "location": {
            "building": f"Building_{random.randint(1, 20)}",
            "floor": random.randint(1, 30),
            "room": f"Room_{random.randint(100, 999)}"
        },
        "thingsModelValue": json.dumps({
            "temperature": {"value": round(random.uniform(20, 30), 1)},
            "humidity": {"value": random.randint(40, 70)},
            "status": {"value": "online"}
        }),
        "createTime": datetime.now().strftime("%Y-%m-%d %H:%M:%S"),
        "description": f"{type_config['description']} - {device_id}"
    }

# ===== 事件监听器 =====

@events.test_start.add_listener
def on_test_start(environment, **kwargs):
    """测试启动时的初始化"""
    global test_statistics
    test_statistics["start_time"] = datetime.now()
    
    logger.info("🚀 SYDH IoT平台REST API压力测试启动")
    logger.info(f"📊 测试配置:")
    logger.info(f"   - 最大设备数: {IOT_TEST_CONFIG['max_devices']:,}")
    logger.info(f"   - 设备类型: {len(IOT_TEST_CONFIG['device_types'])} 种")
    logger.info(f"   - API超时: {IOT_TEST_CONFIG['api_timeout']} 秒")
    logger.info(f"   - 数据上报间隔: {IOT_TEST_CONFIG['data_interval_min']}-{IOT_TEST_CONFIG['data_interval_max']} 秒")
    logger.info(f"   - 测试协议: REST API (MQTT测试已分离)")
    
    # 如果是master节点，输出额外信息
    if isinstance(environment.runner, MasterRunner):
        logger.info("🎛️  Master节点启动，等待Worker节点连接...")

@events.test_stop.add_listener  
def on_test_stop(environment, **kwargs):
    """测试停止时的统计输出"""
    global test_statistics
    
    end_time = datetime.now()
    duration = end_time - test_statistics["start_time"]
    
    stats = environment.runner.stats.total
    
    logger.info("✅ 压力测试完成")
    logger.info("📈 测试统计:")
    logger.info(f"   - 测试时长: {duration}")
    logger.info(f"   - 总请求数: {stats.num_requests:,}")
    logger.info(f"   - 失败请求数: {stats.num_failures:,}")
    logger.info(f"   - 失败率: {(stats.num_failures/stats.num_requests*100):.2f}%" if stats.num_requests > 0 else "0.00%")
    logger.info(f"   - 平均响应时间: {stats.avg_response_time:.2f}ms")
    logger.info(f"   - 最大响应时间: {stats.max_response_time:.2f}ms") 
    logger.info(f"   - 当前RPS: {stats.current_rps:.2f}")
    logger.info(f"   - 平均RPS: {stats.total_rps:.2f}")
    logger.info(f"   - 数据传输量: {test_statistics['data_volume_mb']:.2f}MB")

# ===== IoT设备用户类 =====

class IoTDeviceUser(HttpUser):
    """模拟IoT设备用户"""
    
    wait_time = between(IOT_TEST_CONFIG["data_interval_min"], IOT_TEST_CONFIG["data_interval_max"])
    
    def on_start(self):
        """用户启动时初始化"""
        # 生成设备信息
        self.device_id = random.randint(1, IOT_TEST_CONFIG["max_devices"])
        self.device_type = random.choice(list(IOT_TEST_CONFIG["device_types"].keys()))
        self.device_info = generate_device_info(self.device_id, self.device_type)
        self.serial_number = self.device_info["serialNumber"]
        self.product_id = self.device_info["productId"]
        self.auth_token = None
        
        # 设置数据上报频率（基于设备类型）
        frequency = IOT_TEST_CONFIG["device_types"][self.device_type]["data_frequency"]
        if frequency == "high":
            self.wait_time = between(1, 3)
        elif frequency == "medium":
            self.wait_time = between(3, 10)
        else:  # low
            self.wait_time = between(10, 30)
        
        logger.info(f"🔌 设备 {self.serial_number} ({self.device_type}) 开始测试")
        
        # 登录获取token
        self.login()
        
        # 更新统计
        global test_statistics
        test_statistics["total_devices"] += 1
    
    def login(self):
        """用户登录获取token"""
        login_data = {
            "username": IOT_TEST_CONFIG["auth"]["username"],
            "password": IOT_TEST_CONFIG["auth"]["password"]
        }
        
        try:
            with self.client.post(
                "/login",
                json=login_data,
                timeout=IOT_TEST_CONFIG["api_timeout"],
                catch_response=True
            ) as response:
                if response.status_code == 200:
                    result = response.json()
                    if result.get("code") == 200:
                        self.auth_token = result.get("token", IOT_TEST_CONFIG["auth"]["token"])
                        response.success()
                    else:
                        self.auth_token = IOT_TEST_CONFIG["auth"]["token"]
                        response.failure(f"登录业务失败: {result.get('msg', 'Unknown error')}")
                else:
                    self.auth_token = IOT_TEST_CONFIG["auth"]["token"]
                    response.failure(f"登录HTTP失败: {response.status_code}")
        except Exception as e:
            self.auth_token = IOT_TEST_CONFIG["auth"]["token"]
            logger.warning(f"登录异常，使用预设token: {str(e)}")
        
        # 设置请求头
        self.client.headers.update({
            "Content-Type": "application/json",
            "Authorization": f"Bearer {self.auth_token}",
            "Accept": "application/json"
        })
    
    def on_stop(self):
        """用户停止时清理"""
        logger.info(f"🔌 设备 {self.serial_number} 测试结束")
    
    @task(78)  # 权重78 - 设备数据上报 ⭐⭐⭐⭐⭐ 最高频核心接口
    def report_device_data(self):
        """设备数据上报接口测试 - POST /iot/runtime/service/invoke"""
        data_items = generate_realistic_device_data(self.device_type)
        
        # 生成属性标识符(基于第一个数据项的id)
        identifier = data_items[0]["id"] if data_items else "temperature"
        
        # 构造remoteCommand格式 - key是属性名，value是数据数组
        remote_command = {}
        for item in data_items:
            remote_command[item["id"]] = item["value"]
        
        # 使用InvokeReqDto格式
        payload = {
            "serialNumber": self.serial_number,
            "identifier": identifier,  # 必需字段：标识符
            "productId": self.product_id,
            "params": {}, 
            "remoteCommand": remote_command,
            "timeOut": random.randint(10, 60),
            "type": 1,  # 属性类型
            "isShadow": str(random.choice([True, False])).lower(),
            "messageId": f"data_{random.randint(100000, 999999)}"
        }
        
        with self.client.post(
            "/iot/runtime/service/invoke",
            json=payload,
            timeout=IOT_TEST_CONFIG["api_timeout"],
            catch_response=True
        ) as response:
            if response.status_code == 200:
                try:
                    result = response.json()
                    if result.get("code") == 200:
                        response.success()
                        # 更新统计
                        global test_statistics
                        test_statistics["total_messages"] += 1
                        test_statistics["data_volume_mb"] += len(json.dumps(payload)) / 1024 / 1024
                    else:
                        response.failure(f"数据上报业务失败: {result.get('msg', 'Unknown error')}")
                except:
                    response.failure("数据上报返回格式错误")
            else:
                response.failure(f"数据上报HTTP失败: {response.status_code}")
    
    @task(22)  # 权重22 - 设备指令下发 ⭐⭐⭐⭐⭐ 控制指令
    def control_device(self):
        """设备指令下发接口测试 - POST /iot/runtime/service/invokeReply"""
        command_name, command_params = generate_device_command_data(self.device_type)
        
        payload = {
            "serialNumber": self.serial_number,
            "identifier": command_name,
            "productId": self.product_id,
            "params": command_params,
            "remoteCommand": {command_name: json.dumps(command_params)},
            "timeOut": random.randint(10, 60),
            "type": 2,  # 功能类型
            "isShadow": str(random.choice([True, False])).lower(),
            "messageId": f"cmd_{random.randint(100000, 999999)}"
        }
        
        with self.client.post(
            "/iot/runtime/service/invokeReply",
            json=payload,
            timeout=IOT_TEST_CONFIG["api_timeout"],
            catch_response=True
        ) as response:
            if response.status_code == 200:
                try:
                    result = response.json()
                    if result.get("code") == 200:
                        response.success()
                    else:
                        response.failure(f"设备控制业务失败: {result.get('msg', 'Unknown error')}")
                except:
                    response.failure("设备控制返回格式错误")
            else:
                response.failure(f"设备控制HTTP失败: {response.status_code}")
    
    @task(10)  # 权重10 - 设备消息处理 ⭐⭐⭐⭐ 消息队列
    def process_device_message(self):
        """设备消息处理接口测试 - POST /iot/message/post"""
        complex_message = generate_complex_device_message()
        
        # 根据消息类型生成合适的topicName
        message_type = complex_message.get("messageType", "message")
        if message_type == "alarm":
            topic_name = f"/{self.product_id}/{self.serial_number}/event/post"
        elif message_type == "status":
            topic_name = f"/{self.product_id}/{self.serial_number}/status/post"
        elif message_type == "event":
            topic_name = f"/{self.product_id}/{self.serial_number}/event/post"
        else:
            topic_name = f"/{self.product_id}/{self.serial_number}/message/post"
        
        # 使用DeviceMessage格式
        message_data = {
            "message": complex_message,  # Object类型 - 下发的数据
            "topicName": topic_name,     # String类型 - 下发的topic
            "serialNumber": self.serial_number,  # String类型 - 设备编号
            "dataType": message_type,    # String类型 - 数据类型
            "time": datetime.now().strftime("%Y-%m-%d %H:%M:%S:%f")[:-3]  # 时间格式
        }
        
        with self.client.post(
            "/iot/message/post",
            json=message_data,
            timeout=IOT_TEST_CONFIG["api_timeout"],
            catch_response=True
        ) as response:
            if response.status_code == 200:
                try:
                    result = response.json()
                    if result.get("code") == 200:
                        response.success()
                    else:
                        response.failure(f"消息处理业务失败: {result.get('msg', 'Unknown error')}")
                except:
                    response.failure("消息处理返回格式错误")
            else:
                response.failure(f"消息处理HTTP失败: {response.status_code}")
    


# 已删除不需要的用户类，专注核心数据传输测试

# Locust配置入口
if __name__ == "__main__":
    print("🚀 SYDH IoT REST API 压力测试脚本已加载")
    print("使用 locust -f locustfile.py --host=http://your-server 启动测试")
    print("📝 注意: MQTT协议测试请使用 mqtt_locust_test.py")
