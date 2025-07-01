ALTER TABLE `iot_device`
    ADD COLUMN `device_port` int(10) NULL DEFAULT NULL COMMENT '端口';

ALTER TABLE `iot_product_modbus_job`
    MODIFY COLUMN `command` text CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL COMMENT '指令' ;

ALTER TABLE `iot_device`
    ADD COLUMN `device_ip` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '设备主机ip';