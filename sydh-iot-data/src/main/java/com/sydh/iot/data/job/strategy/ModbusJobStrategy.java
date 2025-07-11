package com.sydh.iot.data.job.strategy;

import com.sydh.iot.domain.DeviceJob;
import com.sydh.iot.model.modbus.ModbusPollJob;
import com.sydh.mq.producer.MessageProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Modbus-rtu 策略
 * @author gsb
 * @date 2025/3/18 15:38
 */
@Component
@Slf4j
public class ModbusJobStrategy implements JobInvokeStrategy{


    @Override
    public boolean supports(int jobType) {
        return 5 == jobType;
    }

    @Override
    public void invoke(DeviceJob job) {
        log.info("----------------执行modbus轮训指令----------------------");
        ModbusPollJob pollJob = new ModbusPollJob();
        pollJob.setType(2);
        pollJob.setDeviceJob(job);
        MessageProducer.sendPropFetch(pollJob);
    }
}
