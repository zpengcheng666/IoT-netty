package com.sydh.iot.model.modbus;

import com.sydh.common.extend.core.domin.mq.message.ModbusPollMsg;
import com.sydh.iot.domain.DeviceJob;
import lombok.Data;

/**
 * @author gsb
 * @date 2024/8/28 11:31
 */
@Data
public class ModbusPollJob {

    private ModbusPollMsg pollMsg;

    private DeviceJob deviceJob;
    /**
     * 1- 主动读取  2- 轮训下发
     */
    private Integer type;
}
