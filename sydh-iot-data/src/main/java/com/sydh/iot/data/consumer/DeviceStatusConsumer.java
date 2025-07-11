package com.sydh.iot.data.consumer;

import com.alibaba.fastjson2.JSONObject;
import com.sydh.base.service.ISessionStore;
import com.sydh.common.enums.DeviceStatus;
import com.sydh.common.extend.core.domin.mq.DeviceStatusBo;
import com.sydh.common.extend.core.domin.mq.MQSendMessageBo;
import com.sydh.common.extend.core.domin.mq.message.ReportDataBo;
import com.sydh.common.extend.core.domin.thingsModel.ThingsModelSimpleItem;
import com.sydh.iot.cache.IDeviceCache;
import com.sydh.iot.data.service.IMqttMessagePublish;
import com.sydh.iot.data.service.IRuleEngine;
import com.sydh.iot.domain.Device;
import com.sydh.iot.domain.Product;
import com.sydh.iot.domain.SubGateway;
import com.sydh.iot.model.ThingsModels.ThingsModelShadow;
import com.sydh.iot.service.IDeviceService;
import com.sydh.iot.service.IProductService;
import com.sydh.iot.service.ISubGatewayService;
import com.sydh.iot.util.SnowflakeIdWorker;
import com.sydh.mq.producer.MessageProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 设备状态消息处理类
 *
 * @author gsb
 * @date 2022/10/10 11:02
 */
@Slf4j
@Component
public class DeviceStatusConsumer {

    @Autowired
    private IDeviceCache deviceCache;
    @Resource
    private IRuleEngine ruleEngine;
    @Resource
    private IDeviceService deviceService;
    @Resource
    private IProductService productService;
    @Resource
    private ISessionStore sessionStore;
    @Resource
    private IMqttMessagePublish mqttMessagePublish;
    @Value("${server.broker.enabled}")
    private Boolean enabled;
    @Resource
    private ISubGatewayService subGatewayService;

    private SnowflakeIdWorker snowflakeIdWorker = new SnowflakeIdWorker(2);

    public synchronized void consume(DeviceStatusBo bo) {
        try {
            List<DeviceStatusBo> list = new ArrayList<>();
            List<SubGateway> subDeviceList = subGatewayService.getSubDeviceListByGw(bo.getSerialNumber());
            if (!CollectionUtils.isEmpty(subDeviceList) && bo.getStatus()== DeviceStatus.OFFLINE){
                for (SubGateway subGateway : subDeviceList) {
                    DeviceStatusBo deviceStatusBo = new DeviceStatusBo();
                    BeanUtils.copyProperties(bo, deviceStatusBo);
                    deviceStatusBo.setSerialNumber(subGateway.getSubClientId());
                    list.add(deviceStatusBo);
                }
            }
            list.add(bo);
            for (DeviceStatusBo statusBo : list) {
                DeviceStatus status = statusBo.getStatus();
                Device device = deviceService.selectDeviceBySerialNumber(statusBo.getSerialNumber());
                Product product = productService.selectProductByProductId(device.getProductId());
                //如果使用Netty版本 监控设备不走这里
                if (enabled && product.getDeviceType() != 3) {
                    boolean containsKey = sessionStore.containsKey(statusBo.getSerialNumber());
                    boolean isOnline = device.getStatus() == 3;
                    log.info("=>session：{},数据库：{}，更新状态:{}", containsKey, isOnline, bo.getStatus().getCode());
                    if (containsKey && !isOnline) {
                        //如果session存在，但数据库状态不在线，则以session为准
                        statusBo.setStatus(DeviceStatus.ONLINE);
                    }
                    if (!containsKey && isOnline) {
                        statusBo.setStatus(DeviceStatus.OFFLINE);
                    }
                    if (statusBo.getSerialNumber().equals(bo.getSerialNumber()) && DeviceStatus.FORBIDDEN.equals(bo.getStatus())) {
                        statusBo.setStatus(DeviceStatus.FORBIDDEN);
                    }
                }

                /*更新设备状态*/
                deviceCache.updateDeviceStatusCache(statusBo, device);
                //处理影子模式值
                this.handlerShadow(device, status);
                //设备上下线执行规则引擎
                ReportDataBo dataBo = new ReportDataBo();
                dataBo.setRuleEngine(true);
                dataBo.setProductId(device.getProductId());
                dataBo.setType(status.equals(DeviceStatus.ONLINE) ? 5 : 6);
                dataBo.setSerialNumber(statusBo.getSerialNumber());
                ruleEngine.ruleMatch(dataBo);
                //推送到前端
                mqttMessagePublish.pushDeviceStatus(device, status);
            }
        } catch (Exception e) {
            log.error("=>设备状态处理异常", e);
        }
    }

    private void handlerShadow(Device device, DeviceStatus status) {
        //获取设备协议编码
//        DeviceAndProtocol dp = deviceService.selectProtocolBySerialNumber(device.getSerialNumber());
        /* 设备上线 处理影子值*/
        if (status.equals(DeviceStatus.ONLINE) && device.getIsShadow() == 1) {
            ThingsModelShadow shadow = deviceService.getDeviceShadowThingsModel(device);
            List<ThingsModelSimpleItem> properties = shadow.getProperties();
            List<ThingsModelSimpleItem> functions = shadow.getFunctions();
            functions.addAll(properties);
            if (!CollectionUtils.isEmpty(functions)) {
                for (ThingsModelSimpleItem function : functions) {
                    MQSendMessageBo bo = new MQSendMessageBo();
//                    bo.setDp(dp);
                    bo.setIsShadow(Boolean.FALSE);
                    bo.setIdentifier(function.getId());
                    bo.setSerialNumber(device.getSerialNumber());
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(function.getId(), function.getValue());
                    bo.setParams(jsonObject);
                    bo.setValue(function.getValue());
                    bo.setDelay(1000L);
                    long id = snowflakeIdWorker.nextId();
                    bo.setMessageId(id + "");
                    //发送到MQ处理
                    MessageProducer.sendFunctionInvoke(bo);
                }
            }
        }
    }
}
