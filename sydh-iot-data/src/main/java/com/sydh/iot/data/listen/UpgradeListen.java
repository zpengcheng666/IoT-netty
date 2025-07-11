package com.sydh.iot.data.listen;

import com.sydh.common.constant.SYDHConstant;
import com.sydh.common.extend.core.domin.mq.ota.OtaUpgradeBo;
import com.sydh.iot.data.service.IMqttMessagePublish;
import com.sydh.mq.queue.OtaUpgradeQueue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * OTA升级消息监听
 *
 * @author gsb
 * @date 2022/10/11 8:36
 */
@Slf4j
@Service
public class UpgradeListen {

    @Autowired
    private IMqttMessagePublish functionSendService;

    @Async(SYDHConstant.TASK.MESSAGE_CONSUME_TASK)
    public void listen() {
        while (true) {
            try {
                /*获取队列中的OTA升级消息*/
                OtaUpgradeBo upgradeBo = OtaUpgradeQueue.take();
                // OTA升级处理
                functionSendService.upGradeOTA(upgradeBo);
            } catch (Exception e) {
                log.error("->OTA消息监听异常", e);
            }
        }
    }
}
