package com.sydh.iot.data.emq;

import com.sydh.common.enums.ServerType;
import com.sydh.common.enums.TopicType;
import com.sydh.common.extend.core.domin.mq.DeviceReportBo;
import com.sydh.common.utils.DateUtils;
import com.sydh.common.utils.StringUtils;
import com.sydh.common.utils.gateway.mq.TopicsUtils;
import com.sydh.rule.context.MsgContext;
import com.sydh.iot.ruleEngine.RuleProcess;
import com.sydh.mqttclient.IEmqxMessageProducer;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;

@Slf4j
@Component
public class subscribeCallback implements IMqttMessageListener {
    @Resource
    private RuleProcess ruleProcess;
    @Resource
    private TopicsUtils topicsUtils;
    @Resource
    private IEmqxMessageProducer emqxMessageSerice;

    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
        String message = new String(mqttMessage.getPayload());
        log.info("接收消息主题 : {}", topic);
        log.info("接收消息Qos : {}", mqttMessage.getQos());
        log.info("接收消息内容 : {}", message);

        // 解决16进制数据转换错误
        byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
        if (bytes.length != mqttMessage.getPayload().length) {
            bytes = mqttMessage.getPayload();
            message = ByteBufUtil.hexDump(Unpooled.wrappedBuffer(mqttMessage.getPayload()));
        }

        //这里默认设备编号长度超过9位
        String[] split = topic.split("/");
        String sn = Arrays.stream(split).filter(imei -> imei.length() > 9).findFirst().get();
        // 规则引擎脚本处理,完成后返回结果
        MsgContext context = ruleProcess.processRuleScript(sn, 1, topic, message);
        if (!Objects.isNull(context) && StringUtils.isNotEmpty(context.getPayload())
                && StringUtils.isNotEmpty(context.getTopic())) {
            topic = context.getTopic();
            message = context.getPayload();
            bytes = message.getBytes(StandardCharsets.UTF_8);
        }
        String serialNumber = topicsUtils.parseSerialNumber(topic);
        Long productId;
        if (topic.contains(TopicType.FETCH_UPGRADE_REPLY.getTopicSuffix()) || topic.contains(TopicType.HTTP_UPGRADE_REPLY.getTopicSuffix())) {
            productId = 0L;
        } else {
            productId = topicsUtils.parseProductId(topic);
        }

        String name = topicsUtils.parseTopicName(topic);
        DeviceReportBo reportBo = DeviceReportBo.builder()
                .serialNumber(serialNumber)
                .productId(productId)
                .data(bytes)
                .platformDate(DateUtils.getNowDate())
                .topicName(topic)
                .serverType(ServerType.MQTT)
                .build();
        /*将mqtt的消息发送至MQ队列处理消息 ,减轻mqtt客户端消息压力*/
        emqxMessageSerice.sendEmqxMessage(name,reportBo);
    }
}
