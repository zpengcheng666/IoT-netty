package com.fastbee.iot.data.job.strategy;

import com.fastbee.iot.domain.Device;
import com.fastbee.iot.domain.DeviceJob;
import com.fastbee.iot.mapper.DeviceMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * modbus tcp 策略
 *
 * @author gsb
 * @date 2025/3/18 16:51
 */
@Component
public class ModbusTcpJobStrategy implements JobInvokeStrategy {

    @Resource
    private DeviceMapper deviceMapper;

    @Override
    public boolean supports(int jobType) {
        return 6 == jobType;
    }

    @Override
    public void invoke(DeviceJob job) {
        String modbusTcpId = job.getSerialNumber();

    }
}
