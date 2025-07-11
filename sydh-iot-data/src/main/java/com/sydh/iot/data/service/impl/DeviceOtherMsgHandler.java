package com.sydh.iot.data.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.sydh.common.enums.TopicType;
import com.sydh.common.extend.core.domin.mq.DeviceReportBo;
import com.sydh.common.extend.core.domin.mq.message.ReportDataBo;
import com.sydh.common.extend.core.domin.mq.ota.OtaReplyMessage;
import com.sydh.common.utils.StringUtils;
import com.sydh.common.utils.gateway.mq.TopicsUtils;
import com.sydh.iot.data.service.IDataHandler;
import com.sydh.iot.data.service.IMqttMessagePublish;
import com.sydh.iot.data.service.IOtaTaskUpgradeService;
import com.sydh.iot.domain.Firmware;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author gsb
 * @date 2023/2/27 14:42
 */
@Component
@Slf4j
public class DeviceOtherMsgHandler {

    @Resource
    private TopicsUtils topicsUtils;
    @Resource
    private IDataHandler dataHandler;
    @Resource
    private IMqttMessagePublish messagePublish;
    @Resource
    private IOtaTaskUpgradeService otaTaskUpgradeService;

    /**
     * true: 使用netty搭建的mqttBroker  false: 使用emq
     */
    @Value("${server.broker.enabled}")
    private Boolean enabled;


    /**
     * 非属性消息消息处理入口
     *
     * @param bo
     */
    public void messageHandler(DeviceReportBo bo) {
        String type = "";
        String name = topicsUtils.parseTopicName(bo.getTopicName());
        if (StringUtils.isEmpty(name) || name.endsWith(TopicType.FUNCTION_GET.getTopicSuffix())) return;
        ReportDataBo data = this.buildReportData(bo);
        TopicType topicType = TopicType.getType(name);
        switch (topicType) {
            case INFO_POST:
                dataHandler.reportDevice(data);
                break;
            case NTP_POST:
                messagePublish.publishNtp(data);
                break;
            case FUNCTION_POST:
                data.setShadow(false);
                data.setType(2);
                data.setRuleEngine(true);
                dataHandler.reportData(data);
                break;
            case EVENT_POST:
                data.setType(3);
                data.setRuleEngine(true);
                dataHandler.reportEvent(data);
                break;
            case HTTP_UPGRADE_REPLY:
                OtaReplyMessage otaReplyMessage = JSONObject.parseObject(data.getMessage(), OtaReplyMessage.class);
                Firmware firmware = new Firmware();
                firmware.setFirmwareType(2);
                otaTaskUpgradeService.upgrade(otaReplyMessage.getTaskId(), data.getSerialNumber(), firmware, otaReplyMessage);
                break;
            case FETCH_UPGRADE_REPLY:
                OtaReplyMessage otaReplyMessage1 = JSONObject.parseObject(data.getMessage(), OtaReplyMessage.class);
                Firmware firmware1 = new Firmware();
                firmware1.setFirmwareType(1);
                otaTaskUpgradeService.upgrade(otaReplyMessage1.getTaskId(), data.getSerialNumber(), firmware1, otaReplyMessage1);
                break;
        }
    }

    /**
     * 组装数据
     */
    private ReportDataBo buildReportData(DeviceReportBo bo) {
        String message = new String(bo.getData());
        log.info("收到设备信息[{}]", message);
        Long productId;
        if (bo.getTopicName().contains(TopicType.FETCH_UPGRADE_REPLY.getTopicSuffix()) || bo.getTopicName().contains(TopicType.HTTP_UPGRADE_REPLY.getTopicSuffix())) {
            productId = 0L;
        } else {
            productId = topicsUtils.parseProductId(bo.getTopicName());
        }
//        Long productId = topicsUtils.parseProductId(bo.getTopicName());
        String serialNumber = topicsUtils.parseSerialNumber(bo.getTopicName());
        ReportDataBo dataBo = new ReportDataBo();
        dataBo.setMessage(message);
        dataBo.setProductId(productId);
        dataBo.setSerialNumber(serialNumber);
        dataBo.setRuleEngine(false);
        return dataBo;
    }

}
