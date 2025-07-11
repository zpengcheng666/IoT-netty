package com.sydh.iot.ruleEngine;

import com.sydh.common.extend.core.domin.model.device.DeviceAndProtocol;
import com.sydh.iot.model.ScriptCondition;
import com.sydh.iot.service.IDeviceService;
import com.sydh.iot.service.IScriptService;
import com.sydh.rule.context.MsgContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * 执行规则引擎
 *
 * @author gsb
 * @date 2024/2/3 16:07
 */
@Component
@Slf4j
public class RuleProcess {

    @Resource
    private IScriptService scriptService;
    @Resource
    private IDeviceService deviceService;

    /**
     * 规则引擎脚本处理
     *
     * @param topic
     * @param payload
     * @param event   1=设备上报 2=平台下发 3=设备上线 4=设备下线 （其他可以增加设备完成主题订阅之类）
     * @return
     */
    public MsgContext processRuleScript(String serialNumber, int event, String topic, String payload) {
        DeviceAndProtocol deviceProtocolDetail = deviceService.getDeviceProtocolDetail(serialNumber);
        if (Objects.isNull(deviceProtocolDetail)) {
            return new MsgContext();
        }
        // 查询数据流脚本组件
        ScriptCondition scriptCondition = ScriptCondition.builder()
                .scriptPurpose(1)
                .scriptEvent(event)
                .productId(deviceProtocolDetail.getProductId())
                .build();
        MsgContext context = MsgContext.builder()
                .serialNumber(serialNumber)
                .productId(deviceProtocolDetail.getProductId())
                .protocolCode(deviceProtocolDetail.getProtocolCode())
                .payload(payload)
                .topic(topic)
                .build();
        //返回处理完的消息上下文
        return scriptService.execRuleScript(scriptCondition, context);
    }

}
