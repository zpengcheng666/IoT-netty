package com.sydh.iot.data.job;

import com.sydh.base.service.ISessionStore;
import com.sydh.base.session.Session;
import com.sydh.common.enums.DeviceStatus;
import com.sydh.common.extend.core.domin.mq.DeviceStatusBo;
import com.sydh.common.utils.DateUtils;
import com.sydh.iot.data.service.IMqttMessagePublish;
import com.sydh.iot.domain.Device;
import com.sydh.iot.model.vo.DeviceStatusVO;
import com.sydh.iot.service.IDeviceService;
import com.sydh.mq.producer.MessageProducer;
import com.sydh.mqtt.manager.SessionManger;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * 定时同步设备状态 -- netty版本mqtt使用
 * @author gsb
 * @date 2024/4/11 10:33
 */
@Slf4j
@Component
public class SyncDeviceStatusJob {

    @Resource
    private IDeviceService deviceService;
    @Resource
    private IMqttMessagePublish mqttMessagePublish;
    @Value("${server.broker.enabled}")
    private Boolean enabled;
    @Resource
    private ISessionStore sessionStore;

    @Value("${server.modbus-tcp.poll}")
    private Long poll;

    /**
     * 定期同步设备状态
     *  1.将异常在线设备变更为离线状态
     *  2.将离线设备但实际在线设备变更为在线
     */
    public void syncDeviceStatus() {
        if (enabled) {
            //获取所有已激活并不是禁用的设备
            List<DeviceStatusVO> deviceStatusVOList = deviceService.selectDeviceActive();
            if (!CollectionUtils.isEmpty(deviceStatusVOList)) {
                for (DeviceStatusVO statusVO : deviceStatusVOList) {
                    Session session = SessionManger.getSession(statusVO.getSerialNumber());
                    Device device = new Device();
                    device.setSerialNumber(statusVO.getSerialNumber());
                    device.setRssi(statusVO.getRssi());
                    device.setProductId(statusVO.getProductId());
                    device.setIsShadow(statusVO.getIsShadow());
                    // 如果session中设备在线，数据库状态离线 ,则更新设备的状态为在线
                    if (!Objects.isNull(session) && statusVO.getStatus() == DeviceStatus.OFFLINE.getType()) {
                        log.warn("设备上线: " + statusVO.getSerialNumber());
                        device.setStatus(DeviceStatus.ONLINE.getType());
                        deviceService.updateDeviceStatus(device);
                        mqttMessagePublish.pushDeviceStatus(device, DeviceStatus.ONLINE);
                    }
                    if (Objects.isNull(session) && statusVO.getStatus() == DeviceStatus.ONLINE.getType()) {
                        log.warn("设备下线: " + statusVO.getSerialNumber());
                        device.setStatus(DeviceStatus.OFFLINE.getType());
                        deviceService.updateDeviceStatus(device);
                        mqttMessagePublish.pushDeviceStatus(device, DeviceStatus.OFFLINE);
                    }
                }
            }
        }
        //定时检测modbusTCP的状态
        List<DeviceStatusVO> modbusTcpDeviceList = deviceService.selectModbusTcpDevice();
        if (!CollectionUtils.isEmpty(modbusTcpDeviceList)) {
            for (DeviceStatusVO statusVO : modbusTcpDeviceList) {
                Session session = SessionManger.getSession(statusVO.getSerialNumber());
                if (Objects.isNull(session)) continue;
                //离线判断4分钟
                long deterTime = (long) ((double) poll /1000 * 1.5);
                long lastAccessTime = session.getLastAccessTime();
                long time = (System.currentTimeMillis() - lastAccessTime) / 1000;
                if (time > deterTime) {
                    //处理设备离线
                    DeviceStatusBo statusBo = new DeviceStatusBo()
                            .setStatus(DeviceStatus.OFFLINE)
                            .setSerialNumber(statusVO.getSerialNumber())
                            .setTimestamp(DateUtils.getNowDate());
                    MessageProducer.sendStatusMsg(statusBo);
                    sessionStore.cleanSession(statusVO.getSerialNumber());
                }

            }
        }
    }
}
