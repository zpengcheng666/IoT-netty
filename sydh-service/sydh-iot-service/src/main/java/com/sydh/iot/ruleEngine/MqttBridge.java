package com.sydh.iot.ruleEngine;

import com.alibaba.fastjson2.JSON;
import com.sydh.iot.domain.Bridge;
import com.sydh.iot.domain.MqttClient;
import com.sydh.iot.service.IBridgeService;
import com.sydh.iot.service.IMqttClientService;
import com.sydh.rule.context.MsgContext;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.core.NodeComponent;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.utils.StringUtils;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author zhuangpengli
 */
@Slf4j
@LiteflowComponent("mqttBridge")
public class MqttBridge extends NodeComponent {
    @Autowired
    private IBridgeService bridgeService;

    @Autowired
    private IMqttClientService mqttClientService;

    @Override
    public void process() throws Exception {
        // 获取上下文对象
        MsgContext cxt = this.getContextBean(MsgContext.class);
        Integer id = cxt.getData("mqttBridgeID");
        Bridge bridge = bridgeService.queryByIdWithCache(Long.valueOf(id));
        if (bridge != null && "1".equals(bridge.getEnable())) {
            MqttClient config = JSON.parseObject(bridge.getConfigJson(), MqttClient.class);
            MqttAsyncClient client = MqttClientFactory.instance(mqttClientService.buildmqttclientconfig(config),null);
            MqttMessage message = new MqttMessage();
            message.setPayload(cxt.getPayload().getBytes());
            try {
                if (StringUtils.isNotEmpty(bridge.getRoute())) {
                    client.publish(bridge.getRoute(), message);
                } else {
                    client.publish(cxt.getTopic(), message);
                }
                log.info("=>桥接发布主题成功 topic={},message={}", cxt.getTopic(), message);
            } catch (MqttException e) {
                log.error("msg:"+e.getMessage());
                log.error("cause:"+e.getCause());
                log.error("reason:"+e.getReasonCode());
                log.error("=>桥接发布主题时发生错误 topic={},message={}", cxt.getTopic(), e.getMessage());
            }
        }
    }
}
