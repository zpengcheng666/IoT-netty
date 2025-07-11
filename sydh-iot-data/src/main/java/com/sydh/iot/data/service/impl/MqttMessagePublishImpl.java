package com.sydh.iot.data.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.sydh.common.core.redis.RedisCache;
import com.sydh.common.core.redis.RedisKeyBuilder;
import com.sydh.common.enums.DeviceStatus;
import com.sydh.common.enums.FunctionReplyStatus;
import com.sydh.common.enums.ServerType;
import com.sydh.common.enums.TopicType;
import com.sydh.common.exception.ServiceException;
import com.sydh.common.extend.core.domin.model.device.DeviceAndProtocol;
import com.sydh.common.extend.core.domin.mq.MQSendMessageBo;
import com.sydh.common.extend.core.domin.mq.message.*;
import com.sydh.common.extend.core.domin.mq.ota.OtaUpgradeBo;
import com.sydh.common.extend.core.domin.thingsModel.ThingsModelSimpleItem;
import com.sydh.common.extend.core.protocol.Message;
import com.sydh.common.extend.utils.modbus.ModbusUtils;
import com.sydh.common.utils.DateUtils;
import com.sydh.common.utils.MessageUtils;
import com.sydh.common.utils.StringUtils;
import com.sydh.common.utils.gateway.mq.TopicsUtils;
import com.sydh.iot.cache.IFirmwareCache;
import com.sydh.iot.cache.ITSLCache;
import com.sydh.iot.data.service.IDataHandler;
import com.sydh.iot.data.service.IFirmwareTaskDetailService;
import com.sydh.iot.data.service.IMqttMessagePublish;
import com.sydh.iot.domain.Device;
import com.sydh.iot.domain.FunctionLog;
import com.sydh.iot.domain.ModbusConfig;
import com.sydh.iot.enums.DeviceType;
import com.sydh.iot.mapper.ModbusParamsMapper;
import com.sydh.iot.model.NtpModel;
import com.sydh.iot.model.ThingsModels.ThingsModelValueItem;
import com.sydh.iot.service.IOrderControlService;
import com.sydh.modbus.tcp.model.ModbusCommand;
import com.sydh.modbus.tcp.write.ModbusClientManager;
import com.sydh.iot.ruleEngine.RuleProcess;
import com.sydh.iot.service.IDeviceService;
import com.sydh.iot.service.IFunctionLogService;
import com.sydh.iot.util.SnowflakeIdWorker;
import com.sydh.mqttclient.PubMqttClient;
import com.sydh.protocol.base.protocol.IProtocol;
import com.sydh.protocol.service.IProtocolManagerService;
import com.sydh.rule.context.MsgContext;
import com.sydh.sip.service.IGatewayService;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

import static com.sydh.common.constant.SYDHConstant.PROTOCOL.ModbusRtu;
import static com.sydh.common.constant.SYDHConstant.PROTOCOL.ModbusTcp;

/**
 * 消息推送方法集合
 *
 * @author bill
 */
@Slf4j
@Service
public class MqttMessagePublishImpl implements IMqttMessagePublish {

    @Resource
    private IProtocolManagerService protocolManagerService;
    @Resource
    private PubMqttClient mqttClient;
    @Resource
    private IFirmwareCache firmwareCache;
    @Resource
    private IFirmwareTaskDetailService firmwareTaskDetailService;
    @Resource
    private MessageManager messageManager;
    @Resource
    private TopicsUtils topicsUtils;
    @Resource
    private IDeviceService deviceService;
    @Resource
    private IFunctionLogService functionLogService;
    @Resource
    private IDataHandler dataHandler;
    @Resource
    private RuleProcess ruleProcess;
    @Autowired
    private IGatewayService gatewayService;
    @Resource
    private ITSLCache itslCache;
    @Resource
    private RedisCache redisCache;
    @Resource
    private PubMqttClient pubMqttClient;
    @Resource
    private ModbusParamsMapper modbusParamsMapper;
    @Resource
    private IOrderControlService orderControlService;

    @Resource
    private ModbusClientManager modbusClientManager;

    private final SnowflakeIdWorker snowflakeIdWorker = new SnowflakeIdWorker(3);


    /**
     * 服务(指令)下发
     */
    @Override
    public void funcSend(MQSendMessageBo bo) throws InterruptedException {
//        DeviceAndProtocol deviceAndProtocol = deviceService.selectProtocolBySerialNumber(bo.getSerialNumber());
        DeviceAndProtocol deviceProtocolDetail = deviceService.getDeviceProtocolDetail(bo.getSerialNumber());
        Optional.ofNullable(deviceProtocolDetail).orElseThrow(() -> new ServiceException(StringUtils.format(MessageUtils.message("mqtt.service.send.device.not.exist"), bo.getSerialNumber())));
        // 处理子设备，用绑定网关下发指令
        if (DeviceType.SUB_GATEWAY.getCode() == deviceProtocolDetail.getDeviceType()) {
            Optional.ofNullable(deviceProtocolDetail.getAddress()).orElseThrow(() -> new ServiceException(StringUtils.format(MessageUtils.message("mqtt.service.send.device.not.exist"), bo.getSerialNumber())));
            DeviceAndProtocol gwDeviceAndProtocol = deviceService.getDeviceProtocolDetail(deviceProtocolDetail.getGwSerialNumber());
            Optional.ofNullable(gwDeviceAndProtocol).orElseThrow(() -> new ServiceException(StringUtils.format(MessageUtils.message("mqtt.service.send.device.not.exist"), bo.getSerialNumber())));
            bo.setDp(gwDeviceAndProtocol);
            bo.setSerialNumber(gwDeviceAndProtocol.getSerialNumber());
            bo.setSubSerialNumber(deviceProtocolDetail.getSerialNumber());
        } else {
            bo.setDp(deviceProtocolDetail);
        }
        DeviceAndProtocol finalDeviceProtocol = bo.getDp();
        //获取物模型
        ThingsModelValueItem thingModels = itslCache.getSingleThingModels(deviceProtocolDetail.getProductId(), bo.getIdentifier());
        ModbusConfig modbusConfig = thingModels.getConfig();
        if (!Objects.isNull(modbusConfig)) {
            thingModels.getConfig().setModbusCode(ModbusUtils.getModbusCode(modbusConfig.getType()));
            if (deviceProtocolDetail.getDeviceType() == DeviceType.SUB_GATEWAY.getCode()) {
                //这里获取绑定网关时，设置的子设备地址
                thingModels.getConfig().setAddress(StringUtils.isNotEmpty(deviceProtocolDetail.getAddress()) ? deviceProtocolDetail.getAddress() : deviceProtocolDetail.getModbusAddress());
            } else if (deviceProtocolDetail.getDeviceType() == DeviceType.DIRECT_DEVICE.getCode()) {
                // 兼容老数据，寄存器没配从机地址
                if (StringUtils.isEmpty(thingModels.getConfig().getAddress())) {
                    thingModels.getConfig().setAddress(deviceProtocolDetail.getModbusAddress());
                }
            }
        }
        bo.setThingsModel(JSON.toJSONString(thingModels));
        Integer type = thingModels.getType();
        //处理影子模式
        this.hadndlerShadow(bo, type);
        //下发指令日志
        FunctionLog funcLog = this.handleLog(bo, thingModels.getName());
        funcLog.setUserId(deviceProtocolDetail.getTenantId());
        funcLog.setCreateBy(deviceProtocolDetail.getCreateBy());
        ServerType serverType = ServerType.explain(finalDeviceProtocol.getTransport());
        FunctionCallBackBo backBo = null;
        switch (serverType) {
            case MQTT:
                //  规则引擎脚本处理,完成后返回结果
                //组建下发服务指令
                backBo = buildMessage(bo);
                MsgContext context = ruleProcess.processRuleScript(bo.getSerialNumber(), 2, backBo.getTopicName(), new String(backBo.getMessage()));
                if (!Objects.isNull(context) && StringUtils.isNotEmpty(context.getPayload())
                        && StringUtils.isNotEmpty(context.getTopic())) {
                    backBo.setTopicName(context.getTopic());
                    backBo.setMessage(context.getPayload().getBytes());
                }

                publishWithLog(backBo.getTopicName(), backBo.getMessage(), funcLog, bo.getDelay());
                log.debug("=>服务下发,topic=[{}],指令=[{}]", backBo.getTopicName(), new String(backBo.getMessage()));
                break;
            case TCP:
                if (finalDeviceProtocol.getProtocolCode().equals(ModbusTcp)) {
                    //处理modbusTCP指令下发
                    writeModbusTcpCommand(thingModels, bo.getValue(),bo.getSerialNumber());
                }else {
                    //组建下发服务指令
                    backBo = buildMessage(bo);
                    Message data = new Message();
                    data.setPayload(Unpooled.wrappedBuffer(backBo.getMessage()));
                    data.setClientId(backBo.getSerialNumber());
                    messageManager.requestR(bo.getSerialNumber(), data, Message.class);
                    funcLog.setResultMsg("指令下发成功");
                    funcLog.setResultCode(203);
                    functionLogService.insertFunctionLog(funcLog);
                }
                break;
            case UDP:
                break;
            case COAP:
                break;
            case GB28181:
                MqttMessagePublishImpl.log.debug("=>功能指令下发,functinos=[{}]", bo);
                gatewayService.sendFunction(bo.getSerialNumber(), bo.getIdentifier(), bo.getParams().getString(bo.getIdentifier()));
                break;

        }
        //发送至前端数据调试
        String topic = topicsUtils.buildTopic(bo.getDp().getProductId(), bo.getSerialNumber(), TopicType.MESSAGE_POST);
        DeviceMessage deviceMessage = new DeviceMessage();
        deviceMessage.setMessage( backBo == null ? null :backBo.getSources());
        deviceMessage.setTime(new Date());
        deviceMessage.setTopicName(TopicType.FUNCTION_GET.getTopicSuffix());
        byte[] bytes = JSONObject.toJSONString(deviceMessage).getBytes();
        publishWithLog(topic, bytes, null, null);

        if (finalDeviceProtocol.getProtocolCode().equals(ModbusRtu)) {
            //这里做一个消息id标记消息下发顺序，如果设备指令带流水号，则不需要使用
            String cacheKey = RedisKeyBuilder.buildDownMessageIdCacheKey(bo.getSerialNumber());
            redisCache.zSetAdd(cacheKey, backBo.getSources() + ":" + bo.getMessageId(), DateUtils.getTimestampSeconds());
        }
        //处理指令下发权限问题
        deviceService.updateByOrder(bo.getUserId(), deviceProtocolDetail.getDeviceId());
    }


    private void writeModbusTcpCommand(ThingsModelValueItem thingModels, String value, String clientId) {
        ModbusConfig config = thingModels.getConfig();
        ModbusCommand writeCmd = new ModbusCommand();
        writeCmd.setCode(config.getModbusCode().getCode());
        writeCmd.setAddress(Integer.parseInt(config.getAddress()));
        writeCmd.setRegister(config.getRegister());
        writeCmd.setQuantity(1);
//        writeCmd.setData(new byte[]{Byte.parseByte(value)});
        List<Integer> values = new ArrayList<>();
        values.add(Integer.parseInt(value));
        writeCmd.setData(ModbusUtils.getModbusCommandData(values, writeCmd.getCode()));
        modbusClientManager.executeCommand(clientId,writeCmd);
    }


    /**
     * 处理影子模式
     */
    private void hadndlerShadow(MQSendMessageBo bo, int type) {
        //处理设备影子模式
        if (Boolean.TRUE.equals(bo.getIsShadow())) {
            List<ThingsModelSimpleItem> dataList = new ArrayList<>();
            bo.getParams().forEach((key, value) -> {
                ThingsModelSimpleItem item = new ThingsModelSimpleItem();
                item.setId(key);
                item.setValue(value + "");
                dataList.add(item);
            });
            ReportDataBo dataBo = new ReportDataBo();
            dataBo.setDataList(dataList)
                    .setProductId(bo.getDp().getProductId())
                    .setSerialNumber(bo.getSerialNumber())
                    .setRuleEngine(false)
                    .setShadow(true)
                    .setType(type);
            dataHandler.reportData(dataBo);
            return;
        }
    }

    /**
     * 处理下发指令日志
     *
     * @return
     */
    private FunctionLog handleLog(MQSendMessageBo message, String modelName) {
        /* 下发服务数据存储对象*/
        JSONObject params = message.getParams();
        String val = params.get(message.getIdentifier()) + "";
        message.setValue(val);
        FunctionLog funcLog = new FunctionLog();
        funcLog.setCreateTime(DateUtils.getNowDate());
        funcLog.setFunValue(val);
        funcLog.setMessageId(message.getMessageId());
        funcLog.setSerialNumber(message.getSerialNumber());
        funcLog.setIdentify(message.getIdentifier());
        funcLog.setShowValue(val);
        funcLog.setFunType(1);
        funcLog.setModelName(modelName);
        funcLog.setUserId(message.getUserId());
        return funcLog;
    }

    /**
     * OTA升级下发
     *
     * @param bo
     */
    @Override
    public void upGradeOTA(OtaUpgradeBo bo) {
        if (TopicType.FETCH_FIRMWARE_SET.getTopicSuffix().equals(bo.getTopicName())) {
            String topicName = topicsUtils.buildTopic(bo.getSerialNumber(), TopicType.FETCH_FIRMWARE_SET);
            FunctionLog log = new FunctionLog();
            log.setCreateTime(DateUtils.getNowDate());
            log.setSerialNumber(bo.getSerialNumber());
            log.setFunType(3);
            log.setIdentify("OTA");
            log.setShowValue(ByteBufUtil.hexDump(bo.getMsg()));
            mqttClient.publish(0, false, topicName, ByteBufUtil.hexDump(bo.getMsg()));
        } else if (TopicType.HTTP_FIRMWARE_SET.getTopicSuffix().equals(bo.getTopicName())) {
            String topicName = topicsUtils.buildTopic(bo.getSerialNumber(), TopicType.HTTP_FIRMWARE_SET);
            JSONObject message = new JSONObject();
            message.put("taskId", bo.getTaskId());
            message.put("url", bo.getUrl());
            message.put("version", bo.getVersion());
            message.put("status", bo.getStatus());
            mqttClient.publish(0, false, topicName, message.toJSONString());
        }

    }

    @Override
    public FunctionCallBackBo buildMessage(MQSendMessageBo bo) {
        String protocolCode = bo.getDp().getProtocolCode();
        Long productId = bo.getDp().getProductId();
        String serialNumber = bo.getSerialNumber();
        /*组建Topic*/
        String topic = topicsUtils.buildTopic(productId, serialNumber, TopicType.FUNCTION_GET);
        bo.setTopicName(topic);
        /*获取编码协议*/
        IProtocol protocolInstance = protocolManagerService.getProtocolByProtocolCode(protocolCode);
        //根据协议编码后数据
        FunctionCallBackBo callBackBo = protocolInstance.encode(bo);
        callBackBo.setSerialNumber(serialNumber);
        callBackBo.setTopicName(topic);
        return callBackBo;
    }

    /**
     * 1.发布设备状态
     */
    @Override
    public void publishStatus(Long productId, String deviceNum, int deviceStatus, int isShadow, int rssi) {
        String message = "{\"status\":" + deviceStatus + ",\"isShadow\":" + isShadow + ",\"rssi\":" + rssi + "}";
        String topic = topicsUtils.buildTopic(productId, deviceNum, TopicType.STATUS_POST);
        mqttClient.publish(1, false, topic, message);
    }


    /**
     * 2.发布设备信息
     */
    @Override
    public void publishInfo(Long productId, String deviceNum) {
        String topic = topicsUtils.buildTopic(productId, deviceNum, TopicType.INFO_GET);
        mqttClient.publish(1, false, topic, "");
    }

    /**
     * 3.发布时钟同步信息
     *
     * @param bo 数据模型
     */
    public void publishNtp(ReportDataBo bo) {
        NtpModel ntpModel = JSON.parseObject(bo.getMessage(), NtpModel.class);
        ntpModel.setServerRecvTime(System.currentTimeMillis());
        ntpModel.setServerSendTime(System.currentTimeMillis());
        String topic = topicsUtils.buildTopic(bo.getProductId(), bo.getSerialNumber(), TopicType.NTP_GET);
        mqttClient.publish(1, false, topic, JSON.toJSONString(ntpModel));
    }

    /**
     * 4.发布属性
     * delay 延时，秒为单位
     */
    @Override
    public void publishProperty(Long productId, String deviceNum, List<ThingsModelSimpleItem> thingsList, int delay) {
        String topic = topicsUtils.buildTopic(productId, deviceNum, TopicType.PROPERTY_GET);
        if (thingsList == null) {
            mqttClient.publish(1, true, topic, "");
        } else {
            mqttClient.publish(1, true, topic, JSON.toJSONString(thingsList));
        }
    }

    /**
     * 5.发布功能
     * delay 延时，秒为单位
     */
    @Override
    public void publishFunction(Long productId, String deviceNum, List<ThingsModelSimpleItem> thingsList, int delay) {
        String pre = "";
        if (delay > 0) {
            pre = "$delayed/" + String.valueOf(delay) + "/";
        }
        String topic = topicsUtils.buildTopic(productId, deviceNum, TopicType.FUNCTION_GET);
        if (thingsList == null) {
            mqttClient.publish(1, true, topic, "");
        } else {
            mqttClient.publish(1, true, topic, JSON.toJSONString(thingsList));
        }

    }


    public void publishWithLog(String topic, byte[] pushMessage, FunctionLog log, Long delay) {
        try {
            if (!Objects.isNull(delay) && delay > 0) {
                Thread.sleep(delay);
            }
            mqttClient.publish(pushMessage, topic, false, 0);
            if (null != log) {
                //存储服务下发成功
                log.setResultMsg(FunctionReplyStatus.SUCCESS.getMessage());
                log.setResultCode(FunctionReplyStatus.SUCCESS.getCode());
                functionLogService.insertFunctionLog(log);
            }
        } catch (Exception e) {
            if (null != log) {
                //服务下发失败存储
                log.setResultMsg(FunctionReplyStatus.FAIL.getMessage() + "原因: " + e.getMessage());
                log.setResultCode(FunctionReplyStatus.FAIL.getCode());
                functionLogService.insertFunctionLog(log);
            }
        }
    }

    /**
     * 推送设备状态
     *
     * @param device 设备
     * @param status 状态
     */
    public void pushDeviceStatus(Device device, DeviceStatus status) {
        String message = "{\"status\":" + status.getType() + ",\"isShadow\":" + device.getIsShadow() + ",\"rssi\":" + device.getRssi() + "}";
        String topic = topicsUtils.buildTopic(device.getProductId(), device.getSerialNumber(), TopicType.STATUS_POST);
        pubMqttClient.publish(0, false, topic, message);
    }

}
