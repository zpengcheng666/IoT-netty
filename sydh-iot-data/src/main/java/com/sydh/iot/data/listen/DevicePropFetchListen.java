package com.sydh.iot.data.listen;

import com.sydh.common.constant.SYDHConstant;
import com.sydh.iot.data.consumer.DevicePropFetchConsumer;
import com.sydh.iot.model.modbus.ModbusPollJob;
import com.sydh.mq.queue.DevicePropFetchQueue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 设备属性获取(定时获取)监听
 *
 * @author gsb
 * @date 2022/10/11 8:26
 */
@Slf4j
@Component
public class DevicePropFetchListen {

    @Autowired
    private DevicePropFetchConsumer devicePropFetchConsumer;

    @Async(SYDHConstant.TASK.MESSAGE_CONSUME_TASK_FETCH)
    public void listen() {
        while (true) {
            try {
                ModbusPollJob take = DevicePropFetchQueue.take();
                devicePropFetchConsumer.consume(take);
            } catch (Exception e) {
                log.error("=>设备属性获取异常", e);
            }
        }
    }
}
