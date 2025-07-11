package com.sydh.iot.data.listen;

import com.sydh.common.constant.SYDHConstant;
import com.sydh.common.extend.core.domin.mq.DeviceStatusBo;
import com.sydh.iot.data.consumer.DeviceStatusConsumer;
import com.sydh.mq.queue.DeviceStatusQueue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 设备状态监听
 * @author bill
 */
@Slf4j
@Component
public class DeviceStatusListen {

    @Autowired
    private DeviceStatusConsumer deviceStatusConsumer;

    @Async(SYDHConstant.TASK.MESSAGE_CONSUME_TASK)
    public void listen() {
        try {
            while (true) {
                DeviceStatusBo status = DeviceStatusQueue.take();
                deviceStatusConsumer.consume(status);
            }
        } catch (Exception e) {
            log.error("设备状态监听错误", e);
        }
    }

}
