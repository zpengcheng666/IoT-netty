package com.sydh.iot.data.listen;

import com.sydh.common.constant.SYDHConstant;
import com.sydh.common.extend.core.domin.mq.DeviceReportBo;
import com.sydh.mq.queue.DeviceOtherQueue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import com.sydh.iot.data.consumer.DeviceOtherMsgConsumer;

import javax.annotation.Resource;

/**
 * @author gsb
 * @date 2023/2/28 10:02
 */
@Slf4j
@Component
public class DeviceOtherListen {

    @Resource
    private DeviceOtherMsgConsumer otherMsgConsumer;

    @Async(SYDHConstant.TASK.DEVICE_OTHER_TASK)
    public void listen(){
        while (true){
            try {
                DeviceReportBo reportBo = DeviceOtherQueue.take();
                otherMsgConsumer.consume(reportBo);
            }catch (Exception e){
                log.error("=>emq数据转发异常");
            }
        }
    }
}
