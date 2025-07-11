package com.sydh.iot.data.listen;

import com.sydh.common.constant.SYDHConstant;
import com.sydh.common.extend.core.domin.mq.DeviceReportBo;
import com.sydh.iot.data.consumer.DeviceReplyMsgConsumer;
import com.sydh.mq.queue.DeviceReplyQueue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 设备回调消息监听
 *
 * @author bill
 */
@Slf4j
@Component
public class DeviceReplyListen {

    @Autowired
    private DeviceReplyMsgConsumer deviceReplyMsgHandler;

    @Async(SYDHConstant.TASK.MESSAGE_CONSUME_TASK_PUB)
    public void listen() {
        while (true) {
            try {
                /*读队列消息*/
                DeviceReportBo reportBo = DeviceReplyQueue.take();
                /*处理消息*/
                deviceReplyMsgHandler.consume(reportBo);
            } catch (Exception e) {
                log.error("=>设备回调消息监听异常", e);
            }
        }
    }
}
