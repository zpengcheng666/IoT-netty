package com.sydh.iot.data.consumer;

import com.sydh.common.constant.SYDHConstant;
import com.sydh.common.extend.core.domin.mq.DeviceReportBo;
import com.sydh.iot.data.service.IDeviceReportMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 设备上报消息处理
 *
 * @author bill
 */
@Slf4j
@Component
public class DeviceReportMsgConsumer {


    @Autowired
    private IDeviceReportMessageService reportMessageService;

    @Async(SYDHConstant.TASK.DEVICE_UP_MESSAGE_TASK)
    public void consume(DeviceReportBo bo) {
        try {
            //处理数据解析
            reportMessageService.parseReportMsg(bo);
        } catch (Exception e) {
            log.error("设备主动上报队列监听异常", e);
        }
    }


}
