package com.sydh.iot.data.listen;

import com.sydh.common.constant.SYDHConstant;
import com.sydh.common.extend.core.domin.mq.DeviceTestReportBo;
import com.sydh.iot.data.consumer.DeviceTestConsumer;
import com.sydh.mq.queue.DeviceTestQueue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author bill
 */
@Component
@Slf4j
public class DeviceTestListen {

    @Resource
    private DeviceTestConsumer deviceTestConsumer;

    @Async(SYDHConstant.TASK.DEVICE_TEST_TASK)
    public void listen(){
        while (true){
            try {
                DeviceTestReportBo take = DeviceTestQueue.take();

                deviceTestConsumer.consume(take);
            }catch (Exception e){
                log.error("=>emq数据转发异常");
            }
        }
    }
}
