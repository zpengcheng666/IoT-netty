package com.sydh.iot.data.listen;

import com.sydh.common.constant.SYDHConstant;
import com.sydh.common.extend.core.domin.mq.DeviceReportBo;
import com.sydh.iot.data.consumer.DeviceReportMsgConsumer;
import com.sydh.mq.queue.DeviceReportQueue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 设备主动上报消息监听
 *
 * @author bill
 */
@Slf4j
@Component
public class DeviceReportListen {

    @Autowired
    private DeviceReportMsgConsumer reportMsgConsumer;

    @Async(SYDHConstant.TASK.MESSAGE_CONSUME_TASK_PUB)
    public void listen() {
        while (true) {
            try {
                /*取出数据*/
                DeviceReportBo reportBo = DeviceReportQueue.take();
                /*处理数据*/
                reportMsgConsumer.consume(reportBo);
            } catch (Exception e) {
                log.error("=>设备上报数据监听异常", e);
            }
        }
    }
}
