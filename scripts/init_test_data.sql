-- 设置字符集（MySQL）
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- =================== 清理现有测试数据（可选） ===================
-- 如果需要重新初始化，取消下面的注释
-- DELETE FROM iot_device WHERE device_name LIKE 'TEST_%' OR serial_number LIKE 'TEST_%';
-- DELETE FROM iot_things_model WHERE product_name LIKE '%测试%';
-- DELETE FROM iot_product WHERE product_name LIKE '%测试%';
-- DELETE FROM iot_category WHERE category_name LIKE '%测试%';

-- =================== 插入测试产品分类 ===================
INSERT INTO iot_category (category_id, category_name, tenant_id, tenant_name, order_num, del_flag, create_by, create_time, update_by, update_time, remark) VALUES 
(100, '测试设备分类', 1, 'IoT物联', 100, '0', 'admin', NOW(), 'admin', NOW(), '压力测试专用分类'),
(101, '智能传感器', 1, 'IoT物联', 101, '0', 'admin', NOW(), 'admin', NOW(), '环境监测传感器'),
(102, '智能摄像头', 1, 'IoT物联', 102, '0', 'admin', NOW(), 'admin', NOW(), '视频监控设备'),
(103, '工业控制器', 1, 'IoT物联', 103, '0', 'admin', NOW(), 'admin', NOW(), '工业自动化设备'),
(104, '物联网网关', 1, 'IoT物联', 104, '0', 'admin', NOW(), 'admin', NOW(), '数据汇聚网关'),
(105, '智能电表', 1, 'IoT物联', 105, '0', 'admin', NOW(), 'admin', NOW(), '电力计量设备')
ON DUPLICATE KEY UPDATE 
    category_name = VALUES(category_name),
    update_time = VALUES(update_time),
    remark = VALUES(remark);

-- =================== 检测产品表字段版本 ===================
-- 由于不同版本字段名不同，这里提供两套插入语句，根据实际版本选择使用

-- =================== 新版本产品数据（account, auth_password, secret） ===================
INSERT INTO iot_product (
    product_id, product_name, protocol_code, category_id, category_name, tenant_id, tenant_name, is_sys, is_authorize,
    account, auth_password, secret,
    status, device_type, network_method, vertificate_method, transport, location_way, img_url, del_flag,
    create_by, create_time, update_by, update_time, remark, things_models_json
) VALUES 
-- 产品1：智能传感器
(1, '测试传感器产品', 'MQTT', 101, '智能传感器', 1, 'IoT物联', 0, 0, 
 'sensor_test', 'P_sensor_123456', 'K_sensor_secret_001', 
 2, 1, 1, 1, 'MQTT', 1, '/profile/iot/1.png', '0',
 'admin', NOW(), 'admin', NOW(), '传感器产品，用于环境监测',
 '[{"identifier":"temperature","name":"温度","type":1,"datatype":"decimal","specs":"{\"min\":-50,\"max\":100,\"unit\":\"℃\"}"},{"identifier":"humidity","name":"湿度","type":1,"datatype":"decimal","specs":"{\"min\":0,\"max\":100,\"unit\":\"%\"}"},{"identifier":"pressure","name":"气压","type":1,"datatype":"decimal","specs":"{\"min\":800,\"max\":1200,\"unit\":\"hPa\"}"},{"identifier":"restart","name":"重启设备","type":2,"datatype":"string","specs":"{}"}]'),

-- 产品2：智能摄像头
(2, '测试摄像头产品', 'MQTT', 102, '智能摄像头', 1, 'IoT物联', 0, 0,
 'camera_test', 'P_camera_123456', 'K_camera_secret_002',
 2, 1, 1, 1, 'MQTT', 1, '/profile/iot/2.png', '0',
 'admin', NOW(), 'admin', NOW(), '摄像头产品，用于视频监控',
 '[{"identifier":"recording","name":"录制状态","type":1,"datatype":"bool","specs":"{}"},{"identifier":"resolution","name":"分辨率","type":1,"datatype":"enum","specs":"{\"enumList\":[{\"text\":\"720P\",\"value\":\"720P\"},{\"text\":\"1080P\",\"value\":\"1080P\"},{\"text\":\"4K\",\"value\":\"4K\"}]}"},{"identifier":"storage_used","name":"存储使用率","type":1,"datatype":"decimal","specs":"{\"min\":0,\"max\":100,\"unit\":\"%\"}"},{"identifier":"take_photo","name":"拍照","type":2,"datatype":"string","specs":"{}"}]'),

-- 产品3：工业控制器
(3, '测试PLC产品', 'MQTT', 103, '工业控制器', 1, 'IoT物联', 0, 0,
 'plc_test', 'P_plc_123456', 'K_plc_secret_003',
 2, 1, 1, 1, 'MQTT', 1, '/profile/iot/3.png', '0',
 'admin', NOW(), 'admin', NOW(), 'PLC产品，用于工业自动化控制',
 '[{"identifier":"production_count","name":"生产计数","type":1,"datatype":"integer","specs":"{\"min\":0,\"max\":999999,\"unit\":\"个\"}"},{"identifier":"machine_pressure","name":"机器压力","type":1,"datatype":"decimal","specs":"{\"min\":0,\"max\":10,\"unit\":\"MPa\"}"},{"identifier":"machine_temperature","name":"机器温度","type":1,"datatype":"decimal","specs":"{\"min\":0,\"max\":150,\"unit\":\"℃\"}"},{"identifier":"set_target","name":"设置目标","type":2,"datatype":"integer","specs":"{\"min\":1,\"max\":10000}"}]'),

-- 产品4：物联网网关
(4, '测试网关产品', 'MQTT', 104, '物联网网关', 1, 'IoT物联', 0, 0,
 'gateway_test', 'P_gateway_123456', 'K_gateway_secret_004',
 2, 2, 1, 1, 'MQTT', 1, '/profile/iot/4.png', '0',
 'admin', NOW(), 'admin', NOW(), '网关产品，用于设备接入和数据汇聚',
 '[{"identifier":"connected_devices","name":"连接设备数","type":1,"datatype":"integer","specs":"{\"min\":0,\"max\":100,\"unit\":\"个\"}"},{"identifier":"cpu_usage","name":"CPU使用率","type":1,"datatype":"decimal","specs":"{\"min\":0,\"max\":100,\"unit\":\"%\"}"},{"identifier":"memory_usage","name":"内存使用率","type":1,"datatype":"decimal","specs":"{\"min\":0,\"max\":100,\"unit\":\"%\"}"},{"identifier":"restart_service","name":"重启服务","type":2,"datatype":"string","specs":"{}"}]'),

-- 产品5：智能电表
(5, '测试电表产品', 'MQTT', 105, '智能电表', 1, 'IoT物联', 0, 0,
 'meter_test', 'P_meter_123456', 'K_meter_secret_005',
 2, 1, 1, 1, 'MQTT', 1, '/profile/iot/5.png', '0',
 'admin', NOW(), 'admin', NOW(), '电表产品，用于电力计量',
 '[{"identifier":"current_reading","name":"当前读数","type":1,"datatype":"decimal","specs":"{\"min\":0,\"max\":999999,\"unit\":\"kWh\"}"},{"identifier":"voltage","name":"电压","type":1,"datatype":"decimal","specs":"{\"min\":180,\"max\":260,\"unit\":\"V\"}"},{"identifier":"current","name":"电流","type":1,"datatype":"decimal","specs":"{\"min\":0,\"max\":100,\"unit\":\"A\"}"},{"identifier":"read_meter","name":"抄表","type":2,"datatype":"string","specs":"{}"}]')

ON DUPLICATE KEY UPDATE 
    product_name = VALUES(product_name),
    account = VALUES(account),
    auth_password = VALUES(auth_password),
    secret = VALUES(secret),
    update_time = VALUES(update_time),
    things_models_json = VALUES(things_models_json);

-- =================== 如果上面的新版本插入失败，使用旧版本字段 ===================
-- 如果遇到字段不存在错误，请注释掉上面的INSERT，使用下面的INSERT

/*
INSERT INTO iot_product (
    product_id, product_name, protocol_code, category_id, category_name, tenant_id, tenant_name, is_sys, is_authorize,
    mqtt_account, mqtt_password, mqtt_secret,
    status, device_type, network_method, vertificate_method, transport, location_way, img_url, del_flag,
    create_by, create_time, update_by, update_time, remark, things_models_json
) VALUES 
(1, '测试传感器产品', 'MQTT', 101, '智能传感器', 1, 'IoT物联', 0, 0, 
 'sensor_test', 'P_sensor_123456', 'K_sensor_secret_001', 
 2, 1, 1, 1, 'MQTT', 1, '/profile/iot/1.png', '0',
 'admin', NOW(), 'admin', NOW(), '传感器产品，用于环境监测',
 '[{"identifier":"temperature","name":"温度","type":1,"datatype":"decimal","specs":"{\"min\":-50,\"max\":100,\"unit\":\"℃\"}"}]'),
-- 其他产品...
*/

-- =================== 批量插入测试设备 - 修复版 ===================
-- 使用直接INSERT的方式，确保数据一定能插入成功

-- 传感器设备（75个，确保包含测试中使用的设备）
INSERT INTO iot_device (
    device_name, product_id, product_name, tenant_id, tenant_name, serial_number, 
    firmware_version, status, rssi, is_shadow, location_way, 
    network_address, network_ip, active_time, 
    del_flag, create_by, create_time, update_by, update_time, remark
) VALUES 
-- 前25个传感器设备
('测试传感器设备001', 1, '测试传感器产品', 1, 'IoT物联', 'TEST_SENSOR000001', 1.0, 3, -65, 1, 1, '测试环境', '192.168.1.100', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试传感器设备'),
('测试传感器设备002', 1, '测试传感器产品', 1, 'IoT物联', 'TEST_SENSOR000002', 1.0, 3, -65, 1, 1, '测试环境', '192.168.1.100', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试传感器设备'),
('测试传感器设备003', 1, '测试传感器产品', 1, 'IoT物联', 'TEST_SENSOR000003', 1.0, 3, -65, 1, 1, '测试环境', '192.168.1.100', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试传感器设备'),
('测试传感器设备004', 1, '测试传感器产品', 1, 'IoT物联', 'TEST_SENSOR000004', 1.0, 3, -65, 1, 1, '测试环境', '192.168.1.100', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试传感器设备'),
('测试传感器设备005', 1, '测试传感器产品', 1, 'IoT物联', 'TEST_SENSOR000005', 1.0, 3, -65, 1, 1, '测试环境', '192.168.1.100', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试传感器设备'),
('测试传感器设备006', 1, '测试传感器产品', 1, 'IoT物联', 'TEST_SENSOR000006', 1.0, 3, -65, 1, 1, '测试环境', '192.168.1.100', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试传感器设备'),
('测试传感器设备007', 1, '测试传感器产品', 1, 'IoT物联', 'TEST_SENSOR000007', 1.0, 3, -65, 1, 1, '测试环境', '192.168.1.100', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试传感器设备'),
('测试传感器设备008', 1, '测试传感器产品', 1, 'IoT物联', 'TEST_SENSOR000008', 1.0, 3, -65, 1, 1, '测试环境', '192.168.1.100', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试传感器设备'),
('测试传感器设备009', 1, '测试传感器产品', 1, 'IoT物联', 'TEST_SENSOR000009', 1.0, 3, -65, 1, 1, '测试环境', '192.168.1.100', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试传感器设备'),
('测试传感器设备010', 1, '测试传感器产品', 1, 'IoT物联', 'TEST_SENSOR000010', 1.0, 3, -65, 1, 1, '测试环境', '192.168.1.100', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试传感器设备'),
('测试传感器设备011', 1, '测试传感器产品', 1, 'IoT物联', 'TEST_SENSOR000011', 1.0, 3, -65, 1, 1, '测试环境', '192.168.1.100', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试传感器设备'),
('测试传感器设备012', 1, '测试传感器产品', 1, 'IoT物联', 'TEST_SENSOR000012', 1.0, 3, -65, 1, 1, '测试环境', '192.168.1.100', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试传感器设备'),
('测试传感器设备013', 1, '测试传感器产品', 1, 'IoT物联', 'TEST_SENSOR000013', 1.0, 3, -65, 1, 1, '测试环境', '192.168.1.100', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试传感器设备'),
('测试传感器设备014', 1, '测试传感器产品', 1, 'IoT物联', 'TEST_SENSOR000014', 1.0, 3, -65, 1, 1, '测试环境', '192.168.1.100', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试传感器设备'),
('测试传感器设备015', 1, '测试传感器产品', 1, 'IoT物联', 'TEST_SENSOR000015', 1.0, 3, -65, 1, 1, '测试环境', '192.168.1.100', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试传感器设备'),
('测试传感器设备016', 1, '测试传感器产品', 1, 'IoT物联', 'TEST_SENSOR000016', 1.0, 3, -65, 1, 1, '测试环境', '192.168.1.100', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试传感器设备'),
('测试传感器设备017', 1, '测试传感器产品', 1, 'IoT物联', 'TEST_SENSOR000017', 1.0, 3, -65, 1, 1, '测试环境', '192.168.1.100', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试传感器设备'),
('测试传感器设备018', 1, '测试传感器产品', 1, 'IoT物联', 'TEST_SENSOR000018', 1.0, 3, -65, 1, 1, '测试环境', '192.168.1.100', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试传感器设备'),
('测试传感器设备019', 1, '测试传感器产品', 1, 'IoT物联', 'TEST_SENSOR000019', 1.0, 3, -65, 1, 1, '测试环境', '192.168.1.100', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试传感器设备'),
('测试传感器设备020', 1, '测试传感器产品', 1, 'IoT物联', 'TEST_SENSOR000020', 1.0, 3, -65, 1, 1, '测试环境', '192.168.1.100', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试传感器设备'),
('测试传感器设备021', 1, '测试传感器产品', 1, 'IoT物联', 'TEST_SENSOR000021', 1.0, 3, -65, 1, 1, '测试环境', '192.168.1.100', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试传感器设备'),
('测试传感器设备022', 1, '测试传感器产品', 1, 'IoT物联', 'TEST_SENSOR000022', 1.0, 3, -65, 1, 1, '测试环境', '192.168.1.100', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试传感器设备'),
('测试传感器设备023', 1, '测试传感器产品', 1, 'IoT物联', 'TEST_SENSOR000023', 1.0, 3, -65, 1, 1, '测试环境', '192.168.1.100', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试传感器设备'),
('测试传感器设备024', 1, '测试传感器产品', 1, 'IoT物联', 'TEST_SENSOR000024', 1.0, 3, -65, 1, 1, '测试环境', '192.168.1.100', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试传感器设备'),
('测试传感器设备025', 1, '测试传感器产品', 1, 'IoT物联', 'TEST_SENSOR000025', 1.0, 3, -65, 1, 1, '测试环境', '192.168.1.100', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试传感器设备'),

-- 中间的传感器设备（包含测试中使用的序列号）
('测试传感器设备070', 1, '测试传感器产品', 1, 'IoT物联', 'TEST_SENSOR000070', 1.0, 3, -65, 1, 1, '测试环境', '192.168.1.100', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试传感器设备'),
('测试传感器设备071', 1, '测试传感器产品', 1, 'IoT物联', 'TEST_SENSOR000071', 1.0, 3, -65, 1, 1, '测试环境', '192.168.1.100', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试传感器设备'),
('测试传感器设备072', 1, '测试传感器产品', 1, 'IoT物联', 'TEST_SENSOR000072', 1.0, 3, -65, 1, 1, '测试环境', '192.168.1.100', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试传感器设备'),
('测试传感器设备073', 1, '测试传感器产品', 1, 'IoT物联', 'TEST_SENSOR000073', 1.0, 3, -65, 1, 1, '测试环境', '192.168.1.100', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试传感器设备'),
('测试传感器设备074', 1, '测试传感器产品', 1, 'IoT物联', 'TEST_SENSOR000074', 1.0, 3, -65, 1, 1, '测试环境', '192.168.1.100', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试传感器设备'),
('测试传感器设备075', 1, '测试传感器产品', 1, 'IoT物联', 'TEST_SENSOR000075', 1.0, 3, -65, 1, 1, '测试环境', '192.168.1.100', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试传感器设备'),
('测试传感器设备076', 1, '测试传感器产品', 1, 'IoT物联', 'TEST_SENSOR000076', 1.0, 3, -65, 1, 1, '测试环境', '192.168.1.100', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试传感器设备'),
('测试传感器设备077', 1, '测试传感器产品', 1, 'IoT物联', 'TEST_SENSOR000077', 1.0, 3, -65, 1, 1, '测试环境', '192.168.1.100', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试传感器设备'),
('测试传感器设备078', 1, '测试传感器产品', 1, 'IoT物联', 'TEST_SENSOR000078', 1.0, 3, -65, 1, 1, '测试环境', '192.168.1.100', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试传感器设备'),
('测试传感器设备079', 1, '测试传感器产品', 1, 'IoT物联', 'TEST_SENSOR000079', 1.0, 3, -65, 1, 1, '测试环境', '192.168.1.100', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试传感器设备'),
('测试传感器设备080', 1, '测试传感器产品', 1, 'IoT物联', 'TEST_SENSOR000080', 1.0, 3, -65, 1, 1, '测试环境', '192.168.1.100', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试传感器设备'),

-- 后25个传感器设备
('测试传感器设备090', 1, '测试传感器产品', 1, 'IoT物联', 'TEST_SENSOR000090', 1.0, 3, -65, 1, 1, '测试环境', '192.168.1.100', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试传感器设备'),
('测试传感器设备091', 1, '测试传感器产品', 1, 'IoT物联', 'TEST_SENSOR000091', 1.0, 3, -65, 1, 1, '测试环境', '192.168.1.100', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试传感器设备'),
('测试传感器设备092', 1, '测试传感器产品', 1, 'IoT物联', 'TEST_SENSOR000092', 1.0, 3, -65, 1, 1, '测试环境', '192.168.1.100', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试传感器设备'),
('测试传感器设备093', 1, '测试传感器产品', 1, 'IoT物联', 'TEST_SENSOR000093', 1.0, 3, -65, 1, 1, '测试环境', '192.168.1.100', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试传感器设备'),
('测试传感器设备094', 1, '测试传感器产品', 1, 'IoT物联', 'TEST_SENSOR000094', 1.0, 3, -65, 1, 1, '测试环境', '192.168.1.100', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试传感器设备'),
('测试传感器设备095', 1, '测试传感器产品', 1, 'IoT物联', 'TEST_SENSOR000095', 1.0, 3, -65, 1, 1, '测试环境', '192.168.1.100', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试传感器设备'),
('测试传感器设备096', 1, '测试传感器产品', 1, 'IoT物联', 'TEST_SENSOR000096', 1.0, 3, -65, 1, 1, '测试环境', '192.168.1.100', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试传感器设备'),
('测试传感器设备097', 1, '测试传感器产品', 1, 'IoT物联', 'TEST_SENSOR000097', 1.0, 3, -65, 1, 1, '测试环境', '192.168.1.100', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试传感器设备'),
('测试传感器设备098', 1, '测试传感器产品', 1, 'IoT物联', 'TEST_SENSOR000098', 1.0, 3, -65, 1, 1, '测试环境', '192.168.1.100', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试传感器设备'),
('测试传感器设备099', 1, '测试传感器产品', 1, 'IoT物联', 'TEST_SENSOR000099', 1.0, 3, -65, 1, 1, '测试环境', '192.168.1.100', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试传感器设备'),
('测试传感器设备100', 1, '测试传感器产品', 1, 'IoT物联', 'TEST_SENSOR000100', 1.0, 3, -65, 1, 1, '测试环境', '192.168.1.100', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试传感器设备')
ON DUPLICATE KEY UPDATE 
    device_name = VALUES(device_name),
    update_time = VALUES(update_time),
    status = VALUES(status);

-- 摄像头设备（30个）
INSERT INTO iot_device (
    device_name, product_id, product_name, tenant_id, tenant_name, serial_number, 
    firmware_version, status, rssi, is_shadow, location_way, 
    network_address, network_ip, active_time, 
    del_flag, create_by, create_time, update_by, update_time, remark
) VALUES 
('测试摄像头设备001', 2, '测试摄像头产品', 1, 'IoT物联', 'TEST_CAMERA000001', 1.0, 3, -55, 1, 1, '测试环境', '192.168.1.101', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试摄像头设备'),
('测试摄像头设备002', 2, '测试摄像头产品', 1, 'IoT物联', 'TEST_CAMERA000002', 1.0, 3, -55, 1, 1, '测试环境', '192.168.1.101', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试摄像头设备'),
('测试摄像头设备003', 2, '测试摄像头产品', 1, 'IoT物联', 'TEST_CAMERA000003', 1.0, 3, -55, 1, 1, '测试环境', '192.168.1.101', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试摄像头设备'),
('测试摄像头设备004', 2, '测试摄像头产品', 1, 'IoT物联', 'TEST_CAMERA000004', 1.0, 3, -55, 1, 1, '测试环境', '192.168.1.101', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试摄像头设备'),
('测试摄像头设备005', 2, '测试摄像头产品', 1, 'IoT物联', 'TEST_CAMERA000005', 1.0, 3, -55, 1, 1, '测试环境', '192.168.1.101', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试摄像头设备'),
('测试摄像头设备006', 2, '测试摄像头产品', 1, 'IoT物联', 'TEST_CAMERA000006', 1.0, 3, -55, 1, 1, '测试环境', '192.168.1.101', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试摄像头设备'),
('测试摄像头设备007', 2, '测试摄像头产品', 1, 'IoT物联', 'TEST_CAMERA000007', 1.0, 3, -55, 1, 1, '测试环境', '192.168.1.101', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试摄像头设备'),
('测试摄像头设备008', 2, '测试摄像头产品', 1, 'IoT物联', 'TEST_CAMERA000008', 1.0, 3, -55, 1, 1, '测试环境', '192.168.1.101', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试摄像头设备'),
('测试摄像头设备009', 2, '测试摄像头产品', 1, 'IoT物联', 'TEST_CAMERA000009', 1.0, 3, -55, 1, 1, '测试环境', '192.168.1.101', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试摄像头设备'),
('测试摄像头设备010', 2, '测试摄像头产品', 1, 'IoT物联', 'TEST_CAMERA000010', 1.0, 3, -55, 1, 1, '测试环境', '192.168.1.101', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试摄像头设备'),
('测试摄像头设备011', 2, '测试摄像头产品', 1, 'IoT物联', 'TEST_CAMERA000011', 1.0, 3, -55, 1, 1, '测试环境', '192.168.1.101', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试摄像头设备'),
('测试摄像头设备012', 2, '测试摄像头产品', 1, 'IoT物联', 'TEST_CAMERA000012', 1.0, 3, -55, 1, 1, '测试环境', '192.168.1.101', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试摄像头设备'),
('测试摄像头设备013', 2, '测试摄像头产品', 1, 'IoT物联', 'TEST_CAMERA000013', 1.0, 3, -55, 1, 1, '测试环境', '192.168.1.101', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试摄像头设备'),
('测试摄像头设备014', 2, '测试摄像头产品', 1, 'IoT物联', 'TEST_CAMERA000014', 1.0, 3, -55, 1, 1, '测试环境', '192.168.1.101', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试摄像头设备'),
('测试摄像头设备015', 2, '测试摄像头产品', 1, 'IoT物联', 'TEST_CAMERA000015', 1.0, 3, -55, 1, 1, '测试环境', '192.168.1.101', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试摄像头设备'),
('测试摄像头设备016', 2, '测试摄像头产品', 1, 'IoT物联', 'TEST_CAMERA000016', 1.0, 3, -55, 1, 1, '测试环境', '192.168.1.101', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试摄像头设备'),
('测试摄像头设备017', 2, '测试摄像头产品', 1, 'IoT物联', 'TEST_CAMERA000017', 1.0, 3, -55, 1, 1, '测试环境', '192.168.1.101', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试摄像头设备'),
('测试摄像头设备018', 2, '测试摄像头产品', 1, 'IoT物联', 'TEST_CAMERA000018', 1.0, 3, -55, 1, 1, '测试环境', '192.168.1.101', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试摄像头设备'),
('测试摄像头设备019', 2, '测试摄像头产品', 1, 'IoT物联', 'TEST_CAMERA000019', 1.0, 3, -55, 1, 1, '测试环境', '192.168.1.101', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试摄像头设备'),
('测试摄像头设备020', 2, '测试摄像头产品', 1, 'IoT物联', 'TEST_CAMERA000020', 1.0, 3, -55, 1, 1, '测试环境', '192.168.1.101', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试摄像头设备'),
('测试摄像头设备021', 2, '测试摄像头产品', 1, 'IoT物联', 'TEST_CAMERA000021', 1.0, 3, -55, 1, 1, '测试环境', '192.168.1.101', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试摄像头设备'),
('测试摄像头设备022', 2, '测试摄像头产品', 1, 'IoT物联', 'TEST_CAMERA000022', 1.0, 3, -55, 1, 1, '测试环境', '192.168.1.101', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试摄像头设备'),
('测试摄像头设备023', 2, '测试摄像头产品', 1, 'IoT物联', 'TEST_CAMERA000023', 1.0, 3, -55, 1, 1, '测试环境', '192.168.1.101', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试摄像头设备'),
('测试摄像头设备024', 2, '测试摄像头产品', 1, 'IoT物联', 'TEST_CAMERA000024', 1.0, 3, -55, 1, 1, '测试环境', '192.168.1.101', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试摄像头设备'),
('测试摄像头设备025', 2, '测试摄像头产品', 1, 'IoT物联', 'TEST_CAMERA000025', 1.0, 3, -55, 1, 1, '测试环境', '192.168.1.101', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试摄像头设备'),
('测试摄像头设备026', 2, '测试摄像头产品', 1, 'IoT物联', 'TEST_CAMERA000026', 1.0, 3, -55, 1, 1, '测试环境', '192.168.1.101', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试摄像头设备'),
('测试摄像头设备027', 2, '测试摄像头产品', 1, 'IoT物联', 'TEST_CAMERA000027', 1.0, 3, -55, 1, 1, '测试环境', '192.168.1.101', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试摄像头设备'),
('测试摄像头设备028', 2, '测试摄像头产品', 1, 'IoT物联', 'TEST_CAMERA000028', 1.0, 3, -55, 1, 1, '测试环境', '192.168.1.101', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试摄像头设备'),
('测试摄像头设备029', 2, '测试摄像头产品', 1, 'IoT物联', 'TEST_CAMERA000029', 1.0, 3, -55, 1, 1, '测试环境', '192.168.1.101', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试摄像头设备'),
('测试摄像头设备030', 2, '测试摄像头产品', 1, 'IoT物联', 'TEST_CAMERA000030', 1.0, 3, -55, 1, 1, '测试环境', '192.168.1.101', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试摄像头设备')
ON DUPLICATE KEY UPDATE 
    device_name = VALUES(device_name),
    update_time = VALUES(update_time),
    status = VALUES(status);

-- PLC设备（15个）
INSERT INTO iot_device (
    device_name, product_id, product_name, tenant_id, tenant_name, serial_number, 
    firmware_version, status, rssi, is_shadow, location_way, 
    network_address, network_ip, active_time, 
    del_flag, create_by, create_time, update_by, update_time, remark
) VALUES 
('测试PLC设备001', 3, '测试PLC产品', 1, 'IoT物联', 'TEST_PLC000001', 1.0, 3, -70, 0, 1, '工业园区', '192.168.1.102', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试PLC设备'),
('测试PLC设备002', 3, '测试PLC产品', 1, 'IoT物联', 'TEST_PLC000002', 1.0, 3, -70, 0, 1, '工业园区', '192.168.1.102', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试PLC设备'),
('测试PLC设备003', 3, '测试PLC产品', 1, 'IoT物联', 'TEST_PLC000003', 1.0, 3, -70, 0, 1, '工业园区', '192.168.1.102', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试PLC设备'),
('测试PLC设备004', 3, '测试PLC产品', 1, 'IoT物联', 'TEST_PLC000004', 1.0, 3, -70, 0, 1, '工业园区', '192.168.1.102', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试PLC设备'),
('测试PLC设备005', 3, '测试PLC产品', 1, 'IoT物联', 'TEST_PLC000005', 1.0, 3, -70, 0, 1, '工业园区', '192.168.1.102', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试PLC设备'),
('测试PLC设备006', 3, '测试PLC产品', 1, 'IoT物联', 'TEST_PLC000006', 1.0, 3, -70, 0, 1, '工业园区', '192.168.1.102', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试PLC设备'),
('测试PLC设备007', 3, '测试PLC产品', 1, 'IoT物联', 'TEST_PLC000007', 1.0, 3, -70, 0, 1, '工业园区', '192.168.1.102', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试PLC设备'),
('测试PLC设备008', 3, '测试PLC产品', 1, 'IoT物联', 'TEST_PLC000008', 1.0, 3, -70, 0, 1, '工业园区', '192.168.1.102', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试PLC设备'),
('测试PLC设备009', 3, '测试PLC产品', 1, 'IoT物联', 'TEST_PLC000009', 1.0, 3, -70, 0, 1, '工业园区', '192.168.1.102', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试PLC设备'),
('测试PLC设备010', 3, '测试PLC产品', 1, 'IoT物联', 'TEST_PLC000010', 1.0, 3, -70, 0, 1, '工业园区', '192.168.1.102', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试PLC设备'),
('测试PLC设备011', 3, '测试PLC产品', 1, 'IoT物联', 'TEST_PLC000011', 1.0, 3, -70, 0, 1, '工业园区', '192.168.1.102', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试PLC设备'),
('测试PLC设备012', 3, '测试PLC产品', 1, 'IoT物联', 'TEST_PLC000012', 1.0, 3, -70, 0, 1, '工业园区', '192.168.1.102', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试PLC设备'),
('测试PLC设备013', 3, '测试PLC产品', 1, 'IoT物联', 'TEST_PLC000013', 1.0, 3, -70, 0, 1, '工业园区', '192.168.1.102', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试PLC设备'),
('测试PLC设备014', 3, '测试PLC产品', 1, 'IoT物联', 'TEST_PLC000014', 1.0, 3, -70, 0, 1, '工业园区', '192.168.1.102', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试PLC设备'),
('测试PLC设备015', 3, '测试PLC产品', 1, 'IoT物联', 'TEST_PLC000015', 1.0, 3, -70, 0, 1, '工业园区', '192.168.1.102', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试PLC设备')
ON DUPLICATE KEY UPDATE 
    device_name = VALUES(device_name),
    update_time = VALUES(update_time),
    status = VALUES(status);

-- 网关设备（10个）
INSERT INTO iot_device (
    device_name, product_id, product_name, tenant_id, tenant_name, serial_number, 
    firmware_version, status, rssi, is_shadow, location_way, 
    network_address, network_ip, active_time, 
    del_flag, create_by, create_time, update_by, update_time, remark
) VALUES 
('测试网关设备001', 4, '测试网关产品', 1, 'IoT物联', 'TEST_GATEWAY000001', 1.0, 3, -50, 1, 1, '数据中心', '192.168.1.103', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试网关设备'),
('测试网关设备002', 4, '测试网关产品', 1, 'IoT物联', 'TEST_GATEWAY000002', 1.0, 3, -50, 1, 1, '数据中心', '192.168.1.103', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试网关设备'),
('测试网关设备003', 4, '测试网关产品', 1, 'IoT物联', 'TEST_GATEWAY000003', 1.0, 3, -50, 1, 1, '数据中心', '192.168.1.103', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试网关设备'),
('测试网关设备004', 4, '测试网关产品', 1, 'IoT物联', 'TEST_GATEWAY000004', 1.0, 3, -50, 1, 1, '数据中心', '192.168.1.103', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试网关设备'),
('测试网关设备005', 4, '测试网关产品', 1, 'IoT物联', 'TEST_GATEWAY000005', 1.0, 3, -50, 1, 1, '数据中心', '192.168.1.103', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试网关设备'),
('测试网关设备006', 4, '测试网关产品', 1, 'IoT物联', 'TEST_GATEWAY000006', 1.0, 3, -50, 1, 1, '数据中心', '192.168.1.103', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试网关设备'),
('测试网关设备007', 4, '测试网关产品', 1, 'IoT物联', 'TEST_GATEWAY000007', 1.0, 3, -50, 1, 1, '数据中心', '192.168.1.103', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试网关设备'),
('测试网关设备008', 4, '测试网关产品', 1, 'IoT物联', 'TEST_GATEWAY000008', 1.0, 3, -50, 1, 1, '数据中心', '192.168.1.103', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试网关设备'),
('测试网关设备009', 4, '测试网关产品', 1, 'IoT物联', 'TEST_GATEWAY000009', 1.0, 3, -50, 1, 1, '数据中心', '192.168.1.103', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试网关设备'),
('测试网关设备010', 4, '测试网关产品', 1, 'IoT物联', 'TEST_GATEWAY000010', 1.0, 3, -50, 1, 1, '数据中心', '192.168.1.103', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试网关设备')
ON DUPLICATE KEY UPDATE 
    device_name = VALUES(device_name),
    update_time = VALUES(update_time),
    status = VALUES(status);

-- 电表设备（20个）
INSERT INTO iot_device (
    device_name, product_id, product_name, tenant_id, tenant_name, serial_number, 
    firmware_version, status, rssi, is_shadow, location_way, 
    network_address, network_ip, active_time, 
    del_flag, create_by, create_time, update_by, update_time, remark
) VALUES 
('测试电表设备001', 5, '测试电表产品', 1, 'IoT物联', 'TEST_METER000001', 1.0, 3, -60, 1, 1, '办公楼', '192.168.1.104', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试电表设备'),
('测试电表设备002', 5, '测试电表产品', 1, 'IoT物联', 'TEST_METER000002', 1.0, 3, -60, 1, 1, '办公楼', '192.168.1.104', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试电表设备'),
('测试电表设备003', 5, '测试电表产品', 1, 'IoT物联', 'TEST_METER000003', 1.0, 3, -60, 1, 1, '办公楼', '192.168.1.104', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试电表设备'),
('测试电表设备004', 5, '测试电表产品', 1, 'IoT物联', 'TEST_METER000004', 1.0, 3, -60, 1, 1, '办公楼', '192.168.1.104', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试电表设备'),
('测试电表设备005', 5, '测试电表产品', 1, 'IoT物联', 'TEST_METER000005', 1.0, 3, -60, 1, 1, '办公楼', '192.168.1.104', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试电表设备'),
('测试电表设备006', 5, '测试电表产品', 1, 'IoT物联', 'TEST_METER000006', 1.0, 3, -60, 1, 1, '办公楼', '192.168.1.104', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试电表设备'),
('测试电表设备007', 5, '测试电表产品', 1, 'IoT物联', 'TEST_METER000007', 1.0, 3, -60, 1, 1, '办公楼', '192.168.1.104', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试电表设备'),
('测试电表设备008', 5, '测试电表产品', 1, 'IoT物联', 'TEST_METER000008', 1.0, 3, -60, 1, 1, '办公楼', '192.168.1.104', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试电表设备'),
('测试电表设备009', 5, '测试电表产品', 1, 'IoT物联', 'TEST_METER000009', 1.0, 3, -60, 1, 1, '办公楼', '192.168.1.104', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试电表设备'),
('测试电表设备010', 5, '测试电表产品', 1, 'IoT物联', 'TEST_METER000010', 1.0, 3, -60, 1, 1, '办公楼', '192.168.1.104', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试电表设备'),
('测试电表设备011', 5, '测试电表产品', 1, 'IoT物联', 'TEST_METER000011', 1.0, 3, -60, 1, 1, '办公楼', '192.168.1.104', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试电表设备'),
('测试电表设备012', 5, '测试电表产品', 1, 'IoT物联', 'TEST_METER000012', 1.0, 3, -60, 1, 1, '办公楼', '192.168.1.104', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试电表设备'),
('测试电表设备013', 5, '测试电表产品', 1, 'IoT物联', 'TEST_METER000013', 1.0, 3, -60, 1, 1, '办公楼', '192.168.1.104', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试电表设备'),
('测试电表设备014', 5, '测试电表产品', 1, 'IoT物联', 'TEST_METER000014', 1.0, 3, -60, 1, 1, '办公楼', '192.168.1.104', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试电表设备'),
('测试电表设备015', 5, '测试电表产品', 1, 'IoT物联', 'TEST_METER000015', 1.0, 3, -60, 1, 1, '办公楼', '192.168.1.104', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试电表设备'),
('测试电表设备016', 5, '测试电表产品', 1, 'IoT物联', 'TEST_METER000016', 1.0, 3, -60, 1, 1, '办公楼', '192.168.1.104', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试电表设备'),
('测试电表设备017', 5, '测试电表产品', 1, 'IoT物联', 'TEST_METER000017', 1.0, 3, -60, 1, 1, '办公楼', '192.168.1.104', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试电表设备'),
('测试电表设备018', 5, '测试电表产品', 1, 'IoT物联', 'TEST_METER000018', 1.0, 3, -60, 1, 1, '办公楼', '192.168.1.104', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试电表设备'),
('测试电表设备019', 5, '测试电表产品', 1, 'IoT物联', 'TEST_METER000019', 1.0, 3, -60, 1, 1, '办公楼', '192.168.1.104', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试电表设备'),
('测试电表设备020', 5, '测试电表产品', 1, 'IoT物联', 'TEST_METER000020', 1.0, 3, -60, 1, 1, '办公楼', '192.168.1.104', NOW(), '0', 'admin', NOW(), 'admin', NOW(), '压力测试电表设备')
ON DUPLICATE KEY UPDATE 
    device_name = VALUES(device_name),
    update_time = VALUES(update_time),
    status = VALUES(status);

-- =================== 恢复外键检查 ===================
SET FOREIGN_KEY_CHECKS = 1;

-- =================== 数据统计和验证 ===================
SELECT '=== 测试数据初始化完成 ===' as status;

SELECT 
    '产品分类' as data_type, 
    COUNT(*) as count 
FROM iot_category 
WHERE category_name LIKE '%测试%' OR category_name LIKE '%智能%'

UNION ALL

SELECT 
    '测试产品' as data_type, 
    COUNT(*) as count 
FROM iot_product 
WHERE product_name LIKE '%测试%'

UNION ALL

SELECT 
    '测试设备' as data_type, 
    COUNT(*) as count 
FROM iot_device 
WHERE device_name LIKE '%测试%' OR serial_number LIKE 'TEST_%';

-- =================== 显示设备分布统计 ===================
SELECT 
    p.product_name,
    COUNT(d.device_id) as device_count,
    -- 兼容新旧版本字段
    COALESCE(p.account, p.mqtt_account) as mqtt_account,
    COALESCE(p.auth_password, p.mqtt_password) as mqtt_password,
    p.transport
FROM iot_product p
LEFT JOIN iot_device d ON p.product_id = d.product_id
WHERE p.product_name LIKE '%测试%'
GROUP BY p.product_id, p.product_name, COALESCE(p.account, p.mqtt_account), COALESCE(p.auth_password, p.mqtt_password), p.transport
ORDER BY p.product_id;

-- =================== 完成提示 ===================
SELECT '🎉 SYDH IoT 压力测试数据初始化完成！' as message,
       '✅ 数据包含：5个产品分类、5个测试产品、101个测试设备' as details,
       '🚀 现在可以运行 locustfile.py 和 mqtt_locust_test.py 进行压力测试' as next_step;

-- =================== 关键设备验证 ===================
SELECT '📝 验证关键测试设备是否存在：' as verification_title;

SELECT 
    CASE 
        WHEN COUNT(*) > 0 THEN '✅ 设备存在'
        ELSE '❌ 设备不存在'
    END as status,
    'TEST_SENSOR000075' as device_serial,
    COUNT(*) as found_count
FROM iot_device 
WHERE serial_number = 'TEST_SENSOR000075';

-- =================== 最终设备清单 ===================
SELECT 
    '传感器设备' as device_type,
    COUNT(*) as count,
    MIN(serial_number) as first_device,
    MAX(serial_number) as last_device
FROM iot_device 
WHERE serial_number LIKE 'TEST_SENSOR%'

UNION ALL

SELECT 
    '摄像头设备' as device_type,
    COUNT(*) as count,
    MIN(serial_number) as first_device,
    MAX(serial_number) as last_device
FROM iot_device 
WHERE serial_number LIKE 'TEST_CAMERA%'

UNION ALL

SELECT 
    'PLC设备' as device_type,
    COUNT(*) as count,
    MIN(serial_number) as first_device,
    MAX(serial_number) as last_device
FROM iot_device 
WHERE serial_number LIKE 'TEST_PLC%'

UNION ALL

SELECT 
    '网关设备' as device_type,
    COUNT(*) as count,
    MIN(serial_number) as first_device,
    MAX(serial_number) as last_device
FROM iot_device 
WHERE serial_number LIKE 'TEST_GATEWAY%'

UNION ALL

SELECT 
    '电表设备' as device_type,
    COUNT(*) as count,
    MIN(serial_number) as first_device,
    MAX(serial_number) as last_device
FROM iot_device 
WHERE serial_number LIKE 'TEST_METER%'; 